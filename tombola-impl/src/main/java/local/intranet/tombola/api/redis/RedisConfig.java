package local.intranet.tombola.api.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.GenericToStringSerializer;

/**
 *
 * {@link RedisConfig} for {@link local.intranet.tombola.TombolaApplication}.
 * <p>
 * https://www.baeldung.com/spring-data-redis-tutorial
 * <br/>
 * https://www.baeldung.com/spring-data-redis-properties
 * <br/>
 * https://www.baeldung.com/spring-session
 * <br/>
 * https://www.baeldung.com/jedis-java-redis-client-library
 * <p>
 * <img src="/tombola/res/redis.png"/>
 *  
 */
@Configuration
@EnableAutoConfiguration
// @ComponentScan("local.intranet.tombola.api.redis.model")
// @EnableRedisRepositories(basePackages = "local.intranet.tombola.api.redis.model.repository")
public class RedisConfig {

	private static final String TOMBOLA_MESSAGE_QUEUE = "tombola:messageQueue";
	
	@Value("${spring.redis.password}")
	private String password;
	
	@Value("${spring.redis.database}")
	private Integer database;
	
	@Value("${spring.redis.host}")
	private String host;
	
	@Value("${spring.redis.port}")
	private Integer port;
	
	@Value("${spring.redis.timeout}")
	private Long timeout;
	
	/**
	 * 
	 * Get JedisConnectionFactory
	 * @return {@link JedisConnectionFactory}
	 */
	@Bean
	public JedisConnectionFactory jedisConnectionFactory() {
		JedisConnectionFactory ret = new JedisConnectionFactory();
		ret.getStandaloneConfiguration().setPassword(password);
		ret.getStandaloneConfiguration().setDatabase(database);
		ret.getStandaloneConfiguration().setHostName(host);
		ret.getStandaloneConfiguration().setPort(port);
		ret.getClientConfiguration().getPoolConfig().get().setMaxWaitMillis(timeout);
		return ret;
	}
	
	/**
	 * 
	 * Get redisTemplate
	 * 
	 * @return {@link RedisTemplate}&lt;{@link String}, {@link Object}&gt;
	 */
	@Bean
	public RedisTemplate<String, Object> redisTemplate() {
	    RedisTemplate<String, Object> ret = new RedisTemplate<>();
	    ret.setConnectionFactory(jedisConnectionFactory());
	    ret.setValueSerializer(new GenericToStringSerializer<Object>(Object.class));
	    return ret;
	}
	
	/**
	 * Get messageListener
	 * 
	 * @return {@link MessageListenerAdapter}
	 */
	@Bean
	public MessageListenerAdapter messageListener() {
		MessageListenerAdapter ret = new MessageListenerAdapter(new RedisMessageSubscriber()); 
        return ret;
    }

	/**
	 * 
	 * Get redisContainer
	 * 
	 * @return {@link RedisMessageListenerContainer}
	 */
    @Bean
    public RedisMessageListenerContainer redisContainer() {
        final RedisMessageListenerContainer ret = new RedisMessageListenerContainer();
        ret.setConnectionFactory(jedisConnectionFactory());
        ret.addMessageListener(messageListener(), topic());
        return ret;
    }

    /**
     * 
     * Get redisPublisher
     * 
     * @return {@link MessagePublisher}
     */
    @Bean
    public MessagePublisher redisPublisher() {
        return new RedisMessagePublisher(redisTemplate(), topic());
    }

    /**
     * 
     * Get topic
     * 
     * @return {@link ChannelTopic}
     */
    @Bean
    public ChannelTopic topic() {
        return new ChannelTopic(TOMBOLA_MESSAGE_QUEUE);
    }

}
