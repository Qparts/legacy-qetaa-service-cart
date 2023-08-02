package qetaa.service.cart.apicontract;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "crt_quotation_item")
public class PublicQuotationItem implements Serializable{

	private static final long serialVersionUID = 1L;
	@Id
	@Column(name="id")
	private long id;
	@Column(name="cart_id")
	@JsonIgnore
	private long cartId;
	@Column(name="quotation_id")
	@JsonIgnore
	private long quotationId;
	@Column(name="quantity")
	private int quantity;
	@Column(name="item_desc")
	private String desc;
	@Column(name="status")
	@JsonIgnore
	private char status;//W = waiting , N = not available, C = completed
	@Transient
	private List<Map<String,Object>> products;
	
	
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public List<Map<String, Object>> getProducts() {
		return products;
	}
	public void setProducts(List<Map<String, Object>> products) {
		this.products = products;
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
	public char getStatus() {
		return status;
	}
	public void setStatus(char status) {
		this.status = status;
	}
	
	
	
}
