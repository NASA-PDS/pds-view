package gov.nasa.arc.pds.lace.client.presenter;

import gov.nasa.arc.pds.lace.client.AppInjector;
import gov.nasa.arc.pds.lace.client.event.ContainerChangedEvent;
import gov.nasa.arc.pds.lace.client.event.ElementSelectionEvent;
import gov.nasa.arc.pds.lace.client.event.ElementSelectionEvent.EventDetails;
import gov.nasa.arc.pds.lace.client.event.ElementSelectionEventHandler;
import gov.nasa.arc.pds.lace.client.resources.IconLookup;
import gov.nasa.arc.pds.lace.client.service.LabelContentsServiceAsync;
import gov.nasa.arc.pds.lace.shared.Container;
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
		 * @param isComplete flag to indicate whether the container is complete  
		 */
		void setIcon(String className, boolean isComplete);

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
	}
	
	// Flag to indicate whether the content is shown or not.
	private boolean isContentShown = false; 
	private Container container;
	private AppInjector injector;
	private EventBus bus;
	private LabelContentsServiceAsync service;
	private HandlerRegistration handler;
	private ContainerPresenter presenter;
	private IconLookup lookup;
	
	// TODO: Use the provider interface instead of the injector to get the objects.
	/**
	 * Creates a new instance of the container presenter. 
	 *
	 * @param view the display interface for the container view
	 * @param bus the event bus to use for firing and receiving events
	 * @param injector
	 * @param service the RPC service
	 */
	@Inject
	public ContainerPresenter(
			Display view,
			EventBus bus,
			IconLookup lookup,
			AppInjector injector,
			LabelContentsServiceAsync service
	) {
		super(view);
		this.injector = injector;
		this.bus = bus;
		this.service = service;
		this.presenter = this;
		this.lookup = lookup;
		view.setPresenter(this);
				
		attachHandler();
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
		Display view = getView();
		view.show(contentOnly);
		
		if (contentOnly) {
			showContent(container.getContents());
		} else {
			LabelItemType type = container.getType();
			view.setContainerTitle(type.getElementName());
			view.setIcon(lookup.getIconClassName(type), container.isComplete());
		}
	}

	public void removeHandler() {
		handler.removeHandler();
	}
	
	private void attachHandler() {
		handler = bus.addHandler(ElementSelectionEvent.TYPE, new ElementSelectionEventHandler() {
					
			@Override
			public void onEvent(EventDetails data) {
				if (container.getContents().contains(data.getInsertionPoint()) &&
						data.getParentPresenter().equals(presenter)) {
					insertElement(data);
						
					if (data.getPopup() != null) {
						data.getPopup().hide();
					}
				}	
			}
			
		});
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
	 * Handles the ElementSelectionEvent event which is fired when an element
	 * is selected from an insertion point within this container. Inserts the
	 * selected element to this container and updates the model object.
	 *
	 * @param data the event details
	 */
	private void insertElement(EventDetails data) {
		
		service.updateContainer(container, data.getInsertionPoint(),
				data.getIndex(), new AsyncCallback<ResultType>() {

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("The RPC call failed to update '" +
						container.getType().getElementName() + "' container.");
			}

			@Override			
			public void onSuccess(ResultType result) {							
				int from = result.getFromIndex();
				int to = result.getToIndex();
				Container newContainer = updateContents(result.getContents(), from, to);
				
				if (newContainer != null) {
					// Fire an event for the tree view to update itself.				
					bus.fireEvent(new ContainerChangedEvent(container, newContainer));
				}	
			}
		});
	}
	
	private Container updateContents(List<LabelItem> newItems, int from, int to) {	
		List<LabelItem> contents = container.getContents();	
		Container newContainer = null;
		Display view = getView();
		int pos = from;
	
		// Remove objects within 'from' and 'to' indices
		for (int i = from; i <= to; i++) {			
			contents.remove(pos);
			view.remove(pos);			
		}
			
		// Add new items to the contents list starting at position 'from'
		for (LabelItem item : newItems) {
			contents.add(pos, item);			
			
			if (item instanceof Container) {
				newContainer = (Container) item;
				ContainerPresenter presenter = getPresenter(newContainer);				
				view.insert(presenter.asWidget(), pos);				
				presenter.handleContainerClickEvent();
			} else {				
				view.insert(getWidget(item), pos);
			}			
			pos++;
		}		
		
		return newContainer; 
	}
	
	private void showContent(List<LabelItem> contents) {									
		Iterator<LabelItem> iterator = contents.iterator();			
		while (iterator.hasNext()) {				
			getView().add(getWidget(iterator.next()));			
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
	
	private ContainerPresenter getPresenter(Container container) {
		ContainerPresenter presenter = injector.getContainerPresenter();
		presenter.display(container, false);
		return presenter;
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
}
