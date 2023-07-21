package qetaa.service.cart.model.parts;

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

@Entity
@Table(name="crt_parts_item_approved_return")
public class PartsOrderItemReturn {

	@Id
	@SequenceGenerator(name = "crt_parts_item_approved_return_id_seq_gen", sequenceName = "crt_parts_item_approved_return_id_seq", initialValue=1, allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "crt_parts_item_approved_return_id_seq_gen")
	@Column(name="id")
	private long id;
	@Column(name="cart_id")
	private long cartId;
	@Column(name="parts_order_id")
	private long partsOrderId;
	@Column(name="parts_item_id")
	private long partsItemId;
	@Column(name="approved_item_id")
	private long approvedId;
	@Column(name="return_quantity")
	private int returnQuantity;
	@Column(name="returned")
	@Temporal(TemporalType.TIMESTAMP)
	private Date returned;
	@Column(name="return_by")
	private Integer returnBy;
	@Column(name="shipment_reference")
	private String shipmentReference;
	@Column(name="shipment_cost")
	private double shipmentCost;
	@Column(name="status")
	private char status;//S = shipped, R = received, T = returned
	@Column(name="return_amount")
	private double returnAmount;
	@Column(name="cost_price")
	private double costPrice;
	@Column(name="vendor_id")
	private int vendorId;
	@Column(name="return_inv_reference")
	private String invReference;
	@Column(name="courrier")
	private String courrier;
	@Column(name="item_number")
	private String itemNumber;
	
	
	
	
	
	
	public String getItemNumber() {
		return itemNumber;
	}
	public void setItemNumber(String itemNumber) {
		this.itemNumber = itemNumber;
	}
	public String getCourrier() {
		return courrier;
	}
	public void setCourrier(String courrier) {
		this.courrier = courrier;
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
	public long getApprovedId() {
		return approvedId;
	}
	public void setApprovedId(long approvedId) {
		this.approvedId = approvedId;
	}
	public int getReturnQuantity() {
		return returnQuantity;
	}
	public void setReturnQuantity(int returnQuantity) {
		this.returnQuantity = returnQuantity;
	}
	public Date getReturned() {
		return returned;
	}
	public void setReturned(Date returned) {
		this.returned = returned;
	}
	public Integer getReturnBy() {
		return returnBy;
	}
	public void setReturnBy(Integer returnBy) {
		this.returnBy = returnBy;
	}
	public String getShipmentReference() {
		return shipmentReference;
	}
	public void setShipmentReference(String shipmentReference) {
		this.shipmentReference = shipmentReference;
	}
	public double getShipmentCost() {
		return shipmentCost;
	}
	public void setShipmentCost(double shipmentCost) {
		this.shipmentCost = shipmentCost;
	}
	public char getStatus() {
		return status;
	}
	public void setStatus(char status) {
		this.status = status;
	}
	public double getReturnAmount() {
		return returnAmount;
	}
	public void setReturnAmount(double returnAmount) {
		this.returnAmount = returnAmount;
	}
	public double getCostPrice() {
		return costPrice;
	}
	public void setCostPrice(double costPrice) {
		this.costPrice = costPrice;
	}
	public int getVendorId() {
		return vendorId;
	}
	public void setVendorId(int vendorId) {
		this.vendorId = vendorId;
	}
	public String getInvReference() {
		return invReference;
	}
	public void setInvReference(String invReference) {
		this.invReference = invReference;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (approvedId ^ (approvedId >>> 32));
		result = prime * result + (int) (cartId ^ (cartId >>> 32));
		long temp;
		temp = Double.doubleToLongBits(costPrice);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((invReference == null) ? 0 : invReference.hashCode());
		result = prime * result + (int) (partsItemId ^ (partsItemId >>> 32));
		result = prime * result + (int) (partsOrderId ^ (partsOrderId >>> 32));
		temp = Double.doubleToLongBits(returnAmount);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((returnBy == null) ? 0 : returnBy.hashCode());
		result = prime * result + returnQuantity;
		result = prime * result + ((returned == null) ? 0 : returned.hashCode());
		temp = Double.doubleToLongBits(shipmentCost);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((shipmentReference == null) ? 0 : shipmentReference.hashCode());
		result = prime * result + status;
		result = prime * result + vendorId;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PartsOrderItemReturn other = (PartsOrderItemReturn) obj;
		if (approvedId != other.approvedId)
			return false;
		if (cartId != other.cartId)
			return false;
		if (Double.doubleToLongBits(costPrice) != Double.doubleToLongBits(other.costPrice))
			return false;
		if (id != other.id)
			return false;
		if (invReference == null) {
			if (other.invReference != null)
				return false;
		} else if (!invReference.equals(other.invReference))
			return false;
		if (partsItemId != other.partsItemId)
			return false;
		if (partsOrderId != other.partsOrderId)
			return false;
		if (Double.doubleToLongBits(returnAmount) != Double.doubleToLongBits(other.returnAmount))
			return false;
		if (returnBy == null) {
			if (other.returnBy != null)
				return false;
		} else if (!returnBy.equals(other.returnBy))
			return false;
		if (returnQuantity != other.returnQuantity)
			return false;
		if (returned == null) {
			if (other.returned != null)
				return false;
		} else if (!returned.equals(other.returned))
			return false;
		if (Double.doubleToLongBits(shipmentCost) != Double.doubleToLongBits(other.shipmentCost))
			return false;
		if (shipmentReference == null) {
			if (other.shipmentReference != null)
				return false;
		} else if (!shipmentReference.equals(other.shipmentReference))
			return false;
		if (status != other.status)
			return false;
		if (vendorId != other.vendorId)
			return false;
		return true;
	}
	
	
	
}
