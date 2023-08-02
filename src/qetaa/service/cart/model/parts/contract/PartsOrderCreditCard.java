package qetaa.service.cart.model.parts.contract;

import java.io.Serializable;

import qetaa.service.cart.model.Cart;
import qetaa.service.cart.model.parts.PartsOrder;

public class PartsOrderCreditCard implements Serializable{

	private static final long serialVersionUID = 1L;
	private PartsOrder partsOrder;
	private Cart cart;
	private String ccCompany;
	private String customerName;
	private Double discountPercentage;
	private String gateway;
	private String transactionId;
	private Double creditFees;
	
	

	public Double getCreditFees() {
		return creditFees;
	}

	public void setCreditFees(Double creditFees) {
		this.creditFees = creditFees;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getGateway() {
		return gateway;
	}

	public void setGateway(String gateway) {
		this.gateway = gateway;
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

	public String getCcCompany() {
		return ccCompany;
	}

	public void setCcCompany(String ccCompany) {
		this.ccCompany = ccCompany;
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
