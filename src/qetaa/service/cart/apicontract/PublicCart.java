package qetaa.service.cart.apicontract;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Table(name="crt_cart")
@Entity 
public class PublicCart implements Serializable{
	
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name="id")
	private long id;
	@Column(name="customer_id")
	private long customerId;
	@Column(name="status")
	private char status;
	@Column(name="created")
	private Date created;
	@Column(name="customer_vehicle_id")
	private Long customerVehicleId;
	@Transient
	private List<PublicCartItem> cartItems;
	@Transient
	private List<PublicCartReview> reviews;
	@Transient 
	private List<PublicQuotationItem> quotationItems;

	
	public List<PublicCartReview> getReviews() {
		return reviews;
	}
	public void setReviews(List<PublicCartReview> reviews) {
		this.reviews = reviews;
	}
	public List<PublicCartItem> getCartItems() {
		return cartItems;
	}
	public void setCartItems(List<PublicCartItem> cartItems) {
		this.cartItems = cartItems;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getCustomerId() {
		return customerId;
	}
	public void setCustomerId(long customerId) {
		this.customerId = customerId;
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
	public Long getCustomerVehicleId() {
		return customerVehicleId;
	}
	public void setCustomerVehicleId(Long customerVehicleId) {
		this.customerVehicleId = customerVehicleId;
	}
	public List<PublicQuotationItem> getQuotationItems() {
		return quotationItems;
	}
	public void setQuotationItems(List<PublicQuotationItem> quotationItems) {
		this.quotationItems = quotationItems;
	}
	
	
	
	
}
