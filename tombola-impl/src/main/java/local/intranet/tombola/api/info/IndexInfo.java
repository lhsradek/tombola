package local.intranet.tombola.api.info;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import local.intranet.tombola.api.domain.Componented;
import local.intranet.tombola.api.domain.DefaultFieldLengths;
import local.intranet.tombola.api.domain.Nameable;
import local.intranet.tombola.api.domain.type.RoleType;
import local.intranet.tombola.api.service.BeanService;
import local.intranet.tombola.api.service.IndexService;
import local.intranet.tombola.api.service.RoleService;
import local.intranet.tombola.api.service.UserService;

/**
 * 
 * {@link IndexInfo} for
 * {@link local.intranet.tombola.api.service.IndexService#getIndexInfo},
 * {@link local.intranet.tombola.api.controller.InfoController#getIndexInfo} and
 * {@link local.intranet.tombola.api.info.RestInfo}
 * 
 * 
 * @author Radek Kádner
 *
 */
@JsonPropertyOrder({ "name", "components" })
public class IndexInfo implements Componented, Nameable {

	// private static final Logger LOG = LoggerFactory.getLogger(IndexInfo.class);

	@Size(min = 1, max = DefaultFieldLengths.DEFAULT_NAME)
	private final String basePath;

	/**
	 * 
	 * Constructor with parameters
	 * 
	 * @param basePath {@link String}
	 */
	public IndexInfo(String basePath) {
		this.basePath = basePath;
	}

	@Size(min = 0)
	@Override
	public List<RestInfo> getComponents() {
		List<RestInfo> ret = new ArrayList<>();
		ret.add(new RestInfo(basePath + shortName(BeanService.class.getSimpleName()), BeanService.class.getSimpleName(),
				RoleType.USER_ROLE.getRole()));
		ret.add(new RestInfo(basePath + shortName(UserService.class.getSimpleName()), UserService.class.getSimpleName(),
				RoleType.USER_ROLE.getRole()));
		ret.add(new RestInfo(basePath + shortName(RoleService.class.getSimpleName()), RoleService.class.getSimpleName(),
				RoleType.USER_ROLE.getRole()));
		return ret;
	}

	private String shortName(String simpleName) {
		return simpleName.replace("Service", "").toLowerCase();
	}

	@Size(min = 1, max = DefaultFieldLengths.DEFAULT_NAME)
	@Override
	public String getName() {
		String ret = IndexService.class.getSimpleName();
		return ret;
	}

}
