package gov.nasa.arc.pds.lace.client.presenter;

import gov.nasa.arc.pds.lace.client.service.LabelContentsServiceAsync;
import gov.nasa.arc.pds.lace.shared.Container;
import gov.nasa.arc.pds.lace.shared.LabelItemType;
import gov.nasa.arc.pds.lace.shared.SimpleItem;

import java.util.List;

import com.google.gwt.core.shared.GWT;
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

	}

	private String curValue;
	private Container container;
	private SimpleItem item;
	private LabelItemType type;
	private LabelContentsServiceAsync service;

	/**
	 * Creates an instance of the simple item presenter.
	 *
	 * @param view the view to use for user interaction and display
	 * @param service the label RPC service
	 */
	@Inject
	public SimpleItemPresenter(Display view, LabelContentsServiceAsync service) {
		super(view);
		view.setPresenter(this);
		this.service = service;
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
		Display view = getView();
		view.setLabel(type.getElementName(), type.getMinOccurrences() == 1);
		view.setValue(theItem.getValue());

		List<String> values = this.type.getValidValues();
		if (values != null) {
			view.setValidValues(values.toArray(new String[values.size()]));
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
		service.saveSimplelItem(container, item, value, new AsyncCallback<Container>() {

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("The RPC call failed to save simple item '" + type.getElementName() + "' .");
			}

			@Override
			public void onSuccess(Container updatedContainer) {
				GWT.log("Simple item '" + type.getElementName() + "' is saved successfully.");
				curValue = value;
				item.setValue(value);
				container = updatedContainer;
				// Fire an event for the tree view to update itself.
				// bus.fireEvent(new ContainerChangedEvent(container));
			}
		});
	}

	private boolean isInputValid(String value) {
		boolean valid = true;
		if (value == null || value.trim().length() == 0) {
			valid = false;
		}
		if (value.equals(curValue)) {
			valid = false;
		}
		return valid;
	}
}
