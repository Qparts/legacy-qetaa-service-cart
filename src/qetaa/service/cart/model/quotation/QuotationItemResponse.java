package qetaa.service.cart.model.quotation;

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
public class QuotationItemResponse {
	
	@Id
	@SequenceGenerator(name = "crt_quotation_item_response_id_seq_gen", sequenceName = "crt_quotation_item_response_id_seq", initialValue=1, allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "crt_quotation_item_response_id_seq_gen")
	@Column(name="id")
	private int id;
	@Column(name="cart_id")
	private long cartId;
	@Column(name="quotatuon_id")
	private long quotationId;
	@Column(name="quotation_item_id")
	private long quotationItemId;
	@Column(name="product_id")
	private long productId;
	@Column(name="item_desc")
	private String desc;
	@Column(name="item_cost")
	private Double cost;
	@Column(name="item_cost_wv")
	private Double costWv;
	@Column(name="quantity")
	private int quantity;
	@Column(name="status")
	private char status;//status of the item C = completed, N = not available
	@Column(name="response_type")
	private char responseType;//Finder, Vendor
	@Column(name="created")
	private Date created;//date of response
	@Column(name="created_by")
	private int createdBy;// by vendor_user_id , or finder_id
	@Column(name="default_percentage")
	private Double defaultPercentage;
	@Column(name="vendor_id")
	private Integer vendorId;
	
	
	
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
	public long getProductId() {
		return productId;
	}
	public void setProductId(long productId) {
		this.productId = productId;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public Double getCost() {
		return cost;
	}
	public void setCost(Double cost) {
		this.cost = cost;
	}
	public Double getCostWv() {
		return costWv;
	}
	public void setCostWv(Double costWv) {
		this.costWv = costWv;
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
	public char getResponseType() {
		return responseType;
	}
	public void setResponseType(char responseType) {
		this.responseType = responseType;
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
	public Integer getVendorId() {
		return vendorId;
	}
	public void setVendorId(Integer vendorId) {
		this.vendorId = vendorId;
	}
	
	
	
	
	
	
}
