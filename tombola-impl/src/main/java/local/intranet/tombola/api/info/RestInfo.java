package local.intranet.tombola.api.info;

import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import local.intranet.tombola.api.domain.DefaultFieldLengths;
import local.intranet.tombola.api.domain.Nameable;

/**
 * 
 * {@link RestInfo} for {@link local.intranet.tombola.api.service.IndexService#getIndexInfo} and
 * {@link local.intranet.tombola.api.info.IndexInfo}
 * 
 * @author Radek Kádner
 *
 */
@JsonPropertyOrder({ "link", "name", "role" })
public class RestInfo implements Nameable {

	@Size(min = 1, max = DefaultFieldLengths.DEFAULT_NAME)
	private final String link;

    @Size(min = 1, max = DefaultFieldLengths.DEFAULT_NAME)
	private final String name;
    
    @Size(min = 1, max = DefaultFieldLengths.DEFAULT_STATUS)
	private final String role;
	
	/**
	 * 
	 * Constructor with parameters
	 * 
	 * @param link    {@link String}
	 * @param name    {@link String}
	 * @param role    {@link String}
	 */
	public RestInfo(String link, String name, String role) {
		this.link = link;
		this.name = name;
		this.role = role;
	}

	/**
	 * 
	 * Get link
	 * @return the link
	 */
	public String getLink() {
		return link;
	}

	@Override
	public String getName() {
		return name;
	}

    /**
	 * 
	 * Get role
	 * @return the role
	 */
	public String getRole() {
		return role;
	}

}
