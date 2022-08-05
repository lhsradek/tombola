package local.intranet.tombola.api.model.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * 
 * {@link Ticket} is entity for CRUD with
 * {@link local.intranet.tombola.api.model.repository.TicketRepository}
 * <p>
 * https://www.baeldung.com/database-auditing-jpa
 * 
 * @author Radek Kádner
 *
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
@Audited
@Table(name = "tombola_ticket", indexes = {
		// @Index(name = "tombola_los_pkey", columnList = "id")
		@Index(columnList = "id") })
@GenericGenerator(name = "TicketGenerator", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
		@Parameter(name = "sequence_name", value = "TICKET_SEQUENCE"), @Parameter(name = "initial_value", value = "1"),
		@Parameter(name = "increment_size", value = "1") })
public class Ticket {

	/**
	 * 
	 * Constructor without parameter
	 * 
	 */
	public Ticket() {
		this(null);
	}

	/**
	 * 
	 * Constructor with id
	 * 
	 * @param id {@link Long}
	 */
	public Ticket(Long id) {
		this.setId(id);
		this.win = 0L;
		this.createdDate = new Date().getTime();
		this.modifiedDate = 0L;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long win;

	@Column(name = "created_date")
	@CreatedDate
	private Long createdDate;

	@Column(name = "modified_date")
	@LastModifiedDate
	private Long modifiedDate;

	@Column(name = "created_by")
	@CreatedBy
	private String createdBy;

	@Column(name = "modified_by")
	@LastModifiedBy
	private String modifiedBy;

	/**
	 * 
	 * Get id
	 * 
	 * @return {@link Long}
	 */
	public Long getId() {
		return id;
	}

	/**
	 * 
	 * Set id
	 * 
	 * @param id {@link Long}
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * 
	 * Get win
	 * 
	 * @return the win
	 */
	public Long getWin() {
		return win;
	}

	/**
	 * 
	 * Set win
	 * 
	 * @param win the win to set
	 */
	public void setWin(Long win) {
		this.win = win;
	}

	/**
	 * 
	 * Get createdDate
	 * 
	 * @return the createdDate
	 */
	public Long getCreatedDate() {
		return createdDate;
	}

	/**
	 * 
	 * Set createdDate
	 * 
	 * @param createdDate the createdDate to set
	 */
	public void setCreatedDate(Long createdDate) {
		this.createdDate = createdDate;
	}

	/**
	 * 
	 * Get modifiedDate
	 * 
	 * @return the modifiedDate
	 */
	public Long getModifiedDate() {
		return modifiedDate;
	}

	/**
	 * 
	 * Set modifiedDate
	 * 
	 * @param modifiedDate the modifiedDate to set
	 */
	public void setModifiedDate(Long modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	/**
	 * 
	 * Get createdBy
	 * 
	 * @return the createdBy
	 */
	public String getCreatedBy() {
		return createdBy;
	}

	/**
	 * 
	 * Set createdBy
	 * 
	 * @param createdBy the createdBy to set
	 */
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	/**
	 * 
	 * Get modifiedBy
	 * 
	 * @return the modifiedBy
	 */
	public String getModifiedBy() {
		return modifiedBy;
	}

	/**
	 * 
	 * Set modifiedBy
	 * 
	 * @param modifiedBy the modifiedBy to set
	 */
	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	/**
	 * 
	 * Returns a string representation of the object.
	 */
	@Override
	public String toString() {
		return "Ticket [id=" + id + ", win=" + win + ", createdDate=" + createdDate + ", modifiedDate=" + modifiedDate
				+ ", createdBy=" + createdBy + ", modifiedBy=" + modifiedBy + "]";
	}

}
