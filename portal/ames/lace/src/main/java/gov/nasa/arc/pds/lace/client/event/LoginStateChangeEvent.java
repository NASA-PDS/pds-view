package gov.nasa.arc.pds.lace.client.event;

/**
 * Defines an event that is triggered when the application is starting
 * up. Different states are defined in the {@link EventType} enumeration.
 */
public class LoginStateChangeEvent extends GenericEvent<Void, LoginStateChangeEvent.Handler> {

	/** The type of the event. */
	public static final Type<LoginStateChangeEvent.Handler> TYPE = new Type<LoginStateChangeEvent.Handler>();
	
	/**
	 * Defines the interface the event handlers must implement.
	 */
	public interface Handler extends GenericEventHandler<Void> { }
	
	/**
	 * Creates a new instance of the event.
	 */
	public LoginStateChangeEvent() {
		super(null, TYPE);
	}
	
}
