package local.intranet.tombola.api.model.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import local.intranet.tombola.api.model.entity.User;

/**
 * 
 * {@link UserRepository} is repository for CRUD with
 * {@link local.intranet.tombola.api.model.entity.User}
 * 
 * @author Radek Kádner
 *
 */
public interface UserRepository extends CrudRepository<User, Long> {

	/**
	 *
	 * Find by name
	 * 
	 * @param userName {@link String}
	 * @return {@link User}
	 */
	@Query(value = "select u from User u where u.userName = ?1")
	User findByName(String userName);

}
