package gov.nasa.arc.pds.lace.client.event;

/**
 * Defines an event that is fired when the user requests a new
 * label be created with a given namespace and top-level element.
 */
public class RequestNewLabelEvent extends GenericEvent<RequestNewLabelEvent.Data, RequestNewLabelEvent.Handler> {

	/** The GWT type for this event. */
	public static final Type<RequestNewLabelEvent.Handler> TYPE = new Type<RequestNewLabelEvent.Handler>();

	/**
	 * Defines the interface that a handler for this event must implement.
	 */
	public interface Handler extends GenericEventHandler<RequestNewLabelEvent.Data> { }
		
	/**
	 * Creates a new instance with the given data.
	 * 
	 * @param rootElement the top level element
	 */
	public RequestNewLabelEvent(String rootElement, String namespaceURI) {
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
