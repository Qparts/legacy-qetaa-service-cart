package qetaa.service.cart.model.quotation.contract;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import qetaa.service.cart.model.Cart;


public class AdvisorCarts implements Serializable{

	private static final long serialVersionUID = 1L;
	private Map<String,Object> user;
	private List<Cart> carts;
	
	public Map<String,Object> getUser() {
		return user;
	}
	public List<Cart> getCarts() {
		return carts;
	}
	public void setUser(Map<String,Object> user) {
		this.user = user;
	}
	public void setCarts(List<Cart> carts) {
		this.carts = carts;
	}
	
	
}
