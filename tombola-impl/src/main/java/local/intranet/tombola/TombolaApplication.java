package local.intranet.tombola;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.web.WebApplicationInitializer;

/**
 * 
 * {@link TombolaApplication} extends SpringBootServletInitializer for Tombola
 * API that it uses controllers in {@link local.intranet.tombola.api.controller}
 * and early services in {@link local.intranet.tombola.api.service}
 * <p>
 * Security and configuration is defined in
 * {@link local.intranet.tombola.api.security.SecurityConfig} and
 * {@link local.intranet.tombola.api.config.ApplicationConfig}
 * <p>
 * Redis is configured in {@link local.intranet.tombola.api.redis.RedisConfig}
 * <p>
 * For JdbcHttpSession:
 * <p>
 * <code>
 * &#64;EnableJdbcHttpSession(maxInactiveIntervalInSeconds = 120, cleanupCron = ..)
 * </code>
 * <p>
 * 
 * @author Radek Kádner
 *
 */
@SpringBootApplication
@EnableJpaRepositories
@EnableAutoConfiguration
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 120, cleanupCron = "0 */5 * * * *", redisNamespace = "spring:session:tombola")
public class TombolaApplication extends SpringBootServletInitializer implements WebApplicationInitializer {

	private static final Logger LOG = LoggerFactory.getLogger(TombolaApplication.class);

	/**
	 * 
	 * Log API beans = false
	 */
	public final Boolean logApiBeans = false;

	private static final String ENTERING_APPLICATION = "Entering application.";

	/**
	 * 
	 * Configure
	 * <p>
	 * Comment is from overrides
	 * {@link org.springframework.boot.web.servlet.support.SpringBootServletInitializer#configure}
	 * <p>
	 * Configure the application. Normally all you would need to do is to add
	 * sources (e.g. config classes) because other settings have sensible defaults.
	 * You might choose (for instance) to add default command line arguments, or set
	 * an active Spring profile.
	 * 
	 * @param builder a builder for the application context
	 * @return the application builder
	 * @see SpringApplicationBuilder
	 */
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		LOG.info(ENTERING_APPLICATION);
		SpringApplicationBuilder ret = builder.sources(TombolaApplication.class);
		builder.bannerMode(Banner.Mode.OFF);
		return ret;
	}

}

// @EnableJdbcHttpSession(maxInactiveIntervalInSeconds = 120, cleanupCron = "0 */5 * * * *")
