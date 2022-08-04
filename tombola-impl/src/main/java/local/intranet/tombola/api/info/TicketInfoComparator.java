package local.intranet.tombola.api.info;

import java.util.Comparator;

/**
 * 
 * {@link TicketInfoComparator} for {@link local.intranet.tombola.api.service.TombolaService}
 * 
 * @author Radek Kádner
 *
 */
public class TicketInfoComparator implements Comparator<TicketInfo> {

	/**
	 * 
     * Compares its two arguments for order.  Returns a negative integer,
     * zero, or a positive integer as the first argument is less than, equal
     * to, or greater than the second.
     * 
     * @param o1 {@link TicketInfo}
     * @param o2 {@link TicketInfo}
     * @return int
     */
	@Override
    public int compare(TicketInfo o1, TicketInfo o2) {
        if(o1.getDate() != null && o2.getDate() != null && o2.getDate().compareTo(o1.getDate()) != 0) {
        	return o1.getDate().compareTo(o2.getDate());
        } else {
        	return Long.valueOf(o1.getId()).compareTo(o2.getId());
        }
    }
	
}
