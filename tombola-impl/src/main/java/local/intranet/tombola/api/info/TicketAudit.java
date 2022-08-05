package local.intranet.tombola.api.info;

import java.util.Date;

import org.hibernate.envers.RevisionType;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * 
 * {@link TicketAudit} for
 * {@link local.intranet.tombola.api.service.TombolaService#getTicket}
 * 
 * @author radek.kadner
 *
 */
@JsonPropertyOrder({ "id", "win", "date", "revisionNumber", "revisionType" })
public class TicketAudit extends TicketInfo {

	private final Integer revisionNumber;
	private final RevisionType revisionType;

	/**
	 * 
	 * Constructor with parameters
	 * 
	 * @param id             long
	 * @param win            long
	 * @param date           {@link Date}
	 * @param revisionNumber {@link Integer}
	 * @param revisionType   {@link RevisionType}
	 */
	public TicketAudit(long id, long win, Date date, Integer revisionNumber, RevisionType revisionType) {
		super(id, win, date);
		this.revisionNumber = revisionNumber;
		this.revisionType = revisionType;
	}

	/**
	 * 
	 * Get revision number
	 * 
	 * @return {@link Integer}
	 */
	public Integer getRevisionNumber() {
		return revisionNumber;
	}

	/**
	 * 
	 * Get revision type
	 * 
	 * @return {@link RevisionType}
	 */
	public RevisionType getRevisionType() {
		return revisionType;
	}

	/**
	 * 
	 * Returns a string representation of the object.
	 */
	@Override
	public String toString() {
		return "TicketAudit [id=" + getId() + ", win=" + getWin() + ", date=" + getDate() + ", revisionNumber="
				+ revisionNumber + ", revisionType=" + revisionType + "]";
	}

}
