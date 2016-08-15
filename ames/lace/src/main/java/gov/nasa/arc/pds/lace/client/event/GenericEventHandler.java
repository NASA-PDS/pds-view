package gov.nasa.arc.pds.lace.client.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * Defines an interface for event handlers that handle
 * single events.
 *
 * @param <T> the event data type
 */
public interface GenericEventHandler<T> extends EventHandler {

	/**
	 * Handles the event.
	 * 
	 * @param data the event details
	 */
	void onEvent(T data);
}
