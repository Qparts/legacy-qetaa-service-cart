package qetaa.service.cart.model.quotation.contract;

import qetaa.service.cart.model.quotation.QuotationItem;

public class ManualQuotationVendor {
	private int vendorId;
	private int createdBy;
	private QuotationItem quotationItem;
	public int getVendorId() {
		return vendorId;
	}
	public void setVendorId(int vendorId) {
		this.vendorId = vendorId;
	}
	public int getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(int createdBy) {
		this.createdBy = createdBy;
	}
	public QuotationItem getQuotationItem() {
		return quotationItem;
	}
	public void setQuotationItem(QuotationItem quotationItem) {
		this.quotationItem = quotationItem;
	}
	
	
	

}
