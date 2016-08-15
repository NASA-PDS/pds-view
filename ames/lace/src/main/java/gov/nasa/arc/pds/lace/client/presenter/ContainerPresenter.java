package gov.nasa.arc.pds.lace.client.presenter;

import gov.nasa.arc.pds.lace.client.AppInjector;
import gov.nasa.arc.pds.lace.client.event.ElementDeletionEvent;
import gov.nasa.arc.pds.lace.client.event.ElementInsertedEvent;
import gov.nasa.arc.pds.lace.client.event.ElementPasteEvent;
import gov.nasa.arc.pds.lace.client.event.ElementSelectionEvent;
import gov.nasa.arc.pds.lace.client.event.ElementSelectionEvent.EventDetails;
import gov.nasa.arc.pds.lace.client.event.ElementSelectionEventHandler;
import gov.nasa.arc.pds.lace.client.event.FieldValueChangedEvent;
import gov.nasa.arc.pds.lace.client.event.LabelChangedEvent;
import gov.nasa.arc.pds.lace.client.event.SystemFailureEvent;
import gov.nasa.arc.pds.lace.client.event.ValidationEvent;
import gov.nasa.arc.pds.lace.client.resources.IconLookup;
import gov.nasa.arc.pds.lace.client.service.LabelContentsServiceAsync;
import gov.nasa.arc.pds.lace.client.util.ClipboardManager;
import gov.nasa.arc.pds.lace.client.util.ContainerNameHelper;
import gov.nasa.arc.pds.lace.client.util.InsertOptionMap;
import gov.nasa.arc.pds.lace.client.view.ContainerView;
import gov.nasa.arc.pds.lace.shared.AttributeItem;
import gov.nasa.arc.pds.lace.shared.Container;
import gov.nasa.arc.pds.lace.shared.InsertOption;
import gov.nasa.arc.pds.lace.shared.InsertionPoint;
import gov.nasa.arc.pds.lace.shared.LabelElement;
import gov.nasa.arc.pds.lace.shared.LabelItem;
import gov.nasa.arc.pds.lace.shared.LabelItemType;
import gov.nasa.arc.pds.lace.shared.ResultType;
import gov.nasa.arc.pds.lace.shared.SimpleItem;

import java.util.List;
import java.util.ListIterator;

import javax.inject.Inject;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.ImplementedBy;
import com.google.inject.Provider;

/**
 * Implements the presenter for a widget that displays a container.
 */
public class ContainerPresenter extends Presenter<ContainerPresenter.Display> {

	/**
	 * Defines an interface that the view must implement.
	 *
	 */
	@ImplementedBy(ContainerView.class)
	public interface Display extends Presenter.Display<ContainerPresenter> {

		/**
		 * Expands or collapses the container.
		 */
		void toggleContainer();

		/**
		 * Sets the container title.
		 *
		 * @param title the name of the complex element that represents the container.
		 */
		void setContainerTitle(String title);

		/**
		 * Gets the container title which is the complex element name.
		 *
		 * @return the container title
		 */
		String getContainerTitle();

		/**
		 * Sets an icon representing the container type. For an incomplete container,
		 * an alert icon will be also displayed.
		 *
		 * @param className the CSS class name that represents an icon for this container type
		 * @param isComplete flag to indicate whether or not the container is complete
		 */
		void setIcon(String className, boolean isComplete);

		/**
		 * Sets whether the container should be displayed as complete.
		 *
		 * @param complete true, if the container is complete
		 */
		void setState(boolean complete);

		/**
		 * Adds the given widget to the container's attribute area.
		 *
		 * @param widget the widget for the attribute
		 */
		void addAttribute(Widget widget);

		/**
		 * Adds the given widget to the container's content area.
		 *
		 * @param widget the widget to add to the container's content
		 */
		void add(Widget widget);

		/**
		 * Clears the content of the container.
		 */
		void clearContent();

		/**
		 * Inserts the specified widget at the given index.
		 *
		 * @param widget
		 * @param index
		 * @throw {@link IndexOutOfBoundsException}
		 */
		void insert(Widget widget, int index);

		/**
		 * Shows the widget.
		 *
		 * @param contentOnly flag to indicate whether to show the content area or not
		 */
		void show(boolean contentOnly);

		/**
		 * Removes a widget at position index.
		 *
		 * @param index
		 * @throw {@link IndexOutOfBoundsException}
		 */
		void remove(int index);

		/**
		 * Adds event listeners that are used for handling
		 * events related to modifying an item (e.g. delete).
		 */
		void addDeleteEventListeners();

		/**
		 * Removes event listeners that are used for handling
		 * events related to modifying an item (e.g. delete).
		 */
		void removeDeleteEventListeners();

		/**
		 * Enables/disables the modification UI.
		 *
		 * @param enable true to enable the modification UI, false to disable it
		 */
		void enableModification(boolean enable);

		/**
		 * Tests whether the delete events are already attached.
		 *
		 * @return true, if delete events are attached
		 */
		boolean isDeleteEventsAttached();

		/**
		 * Adds event listeners.
		 */
		void addEventListeners();

		/**
		 * Sets the error messages associated with this container.
		 *
		 * @param messages an array of error messages
		 */
		void setErrors(String[] messages);

		/**
		 * Sets the documentation string for the item.
		 *
		 * @param documentation the HTML documentation for the item
		 */
		void setDocumentation(String documentation);

		/**
		 * Shows or hides the documentation for the item.
		 *
		 * @param show true, if the documentation should be shown
		 */
		void showDocumentation(boolean show);

	}

	private boolean isContentShown = false; // Flag to indicate whether the content is shown or not.
	private boolean contentOnly = false;    // Flag to indicate whether to just show the content.
	private Container container;
	private AppInjector injector;
	private LabelContentsServiceAsync service;
	private ContainerPresenter presenter;
	private IconLookup lookup;
	private Provider<PopupPresenter> popupProvider;
	private InsertOptionMap insertOptionMap;
	private ContainerPresenter parentPresenter;
	private boolean documentationIsVisible = false;
	private ClipboardManager clipboard;

	// TODO: Use the provider interface instead of the injector to get the objects.
	/**
	 * Creates a new instance of the container presenter.
	 *
	 * @param view the display interface for the container view
	 * @param lookup the icon lookup service
	 * @param injector the application injector for creating new label items
	 * @param service the RPC service
	 * @param popupProvider a provider of the popup presenter
	 * @param insertOptionMap the map of insertion options
	 * @param clipboard the clipboard manager
	 */
	@Inject
	public ContainerPresenter(
			Display view,
			IconLookup lookup,
			AppInjector injector,
			LabelContentsServiceAsync service,
			Provider<PopupPresenter> popupProvider,
			InsertOptionMap insertOptionMap,
			ClipboardManager clipboard
	) {
		super(view);
		this.injector = injector;
		this.service = service;
		this.presenter = this;
		this.lookup = lookup;
		this.popupProvider = popupProvider;
		this.insertOptionMap = insertOptionMap;
		this.clipboard = clipboard;
		view.setPresenter(this);
	}

	/**
	 * Displays the specified container.
	 *
	 * @param container the container object to display
	 * @param contentOnly flag to indicate whether to display
	 * the content or the whole widget
	 */
	public void display(Container container, ContainerPresenter parentPresenter, boolean contentOnly) {
		this.container = container;
		this.parentPresenter = parentPresenter;
		this.contentOnly = contentOnly;
		Display view = getView();

		InsertOption alternative = container.getInsertOption();
		if (alternative != null) {
			int id = alternative.getId();
			if (insertOptionMap.get(id) == null) {
				insertOptionMap.put(id, alternative);
			}
		}

		view.show(contentOnly);

		if (contentOnly) {
			showContent(container.getAttributes(), container.getContents());
		} else {
			view.setContainerTitle(ContainerNameHelper.getContainerElementName(container));
			view.setIcon(lookup.getIconClassName(container.getType().getElementName()), container.isComplete());
			view.addEventListeners();

			if (container.isDeletable()) {
				view.addDeleteEventListeners();
			}
		}

		updateErrors();

		if (container.getType().getDocumentation() != null) {
			getView().setDocumentation(container.getType().getDocumentation());
		}
	}

	@Override
	protected void addEventHandlers() {
		addEventHandler(ElementSelectionEvent.TYPE, new ElementSelectionEventHandler() {

			@Override
			public void onEvent(EventDetails data) {
				if (container.getContents().contains(data.getInsertionPoint()) &&
						data.getParentPresenter().equals(presenter)) {
					insertElement(data, data.getPopup());
				}
			}
		});

		addEventHandler(ElementPasteEvent.TYPE, new ElementPasteEvent.Handler() {
			@Override
			public void onEvent(ElementPasteEvent.EventDetails data) {
				if (container.getContents().contains(data.getInsertionPoint()) &&
						data.getParentPresenter().equals(presenter)) {
					pasteElement(data);
				}
			}
		});

		addEventHandler(ElementInsertedEvent.TYPE, new ElementInsertedEvent.Handler() {

			@Override
			public void onEvent(ElementInsertedEvent.EventDetails data) {
				if (!data.isSimpleItem()) {
					attachMouseEvents();
				}
			}

		});

		addEventHandler(ElementDeletionEvent.TYPE, new ElementDeletionEvent.Handler() {

			@Override
			public void onEvent(ElementDeletionEvent.EventDetails data) {
				if (container.getContents().contains(data.getLabelElement()) &&
						data.getParentPresenter().equals(presenter)) {
					deleteElement(data.getLabelElement(), data.getPopup());
				}
			}
		});

		addEventHandler(ValidationEvent.TYPE, new ValidationEvent.Handler() {
			@Override
			public void onEvent(Void data) {
				updateErrors();
				getView().setState(container.isComplete());
			}
		});

		addEventHandler(FieldValueChangedEvent.TYPE, new FieldValueChangedEvent.Handler() {
			@Override
			public void onEvent(FieldValueChangedEvent.EventDetails details) {
				if (details.getContainerPresenter().equals(presenter)) {
					getView().setContainerTitle(ContainerNameHelper.getContainerElementName(details.getContainer()));
				}
			}
		});
	}

	private void deleteElement(final LabelElement element, final PopupPresenter popup) {
			service.deleteElement(container, element, new AsyncCallback<ResultType>() {

				@Override
				public void onFailure(Throwable caught) {
					GWT.log("Unable to delete element: " + caught.getMessage());
					if (popup != null) {
						popup.hide();
					}
					fireEvent(new SystemFailureEvent("Server communication error. Unable to delete '" + element.getType().getElementName() + "' element."));
				}

				@Override
				public void onSuccess(ResultType result) {
					GWT.log("Element '" + element.getType().getElementName() + "' is deleted successfully.");
					boolean isSimple = (element instanceof SimpleItem) ? true : false;
					updateContents(result);

					// Notify the containers/simple items that an element was deleted.
					fireEvent(new ElementInsertedEvent(isSimple));

					// Fire an event for the tree view to update itself and save/validate.
					fireEvent(new LabelChangedEvent(container, element, false));

					if (popup != null) {
						popup.hide();
					}
				}
			});
	}

	private void attachMouseEvents() {
		if (!contentOnly) {
			Display view = getView();
			boolean deletable = container.isDeletable();
			if (deletable && !view.isDeleteEventsAttached()) {
				view.addDeleteEventListeners();
			} else if (!deletable && view.isDeleteEventsAttached()) {
				view.removeDeleteEventListeners();
			}
		}
	}

	/**
	 * Handles the click event which triggers when
	 * the user clicks the expand/collapse icon.
	 */
	public void handleContainerClickEvent() {
		if (!isContentShown) {
			showContent(container.getAttributes(), container.getContents());
			isContentShown = true;
		}
		getView().toggleContainer();
	}

	/**
	 * Handles the mouseOver, mouseMove and mouseOut events.
	 *
	 * @param over true, for mouseOver or mouseMove event, false for mouseOut event
	 */
	public void handleMouseEvent(boolean over) {
		getView().enableModification(over);
	}

	/**
	 * Handles a user request to cut the element.
	 */
	public void onCut() {
		getView().enableModification(false);
		clipboard.saveItem(container);
		fireEvent(new ElementDeletionEvent(container, parentPresenter, null));
	}

	/**
	 * Handles a user request to copy the element.
	 */
	public void onCopy() {
		clipboard.saveItem(container);
	}

	/**
	 * Handles the user's request to delete an element.
	 */
	public void onDelete() {
		getView().enableModification(false);

		final PopupPresenter popup = popupProvider.get();

		String msg = "This action will permanently delete the '" + container.getType().getElementName()
					+ "' element. Do you STILL want to delete the element?";

		ClickHandler yesBtnHandler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				event.stopPropagation();
				// Fire an event to the parent container to delete the item
				fireEvent(new ElementDeletionEvent(container, parentPresenter, popup));

			}
		};

		popup.showConfirmationBox("Warning", msg, yesBtnHandler, null);
	}

	/**
	 * Handles the ElementSelectionEvent event which is fired when an element
	 * is selected from an insertion point within this container. Inserts the
	 * selected element to this container and updates the model object.
	 *
	 * @param data the event details
	 */
	private void insertElement(EventDetails data, final PopupPresenter popup) {
		final LabelItemType type = data.getType();
		service.updateContainer(container, data.getInsertionPoint(), data.getAlternativeIndex(),
				type, new AsyncCallback<ResultType>() {

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Unable to insert element: " + caught.getMessage());
				if (popup != null) {
					popup.hide();
				}
				fireEvent(new SystemFailureEvent("Server communication error. Unable to insert '" + container.getType().getElementName() + "' element."));
			}

			@Override
			public void onSuccess(ResultType result) {
				GWT.log("Element '" + type.getElementName() + "' is inserted successfully.");
				Container newContainer = updateContents(result);

				// Notify the containers/simple items that an element was inserted.
				fireEvent(new ElementInsertedEvent(newContainer == null));

				// Fire an event for the tree view to update itself and save/validate.
				fireEvent(new LabelChangedEvent(container, newContainer, true));

				if (popup != null) {
					popup.hide();
				}
			}
		});
	}

	/**
	 * Handles the ElementSelectionEvent event which is fired when an element
	 * is selected from an insertion point within this container. Inserts the
	 * selected element to this container and updates the model object.
	 *
	 * @param data the event details
	 */
	private void pasteElement(ElementPasteEvent.EventDetails data) {
		final LabelElement element = data.getElement();

		service.pasteElement(container, element, data.getInsertionPoint(), new AsyncCallback<ResultType>() {
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Unable to insert element: " + caught.getMessage());
				fireEvent(new SystemFailureEvent("Server communication error. Unable to paste '" + container.getType().getElementName() + "' element."));
			}

			@Override
			public void onSuccess(ResultType result) {
				GWT.log("Element '" + element.getType().getElementName() + "' is inserted successfully.");
				Container newContainer = updateContents(result);

				// Notify the containers/simple items that an element was inserted.
				fireEvent(new ElementInsertedEvent(newContainer == null));

				// Fire an event for the tree view to update itself and save/validate.
				fireEvent(new LabelChangedEvent(container, newContainer, true));
			}
		});
	}

	private Container updateContents(ResultType result) {
		int from = result.getFromIndex();
		int to = result.getToIndex();
		List<LabelItem> newItems = result.getNewItems();
		List<LabelItem> contents = container.getContents();
		Display view = getView();

		// Remove objects within 'from' and 'to' indices
		for (int i = from; i <= to; i++) {
			contents.remove(from);
			view.remove(from);
		}

		for (LabelItem item : newItems) {
			if (item instanceof LabelElement) {
				InsertOption alternative = ((LabelElement) item).getInsertOption();
				InsertOption existingAlternative = insertOptionMap.get(alternative.getId());
				if (existingAlternative != null) {
					existingAlternative.setUsedOccurrences(alternative.getUsedOccurrences());
					((LabelElement) item).setInsertOption(existingAlternative);
				}
			} else {
				ListIterator<InsertOption> it = ((InsertionPoint) item).getAlternatives().listIterator();
				while(it.hasNext()) {
					InsertOption alternative = it.next();
					InsertOption existingAlternative = insertOptionMap.get(alternative.getId());
					if (existingAlternative != null) {
						existingAlternative.setUsedOccurrences(alternative.getUsedOccurrences());
						it.set(existingAlternative);
					}
				}
			}
		}

		return displayNewItems(newItems, contents, from);
	}

	private Container displayNewItems(List<LabelItem> items, List<LabelItem> contents, int pos) {
		Container newContainer = null;

		for (LabelItem item : items) {
			contents.add(pos, item);

			if (item instanceof Container) {
				newContainer = (Container) item;
				ContainerPresenter presenter = getPresenter(newContainer);
				getView().insert(presenter.asWidget(), pos);
				presenter.handleContainerClickEvent();
			} else {
				getView().insert(getWidget(item), pos);
			}
			pos++;
		}

		return newContainer;
	}

	private void showContent(List<AttributeItem> attributes, List<LabelItem> contents) {
		Display view = getView();

		for (AttributeItem item : attributes) {
			view.addAttribute(getWidget(item));
		}

		for (LabelItem item : contents) {
			view.add(getWidget(item));
		}
	}

	/**
	 * Gets the widget for the specified label item (which can be a container,
	 * a simple item or an insertion point).
	 *
	 * @param item the label item
	 * @return
	 */
	private Widget getWidget(LabelItem item) {
		Widget widget;

		if (item instanceof LabelElement) {
			LabelElement element = (LabelElement) item;
			if (element.getType().isComplex()) {
				widget = getContainer((Container) element);
			} else if (element instanceof AttributeItem) {
				widget = getAttributeItem((AttributeItem) element);
			} else {
				widget = getSimpleItem((SimpleItem) element);
			}
		} else {
			widget = getInsertionPoint((InsertionPoint) item);
		}

		return widget;
	}

	private Widget getContainer(Container container) {
		ContainerPresenter presenter = injector.getContainerPresenter();
		presenter.display(container, this, false);
		return presenter.asWidget();
	}

	private Widget getSimpleItem(SimpleItem element) {
		SimpleItemPresenter presenter = injector.getSimpleItemPresenter();
		presenter.display(element, container, this);
		return presenter.asWidget();
	}

	private Widget getAttributeItem(AttributeItem element) {
		AttributeItemPresenter presenter = injector.getAttributeItemPresenter();
		presenter.display(element, container);
		return presenter.asWidget();
	}

	private Widget getInsertionPoint(InsertionPoint insPoint) {
		InsertionPointPresenter presenter = injector.getInsertionPointPresenter();
		presenter.display(insPoint, this, -13, -13);
		return presenter.asWidget();
	}

	private ContainerPresenter getPresenter(Container container) {
		ContainerPresenter presenter = injector.getContainerPresenter();
		presenter.display(container, this, false);
		return presenter;
	}

	private void updateErrors() {
		if (container != null) {
			getView().setErrors(container.getErrorMessages());
		}
	}

	/**
	 * Handles a requet by the user to display the documentation associated
	 * with this element.
	 */
	public void onRequestDocumentation() {
		documentationIsVisible = !documentationIsVisible;
		getView().showDocumentation(documentationIsVisible);
	}

}
