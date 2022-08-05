package local.intranet.tombola.api.model.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Immutable;

import local.intranet.tombola.api.domain.DefaultFieldLengths;

/**
 * 
 * {@link LoggingEvent} is an &#64;Immutable entity for JPA with
 * {@link local.intranet.tombola.api.model.repository.LoggingEventRepository}
 * <p>
 * The entity without setters is only used to read data written by
 * logback-spring DbAppender
 * <p>
 * https://www.baeldung.com/hibernate-immutable <br/>
 * https://stackoverflow.com/questions/67679636/spring-data-jpa-immutable-entity
 * 
 * @author Radek Kádner
 *
 */
@Entity
@Immutable
@Table(name = "logging_event")
public class LoggingEvent {

	/**
	 * 
	 * Constructor without parameter
	 * 
	 */
	public LoggingEvent() {
		this(null);
	}

	/**
	 * 
	 * Constructor with id
	 * 
	 * @param id Long
	 */
	public LoggingEvent(Long id) {
		// this.setId(id);
		this.id = id;
		this.timestmp = new Date().getTime();
	}

	@Id
	@Column(name = "event_id")
	private Long id;

	@Column(name = "formatted_message")
	private String formattedMessage;

	@Column(name = "level_string")
	@Size(max = DefaultFieldLengths.DEFAULT_STATUS)
	private String levelString;

	@Column(name = "caller_class")
	@Size(max = DefaultFieldLengths.DEFAULT_NAME)
	private String callerClass;

	@Column(name = "caller_method")
	@Size(max = DefaultFieldLengths.DEFAULT_NAME)
	private String callerMethod;

	@Size(max = DefaultFieldLengths.DEFAULT_NAME)
	private String arg0;

	@Size(max = DefaultFieldLengths.DEFAULT_NAME)
	private String arg1;

	@Size(max = DefaultFieldLengths.DEFAULT_NAME)
	private String arg2;

	@Size(max = DefaultFieldLengths.DEFAULT_NAME)
	private String arg3;

	private Long timestmp;

	/**
	 * 
	 * Get id
	 * 
	 * @return Long
	 */
	public Long getId() {
		return id;
	}

	/**
	 * 
	 * Get formattedMessage
	 * 
	 * @return the formattedMessage
	 */
	public String getFormattedMessage() {
		return formattedMessage;
	}

	/**
	 * 
	 * Get levelString
	 * 
	 * @return the levelString
	 */
	public String getLevelString() {
		return levelString;
	}

	/**
	 * 
	 * Get callerClass
	 * 
	 * @return the callerClass
	 */
	public String getCallerClass() {
		return callerClass;
	}

	/**
	 * 
	 * Get callerMethod
	 * 
	 * @return the callerMethod
	 */
	public String getCallerMethod() {
		return callerMethod;
	}

	/**
	 * 
	 * Get arg0
	 * 
	 * @return the arg0
	 */
	public String getArg0() {
		return arg0;
	}

	/**
	 * 
	 * Get arg1
	 * 
	 * @return the arg1
	 */
	public String getArg1() {
		return arg1;
	}

	/**
	 * 
	 * Get arg2
	 * 
	 * @return the arg2
	 */
	public String getArg2() {
		return arg2;
	}

	/**
	 * 
	 * Get arg3
	 * 
	 * @return the arg3
	 */
	public String getArg3() {
		return arg3;
	}

	/**
	 * 
	 * Get timestmp
	 * 
	 * @return the timestmp
	 */
	public Long getTimestmp() {
		return timestmp;
	}

	/**
	 * 
	 * Returns a string representation of the object.
	 */
	@Override
	public String toString() {
		return "LoggingEvent [id=" + id + ", formattedMessage=" + formattedMessage + ", levelString=" + levelString
				+ ", callerClass=" + callerClass + ", callerMethod=" + callerMethod + ", arg0=" + arg0 + ", arg1="
				+ arg1 + ", arg2=" + arg2 + ", arg3=" + arg3 + ", timestmp=" + timestmp + "]";
	}

}
