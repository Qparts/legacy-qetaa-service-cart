package qetaa.service.cart.model.quotation.contract;

import java.util.List;

public class FinalizedItemsHolder {
	
	private List<FinalizedItem> finalizedItems;
	private Integer deliveryFees;
	private Integer createdBy;
	private long cartId;
	
	public List<FinalizedItem> getFinalizedItems() {
		return finalizedItems;
	}
	public void setFinalizedItems(List<FinalizedItem> finalizedItems) {
		this.finalizedItems = finalizedItems;
	}
	public Integer getDeliveryFees() {
		return deliveryFees;
	}
	public void setDeliveryFees(Integer deliveryFees) {
		this.deliveryFees = deliveryFees;
	}
	public Integer getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(Integer createdBy) {
		this.createdBy = createdBy;
	}
	public long getCartId() {
		return cartId;
	}
	public void setCartId(long cartId) {
		this.cartId = cartId;
	}
	
	
	
	

}
