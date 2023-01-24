package local.intranet.tombola.api.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import local.intranet.tombola.api.controller.IndexController;
import local.intranet.tombola.api.controller.StatusController;
import local.intranet.tombola.api.domain.type.RoleType;
import local.intranet.tombola.api.info.UserInfo;
import local.intranet.tombola.api.model.entity.User;
import local.intranet.tombola.api.model.repository.UserRepository;
import local.intranet.tombola.api.security.LogoutSuccess;

/**
 * 
 * {@link UserService} for
 * {@link local.intranet.tombola.api.controller.IndexController#signin} and
 * {@link local.intranet.tombola.api.security.SecurityConfig#configure(AuthenticationManagerBuilder)}
 * 
 * @author Radek Kádner
 *
 */
@Service
public class UserService implements UserDetailsService {

	private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

	/**
	 * 
	 * USER_ANONYMOUS = "ANONYMOUS"
	 */
	public static final String USER_ANONYMOUS = "ANONYMOUS";

	private static final int USER_LOGIN_SESSION_MAX_INACTIVE_INTERVAL = 3600;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private HttpSession httpSession;

	@Autowired
	private LoginAttemptService loginAttemptService;

	@Autowired
	private StatusController statusController;

	/**
	 * 
	 * Bean for logout
	 * {@link local.intranet.bttf.api.security.LogoutSuccess#onLogoutSuccess}.
	 * <p>
	 * Login is in {@link local.intranet.bttf.api.controller.IndexController#signin}
	 * 
	 * @return {@link LogoutSuccess}
	 */
	@Bean
	public LogoutSuccessHandler logoutSuccess() {
		LogoutSuccessHandler ret = new LogoutSuccess();
		return ret;
	}

	/**
	 * 
	 * For {@link local.intranet.tombola.api.controller.InfoController#getUserInfo}
	 * 
	 * @return {@link UserInfo}
	 */
	@Transactional(readOnly = true)
	public UserInfo getUserInfo() {
		UserInfo ret = loadUserByUsername(getUsername());
		return ret;
	}

	/**
	 * 
	 * Locates the user based on the username.
	 *
	 * @param username the username identifying the user whose data is required.
	 * @return a fully populated user record (never <code>null</code>)
	 * @throws LockedException           if the user is locked.
	 * @throws UsernameNotFoundException if the user could not be found or the user
	 *                                   has no GrantedAuthority
	 * @throws BadCredentialsException   credentials are invalid
	 * @throws AccountExpiredException   if an authentication request is rejected
	 *                                   because the account has expired. Makes no
	 *                                   assertion as to whether or not the
	 *                                   credentials were valid.
	 */
	@Override
	@Transactional(readOnly = true)
	public UserInfo loadUserByUsername(@NotNull String username)
			throws LockedException, UsernameNotFoundException, BadCredentialsException, AccountExpiredException {
		UserInfo ret;
		String ip = statusController.getClientIP();
		if (loginAttemptService.isBlocked(ip)) {
			throw new LockedException(IndexController.INDEX_ERROR_USERNAME_IS_LOCKED);
		}
		try {
			User user = userRepository.findByName(username);
			if (user == null) {
				loginAttemptService.loginFailed(ip);
				// throw new
				// UsernameNotFoundException(IndexController.INDEX_ERROR_USERNAME_NOT_FOUND);
				throw new BadCredentialsException(IndexController.INDEX_ERROR_INVALID_USERNAME_AND_PASSWORD);
			} else if (user.isAccountNonExpired() && user.isAccountNonLocked() && user.isCredentialsNonExpired()
					&& user.isEnabled()) {
				List<GrantedAuthority> authorities = user.getRole().stream()
						.map(role -> new SimpleGrantedAuthority(IndexController.INDEX_ROLE_PREFIX + role.getRoleName()))
						.collect(Collectors.toList());
				ret = UserInfo.build(user, authorities);
			} else {
				if (!user.isCredentialsNonExpired()) {
					throw new BadCredentialsException(IndexController.INDEX_ERROR_BAD_CREDENTIALS);
				} else if (!user.isAccountNonExpired()) {
					throw new AccountExpiredException(IndexController.INDEX_ERROR_ACCOUNT_EXPIRED);
				}
				throw new LockedException(IndexController.INDEX_ERROR_USERNAME_IS_LOCKED);
			}
			return ret;
			// LOG.debug("ret:{}", ret);
		} catch (Exception e) {
			LOG.error(e.getClass().getSimpleName(), e);
			throw e;
		}
	}

	/**
	 * 
	 * Get Username
	 * 
	 * @return {@link String}
	 */
	public synchronized String getUsername() {
		String ret = "";
		if (httpSession != null) {
			Object auth = httpSession.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
			if (auth != null && ((SecurityContext) auth).getAuthentication() != null) {
				ret = ((SecurityContext) auth).getAuthentication().getName();
				if (httpSession.getMaxInactiveInterval() < USER_LOGIN_SESSION_MAX_INACTIVE_INTERVAL) {
					httpSession.setMaxInactiveInterval(USER_LOGIN_SESSION_MAX_INACTIVE_INTERVAL);
				}
			}
		}
		return ret;
	}

	/**
	 * 
	 * Is authenticated
	 * 
	 * @return {@link Boolean}
	 */
	public synchronized Boolean isAuthenticated() {
		List<String> list = getAuthoritiesRoles();
		boolean ret = (list.size() > 0 && !list.get(0).equals(USER_ANONYMOUS)) ? true : false;
		// ret = (getUsername().length() > 0) ? true : false;
		return ret;
	}

	/**
	 * 
	 * Get authorities roles
	 * 
	 * @return {@link List}&lt;{@link String}&gt;
	 */
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public synchronized List<String> getAuthoritiesRoles() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		List<String> ret = new ArrayList<>();
		if (auth != null) {
			for (GrantedAuthority g : auth.getAuthorities()) {
				ret.add(g.getAuthority().replace(IndexController.INDEX_ROLE_PREFIX, ""));
			}
			Collections.sort(ret);
		}
		// LOG.debug("ret:{}", ret);
		return ret;
	}

	/**
	 *
	 * All user's roles
	 * <p>
	 * 
	 * &#64;JsonIgnore
	 * 
	 * @return get all roles with boolean for Heavy Check &#x2714; or Heavy Ballot
	 *         &#x2718; if user haves role in
	 *         {@link Map}&lt;{@link String},{@link Boolean}&gt; It's displayed in
	 *         {@link local.intranet.tombola.api.controller.IndexController#getLogin}
	 *         if user is logged.
	 */
	@JsonIgnore
	public synchronized Map<String, Boolean> getUserRoles() {
		Map<String, Boolean> ret = Collections.synchronizedMap(new TreeMap<>());
		List<String> list = getAuthoritiesRoles();
		for (RoleType role : RoleType.values()) {
			if (!role.equals(RoleType.ANONYMOUS_ROLE))
				ret.put(role.getRole().replace(IndexController.INDEX_ROLE_PREFIX, ""), list.contains(role.getRole()));
		}
		// LOG.debug("{}", ret);
		return ret;
	}

}
