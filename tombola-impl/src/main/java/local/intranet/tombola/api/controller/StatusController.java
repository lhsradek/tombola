package local.intranet.tombola.api.controller;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.Collectors;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import local.intranet.tombola.InitData;
import local.intranet.tombola.TombolaApplication;
import local.intranet.tombola.api.config.ApplicationConfig;
import local.intranet.tombola.api.config.AuditorAwareImpl;
import local.intranet.tombola.api.config.OpenApiConfig;
import local.intranet.tombola.api.domain.Contented;
import local.intranet.tombola.api.info.LevelCount;
import local.intranet.tombola.api.info.PrizeInfo;
import local.intranet.tombola.api.info.TicketAudit;
import local.intranet.tombola.api.info.TicketInfo;
import local.intranet.tombola.api.info.content.Provider;
import local.intranet.tombola.api.listener.AuthenticationFailureListener;
import local.intranet.tombola.api.listener.AuthenticationSuccessEventListener;
import local.intranet.tombola.api.listener.EndpointsListener;
import local.intranet.tombola.api.model.repository.PrizeRepository;
import local.intranet.tombola.api.model.repository.TicketRepository;
import local.intranet.tombola.api.redis.RedisConfig;
import local.intranet.tombola.api.redis.RedisMessagePublisher;
import local.intranet.tombola.api.redis.RedisMessageSubscriber;
import local.intranet.tombola.api.security.LogoutSuccess;
import local.intranet.tombola.api.security.SecurityConfig;
import local.intranet.tombola.api.service.TombolaService;
import local.intranet.tombola.api.service.UserService;

/**
 * 
 * {@link StatusController} for
 * {@link local.intranet.tombola.TombolaApplication} It's for charge of system
 * information
 * 
 * @author Radek Kádner
 * 
 */
@RestController
@RequestMapping(value = "${spring.data.rest.basePath:/api}" + StatusController.INFO_VERSION_PATH
		+ StatusController.INFO_BASE_INFO)
@Tag(name = StatusController.TAG)
public class StatusController {

	private static final Logger LOG = LoggerFactory.getLogger(StatusController.class);

	/**
	 *
	 * STATUS_IMPLEMENTATION_VERSION = "implementationVersion"
	 */
	public static final String STATUS_IMPLEMENTATION_VERSION = "implementationVersion";

	/**
	 * 
	 * TAG = "status-controller"
	 */
	protected static final String TAG = "status-controller";

	/**
	 * 
	 * INFO_VERSION_PATH = "/v1"
	 */
	protected static final String INFO_VERSION_PATH = "/v1";

	/**
	 * 
	 * INFO_BASE_INFO = "/info"
	 */
	protected static final String INFO_BASE_INFO = "/info";

	/**
	 *
	 * STATUS_OK = "OK"
	 */
	public static final String STATUS_OK = "OK";

	/**
	 * 
	 * STATUS_PROTECTED = "[PROTECTED]"
	 */
	public static final String STATUS_PROTECTED = "[PROTECTED]";

	/**
	 *
	 * STATUS_SERVER_PORT = "serverPort"
	 */
	public static final String STATUS_SERVER_PORT = "serverPort";

	/**
	 *
	 * STATUS_HOST_NAME = "hostName"
	 */
	public static final String STATUS_HOST_NAME = "hostName";

	private static final String STATUS_TYPE_PROTOCOL_HANDLER = "*:type=ProtocolHandler,*";
	private static final String STATUS_PROTOCOL_HTTPS = "\"https-*";
	private static final String STATUS_SECURED_PORT_NOT_DEFINED = "SecuredPort not defined!";
	private static final String SOCKET_PORT = "port";
	private static final String STATUS_BRACKET = "_";
	private static final String STATUS_BRACKETS = "__";
	private static final String STATUS_ORG_APACHE_CATALINA_JSP_CLASSPATH = "org.apache.catalina.jsp_classpath";
	private static final String STATUS_ORG_APACHE_TOMCAT_UTIL_NET_SECURE_REQUESTED_CIPHERS = "org.apache.tomcat.util.net.secure_requested_ciphers";
	private static final String STATUS_ORG_APACHE_TOMCAT_UTIL_NET_SECURE_REQUESTED_PROTOCOL_VERSIONS = "org.apache.tomcat.util.net.secure_requested_protocol_versions";
	private static final String STATUS_ORG_SPRINGFRAMEWORK_WEB_SERVLET_FRAMEWORK_SERVLET_CONTEXT_DISPATCHER_SERVLET = "org.springframework.web.servlet.FrameworkServlet.CONTEXT.dispatcherServlet";
	private static final String STATUS_ORG_SPRINGFRAMEWORK_WEB_SERVLET_DISPATCHERSERVLET_OUTPUT_FLASH_MAP = "org.springframework.web.servlet.DispatcherServlet.OUTPUT_FLASH_MAP";
	private static final String STATUS_ORG_SPRINGFRAMEWORK_WEB_SERVLET_HANDLERMAPPING_BESTMATCHINGHANDLER = "org.springframework.web.servlet.HandlerMapping.bestMatchingHandler";
	private static final String STATUS_ORG_SPRINGFRAMEWORK_WEB_SERVLET_HANDLERMAPPING_PATHWITHINHANDLERMAPPING = "org.springframework.web.servlet.HandlerMapping.pathWithinHandlerMapping";
	private static final String STATUS_ORG_SPRINGFRAMEWORK_WEB_SERVLET_HANDLERMAPPING_URITEMPLATEVARIABLES = "org.springframework.web.servlet.HandlerMapping.uriTemplateVariables";
	private static final String STATUS_JAVAX_SERVLET_REQUEST_SSL_SESSION_ID = "javax.servlet.request.ssl_session_id";
	private static final String STATUS_APPLICATION_CONFIG_PROPERTIES = "applicationConfig: [classpath:/application.properties]";
	private static final String STATUS_APPLICATION_CONFIG_PROFILE_PROPERTIES = "applicationConfig: [classpath:/application-%s.properties]";
	private static final String STATUS_SERVLET_CONTEXT = "@\\w+";
	private static final String STATUS_USER_NAME = "username";
	private static final String STATUS_SECURITY_USER_NAME = "security.user.name";
	private static final String STATUS_PASSWORD = "password";
	private static final String STATUS_SECRET = "Secret";
	private static final String STATUS_TOMBOLA_SEC = "tombola.sec";
	private static final String STATUS_UNKNOWN = "unknown";
	private static final String STATUS_FORMAT_INDEX_API = "/tombola-javadoc/%s.html";
	private static final String STATUS_FORMAT_INDEX_API_NAME = "/tombola-javadoc/%s.html#%s";
	private static final String STATUS_BEAN = "%s:%s";
	private static final String STATUS_FORMAT_BEAN = "%s:<strong class=\"data\">%s</strong>";
	private static final String STATUS_NAME = "name";
	private static final String STATUS_HREF = "<a href=\"%s\" target=\"_blank\">[%s]</a>";
	private static final String STATUS_HREF_BEAN = "<a href=\"%s\" target=\"_blank\">%s</a>";
	private static final String STATUS_X_FORWARDED_FOR = "X-Forwarded-For";

	@Value("${tombola.app.stage}")
	private String stage;

	@Value("${tombola.app.javadocHref:true}")
	private boolean javadocHref;

	@Value("${tombola.app.showSessionId:false}")
	private boolean showSessionId;

	@Value("${tombola.app.emptyParams:false}")
	private boolean emptyParams;

	@Value("${tombola.app.attempts.printBlocked}")
	private boolean printBlocked;

	@Autowired
	private HttpServletRequest httpServletRequest;

	@Autowired
	private ServletContext servletContext;

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private PrizeRepository prizeRepository;

	@Autowired
	private TicketRepository ticketRepository;

	@Autowired
	private Environment environment;

	/**
	 *
	 * text/plain: "OK"
	 * <p>
	 * Accessible to the
	 * {@link local.intranet.tombola.api.domain.type.RoleType#USER_ROLE}
	 * 
	 * @see <a href="/tombola/swagger-ui/#/status-controller/getPlainStatus" target=
	 *      "_blank">tombola/swagger-ui/#/status-controller/getPlainStatus</a>
	 * 
	 * @return "OK" if Tombola API is running
	 */
	@GetMapping(value = "/status", produces = MediaType.TEXT_PLAIN_VALUE)
	@Operation(operationId = "getPlainStatus", summary = "Get Plain Status", description = "Get OK if Tombola API is running\n\n"
			+ "See <a href=\"/tombola-javadoc/local/intranet/tombola/api/controller/StatusController.html#"
			+ "getPlainStatus()\" "
			+ "target=\"_blank\">StatusController.getPlainStatus</a>", tags = { StatusController.TAG })
	@PreAuthorize("hasRole('ROLE_userRole')")
	public String getPlainStatus() {
		return STATUS_OK;
	}

	/**
	 *
	 * Info of Tomcat Environment.
	 * <p>
	 * Accessible to the
	 * {@link local.intranet.tombola.api.domain.type.RoleType#ADMIN_ROLE}
	 *
	 * @see <a href="/tombola/swagger-ui/#/status-controller/getTombolaEnvironment"
	 *      target=
	 *      "_blank">tombola/swagger-ui/#/status-controller/getTombolaEnvironment</a>
	 * 
	 * @return {@link List}&lt;{@link Map.Entry}&lt;{@link String},{@link String}&gt;&gt;
	 *         (({@link org.springframework.core.env.ConfigurableEnvironment})
	 *         environment).getSystemEnvironment() sorted {@link System#getenv()}
	 *         with {@link Map.Entry}&lt;String, String&gt; for ${k.key} and
	 *         ${k.value} in properties.html. Sort by ${k.key}
	 *         {@link String#CASE_INSENSITIVE_ORDER}
	 */
	@GetMapping(value = "/getEnvironment", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(operationId = "getTombolaEnvironment", summary = "Get Environment", description = "Get Environment\n\n"
			+ "See <a href=\"/tombola-javadoc/local/intranet/tombola/api/controller/StatusController.html#"
			+ "getTombolaEnvironment()\" "
			+ "target=\"_blank\">StatusController.getTombolaEnvironment</a>", tags = { StatusController.TAG })
	@PreAuthorize("hasRole('ROLE_adminRole')")
	public List<Map.Entry<String, String>> getTombolaEnvironment() {
		List<Map.Entry<String, String>> ret = Collections.synchronizedList(new ArrayList<>());
		Map<String, String> map = System.getenv().entrySet().stream().sorted(Map.Entry.comparingByKey())
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue,
						LinkedHashMap::new));
		for (Map.Entry<String, String> e : map.entrySet()) {
			if (!e.getKey().equals(STATUS_BRACKET)) { // nelíbí
				if (emptyParams) {
					ret.add(Map.entry(e.getKey(), e.getValue()));
				} else {
					if (e.getValue() != null && e.getValue().length() > 0) {
						ret.add(Map.entry(e.getKey(), e.getValue()));
					}
				}
			}
		}
		// LOG.debug("{}", ret);
		return ret;
	}

	/**
	 *
	 * Info of config params in .properties
	 * <p>
	 * Accessible to the
	 * {@link local.intranet.tombola.api.domain.type.RoleType#ADMIN_ROLE}
	 *
	 * @see <a href="/tombola/swagger-ui/#/status-controller/getTombolaProperties"
	 *      target=
	 *      "_blank">tombola/swagger-ui/#/status-controller/getTombolaProperties</a>
	 * 
	 * @return {@link List}&lt;{@link Map.Entry}&lt;{@link String},{@link String}&gt;&gt;
	 *         (({@link org.springframework.core.env.ConfigurableEnvironment})
	 *         environment).getPropertySources() with {@link Map.Entry}&lt;String,
	 *         String&gt; for ${k.key} and ${k.value} in properties.html. Sort by
	 *         ${k.key} {@link String#CASE_INSENSITIVE_ORDER}
	 */
	@GetMapping(value = "/getProperties", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(operationId = "getTombolaProperties", summary = "Get Properties", description = "Get Properties\n\n"
			+ "See <a href=\"/tombola-javadoc/local/intranet/tombola/api/controller/StatusController.html#"
			+ "getTombolaProperties()\" "
			+ "target=\"_blank\">StatusController.getTombolaProperties</a>", tags = { StatusController.TAG })
	@PreAuthorize("hasRole('ROLE_adminRole')")
	public List<Map.Entry<String, String>> getTombolaProperties() {
		List<Map.Entry<String, String>> ret = new ArrayList<>();
		Map<String, String> map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		String profile = String.join(" ", environment.getActiveProfiles());
		MutablePropertySources mps = ((ConfigurableEnvironment) environment).getPropertySources();

		PropertySource<?> ps = mps.get(STATUS_APPLICATION_CONFIG_PROPERTIES);
		@SuppressWarnings("unchecked")
		Map<String, Object> smap = ((Map<String, Object>) ps.getSource());
		for (Map.Entry<String, Object> e : smap.entrySet()) {
			if (e.getKey().contains(STATUS_PASSWORD) || e.getKey().contains(STATUS_SECRET)
					|| e.getKey().contains(STATUS_USER_NAME) || e.getKey().contains(STATUS_SECURITY_USER_NAME)
					|| e.getKey().startsWith(STATUS_TOMBOLA_SEC)) { // nelíbí
				map.put(e.getKey(), STATUS_PROTECTED);
			} else {
				map.put(e.getKey(), e.getValue().toString());
			}
		}

		ps = mps.get(String.format(STATUS_APPLICATION_CONFIG_PROFILE_PROPERTIES, profile));
		@SuppressWarnings("unchecked")
		Map<String, Object> smap2 = (Map<String, Object>) ps.getSource();
		for (Map.Entry<String, Object> e : smap2.entrySet()) {
			if (e.getKey().contains(STATUS_PASSWORD) || e.getKey().contains(STATUS_SECRET)
					|| e.getKey().contains(STATUS_USER_NAME) || e.getKey().contains(STATUS_SECURITY_USER_NAME)
					|| e.getKey().startsWith(STATUS_TOMBOLA_SEC)) { // nelíbí
				map.put(e.getKey(), STATUS_PROTECTED);
			} else {
				map.put(e.getKey(), e.getValue().toString());
			}
		}

		for (Map.Entry<String, String> e : map.entrySet()) {
			if (emptyParams) {
				ret.add(Map.entry(e.getKey(), e.getValue()));
			} else {
				if (e.getValue() != null && e.getValue().length() > 0) {
					ret.add(Map.entry(e.getKey(), e.getValue()));
				}
			}
		}
		// LOG.debug("{}", ret);
		return ret;
	}

	/**
	 *
	 * Info of Http servlet request
	 * <p>
	 * Accessible to the
	 * {@link local.intranet.tombola.api.domain.type.RoleType#ADMIN_ROLE}
	 *
	 * @see <a href=
	 *      "/tombola/swagger-ui/#/status-controller/getTombolaHttpServletRequest"
	 *      target=
	 *      "_blank">tombola/swagger-ui/#/status-controller/getTombolaHttpServletRequest</a>
	 *
	 * @return {@link List}&lt;{@link Map.Entry}&lt;{@link String},{@link String}&gt;&gt;
	 *         HttpServletRequest.getAttributeNames() and
	 *         HttpServletRequest.getAttribute(key) with
	 *         {@link Map.Entry}&lt;String, String&gt; for ${k.key} and ${k.value}
	 *         in properties.html. Sort by ${k.key}
	 *         {@link String#CASE_INSENSITIVE_ORDER}
	 */
	@GetMapping(value = "/getServletRequest", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(operationId = "getTombolaHttpServletRequest", summary = "Get HttpServletRequest", description = "Get HttpServletRequest\n\n"
			+ "See <a href=\"/tombola-javadoc/local/intranet/tombola/api/controller/StatusController.html#"
			+ "getTombolaHttpServletRequest()\" "
			+ "target=\"_blank\">StatusController.getTombolaHttpServletRequest</a>", tags = { StatusController.TAG })
	@PreAuthorize("hasRole('ROLE_adminRole')")
	public synchronized List<Map.Entry<String, String>> getTombolaHttpServletRequest() {
		List<Map.Entry<String, String>> ret = Collections.synchronizedList(new ArrayList<>());
		Map<String, String> map = new ConcurrentSkipListMap<>(String.CASE_INSENSITIVE_ORDER);
		for (Enumeration<String> en = httpServletRequest.getAttributeNames(); en.hasMoreElements();) {
			String key = en.nextElement();
			String value = httpServletRequest.getAttribute(key).toString();
			if (!(key.contains("@") || value.contains("@") ||
			/*
			 * __spring_security_filterSecurityInterceptor_filterApplied:true
			 * __spring_security_scpf_applied:true
			 * __spring_security_session_mgmt_filter_applied:true
			 */
					key.startsWith(STATUS_BRACKETS)
					|| key.equals(STATUS_ORG_APACHE_TOMCAT_UTIL_NET_SECURE_REQUESTED_CIPHERS) ||
					/*
					 * org.springframework.web.servlet.DispatcherServlet.OUTPUT_FLASH_MAP: FlashMap
					 * [attributes={}, targetRequestPath=null, targetRequestParams={}]
					 */
					key.equals(STATUS_ORG_SPRINGFRAMEWORK_WEB_SERVLET_DISPATCHERSERVLET_OUTPUT_FLASH_MAP) ||
					/*
					 * org.springframework.web.servlet.HandlerMapping.bestMatchingHandler:
					 */
					key.equals(STATUS_ORG_SPRINGFRAMEWORK_WEB_SERVLET_HANDLERMAPPING_BESTMATCHINGHANDLER) ||
					/*
					 * duplicity /status in
					 * org.springframework.web.servlet.HandlerMapping.pathWithinHandlerMapping
					 * org.springframework.web.servlet.HandlerMapping.bestMatchingPattern:/status
					 * org.springframework.web.servlet.HandlerMapping.pathWithinHandlerMapping:/
					 * status:/status
					 */
					key.equals(STATUS_ORG_SPRINGFRAMEWORK_WEB_SERVLET_HANDLERMAPPING_PATHWITHINHANDLERMAPPING) ||
					/*
					 * empty {}
					 * org.springframework.web.servlet.HandlerMapping.uriTemplateVariables:{}
					 */
					key.equals(STATUS_ORG_SPRINGFRAMEWORK_WEB_SERVLET_HANDLERMAPPING_URITEMPLATEVARIABLES) ||
					/*
					 * duplicity /status in
					 * org.apache.tomcat.util.net.secure_protocol_version:TLSv1.3
					 * org.apache.tomcat.util.net.secure_requested_protocol_versions:Unknown(0x9a9a)
					 * ,TLSv1.3,TLSv1.2
					 */
					key.equals(STATUS_ORG_APACHE_TOMCAT_UTIL_NET_SECURE_REQUESTED_PROTOCOL_VERSIONS)
					|| key.equals(STATUS_JAVAX_SERVLET_REQUEST_SSL_SESSION_ID))) { // nelíbí
				map.put(key, value);
			}
		}
		for (Map.Entry<String, String> e : map.entrySet()) {
			if (emptyParams) {
				ret.add(Map.entry(e.getKey(), e.getValue()));
			} else {
				if (e.getValue() != null && e.getValue().length() > 0) {
					ret.add(Map.entry(e.getKey(), e.getValue()));
				}
			}
		}
		// LOG.debug("{}", ret);
		return ret;
	}

	/**
	 *
	 * Info of Servlet context
	 * <p>
	 * Accessible to the
	 * {@link local.intranet.tombola.api.domain.type.RoleType#ADMIN_ROLE}
	 *
	 * @see <a href=
	 *      "/tombola/swagger-ui/#/status-controller/getTombolaServletContext"
	 *      target=
	 *      "_blank">tombola/swagger-ui/#/status-controller/getTombolaServletContext</a>
	 *
	 * @return {@link List}&lt;{@link Map.Entry}&lt;{@link String},{@link String}&gt;&gt;
	 *         ServletContext.getAttributeNames() and
	 *         ServletContext.getAttribute(key) with {@link Map.Entry}&lt;String,
	 *         String&gt; for ${k.key} and ${k.value} in properties.html. Sort by
	 *         ${k.key} {@link String#CASE_INSENSITIVE_ORDER}
	 */
	@GetMapping(value = "/getServletContext", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(operationId = "getTombolaServletContext", summary = "Get ServletContext", description = "Get ServletContext\n\n"
			+ "See <a href=\"/tombola-javadoc/local/intranet/tombola/api/controller/StatusController.html#"
			+ "getTombolaServletContext()\" "
			+ "target=\"_blank\">StatusController.getTombolaServletContext</a>", tags = { StatusController.TAG })
	@PreAuthorize("hasRole('ROLE_adminRole')")
	public List<Map.Entry<String, String>> getTombolaServletContext() {
		List<Map.Entry<String, String>> ret = new ArrayList<>();
		Map<String, String> map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		for (Enumeration<String> en = servletContext.getAttributeNames(); en.hasMoreElements();) {
			String key = en.nextElement();
			String value = servletContext.getAttribute(key).toString();
			if (!(key.equals(STATUS_ORG_APACHE_CATALINA_JSP_CLASSPATH) ||
			/*
			 * duplicity org.springframework.boot.web.servlet.context.
			 * AnnotationConfigServletWebServerApplicationContext in
			 * org.springframework.web.servlet.FrameworkServlet.CONTEXT.dispatcherServlet
			 * org.springframework.web.context.WebApplicationContext.ROOT:
			 * org.springframework.boot.web.servlet.context.
			 * AnnotationConfigServletWebServerApplicationContext, started on Tue Jan 18
			 * 11:45:22 CET 2022
			 * org.springframework.web.servlet.FrameworkServlet.CONTEXT.dispatcherServlet:
			 * org.springframework.boot.web.servlet.context.
			 * AnnotationConfigServletWebServerApplicationContext, started on Tue Jan 18
			 * 11:45:22 CET 2022
			 */
					key.equals(STATUS_ORG_SPRINGFRAMEWORK_WEB_SERVLET_FRAMEWORK_SERVLET_CONTEXT_DISPATCHER_SERVLET)
			// || value.length() > 0
			)) { // nelíbí
				map.put(key, value.replaceFirst(STATUS_SERVLET_CONTEXT, ""));
			}
		}
		for (Map.Entry<String, String> e : map.entrySet()) {
			if (emptyParams) {
				ret.add(Map.entry(e.getKey(), e.getValue()));
			} else {
				if (e.getValue() != null && e.getValue().length() > 0) {
					ret.add(Map.entry(e.getKey(), e.getValue()));
				}
			}
		}
		// LOG.debug("{}", ret);
		return ret;
	}

	// @formatter:off
	/**
	 *
	 * Info of Tombola API beans
	 * <p>
	 * Accessible to the {@link local.intranet.tombola.api.domain.type.RoleType#ADMIN_ROLE}
	 *
	 * @return {@link Map}&lt;{@link String},{@link Object}&gt;
	 */
	@PreAuthorize("hasRole('ROLE_adminRole')")
	public Map<String, Object> getAPIBean() {
		Map<String, Object> ret = new ConcurrentSkipListMap<>();
		for (String beanName : applicationContext.getBeanDefinitionNames()) {
			Object bean = applicationContext.getBean(beanName);
			Class<? extends Object> cl = bean.getClass();
			if (isBeanSuitable(cl)) {
				String name = cl.getSuperclass().getSimpleName();
				if (name.equals("Object")) {
					name = cl.getSimpleName();
				}
				Set<String> set = new TreeSet<>();
				for (Method m : cl.getDeclaredMethods()) {
					if (Modifier.isPublic(m.getModifiers()) && !Modifier.isStatic(m.getModifiers())) {
						makeAPIBeans(set, m, bean, false);
					}
				}
				StringBuffer str = new StringBuffer();
				for (String st : set) {
					if (st.length() > 0) {
						if (str.length() > 0) {
							str.append("|");
						}
						str.append(st);
					}
				}
				if (str.length() > 0) {
					Map<String, Object> map = new TreeMap<>();
					for (String s : str.toString().split("\\|")) {
						List<String> l = Arrays.asList(s.split("\\:"));
						if (l.size() > 1 && l.get(0) != null && l.get(1) != null) {
							String exp = String.join(":", l.subList(1, l.size()));
							if (l.get(0).equals("getAuthoritiesRoles")) {
								List<String> m = new ArrayList<>();
								if (exp.length() > 2) {
									for (String p : exp.substring(1, exp.length() - 1).split(", ")) {
										m.add(p);
									}
								}
								map.put(l.get(0), m);
							} else if (Arrays.asList("countTotalLoggingEvents", "getOperatingSystem").
									contains(l.get(0))) {
								Map<String, Object> m = new HashMap<String, Object>();
								if (exp.length() > 2) {
									for (String p : exp.substring(1, exp.length() - 1).split(", ")) {
										String[] b = p.split("=");
										try {
											m.put(b[0], Long.parseLong(b[1]));
										} catch (NumberFormatException e2) {
											try {
												m.put(b[0], Double.parseDouble(b[1]));
											} catch (NumberFormatException e3) {
												m.put(b[0], b[1]);
											}
										}
									}
								}
								map.put(l.get(0), m);
							} else {
								try { // {@link Date}
									SimpleDateFormat f = new SimpleDateFormat(
											Contented.CONTENT_DATE_FORMAT);
									map.put(l.get(0), f.format(f.parse(exp)));
								} catch (ParseException e1) {
									try { // {@link Long}
										map.put(l.get(0), Long.parseLong(exp));
									} catch (NumberFormatException e2) {
										if (l.get(1).equals(Boolean.TRUE.toString()) ||
												l.get(1).equals(Boolean.FALSE.toString())) { // {@link Boolean}
											map.put(l.get(0), Boolean.parseBoolean(exp));
										} else { // {@link String}
											map.put(l.get(0), String.join(":", exp));
										}
									}
								}
							}
						} else {
							// map.put(s, null);
							map.put(s, "");
						}
					}
					ret.put(name, map);
				} else {
					// ret.put(name, null);
					ret.put(name, "");
				}
			}
		}
	    // LOG.debug("{}", ret);
		return ret;
	}
	// @formatter:on

	// @formatter:off
	/**
	 *
	 * Info of Tombola API beans
	 * <p>
	 * For {@link IndexController#getProperties}
	 * <p>
	 * Accessible to the {@link local.intranet.tombola.api.domain.type.RoleType#ADMIN_ROLE}
	 *
	 * @return {@link List}&lt;{@link Map.Entry}&lt;{@link String},{@link String}&gt;&gt;
	 *         HttpServletRequest.getBeanDefinitionNames(),
	 *         getBean(bean).getClass() and getDeclaredMethods() with
	 *         {@link Map.Entry}&lt;String, String&gt; for ${k.key} and ${k.value}
	 *         in properties.html. Sort by ${k.key}
	 */
	@PreAuthorize("hasRole('ROLE_adminRole')")
	public List<Map.Entry<String, String>> getTombolaAPIBean() {
		List<Map.Entry<String, String>> ret = Collections.synchronizedList(new ArrayList<>());
		Map<String, String> map = new ConcurrentSkipListMap<>();
		for (String beanName : applicationContext.getBeanDefinitionNames()) {
			Object bean = applicationContext.getBean(beanName);
			Class<? extends Object> cl = bean.getClass();
			if (isBeanSuitable(cl)) {
				String name = cl.getSuperclass().getSimpleName();
				String link = String.format(STATUS_FORMAT_INDEX_API,
						cl.getSuperclass().getCanonicalName().replaceAll("\\.", "/"));
				if (name.equals("Object")) {
					if (cl.getCanonicalName() == null) {
						continue;
					}
					name = cl.getSimpleName();
					link = String.format(STATUS_FORMAT_INDEX_API,
							cl.getCanonicalName().replaceAll("\\.", "/"));
				}
				String href = String.format(STATUS_HREF, link, name);
				// LOG.debug("{} {}", name, link);
				if (!javadocHref) {
					href = "[" + name + "]";
				}
				Set<String> set = Collections.synchronizedSortedSet(new TreeSet<>());
				for (Method m : cl.getDeclaredMethods()) {
					if (Modifier.isPublic(m.getModifiers()) && !Modifier.isStatic(m.getModifiers())) {
						makeAPIBeans(set, m, bean, true);
					}
				}
				String setStr = String.join(" ", set);
				if (setStr.length() > 0) {
					map.put(name, href + "|" + setStr);
				} else {
					map.put(name , href + "|");
				}
			}
		}
		for (Map.Entry<String, String> e : map.entrySet()) {    // sort by es.getKey()
			String[] s = e.getValue().split("\\|");
			if (s.length == 2 && s[0] != null && s[1] != null) {
				ret.add(Map.entry(s[0] + " ", s[1]));
			} else if(s.length == 1 && s[0] != null ) {
				if (emptyParams) {
					ret.add(Map.entry(s[0], ""));
				}
			}
		}
	    // LOG.debug("{}", ret);
		return ret;
	}
	// @formatter:on

	/**
	 * 
	 * Get href format
	 * 
	 * @param bc     {@link Class}&lt;?&gt;
	 * @param format boolean
	 * @return {@link String}
	 */
	public synchronized String getHrefFormat(Class<?> bc, boolean format) {
		String ret = getHrefFormat(bc, bc.getSimpleName(), format);
		return ret;
	}

	/**
	 * 
	 * Get href format
	 * 
	 * @param bc     {@link Class}&lt;?&gt;
	 * @param text   {@link String}
	 * @param format boolean
	 * @return {@link String}
	 */
	public synchronized String getHrefFormat(Class<?> bc, String text, boolean format) {
		String ret;
		if (format && javadocHref) {
			ret = String.format(STATUS_HREF_BEAN,
					String.format(STATUS_FORMAT_INDEX_API, bc.getName().replaceAll("\\.", "/")), text);
		} else {
			ret = text;
		}
		return ret;
	}

	/**
	 * 
	 * Get href format
	 * 
	 * @param bc     {@link Class}&lt;?&gt;
	 * @param text   {@link String}
	 * @param sub    {@link String}
	 * @param format boolean
	 * @return {@link String}
	 */
	public synchronized String getHrefFormat(Class<?> bc, String text, String sub, boolean format) {
		String ret;
		if (format && javadocHref) {
			ret = String.format(STATUS_HREF_BEAN,
					String.format(STATUS_FORMAT_INDEX_API_NAME, bc.getName().replaceAll("\\.", "/"), sub), text);
		} else {
			ret = text;
		}
		return ret;
	}

	/**
	 *
	 * Active profiles
	 *
	 * @return environment.getActiveProfiles()
	 */
	public String getActiveProfiles() {
		String ret = String.join(" ", environment.getActiveProfiles());
		// LOG.debug("{}", ret);
		return ret;
	}

	/**
	 *
	 * Get server port
	 *
	 * @return secured serverPort from
	 *         {@link ManagementFactory#getPlatformMBeanServer()} public int
	 *         getServerPort() { int ret = 0; MBeanServer beanServer =
	 *         ManagementFactory.getPlatformMBeanServer(); try { Set<ObjectName>
	 *         objectNames = beanServer.queryNames(new
	 *         ObjectName(STATUS_TYPE_PROTOCOL_HANDLER),
	 *         Query.match(Query.attr(STATUS_NAME),
	 *         Query.value(STATUS_PROTOCOL_HTTPS))); ret =
	 *         Integer.parseInt(objectNames.iterator().next().getKeyProperty(SOCKET_PORT));
	 *         } catch (MalformedObjectNameException e) {
	 *         LOG.error(STATUS_SECURED_PORT_NOT_DEFINED, e); } // LOG.debug("{}",
	 *         ret); return ret; }
	 */

	/**
	 *
	 * Get Operating System
	 *
	 * @return {@link List}&lt;{@link Map.Entry}&lt;{@link String},{@link Object}&gt;&gt;
	 */
	@PreAuthorize("hasRole('ROLE_adminRole')")
	public synchronized List<Map.Entry<String, Object>> getOperatingSystem() {
		List<Map.Entry<String, Object>> ret = new ArrayList<>();
		OperatingSystemMXBean system = ManagementFactory.getOperatingSystemMXBean();
		ret.add(Map.entry("name", system.getName()));
		ret.add(Map.entry("loadAverage", system.getSystemLoadAverage()));
		// ret.add(Map.entry("arch", system.getArch()));
		// ret.add(Map.entry("processors", system.getAvailableProcessors()));
		// ret.add(Map.entry("version", system.getVersion()));
		// LOG.debug("{}", ret);
		return ret;
	}

	/**
	 * 
	 * Get session id
	 */
	public synchronized String getSessionId() {
		String ret = "";
		if (httpServletRequest != null) {
			HttpSession session = httpServletRequest.getSession(false);
			if (session != null) {
				if (session.getId() != null) {
					ret = session.getId();
				}
			}
		}
		return ret;
	}

	/**
	 *
	 * Get stage
	 *
	 * @return ${tombola.pub.app.stage}
	 */
	public String getStage() {
		String ret = stage;
		// LOG.debug("{}", ret);
		return ret;
	}

	/**
	 *
	 * Get host name
	 *
	 * @return hostName (The first word) from {@link #getServerName()}
	 */
	public String getHostName() {
		String ret = getServerName().split("\\.")[0];
		// LOG.debug("{}", ret);
		return ret;
	}

	/**
	 *
	 * Get server name
	 * 
	 * @return serverName (The second word) from {@link #getVirtualServerName()}
	 */
	public String getServerName() {
		String ret = getVirtualServerName().split("/")[1];
		// LOG.debug("{}", ret);
		return ret;
	}

	/**
	 *
	 * Get server software
	 *
	 * @return serverSoftware's name (The first word) without version from
	 *         {@link #getServerInfo()}
	 */
	public synchronized String getServerSoftware() {
		String ret = getServerInfo().split("/")[0];
		// LOG.debug("{}", ret);
		return ret;
	}

	/**
	 *
	 * Implementation version
	 *
	 * @return version from RequestContextUtils - HttpServletRequest -
	 *         {@link Package#getImplementationVersion()}
	 * 
	 */
	public String getImplementationVersion() {
		String ret = applicationContext.getBeansWithAnnotation(SpringBootApplication.class).entrySet().stream()
				.findFirst().flatMap(es -> {
					String implementationVersion = es.getValue().getClass().getPackage().getImplementationVersion();
					return Optional.ofNullable(implementationVersion);
				}).orElse(STATUS_UNKNOWN);
		// LOG.debug("{}", ret);
		return ret;
	}

	/**
	 *
	 * Get startup date
	 *
	 * @return {@link Date} from RequestContextUtils
	 *         ApplicationContext.getStartupDate()
	 */
	public Date getStartupDate() {
		if (applicationContext == null || applicationContext.getStartupDate() == 0) {
			getImplementationVersion();
		}
		Date ret = new Date(applicationContext.getStartupDate());
		// LOG.debug("{}", ret);
		return ret;
	}

	/**
	 *
	 * Get time zone
	 *
	 * @return {@link TimeZone#getDefault()}.getID() with {@link TimeZone#getID()}
	 */
	public String getTimeZone() {
		String ret = TimeZone.getDefault().getID();
		// LOG.debug("{}", ret);
		return ret;
	}

	/**
	 * 
	 * Get true if nice
	 * 
	 * @param showSessionId {@Boolean}
	 * @param name          {@String}
	 * @return boolean if nice
	 */
	public static boolean isNiceBeanName(Boolean showSessionId, String name) {
		boolean ret;
		switch (name) { // don't like this
		// {@link ApplicationConfig}
		case "jedisConnectionFactory":
		case "faviconHandlerMapping":
		case "setBeanFactory":
		case "sessionEventPublisher":
		case "localeResolver":
		case "localeChangeInterceptor":
		case "auditorProvider":
			// case "listener":
			// {@link UserService}
			// case "loadUserByUsername":
		case "getUserRoles":
		case "toString":
		case "toProxyConfigString":
		case "addAdvice":
		case "addAdvisor":
		case "equals":
		case "getAdvisors":
		case "getCallback":
		case "getCallbacks":
		case "getProxiedInterfaces":
		case "getTargetClass":
		case "getTargetSource":
		case "hashCode":
		case "indexOf":
		case "init":
		case "isExposeProxy":
		case "isFrozen":
		case "isInterfaceProxied":
		case "isPreFiltered":
		case "isProxyTargetClass":
		case "newInstance":
		case "removeAdvice":
		case "removeAdvisor":
		case "replaceAdvisor":
		case "setCallback":
		case "setCallbacks":
		case "setExposeProxy":
		case "setPreFiltered":
		case "setTargetSource":
		case "getPassword":
			// case "destroy":
			// case "doFilter":
		case "getRole": // deprecated
			// {@link StatusController}
		case "getTombolaAPIBean":
		case "getHrefFormat":
			ret = false;
			break;
		case "getSessionId":
			if (showSessionId) {
				ret = true;
			} else {
				ret = false;
			}
			break;
		default:
			ret = true;
			break;
		}
		return ret;
	}

	/**
	 * 
	 * Is Bean suitable?
	 * 
	 * @param cl {@link Class}&lt;? extends {@link Object}&gt;
	 * @return boolean
	 */
	// @formatter:off
	public static boolean isBeanSuitable(Class<? extends Object> cl) {
		boolean ret = false;
		if (cl.getName().startsWith(TombolaApplication.class.getPackageName())
			&& !(
				cl.getSimpleName().startsWith(TombolaApplication.class.getSimpleName()) ||
				cl.getSimpleName().startsWith(AuditorAwareImpl.class.getSimpleName()) ||
				cl.getSimpleName().startsWith(AuthenticationSuccessEventListener.class.getSimpleName()) ||
				cl.getSimpleName().startsWith(AuthenticationFailureListener.class.getSimpleName()) ||
				cl.getSimpleName().startsWith(EndpointsListener.class.getSimpleName()) ||
				cl.getSimpleName().startsWith(Provider.class.getSimpleName()) ||
				cl.getSimpleName().startsWith(InitData.class.getSimpleName()) ||
				cl.getSimpleName().startsWith(LogoutSuccess.class.getSimpleName()) ||
				cl.getSimpleName().startsWith(SecurityConfig.class.getSimpleName()) ||
				cl.getSimpleName().startsWith(OpenApiConfig.class.getSimpleName()) ||
				cl.getSimpleName().startsWith(RedisConfig.class.getSimpleName()) ||
				cl.getSimpleName().startsWith(RedisMessagePublisher.class.getSimpleName()) ||
				cl.getSimpleName().startsWith(RedisMessageSubscriber.class.getSimpleName())
				)) {  // nelíbí
			ret = true;
		}
		return ret;
	}
	// @formatter:on

	// @formatter:off
	/**
	 *
	 * For {@link #getTombolaAPIBean()} 
	 * <p>
	 * Used {@link StatusController}, {@link ApplicationConfig}
	 * {@link UserService}
	 * ... all in
	 * {@link local.intranet.tombola.api.service}
	 * <p>
	 *
	 * @param set    {@link Set}&lt;{@link String}&gt;
	 * @param method {@link Method}
	 * @param bean   {@link Object}
	 * @param format boolean
	 */
	@SuppressWarnings("unchecked")
	public synchronized void makeAPIBeans(Set<String> set, Method method, Object bean, boolean format) {
		Class<? extends Object> cl = bean.getClass().getSuperclass();
		if (cl.getSimpleName().equals("Object")) {
			cl = bean.getClass();
		}
		String name = method.getName();
		if (isNiceBeanName(showSessionId, name))
		try {
			// logAPIBeans(cl, name);
			// LOG.debug("bean:{} name:{}", bc.getSimpleName(), name);
			String strFormat = (format) ? STATUS_FORMAT_BEAN : STATUS_BEAN;
			switch (name) {
				// {@link StatusController}
				// case "getServerPort":
				case "getTimeZone":
				case "getActiveProfiles":
				case "getImplementationVersion":
				case "getHostName":
				case "getServerName":
				case "getServerSoftware":
				case "getStage":
				case "getPlainStatus":
				case "getClientIP":
			    // {@link ApplicationConfig}
 			    case "isFlyway":
				// {@link UserService}
				case "isAuthenticated":
					set.add(String.format(strFormat, name, cl.getMethod(name).invoke(bean)));
					break;
				case "getUsername":
				// {@link StatusController}
				case "getSessionId":   
					String str = (String) cl.getMethod(method.getName()).invoke(bean);
					if (str != null && str.length() > 0) {
						set.add(String.format(strFormat, name, str));
					}
					break;
				case "getTombolaEnvironment":
				case "getTombolaHttpServletRequest":
				case "getTombolaServletContext":
				case "getTombolaProperties":
					set.add(String.format(strFormat, name, ((List<Map.Entry<String, String>>) cl
							.getMethod(name).invoke(bean)).size()));
					break;
				case "getAttempts": // {@link LoginAttemptService}
					List<Map.Entry<String, Integer>> listAttemts =
						(List<Map.Entry<String, Integer>>) cl.getMethod(name).invoke(bean);
					if (listAttemts.size() > 0) {
						set.add(String.format(strFormat, name, listAttemts.toString()));
					} else {
						set.add(name);
					}
					break;
				case "isBlocked": // {@LoginAttemptService}
					if (printBlocked) {
						set.add(String.format(strFormat, name, cl
								.getMethod(method.getName(), new Class<?>[]{ String.class })
								.invoke(bean, new Object[]{ getClientIP() })));
					} else {
						set.add(name);
					}
					break;
				case "countTotalLoggingEvents": // {@link LoggingEventService}
					List<Map.Entry<String, Long>> loggingEvents = new ArrayList<>();
					for (LevelCount l : (List<LevelCount>) cl.getMethod(name).invoke(bean)) {
						loggingEvents.add(Map.entry(l.getLevel(), l.getTotal()));
					}
					if (loggingEvents.size() > 0) {
						set.add(String.format(strFormat, name, loggingEvents.toString()));
					} else {
						set.add(name);
					}
					break;
				case "getAuthoritiesRoles":
					set.add(String.format(strFormat, name, ((List<String>) cl.getMethod(name).invoke(bean)).toString()));
					break;
				case "getStartupDate":
					set.add(String.format(strFormat, name,
							new SimpleDateFormat(Contented.CONTENT_DATE_FORMAT).format(cl.getMethod(name).invoke(bean))));
					break;
				case "getOperatingSystem":
					set.add(String.format(strFormat, name, ((List<Map.Entry<String, Object>>) cl
							.getMethod(name).invoke(bean)).toString()));
					break;
				// {@link TombolaService}
				case "getPrize":
					if (bean instanceof TombolaService) {
						set.add(String.format(strFormat, name, getHrefFormat(PrizeInfo.class, format)));
					} else {
						set.add(name);
					}
					break;
				case "getPrizes": 
					if (bean instanceof TombolaService) {
						set.add(String.format(strFormat, name, prizeRepository.count()));
					} else {
						set.add(name);
					}
					break;
				case "getTicket":
					if (bean instanceof TombolaService) {
						String hrefTicketInfo = getHrefFormat(TicketAudit.class, format);
						set.add(String.format(strFormat, name, hrefTicketInfo));
					} else {
						set.add(name);
					}
					break;
				case "getTicketsPage":
					if (bean instanceof TombolaService) {
						set.add(String.format(strFormat, name, ticketRepository.count()));
					} else {
						set.add(name);
					}
					break;
				case "getTicketsWinPage": 
					if (bean instanceof TombolaService) {
						set.add(String.format(strFormat, name, ((Page<TicketInfo>) cl
								.getMethod(name, new Class<?>[] { Pageable.class })
								.invoke(bean, new Object[] { PageRequest.of(0, 1) })).getTotalElements()));
					} else {
						set.add(name);
					}
					break;
				/* nelíbí
				case "loadUserByUsername": // {@link UserService}
					set.add(String.format(strFormat, name, getHrefFormat(UserInfo.class, format)));
					break;
				*/
				/* nelíbí
				case "passwordEncoder": // {@link SecurityConfig}
					set.add(String.format(strFormat, name,
							((PasswordEncoder) bc.getMethod(name)
									.invoke(bean)).getClass().getSimpleName()));
					break;
				*/
				default:
					set.add(name);
					break;
			}
			// {@link SchedulerConfig}
		} catch (NoSuchMethodException | SecurityException | IllegalArgumentException | IllegalAccessException |
				InvocationTargetException e) {
			LOG.warn("{} '{}' name:{}", e.getClass().getSimpleName(), e.getMessage(), name);
		}
	}
	// @formatter:on

	/**
	 * 
	 * Log APIBeans
	 * 
	 * @param cl   {@link Class}&lt;? extends {@link Object}&gt;
	 * @param name {@link String}
	 */
	// @formatter:off
	protected void logAPIBeans(Class<? extends Object> cl, String name) {
		try {
			Method m = null;
			m = cl.getMethod(name);
			String g = (m == null) ? "" : m.getReturnType().getSimpleName();
			List<Type> p = (m == null) ? Arrays.asList() : Arrays.asList(m.getGenericParameterTypes());
 			if (p.size() > 0) {
				LOG.debug("LogAPIBeans bean:'{}' name:'{}' return:'{}' types:'{}'",
 						cl.getSimpleName(), name, g, p);
 			} else {
 				LOG.debug("LogAPIBeans bean:'{}' name:'{}' return:'{}'", cl.getSimpleName(), name, g);
 			}
		} catch (NoSuchMethodException e) {
		}

	}
	// @formatter:on

	/**
	 *
	 * Get virtualServerName from ServletContext.getVirtualServerName()
	 *
	 * @return getVirtualServerName()
	 */
	protected String getVirtualServerName() {
		String ret = servletContext.getVirtualServerName();
		// LOG.debug("{}", ret);
		return ret;
	}

	/**
	 *
	 * Get server info from ServletContext.getServerInfo()
	 *
	 * @return servletContext.getServerInfo()
	 */
	protected String getServerInfo() {
		String ret = servletContext.getServerInfo();
		// LOG.debug("{}", ret);
		return ret;
	}

	/**
	 * 
	 * Get client IP
	 * 
	 * @return {@link String} as ip
	 */
	public synchronized String getClientIP() {
		final String ret;
		String xfHeader = httpServletRequest.getHeader(STATUS_X_FORWARDED_FOR);
		if (xfHeader == null) {
			ret = httpServletRequest.getRemoteAddr();
		} else {
			ret = xfHeader.split(",")[0];
		}
		return ret;
	}

}