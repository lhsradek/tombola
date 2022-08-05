package local.intranet.tombola.api.listener;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * 
 * {@link EndpointsListener} for
 * {@link local.intranet.tombola.TombolaApplication}
 * 
 * Get all end points
 * 
 * @author Radek Kádner
 *
 */
@Component
public class EndpointsListener implements ApplicationListener<ContextRefreshedEvent> {

	private static final Logger LOG = LoggerFactory.getLogger(EndpointsListener.class);

	@Value("${tombola.app.endPointListenerLog:false}")
	private boolean endPointListenerLog;

	private ApplicationContext applicationContext;

	private static final Set<String> LINKS = new ConcurrentSkipListSet<>();

	/**
	 * 
	 * Handle context refresh
	 * 
	 * @param event ContextRefreshedEvent
	 */
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		applicationContext = event.getApplicationContext();
		applicationContext.getBean(RequestMappingHandlerMapping.class).getHandlerMethods()
				.forEach((key, value) -> addLink(key, value));
		if (endPointListenerLog) {
			for (String s : LINKS) {
				LOG.debug(s);
			}
		}
	}

	/**
	 * 
	 * Add link to LINKS
	 * 
	 * @param key   RequestMappingInfo
	 * @param value HandlerMethod
	 */
	protected void addLink(RequestMappingInfo key, HandlerMethod value) {
		String k = String.join(" ", key.getPatternsCondition().getPatterns());
		if (!LINKS.contains(k)) {
			LINKS.add(k);
		}
	}

}