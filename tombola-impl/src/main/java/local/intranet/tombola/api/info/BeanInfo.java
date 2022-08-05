package local.intranet.tombola.api.info;

import java.util.Map;

import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import local.intranet.tombola.api.domain.DefaultFieldLengths;
import local.intranet.tombola.api.domain.Nameable;
import local.intranet.tombola.api.service.BeanService;

/**
 * 
 * {@link BeanInfo} for
 * {@link local.intranet.tombola.api.service.BeanService#getBeanInfo} and
 * {@link local.intranet.tombola.api.controller.InfoController#getBeanInfo}
 * 
 * @author Radek Kádner
 *
 */
@JsonPropertyOrder({ "name", "beans" })
public class BeanInfo implements Nameable {

	@Size(min = 0)
	private final Map<String, Object> beans;

	/**
	 * 
	 * Constructor with parameters
	 * 
	 * @param beans {@link Map}&lt;{@link String},? extends {@link Object}&gt;
	 */
	public BeanInfo(Map<String, Object> beans) {
		this.beans = beans;
	}

	/**
	 *
	 * Get Beans
	 * 
	 * <p>
	 * &#64;JsonInclude(JsonInclude.Include.NON_NULL)
	 * <p>
	 * 
	 * @return {@link Map}&lt;{@link String},{@link Object}&gt; for
	 *         {@link local.intranet.tombola.api.info.BeanInfo#getBeans}
	 */
	@Size(min = 0)
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public Map<String, Object> getBeans() {
		Map<String, Object> ret = beans;
		return ret;
	}

	@Size(min = 1, max = DefaultFieldLengths.DEFAULT_NAME)
	@Override
	public String getName() {
		String ret = BeanService.class.getSimpleName();
		return ret;
	}

}
