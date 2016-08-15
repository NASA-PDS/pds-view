package gov.nasa.arc.pds.lace.client.event;

import gov.nasa.arc.pds.lace.shared.Container;

/**
 * Defines an event that is fired when an existing label is imported.
 */
public class LabelImportedEvent extends GenericEvent<LabelImportedEvent.EventDetails, LabelImportedEvent.Handler> {

	/** The GWT type for this event. */
	public static final Type<LabelImportedEvent.Handler> TYPE = new Type<LabelImportedEvent.Handler>();

	/**
	 * Defines the interface that a handler for this event must implement.
	 */
	public interface Handler extends GenericEventHandler<LabelImportedEvent.EventDetails> { }
		
	/**
	 * Implements an object that holds details about
	 * the changed container.  
	 */
	public static class EventDetails {
		
		private Container container;
		private String fileName;		
		
		/**
		 * Creates an instance of the <code>EventDetails</code> object.
		 * 
		 * @param container the imported root container 
		 * @param fileName the imported label file name 
		 */
		public EventDetails(Container container, String fileName) {
			this.container = container;
			this.fileName = fileName;					
		}
		
		/**
		 * Gets the imported root container.
		 * 
		 * @return a container
		 */
		public Container getContainer() {
			return container;
		}
		
		/**
		 * Gets the imported label file name.
		 * 
		 * @return the label file name
		 */
		public String getFileName() {
			return fileName;
		}
	}
	
	/**
	 * Creates a new instance with the given data.
	 * 
	 * @param container the imported root container 
	 * @param fileName the imported label file name
	 */
	public LabelImportedEvent(Container container, String fileName) {
		super(new EventDetails(container, fileName), TYPE);
	}
}
