package gov.nasa.arc.pds.lace.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Defines a generic event that is parameterized with a handler and an
 * event details class. The <code>onEvent</code> method of the handler is triggered when
 * the event is dispatched.
 *
 * @param <T>
 * @param <H>
 */
public class GenericEvent<T, H extends GenericEventHandler<T>> extends GwtEvent<H> {

	private Type<H> type;
	private T data;
	
	/**
	 * Creates a new event with the given event details and type.
	 * 
	 * @param data the event data (details)
	 * @param type the event type
	 */
	public GenericEvent(T data, Type<H> type) {
		this.data = data;
		this.type = type;
	}
	
	@Override
	public Type<H> getAssociatedType() {
		return type;
	}

	@Override
	protected void dispatch(H handler) {		
		handler.onEvent(data);
	}

	/**
	 * Gets the data associated with the event. 
	 * Used for unit testing.
	 * 
	 * @return the event data
	 */
	public T getData() {
		return data;
	}
}
