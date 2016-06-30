package gov.nasa.arc.pds.lace.client.event;

import gov.nasa.arc.pds.lace.shared.Container;

/**
 * Defines an event that is fired when the root container is changed.
 * For example, when the user requests a new label, or imports a
 * label from an XML file.
 */
public class RootContainerChangedEvent extends GenericEvent<RootContainerChangedEvent.Data, RootContainerChangedEvent.Handler> {

	/** The GWT type for this event. */
	public static final Type<RootContainerChangedEvent.Handler> TYPE = new Type<RootContainerChangedEvent.Handler>();

	/**
	 * Defines the interface that a handler for this event must implement.
	 */
	public interface Handler extends GenericEventHandler<RootContainerChangedEvent.Data> { }
	
	/**
	 * Creates a new instance with the given data.
	 *
	 * @param container the new root container
	 */
	public RootContainerChangedEvent(Container rootContainer) {
		super(new Data(rootContainer), TYPE);
	}
	
	/**
	 * The event data.
	 */
	public static class Data {
	
		private Container rootContainer;
		
		/**
		 * Creates a new instance of the event data. 
		 * 
		 * @param container the new root container 
		 */
		public Data(Container rootContainer) {
			this.rootContainer = rootContainer;
		}

		/**
		 * Gets the new root container.
		 * 
		 * @return the new root container
		 */
		public Container getRootContainer() {
			return rootContainer;
		}
	}
}
