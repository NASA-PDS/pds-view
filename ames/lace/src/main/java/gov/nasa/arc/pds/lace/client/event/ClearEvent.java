package gov.nasa.arc.pds.lace.client.event;

/**
 * Defines an event and a handler for the case when the user cancels an action.
 */
public class ClearEvent extends GenericEvent<String, ClearEvent.Handler> {

	/** The GWT type of the event. */
	public static final Type<ClearEvent.Handler> TYPE = new Type<ClearEvent.Handler>();
	
	/** 
	 * Defines the interface that a handler for this event must implement.
	 */
	public interface Handler extends GenericEventHandler<String> { /*empty*/ }
	
	/**
	 * Creates an instance of <code>ClearEvent</code>.
	 */
	public ClearEvent() {
		this(null);
	}
	
	/**
	 * Creates an instance of <code>ClearEvent</code> with the given data.
	 * 
	 * @param data 
	 */
	public ClearEvent(String data) {
		super(data, TYPE);
	}

}
