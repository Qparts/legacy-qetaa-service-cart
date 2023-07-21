package qetaa.service.cart.model.parts.contract;

import java.io.Serializable;
import java.util.Map;

import qetaa.service.cart.model.Cart;
import qetaa.service.cart.model.parts.PartsOrder;

public class CompletedCart implements Serializable {

	private static final long serialVersionUID = 1L;

	private Cart cart;
	private PartsOrder partsOrder;
	private Map<String, Object> payment;
	private double partsReturnTotal;

	public double getPartsReturnTotal() {
		return partsReturnTotal;
	}

	public void setPartsReturnTotal(double partsReturnTotal) {
		this.partsReturnTotal = partsReturnTotal;
	}

	public Cart getCart() {
		return cart;
	}

	public void setCart(Cart cart) {
		this.cart = cart;
	}

	public PartsOrder getPartsOrder() {
		return partsOrder;
	}

	public void setPartsOrder(PartsOrder partsOrder) {
		this.partsOrder = partsOrder;
	}

	public Map<String, Object> getPayment() {
		return payment;
	}

	public void setPayment(Map<String, Object> payment) {
		this.payment = payment;
	}

}
