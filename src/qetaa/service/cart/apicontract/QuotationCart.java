package qetaa.service.cart.apicontract;

import java.util.List;

public class QuotationCart {

	private long customerVehicleId;
	private String vinImage;
	private long customerId;
	private int cityId;
	private boolean imageAttached;
	private int makeId;
	private int modelYearId;
	private String vin;
	
	private List<QuotationCartItem> quotationCartItems;
	


	public int getMakeId() {
		return makeId;
	}
	public void setMakeId(int makeId) {
		this.makeId = makeId;
	}
	public int getModelYearId() {
		return modelYearId;
	}
	public void setModelYearId(int modelYearId) {
		this.modelYearId = modelYearId;
	}
	public boolean isImageAttached() {
		return imageAttached;
	}
	public void setImageAttached(boolean imageAttached) {
		this.imageAttached = imageAttached;
	}
	public long getCustomerVehicleId() {
		return customerVehicleId;
	}
	public void setCustomerVehicleId(long customerVehicleId) {
		this.customerVehicleId = customerVehicleId;
	}
	public String getVinImage() {
		return vinImage;
	}
	public void setVinImage(String vinImage) {
		this.vinImage = vinImage;
	}
	public long getCustomerId() {
		return customerId;
	}
	public void setCustomerId(long customerId) {
		this.customerId = customerId;
	}
	public int getCityId() {
		return cityId;
	}
	public void setCityId(int cityId) {
		this.cityId = cityId;
	}
	public List<QuotationCartItem> getQuotationCartItems() {
		return quotationCartItems;
	}
	public void setQuotationCartItems(List<QuotationCartItem> quotationCartItems) {
		this.quotationCartItems = quotationCartItems;
	}
	public String getVin() {
		return vin;
	}
	public void setVin(String vin) {
		this.vin = vin;
	}
	
	
	
}
