package gov.nasa.arc.pds.lace.client.event;

import gov.nasa.arc.pds.lace.client.presenter.PopupPresenter;
import gov.nasa.arc.pds.lace.shared.Container;
import gov.nasa.arc.pds.lace.shared.InsertionPoint;

/**
 * Implements an event that is fired when an element is selected
 * from an insertion point in the edit zone. 
 */
public class ElementSelectionEvent extends GenericEvent<ElementSelectionEvent.EventDetails, ElementSelectionEventHandler> {

	/** The GWT type of the event. */
	public static final Type<ElementSelectionEventHandler> TYPE = new Type<ElementSelectionEventHandler>();
	
	/**
	 * Implements an object that holds details about
	 * the selected element. 
	 */
	public static class EventDetails {
				
		private int index;
		private InsertionPoint insPoint;		
		private PopupPresenter popup;
		private Container parentContainer;

		/**
		 * Creates a new instance of <code>EventDetails</code>
		 */
		public EventDetails(int index, InsertionPoint insPoint, Container container, PopupPresenter popup) {			
			this.index = index;
			this.insPoint = insPoint;			
			this.popup = popup;
			this.parentContainer = container;
		}	
		
		/**
		 * Gets the position of the selected element in the list of alternatives. 
		 * 
		 * @return the index of the selected element 
		 */
		public int getIndex() {
			return index;
		}
		
		/**
		 * Gets the insertion point object that the element
		 * was selected from.  
		 * 
		 * @return the insertion point object
		 */
		public InsertionPoint getInsertionPoint() {
			return insPoint;
		}
		
		/**
		 * Returns the container in which the insertion point object is a child of.
		 *  
		 * @return a container object
		 */
		public Container getParentContainer() {
			return parentContainer;
		}
		
		/**
		 * Gets a reference to the popup presenter.
		 * 
		 * @return the reference to the popup presenter
		 */
		public PopupPresenter getPopup() {
			return popup;
		}
	}
	
	/**
	 * Creates a new event that indicates the user has selected
	 * an item from the list of alternatives at position index.
	 * 
	 * @param index index of the selected item
	 * @param insPoint the insertion point object
	 * @param container the parent container object that holds the insertion point.
	 * @param popup
	 */
	public ElementSelectionEvent(int index, InsertionPoint insPoint, Container container, PopupPresenter popup) {
		super(new EventDetails(index, insPoint, container, popup), TYPE);
	}
}

