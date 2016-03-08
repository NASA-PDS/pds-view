package gov.nasa.arc.pds.lace.client.event;

/**
 * Defines an event and a handler for the case when the template name
 * has been changed.
 */
public class TemplateNameChangedEvent extends GenericEvent<String, TemplateNameChangedEvent.Handler> {

	/** The GWT type for this event. */
	public static final Type<TemplateNameChangedEvent.Handler> TYPE = new Type<TemplateNameChangedEvent.Handler>();

	/**
	 * Defines the interface that a handler for this event must implement.
	 */
	public interface Handler extends GenericEventHandler<String> { }

	/**
	 * Creates a new instance with the given data and type.
	 *
	 * @param data the new name of the template
	 */
	public TemplateNameChangedEvent(String data) {
		super(data, TYPE);
	}

}
