package local.intranet.tombola.api.config;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper; // version 2.11.1

/**
 * 
 * {@link JsonFactory} for
 * {@link local.intranet.tombola.api.config.ApplicationConfig}
 * 
 * JsonFactory reads configuration is in
 * classpath:/prize-${spring.profiles.active}.json (for example in
 * /usr/share/tomcat/webapps/tombola/WEB-INF/classes/prize-production.json)
 * 
 * @author Radek Kádner
 *
 */
public class JsonFactory implements PropertySourceFactory {

	private static final Logger LOG = LoggerFactory.getLogger(JsonFactory.class);

	/**
	 * 
	 * Create a {@link PropertySource} that wraps the given resource.
	 * 
	 * @param name     {@link String} the name of the property source (can be
	 *                 {@code null} in which case the factory implementation will
	 *                 have to generate a name based on the given resource)
	 * @param resource {@link EncodedResource}
	 * 
	 * @return the new {@link PropertySource} (never {@code null})
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	@Override
	public PropertySource<?> createPropertySource(String name, EncodedResource resource)
			throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper ob = new ObjectMapper();
		@SuppressWarnings("unchecked")
		Map<String, Object> readValue = ob.readValue(resource.getInputStream(), Map.class);
		// LOG.debug("name:{} readValue:{}", name, readValue);
		LOG.debug("name:{}", name);
		MapPropertySource ret = new MapPropertySource(name, readValue);
		return ret;
	}

}
