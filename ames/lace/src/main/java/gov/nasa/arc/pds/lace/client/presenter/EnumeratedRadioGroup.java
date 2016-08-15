package gov.nasa.arc.pds.lace.client.presenter;

import gov.nasa.arc.pds.lace.client.event.RadioGroupChangeEvent;
import gov.nasa.arc.pds.lace.client.view.EnumeratedRadioGroupView;

import java.util.Map;

import javax.inject.Inject;

import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.inject.ImplementedBy;

/**
 * Implements a widget that allows the user to select among radio
 * buttons whose values are enumeration constants.
 */
public class EnumeratedRadioGroup extends Presenter<EnumeratedRadioGroup.Display> implements HasValueChangeHandlers<String> {

	/**
	 * Defines the interface the view must implement.
	 */
	@ImplementedBy(EnumeratedRadioGroupView.class)
	public interface Display extends Presenter.Display<EnumeratedRadioGroup> {

		/**
		 * Removes all radio buttons.
		 */
		void clear();

		/**
		 * Adds a new radio button with a specified radio button group name,
		 * value, and textual label.
		 *
		 * @param groupName the group name
		 * @param text the textual label
		 * @param value the radio button value
		 */
		void addItem(String groupName, String text, String value);

		/**
		 * Gets the value of the currently selected radio button.
		 *
		 * @return the value of the selected radio button, or null if no button is selected
		 */
		String getSelectedValue();

		/**
		 * Sets the radio button to be selected.
		 *
		 * @param value the value of the radio button to be selected
		 */
		void setSelectedValue(String value);

		/**
		 * Enables or disables the radio field group.
		 *
		 * @param flag true, if the group should be enabled.
		 */
		void setEnabled(boolean flag);

	}

	/** A count of the number of instances, for creating a unique group name. */
	private static int buttonCount = 0;

	private HandlerManager handlerManager = new HandlerManager(this);

	private String groupName;

	/**
	 * Creates a new instance.
	 *
	 * @param view the view to use for user interaction and display
	 */
	@Inject
	public EnumeratedRadioGroup(Display view) {
		super(view);
		view.setPresenter(this);

		groupName = "enum-radio-group-" + (++buttonCount);
	}

	@Override
	public void fireEvent(GwtEvent<?> event) {
		// ignore - events are fired by view actions.
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {
		return handlerManager.addHandler(RadioGroupChangeEvent.getType(), handler);
	}

	/**
	 * Sets the values to display in the list box.
	 *
	 * @param <T> the enumeration type
	 * @param values an array of enumerated values
	 * @param textValues a map from the string representation of the enumeration constants to the item text
	 */
	public <T extends Enum<T>> void setValues(
			T[] values,
			Map<String, String> textValues
	) {
		getView().clear();
		for (T value : values) {
			getView().addItem(groupName, textValues.get(value.toString()), value.toString());
		}

		// Ensure that a value is selected.
		if (values.length > 0) {
			setSelectedValue(values[0]);
		}
	}

	/**
	 * Sets the values to display in the list box.
	 *
	 * @param values an array of string values
	 * @param textValues a map from the string representation of the string values to the item text
	 */
	public void setStringValues(
			String[] values,
			Map<String, String> textValues
	) {
		getView().clear();
		for (String value : values) {
			getView().addItem(groupName, textValues.get(value.toString()), value.toString());
		}

		// Ensure that a value is selected.
		if (values.length > 0) {
			setStringValue(values[0]);
		}
	}

	/**
	 * Gets the selected value as an enumeration constant.
	 *
	 * @param <T> the enumeration type
	 * @param clazz the class of the enumeration type
	 * @return the enumeration constant corresponding to the selected item
	 */
	public <T  extends Enum<T>> T getSelectedValue(Class<T> clazz) {
		String stringValue = getView().getSelectedValue();
		return Enum.valueOf(clazz, stringValue);
	}

	/**
	 * Gets the value of the currently selected radio button,
	 * as a string.
	 *
	 * @return the value of the selected radio button, or null if
	 *   no button is selected
	 */
	public String getStringValue() {
		return getView().getSelectedValue();
	}

	/**
	 * Sets the radio button to be selected.
	 *
	 * @param <T> the enumeration constant type
	 * @param value the value of the radio button to be selected, as an
	 *   enumeration constant
	 */
	public <T extends Enum<T>> void setSelectedValue(T value) {
		getView().setSelectedValue(value.toString());
	}

	/**
	 * Handles the event when the user selects a new radio button.
	 * Fires an event to all change handlers.
	 */
	public void handleValueChanged() {
		handlerManager.fireEvent(new RadioGroupChangeEvent(groupName));
	}

	/**
	 * Enables or disables the radio field group.
	 *
	 * @param flag true, if the group should be enabled.
	 */
	public void setEnabled(boolean flag) {
		getView().setEnabled(flag);
	}

	/**
	 * Sets the value as a string.
	 *
	 * @param valueStr the new value, as a string
	 */
	public void setStringValue(String valueStr) {
		getView().setSelectedValue(valueStr);
	}

}
