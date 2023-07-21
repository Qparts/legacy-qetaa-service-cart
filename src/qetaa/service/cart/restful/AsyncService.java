package qetaa.service.cart.restful;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import qetaa.service.cart.dao.DAO;
import qetaa.service.cart.helpers.AppConstants;
import qetaa.service.cart.helpers.Helper;
import qetaa.service.cart.model.Cart;
import qetaa.service.cart.model.CartAssignment;
import qetaa.service.cart.model.CartItem;
import qetaa.service.cart.model.KeyValue;
import qetaa.service.cart.model.parts.PartsOrder;
import qetaa.service.cart.model.parts.PartsOrderItem;
import qetaa.service.cart.model.parts.PartsOrderItemApproved;
import qetaa.service.cart.model.parts.contract.PurchaseOrderContract;
import qetaa.service.cart.model.quotation.Quotation;
import qetaa.service.cart.model.quotation.QuotationItem;
import qetaa.service.cart.model.quotation.QuotationItemApproved;
import qetaa.service.cart.model.quotation.QuotationVendorItem;
import qetaa.service.cart.model.quotation.contract.FinalizedItem;
import qetaa.service.cart.model.quotation.contract.FinalizedItemResponse;
import qetaa.service.cart.model.quotation.contract.FinalizedItemsHolder;
import qetaa.service.cart.model.security.WebApp;

@Stateless
public class AsyncService {

	@EJB
	private DAO dao;

	@Asynchronous
	public void someAsyncMethod() {
		// ...
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
			partsOrder = dao.persistAndReturn(partsOrder);
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
				sendSms(cart.getCustomerId(), cart.getId(), text, "part-paid", authHeader);
			}
			//create purchase payment
			createPurchaseOrder(cart.getId(), authHeader);	
		}
	}
	
	
	private void createPurchaseOrder(long cartId, String authHeader) {		
		List<Object> vendors = dao.getNative("select b.vendor_id, sum(b.cost_price * b.approved_quantity) from crt_parts_item_approved b where b.cart_id = "+cartId+" group by b.vendor_id");
		if(!vendors.isEmpty()) {
			List<PurchaseOrderContract> contracts = new ArrayList<>();
			for(Object v : vendors) {
				Object[] objects = (Object[]) v;
				Integer vendorId = (Integer) objects[0];
				BigDecimal sum = (BigDecimal) objects[1];
				PurchaseOrderContract contract = new PurchaseOrderContract();
				contract.setVendorId(vendorId);
				contract.setCartId(cartId);
				contract.setAmount(sum.doubleValue());
				contracts.add(contract);
			}
			this.postSecuredRequest(AppConstants.POST_PURCHASE_ORDER, contracts, authHeader);	
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

	@Asynchronous
	public void postCartCreation(Cart cart, String authHeader) {
		try {
			// persist cart items
			for (CartItem item : cart.getCartItems()) {
				item.setCartId(cart.getId());
				item.setCreated(cart.getCreated());
				dao.persist(item);
			}

			createRandomAssignment(authHeader, cart.getId(), cart.getCreatedBy(), cart.getStatus());
			sendEmail(cart.getId());
			String text = "تم استلام طلبكم ";
			text = text + "رقم: " + cart.getId() + " ";
			text = text + "نعمل الان على توفير افضل سعر و سيتم التواصل معكم قريبا";
			sendSms(cart.getCustomerId(), cart.getId(), text, "cart-created", authHeader);
		} catch (Exception ex) {
			// log
			ex.printStackTrace();
			ex.printStackTrace();
		}
	}

	@Asynchronous
	public void postQuotationCreation(Quotation qo, String authHeader) {
		try {
			Cart cart = dao.find(Cart.class, qo.getCartId());
			List<Integer> vendorIds = getVendorIds(cart.getMakeId(), authHeader);
			for (Integer vid : vendorIds) {
				for (QuotationItem qitem : qo.getQuotationItems()) {
					QuotationVendorItem vitem = new QuotationVendorItem();
					vitem.setCartId(cart.getId());
					vitem.setCreated(new Date());
					vitem.setCreatedBy(qo.getCreatedBy());
					vitem.setQuantity(qitem.getQuantity());
					vitem.setQuotationId(qo.getId());
					vitem.setQuotationItemId(qitem.getId());
					vitem.setStatus('W');
					vitem.setVendorId(vid);
					dao.persist(vitem);
				}
			}
		} catch (Exception ex) {
			// log!!
		}
	}
	
	
	@Asynchronous
	public void postQuotationCreationForFinders(Quotation qo, String authHeader) {
		try {
			Cart cart = dao.find(Cart.class, qo.getCartId());
			List<Integer> finderIds = getFinderIds(cart.getMakeId(), authHeader);
			for (Integer fid : finderIds) {
				for (QuotationItem qitem : qo.getQuotationItems()) {
					QuotationVendorItem vitem = new QuotationVendorItem();
					vitem.setCartId(cart.getId());
					vitem.setCreated(new Date());
					vitem.setCreatedBy(qo.getCreatedBy());
					vitem.setQuantity(qitem.getQuantity());
					vitem.setQuotationId(qo.getId());
					vitem.setQuotationItemId(qitem.getId());
					vitem.setStatus('W');
					vitem.setVendorId(0);
					vitem.setFinderId(fid);
					vitem.setSentTo('F');
					dao.persist(vitem);
				}
			}
		} catch (Exception ex) {
			// log!!
		}
	}

	private List<Integer> getVendorIds(int makeId, String authHeader) {
		Response r = this.getSecuredRequest(AppConstants.getQuotationVendors(makeId), authHeader);
		if (r.getStatus() == 200) {
			List<Integer> vendors = r.readEntity(new GenericType<List<Integer>>() {
			});
			return vendors;
		} else {
			return new ArrayList<>();
		}
	}
	
	private List<Integer> getFinderIds(int makeId, String authHeader){
		Response r = this.getSecuredRequest(AppConstants.getFinderIds(makeId), authHeader);
		if(r.getStatus() == 200) {
			List<Integer> finders = r.readEntity(new GenericType<List<Integer>>(){
			});
			return finders;
		}
		return new ArrayList<>();
	}

	// to be qualified
	private void sendEmail(long cartId) {

	}

	@Asynchronous
	public void approveQuotation(String authHeader, FinalizedItemsHolder holder) {
		Cart cart = dao.find(Cart.class, holder.getCartId());
		if (cart.getStatus() == 'R' && !holder.getFinalizedItems().isEmpty()) {
			// this is idempotent because its not creating new order
			Cart c = dao.find(Cart.class, holder.getCartId());
			c.setStatus('S');
			c.setDeliveryFees(holder.getDeliveryFees());
			c.setSubmitted(new Date());
			c.setSubmitteBy(holder.getCreatedBy());
			dao.update(c);
			for (FinalizedItem finalized : holder.getFinalizedItems()) {
				for (FinalizedItemResponse response : finalized.getResponses()) {
					QuotationItemApproved approved = new QuotationItemApproved();
					approved.setCartId(finalized.getCartId());
					approved.setCreated(new Date());
					approved.setCreatedBy(holder.getCreatedBy());
					approved.setPercentage(response.getSalesPercentage());
					approved.setQuantity(response.getSelectedQuantity());
					approved.setQuotationId(finalized.getQuotationId());
					approved.setQuotationItemId(finalized.getQuotationItemId());
					approved.setUnitCost(response.getUnitCost());
					approved.setVendorItemId(response.getQuotationVendorItemId());
					QuotationVendorItem qvi = dao.findCondition(QuotationVendorItem.class, "id", approved.getVendorItemId());
					approved.setProductId(qvi.getProductId());
					// check if approved item exists
					QuotationItemApproved duplicateCheck = dao.findCondition(QuotationItemApproved.class,
							"vendorItemId", approved.getVendorItemId());
					if (duplicateCheck == null) {
						dao.persist(approved);
					}
				}
			}

			updateScores(c, authHeader);
			// send sms to customer
			String smsText = "عزيزنا العميل, تسعيرتكم للطلب رقم ";
			smsText = smsText + c.getId();
			smsText = smsText + " جاهزة على الرابط ";
			smsText = smsText + "qetaa.com/codelg?c=";
			sendSmsWithAppend(c.getCustomerId(), c.getId(), smsText, "quotation-ready", authHeader);
		}
	}
	
	private void sendTestWorkshopPromoCodeSms(Cart cart, String authHeader) {
		if(cart.getCityId() == 2 || cart.getCityId() == 3 || cart.getCityId() == 9) {
			Response r = this.getSecuredRequest(AppConstants.getTestWorkshopPromoCode(cart.getId(), cart.getCustomerId(), cart.getCityId()), authHeader);
			if(r.getStatus() == 200) {
				String code = r.readEntity(String.class);
				String smsText = "عزيزنا عميل قطع.كوم, استخدم البروموكود: ";
				smsText = smsText + code;
				smsText = smsText + " للحصول على خصم خاص على عمل اليد لطلبك رقم: ";
				smsText = smsText + cart.getId();
				smsText = smsText + " لدى ورشة الإكتشاف الجديد بالدمام ";
				smsText = smsText + " goo.gl/maps/jNcNqFT7Evq ";
				sendSms(cart.getCustomerId(), cart.getId(), smsText, "workshop-promocode", authHeader);
			}
		}
		
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

	private void createRandomAssignment(String authHeader, long cartId, int assignedBy, char stage) {
		try {
			List<Integer> users = getActiveAdvisorIds(authHeader);
			List<KeyValue> keyValues = new ArrayList<>();
			for (Integer i : users) {
				List<CartAssignment> list = dao.getCondition(CartAssignment.class, "assignedTo", i);
				KeyValue kv = new KeyValue();
				kv.setKey(i);
				kv.setValue(list.size());
				keyValues.add(kv);
			}
			Collections.sort(keyValues, new Comparator<KeyValue>() {
				@Override
				public int compare(KeyValue o1, KeyValue o2) {
					return o1.getValue().compareTo(o2.getValue());
				}
			});

			Integer userId = keyValues.get(0).getKey();

			// deactivate past assignments
			String sql = "update crt_assignment set status = 'D' where cart_id = " + cartId + " and status = 'A'";
			dao.updateNative(sql);
			// Create new assignment
			CartAssignment assignment = new CartAssignment();
			assignment.setAssignedBy(assignedBy);
			assignment.setAssignedDate(new Date());
			assignment.setAssignedTo(userId);
			assignment.setCartId(cartId);
			assignment.setStage(stage);
			assignment.setStatus('A');
			dao.persist(assignment);
		} catch (Exception ex) {
			// log error

		}
	}

	private List<Integer> getActiveAdvisorIds(String authHeader) {
		Response r = getSecuredRequest(AppConstants.GET_ACTIVE_ADVISORS, authHeader);
		if (r.getStatus() == 200) {
			List<Integer> usrs = r.readEntity(new GenericType<List<Integer>>() {
			});
			return usrs;
		} else {
			return new ArrayList<>();
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

}
