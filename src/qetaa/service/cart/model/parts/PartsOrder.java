package qetaa.service.cart.model.parts;

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

import com.fasterxml.jackson.annotation.JsonIgnore;
 
@Table(name="crt_parts_order")
@Entity
public class PartsOrder implements Serializable{

	private static final long serialVersionUID = 1L;
	@Id
	@SequenceGenerator(name = "crt_parts_order_id_seq_gen", sequenceName = "crt_parts_order_id_seq", initialValue=1, allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "crt_parts_order_id_seq_gen")
	@Column(name="id")
	private long id;
	@Column(name="cart_id")
	private long cartId;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="created")
	private Date created;
	@Column(name="app_code")
	private int appCode;
	@Column(name="address_id")
	private long addressId;
	@Column(name="status")
	private char status;//W = waiting, C = all items collected, R = ready for shipment, S = items shipped
	@Column(name="payment_id")
	private long paymentId;
	@Column(name="sales_amount")
	private double salesAmount;
	@Column(name="cost_amount")
	private double costAmount;
	@Column(name="shipment_reference")
	private String shipmentReference;
	@Column(name="shipment_cost")
	private Double shipmentCost;
	@Column(name="courrier_name")
	private String courrierName;
	@Column(name="shipped")
	@Temporal(TemporalType.TIMESTAMP)
	private Date shipped;
	@Column(name="shipped_by")
	private Integer shippedBy;
	
	@Transient
	private List<PartsOrderItem> partsItems; 
	
	@Transient
	private Map<String, Object> address;
	
	
	@JsonIgnore
	public double getTotalPartsPrice(){
		double total  = 0;
		for(PartsOrderItem poi : this.partsItems){
			total = total + (poi.getOrderedQuantity() * poi.getSalesPrice());
		}
		return total;
	}
	
	public String getShipmentReference() {
		return shipmentReference;
	}

	public Date getShipped() {
		return shipped;
	}

	public void setShipped(Date shipped) {
		this.shipped = shipped;
	}

	public Integer getShippedBy() {
		return shippedBy;
	}

	public void setShippedBy(Integer shippedBy) {
		this.shippedBy = shippedBy;
	}

	public void setShipmentCost(Double shipmentCost) {
		this.shipmentCost = shipmentCost;
	}

	public void setShipmentReference(String shipmentReference) {
		this.shipmentReference = shipmentReference;
	}


	public String getCourrierName() {
		return courrierName;
	}




	public void setCourrierName(String courrierName) {
		this.courrierName = courrierName;
	}




	public Double getShipmentCost() {
		return shipmentCost;
	}

	public List<PartsOrderItem> getPartsItems() {
		return partsItems;
	}
	public void setPartsItems(List<PartsOrderItem> partsItems) {
		this.partsItems = partsItems;
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
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	public int getAppCode() {
		return appCode;
	}
	public void setAppCode(int appCode) {
		this.appCode = appCode;
	}
	public long getAddressId() {
		return addressId;
	}
	public void setAddressId(long addressId) {
		this.addressId = addressId;
	}
	public char getStatus() {
		return status;
	}
	public void setStatus(char status) {
		this.status = status;
	}
	public long getPaymentId() {
		return paymentId;
	}
	public void setPaymentId(long paymentId) {
		this.paymentId = paymentId;
	}
	public double getSalesAmount() {
		return salesAmount;
	}
	public void setSalesAmount(double salesAmount) {
		this.salesAmount = salesAmount;
	}
	public double getCostAmount() {
		return costAmount;
	}
	public void setCostAmount(double costAmount) {
		this.costAmount = costAmount;
	}
	
	
	public Map<String, Object> getAddress() {
		return address;
	}


	public void setAddress(Map<String, Object> address) {
		this.address = address;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (addressId ^ (addressId >>> 32));
		result = prime * result + appCode;
		result = prime * result + (int) (cartId ^ (cartId >>> 32));
		long temp;
		temp = Double.doubleToLongBits(costAmount);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((created == null) ? 0 : created.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((partsItems == null) ? 0 : partsItems.hashCode());
		result = prime * result + (int) (paymentId ^ (paymentId >>> 32));
		temp = Double.doubleToLongBits(salesAmount);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + status;
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
		PartsOrder other = (PartsOrder) obj;
		if (addressId != other.addressId)
			return false;
		if (appCode != other.appCode)
			return false;
		if (cartId != other.cartId)
			return false;
		if (Double.doubleToLongBits(costAmount) != Double.doubleToLongBits(other.costAmount))
			return false;
		if (created == null) {
			if (other.created != null)
				return false;
		} else if (!created.equals(other.created))
			return false;
		if (id != other.id)
			return false;
		if (partsItems == null) {
			if (other.partsItems != null)
				return false;
		} else if (!partsItems.equals(other.partsItems))
			return false;
		if (paymentId != other.paymentId)
			return false;
		if (Double.doubleToLongBits(salesAmount) != Double.doubleToLongBits(other.salesAmount))
			return false;
		if (status != other.status)
			return false;
		return true;
	}
	
	
	
}
