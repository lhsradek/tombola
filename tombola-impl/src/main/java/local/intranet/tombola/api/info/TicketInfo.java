package local.intranet.tombola.api.info;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * 
 * {@link TicketInfo} for {@link local.intranet.tombola.api.service.TombolaService}
 * 
 * @author radek.kadner
 *
 */
@JsonPropertyOrder({ "id", "win", "date" })
public class TicketInfo  {

	private final Long id;
	private final Long win;
	private final Date date;

	/**
	 * 
     * Constructor without parameter
	 */
	public TicketInfo() {
		this.id = 0L;
		this.win = 0L;
		this.date = new Date();
	}

	/**
	 * 
	 * Constructor with parameters
	 * 
	 * @param id   {@link Long}
	 * @param win  {@link Long}
	 * @param date {@link Date}
	 */
	public TicketInfo(Long id, Long win, Date date) {
		this.id = id;
		this.win = win;
		this.date = date;
	}

	/**
	 * 
	 * Get id
	 * 
	 * @return the id as {@link Long}
	 */
	public Long getId() {
		return id;
	}

	/**
	 * 
	 * Get win
	 * 
	 * @return the win as {@link Long}
	 */
	public Long getWin() {
		return win;
	}

	/**
	 * 
	 * Get date
	 * 
	 * @return the date {@link Date}
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * 
	 * Returns a string representation of the object.
	 */
	@Override
	public String toString() {
		return "TicketInfo [id=" + id + ", win=" + win + ", date=" + date + "]";
	}

}
