package gov.nasa.arc.pds.lace.client.event;

import gov.nasa.arc.pds.lace.shared.Container;

/**
 * Implements an event that is fired when an item is selected in the tree view.
 */
public class TreeItemSelectionEvent extends GenericEvent<Container, TreeItemSelectionEventHandler> {

	/** The GWT type of the event. */
	public static final Type<TreeItemSelectionEventHandler> TYPE = new Type<TreeItemSelectionEventHandler>();
	
	/**
	 * Creates a new event instance for a specific label item type.
	 * 
	 * @param type the label item type that the selected item in the tree represents.
	 */
	public TreeItemSelectionEvent(Container container) {
		super(container, TYPE);
	}
}
