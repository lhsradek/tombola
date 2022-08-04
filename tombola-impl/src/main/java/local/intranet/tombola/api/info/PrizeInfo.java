package local.intranet.tombola.api.info;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * 
 * {@link PrizeInfo} for {@link local.intranet.tombola.api.service.TombolaService}
 * 
 * @author radek.kadner
 *
 */
@JsonPropertyOrder({ "id", "prizeName", "cnt", "issued", "date", "ticket" })
public class PrizeInfo  {

	private final Long id;
	private final String prizeName;
	private final Integer cnt;
	private final Integer issued;
	private final Date date;
	private final List<Long> ticket;
	
	/**
	 * 
     * Constructor without parameter
	 */
	public PrizeInfo() {
		this.id = 0L;
		this.prizeName = "";
		this.cnt = 0;
		this.issued = 0;
		this.date = new Date();
		this.ticket = new ArrayList<>();
	}

	/**
	 * 
	 * Constructor with parameters
	 * 
	 * @param id        {@link Long}
	 * @param prizeName {@link String}
	 * @param cnt       int
	 * @param issued    int
	 * @param date      {@link Date}
	 * @param ticket    {@link List}&lt;{@link Long}&gt;
	 */
	public PrizeInfo(Long id, String prizeName, int cnt, int issued, Date date, List<Long> ticket) {
		this.id = id;
		this.prizeName = prizeName;
		this.cnt = cnt;
		this.issued = issued;
		if (date.getTime() == 0) {
			this.date = null;
		} else {
			this.date = date;
		}
		this.ticket = ticket;
	}

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
	 * Get prize name
	 * 
	 * @return the name {@link String}
	 */
	public String getPrizeName() {
		return prizeName;
	}

	/**
	 * 
	 * Get cnt
	 * 
	 * @return {@link Integer}
	 */
	public Integer getCnt() {
		return cnt;
	}

	/**
	 * 
	 * Get issued
	 * 
	 * @return {@link Integer}
	 */
	public Integer getIssued() {
		return issued;
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
	 * Get tickets
	 * 
	 * @return the los {@link List}&lt;{@link Long}&gt;
	 */
	public List<Long> getTicket() {
		return ticket;
	}

	/**
	 * 
	 * Returns a string representation of the object.
	 */
	@Override
	public String toString() {
		return "PrizeInfo [id=" + id + ", prizeName=" + prizeName + ", cnt=" + cnt + ", issued=" + issued + ", date="
				+ date + ", ticket=" + ticket + "]";
	}

}
