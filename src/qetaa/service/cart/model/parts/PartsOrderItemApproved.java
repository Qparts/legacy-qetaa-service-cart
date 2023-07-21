package qetaa.service.cart.model.parts;

import java.io.Serializable;
import java.util.Date;

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

@Entity
@Table(name="crt_parts_item_approved")
public class PartsOrderItemApproved implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@SequenceGenerator(name = "crt_parts_item_approved_id_seq_gen", sequenceName = "crt_parts_item_approved_id_seq", initialValue=1, allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "crt_parts_item_approved_id_seq_gen")
	@Column(name="id")
	private long id;
	@Column(name="cart_id")
	private long cartId;
	@Column(name="parts_order_id")
	private long partsOrderId;
	@Column(name="parts_item_id")
	private long partsItemId;
	@Column(name="quotation_item_approved_id")
	private long quotationItemApprovedId;
	@Column(name="approved_quantity")
	private int approvedQuantity;
	@Column(name="collected")
	@Temporal(TemporalType.TIMESTAMP)
	private Date collected;
	@Column(name="received")
	@Temporal(TemporalType.TIMESTAMP)
	private Date received;
	@Column(name="prepared")
	@Temporal(TemporalType.TIMESTAMP)
	private Date prepared;
	@Column(name="shipped")
	@Temporal(TemporalType.TIMESTAMP)
	private Date shipped;
	@Column(name="collected_by")
	private Integer collectedBy;
	@Column(name="received_by")
	private Integer receivedBy;
	@Column(name="shipped_by")
	private Integer shippedBy;
	@Column(name="prepared_by")
	private Integer preparedBy;
	@Column(name="shipment_id")
	private String shipmentId;
	@Column(name="status")
	private char status;//W = waiting, V=ready at the vendor, C = collected, R = received, S = shipped 
	@Column(name="sales_price")
	private double salesPrice;
	@Column(name="cost_price")
	private double costPrice;
	@Column(name="item_number")
	private String itemNumber;
	@Column(name="vendor_id")
	private int vendorId;
	@Column(name="purchased_vendor_id")
	private Integer actualVendorId;
	@Column(name="inv_reference")
	private String invReference;
	@Column(name="product_id")
	private Long productId;
	
	
	
	public Long getProductId() {
		return productId;
	}
	public void setProductId(Long productId) {
		this.productId = productId;
	}
	public Integer getActualVendorId() {
		return actualVendorId;
	}
	public void setActualVendorId(Integer actualVendorId) {
		this.actualVendorId = actualVendorId;
	}
	public String getInvReference() {
		return invReference;
	}
	public void setInvReference(String invReference) {
		this.invReference = invReference;
	}
	@Transient
	private String itemDesc;
	
	
	public String getItemDesc() {
		return itemDesc;
	}
	public void setItemDesc(String itemDesc) {
		this.itemDesc = itemDesc;
	}
	public int getVendorId() {
		return vendorId;
	}
	public void setVendorId(int vendorId) {
		this.vendorId = vendorId;
	}
	public String getItemNumber() {
		return itemNumber;
	}
	public void setItemNumber(String itemNumber) {
		this.itemNumber = itemNumber;
	}
	public double getSalesPrice() {
		return salesPrice;
	}
	public void setSalesPrice(double salesPrice) {
		this.salesPrice = salesPrice;
	}
	public double getCostPrice() {
		return costPrice;
	}
	public void setCostPrice(double costPrice) {
		this.costPrice = costPrice;
	}
	public Date getPrepared() {
		return prepared;
	}
	public void setPrepared(Date prepared) {
		this.prepared = prepared;
	}
	public Integer getPreparedBy() {
		return preparedBy;
	}
	public void setPreparedBy(Integer preparedBy) {
		this.preparedBy = preparedBy;
	}
	public char getStatus() {
		return status;
	}
	public void setStatus(char status) {
		this.status = status;
	}
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
	public long getPartsOrderId() {
		return partsOrderId;
	}
	public void setPartsOrderId(long partsOrderId) {
		this.partsOrderId = partsOrderId;
	}
	public long getPartsItemId() {
		return partsItemId;
	}
	public void setPartsItemId(long partsItemId) {
		this.partsItemId = partsItemId;
	}
	public long getQuotationItemApprovedId() {
		return quotationItemApprovedId;
	}
	public void setQuotationItemApprovedId(long quotationItemApprovedId) {
		this.quotationItemApprovedId = quotationItemApprovedId;
	}
	public int getApprovedQuantity() {
		return approvedQuantity;
	}
	public void setApprovedQuantity(int approvedQuantity) {
		this.approvedQuantity = approvedQuantity;
	}
	public Date getCollected() {
		return collected;
	}
	public void setCollected(Date collected) {
		this.collected = collected;
	}
	public Date getReceived() {
		return received;
	}
	public void setReceived(Date received) {
		this.received = received;
	}
	public Date getShipped() {
		return shipped;
	}
	public void setShipped(Date shipped) {
		this.shipped = shipped;
	}
	public Integer getCollectedBy() {
		return collectedBy;
	}
	public void setCollectedBy(Integer collectedBy) {
		this.collectedBy = collectedBy;
	}
	public Integer getReceivedBy() {
		return receivedBy;
	}
	public void setReceivedBy(Integer receivedBy) {
		this.receivedBy = receivedBy;
	}
	public Integer getShippedBy() {
		return shippedBy;
	}
	public void setShippedBy(Integer shippedBy) {
		this.shippedBy = shippedBy;
	}
	public String getShipmentId() {
		return shipmentId;
	}
	public void setShipmentId(String shipmentId) {
		this.shipmentId = shipmentId;
	}
	
	

	
}
