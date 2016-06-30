package gov.nasa.arc.pds.lace.client.event;

import gov.nasa.arc.pds.lace.client.presenter.ContainerPresenter;
import gov.nasa.arc.pds.lace.client.presenter.PopupPresenter;
import gov.nasa.arc.pds.lace.shared.LabelElement;

/**
 * Defines an event that is fired when a label
 * element is to be deleted.
 */
public class ElementDeletionEvent extends GenericEvent<ElementDeletionEvent.EventDetails, ElementDeletionEvent.Handler> {

	/** The GWT type for this event. */
	public static final Type<ElementDeletionEvent.Handler> TYPE = new Type<ElementDeletionEvent.Handler>();

	/**
	 * Defines the interface that a handler for this event must implement.
	 */
	public interface Handler extends GenericEventHandler<ElementDeletionEvent.EventDetails> { }
	
	/**
	 * Implements an object that holds details about the label element to be deleted.
	 */
	public static class EventDetails {
	
		private LabelElement element;
		private ContainerPresenter parentPresenter;
		private PopupPresenter popup;
		
		/**
		 * Creates an instance of the <code>EventDetails</code> object.
		 *  
		 * @param element the label element to be deleted
		 * @param parentPresenter the parent presenter of a container that the element object is a child of
		 * @param popup the popup presenter
		 */
		public EventDetails(LabelElement element, ContainerPresenter parentPresenter, PopupPresenter popup) {			
			this.element = element;
			this.parentPresenter = parentPresenter;
			this.popup = popup;
		}

		/**
		 * Returns the label element to be deleted.
		 * 
		 * @return the label element to delete
		 */
		public LabelElement getLabelElement() {
			return element;
		}
		
		/**
		 * Returns the parent presenter of a container that the
		 * label element object is a child of.
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
	 * Creates a new instance with the given data.
	 *
	 * @param element the label element to be deleted
	 * @param parentPresenter the parent presenter of a container that the element object is a child of
	 * @param popup the popup presenter
	 */
	public ElementDeletionEvent(
			LabelElement element,
			ContainerPresenter parentPresenter,
			PopupPresenter popup
	) {
		super(new EventDetails(element, parentPresenter, popup), TYPE);
	}
}
