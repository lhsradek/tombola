package local.intranet.tombola.api.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.ServletContext;
import javax.servlet.SessionTrackingMode;
import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.session.web.context.AbstractHttpSessionApplicationInitializer;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *
 * {@link ApplicationConfig} for
 * {@link local.intranet.tombola.TombolaApplication}.
 * <p>
 * JSON configuration is in classpath:/prize-${spring.profiles.active}.json (for
 * example in
 * /usr/share/tomcat/webapps/tombola/WEB-INF/classes/prize-production.json) This
 * is reading with {@link local.intranet.tombola.api.config.JsonFactory}.
 * <p>
 * https://www.baeldung.com/database-auditing-jpa
 * 
 * @author Radek Kádner
 *
 */
@Configuration
@EnableAutoConfiguration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
@ConfigurationProperties(prefix = "")
@PropertySource(value = {
		"classpath:/prize-${spring.profiles.active}.json" }, name = "prizeConfig: [classpath:/prize.json]", ignoreResourceNotFound = false, factory = JsonFactory.class)
public class ApplicationConfig extends AbstractHttpSessionApplicationInitializer implements WebApplicationInitializer {

	// private static final Logger LOG =
	// LoggerFactory.getLogger(ApplicationConfig.class);

	private List<Map<String, Object>> prizes; // classpath:/prize-${spring.profiles.active}.json

	@Autowired(required = false)
	private Flyway flyway;

	@Autowired
	private ServletContext servletContext;

	/**
	 * 
	 * Primary data source
	 * 
	 * @return {@link DataSource}
	 */
	@ConfigurationProperties(prefix = "spring.datasource")
	@ConditionalOnExpression("${#strings.length(spring.datasource.url) > 0}")
	public DataSource dataSource() {
		DataSource ret = DataSourceBuilder.create().build();
		return ret;
	}

	/**
	 * 
	 * Secondary data source
	 * <p>
	 * https://stackoverflow.com/questions/30337582/spring-boot-configure-and-use-two-datasources
	 * 
	 * @return {@link DataSource}
	 */
	@ConfigurationProperties(prefix = "spring.secondaryDatasource")
	@ConditionalOnExpression("${#strings.length(spring.secondaryDatasource.url) > 0}")
	public DataSource secondaryDataSource() {
		DataSource ret = DataSourceBuilder.create().build();
		return ret;
	}

	/**
	 * 
	 * auditorProvider
	 * 
	 * @return {@link AuditorAware}&lt;{@link String}&gt;
	 */
	@Bean
	public AuditorAware<String> auditorProvider() {
		return new AuditorAwareImpl();
	}

	/**
	 * 
	 * Set HttpSessionEventPublisher
	 * <p>
	 * https://www.baeldung.com/spring-security-session
	 * 
	 * @return {@link HttpSessionEventPublisher}
	 */
	@Bean
	public HttpSessionEventPublisher sessionEventPublisher() {
		HttpSessionEventPublisher ret = new HttpSessionEventPublisher();
		servletContext.setSessionTrackingModes(EnumSet.of(SessionTrackingMode.COOKIE));
		return ret;
	}

	/**
	 * 
	 * Is Flyway ?
	 * 
	 * @return true if flyway exists ${spring.flyway.enabled} == true
	 */
	@Bean
	public boolean isFlyway() {
		return flyway != null;
	}

	/**
	 * 
	 * faviconHandlerMapping
	 * <p>
	 * https://www.baeldung.com/spring-boot-favicon
	 * <p>
	 * <code>
	 * <b>setUrlMap</b>(Collections.singletonMap("<b>/favicon.*</b>", faviconRequestHandler()))
	 * </code>
	 * 
	 * @return {@link SimpleUrlHandlerMapping}
	 */
	@Bean
	public SimpleUrlHandlerMapping faviconHandlerMapping() {
		SimpleUrlHandlerMapping ret = new SimpleUrlHandlerMapping();
		ret.setOrder(Integer.MIN_VALUE);
		ret.setUrlMap(Collections.singletonMap("/favicon.*", faviconRequestHandler()));
		return ret;
	}

	/**
	 * 
	 * faviconRequestHandler
	 * <p>
	 * https://www.baeldung.com/spring-boot-favicon
	 * 
	 * @return {@link ResourceHttpRequestHandler}
	 */
	@Bean
	protected ResourceHttpRequestHandler faviconRequestHandler() {
		ResourceHttpRequestHandler ret = new ResourceHttpRequestHandler();
		ret.setLocationValues(Arrays.asList("res/"));
		return ret;
	}

	/**
	 * 
	 * DEFAULT_TIMEZONE objectMapper.setTimeZone(TimeZone.getDefault());
	 * 
	 * @param objectMapper {@link com.fasterxml.jackson.databind.ObjectMapper}
	 */
	@Autowired
	public void configureJackson(ObjectMapper objectMapper) {
		objectMapper.setTimeZone(TimeZone.getDefault());
		// LOG.debug("ObjectMapper.setTimeZone({})", TimeZone.getDefault().getID());
	}

	/**
	 * 
	 * Get Prizes from classpath:/prize-${spring.profiles.active}.json
	 * 
	 * @return {@link List}&lt;{@link Map}&lt;{@link String},{@link Object}&gt;&gt;
	 */
	public List<Map<String, Object>> getPrizes() {
		List<Map<String, Object>> ret = prizes;
		return ret;
	}

	/**
	 * 
	 * Set prizes from classpath:/prize-${spring.profiles.active}.json
	 * <p>
	 * Used {@link local.intranet.tombola.api.service.TombolaService#putPrizes}
	 * 
	 * @param prizes {@link List}&lt;{@link Map}&lt;{@link String},{@link Object}&gt;&gt;
	 */
	public void setPrizes(List<Map<String, Object>> prizes) {
		this.prizes = prizes;

	}

}
