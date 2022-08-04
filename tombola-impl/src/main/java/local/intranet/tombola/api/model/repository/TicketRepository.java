package local.intranet.tombola.api.model.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import local.intranet.tombola.api.model.entity.Ticket;

/**
 * 
 * {@link TicketRepository} is repository for CRUD with {@link local.intranet.tombola.api.model.entity.Ticket}
 * 
 * @author Radek Kádner
 *
 */
public interface TicketRepository extends CrudRepository<Ticket, Long> {

	/**
	 * 
	 * Find page
	 * 
	 * @param pageable {@link Pageable}
	 * @return {@link Page}&lt;{@link Ticket}&gt;
	 */
	@Query(value = "select u from Ticket u order by case when u.win > 0 then u.modifiedDate else " +
			"cast(0 as java.lang.Long) end desc, u.id")
	Page<Ticket> findPage(Pageable pageable);

	/**
	 * 
	 * Find win page
	 * 
	 * @param pageable {@link Pageable}
	 * @return {@link Page}&lt;{@link Ticket}&gt;
	 */
	@Query(value = "select u from Ticket u where u.win > 0 order by u.modifiedDate desc, u.id")
	Page<Ticket> findPageAndWin(Pageable pageable);
	
	/**
	 * 
	 * Find page ready to win
	 * 
	 * @param pageable {@link Pageable}
	 * @return {@link Page}&lt;{@link Ticket}&gt;
	 */
	@Query(value = "select u from Ticket u where u.win = 0 order by u.id desc")
	Page<Ticket> findPageAndReadyWin(Pageable pageable);
	
}
