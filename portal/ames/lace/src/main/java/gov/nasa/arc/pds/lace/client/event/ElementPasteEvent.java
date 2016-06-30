package gov.nasa.arc.pds.lace.client.event;

import gov.nasa.arc.pds.lace.client.presenter.ContainerPresenter;
import gov.nasa.arc.pds.lace.shared.InsertionPoint;
import gov.nasa.arc.pds.lace.shared.LabelElement;

/**
 * Implements an event that is fired when an element is selected
 * from an insertion point in the edit zone.
 */
public class ElementPasteEvent extends GenericEvent<ElementPasteEvent.EventDetails, ElementPasteEvent.Handler> {

	/** The GWT type of the event. */
	public static final Type<ElementPasteEvent.Handler> TYPE = new Type<ElementPasteEvent.Handler>();

	/**
	 * Defines the interface the event handler must implement.
	 */
	public interface Handler extends GenericEventHandler<ElementPasteEvent.EventDetails> { }

	/**
	 * Implements an object that holds details about
	 * the selected element.
	 */
	public static class EventDetails {

		private LabelElement element;
		private InsertionPoint insPoint;
		private ContainerPresenter parentPresenter;

		/**
		 * Creates a new instance of <code>EventDetails</code>
		 *
		 * @param element the element to paste
		 * @param insPoint the insertion point object
		 * @param parentPresenter the presenter of the container that
		 * the insertion point object is a child of
		 */
		public EventDetails(
				LabelElement element,
				InsertionPoint insPoint,
				ContainerPresenter parentPresenter
		) {
			this.element = element;
			this.insPoint = insPoint;
			this.parentPresenter = parentPresenter;
		}

		/**
		 * Gets the label element to paste.
		 *
		 * @return the label element
		 */
		public LabelElement getElement() {
			return element;
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
		 * Returns the presenter of the container that the
		 * insertion point object is a child of.
		 *
		 * @return the parent container presenter
		 */
		public ContainerPresenter getParentPresenter() {
			return parentPresenter;
		}

	}

	/**
	 * Creates a new event that indicates the user has selected
	 * an item from the list of alternatives at position index.
	 *
	 * @param element the element to paste
	 * @param insPoint the insertion point object
	 * @param parentPresenter the parent presenter of the container that
	 * the insertion point object is a child of
	 */
	public ElementPasteEvent(
			LabelElement element,
			InsertionPoint insPoint,
			ContainerPresenter parentPresenter
	) {
		super(new EventDetails(element, insPoint, parentPresenter), TYPE);
	}
}

