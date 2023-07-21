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
@Table(name="crt_quotation_vendor_item")
public class QuotationVendorItem implements Serializable{

	private static final long serialVersionUID = 1L;
	@Id
	@SequenceGenerator(name = "crt_quotation_vendor_item_id_seq_gen", sequenceName = "crt_quotation_vendor_item_id_seq", initialValue=1, allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "crt_quotation_vendor_item_id_seq_gen")
	@Column(name="id")
	private long id;
	@Column(name="cart_id")
	private long cartId;
	@Column(name="quotation_id")
	private long quotationId;
	@Column(name="quotation_item_id")
	private long quotationItemId;
	@Column(name="vendor_id")
	private Integer vendorId;
	@Column(name="quantity")
	private int quantity;
	@Column(name="item_number")
	private String itemNumber;
	@Column(name="item_desc")
	private String itemDesc;
	@Column(name="item_desc_ar")
	private String itemDescAr;
	@Column(name="item_cost_price")
	private Double itemCostPrice;
	@Column(name="sales_percentage")
	private Double salesPercentage;
	@Column(name="created")
	@Temporal(TemporalType.TIMESTAMP)
	private Date created;
	@Column(name="responded")
	@Temporal(TemporalType.TIMESTAMP)
	private Date responded;
	@Column(name="created_by")
	private Integer createdBy;
	@Column(name="responded_by")
	private Integer respondedBy;
	@Column(name="responded_by_finder")
	private Integer respondedByFinder;
	@Column(name="finder_id")
	private Integer finderId;
	@Column(name="sent_to")
	private Character sentTo;
	@Column(name="status")
	private char status;//F = waiting for finder, W = Waiting for vendor, C = Completed, N = Not available
	@Column(name="product_id")
	private Long productId;
	
	public Integer getFinderId() {
		return finderId;
	}
	public void setFinderId(Integer finderId) {
		this.finderId = finderId;
	}
	public Character getSentTo() {
		return sentTo;
	}
	public void setSentTo(Character sentTo) {
		this.sentTo = sentTo;
	}
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
	public long getQuotationItemId() {
		return quotationItemId;
	}
	public void setQuotationItemId(long quotationItemId) {
		this.quotationItemId = quotationItemId;
	}
	
	public Integer getVendorId() {
		return vendorId;
	}
	public void setVendorId(Integer vendorId) {
		this.vendorId = vendorId;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public String getItemNumber() {
		return itemNumber;
	}
	public void setItemNumber(String itemNumber) {
		this.itemNumber = itemNumber;
	}
	public Double getItemCostPrice() {
		return itemCostPrice;
	}
	public void setItemCostPrice(Double itemCostPrice) {
		this.itemCostPrice = itemCostPrice;
	}
	public Double getSalesPercentage() {
		return salesPercentage;
	}
	public void setSalesPercentage(Double salesPercentage) {
		this.salesPercentage = salesPercentage;
	}
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	public Date getResponded() {
		return responded;
	}
	public void setResponded(Date responded) {
		this.responded = responded;
	}
	public Integer getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(Integer createdBy) {
		this.createdBy = createdBy;
	}
	public Integer getRespondedBy() {
		return respondedBy;
	}
	public void setRespondedBy(Integer respondedBy) {
		this.respondedBy = respondedBy;
	}
	
	public char getStatus() {
		return status;
	}
	public void setStatus(char status) {
		this.status = status;
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
	
	public Integer getRespondedByFinder() {
		return respondedByFinder;
	}
	public void setRespondedByFinder(Integer respondedByFinder) {
		this.respondedByFinder = respondedByFinder;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (cartId ^ (cartId >>> 32));
		result = prime * result + ((created == null) ? 0 : created.hashCode());
		result = prime * result + ((createdBy == null) ? 0 : createdBy.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((itemCostPrice == null) ? 0 : itemCostPrice.hashCode());
		result = prime * result + ((itemDesc == null) ? 0 : itemDesc.hashCode());
		result = prime * result + ((itemDescAr == null) ? 0 : itemDescAr.hashCode());
		result = prime * result + ((itemNumber == null) ? 0 : itemNumber.hashCode());
		result = prime * result + ((productId == null) ? 0 : productId.hashCode());
		result = prime * result + quantity;
		result = prime * result + (int) (quotationId ^ (quotationId >>> 32));
		result = prime * result + (int) (quotationItemId ^ (quotationItemId >>> 32));
		result = prime * result + ((responded == null) ? 0 : responded.hashCode());
		result = prime * result + ((respondedBy == null) ? 0 : respondedBy.hashCode());
		result = prime * result + ((respondedByFinder == null) ? 0 : respondedByFinder.hashCode());
		result = prime * result + ((salesPercentage == null) ? 0 : salesPercentage.hashCode());
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
		QuotationVendorItem other = (QuotationVendorItem) obj;
		if (cartId != other.cartId)
			return false;
		if (created == null) {
			if (other.created != null)
				return false;
		} else if (!created.equals(other.created))
			return false;
		if (createdBy == null) {
			if (other.createdBy != null)
				return false;
		} else if (!createdBy.equals(other.createdBy))
			return false;
		if (id != other.id)
			return false;
		if (itemCostPrice == null) {
			if (other.itemCostPrice != null)
				return false;
		} else if (!itemCostPrice.equals(other.itemCostPrice))
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
		if (itemNumber == null) {
			if (other.itemNumber != null)
				return false;
		} else if (!itemNumber.equals(other.itemNumber))
			return false;
		if (productId == null) {
			if (other.productId != null)
				return false;
		} else if (!productId.equals(other.productId))
			return false;
		if (quantity != other.quantity)
			return false;
		if (quotationId != other.quotationId)
			return false;
		if (quotationItemId != other.quotationItemId)
			return false;
		if (responded == null) {
			if (other.responded != null)
				return false;
		} else if (!responded.equals(other.responded))
			return false;
		if (respondedBy == null) {
			if (other.respondedBy != null)
				return false;
		} else if (!respondedBy.equals(other.respondedBy))
			return false;
		if (respondedByFinder == null) {
			if (other.respondedByFinder != null)
				return false;
		} else if (!respondedByFinder.equals(other.respondedByFinder))
			return false;
		if (salesPercentage == null) {
			if (other.salesPercentage != null)
				return false;
		} else if (!salesPercentage.equals(other.salesPercentage))
			return false;
		if (status != other.status)
			return false;
		if (vendorId != other.vendorId)
			return false;
		return true;
	}

	
	
	
}
