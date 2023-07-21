package qetaa.service.cart.model.quotation.contract;

import java.io.Serializable;
import java.util.Date;

import qetaa.service.cart.model.quotation.QuotationVendorItem;

public class VendorItemContract implements Serializable{
	private static final long serialVersionUID = 1L;
	private long id;
	private long cartId;
	private long quotationId;
	private long quotationItemId;
	private int vendorId;
	private int quotationQuantity;
	private int vendorQuantity;
	private String itemNumber;
	private String itemDesc;
	private Double itemCostPrice;
	private Date created;
	private Date responded;
	private Integer respondedBy;
	private char status;
	private String vin;
	private int makeId;
	private int modelYearId;
	private Object modelYear;
	
	public VendorItemContract(QuotationVendorItem qvi, int makeId, int modelYearId, int quotationQuantity, String vin) {
		this.cartId = qvi.getCartId();
		this.created = qvi.getCreated();
		this.id = qvi.getId();
		this.itemCostPrice = qvi.getItemCostPrice();
		this.itemDesc = qvi.getItemDesc();
		this.itemNumber = qvi.getItemNumber();
		this.makeId = makeId;
		this.modelYearId = modelYearId;
		this.quotationId = qvi.getQuotationId();
		this.quotationItemId = qvi.getQuotationItemId();
		this.quotationQuantity = quotationQuantity;
		this.responded = qvi.getResponded();
		this.respondedBy = qvi.getRespondedBy();
		this.status = qvi.getStatus();
		this.vendorId = qvi.getVendorId();
		this.vin = vin;
		this.vendorQuantity = qvi.getQuantity();
	}

	public VendorItemContract() {
		
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getCartId() {
		return cartId;
	}
	public void setCartId(long cartId) {
		this.cartId = cartId;
	}
	public long getQuotationId() {
		return quotationId;
	}
	public void setQuotationId(long quotationId) {
		this.quotationId = quotationId;
	}
	public long getQuotationItemId() {
		return quotationItemId;
	}
	public void setQuotationItemId(long quotationItemId) {
		this.quotationItemId = quotationItemId;
	}
	public int getVendorId() {
		return vendorId;
	}
	public void setVendorId(int vendorId) {
		this.vendorId = vendorId;
	}
	public int getQuotationQuantity() {
		return quotationQuantity;
	}
	public void setQuotationQuantity(int quotationQuantity) {
		this.quotationQuantity = quotationQuantity;
	}
	public int getVendorQuantity() {
		return vendorQuantity;
	}
	public void setVendorQuantity(int vendorQuantity) {
		this.vendorQuantity = vendorQuantity;
	}
	public String getItemNumber() {
		return itemNumber;
	}
	public void setItemNumber(String itemNumber) {
		this.itemNumber = itemNumber;
	}
	public String getItemDesc() {
		return itemDesc;
	}
	public void setItemDesc(String itemDesc) {
		this.itemDesc = itemDesc;
	}
	public Double getItemCostPrice() {
		return itemCostPrice;
	}
	public void setItemCostPrice(Double itemCostPrice) {
		this.itemCostPrice = itemCostPrice;
	}
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	public Date getResponded() {
		return responded;
	}
	public void setResponded(Date responded) {
		this.responded = responded;
	}
	public Integer getRespondedBy() {
		return respondedBy;
	}
	public void setRespondedBy(Integer respondedBy) {
		this.respondedBy = respondedBy;
	}
	public char getStatus() {
		return status;
	}
	public void setStatus(char status) {
		this.status = status;
	}
	public String getVin() {
		return vin;
	}
	public void setVin(String vin) {
		this.vin = vin;
	}
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

	public Object getModelYear() {
		return modelYear;
	}

	public void setModelYear(Object modelYear) {
		this.modelYear = modelYear;
	}
	
}
