package local.intranet.tombola.api.info.content;

import com.google.common.cache.CacheLoader;

/**
 * 
 * {@link LoginAttemptCacheLoader} for {@link local.intranet.tombola.api.service.LoginAttemptService}
 * 
 * @author Radek Kádner
 */
public class LoginAttemptCacheLoader<K, V> extends CacheLoader<String, Integer> {

	/**
	 * 
	 * Computes or retrieves the value corresponding to {@code key}.
	 */
	@Override
	public Integer load(String key) throws Exception {
		return 0;
	}

}
