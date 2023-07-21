package qetaa.service.cart.helpers;

public class AppConstants {
	private static final String CUSTOMER_SERVICE = "http://localhost:8080/service-qetaa-customer/rest/";
	private static final String USER_SERVICE = "http://localhost:8080/service-qetaa-user/rest/";
	private static final String VENDOR_SERVICE = "http://localhost:8080/service-qetaa-vendor/rest/";
	private static final String VEHICLE_SERVICE = "http://localhost:8080/service-qetaa-vehicle/rest/";
	private static final String PAYMENT_SERVICE = "http://localhost:8080/service-qetaa-payment/rest/";
	private static final String PRODUCT_SERVICE = "http://localhost:8080/service-qetaa-product/rest/";
	private final static String PROMOTION_SERVICE = "http://localhost:8080/service-qetaa-vendor/rest/promotion/";
	
	public static final String CUSTOMER_MATCH_TOKEN = CUSTOMER_SERVICE + "match-token";
	public static final String USER_MATCH_TOKEN = USER_SERVICE + "match-token";
	public static final String VENDOR_MATCH_TOKEN = VENDOR_SERVICE + "match-token";

	public static final String CREATE_CUSTOMER_ADDRESS = CUSTOMER_SERVICE + "create-address";
	
	public static final String SEND_SMS_TO_CUSTOMER = CUSTOMER_SERVICE + "send-sms-to-customer";
	public static final String SEND_SMS_TO_CUSTOMER_APPEND_CODE = CUSTOMER_SERVICE + "send-sms-to-customer/append-generated-code";
	
	public static final String CREATE_VENDOR_SCORE_BEST = VENDOR_SERVICE + "vendor-score/best-quotation";
	public static final String CREATE_VENDOR_SCORE_SECOND_BEST = VENDOR_SERVICE + "vendor-score/second-quotation";
	public static final String CREATE_VENDOR_FIRST = VENDOR_SERVICE + "vendor-score/first-quotation";
	public static final String CREATE_VENDOR_SCORE_INCOMPLETE = VENDOR_SERVICE + "vendor-score/incomplete-quotation";
	
	public static final String POST_PURCHASE_ORDER = PAYMENT_SERVICE + "purchase-order";

	public static final String GET_ACTIVE_ADVISORS = USER_SERVICE + "active-advisor-ids";
	
	public static final String deletePurchaseOrder(long cartId) {
		return PAYMENT_SERVICE + "purchase-order/cart/" + cartId;
	}
				
	public static String getQuotationVendors(int makeId){
		return VENDOR_SERVICE + "selected-vendor-ids/" + makeId;
	}
	
	public static String getFinderIds(int makeId) {
		return USER_SERVICE + "finder-ids/make/" + makeId;
	}
	
	public static String getMakeIds(int finderId) {
		return USER_SERVICE + "make-ids/finder/" + finderId;
	}
	
	
	public static String getVendor(int vendorId) {
		return VENDOR_SERVICE + "vendor/" + vendorId;
	}
	
	public static String getUser(int userId) {
		return USER_SERVICE + "user/" + userId;
	}
	
	public static String getVendorUser(int vendorUserId) {
		return VENDOR_SERVICE + "vendor-user/" + vendorUserId;
	}
	
	public static String getModelYear(int modelYearId) {
		return VEHICLE_SERVICE + "model-year/" + modelYearId;
	}
	
	public static String getMake(int makeId) {
		return VEHICLE_SERVICE + "make/" + makeId;
	}

	public static String getVendorPercentage(int vendor, int make){
		return VENDOR_SERVICE + "vendor-percentage/vendor/"+vendor+"/make/" + make;
	}
	
	public static String getAddress(long addressId) {
		return CUSTOMER_SERVICE + "address/"+ addressId;
	}
	
	public static String getCustomer(long customerId) {
		return CUSTOMER_SERVICE + "customer/"+ customerId;
	}
	
	public static String getProductId(String productNumber, long makeId, String name) {
		return PRODUCT_SERVICE + "product-id/number/"+productNumber+"/make/" + makeId + "/name/" +name+ "/create-if-not-available";
	}
	
	public static String getPromotionDiscount(Integer promId) {
		return PROMOTION_SERVICE + "promotion-code/"+promId+"/discount";
	}
	
	
	public static String getTestWorkshopPromoCode(long cartId, long customerId, int cityId) {
		return PROMOTION_SERVICE + "generate-test-workshop-promo-code/cart/"+cartId+"/city/"+cityId+"/customer/" + customerId;
	}
}
