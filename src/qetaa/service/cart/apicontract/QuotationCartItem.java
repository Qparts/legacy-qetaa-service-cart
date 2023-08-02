package qetaa.service.cart.apicontract;

public class QuotationCartItem {

	private String image;
	private int quantity;
	private String itemName;
	private boolean imageAttached;
	
	
	
	
	public boolean isImageAttached() {
		return imageAttached;
	}
	public void setImageAttached(boolean imageAttached) {
		this.imageAttached = imageAttached;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public String getItemName() {
		return itemName;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	
	
}
