package gov.nasa.arc.pds.lace.client.presenter;

import gov.nasa.arc.pds.lace.client.event.LabelChangedEvent;
import gov.nasa.arc.pds.lace.client.event.SystemFailureEvent;
import gov.nasa.arc.pds.lace.client.resources.Strings;
import gov.nasa.arc.pds.lace.client.service.LabelContentsServiceAsync;
import gov.nasa.arc.pds.lace.client.view.LabelSettingsView;
import gov.nasa.arc.pds.lace.shared.ItemAttributes;

import java.util.Map;

import javax.inject.Inject;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.ImplementedBy;

/**
 * A component for allowing the user to specify label settings.
 */
public class LabelSettingsPresenter extends Presenter<LabelSettingsPresenter.Display> {

	/**
	 * The interface the view must implement.
	 */
	@ImplementedBy(LabelSettingsView.class)
	public interface Display extends Presenter.Display<LabelSettingsPresenter> {

		/**
		 * Sets the available variable styles.
		 *
		 * @param values the string values used to identify the styles
		 * @param captionMap a map between the values and the captions to present to the user
		 */
		void setVariableStyles(String[] values, Map<String, String> captionMap);

		/**
		 * Gets the selected variable style.
		 *
		 * @return the value of the variable style
		 */
		String getVariableStyle();

		/**
		 * Sets the selected variable style.
		 *
		 * @param value the value of the variable style
		 */
		void setVariableStyle(String value);

		/**
		 * Displays the dialog centered in the window.
		 */
		void display();

		/**
		 * Hides the dialog.
		 */
		void hide();

	}

	private Strings strings;
	private LabelContentsServiceAsync labelService;

	/**
	 * Creates a new instance with a given view for user interaction and display.
	 *
	 * @param view the view
	 * @param strings the translatable strings for the application
	 * @param labelService the service RPC for getting label settings
	 */
	@Inject
	public LabelSettingsPresenter(Display view, Strings strings, LabelContentsServiceAsync labelService) {
		super(view);
		this.strings = strings;
		this.labelService = labelService;
		view.setPresenter(this);
	}

	/**
	 * Displays the settings UI.
	 */
	public void display() {
		getView().setVariableStyles(
				new String[] {
						"NO_STYLE",
						"VELOCITY_STYLE",
						"FREEMARKER_STYLE"
				},
				strings.variableStyles()
		);

		labelService.getItemAttribute(ItemAttributes.VARIABLE_STYLE, new AsyncCallback<String>() {

			@Override
			public void onSuccess(String result) {
				if (result != null) {
					getView().setVariableStyle(result);
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				fireEvent(new SystemFailureEvent("Server communication error. Unable to retrieve current label settings."));
			}

		});

		getView().display();
	}

	/**
	 * Handles a request to save settings changes.
	 */
	public void handleSaveChanges() {
		labelService.setItemAttribute(ItemAttributes.VARIABLE_STYLE, getView().getVariableStyle(), new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				fireEvent(new SystemFailureEvent("Server communication error. Unable to save new label settings."));
			}

			@Override
			public void onSuccess(Void result) {
				// Pretend the label changed, to invoke a new validation.
				fireEvent(new LabelChangedEvent());
			}

		});

		getView().hide();
	}

	/**
	 * Handles a request to cancel any settings changes.
	 */
	public void handleCancelChanges() {
		getView().hide();
	}

}
