package qetaa.service.cart.apicontract;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="crt_cart_item")
public class PublicCartItem implements Serializable{

	private static final long serialVersionUID = 1L;
	@Id
	@Column(name="id")
	private long id;
	@Column(name="cart_id")
	private long cartId;
	@Column(name="quantity")
	private int quantity;
	@Column(name="name")
	private String name;
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
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
	
	
}
