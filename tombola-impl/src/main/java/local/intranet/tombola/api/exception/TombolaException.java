package local.intranet.tombola.api.exception;

import java.net.ConnectException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * {@link TombolaException} for
 * {@link local.intranet.tombola.TombolaApplication}
 * 
 * @author Radek Kádner
 *
 */
public class TombolaException extends ConnectException {

	private static final long serialVersionUID = 5639743839480904356L;

	private static final Logger LOG = LoggerFactory.getLogger(TombolaException.class);

	/**
	 * 
	 * Constructor with param
	 * 
	 * @param msg {@link String}
	 */
	public TombolaException(String msg) {
		super(msg);
	}

	/**
	 * 
	 * Constructor with params
	 * 
	 * @param msg {@link String}
	 * @param t   {@link Throwable}
	 */
	public TombolaException(String msg, Throwable t) {
		super(msg);
		LOG.error(msg, t);
	}

}
