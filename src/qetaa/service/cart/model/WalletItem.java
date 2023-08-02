package qetaa.service.cart.model;

import java.io.Serializable;

public class WalletItem implements Serializable{

	private static final long serialVersionUID = 1L;
	private long id;
	private long walletId;
	private Long productId;
	private char itemType;
	private String itemNumber;
	private String itemDesc;
	private int quantity;
	private long cartId;
	private Integer vendorId;
	private double unitSales;
	private double unitSalesWv;
	private double unitSalesNet;
	private double unitSalesNetWv;
	private double unitQuotedCost;
	private double unitQuotedCostWv;
	private char status;//A = awaiting, R = Refunded, P = Purchased, S = Sold, H= shipped
	private Long refundedItemId;
	private String refundNote;
	private Long purchasedItemId;
	private Long soldItemId;	

	public Long getSoldItemId() {
		return soldItemId;
	}
	public void setSoldItemId(Long soldItemId) {
		this.soldItemId = soldItemId;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getWalletId() {
		return walletId;
	}
	public void setWalletId(long walletId) {
		this.walletId = walletId;
	}
	public Long getProductId() {
		return productId;
	}
	public void setProductId(Long productId) {
		this.productId = productId;
	}
	public char getItemType() {
		return itemType;
	}
	public void setItemType(char itemType) {
		this.itemType = itemType;
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
	public long getCartId() {
		return cartId;
	}
	public void setCartId(long cartId) {
		this.cartId = cartId;
	}
	public Integer getVendorId() {
		return vendorId;
	}
	public void setVendorId(Integer vendorId) {
		this.vendorId = vendorId;
	}
	public double getUnitSales() {
		return unitSales;
	}
	public void setUnitSales(double unitSales) {
		this.unitSales = unitSales;
	}
	public double getUnitSalesWv() {
		return unitSalesWv;
	}
	public void setUnitSalesWv(double unitSalesWv) {
		this.unitSalesWv = unitSalesWv;
	}
	public double getUnitSalesNet() {
		return unitSalesNet;
	}
	public void setUnitSalesNet(double unitSalesNet) {
		this.unitSalesNet = unitSalesNet;
	}
	public double getUnitSalesNetWv() {
		return unitSalesNetWv;
	}
	public void setUnitSalesNetWv(double unitSalesNetWv) {
		this.unitSalesNetWv = unitSalesNetWv;
	}
	public double getUnitQuotedCost() {
		return unitQuotedCost;
	}
	public void setUnitQuotedCost(double unitQuotedCost) {
		this.unitQuotedCost = unitQuotedCost;
	}
	public double getUnitQuotedCostWv() {
		return unitQuotedCostWv;
	}
	public void setUnitQuotedCostWv(double unitQuotedCostWv) {
		this.unitQuotedCostWv = unitQuotedCostWv;
	}
	public char getStatus() {
		return status;
	}
	public void setStatus(char status) {
		this.status = status;
	}
	public Long getRefundedItemId() {
		return refundedItemId;
	}
	public void setRefundedItemId(Long refundedItemId) {
		this.refundedItemId = refundedItemId;
	}
	public String getRefundNote() {
		return refundNote;
	}
	public void setRefundNote(String refundNote) {
		this.refundNote = refundNote;
	}
	public Long getPurchasedItemId() {
		return purchasedItemId;
	}
	public void setPurchasedItemId(Long purchasedItemId) {
		this.purchasedItemId = purchasedItemId;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	
}
