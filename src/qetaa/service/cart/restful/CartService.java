package qetaa.service.cart.restful;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import qetaa.service.cart.dao.DAO;
import qetaa.service.cart.filters.Secured;
import qetaa.service.cart.filters.SecuredCustomer;
import qetaa.service.cart.filters.SecuredUser;
import qetaa.service.cart.filters.SecuredVendor;
import qetaa.service.cart.filters.ValidApp;
import qetaa.service.cart.helpers.AppConstants;
import qetaa.service.cart.helpers.Helper;
import qetaa.service.cart.model.Cart;
import qetaa.service.cart.model.CartAssignment;
import qetaa.service.cart.model.CartItem;
import qetaa.service.cart.model.CartReview;
import qetaa.service.cart.model.KeyValue;
import qetaa.service.cart.model.WireTransfer;
import qetaa.service.cart.model.parts.PartsOrder;
import qetaa.service.cart.model.parts.PartsOrderItem;
import qetaa.service.cart.model.parts.PartsOrderItemApproved;
import qetaa.service.cart.model.parts.PartsOrderItemReturn;
import qetaa.service.cart.model.parts.contract.CompletedCart;
import qetaa.service.cart.model.parts.contract.PartCollectionItem;
import qetaa.service.cart.model.quotation.Quotation;
import qetaa.service.cart.model.quotation.QuotationItem;
import qetaa.service.cart.model.quotation.QuotationItemApproved;
import qetaa.service.cart.model.quotation.QuotationItemResponse;
import qetaa.service.cart.model.quotation.QuotationVendorItem;
import qetaa.service.cart.model.quotation.contract.AdvisorCarts;
import qetaa.service.cart.model.quotation.contract.ApprovedItem;
import qetaa.service.cart.model.quotation.contract.FinalizedItem;
import qetaa.service.cart.model.quotation.contract.FinalizedItemResponse;
import qetaa.service.cart.model.quotation.contract.FinalizedItemsHolder;
import qetaa.service.cart.model.quotation.contract.ManualQuotationVendor;
import qetaa.service.cart.model.quotation.contract.VendorItemContract;
import qetaa.service.cart.model.security.WebApp;

@Path("/")
public class CartService {
	@EJB
	private DAO dao;

	@EJB
	private AsyncService async;

	@ValidApp
	@SecuredUser
	@SecuredVendor
	@SecuredCustomer
	@GET
	public void test() {

	}

	// check idempotency of a cart
	private boolean isRedudant(long customerId, Date created) {
		// if a cart was created less than 1 minute ago, then do not do
		String jpql = "select b from Cart b where b.customerId = :value0 and b.created between :value1 and :value2";
		// String sql = " select * from crt_cart "

		Date previous = Helper.addSeconds(created, -20);
		List<Cart> carts = dao.getJPQLParams(Cart.class, jpql, customerId, previous, created);
		return carts.size() > 0;

	}

	// check idempotency of a quotation
	private boolean isQuotationRedudant(int userId, long cartId, Date created) {
		// if a quotation was created less than 30 seconds ago, then do not do
		String jpql = "select b from Quotation b where b.createdBy = :value0 and b.cartId = :value1 and b.created between :value2 and :value3";
		Date previous = Helper.addSeconds(created, -20);
		List<Cart> carts = dao.getJPQLParams(Cart.class, jpql, userId, cartId, previous, created);
		return carts.size() > 0;

	}

	private List<Integer> getMakeIds(int finderId, String authHeader) {
		Response r = this.getSecuredRequest(AppConstants.getMakeIds(finderId), authHeader);
		if (r.getStatus() == 200) {
			List<Integer> finders = r.readEntity(new GenericType<List<Integer>>() {
			});
			return finders;
		}
		return new ArrayList<>();
	}

	@SecuredUser
	@POST
	@Path("quotation-item-response")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createQuotationItemResponse(@HeaderParam("Authorization") String authHeader,
			List<Map<String, Object>> list) {
		try {
			for (Map<String, Object> map : list) {
				Long cartId = (Long) map.get("cartId");
				Double cost = (Double) map.get("cost");
				Double costWv = (Double) map.get("costWv");
				Integer createdBy = (Integer) map.get("createdBy");
				String desc = (String) map.get("desc");
				Long productId = (Long) map.get("productId");
				Integer quantity = (Integer) map.get("quantity");
				Long quotationId = (Long) map.get("quotationId");
				Long quotationItemId = (Long) map.get("quotationItemId");
				Integer vendorId = (Integer) map.get("vendorId");
				Double percentage = (Double) map.get("percentage");

				QuotationItemResponse res = new QuotationItemResponse();
				res.setCartId(cartId);
				res.setCost(cost);
				res.setCostWv(costWv);
				res.setDefaultPercentage(percentage);
				res.setCreated(new Date());
				res.setCreatedBy(createdBy);
				res.setDesc(desc);
				res.setProductId(productId);
				res.setQuantity(quantity);
				res.setQuotationId(quotationId);
				res.setQuotationItemId(quotationItemId);
				res.setResponseType('F');// response from finder
				res.setVendorId(vendorId);// price from which vendor

				if (productId != null && productId != 0) {
					res.setStatus('C');
				} else {
					res.setStatus('N');// Not found
				}
				dao.persist(res);
			}
			return Response.status(201).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@POST
	@Path("quotation-item")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createNewQuotationItem(QuotationItem qi) {
		try {
			qi.setCreated(new Date());
			dao.persist(qi);
			return Response.status(201).entity(qi.getId()).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@Secured
	@GET
	@Path("waiting-quotations/user/{param}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getWaitingQuotations(@PathParam(value = "param") int userId,
			@HeaderParam("Authorization") String authHeader) {
		try {

			List<Integer> makeids = getMakeIds(userId, authHeader);
			String sql = "select * from crt_cart where status = 'W' and make_id in (0";
			for (int makeId : makeids) {
				sql = sql + "," + makeId;
			}
			sql = sql + ")";
			List<Cart> carts = dao.getNative(Cart.class, sql);
			for (Cart cart : carts) {
				cart.setModelYear(getModelYearObjectFromId(cart.getVehicleYear(), authHeader));
				List<Quotation> qs = dao.getTwoConditions(Quotation.class, "cartId", "status", cart.getId(), 'W');
				for (Quotation q : qs) {
					List<QuotationItem> qis = dao.getTwoConditions(QuotationItem.class, "quotationId", "status",
							q.getId(), 'W');
					q.setQuotationItems(qis);
				}
				cart.setQuotations(qs);
			}
			return Response.status(200).entity(carts).build();

		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@Secured
	@PUT
	@Path("cart")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateCart(Cart cart) {
		try {
			cart.setVin(cart.getVin().toUpperCase());
			dao.update(cart);
			return Response.status(201).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	// idempotent (by only allowing to create one cart per customer per minute)
	@Secured
	@POST
	@Path("cart")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createCart(@HeaderParam("Authorization") String authHeader, Cart cart) {
		try {
			// persist cart
			WebApp wa = this.getWebAppFromAuthHeader(authHeader);
			cart.setCreated(new Date());
			cart.setStatus('N');
			cart.setVin(cart.getVin().toUpperCase());
			cart.setAppCode(wa.getAppCode());
			if (isRedudant(cart.getCustomerId(), cart.getCreated())) {
				return Response.status(429).build();
			}

			cart.setVatPercentage(0.05);
			cart = dao.persistAndReturn(cart);
			async.postCartCreation(cart, authHeader);
			return Response.status(200).entity(cart.getId()).build();
		} catch (Exception ex) {
			ex.printStackTrace();
			// log
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@GET
	@Path("waiting-part-cart/cart/{param}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getWaitingPartCart(@PathParam(value = "param") long cartId) {
		try {
			String jpql = "select b from Cart b where b.id = :value0 and b.status = :value1";
			Cart cart = dao.findJPQLParams(Cart.class, jpql, cartId, 'P');
			if (cart == null) {
				return Response.status(404).build();
			}
			return Response.status(200).entity(cart).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@GET
	@Path("waiting-part-carts")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getWaitingPartCarts() {
		try {
			String jpql = "select b from Cart b where b.status in (:value0, :value1, :value2)";
			List<Cart> carts = dao.getJPQLParams(Cart.class, jpql, 'P', 'V', 'E');
			return Response.status(200).entity(carts).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@GET
	@Path("no-vin-cart/{param}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getNoVinCart(@HeaderParam("Authorization") String authHeader,
			@PathParam(value = "param") long cartId) {
		try {
			Cart cart = dao.findThreeConditions(Cart.class, "status", "noVin", "id", 'N', true, cartId);
			this.prepareCart(cart, authHeader);
			List<CartReview> reviews = dao.getCondition(CartReview.class, "cartId", cartId);
			cart.setReviews(reviews);
			return Response.status(200).entity(cart).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@GET
	@Path("no-vin-carts")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getNoVinCarts(@HeaderParam("Authorization") String authHeader) {
		try {

			List<Cart> carts = dao.getTwoConditionsOrdered(Cart.class, "status", "noVin", 'N', true, "created", "asc");
			for (Cart cart : carts) {
				List<CartReview> reviews = dao.getConditionOrdered(CartReview.class, "cartId", cart.getId(), "created",
						"asc");
				cart.setReviews(reviews);
				List<CartItem> cartItems = dao.getCondition(CartItem.class, "cartId", cart.getId());
				cart.setCartItems(cartItems);
			}
			return Response.status(200).entity(carts).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@GET
	@Path("waiting-quotation-carts")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getWaitingQuotationCarts(@HeaderParam("Authorization") String authHeader) {
		try {
			String jpql = "select b from Cart b where b.status in (:value0, :value1, :value2, :value3, :value4) and b.noVin = :value5 order by b.id";
			List<Cart> carts = dao.getJPQLParams(Cart.class, jpql, 'N', 'Q', 'W', 'R', 'A', false);
			for (Cart cart : carts) {
				prepareCart(cart, authHeader);
			}
			return Response.status(200).entity(carts).build();

		} catch (Exception ex) {
			ex.printStackTrace();
			return Response.status(500).build();
		}
	}

	private void prepareCart(Cart cart, String authHeader) {
		try {
			List<CartItem> items = dao.getCondition(CartItem.class, "cartId", cart.getId());
			cart.setCartItems(items);
			cart.setCustomer(getCustomer(cart.getCustomerId(), authHeader));
			cart.setModelYear(getModelYearObjectFromId(cart.getVehicleYear(), authHeader));
		} catch (Exception ex) {

		}
	}

	@SecuredUser
	@GET
	@Path("waiting-quotation-carts/assigned-to/{param}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getWaitingQuotationCarts(@PathParam(value = "param") int userId,
			@HeaderParam("Authorization") String authHeader) {
		try {
			String jpql = "select b from Cart b where b.status in (:value0, :value1, :value2, :value3, :value4) and b.noVin = :value7 "
					+ "and b.id in (select a.cartId From CartAssignment a where a.assignedTo = :value5 and a.status = :value6) order by b.id";
			List<Cart> carts = dao.getJPQLParams(Cart.class, jpql, 'N', 'Q', 'W', 'R', 'A', userId, 'A', false);
			for (Cart cart : carts) {
				prepareCart(cart, authHeader);
			}
			return Response.status(200).entity(carts).build();
		} catch (Exception ex) {
			return Response.status(500).build();

		}
	}

	@SecuredUser
	@GET
	@Path("carts-count")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCartsCount() {
		try {
			String jpql = "select count(b) from Cart b";
			Long count = dao.findJPQLParams(Long.class, jpql);
			return Response.status(200).entity(count).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@GET
	@Path("carts-count/today")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCartCountsToday() {
		try {
			Date from = new Date();
			Calendar cFrom = Calendar.getInstance();
			cFrom.set(Calendar.HOUR_OF_DAY, 0);
			cFrom.set(Calendar.MINUTE, 0);
			cFrom.set(Calendar.SECOND, 0);
			cFrom.set(Calendar.MILLISECOND, 0);
			from.setTime(cFrom.getTimeInMillis());

			Date to = new Date();
			Calendar cTo = Calendar.getInstance();
			cTo.set(Calendar.HOUR_OF_DAY, 23);
			cTo.set(Calendar.MINUTE, 59);
			cTo.set(Calendar.SECOND, 59);
			cTo.set(Calendar.MILLISECOND, 999);
			to.setTime(cTo.getTimeInMillis());

			String jpql = "select count(b) from Cart b where b.created between :value0 and :value1";
			Long count = dao.findJPQLParams(Long.class, jpql, from, to);
			return Response.status(200).entity(count).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@GET
	@Path("part-orders-count/today")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPartOrdersCountToday() {
		try {
			Date from = new Date();
			Calendar cFrom = Calendar.getInstance();
			cFrom.set(Calendar.HOUR_OF_DAY, 0);
			cFrom.set(Calendar.MINUTE, 0);
			cFrom.set(Calendar.SECOND, 0);
			cFrom.set(Calendar.MILLISECOND, 0);
			from.setTime(cFrom.getTimeInMillis());

			Date to = new Date();
			Calendar cTo = Calendar.getInstance();
			cTo.set(Calendar.HOUR_OF_DAY, 23);
			cTo.set(Calendar.MINUTE, 59);
			cTo.set(Calendar.SECOND, 59);
			cTo.set(Calendar.MILLISECOND, 999);
			to.setTime(cTo.getTimeInMillis());

			String jpql = "select count(b) from PartsOrder b and b.created between :value0 and :value1";
			Long count = dao.findJPQLParams(Long.class, jpql, from, to);
			return Response.status(200).entity(count).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@GET
	@Path("part-orders-count")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPartOrdersCount() {
		try {
			String jpql = "select count(b) from PartsOrder b";
			Long count = dao.findJPQLParams(Long.class, jpql);
			return Response.status(200).entity(count).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@GET
	@Path("parts-order-cart/{param}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPartsCart(@HeaderParam("Authorization") String authHeader,
			@PathParam(value = "param") long cartId) {
		try {
			String jpql = "select b from Cart b where b.id = :value0 and b.status in (:value1, :value2, :value3, :value4, :value5)";
			Cart cart = dao.findJPQLParams(Cart.class, jpql, cartId, 'P', 'V', 'E', 'H', 'F');
			if (cart == null) {
				return Response.status(404).build();
			}
			prepareCart(cart, authHeader);
			List<CartItem> items = dao.getCondition(CartItem.class, "cartId", cart.getId());
			cart.setCartItems(items);
			return Response.status(200).entity(cart).build();
		} catch (Exception e) {
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@GET
	@Path("shipped-cart/{param}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getShippedPartsCart(@HeaderParam("Authorization") String authHeader,
			@PathParam(value = "param") long cartId) {
		try {
			String jpql = "select b from Cart b where b.id = :value0 and b.status in (:value1, :value2)";
			Cart cart = dao.findJPQLParams(Cart.class, jpql, cartId, 'H', 'F');
			if (cart == null) {
				return Response.status(404).build();
			}
			this.prepareCart(cart, authHeader);
			List<CartItem> items = dao.getCondition(CartItem.class, "cartId", cart.getId());
			cart.setCartItems(items);
			return Response.status(200).entity(cart).build();
		} catch (Exception e) {
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@GET
	@Path("cart/{param}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCart(@HeaderParam("Authorization") String authHeader, @PathParam(value = "param") long cartId) {
		try {
			String jpql = "select b from Cart b where b.id = :value0 order by b.id";
			Cart cart = dao.findJPQLParams(Cart.class, jpql, cartId);
			if (cart == null) {
				return Response.status(409).build();
			}
			this.prepareCart(cart, authHeader);
			List<CartItem> items = dao.getCondition(CartItem.class, "cartId", cart.getId());
			cart.setCartItems(items);
			return Response.status(200).entity(cart).build();
		} catch (Exception ex) {
			ex.getMessage();
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@GET
	@Path("cart-internal/{param}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCartInternal(@HeaderParam("Authorization") String authHeader,
			@PathParam(value = "param") long cartId) {
		try {
			String jpql = "select b from Cart b where b.id = :value0 order by b.id";
			Cart cart = dao.findJPQLParams(Cart.class, jpql, cartId);
			if (cart == null) {
				return Response.status(409).build();
			}
			this.prepareCart(cart, authHeader);
			String jpql2 = "select b from Cart b where b.status = :value0 and b.id = :value1 and b.id in ("
					+ "select c.cartId" + "  from CartReview c"
					+ "  where c.id IN (SELECT MAX(d.id) FROM CartReview d GROUP BY d.cartId) and c.status = :value2"
					+ ") order by b.created";
			List<Cart> closed = dao.getJPQLParams(Cart.class, jpql2, 'S', cart.getId(), 'C');
			if (!closed.isEmpty()) {
				cart.setStatus('O');
			}
			List<CartItem> items = dao.getCondition(CartItem.class, "cartId", cart.getId());
			cart.setCartItems(items);
			return Response.status(200).entity(cart).build();
		} catch (Exception ex) {
			ex.getMessage();
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@POST
	@Path("filter-out-active-customers")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response filterOutActiveCustomers(List<Long> list) {
		try {
			String values = "";
			for (Long l : list) {
				values = values + "," + l;
			}
			String sql = "select * from crt_cart where id not in (0" + values + ")";
			List<Cart> carts = dao.getNative(Cart.class, sql);
			List<Long> filtered = new ArrayList<>();
			for (Cart cart : carts) {
				filtered.add(cart.getCustomerId());
			}
			return Response.status(200).entity(filtered).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@GET
	@Path("cart/{param}/requester-id/{param2}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRequesterCart(@PathParam(value = "param") long cartId,
			@PathParam(value = "param2") int requesterId) {
		try {
			String jpql = "select b from Cart b where b.id = :value0 and b.id in "
					+ "(select a.cartId from CartAssignment a where a.assignedTo = :value1) order by b.id";
			Cart cart = dao.findJPQLParams(Cart.class, jpql, cartId, requesterId);
			if (cart == null) {
				return Response.status(409).build();
			}
			List<CartItem> items = dao.getCondition(CartItem.class, "cartId", cart.getId());
			cart.setCartItems(items);
			return Response.status(200).entity(cart).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@Secured
	@PUT
	@Path("merge-cart/main/{mainId}/slave/{slaveId}/user/{userId}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response mergeCarts(@PathParam(value = "mainId") long mainId, @PathParam(value = "slaveId") long slaveId,
			@PathParam(value = "userId") int userId) {
		try {
			Cart main = dao.find(Cart.class, mainId);
			Cart slave = dao.find(Cart.class, slaveId);
			List<CartItem> slaveItems = dao.getCondition(CartItem.class, "cartId", slave.getId());
			for (CartItem ci : slaveItems) {
				ci.setCartId(main.getId());
				dao.update(ci);
			}
			List<Quotation> quotations = dao.getCondition(Quotation.class, "cartId", slave.getId());
			for (Quotation q : quotations) {
				q.setCartId(main.getId());
				dao.update(q);
			}
			List<QuotationItem> quotationItems = dao.getCondition(QuotationItem.class, "cartId", slave.getId());
			for (QuotationItem qi : quotationItems) {
				qi.setCartId(main.getId());
				dao.update(qi);
			}
			List<QuotationVendorItem> vendorItems = dao.getCondition(QuotationVendorItem.class, "cartId",
					slave.getId());
			for (QuotationVendorItem qvi : vendorItems) {
				qvi.setCartId(main.getId());
				dao.update(qvi);
			}
			CartReview review = new CartReview();
			review.setStage(2);
			review.setActionValue('X');
			review.setStatus('C');
			review.setCartId(slave.getId());
			review.setCreated(new Date());
			review.setReviewerId(userId);
			review.setReviewText("Merged to : " + main.getId());
			dao.persist(review);
			closePostponedAndActiveReviews(slave.getId());
			if (main.getStatus() != 'N') {
				verifyQuotationCompletion(main.getId());
			}
			if (main.getStatus() == 'R' && slave.getStatus() != 'R') {
				main.setStatus(slave.getStatus());
			}
			slave.setStatus('X');
			dao.update(slave);
			return Response.status(201).build();
		} catch (Exception ex) {
			ex.printStackTrace();
			return Response.status(500).build();
		}
	}

	// idempotent
	@Secured
	@POST
	@Path("wire-transfer")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createWireTransfer(@HeaderParam("Authorization") String authHeader, PartsOrder partsOrder) {
		try {
			// create wire transfer request,
			WireTransfer wire = new WireTransfer();
			wire.setCreated(new Date());
			wire.setCartId(partsOrder.getCartId());
			Cart cart = dao.find(Cart.class, partsOrder.getCartId());
			wire.setCustomerId(cart.getCustomerId());
			wire.setStatus('W');
			double grandTotal = 0;
			double dfees = cart.getDeliveryFees();
			double vat = cart.getVatPercentage();
			double partsTotal = partsOrder.getTotalPartsPrice();
			double discount = 0;
			if (null != cart.getPromotionCode()) {
				double perc = getPromotionPercentage(authHeader, cart.getPromotionCode());
				discount = (perc * partsTotal) + (perc * dfees);
			}

			grandTotal = partsTotal + dfees + ((partsTotal + dfees) * vat) - discount;
			wire.setAmount(grandTotal);
			if (cart.getStatus() == 'T') {
				return Response.status(409).build();
			}
			WireTransfer wt = dao.findCondition(WireTransfer.class, "cartId", cart.getId());
			if (wt != null) {
				return Response.status(409).build();
			}
			dao.persist(wire);
			cart.setStatus('T');
			dao.update(cart);
			// then create parts order
			async.createPartsOrder(partsOrder, authHeader);
			// update cart status to 'T'
			return Response.status(201).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	private double getPromotionPercentage(String authHeader, Integer promoId) {
		Response r = this.getSecuredRequest(AppConstants.getPromotionDiscount(promoId), authHeader);
		if (r.getStatus() == 200) {
			return r.readEntity(Double.class);
		} else {
			return 0D;
		}
	}

	@SecuredUser
	@GET
	@Path("address/cart/{cart}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAddress(@HeaderParam("Authorization") String authHeader,
			@PathParam(value = "cart") long cartId) {
		try {
			Map<String, Object> map = getAddressFromCartId(cartId, authHeader);
			return Response.status(200).entity(map).build();
		} catch (Exception ex) {
			ex.printStackTrace();
			return Response.status(500).build();
		}
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> getAddressFromCartId(long cartId, String authHeader) throws Exception {
		PartsOrder c = dao.findCondition(PartsOrder.class, "cartId", cartId);
		Response r = this.getSecuredRequest(AppConstants.getAddress(c.getAddressId()), authHeader);
		if (r.getStatus() == 200) {
			return r.readEntity(Map.class);
		} else
			throw new Exception();
	}

	@SecuredUser
	@GET
	@Path("/quotation-notification/assigned-to/{param}")
	public Response getAssignedNumberWaitingQuotationCarts(@PathParam(value = "param") int assignedTo) {
		try {
			String jpql = "select count(b) from Cart b where b.status in (:value0, :value1, :value2, :value3, :value4)"
					+ "and b.id in (select a.cartId from CartAssignment a where a.assignedTo = :value5 and a.status = :value6) and b.noVin = :value7";
			Long count = dao.findJPQLParams(Long.class, jpql, 'N', 'Q', 'W', 'R', 'A', assignedTo, 'A', false);
			return Response.status(200).entity(count).build();
		} catch (Exception ex) {
			// log error
			return Response.status(500).build();
		}
	}

	// check idempotency of a returnItem
	private boolean isReturnItemRedudant(long approvedId, Date created) {
		// if a quotation was created less than 30 seconds ago, then do not do
		String jpql = "select b from PartsOrderItemReturn b where b.approvedId = :value0 and b.returned between :value1 and :value2";
		Date previous = Helper.addSeconds(created, -5);// five seconds
		List<PartsOrderItemReturn> returns = dao.getJPQLParams(PartsOrderItemReturn.class, jpql, approvedId, previous,
				created);
		return returns.size() > 0;
	}

	@SecuredUser
	@GET
	@Path("return-items/cart/{cartId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getReturnItems(@PathParam(value = "cartId") long cartId) {
		try {
			List<PartsOrderItemReturn> returns = dao.getCondition(PartsOrderItemReturn.class, "cartId", cartId);
			return Response.status(200).entity(returns).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	// semi idempotent
	@SecuredUser
	@POST
	@Path("return-item")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response returnItem(PartsOrderItemReturn returnItem) {
		try {
			returnItem.setReturned(new Date());
			returnItem.setStatus('S');
			if (isReturnItemRedudant(returnItem.getApprovedId(), returnItem.getReturned())) {
				return Response.status(409).build();// same created 5 seconds ago
			}
			// getApprovedItem
			PartsOrderItemApproved approved = dao.find(PartsOrderItemApproved.class, returnItem.getApprovedId());
			List<PartsOrderItemReturn> returns = dao.getCondition(PartsOrderItemReturn.class, "approvedId",
					returnItem.getApprovedId());
			int returnedQuantity = 0;
			for (PartsOrderItemReturn r : returns) {
				returnedQuantity = returnedQuantity + r.getReturnQuantity();
			}
			if (returnedQuantity + returnItem.getReturnQuantity() > approved.getApprovedQuantity()) {
				return Response.status(403).build();// forbidden refuse action! more returned than actual
			}
			dao.persist(returnItem);

			String jpql = "select sum(approvedQuantity) from PartsOrderItemApproved where cartId = :value0";
			Long allApproved = dao.findJPQLParams(Long.class, jpql, returnItem.getCartId());
			String jpql2 = "select sum(returnQuantity) from PartsOrderItemReturn where cartId = :value0";
			Long allReturned = dao.findJPQLParams(Long.class, jpql2, returnItem.getCartId());
			if (allApproved.longValue() == allReturned.longValue()) {
				PartsOrder po = dao.find(PartsOrder.class, returnItem.getPartsOrderId());
				// po.setStatus('H');
				Cart cart = dao.find(Cart.class, returnItem.getCartId());
				if (cart.getStatus() == 'P' || cart.getStatus() == 'V' || cart.getStatus() == 'E') {
					po.setStatus('S');// shipped
					dao.update(po);
					cart.setStatus('H');// shipped
					dao.update(cart);
				}
			}
			return Response.status(201).build();
		} catch (Exception ex) {
			ex.printStackTrace();
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@GET
	@Path("quotation-notification")
	public Response getTotalNumberWaitingQuotationCarts() {
		try {
			String jpql = "select count(b) from Cart b where b.status in (:value0, :value1, :value2, :value3, :value4) and b.noVin = :value5";
			Long count = dao.findJPQLParams(Long.class, jpql, 'N', 'Q', 'W', 'R', 'A', false);
			return Response.status(200).entity(count).build();
		} catch (Exception ex) {
			// log error
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@GET
	@Path("no-vin-notification")
	public Response getTotalNumberNoVinCarts() {
		try {
			String jpql = "select count(b) from Cart b where b.status in (:value0, :value1, :value2, :value3, :value4) and b.noVin =:value5";
			Long count = dao.findJPQLParams(Long.class, jpql, 'N', 'Q', 'W', 'R', 'A', true);
			return Response.status(200).entity(count).build();
		} catch (Exception ex) {
			// log error
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@GET
	@Path("parts-notification")
	public Response getNumberPartsCarts() {
		try {
			String jpql = "select count(b) from Cart b where b.status in (:value0, :value1, :value2)";
			Long count = dao.findJPQLParams(Long.class, jpql, 'P', 'V', 'E');
			return Response.status(200).entity(count).build();
		} catch (Exception ex) {
			// log error
			return Response.status(500).build();
		}

	}

	@SecuredUser
	@GET
	@Path("wire-notification")
	public Response getNumberWireTransfers() {
		try {
			String jpql = "select count(b) from WireTransfer b where b.status = :value0";
			Long count = dao.findJPQLParams(Long.class, jpql, 'W');
			return Response.status(200).entity(count).build();
		} catch (Exception ex) {
			// log error
			return Response.status(500).build();
		}

	}

	@SecuredUser
	@GET
	@Path("collection-items-notification")
	public Response getCollectionItemsNotifications() {
		try {
			String jpql = "select count(b) from PartsOrderItemApproved b where b.status in (:value0, :value1) and b.cartId in ("
					+ "select c.id from Cart c where c.status in (:value2, :value3, :value4))";
			Long count = dao.findJPQLParams(Long.class, jpql, 'W', 'V', 'P', 'V', 'E');
			return Response.status(200).entity(count).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@GET
	@Path("receival-items-notification")
	public Response getReceivalItemsNotifications() {
		try {
			String jpql = "select count(b) from PartsOrderItemApproved b where b.status = :value0 and b.cartId in ("
					+ "select c.id from Cart c where c.status in (:value1, :value2, :value3))";
			Long count = dao.findJPQLParams(Long.class, jpql, 'C', 'P', 'V', 'E');
			return Response.status(200).entity(count).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	private List<PartCollectionItem> getCollectionFromPartsApprovedItemsItems(List<PartsOrderItemApproved> approvedList,
			String authHeader) throws Exception {
		List<PartCollectionItem> collections = new ArrayList<>();
		for (PartsOrderItemApproved pApproved : approvedList) {
			PartCollectionItem col = new PartCollectionItem();
			String jpql2 = "select b from QuotationVendorItem b where b.id in ("
					+ "select c.vendorItemId from QuotationItemApproved c where c.id =:value0)";
			QuotationVendorItem qvi = dao.findJPQLParams(QuotationVendorItem.class, jpql2,
					pApproved.getQuotationItemApprovedId());
			col.setCartId(pApproved.getCartId());
			col.setItemDesc(qvi.getItemDesc());
			col.setItemNumber(pApproved.getItemNumber());
			col.setPartsApprovedId(pApproved.getId());
			col.setQuantity(pApproved.getApprovedQuantity());
			col.setUnitCost(pApproved.getCostPrice());
			col.setVendorId(pApproved.getVendorId());
			col.setQuoted(qvi.getResponded());
			col.setQuotedBy(qvi.getRespondedBy());
			col.setQuotedByObject(getVendorUser(qvi.getRespondedBy(), authHeader));
			col.setCollected(pApproved.getCollected());
			col.setCollectedBy(pApproved.getCollectedBy());
			col.setCollectedByObject(getUser(col.getCollectedBy(), authHeader));
			col.setReceived(pApproved.getReceived());
			col.setReceivedBy(pApproved.getCollectedBy());
			col.setReceivedByObject(getUser(col.getReceivedBy(), authHeader));
			col.setPrepared(pApproved.getPrepared());
			col.setPreparedBy(pApproved.getPreparedBy());
			col.setPreparedByObject(this.getVendorUser(pApproved.getPreparedBy(), authHeader));
			col.setStatus(pApproved.getStatus());
			collections.add(col);
		}
		return collections;
	}

	@SecuredVendor
	@GET
	@Path("/vendor-parts-items/vendor/{vendor}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getVendorPartsItems(@HeaderParam("Authorization") String authHeader,
			@PathParam(value = "vendor") int vendorId) {
		try {
			String jpql = "select b from PartsOrderItemApproved b where b.status in (:value0, :value1) and b.quotationItemApprovedId in ("
					+ "select c.id from QuotationItemApproved c where c.vendorItemId in ("
					+ "select d.id from QuotationVendorItem d where d.vendorId = :value2)) and b.cartId not in ("
					+ "select w.cartId from WireTransfer w where w.status = :value3" + ")";
			List<PartsOrderItemApproved> approvedList = dao.getJPQLParams(PartsOrderItemApproved.class, jpql, 'W', 'V',
					vendorId, 'W');
			List<PartCollectionItem> collections = getCollectionFromPartsApprovedItemsItems(approvedList, authHeader);
			return Response.status(200).entity(collections).build();
		} catch (Exception ex) {
			ex.printStackTrace();
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@GET
	@Path("collection-items/cart/{param}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCartCollectionItem(@HeaderParam("Authorization") String authHeader,
			@PathParam(value = "param") long cartId) {
		try {

			List<PartsOrderItemApproved> approvedList = dao.getCondition(PartsOrderItemApproved.class, "cartId",
					cartId);
			List<PartCollectionItem> collections = this.getCollectionFromPartsApprovedItemsItems(approvedList,
					authHeader);
			return Response.status(200).entity(collections).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@GET
	@Path("receival-items")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getActiveReceivalItems(@HeaderParam("Authorization") String authHeader) {
		try {
			String jpql = "select b from PartsOrderItemApproved b where b.status in (:value0, :value1, :value2, :value3) and b.cartId in ("
					+ "select c from Cart c where c.status in (:value4, :value5, :value6))";
			List<PartsOrderItemApproved> approvedList = dao.getJPQLParams(PartsOrderItemApproved.class, jpql, 'C', 'R',
					'V', 'W', 'P', 'V', 'E');

			List<PartCollectionItem> collections = this.getCollectionFromPartsApprovedItemsItems(approvedList,
					authHeader);
			Collections.sort(collections, new Comparator<PartCollectionItem>() {
				@Override
				public int compare(PartCollectionItem o1, PartCollectionItem o2) {
					return o1.getCartId().compareTo(o2.getCartId());
				}
			});
			return Response.status(200).entity(collections).build();
		} catch (Exception ex) {
			ex.printStackTrace();
			return Response.status(500).build();
		}

	}

	@SecuredUser
	@GET
	@Path("collection-items")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getActiveCollectionItems(@HeaderParam("Authorization") String authHeader) {
		try {
			String jpql = "select b from PartsOrderItemApproved b where b.status in (:value0, :value1) and "
					+ "b.cartId in (select c.id from Cart c where c.status in (:value2, :value3, :value4)";
			List<PartsOrderItemApproved> approvedList = dao.getJPQLParams(PartsOrderItemApproved.class, jpql, 'W', 'V',
					'P', 'V', 'E');
			List<PartCollectionItem> collections = this.getCollectionFromPartsApprovedItemsItems(approvedList,
					authHeader);
			Collections.sort(collections, new Comparator<PartCollectionItem>() {
				@Override
				public int compare(PartCollectionItem o1, PartCollectionItem o2) {
					return o1.getVendorId().compareTo(o2.getVendorId());
				}
			});
			return Response.status(200).entity(collections).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}

	}

	@SecuredVendor
	@GET
	@Path("/vendor-quotation-notification/vendor/{param}")
	public Response getVendorNumberWaitingQuotationItems(@PathParam(value = "param") int vendorId) {
		try {
			String jpql = "select count(b) from QuotationVendorItem b where b.status = :value0 and vendor_id = :value1";
			Long count = dao.findJPQLParams(Long.class, jpql, 'W', vendorId);
			return Response.status(200).entity(count).build();
		} catch (Exception ex) {
			// log error
			return Response.status(500).build();
		}

	}

	@SecuredVendor
	@GET
	@Path("/vendor-parts-notification/vendor/{param}")
	public Response getVendorNumberWaitingPartsItems(@PathParam(value = "param") int vendorId) {
		try {
			String jpql = "select count(b) from PartsOrderItemApproved b where b.status = :value0 and b.quotationItemApprovedId in ("
					+ "select c.id from QuotationItemApproved c where vendorItemId in ("
					+ "select d.id from QuotationVendorItem d where d.vendorId = :value1))";
			Long count = dao.findJPQLParams(Long.class, jpql, 'W', vendorId);
			return Response.status(200).entity(count).build();
		} catch (Exception ex) {
			// log error
			return Response.status(500).build();
		}

	}

	@SecuredUser
	@GET
	@Path("/quotations/cart/{param}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCartQuotations(@PathParam(value = "param") long cartId) {
		try {
			List<Quotation> quotations = dao.getCondition(Quotation.class, "cartId", cartId);
			for (Quotation q : quotations) {
				List<QuotationItem> qitems = dao.getCondition(QuotationItem.class, "quotationId", q.getId());
				if (q.getStatus() != 'N') {
					for (QuotationItem qitem : qitems) {
						List<QuotationVendorItem> qvis = dao.getCondition(QuotationVendorItem.class, "quotationItemId",
								qitem.getId());
						qitem.setVendorItems(qvis);
					}
				}
				q.setQuotationItems(qitems);
			}
			return Response.status(200).entity(quotations).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@GET
	@Path("/unassigned-notification")
	public Response getNumberUnassignedQuotationCarts() {
		try {
			String jpql = "select count(b) from Cart b where b.status in (:value0, :value1, :value2, :value3, :value4) and b.id in ("
					+ " select a.id from CartAssignment a where a.status = :value5 and a.id = b.id)";
			Long count = dao.findJPQLParams(Long.class, jpql, 'N', 'Q', 'W', 'R', 'A', 'A');
			return Response.status(200).entity(count).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> getCustomer(long customerId, String authHeader) {
		Response r = this.getSecuredRequest(AppConstants.getCustomer(customerId), authHeader);
		if (r.getStatus() == 200) {
			return r.readEntity(Map.class);
		} else {
			return null;
		}
	}

	// to be qualified
	/// private void sendEmail(long cartId) {

	// }

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

	@SecuredUser
	@POST
	@Path("manual-quotation-vendor")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response submitManualQuotation(@HeaderParam("Authorization") String authHeader,
			ManualQuotationVendor manual) {
		try {
			Quotation q = dao.find(Quotation.class, manual.getQuotationItem().getQuotationId());
			q.setStatus('W');
			dao.update(q);
			manual.getQuotationItem().setStatus('W');
			dao.update(manual.getQuotationItem());
			Cart c = dao.find(Cart.class, q.getCartId());
			c.setStatus('W');
			dao.update(c);
			// create quotationvendor item
			QuotationVendorItem vitem = new QuotationVendorItem();
			vitem.setCartId(c.getId());
			vitem.setCreated(new Date());
			vitem.setCreatedBy(manual.getCreatedBy());
			vitem.setQuantity(manual.getQuotationItem().getQuantity());
			vitem.setQuotationId(q.getId());
			vitem.setQuotationItemId(manual.getQuotationItem().getId());
			vitem.setStatus('W');
			vitem.setVendorId(manual.getVendorId());
			vitem.setProductId(this.findProductId(vitem, authHeader));
			dao.persist(vitem);
			return Response.status(201).build();
		} catch (Exception ex) {
			ex.printStackTrace();
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@POST
	@Path("additional-quotation")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response submitAdditionalQuotation(@HeaderParam("Authorization") String authHeader, Quotation qo) {
		try {
			qo.setCreated(new Date());
			// check idempotency
			if (isQuotationRedudant(qo.getCreatedBy(), qo.getCartId(), qo.getCreated())) {
				return Response.status(409).build();
			}
			qo.setStatus('N');
			qo.setDeadline(Helper.addDeadline(qo.getCreated()));
			qo = dao.persistAndReturn(qo);
			for (QuotationItem item : qo.getQuotationItems()) {
				item.setCreated(qo.getCreated());
				item.setStatus('N');
				item.setQuotationId(qo.getId());
				item.setCreatedBy(qo.getCreatedBy());
				item.setCartId(qo.getCartId());
				dao.persist(item);
			} // update quotation holder
			String sql = "update crt_cart set status = 'A' where id = " + qo.getCartId();
			dao.updateNative(sql);

			// detele approved if exists
			String sql2 = "delete from crt_quotation_item_approved where cart_id = " + qo.getCartId();
			dao.updateNative(sql2);
			// unassingCartReview
			this.unassignCart(qo.getCartId());
			// create new assignment
			this.createRandomAssignment(authHeader, qo.getCartId(), qo.getCreatedBy(), 'A');
			// postQuotationCreation(qo, authHeader);
			return Response.status(200).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@PUT
	@Path("additional-quotation")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateAdditionalQuotation(@HeaderParam("Authorization") String authHeader, Quotation qo) {
		try {
			// update quotation
			qo.setStatus('W');
			qo.setDeadline(Helper.addDeadline(qo.getCreated()));
			dao.update(qo);
			// delete previous items
			List<QuotationItem> oldQuotationItems = dao.getCondition(QuotationItem.class, "quotationId", qo.getId());
			for (QuotationItem qitem : oldQuotationItems) {
				dao.delete(qitem);
			}
			// create new items
			for (QuotationItem item : qo.getQuotationItems()) {
				item.setId(0);
				item.setCreated(qo.getCreated());
				item.setStatus('W');
				item.setQuotationId(qo.getId());
				item.setCreatedBy(qo.getCreatedBy());
				item.setCartId(qo.getCartId());
				dao.persist(item);
			}
			String sql = "update crt_cart set status = 'W' where id = " + qo.getCartId();
			dao.updateNative(sql);
			async.postQuotationCreation(qo, authHeader);
			return Response.status(200).build();
			//
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@DELETE
	@Path("quotation-item/{param}")
	public Response deleteQuotationItem(@PathParam(value = "param") long qoutationItemId) {
		try {
			QuotationItem qi = dao.find(QuotationItem.class, qoutationItemId);
			// delete vendor quotation items,
			String sql2 = "delete from crt_quotation_vendor_item where quotation_item_id = " + qi.getId();
			dao.updateNative(sql2);
			// delete approved
			String sql3 = "delete from crt_quotation_item_approved where quotation_item_id = " + qi.getId();
			dao.updateNative(sql3);
			dao.delete(qi);
			verifyQuotationCompletion(qi.getCartId());
			return Response.status(201).build();
		} catch (Exception ex) {
			ex.printStackTrace();
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@DELETE
	@Path("quotation/{param}")
	public Response deleteQuotation(@PathParam(value = "param") long qoutationId) {
		try {
			Quotation qo = dao.find(Quotation.class, qoutationId);
			// delete quotation items,
			String sql = "delete from crt_quotation_item where quotation_id = " + qo.getId();
			dao.updateNative(sql);
			// delete vendor quotation items,
			String sql2 = "delete from crt_quotation_vendor_item where quotation_id = " + qo.getId();
			dao.updateNative(sql2);
			// delete approved
			String sql3 = "delete from crt_quotation_item_approved where quotation_id = " + qo.getId();
			dao.updateNative(sql3);
			// delete quotation
			dao.delete(qo);
			List<Quotation> qs = dao.getCondition(Quotation.class, "cartId", qo.getCartId());
			if (qs.isEmpty()) {
				Cart c = dao.find(Cart.class, qo.getCartId());
				c.setStatus('N');
				dao.update(c);
			}
			verifyQuotationCompletion(qo.getCartId());
			return Response.status(201).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@POST
	@Path("quotation/finders")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response submitQuotationToFinders(@HeaderParam("Authorization") String authHeader, Quotation qo) {
		try {
			qo.setCreated(new Date());
			// check idempotency
			if (isQuotationRedudant(qo.getCreatedBy(), qo.getCartId(), qo.getCreated())) {
				return Response.status(409).build();
			}
			qo.setStatus('W');
			qo.setDeadline(Helper.addDeadline(qo.getCreated()));
			dao.persist(qo);
			for (QuotationItem item : qo.getQuotationItems()) {
				item.setCreated(qo.getCreated());
				item.setStatus('W');
				item.setQuotationId(qo.getId());
				item.setCreatedBy(qo.getCreatedBy());
				item.setCartId(qo.getCartId());
				dao.persist(item);
			}
			String sql = "update crt_cart set status = 'W' where id = " + qo.getCartId();
			dao.updateNative(sql);
			// async.postQuotationCreationForFinders(qo, authHeader);
			return Response.status(200).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	// idempotent
	@SecuredUser
	@POST
	@Path("quotation")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response submitQuotation(@HeaderParam("Authorization") String authHeader, Quotation qo) {
		try {
			qo.setCreated(new Date());
			// check idempotency
			if (isQuotationRedudant(qo.getCreatedBy(), qo.getCartId(), qo.getCreated())) {
				return Response.status(409).build();
			}
			qo.setStatus('W');
			qo.setDeadline(Helper.addDeadline(qo.getCreated()));
			qo = dao.persistAndReturn(qo);
			for (QuotationItem item : qo.getQuotationItems()) {
				item.setCreated(qo.getCreated());
				item.setStatus('W');
				item.setQuotationId(qo.getId());
				item.setCreatedBy(qo.getCreatedBy());
				item.setCartId(qo.getCartId());
				dao.persist(item);
			} // update quotation holder
			String sql = "update crt_cart set status = 'W' where id = " + qo.getCartId();
			dao.updateNative(sql);
			async.postQuotationCreation(qo, authHeader);
			return Response.status(200).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@Path("quotation-vendor-item/user")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public Response submitQuotationVendorItemUser(@HeaderParam("Authorization") String authHeader,
			QuotationVendorItem qvitem) {
		try {
			qvitem.setResponded(new Date());
			QuotationItem qi = dao.find(QuotationItem.class, qvitem.getQuotationItemId());
			qvitem.setItemDesc(qi.getItemDesc());
			qvitem.setItemNumber(qvitem.getItemNumber().trim().toUpperCase());
			Cart cart = dao.find(Cart.class, qvitem.getCartId());
			qvitem.setSalesPercentage(this.getPercentage(qvitem.getVendorId(), cart.getMakeId(), authHeader));
			if (qvitem.getQuantity() == 0) {
				qvitem.setStatus('N');// not available
			} else {
				qvitem.setStatus('C');// complete
			}
			qvitem.setProductId(this.findProductId(qvitem, authHeader));
			dao.update(qvitem);

			QuotationItemApproved approved = dao.findCondition(QuotationItemApproved.class, "vendorItemId",
					qvitem.getId());
			if (approved != null) {
				approved.setUnitCost(qvitem.getItemCostPrice());
				dao.update(approved);
			}

			if (qvitem.getStatus() == 'C') {
				this.unprocessOtherVendorItems(qvitem);
			}

			verifyVendorItemsCompletion(qvitem.getQuotationItemId());
			return Response.status(201).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	// idempotent
	@SecuredVendor
	@Path("quotation-vendor-item")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public Response submitQuotationVendorItem(@HeaderParam("Authorization") String authHeader,
			VendorItemContract contract) {
		try {
			QuotationVendorItem qvitem = dao.find(QuotationVendorItem.class, contract.getId());
			if (qvitem.getStatus() != 'W') {
				return Response.status(409).build();
			}
			qvitem.setResponded(new Date());
			qvitem.setQuantity(contract.getVendorQuantity());
			qvitem.setItemDesc(contract.getItemDesc());
			qvitem.setItemCostPrice(contract.getItemCostPrice());
			qvitem.setItemNumber(contract.getItemNumber().trim().toUpperCase());
			qvitem.setRespondedBy(contract.getRespondedBy());
			qvitem.setSalesPercentage(this.getPercentage(contract.getVendorId(), contract.getMakeId(), authHeader));
			if (qvitem.getQuantity() == 0) {
				qvitem.setStatus('N');// not available
			} else {
				qvitem.setStatus('C');// complete
			}
			qvitem.setProductId(findProductId(qvitem, authHeader));
			dao.update(qvitem);
			if (qvitem.getStatus() == 'C') {
				unprocessOtherVendorItems(qvitem);
			}
			verifyVendorItemsCompletion(qvitem.getQuotationItemId());
			return Response.status(200).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	private void unprocessOtherVendorItems(QuotationVendorItem qvi) {
		List<QuotationVendorItem> qvis = dao.getTwoConditions(QuotationVendorItem.class, "status", "quotationItemId",
				'W', qvi.getQuotationItemId());
		for (QuotationVendorItem qvitem : qvis) {
			qvitem.setStatus('N');
			qvitem.setQuantity(0);
			qvitem.setItemDesc(qvi.getItemDesc());
			qvitem.setSalesPercentage(qvi.getSalesPercentage());
			qvitem.setItemCostPrice(qvi.getItemCostPrice());
			qvitem.setItemNumber(qvi.getItemNumber());
			qvitem.setProductId(qvi.getProductId());
			qvitem.setRespondedBy(0);
			qvitem.setResponded(new Date());
			dao.update(qvitem);
		}
	}

	private Long findProductId(QuotationVendorItem qvi, String authHeader) {
		try {
			if (qvi.getStatus() == 'C') {
				Cart c = dao.find(Cart.class, qvi.getCartId());
				Response r = this.getSecuredRequest(
						AppConstants.getProductId(qvi.getItemNumber(), c.getMakeId(), qvi.getItemDesc()), authHeader);
				if (r.getStatus() == 200) {
					return r.readEntity(Long.class);
				} else {
					throw new Exception();
				}
			} else {
				throw new Exception();
			}
		} catch (Exception ex) {
			return null;
		}
	}

	private void verifyVendorItemsCompletion(long qitemId) {
		String jpql = "select count(b) from QuotationVendorItem b where b.quotationItemId = :value0";
		Long allVendorItems = dao.findJPQLParams(Long.class, jpql, qitemId);
		jpql = jpql + " and b.status in (:value1, :value2)";
		Long submittedVendorItems = dao.findJPQLParams(Long.class, jpql, qitemId, 'C', 'N');
		if (allVendorItems.equals(submittedVendorItems)) {
			QuotationItem qitem = dao.find(QuotationItem.class, qitemId);
			qitem.setStatus('C');
			dao.update(qitem);
			verifyQuotationItemsCompletion(qitem.getQuotationId());
		}
	}

	private void verifyQuotationItemsCompletion(long quotationId) {
		String jpql = "select count(b) from QuotationItem b where b.quotationId = :value0";
		Long allQuotationItems = dao.findJPQLParams(Long.class, jpql, quotationId);
		jpql = jpql + " and b.status = :value1";
		Long submittedQuotationItems = dao.findJPQLParams(Long.class, jpql, quotationId, 'C');
		if (allQuotationItems.equals(submittedQuotationItems)) {
			Quotation quotation = dao.find(Quotation.class, quotationId);
			quotation.setStatus('C');
			dao.update(quotation);
			verifyQuotationCompletion(quotation.getCartId());

		}
	}

	private void verifyQuotationCompletion(long cartId) {
		String jpql = "select count(b) from Quotation b where cartId = :value0";
		Long allQuotations = dao.findJPQLParams(Long.class, jpql, cartId);
		jpql = jpql + " and b.status = :value1";
		Long submittedQuotations = dao.findJPQLParams(Long.class, jpql, cartId, 'C');
		if (allQuotations.equals(submittedQuotations)) {
			Cart cart = dao.find(Cart.class, cartId);
			cart.setStatus('R');// ready for submission
			dao.update(cart);
		}
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> getVendor(int vendorId, String authHeader) throws Exception {
		Response r = this.getSecuredRequest(AppConstants.getVendor(vendorId), authHeader);
		if (r.getStatus() == 200) {
			Map<String, Object> map = r.readEntity(Map.class);
			return map;
		} else {
			throw new Exception();
		}
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> getUser(Integer userId, String authHeader) throws Exception {
		if (userId == null || userId == 0) {
			return null;
		}
		Response r = this.getSecuredRequest(AppConstants.getUser(userId), authHeader);
		if (r.getStatus() == 200) {
			Map<String, Object> map = r.readEntity(Map.class);
			return map;
		} else {
			throw new Exception();
		}
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> getVendorUser(Integer vendorUserId, String authHeader) throws Exception {
		if (vendorUserId == null || vendorUserId == 0) {
			return null;
		}
		Response r = this.getSecuredRequest(AppConstants.getVendorUser(vendorUserId), authHeader);
		if (r.getStatus() == 200) {
			Map<String, Object> map = r.readEntity(Map.class);
			return map;
		} else {
			throw new Exception();
		}
	}

	private double getPercentage(int vendorId, int makeId, String authHeader) throws Exception {
		Response r = this.getSecuredRequest(AppConstants.getVendorPercentage(vendorId, makeId), authHeader);
		if (r.getStatus() == 200) {
			Double d = r.readEntity(Double.class);
			return d;
		} else if (r.getStatus() == 404) {
			return 0.15;
		}
		throw new Exception();
	}

	@SecuredVendor
	@Path("waiting-vendor-quotation-items/vendor/{param}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getWaitingVendorQuotationItems(@HeaderParam("Authorization") String authHeader,
			@PathParam(value = "param") int vendorId) {
		try {
			List<QuotationVendorItem> vendorItems = dao.getTwoConditionsOrdered(QuotationVendorItem.class, "vendorId",
					"status", vendorId, 'W', "created", "asc");
			List<VendorItemContract> contracts = new ArrayList<>();
			for (QuotationVendorItem qvi : vendorItems) {
				Cart c = dao.find(Cart.class, qvi.getCartId());
				QuotationItem qi = dao.find(QuotationItem.class, qvi.getQuotationItemId());
				VendorItemContract contract = new VendorItemContract(qvi, c.getMakeId(), c.getVehicleYear(),
						qi.getQuantity(), c.getVin());
				contract.setModelYear(getModelYearObjectFromId(c.getVehicleYear(), authHeader));
				contract.setItemDesc(qi.getItemDesc());
				contracts.add(contract);
			}
			return Response.status(200).entity(contracts).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> getModelYearObjectFromId(int modelYearId, String authHeader) throws Exception {
		Response r = this.getSecuredRequest(AppConstants.getModelYear(modelYearId), authHeader);
		if (r.getStatus() == 200) {
			Map<String, Object> object = r.readEntity(Map.class);
			return object;
		} else
			throw new Exception();
	}

	@SecuredUser
	@GET
	@Path("quotation-finalized-items/cart/{param}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFinalizedQuotationItems(@HeaderParam("Authorization") String authHeader,
			@PathParam(value = "param") long cartId) {
		try {
			List<FinalizedItem> finalizedItems = new ArrayList<>();
			List<QuotationItem> quotations = dao.getCondition(QuotationItem.class, "cartId", cartId);
			for (QuotationItem qitem : quotations) {
				FinalizedItem finalized = new FinalizedItem();
				finalized.setCartId(cartId);
				finalized.setItemDesc(qitem.getItemDesc());
				finalized.setQuotationId(qitem.getQuotationId());
				finalized.setQuotationItemId(qitem.getId());
				finalized.setRequestedQuantity(qitem.getQuantity());
				finalized.setResponses(new ArrayList<>());
				finalized = prepareResponsesSelected(finalized, qitem.getQuantity(), qitem.getId(), authHeader);
				finalizedItems.add(finalized);
			}
			return Response.status(200).entity(finalizedItems).build();
		} catch (Exception ex) {
			ex.printStackTrace();
			return Response.status(500).build();
		}
	}

	// idempotent
	// try to make it async
	@SecuredUser
	@POST
	@Path("approve-quotation")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response approveQuotation(@HeaderParam("Authorization") String authHeader, FinalizedItemsHolder holder) {
		try {
			async.approveQuotation(authHeader, holder);
			return Response.status(200).build();

		} catch (Exception ex) {
			return Response.status(500).build();
		}

	}

	@SecuredUser
	@GET
	@Path("postponed-sales")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPostponedSales(@HeaderParam("Authorization") String authHeader) {
		try {
			String jpql = "select b from Cart b where b.status = :value0 and b.id in ("
					+ "select c.cartId from CartReview c where c.status = :value1) order by b.id";
			List<Cart> carts = dao.getJPQLParams(Cart.class, jpql, 'S', 'P');
			for (Cart cart : carts) {
				prepareCart(cart, authHeader);// cart items, customer, model year
			}
			return Response.status(200).entity(carts).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}

	}

	@SecuredUser
	@GET
	@Path("search-cart/cart/{cartId}/customer-id/{customerId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSeachCarts(@HeaderParam("Authorization") String authHeader,
			@PathParam(value = "cartId") long cartId, @PathParam(value = "customerId") long customerId) {
		try {
			String sql = "select * from crt_cart where id > 0";
			if (cartId > 0) {
				sql += " and id = " + cartId;
			}
			if (customerId > 0) {
				sql += " and customer_id = " + customerId;
			}
			sql += "order by id";
			List<Cart> carts = dao.getNative(Cart.class, sql);
			for (Cart cart : carts) {
				this.prepareCart(cart, authHeader);
				String jpql2 = "select b from Cart b where b.status = :value0 and b.id = :value1 and b.id in ("
						+ "select c.cartId" + "  from CartReview c"
						+ "  where c.id IN (SELECT MAX(d.id) FROM CartReview d GROUP BY d.cartId) and c.status = :value2"
						+ ") order by b.created";
				List<Cart> closed = dao.getJPQLParams(Cart.class, jpql2, 'S', cart.getId(), 'C');
				if (!closed.isEmpty()) {
					cart.setStatus('O');
				}
			}
			return Response.status(200).entity(carts).build();

		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@GET
	@Path("lost-sales/year/{year}/month/{month}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getLostSales(@HeaderParam("Authorization") String authHeader, @PathParam(value = "year") int year,
			@PathParam(value = "month") int month) {
		try {
			// init from and to
			Date from = new Date();
			Date to = new Date();
			if (month == 12) {
				Calendar cFrom = new GregorianCalendar();
				cFrom.set(year, 0, 1, 0, 0, 0);
				cFrom.set(Calendar.MILLISECOND, 0);
				from.setTime(cFrom.getTimeInMillis());

				Calendar cTo = new GregorianCalendar();
				cTo.set(year, 11, 31, 0, 0, 0);
				cTo.set(Calendar.HOUR_OF_DAY, 23);
				cTo.set(Calendar.MINUTE, 59);
				cTo.set(Calendar.SECOND, 59);
				cTo.set(Calendar.MILLISECOND, cTo.getActualMaximum(Calendar.MILLISECOND));
				to.setTime(cTo.getTimeInMillis());
			} else {
				Calendar cFrom = new GregorianCalendar();
				cFrom.set(year, month, 1, 0, 0, 0);
				cFrom.set(Calendar.MILLISECOND, 0);
				from.setTime(cFrom.getTimeInMillis());

				Calendar cTo = new GregorianCalendar();
				cTo.set(year, month, 1, 0, 0, 0);
				cTo.set(Calendar.DAY_OF_MONTH, cTo.getActualMaximum(Calendar.DAY_OF_MONTH));
				cTo.set(Calendar.HOUR_OF_DAY, 23);
				cTo.set(Calendar.MINUTE, 59);
				cTo.set(Calendar.SECOND, 59);
				cTo.set(Calendar.MILLISECOND, cTo.getActualMaximum(Calendar.MILLISECOND));
				to.setTime(cTo.getTimeInMillis());
			}
			//

			List<Cart> carts = new ArrayList<>();
			String jpql = "select b from Cart b where b.status = :value0 and b.created between :value1 and :value2";
			List<Cart> archived = dao.getJPQLParams(Cart.class, jpql, 'X', from, to);
			for (Cart cart : archived) {
				// prepareCart(cart, authHeader);// cart items, customer, model year
				List<CartReview> reviews = dao.getCondition(CartReview.class, "cartId", cart.getId());
				cart.setReviews(reviews);
				carts.add(cart);

			}
			// get closed carts
			String jpql2 = "select b from Cart b where b.status = :value0 and b.created between :value1 and :value2 and b.id in ("
					+ "select c.cartId" + "  from CartReview c"
					+ "  where c.id IN (SELECT MAX(d.id) FROM CartReview d GROUP BY d.cartId) and c.status = :value3"
					+ ") order by b.created";
			List<Cart> closed = dao.getJPQLParams(Cart.class, jpql2, 'S', from, to, 'C');
			for (Cart cart : closed) {
				// prepareCart(cart, authHeader);// cart items, customer, model year
				List<CartReview> reviews = dao.getCondition(CartReview.class, "cartId", cart.getId());
				cart.setReviews(reviews);
				initQuotationApprovedItems(cart);
				carts.add(cart);
			}

			Collections.sort(carts, new Comparator<Cart>() {
				@Override
				public int compare(Cart o1, Cart o2) {
					return o1.getCreated().compareTo(o2.getCreated());
				}
			});

			return Response.status(200).entity(carts).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	private void initQuotationApprovedItems(Cart cart) {
		String jpql2 = "select b from QuotationItem b where b.cartId = :value0 and b.id in ("
				+ "select c.quotationItemId from QuotationItemApproved c where c.cartId = :value0)";
		List<QuotationItem> qitems = dao.getJPQLParams(QuotationItem.class, jpql2, cart.getId());
		List<ApprovedItem> contracts = new ArrayList<>();
		for (QuotationItem qitem : qitems) {
			List<QuotationItemApproved> approvedItems = dao.getCondition(QuotationItemApproved.class, "quotationItemId",
					qitem.getId());
			ApprovedItem contract = new ApprovedItem();
			contract.setCartId(cart.getId());
			contract.setItemDesc(qitem.getItemDesc());
			contract.setQuotationItemId(qitem.getId());
			int quantity = 0;
			double total = 0;
			String number = "";
			for (QuotationItemApproved approved : approvedItems) {
				QuotationVendorItem qvi = dao.find(QuotationVendorItem.class, approved.getVendorItemId());
				number = qvi.getItemNumber();
				quantity += approved.getQuantity();
				total += (approved.getUnitCost() + (approved.getUnitCost() * approved.getPercentage())) * quantity;
			}
			contract.setQuantity(quantity);
			contract.setItemNumber(number);
			contract.setUnitSales(total / quantity);
			contracts.add(contract);
		}
		cart.setApprovedItems(contracts);

	}

	@SecuredUser
	@GET
	@Path("completed-cart/cart/{param}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCompletedCartReport(@HeaderParam("Authorization") String authHeader,
			@PathParam(value = "param") long cartId) {
		try {
			CompletedCart completedCart = new CompletedCart();
			Cart cart = dao.find(Cart.class, cartId);
			PartsOrder partsOrder = dao.findCondition(PartsOrder.class, "cartId", cartId);
			prepareCart(cart, authHeader);
			partsOrder.setPartsItems(dao.getCondition(PartsOrderItem.class, "cartId", cartId));
			List<PartsOrderItemReturn> returns = dao.getCondition(PartsOrderItemReturn.class, "cartId", cartId);
			double returnTotal = 0;
			for (PartsOrderItemReturn r : returns) {
				double returnAmount = r.getReturnAmount() * r.getReturnQuantity();
				double shipmentAmount = r.getShipmentCost() + r.getShipmentCost() * cart.getVatPercentage();
				double returnCost = r.getCostPrice() * r.getReturnQuantity();
				returnTotal = returnTotal + returnAmount + shipmentAmount - returnCost;
			}

			completedCart.setPartsReturnTotal(returnTotal);
			completedCart.setCart(cart);
			completedCart.setPartsOrder(partsOrder);
			return Response.status(200).entity(completedCart).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@SecuredVendor
	@PUT
	@Path("vendor-prepare-item")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response vendorPrepareItem(PartCollectionItem collection) {
		try {
			PartsOrderItemApproved approved = dao.find(PartsOrderItemApproved.class, collection.getPartsApprovedId());
			approved.setPrepared(new Date());
			approved.setPreparedBy(collection.getPreparedBy());
			approved.setStatus('V');
			dao.update(approved);
			return Response.status(201).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@PUT
	@Path("receive-item")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response receiveItem(PartCollectionItem collection) {
		try {
			PartsOrderItemApproved approved = dao.find(PartsOrderItemApproved.class, collection.getPartsApprovedId());
			approved.setReceived(new Date());
			approved.setReceivedBy(collection.getReceivedBy());
			approved.setStatus('R');
			dao.update(approved);
			verifyReceiveCompletion(approved);
			return Response.status(201).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@PUT
	@Path("collect-item")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response collectItem(PartCollectionItem collection) {
		try {
			PartsOrderItemApproved approved = dao.find(PartsOrderItemApproved.class, collection.getPartsApprovedId());
			approved.setCollected(new Date());
			approved.setCollectedBy(collection.getCollectedBy());
			approved.setStatus('C');
			approved.setItemNumber(collection.getItemNumber().trim().toUpperCase());
			dao.update(approved);
			verifyCollectionCompletion(approved);
			return Response.status(201).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	private void verifyReceiveCompletion(PartsOrderItemApproved approved) throws Exception {
		String jpql = "select count(b) from PartsOrderItemApproved b where b.cartId = :value0";
		Long allApprovedItems = dao.findJPQLParams(Long.class, jpql, approved.getCartId());
		jpql = jpql + " and b.status in (:value1)";
		Long collectedItems = dao.findJPQLParams(Long.class, jpql, approved.getCartId(), 'R');// recieved
		if (allApprovedItems.equals(collectedItems)) {
			Cart cart = dao.find(Cart.class, approved.getCartId());
			cart.setStatus('E');// cart ready fro shipment
			dao.update(cart);
			// update parts order
			PartsOrder po = dao.find(PartsOrder.class, approved.getPartsOrderId());
			po.setStatus('R');// parts ready fir shipment
			dao.update(po);
			List<PartsOrderItem> items = dao.getCondition(PartsOrderItem.class, "partsOrderId", po.getId());
			for (PartsOrderItem item : items) {
				item.setStatus('R');// part item receieved R = received
				dao.update(item);
			}

		}
	}

	@SecuredUser
	@PUT
	@Path("ship-items")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response shipItems(@HeaderParam("Authorization") String authHeader, Map<String, String> map) {
		try {
			final Long cartId = Long.parseLong(map.get("cartId"));
			final Double shipmentCost = Double.parseDouble(map.get("shipmentCost"));
			final Integer shippedBy = Integer.parseInt(map.get("shippedBy"));
			final String shipmentReference = map.get("shipmentReference");
			final String courrier = map.get("courrier");
			final Date date = new Date();
			// update cart
			Cart c = dao.find(Cart.class, cartId);
			c.setStatus('H');
			dao.update(c);
			// update part order
			PartsOrder partOrder = dao.findCondition(PartsOrder.class, "cartId", cartId);
			partOrder.setStatus('S');
			partOrder.setShipmentCost(shipmentCost);
			partOrder.setShipmentReference(shipmentReference);
			partOrder.setShipped(date);
			partOrder.setShippedBy(shippedBy);
			partOrder.setCourrierName(courrier);
			dao.update(partOrder);
			// update part approved items
			List<PartsOrderItemApproved> approvedList = dao.getCondition(PartsOrderItemApproved.class, "cartId",
					cartId);
			for (PartsOrderItemApproved approved : approvedList) {
				approved.setStatus('S');
				approved.setShipped(date);
				approved.setShipmentId(shipmentReference);
				approved.setShippedBy(shippedBy);
				dao.update(approved);
			}
			// update part Items
			List<PartsOrderItem> partsItems = dao.getCondition(PartsOrderItem.class, "cartId", cartId);
			for (PartsOrderItem item : partsItems) {
				item.setStatus('S');
				dao.update(item);
			}

			String text = "تم شحن القطع الى عنوانك للطلب رقم ";
			text += cartId;
			if (partOrder.getCourrierName().equals("QETAA")) {

			} else {
				text += " رقم التتبع ";
				text += partOrder.getShipmentReference();
				text += " على الناقل ";
				text += partOrder.getCourrierName();
				if (partOrder.getCourrierName().equals("SMSA")) {
					text += " , رابط التتبع: ";
					text += "goo.gl/6dxL1A ";
				} else if (partOrder.getCourrierName().equals("Fetchr")) {
					text += " , رابط التتبع: ";
					text += "goo.gl/4yvsdw ";
				} else if (partOrder.getCourrierName().equals("Zajil")) {
					text += " , رابط التتبع: ";
					text += "goo.gl/sTWS6Y ";
				}
			}
			text += " شكرا لكم, نتمنى أن نكون عند حسن ظنكم";
			// this.sendSms(c.getCustomerId(), cartId, text, "shipped", authHeader);
			return Response.status(201).build();
		} catch (Exception ex) {
			ex.printStackTrace();
			return Response.status(500).build();
		}
	}

	private void verifyCollectionCompletion(PartsOrderItemApproved approved) throws Exception {
		String jpql = "select count(b) from PartsOrderItemApproved b where b.cartId = :value0";
		Long allApprovedItems = dao.findJPQLParams(Long.class, jpql, approved.getCartId());
		jpql = jpql + " and b.status in (:value1, :value2)";
		Long collectedItems = dao.findJPQLParams(Long.class, jpql, approved.getCartId(), 'C', 'R');// collected, or
																									// recieved
		if (allApprovedItems.equals(collectedItems)) {
			Cart cart = dao.find(Cart.class, approved.getCartId());
			cart.setStatus('V');
			dao.update(cart);
			// update parts order
			PartsOrder po = dao.find(PartsOrder.class, approved.getPartsOrderId());
			po.setStatus('C');
			dao.update(po);
			List<PartsOrderItem> items = dao.getCondition(PartsOrderItem.class, "partsOrderId", po.getId());
			for (PartsOrderItem item : items) {
				item.setStatus('C');
				dao.update(item);
			}

		}
	}

	@SecuredUser
	@GET
	@Path("quotation-finalized-items-full/cart/{param}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFinalizedQuotationItemsFull(@HeaderParam("Authorization") String authHeader,
			@PathParam(value = "param") long cartId) {
		try {
			List<FinalizedItem> finalizedItems = new ArrayList<>();
			List<QuotationItem> quotations = dao.getCondition(QuotationItem.class, "cartId", cartId);
			for (QuotationItem qitem : quotations) {
				FinalizedItem finalized = new FinalizedItem();
				finalized.setCartId(cartId);
				finalized.setItemDesc(qitem.getItemDesc());
				finalized.setQuotationId(qitem.getQuotationId());
				finalized.setQuotationItemId(qitem.getId());
				finalized.setRequestedQuantity(qitem.getQuantity());
				finalized.setResponses(new ArrayList<>());
				finalized = prepareResponseFull(finalized, qitem.getId(), authHeader);
				finalizedItems.add(finalized);
			}
			return Response.status(200).entity(finalizedItems).build();
		} catch (Exception ex) {
			ex.printStackTrace();
			return Response.status(500).build();
		}
	}

	private FinalizedItem prepareResponseFull(FinalizedItem finalized, long qitemId, String authHeader)
			throws Exception {
		List<QuotationVendorItem> vendorItems = dao.getConditionOrderedByTwoColumns(QuotationVendorItem.class,
				"quotationItemId", qitemId, "itemCostPrice", "responded", "ASC");
		for (QuotationVendorItem vitem : vendorItems) {
			FinalizedItemResponse response = new FinalizedItemResponse();
			response.setPartNumber(vitem.getItemNumber());
			response.setQuotationVendorItemId(vitem.getId());
			response.setSalesPercentage(vitem.getSalesPercentage());
			response.setSubmittedQuantity(vitem.getQuantity());
			response.setUnitCost(vitem.getItemCostPrice() == null ? 0 : vitem.getItemCostPrice());
			response.setVendor(getVendor(vitem.getVendorId(), authHeader));
			// set vendor
			response.setSelectedQuantity(vitem.getQuantity());
			finalized.getResponses().add(response);
		}
		finalized.setStatus('A');// nothing, for full items! there is no status
		return finalized;
	}

	private FinalizedItem prepareResponsesSelected(FinalizedItem finalized, int requested, long qitemId,
			String authHeader) throws Exception {
		List<QuotationVendorItem> vendorItems = dao.getConditionOrderedByTwoColumns(QuotationVendorItem.class,
				"quotationItemId", qitemId, "itemCostPrice", "responded", "ASC");
		int index = 0;
		while (requested > 0) {
			if (index < vendorItems.size()) {
				QuotationVendorItem vitem = vendorItems.get(index);
				FinalizedItemResponse response = new FinalizedItemResponse();
				response.setPartNumber(vitem.getItemNumber());
				response.setQuotationVendorItemId(vitem.getId());
				response.setSalesPercentage(vitem.getSalesPercentage());
				response.setSubmittedQuantity(vitem.getQuantity());
				response.setUnitCost(vitem.getQuantity() == 0 ? 0 : vitem.getItemCostPrice());
				response.setVendor(getVendor(vitem.getVendorId(), authHeader));
				// set vendor
				response.setSelectedQuantity(vitem.getQuantity());
				if (vitem.getQuantity() > requested) {
					response.setSelectedQuantity(requested);
				}
				requested = requested - response.getSelectedQuantity();
				index++;
				if (response.getSelectedQuantity() > 0) {
					finalized.getResponses().add(response);
				}
			} else {
				break;
			}
		}
		if (finalized.getResponses().isEmpty()) {
			finalized.setStatus('N');// No responses
		} else if (requested == 0) {
			finalized.setStatus('F');// item fulfilled
		} else if (requested > 0) {
			finalized.setStatus('M');// not all quantity fulfilled
		}
		return finalized;
	}

	@SecuredCustomer
	@GET
	@Path("quotation-cart/{cartid}/customer/{customerid}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getQuotationCart(@PathParam(value = "cartid") long cartId,
			@PathParam(value = "customerid") long customerId) {
		try {
			String jpql = "select b from Cart b where b.id = :value0 and b.customerId = :value1 and b.status in (:value2, :value3, :value4, :value5, :value6, :value7) order by b.id";
			Cart cart = dao.findJPQLParams(Cart.class, jpql, cartId, customerId, 'N', 'Q', 'W', 'R', 'A', 'S');
			List<CartItem> cartItems = dao.getCondition(CartItem.class, "cartId", cartId);
			cart.setCartItems(cartItems);
			return Response.status(200).entity(cart).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@PUT
	@Path("parts-approved-item")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updatePartsApprovedItem(PartsOrderItemApproved approved) {
		try {
			approved.getInvReference().trim();
			approved.getItemNumber().trim().toUpperCase();
			approved.setActualVendorId(approved.getVendorId());
			dao.update(approved);
			return Response.status(201).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@PUT
	@Path("parts-approved-items")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updatePartsApprovedItems(List<PartsOrderItemApproved> approvedItems) {
		try {
			long cartId = approvedItems.get(0).getCartId();
			for (PartsOrderItemApproved approved : approvedItems) {
				approved.getInvReference().trim();
				approved.getItemNumber().trim().toUpperCase();
				dao.update(approved);
			}
			//
			PartsOrder po = dao.findCondition(PartsOrder.class, "cartId", cartId);
			List<PartsOrderItemApproved> cartApproved = dao.getCondition(PartsOrderItemApproved.class, "cartId",
					cartId);
			double totalCost = 0;
			for (PartsOrderItemApproved approved : cartApproved) {
				totalCost = totalCost + (approved.getApprovedQuantity() * approved.getCostPrice());
			}
			po.setCostAmount(totalCost);
			dao.update(po);
			return Response.status(201).build();
		} catch (Exception ex) {
			ex.printStackTrace();
			return Response.status(500).build();
		}
	}

	@Secured
	@GET
	@Path("full-parts-approved-items/cart/{cart}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFullCartPartsApprovedItems(@PathParam(value = "cart") long cartId) {
		try {
			String jpql = "select b from PartsOrderItemApproved b where b.cartId = :value0 and b.approvedQuantity > :value1";
			List<PartsOrderItemApproved> approvedList = dao.getJPQLParams(PartsOrderItemApproved.class, jpql, cartId,
					0);
			for (PartsOrderItemApproved approved : approvedList) {
				String jpql2 = "select b.itemDesc from QuotationItem b where id = ("
						+ "select c.quotationItemId from QuotationItemApproved c where c.id = :value0)";

				String itemDesc = dao.findJPQLParams(String.class, jpql2, approved.getQuotationItemApprovedId());
				approved.setItemDesc(itemDesc);
			}
			return Response.status(200).entity(approvedList).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@Secured
	@GET
	@Path("full-parts-approved-items/cart/{cart}/vendor/{vendor}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFullCartPartsApprovedItems(@PathParam(value = "cart") long cartId,
			@PathParam(value = "vendor") int vendorId) {
		try {
			String jpql = "select b from PartsOrderItemApproved b where b.cartId = :value0 and b.approvedQuantity > :value1 and vendorId = :value2";
			List<PartsOrderItemApproved> approvedList = dao.getJPQLParams(PartsOrderItemApproved.class, jpql, cartId, 0,
					vendorId);
			for (PartsOrderItemApproved approved : approvedList) {
				String jpql2 = "select b.itemDesc from QuotationItem b where id = ("
						+ "select c.quotationItemId from QuotationItemApproved c where c.id = :value0)";

				String itemDesc = dao.findJPQLParams(String.class, jpql2, approved.getQuotationItemApprovedId());
				approved.setItemDesc(itemDesc);
			}
			return Response.status(200).entity(approvedList).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@Secured
	@GET
	@Path("customer-parts-approved-items/cart/{cart}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCartPartsApprovedItems(@PathParam(value = "cart") long cartId) {
		try {
			List<PartsOrderItem> pitems = dao.getCondition(PartsOrderItem.class, "cartId", cartId);
			List<ApprovedItem> contracts = new ArrayList<>();
			for (PartsOrderItem pitem : pitems) {
				ApprovedItem contract = new ApprovedItem();
				contract.setCartId(cartId);
				QuotationItem qitem = dao.find(QuotationItem.class, pitem.getQuotationItemId());
				contract.setItemDesc(qitem.getItemDesc());
				contract.setQuotationItemId(qitem.getId());
				contract.setQuantity(pitem.getOrderedQuantity());
				contract.setUnitSales(pitem.getSalesPrice());
				contracts.add(contract);
			}
			return Response.status(200).entity(contracts).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@Secured
	@GET
	@Path("customer-approved-items/cart/{cart}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCartApprovedItems(@PathParam(value = "cart") long cartId) {
		try {
			String jpql = "select b from QuotationItem b where b.cartId = :value0 and b.id in ("
					+ "select c.quotationItemId from QuotationItemApproved c where c.cartId = :value0)";
			List<QuotationItem> qitems = dao.getJPQLParams(QuotationItem.class, jpql, cartId);
			List<ApprovedItem> contracts = new ArrayList<>();
			for (QuotationItem qitem : qitems) {
				List<QuotationItemApproved> approvedItems = dao.getCondition(QuotationItemApproved.class,
						"quotationItemId", qitem.getId());
				ApprovedItem contract = new ApprovedItem();
				contract.setCartId(cartId);
				contract.setItemDesc(qitem.getItemDesc());
				contract.setQuotationItemId(qitem.getId());
				int quantity = 0;
				double total = 0;
				String number = "";
				for (QuotationItemApproved approved : approvedItems) {
					QuotationVendorItem qvi = dao.find(QuotationVendorItem.class, approved.getVendorItemId());
					number = qvi.getItemNumber();
					quantity += approved.getQuantity();
					total += (approved.getUnitCost() + (approved.getUnitCost() * approved.getPercentage())) * quantity;
				}
				contract.setQuantity(quantity);
				contract.setItemNumber(number);
				contract.setUnitSales(total / quantity);
				contracts.add(contract);
			}
			return Response.status(200).entity(contracts).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@POST
	@Path("cart-ids/filter-by-customers")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCartIdsByCustomers(List<Long> customerIds) {
		try {
			String sql = "select * from crt_cart where customer_id in (0 ";
			for (Long cid : customerIds) {
				sql = sql + "," + cid;
			}
			sql = sql + ")";
			List<Cart> carts = dao.getNative(Cart.class, sql);
			List<Long> cartIds = new ArrayList<>();
			for (Cart c : carts) {
				cartIds.add(c.getId());
			}
			return Response.status(200).entity(cartIds).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@GET
	@Path("cart/customer/{customerId}/except/{cartId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllCustomerCarts(@PathParam(value = "customerId") long customerId,
			@PathParam(value = "cartId") long cartId) {
		try {
			String jpql = "select b from Cart b where customerId = :value0 and id not in (:value1)";
			List<Cart> carts = dao.getJPQLParams(Cart.class, jpql, customerId, cartId);
			return Response.status(200).entity(carts).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@SecuredCustomer
	@GET
	@Path("customer-past-carts/customer/{customer}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getArchivedCarts(@HeaderParam("Authorization") String authHeader,
			@PathParam(value = "customer") long customerId) {
		try {
			String jpql = "select b from Cart b where b.customerId = :value0 and b.status in (:value1, :value2, :value3, :value4, :value5, :value6) order by b.id desc";
			List<Cart> carts = dao.getJPQLParams(Cart.class, jpql, customerId, 'P', 'V', 'E', 'H', 'F', 'X'); //
			for (Cart cart : carts) {
				List<CartItem> cartItems = dao.getCondition(CartItem.class, "cartId", cart.getId());
				cart.setCartItems(cartItems);
				cart.setModelYear(this.getModelYearObjectFromId(cart.getVehicleYear(), authHeader));
			}
			return Response.status(200).entity(carts).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@SecuredCustomer
	@GET
	@Path("customer-quotation-carts/customer/{customer}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getActiveQuotationCarts(@HeaderParam("Authorization") String authHeader,
			@PathParam(value = "customer") long customerId) {
		try {
			String jpql = "select b from Cart b where b.customerId = :value0 and b.status in (:value1, :value2, :value3, :value4, :value5, :value6, :value7) order by b.id";
			List<Cart> carts = dao.getJPQLParams(Cart.class, jpql, customerId, 'N', 'Q', 'W', 'R', 'S', 'A', 'T'); //
			for (Cart c : carts) {
				List<CartItem> cartItems = dao.getCondition(CartItem.class, "cartId", c.getId());
				c.setCartItems(cartItems);
				c.setModelYear(this.getModelYearObjectFromId(c.getVehicleYear(), authHeader));
			}
			return Response.status(200).entity(carts).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@SecuredCustomer
	@POST
	@Path("parts-order")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response submitPartsOrder(@HeaderParam("Authorization") String authHeader, PartsOrder partsOrder) {
		try {
			async.createPartsOrder(partsOrder, authHeader);
			return Response.status(201).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@GET
	@Path("parts-order/cart/{cartId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response submitPartsOrder(@PathParam(value = "cartId") long cartId) {
		try {
			PartsOrder po = dao.findCondition(PartsOrder.class, "cartId", cartId);
			return Response.status(200).entity(po).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@GET
	@Path("followups-notification")
	public Response getActiveFollowupsNotification() {
		try {
			String jpql = "select count(b) from Cart b where b.status = :value0 and (b.id not in ("
					+ "select c.cartId from CartReview c where c.status = :value1 or c.status = :value2)"
					+ "or b.id in ("
					+ "select c.cartId from CartReview c where c.status = :value2 and c.reminderDate <= :value3)"
					+ "or b.id in (" + "select e.cartId from CartReview e where e.status = :value4))";
			Long carts = dao.findJPQLParams(Long.class, jpql, 'S', 'C', 'P', new Date(), 'A');
			return Response.status(200).entity(carts).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@GET
	@Path("no-answer-followups")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getNoAnswerFollowUps(@HeaderParam("Authorization") String authHeader) {
		try {
			String jpql = "select b from Cart b where b.status = :value0 and b.id in ("
					+ "select c.cartId from CartReview c where c.status = :value1 and c.actionValue = :value2)";
			List<Cart> carts = dao.getJPQLParams(Cart.class, jpql, 'S', 'A', 'H');
			for (Cart cart : carts) {
				this.prepareCart(cart, authHeader);
			}
			return Response.status(200).entity(carts).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}

	}

	@SecuredUser
	@GET
	@Path("active-followups")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getActiveFollowups(@HeaderParam("Authorization") String authHeader) {
		try {
			String jpql = "select b from Cart b where b.status = :value0 and (b.id not in ("
					+ "select c.cartId from CartReview c where c.status = :value1 or c.status = :value2)"
					+ "or b.id in ("
					+ "select d.cartId from CartReview d where d.status = :value2 and d.reminderDate <= :value3)"
					+ "or b.id in (" + "select e.cartId from CartReview e where e.status = :value4) "
					+ "and  b.id not in ("
					+ "select f.cartId from CartReview f where f.actionValue = :value5 and f.status = :value4)"
					+ ") order by b.submitted";
			List<Cart> carts = dao.getJPQLParams(Cart.class, jpql, 'S', 'C', 'P', new Date(), 'A', 'H');
			for (Cart cart : carts) {
				List<CartReview> reviews = dao.getTwoConditionsOrdered(CartReview.class, "cartId", "stage",
						cart.getId(), 3, "created", "asc");
				cart.setReviews(reviews);
				initQuotationApprovedItems(cart);
			}
			return Response.status(200).entity(carts).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@GET
	@Path("active-followup/{cart}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getActiveFollowup(@PathParam(value = "cart") long cartId) {
		try {
			String jpql = "select b from Cart b where b.status = :value0 and (b.id = :value1 and b.id not in ("
					+ "select c.cartId from CartReview c where c.status = :value2 or c.status = :value3)"
					+ "or b.id in ("
					+ "select c.cartId from CartReview c where c.status = :value3 and c.reminderDate <= :value4)"
					+ "or b.id in ("
					+ "select e.cartId from CartReview e where e.status = :value5)) and b.id = :value1 order by b.id";
			Cart cart = dao.findJPQLParams(Cart.class, jpql, 'S', cartId, 'C', 'P', new Date(), 'A');
			return Response.status(200).entity(cart).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@GET
	@Path("wire-transfer/cart/{param}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getActiveWireTransfer(@HeaderParam("Authorization") String authHeader,
			@PathParam(value = "param") long cartId) {
		try {
			WireTransfer wire = dao.findTwoConditions(WireTransfer.class, "status", "cartId", 'W', cartId);
			wire.setCustomer(this.getCustomer(wire.getCustomerId(), authHeader));
			Cart cart = dao.find(Cart.class, wire.getCartId());
			wire.setCart(cart);
			wire.getCart().setModelYear(getModelYearObjectFromId(cart.getVehicleYear(), authHeader));
			List<CartReview> reviews = dao.getCondition(CartReview.class, "cartId", cartId);
			wire.getCart().setReviews(reviews);
			return Response.status(200).entity(wire).build();
		} catch (Exception ex) {
			ex.printStackTrace();
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@GET
	@Path("active-wire-transfers")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getActiveWireTransfers(@HeaderParam("Authorization") String authHeader) {
		try {
			List<WireTransfer> wires = dao.getConditionOrdered(WireTransfer.class, "status", 'W', "created", "asc");
			for (WireTransfer wire : wires) {
				wire.setCustomer(this.getCustomer(wire.getCustomerId(), authHeader));
				Cart cart = dao.find(Cart.class, wire.getCartId());
				List<CartReview> reviews = dao.getTwoConditionsOrdered(CartReview.class, "cartId", "stage",
						cart.getId(), 3, "created", "asc");
				cart.setReviews(reviews);
				wire.setCart(cart);
			}
			return Response.status(200).entity(wires).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@PUT
	@Path("undo-wire-transfer")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response undoWireTransfer(WireTransfer wire, @HeaderParam("Authorization") String authHeader) {
		try {
			Cart c = dao.find(Cart.class, wire.getCartId());
			int userId = wire.getConfirmedBy();
			// delete wire transfer
			dao.delete(wire);
			c.setStatus('S');
			dao.update(c);
			// delete parts order
			PartsOrder po = dao.findCondition(PartsOrder.class, "cartId", c.getId());
			dao.delete(po);
			// delete parts items
			String sql = "delete from crt_parts_item where cart_id = " + c.getId();
			dao.updateNative(sql);
			sql = "delete from crt_parts_item_approved where cart_id = " + c.getId();
			// delete items approved
			dao.updateNative(sql);
			// add review
			sql = "update crt_review set status = 'C' where cart_id = " + c.getId();
			dao.updateNative(sql);
			CartReview cr = new CartReview();
			cr.setActionValue('B');
			cr.setStage(4);
			cr.setCartId(c.getId());
			cr.setCreated(new Date());
			cr.setReviewerId(userId);
			cr.setReviewText("Transfer Cancelled");
			cr.setStatus('A');
			dao.persist(cr);
			// remove purchase item
			deleteSecuredRequest(AppConstants.deletePurchaseOrder(c.getId()), authHeader);
			return Response.status(201).build();

		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@PUT
	@Path("wire-transfer/confirm")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response confirmWireTransfer(@HeaderParam("Authorization") String authHeader, Map<String, Object> map) {
		try {
			Long cartId = ((Number) map.get("cartId")).longValue();
			Long wireId = ((Number) map.get("wireId")).longValue();
			Integer confirmedBy = (Integer) map.get("confirmedBy");
			
			WireTransfer wt = dao.find(WireTransfer.class, wireId);
			wt.setStatus('P');
			wt.setConfirmed(new Date());
			wt.setConfirmedBy(confirmedBy);
			dao.update(wt);
			Cart c = dao.find(Cart.class, cartId);
			c.setStatus('P');
			dao.update(c);
			String text = "تم استلام المبلغ بنجاح للطلب رقم ";
			text += c.getId();
			text += " نعمل الان على شحن القطع";
			async.sendSms(c.getCustomerId(), c.getId(), text, "part-paid", authHeader);
			return Response.status(201).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@PUT
	@Path("confirm-wire-transfer")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response confirmWireTransfer(WireTransfer wire, @HeaderParam("Authorization") String authHeader) {
		try {
			wire.setStatus('P');// confirmed payment
			wire.setConfirmed(new Date());
			dao.update(wire);
			Cart c = dao.find(Cart.class, wire.getCartId());
			c.setStatus('P');
			dao.update(c);
			String text = "تم استلام المبلغ بنجاح للطلب رقم ";
			text += c.getId();
			text += " نعمل الان على شحن القطع";
			async.sendSms(c.getCustomerId(), c.getId(), text, "part-paid", authHeader);
			return Response.status(200).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@GET
	@Path("unassigned-carts")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUnassignedCarts(@HeaderParam("Authorization") String authHeader) {
		try {
			String jpql = "select b from Cart b where b.status in (:value0, :value1, :value2 , :value3, :value4) and b.id not in ("
					+ "select a.cartId from CartAssignment a where a.status = :value5) order by b.id";
			List<Cart> carts = dao.getJPQLParams(Cart.class, jpql, 'N', 'Q', 'W', 'R', 'A', 'A');
			for (Cart cart : carts) {
				cart.setCustomer(this.getCustomer(cart.getCustomerId(), authHeader));
				cart.setModelYear(this.getModelYearObjectFromId(cart.getVehicleYear(), authHeader));
			}
			return Response.status(200).entity(carts).build();
		} catch (Exception ex) {
			ex.printStackTrace();
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@GET
	@Path("advisor-quotation-carts")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAdvisorQuotationCarts(@HeaderParam("Authorization") String authHeader) {
		try {
			String jpql = "select distinct b.assignedTo from CartAssignment b where b.status = :value0 and b.cartId in ("
					+ "select c.id from Cart c where c.status in (:value1, :value2, :value3, :value4, :value5))";
			List<Integer> userIds = dao.getJPQLParams(Integer.class, jpql, 'A', 'N', 'Q', 'W', 'R', 'A');
			// get active advisors
			List<Integer> otherIds = getActiveAdvisorIds(authHeader);
			for (Integer i : otherIds) {
				if (!userIds.contains(i)) {
					userIds.add(i);
				}
			}

			List<AdvisorCarts> advisorCarts = new ArrayList<>();
			for (Integer userId : userIds) {
				AdvisorCarts advisorCart = new AdvisorCarts();
				String jpql2 = "select b from Cart b where b.status in (:value0, :value1, :value2, :value3, :value4) and b.id in ("
						+ "select c.cartId from CartAssignment c where c.status = :value5 and c.assignedTo = :value6) order by b.id";
				List<Cart> carts = dao.getJPQLParams(Cart.class, jpql2, 'N', 'Q', 'W', 'R', 'A', 'A', userId);
				for (Cart cart : carts) {
					prepareCart(cart, authHeader);
				}
				Map<String, Object> user = this.getUser(userId.intValue(), authHeader);
				advisorCart.setUser(user);
				advisorCart.setCarts(carts);
				advisorCarts.add(advisorCart);
			}
			return Response.status(200).entity(advisorCarts).build();
		} catch (Exception ex) {
			ex.printStackTrace();
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@GET
	@Path("quotation-carts/assigned-to/{param}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAdvisorQuotationCarts(@PathParam(value = "param") int assignedTo) {
		try {
			String jpql = "select b from Cart b where b.status in (:value0, :value1, :value2, :value3, :value4) and b.id in ("
					+ "select c.cartId from CartAssignment c where c.status = :value5 and c.assignedTo = :value6";
			List<Cart> carts = dao.getJPQLParams(Cart.class, jpql, 'N', 'Q', 'W', 'R', 'A', 'A', assignedTo);
			return Response.status(200).entity(carts).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@PUT
	@Path("unassign-cart")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response unassignCart(Cart cart) {
		try {
			unassignCart(cart.getId());
			return Response.status(201).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	private void unassignCart(long cartId) {
		List<CartAssignment> ass = dao.getCondition(CartAssignment.class, "cartId", cartId);
		for (CartAssignment c : ass) {
			c.setStatus('D');
			dao.update(c);
		}
	}

	@SecuredUser
	@POST
	@Path("assign-cart")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response assignCart(CartAssignment assignment) {
		try {
			// deactivte previous assignments for this cart combo
			List<CartAssignment> ass = dao.getCondition(CartAssignment.class, "cartId", assignment.getCartId());
			for (CartAssignment c : ass) {
				c.setStatus('D');
				dao.update(c);
			}
			assignment.setStatus('A');
			assignment.setAssignedDate(new Date());
			dao.persist(assignment);
			return Response.status(201).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@Path("reviewes/cart/{cart}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCartReviews(@HeaderParam("Authorization") String authHeader,
			@PathParam(value = "cart") long cartId) {
		try {
			List<CartReview> reviews = dao.getConditionOrdered(CartReview.class, "cartId", cartId, "created", "asc");
			for (CartReview review : reviews) {
				review.setReviewer(getUser(review.getReviewerId(), authHeader));
			}
			return Response.status(200).entity(reviews).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	private void closePostponedAndActiveReviews(long cartId) {
		String jpql = "select b from CartReview b where b.cartId = :value0 and (b.status = :value1 or b.status = :value2)";
		List<CartReview> pastReviews = dao.getJPQLParams(CartReview.class, jpql, cartId, 'P', 'A');
		for (CartReview r : pastReviews) {
			r.setStatus('C');
			dao.update(r);
		}

		String jpql2 = "select b from  CartReview b where b.cartId = :value0 and b.actionValue = :value1";
		List<CartReview> pastReviews2 = dao.getJPQLParams(CartReview.class, jpql2, cartId, 'H');
		for (CartReview r : pastReviews2) {
			r.setActionValue('B');// general comment
			dao.update(r);
		}
	}

	// check idempotency of a quotation
	private boolean isReviewRedudant(int userId, long cartId, Date created) {
		// if a quotation was created less than 30 seconds ago, then do not do
		String jpql = "select b from CartReview b where b.reviewerId = :value0 and b.cartId = :value1 and b.created between :value2 and :value3";
		Date previous = Helper.addSeconds(created, -10);
		List<CartReview> carts = dao.getJPQLParams(CartReview.class, jpql, userId, cartId, previous, created);
		return carts.size() > 0;
	}

	// idempotent
	@SecuredUser
	@POST
	@Path("submit-review")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response submitCustomerServiceReview(CartReview review, @HeaderParam("Authorization") String authHeader) {
		try {
			if (isReviewRedudant(review.getReviewerId(), review.getCartId(), new Date())) {
				return Response.status(409).build();
			}
			closePostponedAndActiveReviews(review.getCartId());
			review.setCreated(new Date());
			dao.persist(review);
			if (review.getActionValue() == 'X') {
				Cart cart = dao.find(Cart.class, review.getCartId());
				cart.setStatus('X');
				cart.setSubmitteBy(review.getReviewerId());
				cart.setSubmitted(new Date());
				dao.update(cart);
			}
			return Response.status(200).build();
		} catch (Exception ex) {
			ex.printStackTrace();
			return Response.status(500).build();
		}
	}

	public <T> Response postSecuredRequest(String link, T t, String authHeader) {
		Builder b = ClientBuilder.newClient().target(link).request();
		b.header(HttpHeaders.AUTHORIZATION, authHeader);
		Response r = b.post(Entity.entity(t, "application/json"));// not secured
		return r;
	}

	public Response getSecuredRequest(String link, String authHeader) {
		Builder b = ClientBuilder.newClient().target(link).request();
		b.header(HttpHeaders.AUTHORIZATION, authHeader);
		Response r = b.get();
		return r;
	}

	public Response deleteSecuredRequest(String link, String authHeader) {
		Builder b = ClientBuilder.newClient().target(link).request();
		b.header(HttpHeaders.AUTHORIZATION, authHeader);
		Response r = b.delete();
		return r;
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

}
