package gov.nasa.arc.pds.lace.client.event;

import gov.nasa.arc.pds.lace.client.presenter.InsertionPointPresenter;

/**
 * Implements an event that is fired when an element is selected from 'any' insertion point. 
 */
public class WildcardElementSelectedEvent extends GenericEvent<WildcardElementSelectedEvent.EventDetails, WildcardElementSelectedEvent.Handler> {

	/** The GWT type of the event. */
	public static final Type<WildcardElementSelectedEvent.Handler> TYPE = new Type<WildcardElementSelectedEvent.Handler>();
	
	/**
	 * Defines the interface that a handler for this event must implement.
	 */
	public interface Handler extends GenericEventHandler<WildcardElementSelectedEvent.EventDetails> { }
	
	/**
	 * Implements an object that holds details about the selected wildcard element. 
	 */
	public static class EventDetails {
		private String namespace; 
		private String elementName;
		private InsertionPointPresenter presenter;
		
		/**
		 * Creates a new instance of <code>EventDetails</code>.
		 * 
		 * @param presenter the presenter of an insertion point from which the element was selected from
		 * @param namespace the element namespace
		 * @param elementName the selected element name
		 */
		public EventDetails(InsertionPointPresenter presenter, String namespace, String elementName) {
			this.presenter = presenter;
			this.namespace = namespace;
			this.elementName = elementName;
		}

		/**
		 * Gets the selected namespace.
		 * 
		 * @return the namespace
		 */
		public String getNamespace() {
			return namespace;
		}
		
		/**
		 * Gets the selected element name.
		 * 
		 * @return the element name
		 */
		public String getElementName() {
			return elementName;
		}
		
		/**
		 * Gets the presenter of an insertion point from which the wildcard element was selected from.
		 * 
		 * @return the insertion point presenter
		 */
		public InsertionPointPresenter getPresenter() {
			return presenter;
		}
	}
	
	/**
	 * Creates a new event that indicates the user has selected an element.
	 * 
	 * @param presenter the presenter of an insertion point from which the element was selected from
	 * @param namespace the element namespace
	 * @param elementName the element name
	 */
	public WildcardElementSelectedEvent(InsertionPointPresenter presenter, String namespace, String elementName) {
		super(new EventDetails(presenter, namespace, elementName), TYPE);
	}
}
