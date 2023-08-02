package qetaa.service.cart.apicontract;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Table(name = "crt_review")
@Entity
public class PublicCartReview implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id")
	private long id;
	@Column(name = "cart_id")
	private long cartId;
	@Column(name = "created")
	@Temporal(TemporalType.TIMESTAMP)
	private Date created;
	@Column(name = "review_text")
	private String reviewText;
	@Column(name = "visible_to_customer")
	@JsonIgnore
	private boolean visibleToCustomer;
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
	public String getReviewText() {
		return reviewText;
	}
	public void setReviewText(String reviewText) {
		this.reviewText = reviewText;
	}
	public boolean isVisibleToCustomer() {
		return visibleToCustomer;
	}
	public void setVisibleToCustomer(boolean visibleToCustomer) {
		this.visibleToCustomer = visibleToCustomer;
	}

	
	

}
