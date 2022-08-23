package local.intranet.tombola.api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import local.intranet.tombola.api.exception.TombolaException;
import local.intranet.tombola.api.info.BeanInfo;
import local.intranet.tombola.api.info.IndexInfo;
import local.intranet.tombola.api.info.RoleInfo;
import local.intranet.tombola.api.info.UserInfo;
import local.intranet.tombola.api.service.BeanService;
import local.intranet.tombola.api.service.IndexService;
import local.intranet.tombola.api.service.RoleService;
import local.intranet.tombola.api.service.UserService;

/**
 * 
 * {@link InfoController} for {@link local.intranet.tombola.TombolaApplication}
 * It's for charge of working with buffers and for REST methods
 * <p>
 * info from services in {@link local.intranet.tombola.api.service}
 * 
 * @author Radek Kádner
 *
 */
@RestController
@Tag(name = InfoController.TAG)
@RequestMapping(value = "${spring.data.rest.basePath:/api}" + InfoController.INFO_VERSION_PATH)
public class InfoController {

	private static final Logger LOG = LoggerFactory.getLogger(InfoController.class);

	/**
	 * 
	 * TAG = "info-controller"
	 */
	protected static final String TAG = "info-controller";

	/**
	 * 
	 * INFO_VERSION_PATH = "/v1"
	 */
	public static final String INFO_VERSION_PATH = "/v1";

	/**
	 * 
	 * INFO_BASE_INFO = "/info"
	 */
	public static final String INFO_BASE_INFO = "/info";

	@Autowired
	private IndexService indexService;

	@Autowired
	private UserService userService;

	@Autowired
	private RoleService roleService;

	@Autowired
	private BeanService beanService;

	// @Autowired private ErrorService errorService;

	/**
	 * 
	 * Root gives other REST links
	 * <p>
	 * Like this
	 * 
	 * <pre>
	 * {
	 *   "name": "IndexService",
	 *   "components": [
	 *     {
	 *       "link": "/api/v1/info/bean",
	 *       "name": "BeanService",
	 *       "role": "userRole"
	 *     },
	 *     {
	 *       "link": "/api/v1/info/user",
	 *       "name": "UserService",
	 *       "status": "UP",
	 *       "role": "userRole"
	 *     },
	 *     {
	 *       "link": "/api/v1/info/role",
	 *       "name": "RoleService",
	 *       "status": "UP",
	 *       "role": "userRole"
	 *     }
	 *   ]
	 * }
	 * </pre>
	 * <p>
	 * Accessible to the
	 * {@link local.intranet.tombola.api.domain.type.RoleType#USER_ROLE}.
	 * <p>
	 * Used {@link local.intranet.tombola.api.service.IndexService#getIndexInfo}.
	 * 
	 * @see <a href="/tombola/swagger-ui/#/info-controller/getIndexInfo" target=
	 *      "_blank">swagger-ui/#/info-controller/getIndexInfo</a>
	 * @return {@link IndexInfo}
	 */
	@GetMapping(value = InfoController.INFO_BASE_INFO, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(operationId = "getIndexInfo", summary = "Get Index Info", description = "Get Index Info\n\n"
			+ "This method is calling InfoService.getIndexInfo\n\n"
			+ "See <a href=\"/tombola-javadoc/local/intranet/tombola/api/controller/InfoController.html#"
			+ "getIndexInfo()\" target=\"_blank\">InfoController.getIndexInfo</a>", tags = {
					InfoController.TAG })
	@PreAuthorize("hasRole('ROLE_userRole')")
	public IndexInfo getIndexInfo() {
		IndexInfo ret = indexService.getIndexInfo();
		// LOG.debug("{}", ret);
		return ret;
	}

	/**
	 * 
	 * Bean informations
	 * <p>
	 * Accessible to the
	 * {@link local.intranet.tombola.api.domain.type.RoleType#USER_ROLE}.
	 * <p>
	 * Used {@link local.intranet.tombola.api.service.BeanService#getBeanInfo}.
	 * <p>
	 * 
	 * @see {@link #getIndexInfo}
	 * 
	 * @see <a href="/tombola/swagger-ui/#/info-controller/getBeanInfo" target=
	 *      "_blank">swagger-ui/#/info-controller/getBeanInfo</a>
	 * @return {@link BeanInfo}
	 * @GetMapping(value = InfoController.INFO_BASE_INFO + "/bean", produces =
	 *                   MediaType.APPLICATION_JSON_VALUE)
	 * @Operation(operationId = "getBeanInfo", summary = "Get Bean Info",
	 *                        description = "Get Bean Info\n\n" + "This method is
	 *                        calling BeanService.getBeanInfo\n\n" + "See <a
	 *                        href=\"/tombola-javadoc/local/intranet/tombola/api/controller/InfoController.html#"
	 *                        + "getBeanInfo()\"
	 *                        target=\"_blank\">InfoController.getBeanInfo</a>",
	 *                        tags = { InfoController.TAG
	 *                        }) @PreAuthorize("hasRole('ROLE_userRole')")
	 */
	public BeanInfo getBeanInfo() {
		BeanInfo ret;
		try {
			ret = beanService.getBeanInfo();
			// LOG.debug("{}", ret.toString());
		} catch (Exception e) {
			TombolaException exc = new TombolaException(e.getClass().getSimpleName() + ":" + e.getMessage(), e);
			LOG.error(exc.getMessage(), exc);
			throw e;
		}
		// LOG.debug("{}", ret);
		return ret;
	}

	/**
	 * 
	 * User informations
	 * <p>
	 * Accessible to the
	 * {@link local.intranet.tombola.api.domain.type.RoleType#USER_ROLE}.
	 * <p>
	 * Used {@link local.intranet.tombola.api.service.UserService#getUserInfo}.
	 * <p>
	 * 
	 * @see {@link #getIndexInfo}
	 * 
	 * @see <a href="/tombola/swagger-ui/#/info-controller/getUserInfo" target=
	 *      "_blank">swagger-ui/#/info-controller/getUserInfo</a>
	 * @return {@link UserInfo}
	 */
	@GetMapping(value = InfoController.INFO_BASE_INFO + "/user", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(operationId = "getUserInfo", summary = "Get User Info", description = "Get User Info\n\n"
			+ "This method is calling UserService.getUserInfo\n\n"
			+ "See <a href=\"/tombola-javadoc/local/intranet/tombola/api/controller/InfoController.html#"
			+ "getUserInfo()\" target=\"_blank\">InfoController.getUserInfo</a>", tags = {
					InfoController.TAG })
	@PreAuthorize("hasRole('ROLE_userRole')")
	public UserInfo getUserInfo() {
		UserInfo ret;
		try {
			ret = userService.getUserInfo();
			// LOG.debug("{}", ret.toString());
		} catch (Exception e) {
			TombolaException exc = new TombolaException(e.getClass().getSimpleName() + ":" + e.getMessage(), e);
			LOG.error(exc.getMessage(), exc);
			throw e;
		}
		// LOG.debug("{}", ret);
		return ret;
	}

	/**
	 * 
	 * Role informations
	 * <p>
	 * Accessible to the
	 * {@link local.intranet.tombola.api.domain.type.RoleType#USER_ROLE}.
	 * <p>
	 * Used {@link local.intranet.tombola.api.service.RoleService#getRoleInfo}.
	 * <p>
	 * 
	 * @see {@link #getIndexInfo}
	 * 
	 * @see <a href="/tombola/swagger-ui/#/info-controller/getRoleInfo" target=
	 *      "_blank">swagger-ui/#/info-controller/getRoleInfo</a>
	 * @return {@link RoleInfo}
	 */
	@GetMapping(value = InfoController.INFO_BASE_INFO + "/role", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(operationId = "getRoleInfo", summary = "Get Role Info", description = "Get Role Info\n\n"
			+ "This method is calling RoleService.getRoleInfo\n\n"
			+ "See <a href=\"/tombola-javadoc/local/intranet/tombola/api/controller/InfoController.html#"
			+ "getRoleInfo()\" target=\"_blank\">InfoController.getRoleInfo</a>", tags = {
					InfoController.TAG })
	@PreAuthorize("hasRole('ROLE_userRole')")
	public RoleInfo getRoleInfo() {
		RoleInfo ret;
		try {
			ret = roleService.getRoleInfo();
			// LOG.debug("{}", ret.toString());
		} catch (Exception e) {
			TombolaException exc = new TombolaException(e.getClass().getSimpleName() + ":" + e.getMessage(), e);
			LOG.error(exc.getMessage(), exc);
			throw e;
		}
		// LOG.debug("{}", ret);
		return ret;
	}

}
