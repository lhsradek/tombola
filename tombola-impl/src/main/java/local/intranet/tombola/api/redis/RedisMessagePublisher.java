package local.intranet.tombola.api.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

/**
 * 
 * {@link RedisMessagePublisher} for {@link local.intranet.tombola.TombolaApplication}.
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
public class RedisMessagePublisher implements MessagePublisher {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    private ChannelTopic topic;

    /**
     *
     * Constructor with parameters
     * 
     * @param redisTemplate {@link RedisTemplate}&lt;{@link String}, {@link Object}&gt; 
     * @param topic {@link ChannelTopic}
     */
    public RedisMessagePublisher(final RedisTemplate<String, Object> redisTemplate, final ChannelTopic topic) {
        this.redisTemplate = redisTemplate;
        this.topic = topic;
    }

    @Override
    public void publish(final String message) {
        redisTemplate.convertAndSend(topic.getTopic(), message);
    }
    
}