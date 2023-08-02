package qetaa.service.cart.restful;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import qetaa.service.cart.apicontract.QuotationCart;
import qetaa.service.cart.apicontract.QuotationCartItem;
import qetaa.service.cart.dao.DAO;
import qetaa.service.cart.helpers.AppConstants;
import qetaa.service.cart.helpers.Helper;
import qetaa.service.cart.model.Cart;
import qetaa.service.cart.model.CartItem;
import qetaa.service.cart.model.Wallet;
import qetaa.service.cart.model.WalletItem;
import qetaa.service.cart.model.parts.PartsOrder;
import qetaa.service.cart.model.parts.PartsOrderItem;
import qetaa.service.cart.model.parts.PartsOrderItemApproved;
import qetaa.service.cart.model.parts.contract.PartsOrderCashOnDelivery;
import qetaa.service.cart.model.parts.contract.PartsOrderCreditCard;
import qetaa.service.cart.model.parts.contract.PartsOrderCreditSales;
import qetaa.service.cart.model.quotation.Quotation;
import qetaa.service.cart.model.quotation.QuotationItem;
import qetaa.service.cart.model.quotation.QuotationItemApproved;
import qetaa.service.cart.model.quotation.QuotationItemResponse;
import qetaa.service.cart.model.quotation.QuotationVendorItem;
import qetaa.service.cart.model.security.WebApp;
import qetaa.service.cart.serverpush.QuotationsEndpoint;
import qetaa.service.cart.serverpush.QuotingEndpoint;

@Stateless
public class AsyncService {

	@EJB
	private DAO dao;

	
	private void createPartOrder(PartsOrder partsOrder, String authHeader) {
		long addressId = getAddressId(partsOrder, authHeader);
		partsOrder.setAppCode(this.getWebAppFromAuthHeader(authHeader).getAppCode());
		partsOrder.setAddressId(addressId);
		partsOrder.setCreated(new Date());
		partsOrder.setStatus('N');
		PartsOrder pocheck = dao.findCondition(PartsOrder.class, "cartId", partsOrder.getCartId());
		if (pocheck == null) {
			dao.persist(partsOrder);
		}	
	}
	
	private void createPartsOrderItems(PartsOrder partsOrder){
		for (PartsOrderItem poi : partsOrder.getPartsItems()) {
			if(poi.getOrderedQuantity() > 0) {
				poi.setPartsOrderId(partsOrder.getId());
				QuotationItem qitem = dao.find(QuotationItem.class, poi.getQuotationItemId());
				poi.setCartId(partsOrder.getCartId());
				poi.setQuotationId(qitem.getQuotationId());
				poi.setStatus('N');
				List<QuotationItemApproved> listqai = dao.getCondition(QuotationItemApproved.class, "quotationItemId", poi.getQuotationItemId());
				if(!listqai.isEmpty()) {
					poi.setProductId(listqai.get(0).getProductId());
				}
				PartsOrderItem testPoi = dao.findCondition(PartsOrderItem.class, "quotationItemId", poi.getQuotationItemId());
				if(testPoi == null) {
					poi = dao.persistAndReturn(poi);
					int q = poi.getOrderedQuantity();
					createPartsApprovedItem(poi, partsOrder, q);
				}
				
			}
			
			
		}
	}
	
	private void updateCartAfterPartsOrder(Cart cart, String authHeader){
		//don't send sms if wire transfer is requested. It should be sent once confirmed
		if (cart.getStatus() == 'S') {
			cart.setStatus('P');
			dao.update(cart);
			String text = "تم استلام طلبكم بنجاح للطلب رقم ";
			text += cart.getId();
			text += " نعمل الان على شحن القطع";
//			text += " قد يتأخر إرسال الطلب و ذلك لإجازة عيد الأضحى المبارك. و كل عام و انتم بخير ";
			this.sendSms(cart.getCustomerId(), cart.getId(), text, "part-paid", authHeader);
		}
	}
	
	@Asynchronous
	public void payCreditCard(PartsOrderCreditCard pocc, String authHeader) {
		PartsOrder partsOrder = pocc.getPartsOrder();
		partsOrder.setCartId(pocc.getCart().getId());
		createPartOrder(partsOrder, authHeader);
		createPartsOrderItems(partsOrder);
		updateCartAfterPartsOrder(pocc.getCart(), authHeader);
		fundWallet(pocc, authHeader);
		
	}
	
	@Asynchronous
	public void payCashOnDelivery(PartsOrderCashOnDelivery pocod, String authHeader) {
		PartsOrder partsOrder = pocod.getPartsOrder();
		partsOrder.setCartId(pocod.getCart().getId());
		createPartOrder(partsOrder, authHeader);
		createPartsOrderItems(partsOrder);
		updateCartAfterPartsOrder(pocod.getCart(), authHeader);
		fundWallet(pocod, authHeader);
		
	}
	
	@Asynchronous
	public void payCreditSales(PartsOrderCreditSales pocs, String authHeader) {
		PartsOrder partsOrder = pocs.getPartsOrder();
		partsOrder.setCartId(pocs.getCart().getId());
		createPartOrder(partsOrder, authHeader);
		createPartsOrderItems(partsOrder);
		updateCartAfterPartsOrder(pocs.getCart(), authHeader);
		fundWallet(pocs, authHeader);
	}
	
	private void fundWallet(PartsOrderCreditSales pocs, String authHeader) {
		Response r = this.postSecuredRequest(AppConstants.POST_NEW_WALLET, pocs.getCart().getId(), authHeader);
		Long walletId = r.readEntity(Long.class);
		Wallet wallet = new Wallet();
//		Map<String,Object> map = new HashMap<String,Object>();
		wallet.setId(walletId);
//		map.put("id", walletId);
		wallet.setCustomerId(pocs.getCart().getCustomerId());
		wallet.setCustomerName(pocs.getCustomerName());
//		map.put("customerId", pocs.getCart().getCustomerId());
//		map.put("customerName", pocs.getCustomerName());
		wallet.setCartId(pocs.getCart().getId());
		wallet.setGateway(null);
//		map.put("cartId", pocs.getCart().getId());
//		map.put("gateway", null);
		wallet.setTransactionId(null);
//		map.put("transactionId", null);
		wallet.setCcCompany(null);
//		map.put("ccCompany", null);
		wallet.setDiscountPercentage(pocs.getDiscountPercentage());
//		map.put("discountPercentage", pocs.getDiscountPercentage());
		wallet.setCreditFees(null);
	//	map.put("creditFees", null);
		List<WalletItem> witems = initWalletItems(pocs.getPartsOrder().getId(), pocs.getCart(), pocs.getDiscountPercentage());
		wallet.setWalletItems(witems);
		//put("walletItems", maps);
		Response r2 = this.putSecuredRequest(AppConstants.PUT_FUND_WALLET_CREDIT_SALES, wallet, authHeader);
		if(r2.getStatus() == 201) {
		}
		else {
			System.out.println("wallet not updated " + r2.getStatus());
		}
	}
	
	private void fundWallet(PartsOrderCashOnDelivery pocod, String authHeader) {
		Response r = this.postSecuredRequest(AppConstants.POST_NEW_WALLET, pocod.getCart().getId(), authHeader);
		Long walletId = r.readEntity(Long.class);
		Wallet wallet = new Wallet();
		wallet.setId(walletId);
//		map.put("id", walletId);
		wallet.setCustomerId(pocod.getCart().getCustomerId());
//		map.put("customerId", pocod.getCart().getCustomerId());
		wallet.setCustomerName(pocod.getCustomerName());
//		map.put("customerName", pocod.getCustomerName());
		wallet.setCartId(pocod.getCart().getId());
//		map.put("cartId", pocod.getCart().getId());
		wallet.setGateway(null);
//		map.put("gateway", null);
		wallet.setTransactionId(null);
//		map.put("transactionId", null);
		wallet.setCcCompany(null);
//		map.put("ccCompany", null);
		wallet.setDiscountPercentage(pocod.getDiscountPercentage());
//		map.put("discountPercentage", pocod.getDiscountPercentage());
		wallet.setCreditFees(null);
	//	map.put("creditFees", null);
		List<WalletItem> maps = initWalletItems(pocod.getPartsOrder().getId(), pocod.getCart(), pocod.getDiscountPercentage());
		wallet.setWalletItems(maps);
		Response r2 = this.putSecuredRequest(AppConstants.PUT_FUND_WALLET_COD, wallet, authHeader);
		if(r2.getStatus() == 201) {
		}
		else {
			System.out.println("wallet not updated " + r2.getStatus());
		}
	}
	
	private void fundWallet(PartsOrderCreditCard pocc, String authHeader) {
		Response r = this.postSecuredRequest(AppConstants.POST_NEW_WALLET, pocc.getCart().getId(), authHeader);
		Long walletId = r.readEntity(Long.class);
		Map<String, Object> map = new HashMap<String,Object>();
		map.put("id", walletId);
		map.put("customerId", pocc.getCart().getCustomerId());
		map.put("customerName", pocc.getCustomerName());
		map.put("cartId", pocc.getCart().getId());
		map.put("gateway", pocc.getGateway());
		map.put("transactionId", pocc.getTransactionId());
		map.put("ccCompany", pocc.getCcCompany());
		map.put("discountPercentage", pocc.getDiscountPercentage());
		map.put("creditFees", pocc.getCreditFees());
		map.put("walletItems", initWalletItems(pocc.getPartsOrder().getId(), pocc.getCart(), pocc.getDiscountPercentage()));
		Response r2 = this.putSecuredRequest(AppConstants.PUT_FUND_WALLET_CREDIT_CARD, map, authHeader);
		if(r2.getStatus() == 201) {
		}
		else {
			System.out.println("wallet not updated " + r2.getStatus());
		}
	}
	
	private List<WalletItem> initWalletItems(long poId, Cart cart, Double discountPercentage) {
		List<PartsOrderItemApproved> approvedList = dao.getCondition(PartsOrderItemApproved.class, "partsOrderId", poId);
		List<WalletItem> witems = new ArrayList<>();
		for(PartsOrderItemApproved approved : approvedList){
			WalletItem wi = new WalletItem();
			wi.setCartId(approved.getCartId());
			wi.setItemDesc(approved.getItemDesc());
//			map.put("cartId", approved.getCartId());
//			map.put("itemDesc", approved.getItemDesc());
			wi.setQuantity(approved.getApprovedQuantity());
//			map.put("quantity", approved.getApprovedQuantity());
			wi.setItemNumber(approved.getItemNumber());
//			map.put("itemNumber", approved.getItemNumber());
			wi.setItemType('P');
//			map.put("itemType", 'P');
			wi.setProductId(approved.getProductId());
			//map.put("productId", approved.getProductId());
			wi.setStatus('A');
//			map.put("status", 'A');
			wi.setUnitQuotedCost(approved.getCostPrice() / 1.05);
	//		map.put("unitQuotedCost", approved.getCostPrice() / 1.05);
			wi.setUnitQuotedCostWv(approved.getCostPrice());
//			map.put("unitQuotedCostWv", approved.getCostPrice());
			wi.setUnitSales(approved.getSalesPrice());
//			map.put("unitSales", approved.getSalesPrice());
			
			double vat = approved.getSalesPrice() * cart.getVatPercentage();
			double discount = approved.getSalesPrice() * discountPercentage;
			wi.setUnitSalesWv(approved.getSalesPrice() + vat);
	//		map.put("unitSalesWv", approved.getSalesPrice() + vat);
			wi.setUnitSalesNet(approved.getSalesPrice() - discount);
			//map.put("unitSalesNet", approved.getSalesPrice() - discount);
			wi.setUnitSalesNetWv(approved.getSalesPrice() - discount + vat);
//			map.put("unitSalesNetWv", approved.getSalesPrice() - discount + vat);
			wi.setVendorId(approved.getVendorId());
//			map.put("vendorId", approved.getVendorId());
			witems.add(wi);
		}
		WalletItem wi = new WalletItem();
		wi.setCartId(cart.getId());
		wi.setItemDesc("Delivery - رسوم التوصيل");
//		map.put("cartId", cart.getId());
//		map.put("itemDesc", "Delivery - رسوم التوصيل");
		wi.setItemNumber("");
//		map.put("itemNumber", "");
		wi.setItemType('D');
	//	map.put("itemType", 'D');
		wi.setProductId(null);
//		map.put("productId", null);
		wi.setStatus('A');
//		map.put("status", 'A');
		wi.setUnitQuotedCost(0);
		wi.setUnitQuotedCostWv(0);
//		map.put("unitQuotedCost", 0);
//		map.put("unitQuotedCostWv", 0);
		wi.setUnitSales(cart.getDeliveryFees());
//		map.put("unitSales", cart.getDeliveryFees());
		double vat = cart.getDeliveryFees() * cart.getVatPercentage();
		double discount = cart.getDeliveryFees() * discountPercentage;
		wi.setUnitSalesWv(cart.getDeliveryFees() + vat);
//		map.put("unitSalesWv", cart.getDeliveryFees() + vat);
		wi.setUnitSalesNet(cart.getDeliveryFees() - discount);
//		map.put("unitSalesNet", cart.getDeliveryFees() - discount);
		wi.setUnitSalesNetWv(cart.getDeliveryFees() - discount + vat);
//		map.put("unitSalesNetWv", cart.getDeliveryFees() - discount + vat);
		witems.add(wi);
//		maps.add(map);
		return witems;
	}

	@Asynchronous
	public void createPartsOrder(PartsOrder partsOrder, String authHeader) {
		long addressId = getAddressId(partsOrder, authHeader);
		partsOrder.setAppCode(this.getWebAppFromAuthHeader(authHeader).getAppCode());
		partsOrder.setAddressId(addressId);
		partsOrder.setCreated(new Date());
		partsOrder.setStatus('N');
		PartsOrder po = dao.findCondition(PartsOrder.class, "cartId", partsOrder.getCartId());
		if (po == null) {
			dao.persist(partsOrder);
			double totalCost = 0;
			for (PartsOrderItem poi : partsOrder.getPartsItems()) {
				if(poi.getOrderedQuantity() > 0) {
					poi.setPartsOrderId(partsOrder.getId());
					QuotationItem qitem = dao.find(QuotationItem.class, poi.getQuotationItemId());
					poi.setCartId(partsOrder.getCartId());
					poi.setQuotationId(qitem.getQuotationId());
					poi.setStatus('N');
					List<QuotationItemApproved> listqai = dao.getCondition(QuotationItemApproved.class, "quotationItemId", poi.getQuotationItemId());
					if(!listqai.isEmpty()) {
						poi.setProductId(listqai.get(0).getProductId());
					}
					PartsOrderItem testPoi = dao.findCondition(PartsOrderItem.class, "quotationItemId", poi.getQuotationItemId());
					if(testPoi == null) {
						poi = dao.persistAndReturn(poi);
						int q = poi.getOrderedQuantity();
						totalCost = totalCost + createPartsApprovedItem(poi, partsOrder, q);
					}
				}
			}
			// parts order
			partsOrder.setCostAmount(totalCost);
			dao.update(partsOrder);

			// if not transfer
			Cart cart = dao.find(Cart.class, partsOrder.getCartId());
			//don't send sms if wire transfer is requested. It should be sent once confirmed
			if (cart.getStatus() == 'S') {
				cart.setStatus('P');
				dao.update(cart);
				String text = "تم استلام المبلغ بنجاح للطلب رقم ";
				text += partsOrder.getCartId();
				text += " نعمل الان على شحن القطع";
//				text += " قد يتأخر إرسال الطلب و ذلك لإجازة عيد الأضحى المبارك. و كل عام و انتم بخير ";
				sendSms(cart.getCustomerId(), cart.getId(), text, "part-paid", authHeader);
			}
			//create purchase payment
		//	createPurchaseOrder(cart.getId(), authHeader);	
		}
	}
	
	
	

	private long getAddressId(PartsOrder partsOrder, String authHeader) {
		if (partsOrder.getAddressId() != 0) {
			return partsOrder.getAddressId();
		} else {
			Response r = this.postSecuredRequest(AppConstants.CREATE_CUSTOMER_ADDRESS, partsOrder.getAddress(),
					authHeader);
			if (r.getStatus() == 200) {
				return r.readEntity(Long.class);
			} else
				return 0;
		}
	}

	private double createPartsApprovedItem(PartsOrderItem poi, PartsOrder partsOrder, int q) {
		String jpql = "select b from QuotationItemApproved b where quotationItemId = :value0 order by unitCost, created";
		List<QuotationItemApproved> qApprovedItems = dao.getJPQLParams(QuotationItemApproved.class, jpql,
				poi.getQuotationItemId());
		int index = 0;
		double totalCost = 0;

		while (q > 0) {
			PartsOrderItemApproved approved = new PartsOrderItemApproved();
			approved.setCartId(partsOrder.getCartId());
			approved.setPartsOrderId(partsOrder.getId());
			approved.setPartsItemId(poi.getId());
			approved.setSalesPrice(poi.getSalesPrice());
			if (q > qApprovedItems.get(index).getQuantity()) {
				approved.setApprovedQuantity(qApprovedItems.get(index).getQuantity());
			} else {
				approved.setApprovedQuantity(q);
			}
			
			QuotationVendorItem qvi = dao.find(QuotationVendorItem.class, qApprovedItems.get(index).getVendorItemId());
			approved.setVendorId(qvi.getVendorId());
			approved.setItemNumber(qvi.getItemNumber());
			approved.setCostPrice(qApprovedItems.get(index).getUnitCost());
			approved.setQuotationItemApprovedId(qApprovedItems.get(index).getId());
			totalCost = (approved.getApprovedQuantity() * qApprovedItems.get(index).getUnitCost());
			approved.setStatus('W');
			approved.setProductId(qvi.getProductId());
			dao.persist(approved);
			q = q - approved.getApprovedQuantity();
			index++;
		}
		return totalCost;

	}
	
	
	
	public void updateCartToQuotation(Cart cart) {
		Quotation quotation = new Quotation();
		quotation.setCartId(cart.getId());
		quotation.setCreated(new Date());
		quotation.setCreatedBy(cart.getCreatedBy());
		quotation.setDeadline(Helper.addDeadline(new Date()));
		quotation.setStatus('W');
		dao.persist(quotation);
		for(CartItem item : cart.getCartItems()) {
			QuotationItem qi = new QuotationItem();
			qi.setCartId(cart.getId());
			qi.setCreated(quotation.getCreated());
			qi.setCreatedBy(quotation.getCreatedBy());
			qi.setItemDesc(item.getName());
			qi.setItemDescAr(null);
			qi.setQuantity(item.getQuantity());
			qi.setQuotationId(quotation.getId());
			qi.setStatus('W');
			dao.persist(qi);
		}
		cart.setStatus('W');
		dao.update(cart);
		broadcastToQuotations("new cart," + cart.getId());
	}
	
	@Asynchronous
	public void postQuotationCartCreation(Cart cart, QuotationCart quotationCart, String authHeader) {
		try {
			//write image
			if(quotationCart.isImageAttached()) {
				Helper.writeVinImage(quotationCart.getVinImage(), cart.getId(), cart.getCreated());
			}
			//write cart items
			cart.setCartItems(new ArrayList<>());
			for(QuotationCartItem qcitem : quotationCart.getQuotationCartItems()) {
				CartItem cartItem = new CartItem();
				cartItem.setCartId(cart.getId());
				cartItem.setCreated(cart.getCreated());
				cartItem.setName(qcitem.getItemName());
				cartItem.setQuantity(qcitem.getQuantity());
				cartItem.setImageAttached(qcitem.isImageAttached());
				dao.persist(cartItem);
				cart.getCartItems().add(cartItem);
				if(qcitem.isImageAttached()) {
					Helper.writeCartItemImage(qcitem.getImage(), cart.getId(), cartItem.getId(), cartItem.getCreated());
				}
			}
			
			this.updateCartToQuotation(cart);
			
			sendEmail(cart.getId());
			String text = "تم استلام طلبكم ";
			text = text + "رقم: " + cart.getId() + " ";
//			text = text + " و سيتم الرد بالتسعير بتاريخ ";
//			text = text + "28/08/2018";
	//		text = text + " و ذلك لإجازة عيد الأضحى المبارك, وكل عام و أنتم بخير ";
			text = text + "نعمل الان على توفير افضل سعر و سيتم التواصل معكم قريبا";
			sendSms(cart.getCustomerId(), cart.getId(), text, "cart-created", authHeader);
			
		}catch(Exception ex) {
			// log
			ex.printStackTrace();
		}
	}

	@Asynchronous
	public void postCartCreation(Cart cart, String authHeader) {
		try {
			// persist cart items
			for (CartItem item : cart.getCartItems()) {
				item.setCartId(cart.getId());
				item.setCreated(cart.getCreated());
				dao.persist(item);
			}
			
			if(!cart.isNoVin()) {
				this.updateCartToQuotation(cart);
			}
			

			sendEmail(cart.getId());
			String text = "تم استلام طلبكم ";
			text = text + "رقم: " + cart.getId() + " ";
			text = text + "نعمل الان على توفير افضل سعر و سيتم التواصل معكم قريبا";
			sendSms(cart.getCustomerId(), cart.getId(), text, "cart-created", authHeader);
		} catch (Exception ex) {
			// log
			ex.printStackTrace();
		}
	}
	@Asynchronous
	public void broadcastToQuotations(String message) {
		QuotationsEndpoint.broadcast(message);
	}
	
	@Asynchronous
	public void sendToQuotingUser(String message, int userId) {
		QuotingEndpoint.sendToUser(message, userId);
	}
	
	@Asynchronous
	public void broadcastToQuoting(String message) {
		QuotingEndpoint.broadcast(message);
	}


	// to be qualified
	private void sendEmail(long cartId) {

	}
	
	@Asynchronous
	public void createFinderScore(QuotationItemResponse qir, String desc, String stage, String authHeader, int score) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("cartId", qir.getCartId());
		map.put("quotationResponseId", qir.getId());
		map.put("stage", stage);
		map.put("userId", qir.getCreatedBy());
		map.put("score", score);
		map.put("desc", desc);
		postSecuredRequest(AppConstants.POST_FINDER_SCORE, map, authHeader);
	}
	
	@Asynchronous
	public void sendSmsAfterOldWay(String authHeader, Cart cart) {
		String smsText = "عزيزنا العميل, تسعيرتكم للطلب رقم ";
		smsText = smsText + cart.getId();
		smsText = smsText + " جاهزة على الرابط ";
		smsText = smsText + "qetaa.com/codelg?c=";
		sendSmsWithAppend(cart.getCustomerId(), cart.getId(), smsText, "quotation-ready", authHeader);
	}
	

	private void sendSmsWithAppend(long customerId, long cartId, String text, String purpose, String authHeader) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("customerId", String.valueOf(customerId));
		map.put("text", text);
		map.put("cartId", String.valueOf(cartId));
		map.put("purpose", purpose);
		postSecuredRequest(AppConstants.SEND_SMS_TO_CUSTOMER_APPEND_CODE, map, authHeader);
	}

	private void updateScores(Cart cart, String authHeader) {
		try {
			String sql = "select vendor_id, sum(item_cost_price) sum_price, sum(EXTRACT(EPOCH FROM responded)) sum_time from crt_quotation_vendor_item"
					+ " where cart_id =" + cart.getId() + " and status = 'C'" + " group by vendor_id"
					+ " order by sum(item_cost_price), sum(EXTRACT(EPOCH FROM responded )) asc";
			List<Object> bestVendors = dao.getNative(sql);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("makeId", cart.getMakeId());
			map.put("entityId", cart.getId());
			// post chepest total Vendor
			if (!bestVendors.isEmpty()) {
				Object[] objects = (Object[]) bestVendors.get(0);
				Integer vendorId = (Integer) objects[0];
				map.put("vendorId", vendorId);
				Response r = this.postSecuredRequest(AppConstants.CREATE_VENDOR_SCORE_BEST, map, authHeader);
				if (r.getStatus() != 200) {
					// log error
				}
			}

			if (!bestVendors.isEmpty() && bestVendors.size() > 1) {
				Object[] objects = (Object[]) bestVendors.get(1);
				Integer vendorId = (Integer) objects[0];
				map.replace("vendorId", vendorId);
				Response r = this.postSecuredRequest(AppConstants.CREATE_VENDOR_SCORE_SECOND_BEST, map, authHeader);
				if (r.getStatus() != 200) {
					// log error
				}
			}
			//
			String sql2 = "select vendor_id, sum(EXTRACT(EPOCH FROM responded)) from crt_quotation_vendor_item"
					+ " where cart_id =" + cart.getId() + " and status = 'C'" + " group by vendor_id"
					+ " order by sum(EXTRACT(EPOCH FROM responded)) asc";
			List<Object> fastest = dao.getNative(sql2);
			if (!fastest.isEmpty()) {
				Object[] objects = (Object[]) fastest.get(0);
				Integer vendorId = (Integer) objects[0];
				map.replace("vendorId", vendorId);
				Response r = this.postSecuredRequest(AppConstants.CREATE_VENDOR_FIRST, map, authHeader);
				if (r.getStatus() != 200) {
					// log
				}
			}
			Helper h = new Helper();
			Quotation q = dao.findCondition(Quotation.class, "cartId", cart.getId());
			String sql3 = "select DISTINCT vendor_id from crt_quotation_vendor_item where cart_id = " + cart.getId()
					+ " and (responded is null or responded > '" + h.getDateFormat(q.getDeadline()) + "');";
			List<Object> incomplete = dao.getNative(sql3);
			for (Object o : incomplete) {
				Integer vendorId = (Integer) o;
				map.replace("vendorId", vendorId);
				Response r = this.postSecuredRequest(AppConstants.CREATE_VENDOR_SCORE_INCOMPLETE, map, authHeader);
				if (r.getStatus() != 200) {
					// log
				}
			}
		} catch (Exception ex) {

		}
	}


	public void sendSms(long customerId, long cartId, String text, String purpose, String authHeader) {
		try {
			Map<String, String> map = new HashMap<String, String>();
			map.put("customerId", String.valueOf(customerId));
			map.put("text", text);
			map.put("cartId", String.valueOf(cartId));
			map.put("purpose", purpose);
			this.postSecuredRequest(AppConstants.SEND_SMS_TO_CUSTOMER, map, authHeader);
		} catch (Exception ex) {
		}
	}

	// qualified
	private WebApp getWebAppFromAuthHeader(String authHeader) {
		try {
			String[] values = authHeader.split("&&");
			String appSecret = values[2].trim();
			// Validate app secret
			return getWebAppFromSecret(appSecret);
		} catch (Exception ex) {
			return null;
		}
	}

	// qualified
	// retrieves app object from app secret
	private WebApp getWebAppFromSecret(String secret) throws Exception {
		// verify web app secret
		WebApp webApp = dao.findTwoConditions(WebApp.class, "appSecret", "active", secret, true);
		if (webApp == null) {
			throw new Exception();
		}
		return webApp;
	}

	public Response getSecuredRequest(String link, String authHeader) {
		Builder b = ClientBuilder.newClient().target(link).request();
		b.header(HttpHeaders.AUTHORIZATION, authHeader);
		Response r = b.get();
		return r;
	}

	public <T> Response postSecuredRequest(String link, T t, String authHeader) {
		Builder b = ClientBuilder.newClient().target(link).request();
		b.header(HttpHeaders.AUTHORIZATION, authHeader);
		Response r = b.post(Entity.entity(t, "application/json"));// not secured
		return r;
	}
	
	public <T> Response putSecuredRequest(String link, T t, String authHeader) {
		Builder b = ClientBuilder.newClient().target(link).request();
		b.header(HttpHeaders.AUTHORIZATION, authHeader);
		Response r = b.put(Entity.entity(t, "application/json"));// not secured
		return r;
	}


}
