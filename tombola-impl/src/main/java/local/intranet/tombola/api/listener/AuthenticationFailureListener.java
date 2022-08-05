package local.intranet.tombola.api.listener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import local.intranet.tombola.api.controller.IndexController;
import local.intranet.tombola.api.controller.StatusController;
import local.intranet.tombola.api.service.LoginAttemptService;

/**
 * 
 * {@link AuthenticationFailureListener} for
 * {@link local.intranet.tombola.TombolaApplication}
 * <p>
 * https://www.baeldung.com/spring-security-block-brute-force-authentication-attempts
 *
 * @author Radek Kádner
 * 
 */
@Component
public class AuthenticationFailureListener implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

	private static final Logger LOG = LoggerFactory.getLogger(AuthenticationFailureListener.class);

	private static final String FAILED_USERNAME = "Failed username:'{}' authorities:'{}' attempt:{}";

	@Autowired
	private StatusController statusController;

	@Autowired
	private LoginAttemptService loginAttemptService;

	/**
	 * 
	 * Application event listener
	 */
	@Override
	public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent e) {
		String ip = statusController.getClientIP();
		loginAttemptService.loginFailed(ip);
		List<String> arr = new ArrayList<>();
		if (e.getAuthentication() != null) {
			for (GrantedAuthority g : e.getAuthentication().getAuthorities()) {
				arr.add(g.getAuthority().replace(IndexController.INDEX_ROLE_PREFIX, ""));
			}
			Collections.sort(arr);
		}
		LOG.warn(FAILED_USERNAME, e.getAuthentication().getName(), arr, ip, loginAttemptService.findById(ip));
	}
}