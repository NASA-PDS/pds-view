package gov.nasa.arc.pds.lace.client.presenter;

import gov.nasa.arc.pds.lace.client.event.CompleteStateChangedEvent;
import gov.nasa.arc.pds.lace.client.event.ContainerChangedEvent;
import gov.nasa.arc.pds.lace.client.event.ContainerChangedEvent.EventDetails;
import gov.nasa.arc.pds.lace.client.event.TreeItemSelectionEvent;
import gov.nasa.arc.pds.lace.client.resources.IconLookup;
import gov.nasa.arc.pds.lace.client.service.LabelContentsServiceAsync;
import gov.nasa.arc.pds.lace.shared.Container;
import gov.nasa.arc.pds.lace.shared.LabelItem;
import gov.nasa.arc.pds.lace.shared.LabelItemType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.inject.Inject;

/**
 * Implements a presenter for the tree widget.
 */
public class TreePresenter extends Presenter<TreePresenter.Display> {

	private static final String ELEMENT_NAME = "Product_Observational";

	private final LabelContentsServiceAsync labelContentsService;

	/**
	 * Defines an interface that the view must implement.
	 */
	public interface Display extends Presenter.Display<TreePresenter> {

		/**
		 * Removes all tree items.
		 */
		void removeAllItems();

		/**
		 * Adds a tree item as root to the tree.
		 *
		 * @param root the tree root item
		 * @param name the root item label
		 * @param icon the icon class name which corresponds to the container type
		 * @param isComplete flag to indicate whether the container is complete
		 */
		void addRootItem(TreeItem root, String name, String icon, boolean isComplete);

		/**
		 * Adds a child tree item to the parent item.
		 *
		 * @param parent the parent tree item
		 * @param name the child item name
		 * @param icon the icon class name which corresponds to the container type
		 * @param isComplete flag to indicate whether the container is complete
		 * 
		 * @return TreeItem the item that was added
		 */
		TreeItem addChildItem(TreeItem parent, String name, String icon, boolean isComplete);
		
		/**
		 * Inserts a child tree item to the parent item at the specified index.
		 *
		 * @param parent the parent tree item
		 * @param name the child item name
		 * @param icon the icon class name which corresponds to the container type
		 * @param isComplete flag to indicate whether the container is complete
		 * @param index the index where the item will be inserted
		 * 
		 * @return TreeItem the item that was added
		 */
		TreeItem insertItem(TreeItem parent, String name, String icon, boolean isComplete, int index);

		/**
		 * Sets whether the children of a tree item should be displayed or not.
		 *
		 * @param item the tree item
		 * @param open true, if the children of the item should be displayed
		 */
		void setState(TreeItem item, boolean open);

		/**
		 * Selects the specified item in the tree and fires the selection events.
		 *
		 * @param item the tree item to be selected
		 */
		void setSelectedItem(TreeItem item);

		/**
		 * Gets the currently selected item in the tree. 
		 * 
		 * @return TreeItem the selected tree item
		 */
		TreeItem getSelectedItem();
				
		/**
		 * Updates the complete state of the specified item and it's parents.
		 *  
		 * @param item
		 * @param complete
		 */
		void updateCompleteState(TreeItem item, boolean complete);
	}

	private EventBus bus;
	private IconLookup lookup;
	private Map<TreeItem, Container> map = new HashMap<TreeItem, Container>();

	/**
	 * Creates a new tree presenter for the tree view.
	 *
	 * @param view the display interface for the tree view
	 * @param bus the event bus to use for firing and receiving events
	 * @param labelContentsService the RPC service
	 */
	@Inject
	public TreePresenter(
			Display view,
			EventBus bus,
			IconLookup lookup,
			LabelContentsServiceAsync labelContentsService
	) {
		super(view);
		this.bus = bus;
		this.labelContentsService = labelContentsService;
		this.lookup = lookup;
		view.setPresenter(this);
		getRootContainer(ELEMENT_NAME);

		bus.addHandler(ContainerChangedEvent.TYPE, new ContainerChangedEvent.Handler() {
			
			@Override
			public void onEvent(EventDetails details) {
				if (details.isRootContainer()) {
					populateTree(details.getContainer());
				} else {
					Container newContainer;
					if ((newContainer = details.getNewContainer()) != null) {
						updateTree(details.getContainer(), newContainer);
					}	
				}
			}
			
		});		
		
		bus.addHandler(CompleteStateChangedEvent.TYPE, new CompleteStateChangedEvent.Handler() {
			
			@Override
			public void onEvent(CompleteStateChangedEvent.EventDetails details) {
				if (details.isSimpleItem()) {
					updateState(findChangedItem(details.getContainer()));
				}				
			}	
			
		});
	}

	/**
	 * Handles the tree item selection by firing TreeItemSelectionEvent
	 * to registered handlers.
	 *
	 * @param item the selected tree item
	 */
	public void handleItemSelection(TreeItem item) {
		getView().setState(item, true);
		bus.fireEvent(new TreeItemSelectionEvent(map.get(item)));
	}

	/**
	 * Gets the root container for the given element name by making an RPC call
	 * to the LabelContentsService service and updates the view.
	 *
	 * @param elementName the name of the root element
	 */
	public void getRootContainer(String elementName) {
		labelContentsService.getRootContainer(elementName, new AsyncCallback<Container>() {
			
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("The RPC call failed in getting the root container.");
			}

			@Override
			public void onSuccess(Container container) {
				bus.fireEvent(new ContainerChangedEvent(container, true));
			}
		});
	}

	private void populateTree(Container container) {
		assert container != null;

		LabelItemType type = container.getType();
		assert type != null;
				
		TreeItem root = new TreeItem();
		Display view = getView();
		view.removeAllItems();
		view.addRootItem(
				root,
				type.getElementName(),
				lookup.getIconClassName(type),
				container.isComplete()
		);
		
		map.clear();
		map.put(root, container);
		
		processTreeItemChildren(view, root, container.getContents());
		view.setSelectedItem(root); // Select the root item in the tree
	}

	/**
	 * Updates a sub-tree from the currently selected item.
	 *  
	 * @param container the changed container
	 * @param newContainer the newly inserted container
	 */
	private void updateTree(Container container, Container newContainer) {		
		Display view = getView();
		TreeItem selectedItem = view.getSelectedItem();
		
		// Find the tree item that represents the changed container.
		TreeItem changedItem = findChangedItem(container);
		
		if (changedItem == null) {
			String msg = "Couldn't find the tree item for the changed container '" +
					container.getType().getElementName() + "'.";
			GWT.log(msg);
			throw new NullPointerException(msg);
		}
		
		// Find the position where the new container should be inserted in the tree.
		int pos = parseContents(container.getContents()).indexOf(newContainer);
		LabelItemType type = newContainer.getType();
		TreeItem newItem = view.insertItem(
				changedItem,
				type.getElementName(),
				lookup.getIconClassName(type),
				newContainer.isComplete(),
				pos
		);
		
		processTreeItemChildren(view, newItem, newContainer.getContents());
		map.put(newItem, newContainer);
		
		// Display the children of the changed item as well as the (new) inserted item.
		view.setState(changedItem, true);
		view.setState(newItem, true);
		
		TreeItem parent = changedItem.getParentItem();
		while (parent != null && parent != selectedItem) {
			view.setState(parent, true);
			parent = parent.getParentItem();
		}
				
		updateState(changedItem);	
	}
	
	/**
	 * Updates the complete state of a tree item and it's parent items. 
	 * 
	 * @param item a tree item
	 */
	private void updateState(TreeItem item) {				
		TreeItem parent = item;
		while (parent != null) {				
			getView().updateCompleteState(parent, map.get(parent).isComplete());
			parent = parent.getParentItem();
		}
	}
	
	/**
	 * Parses the specified contents and constructs a list which
	 * contains only Container objects.
	 * 
	 * @param contents a list of label items
	 * @return a list of container objects
	 */
	private List<LabelItem> parseContents(List<LabelItem> contents) {
		List<LabelItem> items = new ArrayList<LabelItem>();
		for (LabelItem item : contents) {
			if (item instanceof Container) {
				items.add(item);
			}
		}
		return items;
	}
	
	/**
	 * Find the tree item that represents the changed container.
	 * 
	 * @param changedContainer
	 * @return
	 */
	private TreeItem findChangedItem(Container changedContainer) {		
		TreeItem selectedItem = getView().getSelectedItem();
		TreeItem changedItem = map.get(selectedItem).equals(changedContainer) ? 
				selectedItem :
				findChangedItem(changedContainer, selectedItem);
		
		return changedItem;
	}
	
	private TreeItem findChangedItem(Container changedContainer, TreeItem item) {
		TreeItem changedItem = null;
		
		for (int i = 0; i < item.getChildCount(); i++) {
			TreeItem childItem = item.getChild(i);
			if (map.get(childItem).equals(changedContainer)) {
				changedItem = childItem;
				break;
			}
			
			changedItem = findChangedItem(changedContainer, childItem);
			if (changedItem != null) {
				break;
			}
		}
		
		return changedItem;
	}
	
	private void processTreeItemChildren(Display view, TreeItem parent, List<LabelItem> labelItems) {
		assert (labelItems != null);

		int size = labelItems.size();		
		for (int i = 0; i < size; i++) {
			LabelItem labelItem = labelItems.get(i);			
			if (labelItem instanceof Container) {			
				Container container = (Container) labelItem;
				LabelItemType type = container.getType();				
				TreeItem childItem = view.addChildItem(
						parent,
						type.getElementName(),
						lookup.getIconClassName(type),
						container.isComplete()
				);
				
				map.put(childItem, container);
				processTreeItemChildren(view, childItem, container.getContents());
			}
		}
	}
}
