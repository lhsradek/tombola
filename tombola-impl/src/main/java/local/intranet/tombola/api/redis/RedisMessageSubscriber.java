package local.intranet.tombola.api.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

/**
 * 
 * {@link RedisMessageSubscriber} for {@link local.intranet.tombola.TombolaApplication}.
 * <p>
 * https://www.baeldung.com/spring-data-redis-tutorial
 * <br>
 * https://www.baeldung.com/spring-data-redis-pub-sub
 * <p>
 * <img src="/tombola/res/redis.png"/>
 * <p>
 *
 */
@Service
public class RedisMessageSubscriber implements MessageListener {

	private static final Logger LOG = LoggerFactory.getLogger(RedisMessageSubscriber.class);

	/**
	 * 
	 * On message
	 * 
	 * @param message {@link Message}
	 * @param pattern byte[]
	 */
	@Override
    public void onMessage(final Message message, @Nullable byte[] pattern) {
		LOG.info("OnMessage '{}'", new String(message.getBody()));
    }
}
