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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="crt_quotation_item_approved")
public class QuotationItemApproved implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@SequenceGenerator(name = "crt_quotation_item_approved_id_seq_gen", sequenceName = "crt_quotation_item_approved_id_seq", initialValue=1, allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "crt_quotation_item_approved_id_seq_gen")
	@Column(name="id")
	private long id;
	@Column(name="cart_id")
	private long cartId;
	@Column(name="quotation_id")
	private long quotationId;
	@Column(name="vendor_item_id")
	private long vendorItemId;
	@Column(name="quotation_item_id")
	private long quotationItemId;
	@Column(name="quantity")
	private int quantity;
	@Column(name="created_by")
	private int createdBy;
	@Column(name="created")
	@Temporal(TemporalType.TIMESTAMP)
	private Date created;
	@Column(name="unit_cost")
	private double unitCost;
	@Column(name="sales_percentage")
	private double percentage;
	@Column(name="product_id")
	private Long productId;
	
	
	
	
	public Long getProductId() {
		return productId;
	}
	public void setProductId(Long productId) {
		this.productId = productId;
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
	public long getQuotationId() {
		return quotationId;
	}
	public void setQuotationId(long quotationId) {
		this.quotationId = quotationId;
	}
	public long getVendorItemId() {
		return vendorItemId;
	}
	public void setVendorItemId(long vendorItemId) {
		this.vendorItemId = vendorItemId;
	}
	public long getQuotationItemId() {
		return quotationItemId;
	}
	public void setQuotationItemId(long quotationItemId) {
		this.quotationItemId = quotationItemId;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public int getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(int createdBy) {
		this.createdBy = createdBy;
	}
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	public double getUnitCost() {
		return unitCost;
	}
	public void setUnitCost(double unitCost) {
		this.unitCost = unitCost;
	}
	public double getPercentage() {
		return percentage;
	}
	public void setPercentage(double percentage) {
		this.percentage = percentage;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (cartId ^ (cartId >>> 32));
		result = prime * result + ((created == null) ? 0 : created.hashCode());
		result = prime * result + createdBy;
		result = prime * result + (int) (id ^ (id >>> 32));
		long temp;
		temp = Double.doubleToLongBits(percentage);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + quantity;
		result = prime * result + (int) (quotationId ^ (quotationId >>> 32));
		result = prime * result + (int) (quotationItemId ^ (quotationItemId >>> 32));
		temp = Double.doubleToLongBits(unitCost);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + (int) (vendorItemId ^ (vendorItemId >>> 32));
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
		QuotationItemApproved other = (QuotationItemApproved) obj;
		if (cartId != other.cartId)
			return false;
		if (created == null) {
			if (other.created != null)
				return false;
		} else if (!created.equals(other.created))
			return false;
		if (createdBy != other.createdBy)
			return false;
		if (id != other.id)
			return false;
		if (Double.doubleToLongBits(percentage) != Double.doubleToLongBits(other.percentage))
			return false;
		if (quantity != other.quantity)
			return false;
		if (quotationId != other.quotationId)
			return false;
		if (quotationItemId != other.quotationItemId)
			return false;
		if (Double.doubleToLongBits(unitCost) != Double.doubleToLongBits(other.unitCost))
			return false;
		if (vendorItemId != other.vendorItemId)
			return false;
		return true;
	}
	
	


	
}
