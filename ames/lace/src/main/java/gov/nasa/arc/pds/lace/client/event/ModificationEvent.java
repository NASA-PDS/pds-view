package gov.nasa.arc.pds.lace.client.event;

/**
 * Represents a user request to cut, copy, or delete an element.
 */
public class ModificationEvent extends GenericEvent<ModificationEvent.ModificationType, ModificationEvent.Handler> {

	/** The GWT type for this event. */
	public static final Type<ModificationEvent.Handler> TYPE = new Type<ModificationEvent.Handler>();

	/**
	 * Defines the type of modification events that can occur.
	 */
	public enum ModificationType {

		/** The user requests to cut an item. */
		CUT,

		/** The user requests to copy an item. */
		COPY,

		/** The user requests to delete an item. */
		DELETE

	}

	/**
	 * Defines the interface event handlers must implement.
	 */
	public interface Handler extends GenericEventHandler<ModificationEvent.ModificationType> { }

	/**
	 * Creates a new modification event.
	 *
	 * @param eventType the event type
	 */
	public ModificationEvent(ModificationType eventType) {
		super(eventType, TYPE);
	}

}
