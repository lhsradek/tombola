package local.intranet.tombola.api.model.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import local.intranet.tombola.api.domain.DefaultFieldLengths;

/**
 * 
 * {@link Prize} is entity for JPA with
 * {@link local.intranet.tombola.api.model.repository.PrizeRepository}
 * 
 * @author Radek Kádner
 *
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "tombola_prize", indexes = {
		// @Index(name = "tombola_prize_pkey", columnList = "id")
		@Index(columnList = "id") }, uniqueConstraints = {
				// @UniqueConstraint(name = "tombola_prize_name_uk", columnNames = {
				// "prize_name" })
				@UniqueConstraint(columnNames = { "prize_name" }) })
@GenericGenerator(name = "PrizeGenerator", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
		@Parameter(name = "sequence_name", value = "PRIZE_SEQUENCE"), @Parameter(name = "initial_value", value = "1"),
		@Parameter(name = "increment_size", value = "1") })
public class Prize {

	/**
	 * 
	 * Constructor without parameter
	 * 
	 */
	public Prize() {
		this(null);
	}

	/**
	 * 
	 * Constructor with id
	 * 
	 * @param id Long
	 */
	public Prize(Long id) {
		this.setId(id);
		this.createdDate = new Date().getTime();
		this.modifiedDate = 0L;
	}

	/**
	 * 
	 * Constructor with prizeName and cnt
	 * 
	 * @param prizeName {@link String}
	 * @param cnt       int
	 */
	public Prize(String prizeName, int cnt) {
		this.prizeName = prizeName;
		this.cnt = cnt;
		this.issued = 0;
		// this.timestmp = new Date().getTime();
		Date date = new Date();
		this.createdDate = date.getTime();
		this.modifiedDate = date.getTime();
		this.createdBy = "admin";
		this.modifiedBy = "admin";
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Audited
	@NotNull
	@Column(name = "prize_name", nullable = false)
	@Size(max = DefaultFieldLengths.DEFAULT_NAME)
	private String prizeName;

	@Audited
	private int cnt;

	@Audited
	private int issued;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "tombola_prize_ticket", indexes = {
			// @Index(name = "tombola_prize_ticket_id", columnList = "ticket_id")
			@Index(columnList = "ticket_id") }, uniqueConstraints = {
					// @UniqueConstraint(name = "tombola_prize_ticket_uk", columnNames =
					// {"ticket_id"})
					@UniqueConstraint(columnNames = { "ticket_id" }) })
	private Set<Ticket> ticket = new HashSet<>();

	@Audited
	@Column(name = "created_date")
	@CreatedDate
	private Long createdDate;

	@Audited
	@Column(name = "modified_date")
	@LastModifiedDate
	private Long modifiedDate;

	@Audited
	@Column(name = "created_by")
	@CreatedBy
	private String createdBy;

	@Audited
	@Column(name = "modified_by")
	@LastModifiedBy
	private String modifiedBy;

	/**
	 * Get id
	 * 
	 * @return {@link Long}
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Set id
	 * 
	 * @param id {@link Long}
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * 
	 * Get prize name
	 * 
	 * @return the prizeName
	 */
	public String getPrizeName() {
		return prizeName;
	}

	/**
	 * 
	 * Set prize name
	 * 
	 * @param prizeName the priceName to set
	 */
	public void setPrizeName(String prizeName) {
		this.prizeName = prizeName;
	}

	/**
	 * 
	 * Get cnt
	 * 
	 * @return the cnt int
	 */
	public int getCnt() {
		return cnt;
	}

	/**
	 * 
	 * Set cnt
	 * 
	 * @param cnt the cnt to set
	 */
	public void setCnt(int cnt) {
		this.cnt = cnt;
	}

	/**
	 * 
	 * Get issued int
	 * 
	 * @return the issued
	 */
	public int getIssued() {
		return issued;
	}

	/**
	 * 
	 * Set issued int
	 * 
	 * @param issued the issued to set
	 */
	public void setIssued(int issued) {
		this.issued = issued;
	}

	/**
	 * 
	 * Get ticket
	 * 
	 * @return the {@link Set}&lt;{@link Ticket}&gt;
	 */
	public Set<Ticket> getTicket() {
		return ticket;
	}

	/**
	 * 
	 * Set ticket
	 * 
	 * @param ticket {@link Set}&lt;{@link Ticket}&gt;
	 */
	public void setTicket(Set<Ticket> ticket) {
		this.ticket = ticket;
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
	 * Set createDate
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
		return "Prize [id=" + id + ", prizeName=" + prizeName + ", cnt=" + cnt + ", issued=" + issued + ", ticket="
				+ ticket + ", createdDate=" + createdDate + ", modifiedDate=" + modifiedDate + ", createdBy="
				+ createdBy + ", modifiedBy=" + modifiedBy + "]";
	}

}
