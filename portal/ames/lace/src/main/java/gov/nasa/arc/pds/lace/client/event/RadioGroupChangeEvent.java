package gov.nasa.arc.pds.lace.client.event;

import com.google.gwt.event.logical.shared.ValueChangeEvent;

/**
 * Implements an event that is fired when a radio group value has changed.
 * The event provides a String value which is the name of the radio group
 * that has changed in value.
 */
public class RadioGroupChangeEvent extends ValueChangeEvent<String> {

	/**
	 * Creates a new event instance for a specific radio button group.
	 *
	 * @param groupName the name of the radio group
	 */
	public RadioGroupChangeEvent(String groupName) {
		super(groupName);
	}

}
