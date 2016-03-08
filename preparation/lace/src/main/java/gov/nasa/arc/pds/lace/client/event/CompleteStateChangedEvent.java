package gov.nasa.arc.pds.lace.client.event;

import gov.nasa.arc.pds.lace.shared.Container;


public class CompleteStateChangedEvent extends GenericEvent<CompleteStateChangedEvent.EventDetails, CompleteStateChangedEvent.Handler> {

	/** The GWT type for this event. */
	public static final Type<CompleteStateChangedEvent.Handler> TYPE = new Type<CompleteStateChangedEvent.Handler>();

	/**
	 * Defines the interface that a handler for this event must implement.
	 */
	public interface Handler extends GenericEventHandler<CompleteStateChangedEvent.EventDetails> { }
	
	/**
	 * Implements an object that holds details about
	 * the changed state.  
	 */
	public static class EventDetails {

		private Container container;
		
		public EventDetails(Container container) {
			this.container = container;
		}
		
		public Container getContainer() {
			return container;
		}
	}
	
	public CompleteStateChangedEvent(Container container) {
		super(new EventDetails(container), TYPE);
	}
}
