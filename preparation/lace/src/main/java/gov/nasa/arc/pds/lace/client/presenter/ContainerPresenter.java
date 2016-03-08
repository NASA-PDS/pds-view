package gov.nasa.arc.pds.lace.client.presenter;

import gov.nasa.arc.pds.lace.client.AppInjector;
import gov.nasa.arc.pds.lace.client.event.ContainerChangedEvent;
import gov.nasa.arc.pds.lace.client.event.ElementSelectionEvent;
import gov.nasa.arc.pds.lace.client.event.ElementSelectionEventHandler;
import gov.nasa.arc.pds.lace.client.event.ElementSelectionEvent.EventDetails;
import gov.nasa.arc.pds.lace.client.resources.Resources;
import gov.nasa.arc.pds.lace.client.service.LabelContentsServiceAsync;
import gov.nasa.arc.pds.lace.shared.Container;
import gov.nasa.arc.pds.lace.shared.InsertionPoint;
import gov.nasa.arc.pds.lace.shared.LabelElement;
import gov.nasa.arc.pds.lace.shared.LabelItem;
import gov.nasa.arc.pds.lace.shared.SimpleItem;

import java.util.Iterator;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ImageResource;
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
		 * Sets the container type icon.
		 *
		 * @param image
		 */
		void setTypeIcon(ImageResource image);

		/**
		 * Gets the container type icon.
		 *
		 * @return
		 */
		ImageElement getTypeIcon();

		/**
		 * Adds the given widget to the content of the container.
		 *
		 * @param widget the widget to add to the container's content
		 */
		void addContent(Widget widget);

		/**
		 * Clears the content of the container.
		 */
		void clearContent();
		
		/**
		 * 
		 * @return
		 */
		Widget getContent();
	}

	// Flag indicating whether the content is shown or not.
	private boolean isContentShown = false;
	private Container container;
	private AppInjector injector;
	private EventBus bus;
	private LabelContentsServiceAsync service;
	
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
			final EventBus bus,
			AppInjector injector,
			LabelContentsServiceAsync service
	) {
		super(view);
		this.injector = injector;
		this.bus = bus;
		this.service = service;
		view.setPresenter(this);

		bus.addHandler(ElementSelectionEvent.TYPE, new ElementSelectionEventHandler() {
			
			@Override
			public void onEvent(EventDetails data) {
				if (container.getContents().contains(data.getInsertionPoint())) {
					GWT.log("insPoint matches contents");					
					insertElement(data);
					if (data.getPopup() != null) {
						data.getPopup().hide();
					}
				}
				if (container.equals(data.getParentContainer())) {
					GWT.log("container is the same as the parent container.");
				}	
			}
			
		});
	}

	/**
	 * Displays the specified container.
	 *
	 * @param container the container object to display
	 */
	public void display(Container container) {
		this.container = container;
		Display view = getView();
		view.setContainerTitle(container.getType().getElementName());
		view.setTypeIcon(Resources.INSTANCE.getLargeCubeIcon());
	}

	/**
	 * Handles the click event which triggers when
	 * the user clicks the expand/collapse icon.
	 *
	 * @param isExpanded
	 */
	public void handleContainerClickEvent(boolean isExpanded) {
		// TODO: Use isExpanded flag
		if (!isContentShown) {
			updateContent();
			isContentShown = true;
		}
		getView().toggleContainer();
	}
	
	/**
	 * 
	 * @param container
	 * @return
	 */
	public Widget getContent(Container container) {
		this.container = container;
		updateContent();
		return getView().getContent();			
	}
	
	private void updateContent() {
		Iterator<LabelItem> iterator = container.getContents().iterator();
		while (iterator.hasNext()) {
			Object item = iterator.next();
			if (item instanceof LabelElement) {
				// The label item is an element which can be
				// either a container or a simple item.
				LabelElement element = (LabelElement) item;
				if (element.getType().isComplex()) {
					showContainer((Container) element);
				} else {
					showSimpleItem((SimpleItem) element);
				}
			} else {
				// The label item is an insertion point.
				InsertionPoint insPoint = (InsertionPoint) item;
				showInsertionPoint(insPoint);
			}
		}
	}

	private void showContainer(Container container) {
		ContainerPresenter presenter = injector.getContainerPresenter();
		presenter.display(container);
		getView().addContent(presenter.asWidget());
	}

	private void showSimpleItem(SimpleItem element) {
		SimpleItemPresenter presenter = injector.getSimpleItemPresenter();
		presenter.display(element, container);
		getView().addContent(presenter.asWidget());
	}

	private void showInsertionPoint(InsertionPoint insPoint) {
		InsertionPointPresenter presenter = injector.getInsertionPointPresenter();
		presenter.display(insPoint, container, -13, -13);
		getView().addContent(presenter.asWidget());
	}

	/**
	 * Handles the ElementSelectionEvent event which is fired when an
	 * element is selected from an insertion point. Inserts the selected
	 * element to this container and updates the model object.
	 *
	 * @param data the event details
	 */
	private void insertElement(EventDetails data) {		
		service.updateContainer(container, data.getInsertionPoint(),
				data.getIndex(), new AsyncCallback<Container>() {

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("The RPC call failed to update the container.");
			}

			@Override
			public void onSuccess(Container result) {
				container.setContents(result.getContents());
				getView().clearContent();
				updateContent();				
				// Fire an event for the tree view to update itself.
				bus.fireEvent(new ContainerChangedEvent(container, false));
			}

		});
	}
}
