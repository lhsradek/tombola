package local.intranet.tombola.api.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.util.StringUtils;

/**
 * 
 * {@link LogoutSuccess} for
 * {@link local.intranet.tombola.api.security.SecurityConfig#configure(org.springframework.security.config.annotation.web.builders.HttpSecurity)}
 * <br>
 * https://www.baeldung.com/spring-security-logout
 * 
 * @author Radek Kádner
 *
 */
public class LogoutSuccess extends SimpleUrlLogoutSuccessHandler implements LogoutSuccessHandler {

	private static final Logger LOG = LoggerFactory.getLogger(LogoutSuccess.class);

	/**
	 * 
	 * Logout info of user. Clean session, call super.onLogoutSuccess(request,
	 * response, authentication) and write to LOG event Logout.
	 * <p>
	 * Login is in
	 * {@link local.intranet.tombola.api.controller.IndexController#signin}
	 */
	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) {
		// auditService.track("Logout from: " + refererUrl);
		try {
			String id = request.getSession().getId();
			String refererUrl = request.getHeader("Referer");
			if (request.getCookies() != null) {
				for (Cookie cookie : request.getCookies()) {
					if (cookie != null) {
						String cookieName = cookie.getName();
						Cookie cookieToDelete = new Cookie(cookieName, null);
						cookieToDelete.setMaxAge(0);
						response.addCookie(cookieToDelete);
					}
				}
			}
			;
			String username = "";
			if (authentication != null) {
				if (authentication.getPrincipal() != null) {
					// LOG.info("Logout authentication:'{}' principal:'{}' sessionId:{}",
					// authentication, authentication.getPrincipal(), id);
					username = authentication.getName();
				}
				LOG.info("Logout username:'{}' refererUrl:'{}' sessionId:{}", username, refererUrl, id);
				super.onLogoutSuccess(request, response, authentication);
			}
			request.getSession().invalidate();
		} catch (ServletException | IOException e) {
			LOG.error(e.getMessage(), e);
		}
	}

	/**
	 * 
	 * Builds the target URL according to the logic defined in the main class
	 * Javadoc.
	 * 
	 * @param request  {@link javax.servlet.http.HttpServletRequest}
	 * @param response {@link javax.servlet.http.HttpServletResponse}
	 * @return {@link String}
	 */
	@Override
	protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response) {
		if (isAlwaysUseDefaultTargetUrl()) {
			return getDefaultTargetUrl();
		}
		String targetUrl = null;
		if (getTargetUrlParameter() != null) {
			targetUrl = request.getParameter(getTargetUrlParameter());
			if (StringUtils.hasText(targetUrl)) {
				// LOG.debug("Found targetUrlParameter in request: " + targetUrl);
				return targetUrl;
			}
		}
		if (!StringUtils.hasText(targetUrl)) {
			targetUrl = getDefaultTargetUrl();
			// LOG.debug("Using default Url: " + targetUrl);
		}
		return targetUrl;
	}

}