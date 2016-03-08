package gov.nasa.arc.pds.lace.client.event;

import gov.nasa.arc.pds.lace.client.presenter.ContainerPresenter;
import gov.nasa.arc.pds.lace.client.presenter.PopupPresenter;
import gov.nasa.arc.pds.lace.shared.InsertionPoint;
import gov.nasa.arc.pds.lace.shared.LabelItemType;

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
				
		private LabelItemType type;
		private int alternativeIndex;
		private InsertionPoint insPoint;		
		private PopupPresenter popup;
		private ContainerPresenter parentPresenter;

		/**
		 * Creates a new instance of <code>EventDetails</code>
		 */
		public EventDetails(
				int alternativeIndex,
				LabelItemType type,
				InsertionPoint insPoint,
				ContainerPresenter parentPresenter,
				PopupPresenter popup
		) {			
			this.alternativeIndex = alternativeIndex;
			this.type = type;
			this.insPoint = insPoint;			
			this.popup = popup;
			this.parentPresenter = parentPresenter;
		}	
		
		/**
		 * Gets the label item type that matches
		 * the selected element in the related insert option. 
		 * 
		 * @return the selected label item type
		 */
		public LabelItemType getType() {
			return type;
		}
		
		/**
		 * Gets the position of the insert option within the
		 * list of alternatives that holds the type info for the 
		 * selected element. 
		 * 
		 * @return the index of an insert option within the list of alternatives
		 */
		public int getAlternativeIndex() {
			return alternativeIndex;
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
		 * Returns the container presenter that the
		 * insertion point object is a child of.
		 *  
		 * @return the parent container presenter
		 */
		public ContainerPresenter getParentPresenter() {
			return parentPresenter;
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
	 * @param alternativeIndex the index of the selected insert option
	 * @param type the selected item type 
	 * @param insPoint the insertion point object
	 * @param parentPresenter the parent container presenter that
	 * the insertion point object is a child of
	 * @param popup
	 */
	public ElementSelectionEvent(
			int alternativeIndex,
			LabelItemType type,
			InsertionPoint insPoint,
			ContainerPresenter parentPresenter,
			PopupPresenter popup
	) {
		super(new EventDetails(alternativeIndex, type, insPoint, parentPresenter, popup), TYPE);
	}
}

