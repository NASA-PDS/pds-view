package gov.nasa.arc.pds.lace.client.event;

/**
 * Defines an event that is fired when a new label with a
 * specified root element is to be created.
 */
public class CreateLabelEvent extends GenericEvent<CreateLabelEvent.Data, CreateLabelEvent.Handler> {

	/** The GWT type for this event. */
	public static final Type<CreateLabelEvent.Handler> TYPE = new Type<CreateLabelEvent.Handler>();

	/**
	 * Defines the interface that a handler for this event must implement.
	 */
	public interface Handler extends GenericEventHandler<CreateLabelEvent.Data> { }
		
	/**
	 * Creates a new instance with the given data.
	 * 
	 * @param rootElement the top level element
	 */
	public CreateLabelEvent(String rootElement, String namespaceURI) {
		super(new Data(rootElement, namespaceURI), TYPE);
	}
	
	/**
	 * The event data.
	 */
	public static class Data {
		
		private String elementName;
		private String elementNS;

		/**
		 * Creates a new instance of the event data.
		 * 
		 * @param elementName the element name
		 * @param elementNS the namespace of the element
		 */
		public Data(String elementName, String elementNS) {
			this.elementName = elementName;
			this.elementNS = elementNS;
		}
		
		/**
		 * Gets the element name.
		 * 
		 * @return the element name
		 */
		public String getElementName() {
			return elementName;
		}
		
		/**
		 * Gets the namespace of the element.
		 * 
		 * @return the element namespace URI
		 */
		public String getNamespaceURI() {
			return elementNS;
		}
		
	}
	
}
