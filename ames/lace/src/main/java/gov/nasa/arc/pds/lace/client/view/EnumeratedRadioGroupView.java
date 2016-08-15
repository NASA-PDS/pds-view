package gov.nasa.arc.pds.lace.client.view;

import gov.nasa.arc.pds.lace.client.presenter.EnumeratedRadioGroup;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RadioButton;

/**
 * Implements the view for a set of radio buttons.
 */
public class EnumeratedRadioGroupView extends Composite implements EnumeratedRadioGroup.Display {

	interface MyUIBinder extends UiBinder<IsWidget, EnumeratedRadioGroupView> { /*empty*/ }
	private static MyUIBinder uiBinder = GWT.create(MyUIBinder.class);

	@UiField
	HTMLPanel panel;

	private List<RadioButton> buttons = new ArrayList<RadioButton>();

	private EnumeratedRadioGroup presenter;

	/**
	 * Creates a new instance of the view.
	 */
	@Inject
	public EnumeratedRadioGroupView() {
		initWidget(uiBinder.createAndBindUi(this).asWidget());
	}

	@Override
	public void setPresenter(EnumeratedRadioGroup presenter) {
		this.presenter = presenter;
	}

	@Override
	public void clear() {
		panel.clear();
		buttons.clear();
	}

	@Override
	public void addItem(String groupName, String text, String value) {
		RadioButton button = new RadioButton(groupName, text);
		button.setFormValue(value);
		buttons.add(button);
		panel.add(button);

		// Set the default value if it's the first button.
		if (buttons.size() == 1) {
			button.setValue(true);
		}

		button.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				presenter.handleValueChanged();
			}
		});
	}

	@Override
	public String getSelectedValue() {
		for (RadioButton button : buttons) {
			if (button.getValue()) {
				return button.getFormValue();
			}
		}

		return null;
	}

	@Override
	public void setSelectedValue(String value) {
		boolean foundValue = false;
		for (RadioButton button : buttons) {
			foundValue |= value.equals(button.getFormValue());
			button.setValue(value.equals(button.getFormValue()));
		}

		// Ensure that some radio button is selected.
		if (!foundValue && buttons.size() > 0) {
			buttons.get(0).setValue(true);
		}
	}

	@Override
	public void setEnabled(boolean flag) {
		for (RadioButton button : buttons) {
			button.setEnabled(flag);
		}
	}

}
