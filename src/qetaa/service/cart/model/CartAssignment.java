package qetaa.service.cart.model;

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

@Table(name="crt_assignment")
@Entity
public class CartAssignment implements Serializable{

	private static final long serialVersionUID = 1L;
	@Id
	@SequenceGenerator(name = "crt_assignment_id_seq_gen", sequenceName = "crt_assignment_id_seq", initialValue=1, allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "crt_assignment_id_seq_gen")
	@Column(name="id")
	private long id;
	@Column(name="assigned_to")
	private Integer assignedTo;
	@Column(name="assigned_by")
	private Integer assignedBy;
	@Column(name="stage")
	private char stage;
	@Column(name="cart_id")
	private long cartId;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="assigned_date")
	private Date assignedDate;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="complete_date")
	private Date completedDate;
	@Column(name="status")
	private char status;//A = active, D = deactive, C = completed
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public Integer getAssignedTo() {
		return assignedTo;
	}
	public void setAssignedTo(Integer assignedTo) {
		this.assignedTo = assignedTo;
	}
	public Integer getAssignedBy() {
		return assignedBy;
	}
	public void setAssignedBy(Integer assignedBy) {
		this.assignedBy = assignedBy;
	}
	public char getStage() {
		return stage;
	}
	public void setStage(char stage) {
		this.stage = stage;
	}
	public long getCartId() {
		return cartId;
	}
	public void setCartId(long cartId) {
		this.cartId = cartId;
	}
	public Date getAssignedDate() {
		return assignedDate;
	}
	public void setAssignedDate(Date assignedDate) {
		this.assignedDate = assignedDate;
	}
	public Date getCompletedDate() {
		return completedDate;
	}
	public void setCompletedDate(Date completedDate) {
		this.completedDate = completedDate;
	}
	public char getStatus() {
		return status;
	}
	public void setStatus(char status) {
		this.status = status;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((assignedBy == null) ? 0 : assignedBy.hashCode());
		result = prime * result + ((assignedDate == null) ? 0 : assignedDate.hashCode());
		result = prime * result + ((assignedTo == null) ? 0 : assignedTo.hashCode());
		result = prime * result + (int) (cartId ^ (cartId >>> 32));
		result = prime * result + ((completedDate == null) ? 0 : completedDate.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + stage;
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
		CartAssignment other = (CartAssignment) obj;
		if (assignedBy == null) {
			if (other.assignedBy != null)
				return false;
		} else if (!assignedBy.equals(other.assignedBy))
			return false;
		if (assignedDate == null) {
			if (other.assignedDate != null)
				return false;
		} else if (!assignedDate.equals(other.assignedDate))
			return false;
		if (assignedTo == null) {
			if (other.assignedTo != null)
				return false;
		} else if (!assignedTo.equals(other.assignedTo))
			return false;
		if (cartId != other.cartId)
			return false;
		if (completedDate == null) {
			if (other.completedDate != null)
				return false;
		} else if (!completedDate.equals(other.completedDate))
			return false;
		if (id != other.id)
			return false;
		if (stage != other.stage)
			return false;
		if (status != other.status)
			return false;
		return true;
	}
	
	

	
}
