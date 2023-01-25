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

import local.intranet.tombola.api.domain.DefaultFieldLengths;

/**
 * 
 * {@link Role} is entity for CRUD with
 * {@link local.intranet.tombola.api.model.repository.RoleRepository}
 * 
 * @author Radek Kádner
 *
 */
@Entity
@Table(name = "tombola_role"
/*
 * , indexes = { // @Index(name = "tombola_role_pkey", columnList = "id")},
 * 
 * @Index(columnList = "id") }, uniqueConstraints = { // @UniqueConstraint(name
 * = "tombola_role_name_uk", columnNames = "role_name")
 * 
 * @UniqueConstraint(columnNames = { "role_name" }) }
 */
)
@GenericGenerator(name = "RoleGenerator", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
		@Parameter(name = "sequence_name", value = "ROLE_SEQUENCE"), @Parameter(name = "initial_value", value = "1"),
		@Parameter(name = "increment_size", value = "1") })
public class Role {

	/**
	 * 
	 * Constructor without parameter
	 * 
	 */
	public Role() {
		this((Long) null);
	}

	/**
	 * 
	 * Constructor with id
	 * 
	 * @param id {@link Long}
	 */
	public Role(Long id) {
		this.setId(id);
	}

	/**
	 * 
	 * Constructor for {@link local.intranet.tombola.api.config.ApplicationConfig}
	 * 
	 * @param roleName {@link String}
	 */
	public Role(String roleName) {
		setRoleName(roleName);
		setEnabled(true);
	}

	/**
	 * Constructor for {@link local.intranet.tombola.api.config.ApplicationConfig}
	 * 
	 * @param roleName {@link String}
	 * @param enabled  boolean
	 */
	public Role(String roleName, boolean enabled) {
		setRoleName(roleName);
		setEnabled(enabled);
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Column(name = "role_name", nullable = false)
	@Size(max = DefaultFieldLengths.DEFAULT_NAME)
	private String roleName;

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
	private Set<User> user = new HashSet<>();

	/**
	 * 
	 * Get id
	 * 
	 * @return {@link Long}
	 */
	public Long getId() {
		return id;
	}

	/**
	 * 
	 * Set id
	 * 
	 * @param id {@link Long}
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * 
	 * Get roleName
	 * 
	 * @return {@link String}
	 */
	public String getRoleName() {
		return roleName;
	}

	/**
	 * 
	 * Set roleName
	 * 
	 * @param roleName {@link String}
	 */
	public void setRoleName(String roleName) {
		this.roleName = roleName;
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
	 * Get user
	 * 
	 * @return {@link Set}&lt;{@link User}&gt;
	 */
	public Set<User> getUser() {
		return user;
	}

	/**
	 * 
	 * Set user
	 * 
	 * @param user {@link Set}&lt;{@link User}&gt;
	 */
	public void setUser(Set<User> user) {
		this.user = user;
	}

	/**
	 * 
	 * Returns a string representation of the object.
	 */
	@Override
	public String toString() {
		return "Role [id=" + id + ", roleName=" + roleName + ", enabled=" + enabled + ", user=" + user + "]";
	}

}
