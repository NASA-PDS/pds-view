package gov.nasa.arc.pds.lace.client.presenter;

import gov.nasa.arc.pds.lace.client.event.CreateLabelEvent;
import gov.nasa.arc.pds.lace.client.event.FieldValueChangedEvent;
import gov.nasa.arc.pds.lace.client.event.LabelChangedEvent;
import gov.nasa.arc.pds.lace.client.event.RootContainerChangedEvent;
import gov.nasa.arc.pds.lace.client.event.SystemFailureEvent;
import gov.nasa.arc.pds.lace.client.event.TemplateNameChangedEvent;
import gov.nasa.arc.pds.lace.client.event.TreeItemSelectionEvent;
import gov.nasa.arc.pds.lace.client.event.ValidationEvent;
import gov.nasa.arc.pds.lace.client.resources.IconLookup;
import gov.nasa.arc.pds.lace.client.service.LabelContentsServiceAsync;
import gov.nasa.arc.pds.lace.client.util.ContainerNameHelper;
import gov.nasa.arc.pds.lace.client.view.TreeView;
import gov.nasa.arc.pds.lace.shared.AttributeItem;
import gov.nasa.arc.pds.lace.shared.Container;
import gov.nasa.arc.pds.lace.shared.LabelElement;
import gov.nasa.arc.pds.lace.shared.LabelItem;
import gov.nasa.arc.pds.lace.shared.LabelItemType;
import gov.nasa.arc.pds.lace.shared.SimpleItem;
import gov.nasa.arc.pds.lace.shared.ValidationMessage;
import gov.nasa.arc.pds.lace.shared.ValidationResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.inject.ImplementedBy;

/**
 * Implements a presenter for the tree widget.
 */
public class TreePresenter extends Presenter<TreePresenter.Display> {

	/**
	 * Defines an interface that the view must implement.
	 */
	@ImplementedBy(TreeView.class)
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
		 * Removes the specified tree item from the parent item.
		 *
		 * @param parent the parent tree item
		 * @param item the tree item to remove
		 */
		void removeItem(TreeItem parent, TreeItem item);

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

		/**
		 * Updates the given item's text with the new value.
		 *
		 * @param item the tree item to be updated with a new text
		 * @param text the new text
		 */
		void updateItemText(TreeItem item, String text);
	}

	private IconLookup lookup;
	private Feedback feedback;
	private final LabelContentsServiceAsync service;
	private Map<TreeItem, Container> map = new HashMap<TreeItem, Container>();
	private Container rootContainer;
	private TreeItem rootItem;
	private boolean needsSave = false;
	private boolean validationInProgress = false;
	private boolean showFeedback = false;

	/**
	 * Creates a new tree presenter for the tree view.
	 *
	 * @param view the display interface for the tree view
	 * @param lookup
	 * @param service the RPC service
	 */
	@Inject
	public TreePresenter(
			Display view,
			IconLookup lookup,
			Feedback feedback,
			LabelContentsServiceAsync service
	) {
		super(view);
		this.service = service;
		this.lookup = lookup;
		this.feedback = feedback;
		view.setPresenter(this);
		scheduleValidation();
	}

	@Override
	protected void addEventHandlers() {

		addEventHandler(LabelChangedEvent.TYPE, new LabelChangedEvent.Handler() {
			@Override
			public void onEvent(LabelChangedEvent.Data data) {
				if (data.getRootContainer() != null) {
					rootContainer = data.getRootContainer();
					showFeedback = false;
					populateTree(rootContainer);
				} else {
					showFeedback = true;
					LabelElement newElement = data.getElement();
					if (newElement != null && newElement instanceof Container) {
						updateTree(data.getChangedContainer(), (Container) newElement, data.isInsert());
					}
				}

				needsSave = true;
			}
		});

		addEventHandler(CreateLabelEvent.TYPE, new CreateLabelEvent.Handler() {
			@Override
			public void onEvent(CreateLabelEvent.Data data) {
				getRootContainer(data.getElementName(), data.getNamespaceURI());
			}
		});

		addEventHandler(ValidationEvent.TYPE, new ValidationEvent.Handler() {
			@Override
			public void onEvent(Void data) {
				for (TreeItem item : map.keySet()) {
					updateState(item);
				}
			}
		});

		addEventHandler(FieldValueChangedEvent.TYPE, new FieldValueChangedEvent.Handler() {
			@Override
			public void onEvent(FieldValueChangedEvent.EventDetails details) {
				Container container = details.getContainer();
				String name = ContainerNameHelper.getContainerElementName(container);
				getView().updateItemText(findItem(container, rootItem), name);
			}
		});
	}

	private void scheduleValidation() {
		Timer timer = new Timer() {
			@Override
			public void run() {
				if (needsSave && !validationInProgress) {
					needsSave = false;
					validationInProgress = true;
					validateLabel();
				}
			}
		};
		timer.scheduleRepeating(2000);
	}

	/**
	 * Handles the tree item selection by firing TreeItemSelectionEvent
	 * to registered handlers.
	 *
	 * @param item the selected tree item
	 */
	public void handleItemSelection(TreeItem item) {
		getView().setState(item, true);
		fireEvent(new TreeItemSelectionEvent(map.get(item)));
	}

	/**
	 * Gets the root container for the given element name by making an RPC call
	 * to the service and updates the view.
	 *
	 * @param elementName the name of the root element
	 * @param namespaceURI the namespace of the root element
	 */
	private void getRootContainer(String elementName, String namespaceURI) {
		feedback.display("Loading...", true);

		service.getRootContainer(elementName, namespaceURI, new AsyncCallback<Container>() {

			@Override
			public void onFailure(Throwable caught) {
				String msg = "There was an error while trying to get the root element.";
				GWT.log(msg + " " + caught.getMessage());
				fireEvent(new SystemFailureEvent(msg, feedback));
			}

			@Override
			public void onSuccess(Container container) {
				fireEvent(new LabelChangedEvent(container));
				fireEvent(new RootContainerChangedEvent(container));
				fireEvent(new TemplateNameChangedEvent(null));
				feedback.hide();
				feedback.display("Label successfully created", false);
			}
		});
	}

	private void populateTree(Container container) {
		assert container != null;

		LabelItemType type = container.getType();
		assert type != null;

		TreeItem root = new TreeItem();
		rootItem = root;
		Display view = getView();
		view.removeAllItems();
		view.addRootItem(
				root,
				type.getElementName(),
				lookup.getIconClassName(type.getElementName()),
				container.isComplete()
		);
		map.clear();
		map.put(root, container);
		addTreeItemChildren(view, root, container.getContents());
		view.setSelectedItem(root); // Select the root item in the tree
	}

	/**
	 * Updates a sub-tree from the currently selected item.
	 *
	 * @param changedContainer the changed container
	 * @param container the newly inserted or deleted container
	 * @param insert flag to indicate whether to insert the container or to delete it
	 */
	private void updateTree(Container changedContainer, Container container, boolean insert) {
		Display view = getView();
		TreeItem selectedItem = view.getSelectedItem();

		// Find the tree item that represents the changed container.
		TreeItem changedItem = findItem(changedContainer);

		if (changedItem == null) {
			String msg = "Couldn't find the tree item for the changed container '" +
					changedContainer.getType().getElementName() + "'.";
			GWT.log(msg);
			throw new NullPointerException(msg);
		}

		if (insert) {
			LabelItemType type = container.getType();
			TreeItem newItem = view.insertItem(
					changedItem,
					type.getElementName(),
					lookup.getIconClassName(type.getElementName()),
					container.isComplete(),
					parseContents(changedContainer.getContents()).indexOf(container)
			);
			addTreeItemChildren(view, newItem, container.getContents());
			map.put(newItem, container);

			// Display the children of the changed item as well as the (new) inserted item.
			view.setState(changedItem, true);
			view.setState(newItem, true);

			TreeItem parent = changedItem.getParentItem();
			while (parent != null && parent != selectedItem) {
				view.setState(parent, true);
				parent = parent.getParentItem();
			}
		} else {
			TreeItem item = findItem(container);
			removeTreeItemChildren(item);
			map.remove(item);
			view.removeItem(changedItem, item);
		}
	}

	/**
	 * Updates the complete state of a tree item and it's parent items.
	 *
	 * @param item a tree item
	 */
	private void updateState(TreeItem item) {
		TreeItem parent = item;
		Display view = getView();
		while (parent != null) {
			Container container = map.get(parent);
			if (container != null) {
				view.updateCompleteState(parent, container.isComplete());
			}
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
	 * @param container
	 * @return
	 */
	private TreeItem findItem(Container container) {
		TreeItem selectedItem = getView().getSelectedItem();
		TreeItem changedItem = map.get(selectedItem).equals(container) ?
				selectedItem :
				findItem(container, selectedItem);

		return changedItem;
	}

	private TreeItem findItem(Container container, TreeItem item) {
		TreeItem changedItem = null;

		for (int i = 0; i < item.getChildCount(); i++) {
			TreeItem childItem = item.getChild(i);
			if (map.get(childItem).equals(container)) {
				changedItem = childItem;
				break;
			}

			changedItem = findItem(container, childItem);
			if (changedItem != null) {
				break;
			}
		}

		return changedItem;
	}

	private void addTreeItemChildren(Display view, TreeItem parent, List<LabelItem> labelItems) {
		assert (labelItems != null);

		int size = labelItems.size();

		for (int i = 0; i < size; i++) {
			LabelItem labelItem = labelItems.get(i);

			if (labelItem instanceof Container) {
				Container container = (Container) labelItem;
				TreeItem childItem = view.addChildItem(
						parent,
						ContainerNameHelper.getContainerElementName(container),
						lookup.getIconClassName(container.getType().getElementName()),
						container.isComplete()
				);
				map.put(childItem, container);
				addTreeItemChildren(view, childItem, container.getContents());
			}
		}
	}

	private void removeTreeItemChildren(TreeItem item) {
		if (item != null) {
			int count = item.getChildCount();

			for (int i = 0; i < count; i++) {
				TreeItem childItem = item.getChild(i);
				removeTreeItemChildren(childItem);
				map.remove(childItem);
				getView().removeItem(item, childItem);
			}
		}
	}

	private void validateLabel() {
		service.validateModel(rootContainer, new AsyncCallback<ValidationResult>() {

			@Override
			public void onFailure(Throwable caught) {
				String msg = "The system was not able to validate the label.";
				GWT.log(msg + " " + caught.getMessage());
				fireEvent(new SystemFailureEvent(msg));
				validationInProgress = false;
			}

			@Override
			public void onSuccess(ValidationResult result) {
				setErrors(rootContainer, result);
				fireEvent(new ValidationEvent());

				if (showFeedback) {
					feedback.display("Changes saved.", false);
				}

				validationInProgress = false;
			}

			private void setErrors(LabelItem item, ValidationResult result) {
				RegExp varPattern = null;
				if (result.getVariablePattern() != null) {
					varPattern = RegExp.compile(result.getVariablePattern());
				}
				setErrors(item, result, varPattern);
			}

			private void setErrors(LabelItem item, ValidationResult result, RegExp varPattern) {
				if (item instanceof LabelElement) {
					LabelElement e = (LabelElement) item;
					e.clearErrorMessages();
					for (AttributeItem attr : e.getAttributes()) {
						attr.clearErrorMessages();
					}

					List<ValidationMessage> messages = result.getMessages(e.getID());
					if (messages != null) {
						for (ValidationMessage message : messages) {
							if (message.getAttributeName() != null) {
								addAttributeMessage(e, message.getMessage(), message.getAttributeName(), varPattern);
								GWT.log("Attribute " + message.getAttributeName()
										+ " of element " + e.getID() + " had error: " + message);
							} else if ((item instanceof SimpleItem) && message.getValue() != null) {
								addMessageByValue((SimpleItem) e, message.getMessage(), message.getValue(), varPattern);
								GWT.log("Element " + e.getID() + " had error: " + message);
							} else if (!(e instanceof SimpleItem) || varPattern==null
									|| !varPattern.test(((SimpleItem) e).getValue())) {
								e.addErrorMessage(message.getMessage());
								GWT.log("Element " + e.getID() + " had error: " + message);
							}
							// else omit the message
						}
					}

					if (item instanceof Container) {
						boolean childrenHaveErrors = false;

						for (LabelItem child : ((Container) item).getContents()) {
							setErrors(child, result, varPattern);

							if (child instanceof LabelElement) {
								String[] childMessages = ((LabelElement) child).getErrorMessages();
								if (childMessages!=null && childMessages.length > 0) {
									childrenHaveErrors = true;
								}
							}
						}

						if (childrenHaveErrors) {
							e.addErrorMessage("Child elements contain errors.");
						}
					}
				}
			}

			/**
			 * Adds a message to an attribute, if the attribute is found, otherwise
			 * the parent element.
			 *
			 * @param e the parent element
			 * @param message the validation message
			 * @param attrName the name of the attribute
			 * @param varPattern the pattern for variable values
			 */
			private void addAttributeMessage(LabelElement e, String message, String attrName, RegExp varPattern) {
				boolean found = false;

				for (AttributeItem attr : e.getAttributes()) {
					if (attr.getType().getElementName().equals(attrName)) {
						found = true;

						// If the value isn't a variable, add the message.
						if (varPattern==null || !varPattern.test(attr.getValue())) {
							attr.addErrorMessage(message);
						}
					}
				}

				if (!found) {
					e.addErrorMessage(message);
				}
			}

			/**
			 * Adds a message to an element or its attributes, matching
			 * attributes by their current value. That is, the message is
			 * complaining about a value being invalid, but the message may
			 * apply to an attribute or the element. If the element value matches,
			 * the message is added to the element. Otherwise, it is added to
			 * the matching attribute.
			 *
			 * @param e the parent element
			 * @param message the validation message
			 * @param value the value the message is complaining about
			 * @param varPattern the pattern for variable values
			 */
			private void addMessageByValue(SimpleItem e, String message, String value, RegExp varPattern) {
				boolean found = false;

				if (varPattern!=null && varPattern.test(value)) {
					// Ignore
					return;
				}

				if (!valueMatches(e.getValue(), value)) {
					for (AttributeItem attr : e.getAttributes()) {
						if (valueMatches(attr.getValue(), value)) {
							found = true;
							attr.addErrorMessage(message);
						}
					}
				}

				if (!found) {
					e.addErrorMessage(message);
				}
			}

			private boolean valueMatches(String v1, String v2) {
				if (v1 == null) {
					v1 = "";
				}
				if (v2 == null) {
					v2 = "";
				}

				return v1.equals(v2);
			}

		});
	}
}
