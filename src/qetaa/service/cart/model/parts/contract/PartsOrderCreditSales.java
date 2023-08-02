package qetaa.service.cart.model.parts.contract;

import qetaa.service.cart.model.Cart;
import qetaa.service.cart.model.parts.PartsOrder;

public class PartsOrderCreditSales {

	private PartsOrder partsOrder;
	private Cart cart;
	private Double discountPercentage;
	private String customerName;
	public PartsOrder getPartsOrder() {
		return partsOrder;
	}
	public void setPartsOrder(PartsOrder partsOrder) {
		this.partsOrder = partsOrder;
	}
	public Cart getCart() {
		return cart;
	}
	public void setCart(Cart cart) {
		this.cart = cart;
	}
	public Double getDiscountPercentage() {
		return discountPercentage;
	}
	public void setDiscountPercentage(Double discountPercentage) {
		this.discountPercentage = discountPercentage;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	
}
