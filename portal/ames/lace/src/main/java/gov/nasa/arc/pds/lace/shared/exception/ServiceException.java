package gov.nasa.arc.pds.lace.shared.exception;

/**
 * Implements an exception that can be safely thrown by RPC services.
 */
public class ServiceException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new instance with a given message.
	 * 
	 * @param message the exception message
	 */
	public ServiceException(String message) {
		super(message);
	}
	
	/**
	 * Creates a new instance with empty message, used by
	 * serialization layer. (Default scope to keep application
	 * code from using this constructor.) 
	 */
	ServiceException() {
		super();
	}
	
}
