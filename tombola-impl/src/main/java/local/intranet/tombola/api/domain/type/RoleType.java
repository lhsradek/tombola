package local.intranet.tombola.api.domain.type;

/**
 * 
 * {@link RoleType} for {@link local.intranet.tombola.api.config.ApplicationConfig}
 * <p>
 * ANONYMOUS_ROLE, USER_ROLE, MANAGER_ROLE, ADMIN_ROLE
 * 
 * @author Radek Kádner
 */
public enum RoleType {
	
	/**
	 *
	 * ANONYMOUS_ROLE = "anonymousRole"
	 */
	ANONYMOUS_ROLE("anonymousRole"),
	
	/**
	 *
	 * USER_ROLE = "userRole"
	 */
	USER_ROLE("userRole"),
	
	/**
	 * 
	 * MANAGER_ROLE = "managerRole"
	 */
	MANAGER_ROLE("managerRole"),
	
	/**
	 * 
	 * ADMIN_ROLE = "adminRole"
	 */
	ADMIN_ROLE("adminRole");
	
	private final String role;
	
	private RoleType(String role) {
		this.role = role;
	}

	/**
	 * 
	 * Get role
	 * @return userRole, managerRole or adminRole as String
	 */
	public String getRole() {
		return role;
	}

}
