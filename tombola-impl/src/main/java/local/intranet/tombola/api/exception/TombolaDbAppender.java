package local.intranet.tombola.api.exception;

import java.text.MessageFormat;

import org.apache.commons.lang3.reflect.FieldUtils;

import ch.qos.logback.classic.db.DBAppender;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;

/**
 * 
 * {@link TombolaDbAppender}. Implementation of log DBAppender. Only purpose is fix bugs in DBAppender
 * (max message length, forbidden characters in Postgresql).
 *
 * @author Vít Švanda
 * 
 * @since 11.0.0
 */
public class TombolaDbAppender extends DBAppender {

    /**
     * 
     * This is where an appender accomplishes its work. Note that the argument 
     * is of type Object.
     * 
     * @param eventObject {@link ILoggingEvent}
     */
	@Override
	public void doAppend(ILoggingEvent eventObject) {
		fixFormattedMessage(eventObject);
		IThrowableProxy throwableProxy = eventObject.getThrowableProxy();
		while (throwableProxy != null) {
			fixMessage(throwableProxy);
			throwableProxy = throwableProxy.getCause();
		}
		super.doAppend(eventObject);
	}

	/**
	 * 
	 * Fix message in DBAppender (max message length, forbidden characters in Postgresql).
	 * 
	 * @param throwableProxy {@link IThrowableProxy}
	 */
	private void fixMessage(IThrowableProxy throwableProxy) {
		if (throwableProxy != null) {
			try {
				String message = throwableProxy.getMessage();
				String fixedMessage = fixMessage(message);
				if (message != null && !message.equals(fixedMessage)) {
					FieldUtils.writeField(throwableProxy, "message", fixedMessage, true);
				}
			} catch (IllegalAccessException e) {
				// System out is OK here.
				System.out.println(MessageFormat.format("IndexDbAppender error during fixing message: {0}", e.getMessage()));
			}
		}
	}

	/**
	 * 
	 * Fix formatted message in DBAppender (max message length, forbidden characters in Postgresql).
	 * 
     * @param eventObject {@link ILoggingEvent}
	 */
	private void fixFormattedMessage(ILoggingEvent eventObject) {
		if (eventObject != null) {
			try {
				String formattedMessage = eventObject.getFormattedMessage();
				String fixedMessage = fixMessage(formattedMessage);
				if (formattedMessage != null && !formattedMessage.equals(fixedMessage)) {
					FieldUtils.writeField(eventObject, "formattedMessage", fixedMessage, true);
				}
				String message = eventObject.getMessage();
				fixedMessage = fixMessage(message);
				if (message != null && !message.equals(fixedMessage)) {
					FieldUtils.writeField(eventObject, "message", fixedMessage, true);
				}
			} catch (IllegalAccessException e) {
				// System out is OK here.
				System.out.println(MessageFormat.format("IndexDbAppender error during fixing message: {0}", e.getMessage()));
			}
		}
	}

	private String fixMessage(String message) {
		int maxLength = 240; // Only 200 because prefix is added lately. 240 By radek.kadner
		String fixedMessage = message;
		if (message != null
				&& (message.contains("\u0000")
				|| message.contains("\\x00")
				|| message.length() >= maxLength)) {
			// Workaround -> We have replace null characters by empty space, for case when exception will persisted in a Postgresql DB.
			fixedMessage = message.replace("\u0000", "").replace("\\x00", "");
			// Workaround for https://jira.qos.ch/browse/LOGBACK-493. -> DB tables has limitation for max 254 characters.
			if (fixedMessage.length() >= maxLength) {
				fixedMessage = fixedMessage.substring(0, maxLength - 1);
			}
		}
		return fixedMessage;
	}
}
