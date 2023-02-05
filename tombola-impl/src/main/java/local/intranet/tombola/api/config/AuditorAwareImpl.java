package local.intranet.tombola.api.config;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 
 * {@link AuditorAwareImpl} for
 * {@link local.intranet.tombola.TombolaApplication}
 * <p>
 * https://www.baeldung.com/database-auditing-jpa
 *
 */
public class AuditorAwareImpl implements AuditorAware<String> {

	/**
	 * 
	 * Get current auditor
	 * 
	 * @return {@link Optional}&lt;{@link String}&gt;
	 */
	@Override
	public Optional<String> getCurrentAuditor() {
		final Optional<String> ret;
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (SecurityContextHolder.getContext().getAuthentication() != null && authentication.isAuthenticated()) {
			ret = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication().getName());
		} else {
			ret = Optional.empty();
		}
		return ret;
	}

}