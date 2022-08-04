package local.intranet.tombola.api.model.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import local.intranet.tombola.api.model.entity.Prize;

/**
 * 
 * {@link PrizeRepository} is repository for CRUD with {@link local.intranet.tombola.api.model.entity.Prize}
 * 
 * @author Radek Kádner
 *
 */
public interface PrizeRepository extends JpaRepository<Prize, Long> {
	
	/**
	 *
	 * Find by name
	 * 
	 * @param prizeName {@link String}
	 * @return {@link Prize}
	 */
	@Query(value = "select u from Prize u where u.prizeName = ?1")
	Prize findByName(String prizeName);
	
	/**
	 * 
	 * Find by ready to win
	 * 
	 * @return {@link List}&lt;{@link Prize}&gt;
	 */
	@Query(value = "select u from Prize u where u.cnt > u.issued order by u.cnt - u.issued desc, u.id desc")
	List<Prize> findByReadyToWin();
	
}
