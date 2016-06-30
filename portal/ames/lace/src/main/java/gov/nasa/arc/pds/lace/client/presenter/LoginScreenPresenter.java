package gov.nasa.arc.pds.lace.client.presenter;

import gov.nasa.arc.pds.lace.client.event.LoginStateChangeEvent;
import gov.nasa.arc.pds.lace.client.service.LabelContentsServiceAsync;
import gov.nasa.arc.pds.lace.client.view.LoginScreenView;

import javax.inject.Inject;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.inject.ImplementedBy;

/**
 * Implements a presenter for a widget that displays a start screen which
 * enables the user to either import an existing label or create a new label.
 */
public class LoginScreenPresenter extends Presenter<LoginScreenPresenter.Display> {

	/**
	 * Defines an interface that the view must implement.
	 */
	@ImplementedBy(LoginScreenView.class)
	public interface Display extends Presenter.Display<LoginScreenPresenter> {

	}

	private LabelContentsServiceAsync labelService;

	/**
	 * Creates a new instance of the start screen presenter.
	 *
	 * @param view the display interface for the start screen view
	 * @param labelService the label editor RPC service to use for server communication
	 */
	@Inject
	public LoginScreenPresenter(
			Display view,
			LabelContentsServiceAsync labelService
	) {
		super(view);
		view.setPresenter(this);
		this.labelService = labelService;
	}

	/**
	 * Handles a request from the uer to log in.
	 *
	 * @param userID the user ID
	 */
	public void onRequestLogin(String userID) {
		labelService.setUser(userID, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				PopupPanel alert = new PopupPanel();
				alert.setWidget(new Label("Error communicating with the server."));
				alert.show();
			}

			@Override
			public void onSuccess(Void result) {
				fireEvent(new LoginStateChangeEvent());
			}

		});
	}

}
