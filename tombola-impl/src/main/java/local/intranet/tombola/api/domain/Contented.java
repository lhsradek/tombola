package local.intranet.tombola.api.domain;

/**
 * 
 * {@link Contented} for {@link local.intranet.tombola.api.controller.StatusController}
 * 
 * @author Radek Kádner
 *
 */
public interface Contented {

	/**
	 * 
	 * CONTENT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"
	 */
	static final String CONTENT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

	/**
	 * 
	 * CONTENT_DATE_REST_FORMAT = "yyyy-MM-dd'T'HH:mm:ss"
	 */
	static final String CONTENT_DATE_REST_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
	
	/**
	 * 
	 * CONTENT_TIME_REST_FORMAT = "'T'HH:mm:ssXXX"
	 */
	static final String CONTENT_TIME_REST_FORMAT = "'T'HH:mm:ssXXX";

}
