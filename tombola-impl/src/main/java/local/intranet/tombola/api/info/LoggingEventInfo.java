package local.intranet.tombola.api.info;

import java.util.Date;

/**
 * 
 * {@link LoggingEventInfo} for {@link local.intranet.tombola.api.service.TombolaService}
 * 
 * @author Radek Kádner
 *
 */
public class LoggingEventInfo {

	private final Long id;
	private final String formattedMessage;
	private final String levelString;
	private final String callerClass;
	private final String callerMethod;
	private final String arg0;
	private final String arg1;
	private final String arg2;
	private final String arg3;
	private final Date date;
	
	/**
	 * 
	 * Constructor with parameters
	 * 
	 * @param id               {@link Long}
	 * @param formattedMessage {@link String}
	 * @param levelString      {@link String}
	 * @param callerClass      {@link String}
	 * @param callerMethod     {@link String}
	 * @param arg0             {@link String}
	 * @param arg1             {@link String}
	 * @param arg2             {@link String}
	 * @param arg3             {@link String}
	 * @param date             {@link Date}
	 */
	public LoggingEventInfo(Long id, String formattedMessage, String levelString, String callerClass, String callerMethod, String arg0, String arg1,
			String arg2, String arg3, Date date) {
		super();
		this.id = id;
		this.formattedMessage = formattedMessage;
		this.levelString = levelString;
		this.callerClass = callerClass;
		this.callerMethod = callerMethod;
		this.arg0 = arg0;
		this.arg1 = arg1;
		this.arg2 = arg2;
		this.arg3 = arg3;
		this.date = date;
	}

	/**
	 * 
	 * Get id
	 * 
	 * @return the id {@link Long}
	 */
	public Long getId() {
		return id;
	}

	/**
	 * 
	 * Get formattedMessage
	 * 
	 * @return the formattedMessage {@link String}
	 */
	public String getFormattedMessage() {
		return formattedMessage;
	}

	/**
	 * 
	 * Get levelString
	 * 
	 * @return the levelString {@link String}
	 */
	public String getLevelString() {
		return levelString;
	}

	/**
	 * 
	 * Get callerClass
	 * 
	 * @return the callerClass {@link String}
	 */
	public String getCallerClass() {
		return callerClass;
	}

	/**
	 * 
	 * Get callerMethod
	 * 
	 * @return the callerMethod {@link String}
	 */
	public String getCallerMethod() {
		return callerMethod;
	}

	/**
	 * 
	 * Get arg0
	 * 
	 * @return the arg0 {@link String}
	 */
	public String getArg0() {
		return arg0;
	}

	/**
	 * 
	 * Get arg1
	 * 
	 * @return the arg1 {@link String}
	 */
	public String getArg1() {
		return arg1;
	}

	/**
	 * 
	 * Get arg2
	 * 
	 * @return the arg2 {@link String}
	 */
	public String getArg2() {
		return arg2;
	}

	/**
	 * 
	 * Get arg3
	 * 
	 * @return the arg3 {@link String}
	 */
	public String getArg3() {
		return arg3;
	}

	/**
	 * 
	 * Get date
	 * 
	 * @return the date {@link Date}
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * 
	 * Returns a string representation of the object.
	 */
	@Override
	public String toString() {
		return "LoggingEventInfo [id=" + id + ", formattedMessage=" + formattedMessage + ", levelString=" + levelString
				+ ", callerClass=" + callerClass + ", callerMethod=" + callerMethod + ", arg0=" + arg0 + ", arg1="
				+ arg1 + ", arg2=" + arg2 + ", arg3=" + arg3 + ", date=" + date + "]";
	}

}
