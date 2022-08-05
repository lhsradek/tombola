package local.intranet.tombola.api.domain;

import java.util.List;

import local.intranet.tombola.api.info.RestInfo;

/**
 * 
 * {@link Componented} for {@link local.intranet.tombola.api.info.IndexInfo}
 * 
 * @author Radek Kádner
 *
 */
public interface Componented {

	/**
	 * 
	 * Get service
	 * 
	 * @return {@link List}&lt;{@link local.intranet.tombola.api.info.RestInfo}&gt;
	 */
	List<RestInfo> getComponents();

}
