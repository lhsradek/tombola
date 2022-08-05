package local.intranet.tombola.api.service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;

import local.intranet.tombola.api.info.content.LoginAttemptCacheLoader;

/**
 * 
 * {@link LoginAttemptService} for
 * {@link local.intranet.tombola.TombolaApplication}
 * <p>
 * https://www.baeldung.com/spring-security-block-brute-force-authentication-attempts
 */
@Service
public class LoginAttemptService {

	private static final Logger LOG = LoggerFactory.getLogger(LoginAttemptService.class);

	private static final Integer MAX = 10000;

	@Value("${tombola.app.login.maxAttemt}")
	private int maxAttempt;

	@Value("${tombola.app.login.waitSec}")
	private int waitSec;

	@Value("${tombola.app.attempts.invalidateKey}")
	private boolean invalidateKey;

	@Value("${tombola.app.attempts.printZero}")
	private boolean printZero;

	private LoadingCache<String, Integer> loginAttemptCache;

	/**
	 * 
	 * Init be executed after injecting this service.
	 */
	@PostConstruct
	public void init() {
		if (invalidateKey) {
			loginAttemptCache = CacheBuilder.newBuilder().expireAfterWrite(Duration.ofSeconds(waitSec)).maximumSize(MAX)
					.build(new LoginAttemptCacheLoader<String, Integer>());

		} else {
			loginAttemptCache = CacheBuilder.newBuilder().maximumSize(MAX)
					.build(new LoginAttemptCacheLoader<String, Integer>());

		}
		// LOG.debug("maxAttempt:{} waitSec:{} invalidateKey:{}", maxAttempt, waitSec,
		// invalidateKey);
	}

	/**
	 * 
	 * Find by id
	 * 
	 * @param key {@link String}
	 * @return {@link Integer}
	 */
	public synchronized Integer findById(@NotNull String key) {
		int ret = 0;
		try {
			ret = loginAttemptCache.get(key);
		} catch (ExecutionException e) {
			LOG.error(e.getMessage(), e);
		}
		// LOG.debug("key '{}' ret:{}", key, ret);
		return ret;
	}

	/**
	 * 
	 * Get attempts
	 * 
	 * @return {@link List}&lt;{@link Map.Entry}&lt;{@link String},
	 *         {@link Integer}&gt;&gt;
	 */
	public synchronized List<Map.Entry<String, Integer>> getAttempts() {
		List<Map.Entry<String, Integer>> ret = new ArrayList<>();
		for (Map.Entry<String, Integer> s : loginAttemptCache.asMap().entrySet()) {
			if (printZero || s.getValue() > 0) {
				ret.add(Map.entry(s.getKey(), s.getValue()));
			}
		}
		// LOG.debug("ret:{}", ret);
		return ret;
	}

	/**
	 * 
	 * Login succeeded
	 * 
	 * @param key {@link String}
	 */
	public synchronized void loginSucceeded(@NotNull String key) {
		if (invalidateKey) {
			loginAttemptCache.invalidate(key);
		} else {
			loginAttemptCache.put(key, 0);
		}
		// LOG.debug("key:'{}'", key);
	}

	/**
	 * 
	 * Login failed
	 * 
	 * @param key {@link String}
	 */
	public synchronized void loginFailed(@NotNull String key) {
		int attempt = 0;
		try {
			attempt = loginAttemptCache.get(key);
		} catch (ExecutionException e) {
			LOG.error(e.getMessage(), e);
		}
		attempt++;
		loginAttemptCache.put(key, attempt);
		// LOG.debug("key:'{}' attempts:{}", key, attempts);
	}

	/**
	 * 
	 * Is blocked
	 * 
	 * @param key {@link String}
	 * @return {@link Boolean}
	 */
	public synchronized Boolean isBlocked(@NotNull String key) {
		boolean ret;
		int attempt = 0;
		try {
			attempt = loginAttemptCache.get(key);
		} catch (ExecutionException e) {
			LOG.error(e.getMessage(), e);
		}
		ret = attempt >= maxAttempt;
		// LOG.debug("key:'{}' ret:{} attemts:{} maxAttemts:{}", key, ret, attempts,
		// maxAttempt);
		return ret;
	}

}