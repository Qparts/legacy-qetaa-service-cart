package qetaa.service.cart.model.quotation.contract;

import java.io.Serializable;
import java.util.Map;

public class FinalizedItemResponse implements Serializable {
	private static final long serialVersionUID = 1L;
	private long quotationVendorItemId;
	private Map<String, Object> vendor;
	private String partNumber;
	private int submittedQuantity;
	private int selectedQuantity;
	private double unitCost;
	private double salesPercentage;
	
	public long getQuotationVendorItemId() {
		return quotationVendorItemId;
	}
	public void setQuotationVendorItemId(long quotationVendorItemId) {
		this.quotationVendorItemId = quotationVendorItemId;
	}
	public Map<String, Object> getVendor() {
		return vendor;
	}
	public void setVendor(Map<String, Object> vendor) {
		this.vendor = vendor;
	}
	public String getPartNumber() {
		return partNumber;
	}
	public void setPartNumber(String partNumber) {
		this.partNumber = partNumber;
	}
	public int getSubmittedQuantity() {
		return submittedQuantity;
	}
	public void setSubmittedQuantity(int submittedQuantity) {
		this.submittedQuantity = submittedQuantity;
	}
	public int getSelectedQuantity() {
		return selectedQuantity;
	}
	public void setSelectedQuantity(int selectedQuantity) {
		this.selectedQuantity = selectedQuantity;
	}
	public double getUnitCost() {
		return unitCost;
	}
	public void setUnitCost(double unitCost) {
		this.unitCost = unitCost;
	}
	public double getSalesPercentage() {
		return salesPercentage;
	}
	public void setSalesPercentage(double salesPercentage) {
		this.salesPercentage = salesPercentage;
	}
	
	
}
