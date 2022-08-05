package local.intranet.tombola.api.domain;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 *
 * 
 * {@link Nameable}
 * 
 * @author Radek Kádner
 *
 */
public interface Nameable {

	/**
	 * 
	 * Get name
	 * 
	 * <p>
	 * &#64;JsonInclude(JsonInclude.Include.NON_NULL)
	 * <p>
	 * 
	 * @return name {@link String}
	 */
	@JsonInclude(JsonInclude.Include.NON_NULL)
	String getName();

}
