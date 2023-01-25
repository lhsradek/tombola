package local.intranet.tombola.api.info;

import java.util.List;

import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import local.intranet.tombola.api.domain.DefaultFieldLengths;
import local.intranet.tombola.api.domain.Nameable;
import local.intranet.tombola.api.service.RoleService;

/**
 * 
 * {@link RoleInfo} for
 * {@link local.intranet.tombola.api.service.RoleService#getRoleInfo} and
 * {@link local.intranet.tombola.api.controller.InfoController#getRoleInfo}
 * 
 * @author Radek Kádner
 *
 */
@JsonPropertyOrder({ "name", "roles" })
public class RoleInfo implements Nameable {

	private final List<RolePlain> role;

	/**
	 * 
	 * Constructor with parameters
	 * 
	 * @param role {@link List}&lt;{@link RolePlain}&gt;
	 */
	public RoleInfo(List<RolePlain> role) {
		this.role = role;
	}

	/**
	 *
	 * Get RoleInfo
	 * <p>
	 * &#64;JsonInclude(JsonInclude.Include.NON_NULL)
	 * 
	 * @return {@link List}&lt;{@link RolePlain}&gt;
	 */
	@Size(min = 0)
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public List<RolePlain> getRoles() {
		List<RolePlain> ret = role;
		return ret;
	}

	@Size(min = 1, max = DefaultFieldLengths.DEFAULT_NAME)
	@Override
	public String getName() {
		String ret = RoleService.class.getSimpleName();
		return ret;
	}

}
