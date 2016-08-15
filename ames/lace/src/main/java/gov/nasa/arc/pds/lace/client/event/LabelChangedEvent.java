package gov.nasa.arc.pds.lace.client.event;

import gov.nasa.arc.pds.lace.shared.Container;
import gov.nasa.arc.pds.lace.shared.LabelElement;

/**
 * Defines an event that is fired when a label is changed.
 * For example, when the user requests a new label, imports
 * a label from an XML file, inserts or deletes an element,
 * or changes the value of an item.
 */
public class LabelChangedEvent extends GenericEvent<LabelChangedEvent.Data, LabelChangedEvent.Handler> {
	
	/** The GWT type for this event. */
	public static final Type<LabelChangedEvent.Handler> TYPE = new Type<LabelChangedEvent.Handler>();

	/**
	 * Defines the interface that a handler for this event must implement.
	 */
	public interface Handler extends GenericEventHandler<LabelChangedEvent.Data> { }
	
	/**
	 * Creates a new instance of the event.
	 */
	public LabelChangedEvent() {
		super(new Data(), TYPE);
	}
	
	/**
	 * Creates a new instance with the given data.
	 *
	 * @param container the new root container
	 */
	public LabelChangedEvent(Container rootContainer) {
		super(new Data(rootContainer), TYPE);
	}
	
	/**
	 * Creates a new instance with the given data.
	 *	
	 * @param changedContainer the changed container 
	 * @param element the label element that is inserted or deleted
	 * @param insert flag to indicate whether the changed event is related to insertion or deletion
	 */
	public LabelChangedEvent(Container changedContainer, LabelElement element, boolean insert) {
		super(new Data(changedContainer, element, insert), TYPE);
	}
	
	/**
	 * The event data.
	 */
	public static class Data {
		
		private Container rootContainer;
		private Container changedContainer;
		private LabelElement element;		
		private boolean insert;
		
		/**
		 * Creates a new instance of the event data.
		 */
		public Data() {
			// Do nothing
		}
		
		/**
		 * Creates a new instance of the event data.
		 * 
		 * @param container the new root container
		 */
		public Data(Container rootContainer) {
			this.rootContainer = rootContainer;
		}
		
		/**
		 * Creates a new instance of the event data.
		 * 
		 * @param changedContainer the changed container 
		 * @param element the label element that is inserted or deleted
		 * @param insert flag to indicate whether the changed event is related to insertion or deletion
		 */
		public Data(Container changedContainer, LabelElement element, boolean insert) {
			this.changedContainer = changedContainer;
			this.element = element;
			this.insert = insert;
		}
		
		/**
		 * Gets the changed container.
		 * 
		 * @return a container
		 */
		public Container getChangedContainer() {
			return changedContainer;
		}
		
		/**
		 * Gets the root container.
		 * 
		 * @return the root container
		 */
		public Container getRootContainer() {
			return rootContainer;
		}
		
		/**
		 * Gets the label element that is inserted or deleted
		 * to or from the changed container.
		 * 
		 * @return a label element
		 */
		public LabelElement getElement() {
			return element;
		}
		
		/**
		 * Tests whether the changed event was the result of
		 * an insertion or deletion. 
		 * 
		 * @return true for insertion, false for deletion
		 */
		public boolean isInsert() {
			return insert;
		}
	}
}
