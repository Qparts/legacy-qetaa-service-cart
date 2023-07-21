package qetaa.service.cart.model.parts;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="crt_parts_item")
public class PartsOrderItem implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "crt_parts_item_id_seq_gen", sequenceName = "crt_parts_item_id_seq", initialValue=1, allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "crt_parts_item_id_seq_gen")
	@Column(name="id")
	private long id;
	@Column(name="quotation_id")
	private long quotationId;
	@Column(name="cart_id")
	private long cartId;
	@Column(name="parts_order_id")
	private long partsOrderId;
	@Column(name="quotation_item_id")
	private long quotationItemId;
	@Column(name="ordered_quantity")
	private int orderedQuantity;
	@Column(name="status")
	private char status;
	@Column(name="sales_price")
	private double salesPrice;
	@Column(name="product_id")
	private Long productId;
	
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getQuotationId() {
		return quotationId;
	}
	public void setQuotationId(long quotationId) {
		this.quotationId = quotationId;
	}
	public long getPartsOrderId() {
		return partsOrderId;
	}
	public void setPartsOrderId(long partsOrderId) {
		this.partsOrderId = partsOrderId;
	}
	public long getQuotationItemId() {
		return quotationItemId;
	}
	public void setQuotationItemId(long quotationItemId) {
		this.quotationItemId = quotationItemId;
	}
	public int getOrderedQuantity() {
		return orderedQuantity;
	}
	public void setOrderedQuantity(int orderedQuantity) {
		this.orderedQuantity = orderedQuantity;
	}
	public char getStatus() {
		return status;
	}
	public void setStatus(char status) {
		this.status = status;
	}
	public double getSalesPrice() {
		return salesPrice;
	}
	public void setSalesPrice(double salesPrice) {
		this.salesPrice = salesPrice;
	}
	
	
	
	
	
	public Long getProductId() {
		return productId;
	}
	public void setProductId(Long productId) {
		this.productId = productId;
	}
	public long getCartId() {
		return cartId;
	}
	public void setCartId(long cartId) {
		this.cartId = cartId;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + orderedQuantity;
		result = prime * result + (int) (partsOrderId ^ (partsOrderId >>> 32));
		result = prime * result + (int) (quotationId ^ (quotationId >>> 32));
		result = prime * result + (int) (quotationItemId ^ (quotationItemId >>> 32));
		long temp;
		temp = Double.doubleToLongBits(salesPrice);
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
		PartsOrderItem other = (PartsOrderItem) obj;
		if (id != other.id)
			return false;
		if (orderedQuantity != other.orderedQuantity)
			return false;
		if (partsOrderId != other.partsOrderId)
			return false;
		if (quotationId != other.quotationId)
			return false;
		if (quotationItemId != other.quotationItemId)
			return false;
		if (Double.doubleToLongBits(salesPrice) != Double.doubleToLongBits(other.salesPrice))
			return false;
		if (status != other.status)
			return false;
		return true;
	}
	
	
}
