package qetaa.service.cart.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import qetaa.service.cart.model.quotation.Quotation;
import qetaa.service.cart.model.quotation.contract.ApprovedItem;

@Table(name="crt_cart")
@Entity 
public class Cart implements Serializable{

	private static final long serialVersionUID = 1L;
	@Id
	@SequenceGenerator(name = "crt_cart_id_seq_gen", sequenceName = "crt_cart_id_seq", initialValue=1000, allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "crt_cart_id_seq_gen")
	@Column(name="id")
	private long id;
	@Column(name="customer_id")
	private long customerId;
	@Column(name="status")
	private char status;
	@Column(name="vin")
	private String vin;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="created")
	private Date created;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="submitted")
	private Date submitted;
	@Column(name="created_by")
	private Integer createdBy;
	@Column(name="submitted_by")
	private Integer submitteBy;
	@Column(name="city_id")
	private Integer cityId;
	@Column(name="app_code")
	private Integer appCode;
	@Column(name="vehicle_year")
	private Integer vehicleYear;
	@Column(name="make_id")
	private Integer makeId;
	@Column(name="delivery_fees")
	private Integer deliveryFees;
	@Column(name="vat_percentage")
	private double vatPercentage;
	@Column(name="no_vin")
	private boolean noVin;
	@Column(name="vin_image")
	private boolean vinImage;
	@Column(name="promotion_code")
	private Integer promotionCode;
	
	
	@Transient
	private List<Quotation> quotations;
	@Transient
	private Map<String,Object> customer;
	@Transient
	private Map<String,Object> modelYear;
	
	@Transient
	private List<CartItem> cartItems;
	
	@Transient
	private List<CartReview> reviews;
	
	@Transient
	private List<ApprovedItem> approvedItems;
	
	
	
	public List<CartReview> getReviews() {
		return reviews;
	}
	public void setReviews(List<CartReview> reviews) {
		this.reviews = reviews;
	}
	public boolean isNoVin() {
		return noVin;
	}
	public void setNoVin(boolean noVin) {
		this.noVin = noVin;
	}
	public Map<String, Object> getModelYear() {
		return modelYear;
	}
	public void setModelYear(Map<String, Object> modelYear) {
		this.modelYear = modelYear;
	}
	public Map<String, Object> getCustomer() {
		return customer;
	}
	public void setCustomer(Map<String, Object> customer) {
		this.customer = customer;
	}

	public long getId() {
		return id;
	}
	public List<Quotation> getQuotations() {
		return quotations;
	}
	public void setQuotations(List<Quotation> quotations) {
		this.quotations = quotations;
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
	public String getVin() {
		return vin;
	}
	public void setVin(String vin) {
		this.vin = vin;
	}
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	public Date getSubmitted() {
		return submitted;
	}
	public void setSubmitted(Date submitted) {
		this.submitted = submitted;
	}
	public Integer getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(Integer createdBy) {
		this.createdBy = createdBy;
	}
	public Integer getSubmitteBy() {
		return submitteBy;
	}
	public void setSubmitteBy(Integer submitteBy) {
		this.submitteBy = submitteBy;
	}
	public Integer getCityId() {
		return cityId;
	}
	public void setCityId(Integer cityId) {
		this.cityId = cityId;
	}
	public Integer getAppCode() {
		return appCode;
	}
	public void setAppCode(Integer appCode) {
		this.appCode = appCode;
	}
	public Integer getVehicleYear() {
		return vehicleYear;
	}
	public void setVehicleYear(Integer vehicleYear) {
		this.vehicleYear = vehicleYear;
	}
	public List<CartItem> getCartItems() {
		return cartItems;
	}
	public void setCartItems(List<CartItem> cartItems) {
		this.cartItems = cartItems;
	}
	public Integer getMakeId() {
		return makeId;
	}
	public void setMakeId(Integer makeId) {
		this.makeId = makeId;
	}
	public Integer getDeliveryFees() {
		return deliveryFees;
	}
	public void setDeliveryFees(Integer deliveryFees) {
		this.deliveryFees = deliveryFees;
	}
	public double getVatPercentage() {
		return vatPercentage;
	}
	public void setVatPercentage(double vatPercentage) {
		this.vatPercentage = vatPercentage;
	}
	public boolean isVinImage() {
		return vinImage;
	}
	public void setVinImage(boolean vinImage) {
		this.vinImage = vinImage;
	}
	public List<ApprovedItem> getApprovedItems() {
		return approvedItems;
	}
	public void setApprovedItems(List<ApprovedItem> approvedItems) {
		this.approvedItems = approvedItems;
	}
	public Integer getPromotionCode() {
		return promotionCode;
	}
	public void setPromotionCode(Integer promotionCode) {
		this.promotionCode = promotionCode;
	}
	
	
	
	
	
	
	 	
}
