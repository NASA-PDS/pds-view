package gov.nasa.arc.pds.lace.client.presenter;

import gov.nasa.arc.pds.lace.client.AppController;
import gov.nasa.arc.pds.lace.client.event.EditorContentChangingEvent;
import gov.nasa.arc.pds.lace.client.event.LabelChangedEvent;
import gov.nasa.arc.pds.lace.client.event.SystemFailureEvent;
import gov.nasa.arc.pds.lace.client.event.ValidationEvent;
import gov.nasa.arc.pds.lace.client.service.LabelContentsServiceAsync;
import gov.nasa.arc.pds.lace.client.util.ReferenceHelper;
import gov.nasa.arc.pds.lace.client.view.AttributeItemView;
import gov.nasa.arc.pds.lace.shared.AttributeItem;
import gov.nasa.arc.pds.lace.shared.Container;
import gov.nasa.arc.pds.lace.shared.LabelItem;
import gov.nasa.arc.pds.lace.shared.LabelItemType;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.ImplementedBy;

/**
 * Implements the presenter for a widget that displays an attribute.
 */
public class AttributeItemPresenter extends Presenter<AttributeItemPresenter.Display> {

	/**
	 * Defines an interface that the view must implement.
	 */
	@ImplementedBy(AttributeItemView.class)
	public interface Display extends  Presenter.Display<AttributeItemPresenter> {

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
		 * Updates the errors displayed when the user hovers over the
		 * complete icon.
		 *
		 * @param errors an array of error messages
		 */
		void setErrors(String[] errors);

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
	private LabelItem parentItem;
	private AttributeItem item;
	private LabelItemType type;
	private LabelContentsServiceAsync service;
	private boolean documentationIsVisible = false;
	private Provider<AppController> controllerProvider;

	/**
	 * Creates an instance of the simple item presenter.
	 *
	 * @param view the view to use for user interaction and display
	 * @param service the label RPC service
	 * @param controllerProvider a provider for the application controller
	 */
	@Inject
	public AttributeItemPresenter(
			Display view,
			LabelContentsServiceAsync service,
			Provider<AppController> controllerProvider
	) {
		super(view);
		view.setPresenter(this);
		this.service = service;
		this.controllerProvider = controllerProvider;
	}

	@Override
	protected void addEventHandlers() {
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
	 * @param parentItem the container that this item is a child of
	 */
	public void display(AttributeItem theItem, LabelItem parentItem) {
		this.parentItem = parentItem;
		this.item = theItem;
		this.type = theItem.getType();
		this.curValue = theItem.getValue();

		Display view = getView();
		view.setLabel(type.getElementName(), theItem.isRequired());
		view.setValue(curValue);
		view.setState(theItem.isComplete());
		updateErrors();

		List<String> values = this.type.getValidValues();
		if (values!=null && values.size() > 0) {
			view.setEnumeration(true);
		} else if (ReferenceHelper.isReference(null, item)) {
			view.setEnumeration(true);
		}

		if (type.getDefaultValue()!=null && !type.getDefaultValue().isEmpty()) {
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

	/**
	 * Updates the model.
	 *
	 * @param value the new value to save
	 */
	private void updateModel(final String value) {
		service.saveAttribute(parentItem, item, value, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Unable to save the attribute value: " + caught.getMessage());
				fireEvent(new SystemFailureEvent("Server communication error. Unable to save the attribute value for '" + type.getElementName() + "'."));
			}

			@Override
			public void onSuccess(Void data) {
				GWT.log("Attribute '" + type.getElementName() + "' is saved successfully.");
				curValue = value;
				item.setValue(value);
				fireEvent(new LabelChangedEvent());
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
		if (type.getValidValues() != null) {
			return type.getValidValues();
		} else if (ReferenceHelper.isReference(null, item)) {
			Container root = controllerProvider.get().getRootContainer();
			return ReferenceHelper.getReferenceValues(item, root);
		} else {
			return Collections.emptyList();
		}
	}

}
