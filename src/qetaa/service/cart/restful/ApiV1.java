package qetaa.service.cart.restful;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import qetaa.service.cart.apicontract.PublicCart;
import qetaa.service.cart.apicontract.PublicCartItem;
import qetaa.service.cart.apicontract.PublicCartReview;
import qetaa.service.cart.apicontract.PublicQuotationItem;
import qetaa.service.cart.apicontract.QuotationCart;
import qetaa.service.cart.dao.DAO;
import qetaa.service.cart.filters.Secured;
import qetaa.service.cart.filters.SecuredCustomer;
import qetaa.service.cart.helpers.AppConstants;
import qetaa.service.cart.helpers.Helper;
import qetaa.service.cart.model.Cart;
import qetaa.service.cart.model.quotation.QuotationItemResponse;
import qetaa.service.cart.model.security.WebApp;


@Path("/api/v1/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ApiV1 {

	@EJB
	private DAO dao;

	@EJB
	private AsyncService async;
	
	
	/*@SecuredCustomer
	@GET
	@Path("quotation-carts/customer/{param}/quoted")
	public Response getQuotationCartsQuoted(@HeaderParam("Authorization") String authHeader, @PathParam(value="param") long customerId) {
		try {
			String jpql = "select b from PublicCart b where b.customerId = :value0 and b.status = :value1 order by b.id desc";
			List<PublicCart> pcarts = dao.getJPQLParams(PublicCart.class, jpql, customerId, 'S');
			for(PublicCart pc : pcarts) {
				List<QuotationItemApproved> list = dao.getCondition(QuotationItemApproved.class, "cartId", pc.getId());
				for(QuotationItemApproved qouted : list) {
					
				}
			}
		}catch(Exception ex) {
			
		}
	}*/
	
	
	
	
	
	@SecuredCustomer
	@GET
	@Path("quotation-carts/customer/{param}/submitted")
	public Response getQuotationCartsCompleted(@HeaderParam("Authorization") String authHeader, @PathParam(value="param") long customerId) {
		try {
			List<PublicCart> pcarts = dao.getTwoConditions(PublicCart.class, "customerId", "status", customerId, 'S');
			for(PublicCart pc : pcarts) {
				List<PublicCartReview> previews = dao.getTwoConditions(PublicCartReview.class, "cartId", "visibleToCustomer", pc.getId(), true);
				pc.setReviews(previews);				
				String jpql = "select b from PublicQuotationItem b where b.cartId = :value0 and status = :value1"
						+ " and b.quotationId in (select c.id from Quotation c where c.status = :value1 and c.cartId = :value0)";
				List<PublicQuotationItem> pqitems = dao.getJPQLParams(PublicQuotationItem.class, jpql, pc.getId(), 'C');
				pc.setQuotationItems(new ArrayList<>());
				for(PublicQuotationItem pqi : pqitems) {
					pqi.setProducts(new ArrayList<>());
					String jpql2 = "select b from QuotationItemResponse b where b.cartId = :value0 and b.status = :value1 and b.quotationItemId = :value2";
					List<QuotationItemResponse> qirs = dao.getJPQLParams(QuotationItemResponse.class, jpql2, pc.getId(), 'C', pqi.getId());
					for(QuotationItemResponse qir : qirs) {
						Response r = this.getSecuredRequest(AppConstants.getPublicProduct(qir.getProductPriceId(), qir.getDefaultPercentage()), authHeader);
						if(r.getStatus() == 200) {
							Map<String,Object> map = r.readEntity(Map.class);
							pqi.getProducts().add(map);
						}
					}
					pc.getQuotationItems().add(pqi);
				}
			}
			return Response.status(200).entity(pcarts).build();
			
		}catch(Exception ex) {
			return Response.status(500).build();
		}
	}
	
	
	@SecuredCustomer
	@GET
	@Path("quotation-carts/customer/{param}")
	public Response getQuotationCarts(@HeaderParam("Authorization") String authHeader, @PathParam(value="param") long customerId) {
		try {
			String jpql = "select b from PublicCart b where b.customerId = :value0 and b.status in (:value1, :value2, :value3, :value4, :value5)";
			List<PublicCart> pcarts = dao.getJPQLParams(PublicCart.class, jpql, customerId, 'N', 'Q' , 'W' , 'R' , 'A');
			for(PublicCart pc : pcarts) {
				List<PublicCartItem> pcis = dao.getCondition(PublicCartItem.class, "cartId", pc.getId());
				pc.setCartItems(pcis);
				List<PublicCartReview> previews = dao.getTwoConditions(PublicCartReview.class, "cartId", "visibleToCustomer", pc.getId(), true);
				pc.setReviews(previews);
			}
			return Response.status(200).entity(pcarts).build();
		}catch(Exception ex) {
			return Response.status(500).build();
		}
	}
	
	
	@Secured
	@POST
	@Path("quotation-cart")
	public Response createQuotationCart(@HeaderParam("Authorization") String authHeader, QuotationCart quotationCart) {
		try {
			if (isRedudant(quotationCart.getCustomerId(), new Date())) {
				return Response.status(429).build();
			}
			
			WebApp wa = this.getWebAppFromAuthHeader(authHeader);
			
			Cart cart = new Cart();
			cart.setAppCode(wa.getAppCode());
			cart.setCityId(quotationCart.getCityId());
			cart.setCreated(new Date());
			cart.setCreatedBy(0);
			cart.setCustomerId(quotationCart.getCustomerId());
			cart.setMakeId(quotationCart.getMakeId());
			cart.setNoVin(quotationCart.isImageAttached());
			cart.setStatus('N');
			cart.setVatPercentage(0.05);
			cart.setVehicleYear(quotationCart.getModelYearId());
			cart.setVin(quotationCart.getVin());
			cart.setCustomerVehicleId(quotationCart.getCustomerVehicleId());
			dao.persist(cart);
			//customize this
			async.postQuotationCartCreation(cart, quotationCart, authHeader);
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("cartId", cart.getId());
			return Response.status(200).entity(map).build();
		}catch(Exception ex) {
			return Response.status(500).build();
		}
		// create quotation cart and send back the id!
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


}
