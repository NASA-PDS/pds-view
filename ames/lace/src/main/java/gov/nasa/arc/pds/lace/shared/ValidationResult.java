package gov.nasa.arc.pds.lace.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implements a class that holds validation errors for a model.
 * The errors can be retrieved by the element ID.
 */
public class ValidationResult implements Serializable {

	private static final long serialVersionUID = 1L;

	private String variablePattern;
	private Map<String, List<ValidationMessage>> validationMessages = new HashMap<String, List<ValidationMessage>>();

	/**
	 * Adds a message for a model item with a given ID.
	 *
	 * @param id the item ID
	 * @param message the validation message
	 * @param attributeName the name of the attribute for the message, or null if the message is for an element
	 * @param value the value that the message is complaining about, or null if no value was specified in the message
	 */
	public void addMessage(String id, String message, String attributeName, String value) {
		List<ValidationMessage> messages = validationMessages.get(id);
		if (messages == null) {
			messages = new ArrayList<ValidationMessage>();
			validationMessages.put(id, messages);
		}
		messages.add(new ValidationMessage(message, attributeName, value));
	}

	/**
	 * Gets the validation messages for an item with a given ID.
	 *
	 * @param id the item ID
	 * @return a list of validation messages
	 */
	public List<ValidationMessage> getMessages(String id) {
		return validationMessages.get(id);
	}

	/**
	 * Gets the regular expression pattern that represents a variable value
	 * for an element or attribute. The regular expression should match the
	 *
	 * @return the variable pattern
	 */
	public String getVariablePattern() {
		return variablePattern;
	}

	/**
	 * Sets the regular expression pattern that represents a variable value
	 * in an element or attribute.
	 *
	 * @param pattern the new pattern, as a regular expression
	 */
	public void setVariablePattern(String pattern) {
		variablePattern = pattern;
	}

}
