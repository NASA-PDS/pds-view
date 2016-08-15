package gov.nasa.arc.pds.lace.client.event;

/**
 * Defines an event that is send when the editor is about to load new content.
 * Existing components being shown in the editor pane can use this event to
 * save their values prior to being removed from the screen.
 */
public class EditorContentChangingEvent extends GenericEvent<Void, EditorContentChangingEvent.Handler> {

	/** The GWT type of the event. */
	public static final Type<EditorContentChangingEvent.Handler> TYPE = new Type<EditorContentChangingEvent.Handler>();
	
	/**
	 * Defines an interface that event handlers must implement.
	 */
	public interface Handler extends GenericEventHandler<Void> {
		
	}

	/**
	 * Creates a new event.
	 */
	public EditorContentChangingEvent() {
		super(null, TYPE);
	}

}
