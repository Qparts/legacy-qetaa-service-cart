package qetaa.service.cart.model.quotation;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Table(name="crt_quotation_item_response")
@Entity
public class QuotationItemResponse implements Serializable{
	
	private static final long serialVersionUID = 1L;
	@Id
	@SequenceGenerator(name = "crt_quotation_item_response_id_seq_gen", sequenceName = "crt_quotation_item_response_id_seq", initialValue=1, allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "crt_quotation_item_response_id_seq_gen")
	@Column(name="id")
	private int id;
	@Column(name="cart_id")
	private long cartId;
	@Column(name="quotation_id")
	private long quotationId;
	@Column(name="quotation_item_id")
	private long quotationItemId;
	@Column(name="product_id")
	private Long productId;
	@Column(name="product_price_id")
	private Long productPriceId;
	@Column(name="item_desc")
	private String desc;
	@Column(name="quantity")
	private int quantity;
	@Column(name="status")
	private char status;//status of the item C = completed, N = not available
	@Column(name="created")
	private Date created;//date of response
	@Column(name="created_by")
	private int createdBy;// by vendor_user_id , or finder_id
	@Column(name="default_percentage")
	private Double defaultPercentage;
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
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
	public void setQuotationId(long quotationid) {
		this.quotationId = quotationid;
	}
	public long getQuotationItemId() {
		return quotationItemId;
	}
	public void setQuotationItemId(long quotationItemId) {
		this.quotationItemId = quotationItemId;
	}
	public Long getProductId() {
		return productId;
	}
	public void setProductId(Long productId) {
		this.productId = productId;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	public Long getProductPriceId() {
		return productPriceId;
	}
	public void setProductPriceId(Long productPriceId) {
		this.productPriceId = productPriceId;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public char getStatus() {
		return status;
	}
	public void setStatus(char status) {
		this.status = status;
	}
	
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	public int getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(int createdBy) {
		this.createdBy = createdBy;
	}
	public Double getDefaultPercentage() {
		return defaultPercentage;
	}
	public void setDefaultPercentage(Double defaultPercentage) {
		this.defaultPercentage = defaultPercentage;
	}
	
	
	
	
}
