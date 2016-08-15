package gov.nasa.arc.pds.lace.server.validation;

import org.w3c.dom.Node;

/**
 * Defines an interface for handling validation failures.
 */
public interface FailureHandler {

	/**
	 * Handles a validation failure on a specified DOM node.
	 * 
	 * @param node the DOM node associated with the error
	 * @param message the error message
	 */
	void handleFailure(Node node, String message);
	
}
