package gov.nasa.arc.pds.lace.client.presenter;

import gov.nasa.arc.pds.lace.client.AppInjector;
import gov.nasa.arc.pds.lace.client.event.CompleteStateChangedEvent;
import gov.nasa.arc.pds.lace.client.event.ContainerChangedEvent;
import gov.nasa.arc.pds.lace.client.event.ElementSelectionEvent;
import gov.nasa.arc.pds.lace.client.event.ElementSelectionEvent.EventDetails;
import gov.nasa.arc.pds.lace.client.event.ElementSelectionEventHandler;
import gov.nasa.arc.pds.lace.client.resources.IconLookup;
import gov.nasa.arc.pds.lace.client.service.LabelContentsServiceAsync;
import gov.nasa.arc.pds.lace.client.util.InsertOptionMap;
import gov.nasa.arc.pds.lace.shared.Container;
import gov.nasa.arc.pds.lace.shared.InsertOption;
import gov.nasa.arc.pds.lace.shared.InsertionPoint;
import gov.nasa.arc.pds.lace.shared.LabelElement;
import gov.nasa.arc.pds.lace.shared.LabelItem;
import gov.nasa.arc.pds.lace.shared.LabelItemType;
import gov.nasa.arc.pds.lace.shared.ResultType;
import gov.nasa.arc.pds.lace.shared.SimpleItem;

import java.util.Iterator;
import java.util.List;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

/**
 * Implements the presenter for a widget that displays a container.
 */
public class ContainerPresenter extends Presenter<ContainerPresenter.Display> {

	/**
	 * Defines an interface that the view must implement.
	 *
	 */
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
		 */
		void remove(int index);
		
		/** 
		 * Adds event listeners that that used for handling
		 * events related to modifying an item (e.g. delete).
		 * 
		 * @param isDeletable flag to indicate whether or not the container is deletable
		 */
		void addEventListeners(boolean isDeletable);
		
		/**
		 * Enables/disables the modification UI.
		 * 
		 * @param enable true to enable the modification UI, false to disable it
		 */
		void enableModification(boolean enable);
		
		/**
		 * Handles the delete action.
		 * 
		 * @param name the name of the element to be deleted
		 * @param popup the popup presenter
		 */
		void handleDeleteAction(String name, PopupPresenter popup);		
	}
	
	// Flag to indicate whether the content is shown or not.
	private boolean isContentShown = false; 
	private Container container;
	private AppInjector injector;
	private EventBus bus;
	private LabelContentsServiceAsync service;
	private HandlerRegistration handler1;
	private HandlerRegistration handler2;
	private ContainerPresenter presenter;
	private IconLookup lookup;
	private PopupPresenter popup;
	private InsertOptionMap insertOptionMap;
	
	// TODO: Use the provider interface instead of the injector to get the objects.
	/**
	 * Creates a new instance of the container presenter. 
	 *
	 * @param view the display interface for the container view
	 * @param bus the event bus to use for firing and receiving events
	 * @param injector
	 * @param service the RPC service
	 * @param popup the popup presenter
	 */
	@Inject
	public ContainerPresenter(
			Display view,
			EventBus bus,
			IconLookup lookup,
			AppInjector injector,
			LabelContentsServiceAsync service,
			PopupPresenter popup,
			InsertOptionMap insertOptionMap
	) {
		super(view);
		this.injector = injector;
		this.bus = bus;
		this.service = service;
		this.presenter = this;
		this.lookup = lookup;
		this.popup = popup;
		this.insertOptionMap = insertOptionMap;
		view.setPresenter(this);
				
		attachHandlers();
	}
	
	/**
	 * Displays the specified container.
	 *
	 * @param container the container object to display
	 * @param contentOnly flag to indicate whether to display
	 * the content or the whole widget
	 */
	public void display(Container container, boolean contentOnly) {
		this.container = container;
		InsertOption alternative = container.getInsertOption();
		if (alternative != null) {
			int id = alternative.getId();
			if (insertOptionMap.get(id) == null) {	
				insertOptionMap.put(id, alternative);
			}	
		}	
		
		Display view = getView();
		view.show(contentOnly);
		
		if (contentOnly) {
			showContent(container.getContents());
		} else {
			LabelItemType type = container.getType();
			view.setContainerTitle(type.getElementName());
			view.setIcon(lookup.getIconClassName(type), container.isComplete());
			view.addEventListeners(container.isDeletable());
		}
	}

	/**
	 * Deregisters the handlers.
	 */
	public void removeHandlers() {
		handler1.removeHandler();
		handler2.removeHandler();
	}
	
	private void attachHandlers() {
		handler1 = bus.addHandler(ElementSelectionEvent.TYPE, new ElementSelectionEventHandler() {
					
			@Override
			public void onEvent(EventDetails data) {
				if (container.getContents().contains(data.getInsertionPoint())
						&& data.getParentPresenter().equals(presenter)) {
					insertElement(data);
						
					if (data.getPopup() != null) {
						data.getPopup().hide();
					}
				}	
			}
			
		});
		
		handler2 = bus.addHandler(CompleteStateChangedEvent.TYPE, new CompleteStateChangedEvent.Handler() {
			
			@Override
			public void onEvent(CompleteStateChangedEvent.EventDetails details) {
				Container targetContainer = details.getContainer();
				if (targetContainer.equals(container)) {					
					setState();
				} else {
					searchContents(targetContainer, container);
				}
			}
		});
	}
	
	private void searchContents(Container targetContainer, Container container) {		
		for (LabelItem item : container.getContents()) {
			if (item.equals(targetContainer)) {
				setState();
				break;
			} 
			if (item instanceof Container) {
				searchContents(targetContainer, (Container) item);
			}	
		}
	}
	
	private void setState() {
		getView().setState(container.isComplete());		
	}
	
	/**
	 * Handles the click event which triggers when
	 * the user clicks the expand/collapse icon.
	 *
	 * @param isExpanded
	 */
	public void handleContainerClickEvent() {
		if (!isContentShown) {
			showContent(container.getContents());
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
	 * Handles element modification.
	 */
	public void modifyElement() {		
		getView().handleDeleteAction(container.getType().getElementName(), popup);
	}
	
	/**
	 * Deletes the container.
	 */
	public void deleteElement() {
		GWT.log("Deleting the container.");
		popup.hide();
	}

	/**
	 * Cancels the delete action.
	 */
	public void cancelDelete() {
		popup.hide();		
	}	
	
	/**
	 * Handles the ElementSelectionEvent event which is fired when an element
	 * is selected from an insertion point within this container. Inserts the
	 * selected element to this container and updates the model object.
	 *
	 * @param data the event details
	 */
	private void insertElement(EventDetails data) {
		
		service.updateContainer(container, data.getInsertionPoint(), data.getAlternativeIndex(),
				data.getType(), new AsyncCallback<ResultType>() {

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("The RPC call failed to update '" +
						container.getType().getElementName() + "' container.");
			}

			@Override			
			public void onSuccess(ResultType result) {
				Container newContainer = updateContents(result);
				// Fire an event for containers to update their complete state.
				bus.fireEvent(new CompleteStateChangedEvent(container, false));
				// Fire an event for the tree view to update itself.				
				bus.fireEvent(new ContainerChangedEvent(container, newContainer));
			}
		});
	}
	
	private Container updateContents(ResultType result) {
		int from = result.getFromIndex();
		int to = result.getToIndex();
		int pos = from;
		List<LabelItem> contents = container.getContents();			
		Display view = getView();		
	
		// Remove objects within 'from' and 'to' indices
		for (int i = from; i <= to; i++) {			
			contents.remove(pos);
			view.remove(pos);			
		}
		
		for (LabelItem item : result.getNewItems()) {
			if (item instanceof LabelElement) {
				InsertOption alternative = ((LabelElement) item).getInsertOption();
				InsertOption existingAlternative = insertOptionMap.get(alternative.getId());
				if (existingAlternative != null) {					
					existingAlternative.setUsedOccurrences(alternative.getUsedOccurrences());
					((LabelElement) item).setInsertOption(existingAlternative);
				}
			} else {
				List<InsertOption> alternatives = ((InsertionPoint) item).getAlternatives();
				for (int i = 0; i < alternatives.size(); i++) {
					InsertOption alternative = alternatives.get(i);
					InsertOption existingAlternative = insertOptionMap.get(alternative.getId());
					if (existingAlternative != null) {						
						existingAlternative.setUsedOccurrences(alternative.getUsedOccurrences());
						alternatives.remove(i);
						alternatives.add(i, existingAlternative);
					}
				}
			}
		}
				
		// Get the new container by adding new items to the contents list starting at position 'from'
		return getNewContainer(result.getNewItems(), contents, pos);
	}
	
	private Container getNewContainer(List<LabelItem> items, List<LabelItem> contents, int pos) {		
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
	
	private void showContent(List<LabelItem> contents) {
		Display view = getView();		
		Iterator<LabelItem> iterator = contents.iterator();		
		while (iterator.hasNext()) {				
			view.add(getWidget(iterator.next()));			
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
		presenter.display(container, false);
		return presenter.asWidget();
	}

	private Widget getSimpleItem(SimpleItem element) {
		SimpleItemPresenter presenter = injector.getSimpleItemPresenter();
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
		presenter.display(container, false);
		return presenter;
	}
}
