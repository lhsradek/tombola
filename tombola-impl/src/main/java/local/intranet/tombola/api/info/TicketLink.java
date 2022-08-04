package local.intranet.tombola.api.info;

import java.util.Arrays;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.authentication.WebAuthenticationDetails;

/**
 * 
 * {@link TicketLink} for {@link local.intranet.tombola.TombolaApplication}
 * 
 * @author Radek Kádner
 *
 */
public class TicketLink extends WebAuthenticationDetails {

	private static final long serialVersionUID = -5064057139138823279L;

	private final String salt;
	
	private final byte[] iv;
	
	/**
	 * 
	 * Constructor with parameters
	 * 
	 * @param request {@link HttpServletRequest}
	 * @param salt    {@link String}
	 * @param iv      byte[]
	 */
	public TicketLink(HttpServletRequest request, String salt, byte[] iv) {
		super(request);
		this.salt = salt;
		this.iv = iv;
	}

	/**
	 * 
	 * Get salt
	 * 
	 * @return the salt
	 */
	public String getSalt() {
		return salt;
	}

	/**
	 * 
	 * Get iv
	 * 
	 * @return the iv
	 */
	public byte[] getIv() {
		return iv;
	}

	/**
	 *
	 * hashCode
	 * 
	 * @return int
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Arrays.hashCode(iv);
		result = prime * result + Objects.hash(salt);
		return result;
	}

	/**
	 * 
	 * Indicates whether some other object is "equal to" this one.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		TicketLink other = (TicketLink) obj;
		return Arrays.equals(iv, other.iv) && Objects.equals(salt, other.salt);
	}

	/**
	 * 
	 * Returns a string representation of the object.
	 */
	@Override
	public String toString() {
		return "TicketLink [salt=" + salt + ", iv=" + Arrays.toString(iv) + "]";
	}
	
}
