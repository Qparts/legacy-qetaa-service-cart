package qetaa.service.cart.model;

import java.io.Serializable;
import java.util.Date;
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

@Table(name="crt_review")
@Entity
public class CartReview implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@SequenceGenerator(name = "crt_review_id_seq_gen", sequenceName = "crt_review_id_seq", initialValue=1, allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "crt_review_id_seq_gen")
	@Column(name="id")
	private long id;
	@Column(name="cart_id")
	private long cartId;
	@Column(name="reviewer_id")
	private int reviewerId;
	@Column(name="created")
	@Temporal(TemporalType.TIMESTAMP)
	private Date created;
	@Column(name="status")
	private char status;
	@Column(name="review_text")
	private String reviewText;
	@Column(name="action_value")
	private char actionValue;
	@Column(name="cart_price")
	private Double cartPrice;
	@Column(name="alternative_price")
	private Double alternativePrice;
	@Column(name="bought_from")
	private String boughtFrom;
	@Column(name="bought_city")
	private Integer boughtCity;
	@Temporal(TemporalType.DATE)
	@Column(name="reminder_date")
	private Date reminderDate;
	@Column(name="stage")
	private Integer stage;//1 = no vin, 2 = quotation, 3 = follow up, 4 = wire transfer, 5 = open archived or closed
	
	@Transient
	private Map<String,Object> reviewer;
	
	

	
	
	public Integer getStage() {
		return stage;
	}
	public void setStage(Integer stage) {
		this.stage = stage;
	}
	public Map<String, Object> getReviewer() {
		return reviewer;
	}
	public void setReviewer(Map<String, Object> reviewer) {
		this.reviewer = reviewer;
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
	public int getReviewerId() {
		return reviewerId;
	}
	public void setReviewerId(int reviewerId) {
		this.reviewerId = reviewerId;
	}
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	public char getStatus() {
		return status;
	}
	public void setStatus(char status) {
		this.status = status;
	}
	public String getReviewText() {
		return reviewText;
	}
	public void setReviewText(String reviewText) {
		this.reviewText = reviewText;
	}
	public char getActionValue() {
		return actionValue;
	}
	public void setActionValue(char actionValue) {
		this.actionValue = actionValue;
	}
	public Double getCartPrice() {
		return cartPrice;
	}
	public void setCartPrice(Double cartPrice) {
		this.cartPrice = cartPrice;
	}
	public Double getAlternativePrice() {
		return alternativePrice;
	}
	public void setAlternativePrice(Double alternativePrice) {
		this.alternativePrice = alternativePrice;
	}
	public String getBoughtFrom() {
		return boughtFrom;
	}
	public void setBoughtFrom(String boughtFrom) {
		this.boughtFrom = boughtFrom;
	}
	public Integer getBoughtCity() {
		return boughtCity;
	}
	public void setBoughtCity(Integer boughtCity) {
		this.boughtCity = boughtCity;
	}
	public Date getReminderDate() {
		return reminderDate;
	}
	public void setReminderDate(Date reminderDate) {
		this.reminderDate = reminderDate;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + actionValue;
		result = prime * result + ((alternativePrice == null) ? 0 : alternativePrice.hashCode());
		result = prime * result + ((boughtCity == null) ? 0 : boughtCity.hashCode());
		result = prime * result + ((boughtFrom == null) ? 0 : boughtFrom.hashCode());
		result = prime * result + (int) (cartId ^ (cartId >>> 32));
		result = prime * result + ((cartPrice == null) ? 0 : cartPrice.hashCode());
		result = prime * result + ((created == null) ? 0 : created.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((reminderDate == null) ? 0 : reminderDate.hashCode());
		result = prime * result + ((reviewText == null) ? 0 : reviewText.hashCode());
		result = prime * result + reviewerId;
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
		CartReview other = (CartReview) obj;
		if (actionValue != other.actionValue)
			return false;
		if (alternativePrice == null) {
			if (other.alternativePrice != null)
				return false;
		} else if (!alternativePrice.equals(other.alternativePrice))
			return false;
		if (boughtCity == null) {
			if (other.boughtCity != null)
				return false;
		} else if (!boughtCity.equals(other.boughtCity))
			return false;
		if (boughtFrom == null) {
			if (other.boughtFrom != null)
				return false;
		} else if (!boughtFrom.equals(other.boughtFrom))
			return false;
		if (cartId != other.cartId)
			return false;
		if (cartPrice == null) {
			if (other.cartPrice != null)
				return false;
		} else if (!cartPrice.equals(other.cartPrice))
			return false;
		if (created == null) {
			if (other.created != null)
				return false;
		} else if (!created.equals(other.created))
			return false;
		if (id != other.id)
			return false;
		if (reminderDate == null) {
			if (other.reminderDate != null)
				return false;
		} else if (!reminderDate.equals(other.reminderDate))
			return false;
		if (reviewText == null) {
			if (other.reviewText != null)
				return false;
		} else if (!reviewText.equals(other.reviewText))
			return false;
		if (reviewerId != other.reviewerId)
			return false;
		if (status != other.status)
			return false;
		return true;
	}
	
	
	
}
