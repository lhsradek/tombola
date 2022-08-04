package local.intranet.tombola.api.info;

import java.util.Collection;
import java.util.List;

import javax.validation.constraints.Size;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

import local.intranet.tombola.api.domain.DefaultFieldLengths;
import local.intranet.tombola.api.model.entity.User;

/**
 * 
 * {@link UserInfo} for {@link local.intranet.tombola.api.service.UserService} and
 * {@link local.intranet.tombola.api.controller.IndexController#signin}
 * 
 * @author Radek Kádner
 *
 */
public class UserInfo implements UserDetails {

	private static final long serialVersionUID = -8869041140299694054L;

	private final String username;
	
	private final String password;
	
	private final boolean enabled;
	
	@Size(min = 0)
	private final List<GrantedAuthority> authorities;
	
	/**
	 * 
	 * Constructor with parameters
	 * 
	 * @param user         {@link User}
	 * @param authorities  {@link List}&lt;{@link GrantedAuthority}&gt;
	 */
    public UserInfo(User user, List<GrantedAuthority> authorities) {
		this.username = user.getUserName();
		this.password = user.getPassword();
		this.enabled = true;
		this.authorities = authorities;
	}

	/**
	 * 
	 * Build {@link UserInfo}
     * <p>
     * For {@link local.intranet.tombola.api.service.UserService#loadUserByUsername}
	 * 
	 * @param user        {@link User}
	 * @param authorities {@link List}&lt;{@link GrantedAuthority}&gt;
	 * @return {@link UserInfo}
	 */
	public static UserInfo build(User user, List<GrantedAuthority> authorities) {
		UserInfo ret = new UserInfo(user, authorities); 
		return ret;
	}
	
	/**
	 * 
	 * Returns the username used to authenticate the user. Cannot return <code>null</code>.
	 *
	 * @return the username (never <code>null</code>)
	 */
	@Override
	@Size(min = 1, max = DefaultFieldLengths.DEFAULT_NAME)
	public String getUsername() {
		return username;
	}

	/**
	 * 
	 * Returns the authorities granted to the user. Cannot return <code>null</code>.
	 *
	 * @return the authorities, sorted by natural key (never <code>null</code>)
	 */
	@Override
	@JsonIgnore
	@Size(min = 0)
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	/**
	 * 
	 * Get password
	 * 
	 * @return {@link String}
	 */
	@Override
	@JsonIgnore
	@Size(min = 1, max = DefaultFieldLengths.DEFAULT_NAME)
	public String getPassword() {
		return password;
	}

	/**
	 * 
	 * Indicates whether the user's account has expired. An expired account cannot be
	 * authenticated.
	 *
	 * @return <code>true</code> if the user's account is valid (ie non-expired),
	 * <code>false</code> if no longer valid (ie expired)
	 */
	@Override
	public boolean isAccountNonExpired() {
		return enabled;
	}

	/**
	 * 
	 * Indicates whether the user is locked or unlocked. A locked user cannot be
	 * authenticated.
	 *
	 * @return <code>true</code> if the user is not locked, <code>false</code> otherwise
	 */
	@Override
	public boolean isAccountNonLocked() {
		return enabled;
	}

	/**
	 * 
	 * Indicates whether the user's credentials (password) has expired. Expired
	 * credentials prevent authentication.
	 *
	 * @return <code>true</code> if the user's credentials are valid (ie non-expired),
	 * <code>false</code> if no longer valid (ie expired)
	 */
	@Override
	public boolean isCredentialsNonExpired() {
		return enabled;
	}

	/**
	 * 
	 * Indicates whether the user is enabled or disabled. A disabled user cannot be
	 * authenticated.
	 *
	 * @return <code>true</code> if the user is enabled, <code>false</code> otherwise
	 */
	@Override
	public boolean isEnabled() {
		return enabled;
	}

}
