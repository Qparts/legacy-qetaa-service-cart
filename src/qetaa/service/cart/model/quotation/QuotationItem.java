package qetaa.service.cart.model.quotation;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

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
@Table(name = "crt_quotation_item")
public class QuotationItem implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@SequenceGenerator(name = "crt_quotation_item_id_seq_gen", sequenceName = "crt_quotation_item_id_seq", initialValue = 1, allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "crt_quotation_item_id_seq_gen")
	@Column(name="id")
	private long id;	
	@Column(name="cart_id")
	private long cartId;
	@Column(name="quotation_id")
	private long quotationId;
	@Column(name="quantity")
	private int quantity;
	@Column(name="created_by")
	private int createdBy;
	@Column(name="item_desc")
	private String itemDesc;
	@Column(name="item_desc_ar")
	private String itemDescAr;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="created")
	private Date created;
	@Column(name="status")
	private char status;
	@Transient
	private List<QuotationVendorItem> vendorItems;
	
	
	
	
	
	public char getStatus() {
		return status;
	}
	public void setStatus(char status) {
		this.status = status;
	}
	public List<QuotationVendorItem> getVendorItems() {
		return vendorItems;
	}
	public void setVendorItems(List<QuotationVendorItem> vendorItems) {
		this.vendorItems = vendorItems;
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
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public String getItemDesc() {
		return itemDesc;
	}
	public void setItemDesc(String itemDesc) {
		this.itemDesc = itemDesc;
	}
	public String getItemDescAr() {
		return itemDescAr;
	}
	public void setItemDescAr(String itemDescAr) {
		this.itemDescAr = itemDescAr;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (cartId ^ (cartId >>> 32));
		result = prime * result + ((created == null) ? 0 : created.hashCode());
		result = prime * result + createdBy;
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((itemDesc == null) ? 0 : itemDesc.hashCode());
		result = prime * result + ((itemDescAr == null) ? 0 : itemDescAr.hashCode());
		result = prime * result + quantity;
		result = prime * result + (int) (quotationId ^ (quotationId >>> 32));
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
		QuotationItem other = (QuotationItem) obj;
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
		if (itemDesc == null) {
			if (other.itemDesc != null)
				return false;
		} else if (!itemDesc.equals(other.itemDesc))
			return false;
		if (itemDescAr == null) {
			if (other.itemDescAr != null)
				return false;
		} else if (!itemDescAr.equals(other.itemDescAr))
			return false;
		if (quantity != other.quantity)
			return false;
		if (quotationId != other.quotationId)
			return false;
		return true;
	}

	
}
