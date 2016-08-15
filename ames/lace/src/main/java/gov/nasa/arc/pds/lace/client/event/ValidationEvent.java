package gov.nasa.arc.pds.lace.client.event;

/**
 * Defines an event that is triggered when a validation of the model
 * occurs, resulting in updated validation error messages.
 */
public class ValidationEvent extends GenericEvent<Void, ValidationEvent.Handler> {

	/** The type of the event. */
	public static final Type<ValidationEvent.Handler> TYPE = new Type<ValidationEvent.Handler>();
	
	/**
	 * Defines the interface the event handlers must implement.
	 */
	public interface Handler extends GenericEventHandler<Void> { }
	
	/**
	 * Creates a new instance of the event.
	 */
	public ValidationEvent() {
		super(null, TYPE);
	}

}
