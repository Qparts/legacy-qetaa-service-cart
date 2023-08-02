package qetaa.service.cart.model.parts.contract;

import qetaa.service.cart.model.Cart;
import qetaa.service.cart.model.parts.PartsOrder;

public class PartsOrderCashOnDelivery {
	private PartsOrder partsOrder;
	private Cart cart;
	private String customerName;
	private Double discountPercentage;
	
	
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public Double getDiscountPercentage() {
		return discountPercentage;
	}
	public void setDiscountPercentage(Double discountPercentage) {
		this.discountPercentage = discountPercentage;
	}
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
	
	
}
