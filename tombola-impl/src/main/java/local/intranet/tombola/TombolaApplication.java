package local.intranet.tombola;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.web.WebApplicationInitializer;

import local.intranet.tombola.api.controller.StatusController;

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

	/**
	 * 
	 * On startup
	 * <p>
	 * For {@link #onStartup} if logApiBeans == true
	 * <p>
	 * Comment is from overrides
	 * {@link org.springframework.web.WebApplicationInitializer#onStartup}
	 * <p>
	 * Configure the given {@link ServletContext} with any servlets, filters,
	 * listeners context-params and attributes necessary for initializing this web
	 * application. See examples {@linkplain WebApplicationInitializer above}.
	 * 
	 * @param servletContext the {@code ServletContext} to initialize
	 * @throws ServletException if any call against the given {@code ServletContext}
	 *                          throws a {@code ServletException}
	 */
	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		super.onStartup(servletContext);
		if (logApiBeans) {
			logOnStartup(servletContext);
		}
	}

	/**
	 * 
	 * Log on startup
	 * 
	 * @param servletContext {@link ServletContext}
	 */
	protected void logOnStartup(ServletContext servletContext) {
		ApplicationContext applicationContext = super.createRootApplicationContext(servletContext);
		if (applicationContext != null) {
			for (String beanName : applicationContext.getBeanDefinitionNames()) {
				Object bean = applicationContext.getBean(beanName);
				Class<? extends Object> cl = bean.getClass();
				if (StatusController.isBeanSuitable(cl)) {
					String name = cl.getSuperclass().getSimpleName();
					if (name.equals("Object")) {
						name = cl.getSimpleName();
					}
					// LOG.debug("{} {}", beanName, name);
					for (Method m : cl.getSuperclass().getDeclaredMethods()) {
						if (Modifier.isPublic(m.getModifiers()) && !Modifier.isStatic(m.getModifiers())) {
							logAPIBeans(m, cl.getSuperclass());
						}
					}
				}
			}
		}
	}

	/**
	 * 
	 * Log API Beans
	 * <p>
	 * For {@link #onStartup} if logApiBeans == true
	 * 
	 * @param method {@link Method}
	 * @param cl     {@link Class}&lt;? extends {@link Object}&gt;
	 */
	protected void logAPIBeans(Method method, Class<? extends Object> cl) {
		if (StatusController.isBeanSuitable(cl)) {
			String name = method.getName();
			if (StatusController.isNiceBeanName(true, name)) {
				String g = (method == null) ? "" : method.getReturnType().getSimpleName();
				List<Type> p = (method == null) ? Arrays.asList() : Arrays.asList(method.getGenericParameterTypes());
				if (p.size() > 0) {
					LOG.debug("LogAPIBeans bean:'{}' name:'{}' return:'{}' types:'{}'", cl.getSimpleName(), name, g, p);
				} else {
					LOG.debug("LogAPIBeans bean:'{}' name:'{}' return:'{}'", cl.getSimpleName(), name, g);
				}
			}
		}
	}

}

// @EnableJdbcHttpSession(maxInactiveIntervalInSeconds = 120, cleanupCron = "0 */5 * * * *")
