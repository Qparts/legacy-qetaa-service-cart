package qetaa.service.cart.model.parts.contract;

import java.util.Date;
import java.util.Map;

public class PartCollectionItem {

	private long partsApprovedId;
	private Integer vendorId;
	private long cartId;
	private int quantity;
	private double unitCost;
	private String itemDesc;
	private String itemNumber;
	
	private Date quoted;
	private Integer quotedBy;
	private Map<String, Object> quotedByObject;
	
	private Date collected;
	private Integer collectedBy;
	private Map<String, Object> collectedByObject;
	
	private Date received;
	private Integer receivedBy;
	private Map<String, Object> receivedByObject;
	
	private Integer preparedBy;
	private Date prepared;
	private Map<String, Object> preparedByObject;
	
	private char status;

	public char getStatus() {
		return status;
	}

	public void setStatus(char status) {
		this.status = status;
	}

	public Map<String, Object> getQuotedByObject() {
		return quotedByObject;
	}

	public void setQuotedByObject(Map<String, Object> quotedByObject) {
		this.quotedByObject = quotedByObject;
	}

	public Integer getPreparedBy() {
		return preparedBy;
	}

	public void setPreparedBy(Integer preparedBy) {
		this.preparedBy = preparedBy;
	}

	public Date getPrepared() {
		return prepared;
	}

	public void setPrepared(Date prepared) {
		this.prepared = prepared;
	}
	
	public Integer getVendorId() {
		return vendorId;
	}

	public void setVendorId(Integer vendorId) {
		this.vendorId = vendorId;
	}

	public Long getCartId() {
		return cartId;
	}

	public void setCartId(long cartId) {
		this.cartId = cartId;
	}

	public Date getCollected() {
		return collected;
	}

	public void setCollected(Date collected) {
		this.collected = collected;
	}

	public Integer getCollectedBy() {
		return collectedBy;
	}

	public void setCollectedBy(Integer collectedBy) {
		this.collectedBy = collectedBy;
	}

	public Date getReceived() {
		return received;
	}

	public void setReceived(Date received) {
		this.received = received;
	}

	public Integer getReceivedBy() {
		return receivedBy;
	}

	public void setReceivedBy(Integer receivedBy) {
		this.receivedBy = receivedBy;
	}

	public long getPartsApprovedId() {
		return partsApprovedId;
	}

	public void setPartsApprovedId(long partsApprovedId) {
		this.partsApprovedId = partsApprovedId;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public double getUnitCost() {
		return unitCost;
	}

	public void setUnitCost(double unitCost) {
		this.unitCost = unitCost;
	}

	public String getItemDesc() {
		return itemDesc;
	}

	public void setItemDesc(String itemDesc) {
		this.itemDesc = itemDesc;
	}

	public String getItemNumber() {
		return itemNumber;
	}

	public void setItemNumber(String itemNumber) {
		this.itemNumber = itemNumber;
	}

	public Date getQuoted() {
		return quoted;
	}

	public void setQuoted(Date quoted) {
		this.quoted = quoted;
	}

	public Integer getQuotedBy() {
		return quotedBy;
	}

	public void setQuotedBy(Integer quotedBy) {
		this.quotedBy = quotedBy;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (cartId ^ (cartId >>> 32));
		result = prime * result + ((collected == null) ? 0 : collected.hashCode());
		result = prime * result + ((collectedBy == null) ? 0 : collectedBy.hashCode());
		result = prime * result + ((itemDesc == null) ? 0 : itemDesc.hashCode());
		result = prime * result + ((itemNumber == null) ? 0 : itemNumber.hashCode());
		result = prime * result + (int) (partsApprovedId ^ (partsApprovedId >>> 32));
		result = prime * result + quantity;
		result = prime * result + ((quoted == null) ? 0 : quoted.hashCode());
		result = prime * result + quotedBy;
		result = prime * result + ((received == null) ? 0 : received.hashCode());
		result = prime * result + ((receivedBy == null) ? 0 : receivedBy.hashCode());
		long temp;
		temp = Double.doubleToLongBits(unitCost);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((vendorId == null) ? 0 : vendorId.hashCode());
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
		PartCollectionItem other = (PartCollectionItem) obj;
		if (cartId != other.cartId)
			return false;
		if (collected == null) {
			if (other.collected != null)
				return false;
		} else if (!collected.equals(other.collected))
			return false;
		if (collectedBy == null) {
			if (other.collectedBy != null)
				return false;
		} else if (!collectedBy.equals(other.collectedBy))
			return false;
		if (itemDesc == null) {
			if (other.itemDesc != null)
				return false;
		} else if (!itemDesc.equals(other.itemDesc))
			return false;
		if (itemNumber == null) {
			if (other.itemNumber != null)
				return false;
		} else if (!itemNumber.equals(other.itemNumber))
			return false;
		if (partsApprovedId != other.partsApprovedId)
			return false;
		if (quantity != other.quantity)
			return false;
		if (quoted == null) {
			if (other.quoted != null)
				return false;
		} else if (!quoted.equals(other.quoted))
			return false;
		if (quotedBy != other.quotedBy)
			return false;
		if (received == null) {
			if (other.received != null)
				return false;
		} else if (!received.equals(other.received))
			return false;
		if (receivedBy == null) {
			if (other.receivedBy != null)
				return false;
		} else if (!receivedBy.equals(other.receivedBy))
			return false;
		if (Double.doubleToLongBits(unitCost) != Double.doubleToLongBits(other.unitCost))
			return false;
		if (vendorId == null) {
			if (other.vendorId != null)
				return false;
		} else if (!vendorId.equals(other.vendorId))
			return false;
		return true;
	}

	public Map<String, Object> getCollectedByObject() {
		return collectedByObject;
	}

	public void setCollectedByObject(Map<String, Object> collectedByObject) {
		this.collectedByObject = collectedByObject;
	}

	public Map<String, Object> getReceivedByObject() {
		return receivedByObject;
	}

	public void setReceivedByObject(Map<String, Object> receivedByObject) {
		this.receivedByObject = receivedByObject;
	}

	public Map<String, Object> getPreparedByObject() {
		return preparedByObject;
	}

	public void setPreparedByObject(Map<String, Object> preparedByObject) {
		this.preparedByObject = preparedByObject;
	}
	
	

}
