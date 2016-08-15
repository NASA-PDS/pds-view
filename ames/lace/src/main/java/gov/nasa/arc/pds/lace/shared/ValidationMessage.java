package gov.nasa.arc.pds.lace.shared;

import java.io.Serializable;

/**
 * Holds the data for a single validation message.
 */
public class ValidationMessage implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Defines the severities of the messages.
	 */
	public static enum Severity {
	
		/** The message is a warning. */
		WARNING,
		
		/** The message is an error. */
		ERROR;
		
	}
	
	private String message;
	private String attributeName;
	private String value;
	
	/**
	 * Creates a new instance with a given message and
	 * attribute name.
	 * 
	 * @param message the validation message
	 * @param attributeName the attribute name the message applies to
	 */
	ValidationMessage(String message, String attributeName, String value) {
		this.message = message;
		this.attributeName = attributeName;
		this.value = value;
	}
	
	/**
	 * Required by GWT serialization.
	 */
	ValidationMessage() {
		// empty
	}
	
	/**
	 * Gets the validation message.
	 * 
	 * @return a string message
	 */
	public String getMessage() {
		return message;
	}
	
	/**
	 * Gets the attribute name for the message, or null
	 * if the message is for an element.
	 * 
	 * @return the attribute name, or null if the message is not for an attribute
	 */
	public String getAttributeName() {
		return attributeName;
	}
	
	/**
	 * Gets the value that the message is complaining about.
	 * This is used to apply the message to an element or
	 * an attribute, depending on which the value matches.
	 * @return
	 */
	public String getValue() {
		return value;
	}
	
}
