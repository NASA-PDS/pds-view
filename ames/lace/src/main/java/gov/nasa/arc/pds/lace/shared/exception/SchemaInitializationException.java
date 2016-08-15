package gov.nasa.arc.pds.lace.shared.exception;


/**
 * Implements an exception indicating an error initializing the
 * DOM services for XML schema.
 */
public class SchemaInitializationException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * Creates an empty instance. Required for GWT serialization.
	 */
	public SchemaInitializationException() {
		super();
	}

	/**
	 * Creates a new instance with a given message and underlying
	 * cause. We ignore the cause since it may not be serializable,
	 * and this exception must work on both the server and client
	 * side.
	 *
	 * @param msg the exception message
	 * @param cause the underlying cause
	 */
	public SchemaInitializationException(String msg, Throwable cause) {
		super(msg + ": " + cause.getMessage());
	}

}
