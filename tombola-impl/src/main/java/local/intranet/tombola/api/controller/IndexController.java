package local.intranet.tombola.api.controller;

import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootVersion;
import org.springframework.core.SpringVersion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpServerErrorException.InternalServerError;

import local.intranet.tombola.TombolaApplication;
import local.intranet.tombola.api.exception.TombolaException;
import local.intranet.tombola.api.info.LevelCount;
import local.intranet.tombola.api.info.LoggingEventInfo;
import local.intranet.tombola.api.info.PrizeInfo;
import local.intranet.tombola.api.info.TicketInfo;
import local.intranet.tombola.api.info.TicketLink;
import local.intranet.tombola.api.info.UserInfo;
import local.intranet.tombola.api.info.content.Provider;
import local.intranet.tombola.api.model.repository.TicketRepository;
import local.intranet.tombola.api.security.AESUtil;
import local.intranet.tombola.api.service.LoggingEventService;
import local.intranet.tombola.api.service.LoginAttemptService;
import local.intranet.tombola.api.service.TombolaService;
import local.intranet.tombola.api.service.UserService;

/**
 * 
 * {@link IndexController} for {@link local.intranet.tombola.TombolaApplication}
 * It's for charge of html pages
 * 
 * @author Radek Kádner
 * 
 */
@Controller
public class IndexController {

	private static final Logger LOG = LoggerFactory.getLogger(IndexController.class);

	/**
	 * 
	 * INDEX_ERROR_INVALID_USERNAME_AND_PASSWORD = "Invalid username and password!"
	 */
	public static final String INDEX_ERROR_INVALID_USERNAME_AND_PASSWORD = "Invalid username and password!";

	/**
	 * 
	 * INDEX_ERROR_INVALID_ROLE = "Invalid role for this page."
	 */
	public static final String INDEX_ERROR_INVALID_ROLE = "Invalid role for this page!";

	/**
	 * 
	 * INDEX_ERROR_USERNAME_IS_LOCKED = "Username or user roles are locked!"
	 */
	public static final String INDEX_ERROR_USERNAME_IS_LOCKED = "Username or user roles are locked!";

	/**
	 * 
	 * INDEX_ERROR_USERNAME_NOT_FOUND = "Username not found!"
	 */
	public static final String INDEX_ERROR_USERNAME_NOT_FOUND = "Username not found!";

	/**
	 * 
	 * INDEX_ERROR_BAD_CREDENTIALS = "Bad credetials!"
	 */
	public static final String INDEX_ERROR_BAD_CREDENTIALS = "Bad credetials!";

	/**
	 *
	 * INDEX_ERROR_ACCOUNT_EXPIRED = "Account expired!"
	 */
	public static final String INDEX_ERROR_ACCOUNT_EXPIRED = "Account expired!";

	/**
	 * 
	 * INDEX_ROLE_PREFIX = "ROLE_"
	 */
	public static final String INDEX_ROLE_PREFIX = "ROLE_";

	private static final String INDEX = "index";
	private static final String INDEX_API = "tombolaApi";
	private static final String INDEX_LICENSE = "license";
	private static final String INDEX_MANAGER = "manager";
	private static final String INDEX_PROPERTIES = "properties";
	private static final String INDEX_LOG = "tombolaLog";
	private static final String INDEX_AUDIT = "audit";
	private static final String INDEX_STATUS = "status";
	private static final String INDEX_LOGIN = "login";
	private static final String INDEX_ERROR = "error";
	private static final String INDEX_ROLE = "role";
	private static final String INDEX_USER_ROLES = "userRoles";
	private static final String INDEX_STATUS_SPRING_BOOT_VERSION = "springBootVersion";
	private static final String INDEX_STATUS_SPRING_VERSION = "springVersion";
	private static final String INDEX_HEADER_SOFTWARE = "headerSoftware";
	private static final String INDEX_STAGE = "stage";
	private static final String INDEX_COUNTER = "counter";
	private static final String INDEX_TICKET_COUNTER = "ticketCounter";
	private static final String INDEX_TICKET = "ticketAll";
	private static final String INDEX_TICKET_DRAFT = "ticketDraft";
	private static final String INDEX_TICKET_WIN = "ticketWin";
	private static final String INDEX_TICKET_PAGE = "ticketPage";
	private static final String INDEX_TICKET_COUNT = "ticketCnt";
	private static final String INDEX_TICKET_MAX = "ticketMax";
	private static final String INDEX_LOGS_PAGE = "logsPage";
	private static final String INDEX_LOGS_TOTAL = "logsTotal";
	private static final String INDEX_LOGS_COUNTER = "logsTotalCounter";
	private static final String INDEX_LOGS_SORT = "logsSort";
	private static final String INDEX_LOGS_FILTER = "logsFilter";
	private static final String INDEX_LOGS_COUNT = "logsCnt";
	private static final String INDEX_LOGS_MAX = "logsMax";
	private static final String INDEX_PRIZE = "prizeAll";
	private static final String INDEX_PRIZE_SUM = "prizeSum";
	private static final String INDEX_PRIZE_ISSUED = "prizeIssued";
	private static final String INDEX_NEXT = "next";
	private static final String INDEX_PREV = "prev";
	private static final String INDEX_ACTIVE_PROFILES = "activeProfiles";
	private static final String INDEX_SERVER_NAME = "serverName";
	private static final String INDEX_SERVER_SOFTWARE = "serverSoftware";
	private static final String INDEX_USER_IS_AUTHENTICATED = "isAuthenticated";
	private static final String INDEX_USERNAME = "username";
	private static final String INDEX_JAVAX_SERVLET_FORWARD_REQUEST_URI = "javax.servlet.forward.request_uri";
	private static final String INDEX_SPRING_SECURITY_SAVED_REQUEST = "SPRING_SECURITY_SAVED_REQUEST";
	private static final String INDEX_TOMBOLA_EXCEPTION = "TOMBOLA_APPLICATION_LAST_EXCEPTION";
	private static final String INDEX_TOMBOLA_APPLICATION_CONTEXT = "TOMBOLA_APPLICATION_CONTEXT";
	private static final String INDEX_TOMBOLA_H2_CONSOLE = "/tombola/h2-console";
	private static final String INDEX_SECRET_KEY = "secretKey";
	private static final String INDEX_SECRET_IV = "secretIv";
	private static final String INDEX_TOMBOLA_PROPERTIES = "tombolaProperties";
	private static final String INDEX_TOMBOLA_ENVIRONMENT = "tombolaEnvironment";
	private static final String INDEX_TOMBOLA_BEANS = "tombolaBeans";
	private static final String INDEX_TOMBOLA_HTTP_SERVLET_REQUEST = "tombolaHttpServletRequest";
	private static final String INDEX_TOMBOLA_SERVLET_CONTEXT = "tombolaServletContext";
	private static final String INDEX_TOMBOLA_LOGS = "tombolaLogs";
	private static final String INDEX_LOGIN_QUERY_STRING = "queryString:{} path:'{}'";
	private static final String INDEX_LOGIN_INVALID_ROLE = "invalidRole";
	private static final String INDEX_GET_ERROR = "getError";
	private static final String INDEX_REST = "rest";
	private static final String INDEX_REST_PNG = "/res/rest.png";

	@Value("${tombola.app.putTicket}")
	private int putTicket;

	@Value("${tombola.app.headerSoftware:false}")
	boolean headerSoftware;

	@Value("${tombola.app.stage}")
	private String stage;

	@Value("${tombola.app.ticketCnt:50}")
	private int ticketCnt;

	@Value("${tombola.app.logCnt:25}")
	private int logCnt;

	@Value("${tombola.sec.key}")
	private String key;

	@Value("${tombola.app.javadocHref:true}")
	boolean javadocHref;

	@Autowired
	private StatusController statusController;

	@Autowired
	private UserService userService;

	@Autowired
	private TombolaService tombolaService;

	@Autowired
	private LoggingEventService loggingEventService;

	@Autowired
	private LoginAttemptService loginAttemptService;

	@Autowired
	private TicketRepository ticketRepository;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private Provider provider;

	/**
	 * 
	 * HTML License info
	 * <p>
	 * The method getLicense for /license
	 * 
	 * @param request {@link HttpServletRequest}
	 * @param model   {@link Model}
	 * @return "license" for thymeleaf license.html {@link String}
	 */
	@GetMapping(value = "/license", produces = MediaType.TEXT_HTML_VALUE)
	@PreAuthorize("permitAll()")
	public String getLicense(HttpServletRequest request, Model model) {
		addModel(request, model);
		String username = (String) model.asMap().get(INDEX_USERNAME);
		if (username == null || username.length() == 0) {
			LOG.debug("GetLicense ip:'{}' sessionId:'{}'", statusController.getClientIP(),
					request.getRequestedSessionId());
		} else {
			LOG.debug("GetLicense username:'{}' ip:'{}' sessionId:'{}'", username, statusController.getClientIP(),
					request.getRequestedSessionId());
		}
		// model.asMap().forEach((key, value) -> { LOG.debug("key:{} value:{}", key,
		// value.toString()); });
		return INDEX_LICENSE;
	}

	/**
	 * 
	 * HTML Index
	 * <p>
	 * The method getIndex for /
	 * <p>
	 * Accessible to the
	 * {@link local.intranet.tombola.api.domain.type.RoleType#USER_ROLE}.
	 * 
	 * @param pg      {@link Integer} Number from 0. Zero is the first page
	 * @param request {@link HttpServletRequest}
	 * @param model   {@link Model}
	 * @return "index" for thymeleaf index.html {@link String}
	 */
	@GetMapping(value = { "/", "/page/{page}" }, produces = MediaType.TEXT_HTML_VALUE)
	public String getIndex(@PathVariable(value = "page", required = false) Integer pg, HttpServletRequest request,
			Model model) {
		AtomicInteger sumaCnt = new AtomicInteger();
		AtomicInteger sumaIssued = new AtomicInteger();
		List<PrizeInfo> prize = tombolaService.getPrizes(false);
		addModel(request, model);
		Pageable pageable = PageRequest.of(0, ticketCnt);
		Page<TicketInfo> ticket = tombolaService.getTicketsWinPage(pageable);
		long cnt = ticket.getTotalElements();
		int max = (ticket.getTotalPages() > 0) ? ticket.getTotalPages() - 1 : 0;
		AtomicInteger page = getPage(pg, max, sumaCnt, sumaIssued, prize);
		model.addAttribute(INDEX_STATUS_SPRING_BOOT_VERSION, SpringBootVersion.getVersion());
		model.addAttribute(INDEX_STATUS_SPRING_VERSION, SpringVersion.getVersion());
		model.addAttribute(INDEX_COUNTER, new AtomicLong(page.get() * ticketCnt));
		model.addAttribute(INDEX_TICKET_WIN, ticket.getContent());
		model.addAttribute(INDEX_API, statusController.getHrefFormat(TombolaApplication.class, javadocHref));
		model.addAttribute(StatusController.STATUS_IMPLEMENTATION_VERSION, statusController.getImplementationVersion());
		addAttribute(cnt, max, page, sumaCnt, sumaIssued, prize, model);
		LOG.debug("GetIndex '{}' {} {} {}", model.asMap().get(INDEX_USERNAME), request.getRequestedSessionId(),
				tombolaService.ticketInfoAsLong(ticket.getContent()), tombolaService.prizeInfoAsMap());
		// model.asMap().forEach((key, value) -> { LOG.debug("key:{} value:{}", key,
		// value.toString()); });
		return INDEX;
	}

	/**
	 * 
	 * HTML Manager
	 * <p>
	 * The method getManager for /manager
	 * <p>
	 * Accessible to the
	 * {@link local.intranet.tombola.api.domain.type.RoleType#MANAGER_ROLE}.
	 * 
	 * @param pg      {@link Integer} Number from 0. Zero is the first page
	 * @param request {@link HttpServletRequest}
	 * @param model   {@link Model}
	 * @return "manager" for thymeleaf manager.html {@link String}
	 */
	@GetMapping(value = { "/manager", "/manager/page/{page}" }, produces = MediaType.TEXT_HTML_VALUE)
	@PreAuthorize("hasRole('ROLE_managerRole')")
	public synchronized String getManager(@PathVariable(value = "page", required = false) Integer pg,
			HttpServletRequest request, Model model) throws GeneralSecurityException {
		AtomicInteger sumaCnt = new AtomicInteger();
		AtomicInteger sumaIssued = new AtomicInteger();
		List<PrizeInfo> prize = tombolaService.getPrizes(true);
		AtomicInteger page = getPage(pg, Integer.MAX_VALUE, sumaCnt, sumaIssued, prize);
		addModel(request, model);
		Pageable pageable = PageRequest.of(page.get(), ticketCnt);
		Page<TicketInfo> ticket = tombolaService.getTicketsPage(pageable);
		long cnt = ticket.getTotalElements();
		int max = (ticket.getTotalPages() > 0) ? ticket.getTotalPages() - 1 : 0;
		int newPage = getPage(page.get(), max, null, null, null).get();
		if (newPage != page.get()) {
			page.set(newPage);
			ticket = tombolaService.getTicketsPage(pageable);
		}
		LOG.debug("GetManager '{}' {} {} {}", model.asMap().get(INDEX_USERNAME), request.getRequestedSessionId(),
				tombolaService.ticketInfoAsLong(tombolaService.getTicketsWinPage(pageable).getContent()),
				tombolaService.prizeInfoAsMap());
		addAttribute(cnt, max, page, sumaCnt, sumaIssued, prize, model);
		try {
			String salt = AESUtil.generateSalt();
			IvParameterSpec iv = AESUtil.generateIv();
			request.getSession().setAttribute(INDEX_TOMBOLA_APPLICATION_CONTEXT,
					new TicketLink(request, AESUtil.setHex(salt), iv.getIV()));
			model.addAttribute(INDEX_COUNTER, new AtomicLong(page.get() * ticketCnt));
			model.addAttribute(INDEX_TICKET_COUNTER, new AtomicLong());
			model.addAttribute(INDEX_TICKET_DRAFT, tombolaService.getTicketsPrizes((int) ticketRepository.count()));
			model.addAttribute(INDEX_TICKET, ticket.getContent());
			model.addAttribute(INDEX_SECRET_KEY, AESUtil.getKeyFromPassword(AESUtil.getHex(key), salt));
			model.addAttribute(INDEX_SECRET_IV, iv);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException | InternalServerError e) {
			LOG.warn("GetManager error:'{}' message:'{}'", e.getClass().getSimpleName(), e.getMessage());
			if (e instanceof InternalServerError) {
				LOG.error(e.getMessage(), e);
			}
			request.getSession().setAttribute(INDEX_TOMBOLA_EXCEPTION, e);
			throw e;
		}
		// model.asMap().forEach((key, value) -> { LOG.debug("key:{} value:{}", key,
		// value.toString()); });
		return INDEX_MANAGER;
	}

	/**
	 * 
	 * HTML Manager Set
	 * <p>
	 * The method setManager for /manager
	 * <p>
	 * Accessible to the
	 * {@link local.intranet.tombola.api.domain.type.RoleType#MANAGER_ROLE}.
	 * 
	 * @param pg       {@link Integer} Number from 0. Zero is the first page
	 * @param prizeId  {@link Long} Prize's id
	 * @param ticketId {@link String} Ticket's id
	 * @param request  {@link HttpServletRequest}
	 * @param response {@link HttpServletResponse}
	 * @throws Exception
	 */
	@PostMapping(path = "/manager/page/{page}/prizeId/{prizeId}/ticketId/{ticketId}", consumes = {
			MediaType.APPLICATION_FORM_URLENCODED_VALUE }, produces = MediaType.TEXT_HTML_VALUE)
	@PreAuthorize("hasRole('ROLE_managerRole')")
	public synchronized String setManager(@PathVariable(value = "page", required = false) Integer pg,
			@PathVariable @NotNull Long prizeId, @PathVariable @NotNull String ticketId, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// LOG.debug("prizeId:{} ticketId:{} pg:{}", prizeId, ticketId, pg);
		AtomicInteger page = getPage(pg, Integer.MAX_VALUE, null, null, null);
		try {
			Object detail = request.getSession().getAttribute(INDEX_TOMBOLA_APPLICATION_CONTEXT);
			// LOG.debug("auth.detail:{}", detail);
			String salt = AESUtil.getHex(((TicketLink) detail).getSalt());
			IvParameterSpec iv = new IvParameterSpec(((TicketLink) detail).getIv());
			SecretKey secretKey = AESUtil.getKeyFromPassword(AESUtil.getHex(key), salt);
			Long ti = Long.valueOf(AESUtil.decrypt(AESUtil.getHex(ticketId), secretKey, iv));
			tombolaService.patchPrizeTicket(Long.valueOf(prizeId), ti);
			// Optional<Prize> prize = prizeRepository.findById(Long.valueOf(prizeId));
			String username = userService.getUsername();
			Pageable pageable = PageRequest.of(page.get(), ticketCnt);
			Page<TicketInfo> ticket = tombolaService.getTicketsWinPage(pageable);
			LOG.info("SetManager '{}' {} {} {}", username, request.getRequestedSessionId() + "|" + ti + "|" + prizeId,
					tombolaService.ticketInfoAsLong(ticket.getContent()), tombolaService.prizeInfoAsMap());
			// response.sendRedirect("/tombola/manager/page/" + page);
		} catch (InvalidKeyException | NoSuchPaddingException | NoSuchAlgorithmException
				| InvalidAlgorithmParameterException | BadPaddingException | NumberFormatException
				| InvalidKeySpecException | IllegalBlockSizeException e) {
			LOG.warn("SetManager error:'{}' message:'{}'", e.getClass().getSimpleName(), e.getMessage());
			// LOG.error(e.getMessage(), e);
			request.getSession().setAttribute(INDEX_TOMBOLA_EXCEPTION, e);
			throw e;
		}
		return "redirect: /tombola/manager/page/" + page;
		// model.asMap().forEach((key, value) -> { LOG.debug("key:{} value:{}", key,
		// value.toString()); });
	}

	/**
	 * 
	 * HTML Properties
	 * <p>
	 * The method getProperties for /properties
	 * <p>
	 * Accessible to the
	 * {@link local.intranet.tombola.api.domain.type.RoleType#ADMIN_ROLE}.
	 * 
	 * @param request {@link HttpServletRequest}
	 * @param model   {@link Model}
	 * @return "properties" for thymeleaf properties.html {@link String}
	 */
	@GetMapping(value = { "/properties" }, produces = MediaType.TEXT_HTML_VALUE)
	@PreAuthorize("hasRole('ROLE_adminRole')")
	public String getProperties(HttpServletRequest request, Model model) {
		addModel(request, model);
		model.addAttribute(INDEX_TOMBOLA_BEANS, statusController.getTombolaAPIBean());
		model.addAttribute(StatusController.STATUS_HOST_NAME, statusController.getHostName());
		// model.addAttribute(StatusController.STATUS_SERVER_PORT,
		// statusController.getServerPort());
		model.addAttribute(StatusController.STATUS_SERVER_PORT, 8080);
		model.addAttribute(INDEX_REST, INDEX_REST_PNG);
		model.addAttribute(INDEX_TOMBOLA_PROPERTIES, statusController.getTombolaProperties());
		model.addAttribute(INDEX_TOMBOLA_HTTP_SERVLET_REQUEST, statusController.getTombolaHttpServletRequest());
		model.addAttribute(INDEX_TOMBOLA_SERVLET_CONTEXT, statusController.getTombolaServletContext());
		model.addAttribute(INDEX_TOMBOLA_ENVIRONMENT, statusController.getTombolaEnvironment());
		LOG.debug("GetProperties username:'{}' ip:'{}' sessionId:'{}'", model.asMap().get(INDEX_USERNAME),
				statusController.getClientIP(), request.getRequestedSessionId());
		// model.asMap().forEach((key, value) -> { LOG.debug("key:{} value:{}", key,
		// value.toString()); });
		return INDEX_PROPERTIES;
	}

	/**
	 * 
	 * HTML Log
	 * <p>
	 * The method getTombolaLog for /tombolaLog
	 * <p>
	 * Accessible to the
	 * {@link local.intranet.tombola.api.domain.type.RoleType#MANAGER_ROLE} and
	 * {@link local.intranet.tombola.api.domain.type.RoleType#ADMIN_ROLE}
	 * 
	 * @param pg      {@link Integer} Number from 0. Zero is the first page
	 * @param srt     {@link String} Sort by [idD, idU, mD, mU, a0D, a0U, a1D, a1U,
	 *                a2D, a2U, a3D, a3U, cU, cD, lU, lD]
	 * @param filter  {@link String} Empty or filter by [DEBUG, ERROR, INFO, WARN]
	 *                or their combinations. Example: 'ERROR+WARN'
	 * @param request {@link HttpServletRequest}
	 * @param model   {@link Model}
	 * @return "indexLog" for thymeleaf indexLog.html {@link String}
	 */
	@GetMapping(value = { "/tombolaLog", "/tombolaLog/page/{page}", "/tombolaLog/page/{page}/sort/{sort}",
			"/tombolaLog/page/{page}/sort/{sort}/filter/{filter}" }, produces = MediaType.TEXT_HTML_VALUE)
	@PreAuthorize("hasAnyRole('ROLE_managerRole', 'ROLE_adminRole')")
	public String getTombolaLog(@PathVariable(value = "page", required = false) Integer pg,
			@PathVariable(value = "sort", required = false) String srt, @PathVariable(required = false) String filter,
			HttpServletRequest request, Model model) {
		List<LevelCount> ctle = loggingEventService.countTotalLoggingEvents();
		List<String> levelString = new ArrayList<>();
		if (filter == null || filter.length() == 0) {
			filter = "0";
			for (LevelCount l : ctle) {
				levelString.add(l.getLevel());
			}
		} else {
			for (String s : filter.split("\\+")) {
				if (Arrays.asList("DEBUG", "ERROR", "INFO", "WARN").contains(s)) {
					levelString.add(s);
				} else {
					levelString.clear();
					break;
				}
			}
			if (levelString.size() == 0) {
				filter = "0";
				for (LevelCount l : ctle) {
					levelString.add(l.getLevel());
				}
			}
		}
		// Up, Down
		if (srt == null || srt.length() == 0 || !Arrays.asList("idU", "idD", "mU", "mD", "a0U", "a0D", "a1U", "a1D",
				"a2U", "a2D", "a3U", "a3D", "cU", "cD", "lU", "lD").contains(srt)) {
			srt = "idD";
		}
		AtomicInteger page = getPage(pg, Integer.MAX_VALUE, null, null, null);
		addModel(request, model);
		List<Order> order = logSortByParam(srt);
		Pageable pageable = PageRequest.of(page.get(), logCnt, Sort.by(order));
		Page<LoggingEventInfo> log = loggingEventService.findPageByLevelString(pageable, levelString);
		long cnt = log.getTotalElements();
		int max = (log.getTotalPages() > 0) ? log.getTotalPages() - 1 : 0;
		int newPage = getPage(page.get(), max, null, null, null).get();
		if (newPage != page.get()) {
			page.set(newPage);
			log = loggingEventService.findPageByLevelString(pageable, levelString);
		}
		model.addAttribute(INDEX_ACTIVE_PROFILES, statusController.getActiveProfiles());
		model.addAttribute(INDEX_LOGS_TOTAL, ctle);
		model.addAttribute(INDEX_LOGS_COUNTER, new AtomicInteger());
		model.addAttribute(INDEX_TOMBOLA_LOGS, log.getContent());
		model.addAttribute(INDEX_LOGS_COUNT, cnt);
		model.addAttribute(INDEX_LOGS_MAX, max);
		model.addAttribute(INDEX_LOGS_PAGE, page);
		model.addAttribute(INDEX_LOGS_SORT, srt);
		model.addAttribute(INDEX_LOGS_FILTER, filter);
		setPage(page, max, model);
		// model.asMap().forEach((key, value) -> { LOG.debug("key:{} value:{}", key,
		// value.toString()); });
		return INDEX_LOG;
	}

	/**
	 * 
	 * logSortByParam for {@link #getTombolaLog}
	 * 
	 * @param srt {@link String} Sort by [idD, idU, mD, mU, a0D, a0U, a1D, a1U, a2D,
	 *            a2U, a3D, a3U, cU, cD, lU, lD]
	 * @return {@link List}&lt;{@link Order}&gt;
	 */
	protected List<Order> logSortByParam(String srt) {
		List<Order> ret = new ArrayList<Order>();
		switch (srt) {
		case "idU":
			ret.add(Order.asc("id"));
			break;
		case "idD":
			ret.add(Order.desc("id"));
			break;
		case "mU":
			ret.add(Order.asc("formattedMessage").ignoreCase());
			ret.add(Order.asc("id"));
			break;
		case "mD":
			ret.add(Order.desc("formattedMessage").ignoreCase());
			ret.add(Order.desc("id"));
			break;
		case "a0U":
			ret.add(Order.asc("arg0").ignoreCase());
			ret.add(Order.asc("id"));
			break;
		case "a0D":
			ret.add(Order.desc("arg0").ignoreCase());
			ret.add(Order.desc("id"));
			break;
		case "a1U":
			ret.add(Order.asc("arg1").ignoreCase());
			ret.add(Order.asc("id"));
			break;
		case "a1D":
			ret.add(Order.desc("arg1").ignoreCase());
			ret.add(Order.desc("id"));
			break;
		case "a2U":
			ret.add(Order.asc("arg2").ignoreCase());
			ret.add(Order.asc("id"));
			break;
		case "a2D":
			ret.add(Order.desc("arg2").ignoreCase());
			ret.add(Order.desc("id"));
			break;
		case "a3U":
			ret.add(Order.asc("arg3").ignoreCase());
			ret.add(Order.asc("id"));
			break;
		case "a3D":
			ret.add(Order.desc("arg3").ignoreCase());
			ret.add(Order.desc("id"));
			break;
		case "cU":
			ret.add(Order.asc("callerMethod").ignoreCase());
			ret.add(Order.asc("id"));
			break;
		case "cD":
			ret.add(Order.desc("callerMethod").ignoreCase());
			ret.add(Order.desc("id"));
			break;
		case "lU":
			ret.add(Order.asc("levelString").ignoreCase());
			ret.add(Order.asc("id"));
			break;
		case "lD":
			ret.add(Order.desc("levelString").ignoreCase());
			ret.add(Order.desc("id"));
			break;
		default:
			ret.add(Order.desc("id"));
			break;
		}
		return ret;
	}

	/**
	 * 
	 * HTML Audit
	 * <p>
	 * The method getAudit for /audit
	 * <p>
	 * Accessible to the
	 * {@link local.intranet.tombola.api.domain.type.RoleType#MANAGER_ROLE} and
	 * {@link local.intranet.tombola.api.domain.type.RoleType#ADMIN_ROLE}
	 * 
	 * @param pg      {@link Integer} Number from 0. Zero is the first page
	 * @param srt     {@link String} Sort by [idU, idD, a0U, a0D, a1U, a1D, a2U,
	 *                a2D, a3U, a3D, mU, mD]
	 * @param request {@link HttpServletRequest}
	 * @param model   {@link Model}
	 * @return "indexLog" for thymeleaf indexLog.html {@link String}
	 */
	@GetMapping(value = { "/audit", "/audit/page/{page}",
			"/audit/page/{page}/sort/{sort}" }, produces = MediaType.TEXT_HTML_VALUE)
	@PreAuthorize("hasAnyRole('ROLE_managerRole', 'ROLE_adminRole')")
	public String getAudit(@PathVariable(value = "page", required = false) Integer pg,
			@PathVariable(value = "sort", required = false) String srt, HttpServletRequest request, Model model) {
		// Up, Down
		if (srt == null || srt.length() == 0
				|| !Arrays.asList("idU", "idD", "a0U", "a0D", "a1U", "a1D", "a2U", "a2D", "a3U", "a3D", "mU", "mD")
						.contains(srt)) {
			srt = "idD";
		}
		List<Order> order = auditSortByParam(srt);
		AtomicInteger page = getPage(pg, Integer.MAX_VALUE, null, null, null);
		addModel(request, model);
		LOG.info("GetAudit username:'{}' {} order:'{}' {}", model.asMap().get(INDEX_USERNAME),
				request.getRequestedSessionId(), order.toString(), tombolaService.prizeInfoAsMap());
		List<String> arr = Arrays.asList("deleteTicket", "patchPrizeName", "patchPrizeTicket", "putPrizes",
				"putTickets");
		Page<LoggingEventInfo> log = loggingEventService.findPageByCaller(page.get(), ticketCnt, Sort.by(order),
				Arrays.asList(TombolaService.class.getName()), arr);
		long cnt = log.getTotalElements();
		int max = (log.getTotalPages() > 0) ? log.getTotalPages() - 1 : 0;
		int newPage = getPage(page.get(), max, null, null, null).get();
		if (newPage != page.get()) {
			page.set(newPage);
			log = loggingEventService.findPageByCaller(page.get(), ticketCnt, Sort.by(order),
					Arrays.asList(TombolaService.class.getName()), arr);
		}
		model.addAttribute(INDEX_ACTIVE_PROFILES, statusController.getActiveProfiles());
		model.addAttribute(INDEX_TOMBOLA_LOGS, log.getContent());
		model.addAttribute(INDEX_LOGS_COUNT, cnt);
		model.addAttribute(INDEX_LOGS_MAX, max);
		model.addAttribute(INDEX_LOGS_PAGE, page);
		model.addAttribute(INDEX_LOGS_SORT, srt);
		setPage(page, max, model);
		// model.asMap().forEach((key, value) -> { LOG.debug("key:{} value:{}", key,
		// value.toString()); });
		return INDEX_AUDIT;
	}

	/**
	 * 
	 * auditSortByParam for {@link #getAudit}
	 * 
	 * @param srt {@link String} Sort by [idU, idD, a0U, a0D, a1U, a1D, a2U, a2D,
	 *            a3U, a3D, mU, mD]
	 * @return {@link List}&lt;{@link Order}&gt;
	 */
	protected List<Order> auditSortByParam(String srt) {
		List<Order> ret = new ArrayList<Order>();
		switch (srt) {
		case "idU":
			ret.add(Order.asc("id"));
			break;
		case "idD":
			ret.add(Order.desc("id"));
			break;
		case "a0U":
			ret.add(Order.asc("arg0"));
			ret.add(Order.asc("id"));
			break;
		case "a0D":
			ret.add(Order.desc("arg0"));
			ret.add(Order.desc("id"));
			break;
		case "a1U":
			ret.add(Order.asc("cast(arg1 as java.lang.Long)"));
			ret.add(Order.asc("id"));
			break;
		case "a1D":
			ret.add(Order.desc("cast(arg1 as java.lang.Long)"));
			ret.add(Order.desc("id"));
			break;
		case "a2U":
			ret.add(Order.asc("cast(arg2 as java.lang.Long)"));
			ret.add(Order.asc("id"));
			break;
		case "a2D":
			ret.add(Order.desc("cast(arg2 as java.lang.Long)"));
			ret.add(Order.desc("id"));
			break;
		case "a3U":
			ret.add(Order.asc("arg3"));
			ret.add(Order.asc("id"));
			break;
		case "a3D":
			ret.add(Order.desc("arg3"));
			ret.add(Order.desc("id"));
			break;
		case "mU":
			ret.add(Order.asc("callerMethod"));
			ret.add(Order.asc("id"));
			break;
		case "mD":
			ret.add(Order.desc("callerMethod"));
			ret.add(Order.desc("id"));
			break;
		default:
			ret.add(Order.desc("id"));
			break;
		}
		return ret;
	}

	/**
	 *
	 * Log in
	 * <p>
	 * The method getLogin for /login
	 * 
	 * @param request {@link HttpServletRequest}
	 * @param model   {@link Model}
	 * @return "login" for thymeleaf login.html {@link String}
	 */
	@GetMapping(value = { "/login" }, produces = MediaType.TEXT_HTML_VALUE)
	public String getLogin(HttpServletRequest request, HttpServletResponse response, Model model) {
		String err = getErrorMessage(request, INDEX_TOMBOLA_EXCEPTION, model);
		if (request.getSession() != null && err.equals(StatusController.STATUS_OK)) {
			request.getSession().removeAttribute(INDEX_TOMBOLA_EXCEPTION);
		} else if (request.getSession() != null) {
			request.getSession().setAttribute(INDEX_TOMBOLA_EXCEPTION, new TombolaException(err));
		}
		addModel(request, model);
		boolean isAuthenticated = (boolean) model.getAttribute(INDEX_USER_IS_AUTHENTICATED);
		if (request.getQueryString() != null && request.getQueryString().startsWith(INDEX_ERROR)) {
			if (!isAuthenticated) {
				LOG.warn(INDEX_LOGIN_QUERY_STRING, request.getQueryString(), "");
			}
		}
		model.addAttribute(INDEX_API, statusController.getHrefFormat(TombolaApplication.class, javadocHref));
		model.addAttribute(INDEX_LOGIN_INVALID_ROLE, INDEX_ERROR_INVALID_ROLE);
		// model.asMap().forEach((key, value) -> { LOG.debug("key:{} value:{}", key,
		// value.toString()); });
		return INDEX_LOGIN;
	}

	/**
	 * 
	 * Sign in from log in form
	 * <p>
	 * The method signin for /login/signin
	 * <p>
	 * For {@link #getLogin} after submit &lt;Log in&gt; or press
	 * &lt;&crarr;Enter&gt;
	 * 
	 * @param username {@link String} Username
	 * @param password {@link String} Password
	 * @param request  {@link HttpServletRequest}
	 * @return redirect url {@link String}
	 */
	@PostMapping(path = "/login/signin", consumes = { MediaType.APPLICATION_FORM_URLENCODED_VALUE })
	public synchronized String signin(@RequestParam @NotNull String username, @RequestParam @NotNull String password,
			HttpServletRequest request) {
		String redirect = "/tombola/login";
		if (username != null && request.getSession() != null) {
			DefaultSavedRequest savedRequest = (DefaultSavedRequest) request.getSession()
					.getAttribute(INDEX_SPRING_SECURITY_SAVED_REQUEST);
			if (savedRequest != null) {
				redirect = savedRequest.getRedirectUrl();
			}
			try {
				UserInfo user = userService.loadUserByUsername(username);
				UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user, password,
						user.getAuthorities());
				if (authenticationManager != null) {
					SecurityContextHolder.getContext().setAuthentication(authenticationManager.authenticate(token));
				}
				LOG.info("Login username:'{}' redirect:'{}' sessionId:'{}'", username, redirect,
						request.getSession().getId());
			} catch (UsernameNotFoundException | AccountExpiredException | BadCredentialsException
					| LockedException e) {
				String ret = "/tombola/login"
						+ provider.queryProvider(Arrays.asList(Map.entry(INDEX_ERROR, Boolean.TRUE.toString()),
								Map.entry("exception", e.getClass().getSimpleName())));
				int attempt = loginAttemptService.findById(statusController.getClientIP());
				LOG.warn("Signin username:'{}' redirect:'{}' attempt:{}", username, ret, attempt);
				LOG.error(e.getClass().getSimpleName(), e);
				request.getSession().setAttribute(INDEX_TOMBOLA_EXCEPTION, e);
				redirect = ret;
			}
		}
		return "redirect: " + redirect;
	}

	/**
	 * 
	 * Get Error with statusCode
	 * <p>
	 * and text HttpStatus.valueOf(statusCode).getReasonPhrase()
	 * <p>
	 * The method getError for /error
	 * 
	 * @param request {@link HttpServletRequest}
	 * @param model   {@link Model}
	 * @return "error" for thymeleaf error.html {@link String}
	 */
	@RequestMapping(value = "/error", method = { RequestMethod.GET,
			RequestMethod.POST }, produces = MediaType.TEXT_HTML_VALUE)
	public synchronized String getError(HttpServletRequest request, Model model) {
		try {
			int statusCode = Integer.valueOf(200);
			String statusText = "";
			Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
			if (status != null) {
				statusCode = Integer.valueOf(status.toString());
			}
			statusText = HttpStatus.valueOf(statusCode).getReasonPhrase();
			model.addAttribute(INDEX_STATUS, statusCode);
			model.addAttribute(INDEX_ERROR, statusText);
			if (statusCode == 200) {
				request.getSession().removeAttribute(INDEX_TOMBOLA_EXCEPTION);
			} else {
				String path = (String) request.getAttribute(INDEX_JAVAX_SERVLET_FORWARD_REQUEST_URI);
				if (!Arrays.asList(INDEX_TOMBOLA_H2_CONSOLE).contains(path)) {
					LOG.error("GetError error:'{}' exception:'{}' code:{} path:'{}'", statusText,
							request.getAttribute(RequestDispatcher.ERROR_EXCEPTION), statusCode, path);
					model.addAttribute(statusText);
				}
			}
			addModel(request, model);
			// model.asMap().forEach((key, value) -> { LOG.debug("key:{} value:{}", key,
			// value.toString()); });
		} catch (UsernameNotFoundException | BadCredentialsException | LockedException | InternalServerError e) {
			if (e instanceof InternalServerError) {
				LOG.error(e.getMessage() + " " + e.getStackTrace(), e);
			} else {
				LOG.error(e.getMessage(), e);
			}
		}
		return INDEX_ERROR;
	}

	/**
	 * 
	 * Add Attribute
	 * 
	 * @param cnt        long
	 * @param max        int
	 * @param page       {@link AtomicInteger}
	 * @param sumaCnt    {@link AtomicInteger}
	 * @param sumaIssued {@link AtomicInteger}
	 * @param prize      {@link List}&lt;{@link PrizeInfo}&gt;
	 * @param model      {@link Model}
	 */
	protected void addAttribute(long cnt, int max, AtomicInteger page, AtomicInteger sumaCnt, AtomicInteger sumaIssued,
			List<PrizeInfo> prize, Model model) {
		model.addAttribute(INDEX_TICKET_COUNT, cnt);
		model.addAttribute(INDEX_TICKET_MAX, max);
		model.addAttribute(INDEX_TICKET_PAGE, page);
		model.addAttribute(INDEX_PRIZE_SUM, sumaCnt);
		model.addAttribute(INDEX_PRIZE_ISSUED, sumaIssued);
		model.addAttribute(INDEX_PRIZE, prize);
		setPage(page, max, model);
	}

	/**
	 * 
	 * Get Page
	 * 
	 * @param pg         {@link Integer}
	 * @param max        {@link Integer}
	 * @param sumaCnt    {@link AtomicInteger}
	 * @param sumaIssued {@link AtomicInteger}
	 * @param prize      {@link List}&lt;{@link PrizeInfo}&gt;
	 * @return {@link AtomicInteger}
	 */
	protected AtomicInteger getPage(@Nullable Integer pg, Integer max, AtomicInteger sumaCnt, AtomicInteger sumaIssued,
			@Nullable List<PrizeInfo> prize) {
		AtomicInteger page = new AtomicInteger();
		if (pg == null) {
			page.set(0);
		} else {
			page.set(pg);
			if (page.get() < 0 || page.get() > max) {
				page.set(0);
			}
		}
		if (prize != null) {
			for (PrizeInfo p : prize) {
				sumaCnt.addAndGet(p.getCnt());
				sumaIssued.addAndGet(p.getIssued());
			}
		}
		return page;
	}

	/**
	 * 
	 * Set page
	 * 
	 * @param page  {@link AtomicInteger}
	 * @param max   int
	 * @param model {@link Model}
	 */
	protected void setPage(AtomicInteger page, int max, Model model) {
		if (page.get() > 0) {
			model.addAttribute(INDEX_PREV, page.get() - 1);
		} else {
			model.addAttribute(INDEX_PREV, page.get());
		}
		if (page.get() < max) {
			model.addAttribute(INDEX_NEXT, page.get() + 1);
		} else {
			model.addAttribute(INDEX_NEXT, page.get());
		}
	}

	/**
	 * 
	 * Get error message from session
	 * <p>
	 * Used {@link BadCredentialsException} and {@link LockedException}
	 * 
	 * @param request {@link HttpServletRequest}
	 * @param key     {@link String}
	 * @param model   {@link Model}
	 * @return error message {@link String}
	 */
	protected String getErrorMessage(HttpServletRequest request, String key, Model model) {
		String ret;
		Object object = request.getSession().getAttribute(key);
		if (object != null && object instanceof Exception) {
			Exception exception = (Exception) object;
			if (exception instanceof UsernameNotFoundException) {
				ret = INDEX_ERROR_USERNAME_NOT_FOUND;
				model.addAttribute(INDEX_GET_ERROR, ret);
			} else if (exception instanceof BadCredentialsException) {
				ret = INDEX_ERROR_INVALID_USERNAME_AND_PASSWORD;
				model.addAttribute(INDEX_GET_ERROR, ret);
			} else if (exception instanceof LockedException) {
				ret = INDEX_ERROR_USERNAME_IS_LOCKED;
				model.addAttribute(INDEX_GET_ERROR, ret);
			} else if (exception instanceof InternalServerError) {
				ret = exception.getMessage() + " " + exception.getStackTrace();
				model.addAttribute(INDEX_GET_ERROR, ret);
				LOG.warn(ret, exception);
			} else if (exception instanceof TombolaException) {
				ret = exception.getMessage();
				model.addAttribute(INDEX_GET_ERROR, ret);
				LOG.warn(ret, exception);
			} else if (exception instanceof Exception) {
				ret = exception.getMessage();
				model.addAttribute(INDEX_GET_ERROR, ret);
			} else {
				ret = StatusController.STATUS_OK;
			}
		} else {
			ret = StatusController.STATUS_OK;
		}
		return ret;
	}

	/**
	 * 
	 * Add model for every index links
	 * 
	 * @param request {@link HttpServletRequest}
	 * @param model   {@link Model}
	 */
	protected synchronized void addModel(HttpServletRequest request, Model model) {
		model.addAttribute(INDEX_HEADER_SOFTWARE, headerSoftware);
		model.addAttribute(INDEX_ACTIVE_PROFILES, statusController.getActiveProfiles());
		model.addAttribute(INDEX_STAGE, stage);
		model.addAttribute(INDEX_SERVER_NAME, statusController.getServerName());
		model.addAttribute(INDEX_SERVER_SOFTWARE, statusController.getServerSoftware());
		model.addAttribute(INDEX_USER_IS_AUTHENTICATED, userService.isAuthenticated());
		String username = userService.getUsername();
		if (username.length() > 0) {
			model.addAttribute(INDEX_USERNAME, username);
		} else {
			model.addAttribute(INDEX_USERNAME, "");
		}
		model.addAttribute(INDEX_USER_ROLES, userService.getUserRoles());
		model.addAttribute(INDEX_ROLE, String.join(" ", userService.getAuthoritiesRoles()));
		String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
		if (methodName.equals(INDEX_GET_ERROR)) {
			String err = getErrorMessage(request, INDEX_TOMBOLA_EXCEPTION, model);
			if (!err.equals(StatusController.STATUS_OK)) {
				LOG.warn("AddModel error:'{}' message:'{}' code:{} path:'{}'", model.getAttribute(INDEX_ERROR), err,
						model.getAttribute(INDEX_STATUS),
						request.getAttribute(INDEX_JAVAX_SERVLET_FORWARD_REQUEST_URI));
			}
		}
		// LOG.debug("username:'{}' authenticated:{}", username, isAuthenticated);
		// model.asMap().forEach((key, value) -> { LOG.debug("key:{} value:{}", key,
		// value.toString()); });
	}

}