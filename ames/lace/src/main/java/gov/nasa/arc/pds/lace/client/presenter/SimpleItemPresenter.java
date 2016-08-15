package gov.nasa.arc.pds.lace.client.presenter;

import gov.nasa.arc.pds.lace.client.AppController;
import gov.nasa.arc.pds.lace.client.event.EditorContentChangingEvent;
import gov.nasa.arc.pds.lace.client.event.ElementDeletionEvent;
import gov.nasa.arc.pds.lace.client.event.ElementInsertedEvent;
import gov.nasa.arc.pds.lace.client.event.ElementInsertedEvent.EventDetails;
import gov.nasa.arc.pds.lace.client.event.FieldValueChangedEvent;
import gov.nasa.arc.pds.lace.client.event.LabelChangedEvent;
import gov.nasa.arc.pds.lace.client.event.SystemFailureEvent;
import gov.nasa.arc.pds.lace.client.event.ValidationEvent;
import gov.nasa.arc.pds.lace.client.service.LabelContentsServiceAsync;
import gov.nasa.arc.pds.lace.client.util.ClipboardManager;
import gov.nasa.arc.pds.lace.client.util.InsertOptionMap;
import gov.nasa.arc.pds.lace.client.util.ReferenceHelper;
import gov.nasa.arc.pds.lace.client.view.SimpleItemView;
import gov.nasa.arc.pds.lace.shared.AttributeItem;
import gov.nasa.arc.pds.lace.shared.Container;
import gov.nasa.arc.pds.lace.shared.InsertOption;
import gov.nasa.arc.pds.lace.shared.LabelItemType;
import gov.nasa.arc.pds.lace.shared.SimpleItem;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.ImplementedBy;

/**
 * Implements the presenter for a widget that displays a simple item.
 */
public class SimpleItemPresenter extends Presenter<SimpleItemPresenter.Display> {

	/**
	 * Defines an interface that the view must implement.
	 */
	@ImplementedBy(SimpleItemView.class)
	public interface Display extends  Presenter.Display<SimpleItemPresenter> {

		/**
		 * Sets the label to the given text.
		 *
		 * @param text the simple element name
		 * @param required true, if the element is required in the result document
		 */
		void setLabel(String text, boolean required);

		/**
		 * Gets the value of the item.
		 *
		 * @return the current item value
		 */
		String getValue();

		/**
		 * Sets the value of the text box.
		 *
		 * @param value the simple item value
		 */
		void setValue(String value);

		/**
		 * Sets whether the field has enumerated values.
		 *
		 * @param flag true, if the field has enumerated values.
		 */
		public void setEnumeration(boolean flag);

		/**
		 * Sets the default value for the field.
		 *
		 * @param defaultValue the default value
		 */
		void setDefaultValue(String defaultValue);

		/**
		 * Sets whether the simple item should be displayed as complete.
		 *
		 * @param complete true, if the simple item is complete
		 */
		void setState(boolean complete);

		/**
		 * Adds an attribute for the simple item.
		 *
		 * @param attrPresenter the component used to display the attribute
		 */
		void addAttribute(IsWidget attrPresenter);

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
		 * Updates the errors displayed when the user hovers over the
		 * complete icon.
		 *
		 * @param errors an array of error messages
		 */
		void setErrors(String[] errors);

		/**
		 * Sets whether the item should be displayed as a text area.
		 *
		 * @param multiline true if multi-line, false if one-line.
		 */
		void setMultiline(boolean multiline);

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

	private String curValue;
	private Container container;
	private SimpleItem item;
	private LabelItemType type;
	private LabelContentsServiceAsync service;
	private InsertOptionMap insertOptionMap;
	private ContainerPresenter parentPresenter;
	private Provider<PopupPresenter> popupProvider;
	private Provider<AttributeItemPresenter> attributeProvider;
	private Provider<AppController> controllerProvider;
	private boolean documentationIsVisible = false;
	private ClipboardManager clipboard;

	/**
	 * Creates an instance of the simple item presenter.
	 *
	 * @param view the view to use for user interaction and display
	 * @param service the label RPC service
	 * @param insertOptionMap the map determining what insertion options there are
	 * @param popupProvider the prover for the popup component
	 * @param attributeProvider a provider for attribute components associated with this item
	 * @param controllerProvider a provider for the application controller
	 * @param clipboard the clipboard manager
	 */
	@Inject
	public SimpleItemPresenter(
			Display view,
			LabelContentsServiceAsync service,
			InsertOptionMap insertOptionMap,
			Provider<PopupPresenter> popupProvider,
			Provider<AttributeItemPresenter> attributeProvider,
			Provider<AppController> controllerProvider,
			ClipboardManager clipboard
	) {
		super(view);
		view.setPresenter(this);
		this.service = service;
		this.popupProvider = popupProvider;
		this.insertOptionMap = insertOptionMap;
		this.attributeProvider = attributeProvider;
		this.controllerProvider = controllerProvider;
		this.clipboard = clipboard;
	}

	@Override
	protected void addEventHandlers() {
		addEventHandler(ElementInsertedEvent.TYPE, new ElementInsertedEvent.Handler() {

			@Override
			public void onEvent(EventDetails data) {
				if (data.isSimpleItem()) {
					Display view = getView();
					boolean deletable = item.isDeletable();
					if (deletable && !view.isDeleteEventsAttached()) {
						view.addDeleteEventListeners();
					} else if (!deletable && view.isDeleteEventsAttached()) {
						view.removeDeleteEventListeners();
					}
				}
			}

		});

		addEventHandler(ValidationEvent.TYPE, new ValidationEvent.Handler() {
			@Override
			public void onEvent(Void data) {
				updateErrors();
				getView().setState(item.isComplete());
			}
		});

		addEventHandler(EditorContentChangingEvent.TYPE, new EditorContentChangingEvent.Handler() {
			@Override
			public void onEvent(Void data) {
				saveValue(getView().getValue());
			}
		});
	}

	/**
	 * Displays a simple item.
	 *
	 * @param theItem a simple item object that represents a simple element
	 * @param theContainer the container that this item is a child of
	 */
	public void display(SimpleItem theItem, Container theContainer, ContainerPresenter parentPresenter) {
		this.parentPresenter = parentPresenter;
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
		view.setMultiline(item.isMultiline());
		view.setLabel(type.getElementName(), theItem.isRequired());
		view.setValue(curValue);
		view.setState(theItem.isComplete());
		updateErrors();
		if (item.isDeletable()) {
			view.addDeleteEventListeners();
		}

		for (AttributeItem attribute : theItem.getAttributes()) {
			AttributeItemPresenter attrPresenter = attributeProvider.get();
			attrPresenter.display(attribute, theItem);
			view.addAttribute(attrPresenter);
		}

		List<String> values = this.type.getValidValues();
		if (values!=null && values.size() > 0) {
			view.setEnumeration(true);
		} else if (ReferenceHelper.isReference(container, item)) {
			view.setEnumeration(true);
		}

		if (type.getDefaultValue()!=null && type.getDefaultValue().isEmpty()) {
			view.setDefaultValue(type.getDefaultValue());
		}

		if (type.getDocumentation() != null) {
			getView().setDocumentation(type.getDocumentation());
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

	public void onValueChange(String newValue) {
		item.setValue(newValue);
		// But don't update curValue, so that we'll still send the
		// new value upon blur or another event.
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
		clipboard.saveItem(item);
		fireEvent(new ElementDeletionEvent(item, parentPresenter, null));
	}

	/**
	 * Handles a user request to copy the element.
	 */
	public void onCopy() {
		clipboard.saveItem(item);
	}

	/**
	 * Handles the user's request to delete an element.
	 */
	public void onDelete() {
		getView().enableModification(false);

		final PopupPresenter popup = popupProvider.get();

		String msg = "This action will permanently delete the '" + item.getType().getElementName()
				+ "' element. Do you STILL want to delete the element?";

		ClickHandler yesBtnHandler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				event.stopPropagation();
				// Fire an event to the parent container to delete the item
				fireEvent(new ElementDeletionEvent(item, parentPresenter, popup));
			}
		};

		popup.showConfirmationBox("Warning", msg, yesBtnHandler, null);
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
				GWT.log("Unable to save the element value: " + caught.getMessage());
				fireEvent(new SystemFailureEvent("Server communication error. Unable to save the value for '" + type.getElementName() + "' element."));
			}

			@Override
			public void onSuccess(Container updatedContainer) {
				GWT.log("Simple item '" + type.getElementName() + "' is saved successfully.");
				curValue = value;
				item.setValue(value);
				fireEvent(new LabelChangedEvent());

				// Fire an event to registered handlers to update their name, if necessary.
				fireEvent(new FieldValueChangedEvent(container, value, parentPresenter));
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

	private void updateErrors() {
		if (item != null) {
			getView().setErrors(item.getErrorMessages());
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

	/**
	 * Gets the enumeration of valid values for the field.
	 *
	 * @return a collection of valid values
	 */
	public Collection<String> getSuggestions() {
		if (type.getValidValues()!=null && type.getValidValues().size() > 0) {
			return type.getValidValues();
		} else if (ReferenceHelper.isReference(container, item)) {
			Container root = controllerProvider.get().getRootContainer();
			return ReferenceHelper.getReferenceValues(item, root);
		} else {
			return Collections.emptyList();
		}
	}

}
