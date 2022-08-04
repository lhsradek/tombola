package local.intranet.tombola.api.redis;

/**
 * 
 * {@link MessagePublisher} for {@link local.intranet.tombola.TombolaApplication}.
 * <p>
 * https://www.baeldung.com/spring-data-redis-tutorial
 * <p>
 * <img src="/tombola/res/redis.png"/>
 * <p>
 *
 */
public interface MessagePublisher {

	/**
	 * 
	 * Publish message
	 * 
	 * @param message {@link String}
	 */
    void publish(final String message);
    
}