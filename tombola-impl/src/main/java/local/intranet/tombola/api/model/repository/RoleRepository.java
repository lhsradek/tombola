package local.intranet.tombola.api.model.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import local.intranet.tombola.api.model.entity.Role;

/**
 * 
 * {@link RoleRepository} is repository for CRUD with {@link local.intranet.tombola.api.model.entity.Role}
 * 
 * @author Radek Kádner
 *
 */
public interface RoleRepository extends CrudRepository<Role, Long> {

	/**
	 *
	 * Find by name
	 * 
	 * @param roleName {@link String}
	 * @return {@link Role}
	 */
	@Query(value = "select u from Role u where u.roleName = ?1")
	Role findByName(String roleName);

}
