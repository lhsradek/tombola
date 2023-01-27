package local.intranet.tombola.api.model.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import local.intranet.tombola.api.controller.StatusController;
import local.intranet.tombola.api.domain.DefaultFieldLengths;
import local.intranet.tombola.api.security.AESUtil;

/**
 * 
 * {@link User} is entity for CRUD with
 * {@link local.intranet.tombola.api.model.repository.UserRepository}
 * 
 * @author Radek Kádner
 *
 */
@Entity
@Table(name = "tombola_user"
/*
 * , indexes = { // @Index(name = "tombola_user_pkey", columnList = "id")},
 * 
 * @Index(columnList = "id") }, uniqueConstraints = { // @UniqueConstraint(name
 * = "tombola_user_name_uk", columnNames = "user_name")}
 * 
 * @UniqueConstraint(columnNames = { "user_name" }) }
 */
)
@GenericGenerator(name = "UserGenerator", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
		@Parameter(name = "sequence_name", value = "USER_SEQUENCE"), @Parameter(name = "initial_value", value = "1"),
		@Parameter(name = "increment_size", value = "1") })
public class User {

	/**
	 * 
	 * Constructor without parameter
	 * 
	 */
	public User() {
		this(null);
	}

	/**
	 * 
	 * Constructor with id
	 * 
	 * @param id Long
	 */
	public User(Long id) {
		this.setId(id);
	}

	/**
	 * 
	 * Constructor for {@link local.intranet.tombola.api.config.ApplicationConfig}
	 * 
	 * @param userName {@link String}
	 * @param password {@link String}
	 * @param role     Set&lt;IndexRole&gt;
	 */
	public User(String userName, String password, Set<Role> role) {
		setUserName(userName);
		setPassword(password);
		setAccountNonExpired(true);
		setAccountNonLocked(true);
		setCredentialsNonExpired(true);
		setEnabled(true);
		setRole(role);
	}

	/**
	 * 
	 * Constructor for {@link local.intranet.tombola.api.config.ApplicationConfig}
	 * 
	 * @param userName {@link String}
	 * @param password {@link String}
	 * @param role     Set&lt;IndexRole&gt;
	 * @param enabled  boolean
	 */
	public User(String userName, String password, Set<Role> role, boolean enabled) {
		setUserName(userName);
		setPassword(password);
		setAccountNonExpired(true);
		setAccountNonLocked(true);
		setCredentialsNonExpired(true);
		setEnabled(enabled);
		setRole(role);
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Column(name = "user_name", nullable = false)
	@Size(max = DefaultFieldLengths.DEFAULT_NAME)
	private String userName;

	@NotNull
	@Column(name = "password", nullable = false)
	@Size(max = DefaultFieldLengths.DEFAULT_NAME)
	private String password;

	@Column(name = "account_non_expired")
	private Boolean accountNonExpired;

	@Column(name = "account_non_locked")
	private Boolean accountNonLocked;

	@Column(name = "credentials_non_expired")
	private Boolean credentialsNonExpired;

	private Boolean enabled;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "tombola_user_role"

	/*
	 * , /* indexes = { // @Index(name = "tombola_user_role_user_id", columnList =
	 * "user_id"),
	 * 
	 * @Index(columnList = "user_id"), // @Index(name = "tombola_user_role_role_id",
	 * columnList = "role_id")
	 * 
	 * @Index(columnList = "role_id") }, uniqueConstraints = {
	 * // @UniqueConstraint(name = "tombola_user_role_uk", columnNames = {"user_id",
	 * "role_id"})},
	 * 
	 * @UniqueConstraint(columnNames = {"user_id", "role_id"}) }, joinColumns
	 * = @JoinColumn(name = "user_id", referencedColumnName="id"),
	 * inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName="id")
	 */
	)
	private Set<Role> role = new HashSet<>();

	/**
	 * 
	 * get id
	 * 
	 * @return Long
	 */
	public Long getId() {
		return id;
	}

	/**
	 * 
	 * Set id
	 * 
	 * @param id Long
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * 
	 * Get userName
	 * 
	 * @return {@link String}
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * 
	 * Set userName
	 * 
	 * @param userName {@link String}
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * 
	 * Get password
	 * 
	 * @return {@link String}
	 */
	public String getPassword() {
		return AESUtil.getHex(password);
	}

	/**
	 * 
	 * Set password
	 * 
	 * @param password {@link String}
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * 
	 * Get accountNonExpired
	 * 
	 * @return {@link Boolean}
	 */
	public Boolean isAccountNonExpired() {
		return accountNonExpired;
	}

	/**
	 * 
	 * Set accountNonExpired
	 * 
	 * @param accountNonExpired {@link Boolean}
	 */
	public void setAccountNonExpired(Boolean accountNonExpired) {
		this.accountNonExpired = accountNonExpired;
	}

	/**
	 * 
	 * Is accountNonLocked?
	 * 
	 * @return {@link Boolean}
	 */
	public Boolean isAccountNonLocked() {
		return accountNonLocked;
	}

	/**
	 * 
	 * Set accountNonLocked
	 * 
	 * @param accountNonLocked {@link Boolean}
	 */
	public void setAccountNonLocked(Boolean accountNonLocked) {
		this.accountNonLocked = accountNonLocked;
	}

	/**
	 * 
	 * Is credentialsNonExpired?
	 * 
	 * @return {@link Boolean}
	 */
	public Boolean isCredentialsNonExpired() {
		return credentialsNonExpired;
	}

	/**
	 * 
	 * Set credentialsNonExpired
	 * 
	 * @param credentialsNonExpired {@link Boolean}
	 */
	public void setCredentialsNonExpired(Boolean credentialsNonExpired) {
		this.credentialsNonExpired = credentialsNonExpired;
	}

	/**
	 * 
	 * Is enabled?
	 * 
	 * @return {@link Boolean}
	 */
	public Boolean isEnabled() {
		return enabled;
	}

	/**
	 * 
	 * Set enabled
	 * 
	 * @param enabled {@link Boolean}
	 */
	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * 
	 * Get role
	 * 
	 * @return Set&lt;{@link Role}&gt;
	 */
	public Set<Role> getRole() {
		return role;
	}

	/**
	 * 
	 * Set role
	 * 
	 * @param role {@link Set}&lt;{@link Role}&gt;
	 */
	public void setRole(Set<Role> role) {
		this.role = role;
	}

	/**
	 * 
	 * Returns a string representation of the object.
	 */
	@Override
	public String toString() {
		return "User [id=" + id + ", userName=" + userName + ", password=" + StatusController.STATUS_PROTECTED
				+ ", accountNonExpired=" + accountNonExpired + ", accountNonLocked=" + accountNonLocked
				+ ", credentialsNonExpired=" + credentialsNonExpired + ", enabled=" + enabled + ", role=" + role + "]";
	}

}
