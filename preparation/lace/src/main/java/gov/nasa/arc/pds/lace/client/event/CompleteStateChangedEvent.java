package gov.nasa.arc.pds.lace.client.event;

import gov.nasa.arc.pds.lace.shared.Container;

/**
 * 
 *
 */
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
		private boolean simpleItem;
			
		public EventDetails(Container container, boolean simpleItem) {
			this.container = container;
			this.simpleItem = simpleItem;
		}
		
		public Container getContainer() {
			return container;
		}
		
		public boolean isSimpleItem() {
			return simpleItem;
		}
	}
	
	/**
	 * 
	 * @param container the changed container or the container that the
	 * changed simple item is a child of.
	 * @param simpleItem true, if the changed item is a simple item
	 */
	public CompleteStateChangedEvent(Container container, boolean simpleItem) {
		super(new EventDetails(container, simpleItem), TYPE);
	}
}
