package local.intranet.tombola.api.config;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 
 * {@link AuditorAwareImpl} for {@link local.intranet.tombola.TombolaApplication}
 * <p>
 * https://www.baeldung.com/database-auditing-jpa
 *
 */
public class AuditorAwareImpl implements AuditorAware<String> {

	/**
	 * 
	 * Get current auditor
	 * @return {@link Optional}&lt;{@link String}&gt;
	 */
    @Override
    public Optional<String> getCurrentAuditor() {
    	Optional<String> ret = Optional.ofNullable(SecurityContextHolder.getContext())
                .map(e -> e.getAuthentication())
                .map(Authentication::getName); 
        return ret;
    }

}