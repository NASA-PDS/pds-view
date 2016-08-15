package gov.nasa.arc.pds.lace.client.view;

import gov.nasa.arc.pds.lace.client.presenter.EnumeratedRadioGroup;
import gov.nasa.arc.pds.lace.client.presenter.LabelSettingsPresenter;

import java.util.Map;

import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * Implements the view for the label settings component.
 */
public class LabelSettingsView extends DialogBox implements LabelSettingsPresenter.Display {

	interface Binder extends UiBinder<Widget, LabelSettingsView> { /*empty*/ }

	private static final Binder BINDER = GWT.create(Binder.class);

	@UiField(provided=true)
	EnumeratedRadioGroup radioGroup;

	@UiField
	Button okButton;

	@UiField
	Button cancelButton;

	private LabelSettingsPresenter presenter;

	/**
	 * Creates a new instance of the view.
	 *
	 * @param radioGroup the component for the radio buttons for variable styles
	 */
	@Inject
	public LabelSettingsView(EnumeratedRadioGroup radioGroup) {
		this.radioGroup = radioGroup;

		setWidget(BINDER.createAndBindUi(this));
		setText("Validation Settings");
		setGlassEnabled(true);
		setAutoHideEnabled(false);
	}

	@Override
	public void setPresenter(LabelSettingsPresenter presenter) {
		this.presenter = presenter;
	}

	@UiHandler("okButton")
	void onSaveChanges(ClickEvent event) {
		presenter.handleSaveChanges();
	}

	@UiHandler("cancelButton")
	void onCancelChanges(ClickEvent event) {
		presenter.handleCancelChanges();
	}

	@Override
	public String getVariableStyle() {
		return radioGroup.getStringValue();
	}

	@Override
	public void setVariableStyle(String value) {
		radioGroup.setStringValue(value);
	}

	@Override
	public void setVariableStyles(String[] values, Map<String, String> captionMap) {
		radioGroup.setStringValues(values, captionMap);
	}

	@Override
	public void display() {
		center();
	}

}
