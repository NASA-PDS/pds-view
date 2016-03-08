package gov.nasa.arc.pds.lace.client.event;


public class ElementInsertedEvent extends GenericEvent<ElementInsertedEvent.EventDetails, ElementInsertedEvent.Handler> {	

	/** The GWT type for this event. */
	public static final Type<ElementInsertedEvent.Handler> TYPE = new Type<ElementInsertedEvent.Handler>();

	/**
	 * Defines the interface that a handler for this event must implement.
	 */
	public interface Handler extends GenericEventHandler<ElementInsertedEvent.EventDetails> { }
	
	/**
	 * Implements an object that holds details about
	 * the inserted element.  
	 */
	public static class EventDetails {

		private boolean simpleItem;
		
		public EventDetails() {
			
		}
		
		public EventDetails(boolean simpleItem) {			
			this.simpleItem = simpleItem;
		}
		
		public boolean isSimpleItem() {
			return simpleItem;
		}
	}
	
	public ElementInsertedEvent(boolean isSimple) {
		super(new EventDetails(isSimple), TYPE);
	}
}
