package gov.nasa.arc.pds.lace.client.view;

import gov.nasa.arc.pds.lace.client.presenter.LoginScreenPresenter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * Implements the view of the start screen component.
 */
public class LoginScreenView extends Composite implements LoginScreenPresenter.Display {

	interface Binder extends UiBinder<Widget, LoginScreenView> { /*empty*/ }
	
	private static Binder uiBinder = GWT.create(Binder.class);

	private LoginScreenPresenter presenter;
	
	@UiField 
	HTML logoHolder;

	@UiField
	TextBox userName;
	
	@UiField
	Button loginButton;
	
	/**
	 * Creates a new view instance.
	 */
	public LoginScreenView() {		
		initWidget(uiBinder.createAndBindUi(this));
		
		String html = "<img src='images/logo-nasa-lg.png' class='logo nasa' />"
					+ "<img src='images/logo-lace-lg.png' class='logo lace' />";
		logoHolder.setHTML(html);
		
		// scheduleDeferred is executed after the browser event loop returns,
		// which indicates that the widget is loaded.
		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand () {
			public void execute () {
				userName.setFocus(true);
			}
		});
		
		userName.addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {				
				 if(event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {					 
					 userName.setFocus(false);
					 doLogin();
				 }
			}
		});
	}

	@Override
	public void setPresenter(LoginScreenPresenter presenter) {
		this.presenter = presenter;
	}

	@UiHandler("loginButton")
	public void onLogin(ClickEvent event) {
		doLogin();
	}
	
	private void doLogin() {
		if (presenter != null) {
			presenter.onRequestLogin(userName.getText());
		}
	}
	
}
