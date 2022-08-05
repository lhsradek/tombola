package local.intranet.tombola.api.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import local.intranet.tombola.api.info.RoleInfo;
import local.intranet.tombola.api.info.RolePlain;
import local.intranet.tombola.api.model.entity.Role;
import local.intranet.tombola.api.model.repository.RoleRepository;

/**
 * 
 * {@link RoleService} for
 * {@link local.intranet.tombola.api.controller.InfoController#getRoleInfo}
 * 
 * @author Radek Kádner
 *
 */
@Service
public class RoleService {

	@Autowired
	private RoleRepository roleRepository;

	@Transactional(readOnly = true)
	public RoleInfo getRoleInfo() {
		return new RoleInfo(getUsersRoles());
	}

	/**
	 * 
	 * Get userRole
	 * 
	 * @return {@link List}&lt;{@link Role}&gt;
	 */
	@Transactional(readOnly = true)
	protected List<RolePlain> getUsersRoles() {
		List<RolePlain> ret = new ArrayList<>();
		roleRepository.findAll().forEach(r -> {
			ret.add(new RolePlain(r.getId(), r.getRoleName(), r.isEnabled(),
					r.getUser().stream().map(u -> u.getUserName()).collect(Collectors.toList())));
		});
		// LOG.debug("GetUserRoles ret:{}", list);
		return ret;
	}

}
