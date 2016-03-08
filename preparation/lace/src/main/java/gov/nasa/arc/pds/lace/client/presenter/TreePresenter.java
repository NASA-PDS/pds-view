package gov.nasa.arc.pds.lace.client.presenter;

import gov.nasa.arc.pds.lace.client.event.ContainerChangedEvent;
import gov.nasa.arc.pds.lace.client.event.TreeItemSelectionEvent;
import gov.nasa.arc.pds.lace.client.event.ContainerChangedEvent.EventDetails;
import gov.nasa.arc.pds.lace.client.service.LabelContentsServiceAsync;
import gov.nasa.arc.pds.lace.shared.Container;
import gov.nasa.arc.pds.lace.shared.LabelItem;
import gov.nasa.arc.pds.lace.shared.LabelItemType;

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
		 * Adds the tree root item.
		 *
		 * @param root the tree root item
		 * @param name the root item label
		 */
		void addRootItem(TreeItem root, String name);

		/**
		 * Adds a child item to the parent tree item.
		 *
		 * @param parent a parent tree item
		 * @param name the child item name
		 * @return TreeItem the item that was added
		 */
		TreeItem addChildItem(TreeItem parent, String name);

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
	}

	private EventBus bus;
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
			LabelContentsServiceAsync labelContentsService
	) {
		super(view);
		this.bus = bus;
		this.labelContentsService = labelContentsService;
		view.setPresenter(this);
		getRootContainer(ELEMENT_NAME);

		bus.addHandler(ContainerChangedEvent.TYPE, new ContainerChangedEvent.Handler() {
			@Override
			public void onEvent(EventDetails details) {
				if (details.isRootContainer()) {
					populateTree(details.getContainer());
				} else {
					updateTree(details.getContainer());
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
		
		Display view = getView();
		TreeItem root = new TreeItem();
		view.removeAllItems();
		view.addRootItem(root, type.getElementName());
		map.clear();
		map.put(root, container);
		processTreeItemChildren(view, root, container.getContents());
		// Select the root item in the tree
		view.setSelectedItem(root);
	}

	private void updateTree(Container changedContainer) {		
		TreeItem selectedItem = getView().getSelectedItem();		
		if (map.get(selectedItem).equals(changedContainer))	{
			map.put(selectedItem, changedContainer);			
			selectedItem.removeItems();
			processTreeItemChildren(getView(), selectedItem, changedContainer.getContents());						
		} else {						
			TreeItem item = findChangedTreeItem(changedContainer, selectedItem);
			
			if (item == null) {
				GWT.log("Couldn't find the tree item for the changed container.");
				return;
			}
			
			map.put(item, changedContainer);
			item.removeItems();
			processTreeItemChildren(getView(), item, changedContainer.getContents());
		}
	}
	
	private TreeItem findChangedTreeItem(Container changedContainer, TreeItem item) {
		TreeItem changedItem = null;
		for (int i = 0; i < item.getChildCount(); i++) {
			TreeItem childItem = item.getChild(i);
			if (map.get(childItem).equals(changedContainer)) {
				return childItem;
			}
			findChangedTreeItem(changedContainer, childItem);
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
				TreeItem childItem = view.addChildItem(parent, container.getType().getElementName());
				map.put(childItem, container);
				processTreeItemChildren(view, childItem, container.getContents());
			}
		}
	}

}
