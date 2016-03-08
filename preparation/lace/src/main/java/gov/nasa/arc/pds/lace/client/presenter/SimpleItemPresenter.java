package gov.nasa.arc.pds.lace.client.presenter;

import gov.nasa.arc.pds.lace.client.event.CompleteStateChangedEvent;
import gov.nasa.arc.pds.lace.client.event.ElementInsertedEvent;
import gov.nasa.arc.pds.lace.client.service.LabelContentsServiceAsync;
import gov.nasa.arc.pds.lace.client.util.InsertOptionMap;
import gov.nasa.arc.pds.lace.shared.Container;
import gov.nasa.arc.pds.lace.shared.InsertOption;
import gov.nasa.arc.pds.lace.shared.LabelItemType;
import gov.nasa.arc.pds.lace.shared.SimpleItem;

import java.util.List;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

/**
 * Implements the presenter for a widget that displays a simple item.
 */
public class SimpleItemPresenter extends Presenter<SimpleItemPresenter.Display> {

	/**
	 * Defines an interface that the view must implement.
	 */
	public interface Display extends  Presenter.Display<SimpleItemPresenter> {

		/**
		 * Sets the label to the given text.
		 *
		 * @param text the simple element name
		 * @param required true, if the element is required in the result document
		 */
		void setLabel(String text, boolean required);

		/**
		 * Sets the value of the text box.
		 *
		 * @param value the simple item value
		 */
		void setValue(String value);

		/**
		 * Sets the set of valid values for the field.
		 *
		 * @param values an array of valid values
		 */
		void setValidValues(String[] values);

		/**
		 * Sets whether the simple item should be displayed as complete.
		 *  
		 * @param complete true, if the simple item is complete
		 */
		void setState(boolean complete);
		
		/** 
		 * Adds event listeners that are used for handling
		 * events related to modifying an item (e.g. delete).
		 */
		void addDeleteEventListeners();
		
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
		
		/**
		 * Tests whether the delete events are already attached.
		 * 
		 * @return true, if delete events are attached
		 */
		boolean isDeleteEventsAttached();
	}

	private String curValue;
	private Container container;
	private SimpleItem item;
	private LabelItemType type;
	private EventBus bus;
	private LabelContentsServiceAsync service;
	private PopupPresenter popup;
	private InsertOptionMap insertOptionMap;	

	/**
	 * Creates an instance of the simple item presenter.
	 *
	 * @param view the view to use for user interaction and display
	 * @param bus the event bus to use for firing and receiving events
	 * @param service the label RPC service
	 * @param popup the popup presenter
	 */
	@Inject
	public SimpleItemPresenter(
			Display view,
			EventBus bus,
			LabelContentsServiceAsync service,
			PopupPresenter popup,
			InsertOptionMap insertOptionMap
	) {
		super(view);
		view.setPresenter(this);
		this.bus = bus;
		this.service = service;
		this.popup = popup;
		this.insertOptionMap = insertOptionMap;
		
		attachHandlers();
	}

	private void attachHandlers() {
		bus.addHandler(ElementInsertedEvent.TYPE, new ElementInsertedEvent.Handler() {

			@Override
			public void onEvent(gov.nasa.arc.pds.lace.client.event.ElementInsertedEvent.EventDetails data) {
				if (data.isSimpleItem()) {
					Display view = getView();
					boolean deletable = item.isDeletable();
					if (deletable && !view.isDeleteEventsAttached()) {
						view.addDeleteEventListeners();
					}
				}	
			}
			
		});
	}
	
	/**
	 * Displays a simple item.
	 *
	 * @param theItem a simple item object that represents a simple element
	 * @param theContainer the container that this item is a child of
	 */
	public void display(SimpleItem theItem, Container theContainer) {
		this.container = theContainer;
		this.item = theItem;
		this.type = theItem.getType();
		this.curValue = theItem.getValue();
		
		InsertOption alternative = item.getInsertOption();		
		if (alternative != null) {
			int id = alternative.getId();
			if (insertOptionMap.get(id) == null) {
				insertOptionMap.put(id, alternative);
			}
		}	
		
		Display view = getView();
		view.setLabel(type.getElementName(), theItem.isRequired());
		view.setValue(curValue);
		view.setState(theItem.isComplete());
		
		List<String> values = this.type.getValidValues();
		if (values != null) {
			view.setValidValues(values.toArray(new String[values.size()]));
		}
		
		// Add event listeners if the item is deletable.
		if (item.isDeletable()) {
			view.addDeleteEventListeners();
		}
	}

	/**
	 * Saves the simple item value.
	 *
	 * @param value the new value to save
	 */
	public void saveValue(String value) {
		if (isInputValid(value)) {
			updateModel(value);
		}
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
		getView().handleDeleteAction(item.getType().getElementName(), popup);
	}
	
	/**
	 * Deletes the item.
	 */
	public void deleteElement() {
		GWT.log("Deleting the item.");
		popup.hide();
	}

	/**
	 * Cancels the delete action.
	 */
	public void cancelDelete() {
		popup.hide();		
	}	
	
	/**
	 * Updates the model.
	 *
	 * @param value the new value to save
	 */
	private void updateModel(final String value) {
		service.saveSimplelItem(container, item, value, new AsyncCallback<Container>() {

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("The RPC call failed to save simple item '" + type.getElementName() + "' .");
			}

			@Override
			public void onSuccess(Container updatedContainer) {
				GWT.log("Simple item '" + type.getElementName() + "' is saved successfully.");
				curValue = value;
				int index = container.getContents().indexOf(item);
				// TODO: probably need to only copy the value and complete state from the savedItem to the item object on the client. 
				item = (SimpleItem) updatedContainer.getContents().get(index);
				container.removeItem(index);
				container.addItem(index, item);
				getView().setState(item.isComplete());
															
				// Fire an event to registered handlers to update their complete state.
				// TODO: fire the event only if state is changed?
				bus.fireEvent(new CompleteStateChangedEvent(container, true));
			}
		});
	}

	private boolean isInputValid(String value) {
		if (curValue == null && (value == null || value.trim().isEmpty())) {
			return false;
		}
		if (value.equals(curValue)) {
			return false;
		}
		return true;
	}
}
