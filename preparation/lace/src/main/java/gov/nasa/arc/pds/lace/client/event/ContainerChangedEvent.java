package gov.nasa.arc.pds.lace.client.event;

import gov.nasa.arc.pds.lace.shared.Container;

/**
 * Defines an event that is fired when the container that
 * is being edited is changed. For example, when the user
 * starts editing a new label template, or when importing
 * a label from an XML file.
 */
public class ContainerChangedEvent extends GenericEvent<ContainerChangedEvent.EventDetails, ContainerChangedEvent.Handler> {

	/** The GWT type for this event. */
	public static final Type<ContainerChangedEvent.Handler> TYPE = new Type<ContainerChangedEvent.Handler>();

	/**
	 * Defines the interface that a handler for this event must implement.
	 */
	public interface Handler extends GenericEventHandler<ContainerChangedEvent.EventDetails> { }
	
	/**
	 * Implements an object that holds details about
	 * the changed container.  
	 */
	public static class EventDetails {
	
		private Container container;
		private boolean rootContainer;
		private Container newContainer;
		
		/**
		 * Creates an instance of the <code>EventDetails</code> object.
		 * 
		 * @param container the new root container or a changed container
		 * @param rootContainer flag to indicate whether the changed container is root or not
		 */
		public EventDetails(Container container, boolean rootContainer) {
			this.container = container;
			this.rootContainer = rootContainer; 
		}
		
		/**
		 * Creates an instance of the <code>EventDetails</code> object.
		 * 
		 * @param container the changed container
		 * @param newContainer the newly inserted container
		 * 
		 */
		public EventDetails(Container container, Container newContainer) {
			this.container = container;	
			this.newContainer = newContainer;
			this.rootContainer = false;
		}
		
		public Container getContainer() {
			return container;
		}
		
		public boolean isRootContainer() {
			return rootContainer;
		}
		
		public Container getNewContainer() {
			return newContainer;
		}
	}

	/**
	 * Creates a new instance with the given data.
	 *
	 * @param container the new root container or a changed container 
	 * @param rootContainer whether the container is a new root or not.
	 */
	public ContainerChangedEvent(Container container, boolean rootContainer) {
		super(new EventDetails(container, rootContainer), TYPE);
	}
	
	/**
	 * Creates a new instance with the given data.
	 *	
	 * @param container the changed container 
	 * @param newContainer the newly inserted container
	 */
	public ContainerChangedEvent(Container container, Container newContainer) {
		super(new EventDetails(container, newContainer), TYPE);
	}
}