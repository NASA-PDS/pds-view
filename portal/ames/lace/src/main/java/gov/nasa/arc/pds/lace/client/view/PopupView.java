package gov.nasa.arc.pds.lace.client.view;

import gov.nasa.arc.pds.lace.client.presenter.PopupPresenter;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 *
 */
public class PopupView extends DialogBox implements PopupPresenter.Display {

	interface PopupViewUiBinder extends UiBinder<Widget, PopupView> { /*empty*/ }
	
	private static final String DEFAULT_ERROR_MESSAGE = "Error";
	
	private static PopupViewUiBinder uiBinder = GWT.create(PopupViewUiBinder.class);	
	
	private PopupPresenter presenter;
	private List<PredefinedButton> buttons = new ArrayList<PredefinedButton>();
	private ClickHandler closeHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			presenter.onClose(event);
		}		
	};
	
	/** The predefined buttons supported by this widget.
	 */
	private enum PredefinedButton {
		/** An "OK" button */
		OK("OK"),
		
		/** A "Yes button */
		YES("Yes"),
		
		/** A "No" button */
		NO("No");
		
		private String text;
		private ClickHandler handler;
		
		private PredefinedButton(String text) {
			this.text = text;
		}
		
		public String getText() {
			return text;
		}
		
		public ClickHandler getClickHandler() {
			return handler;
		}
		
		public void setClickHandler(ClickHandler handler) {
			this.handler = handler;
		}
	}

	@UiField
	Label closeButton;
	
	@UiField
	FlowPanel contentPanel;
	
	@UiField
	FlowPanel buttonsPanel;
	
	/**
	 * Creates an instance of <code>PopupView</code>
	 */
	public PopupView() {
		setWidget(uiBinder.createAndBindUi(this));
		setGlassEnabled(true);
		setAutoHide(false);
		
		addCloseHandler(new CloseHandler<PopupPanel>() {
			
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				presenter.onClose(null);
			}
			
		});
	}
	
	@Override
	public void setPresenter(PopupPresenter presenter) {
		this.presenter = presenter;
	}
	
	@Override
	public void setAutoHide(boolean autoHide) {
		setAutoHideEnabled(autoHide);
	}
	
	@Override
	public void display() {
		center();
	}

	@Override
	public void setContent(Widget widget) {
		contentPanel.add(widget);
	}
	
	@UiHandler("closeButton")
	void onClose(ClickEvent event) {
		presenter.onClose(event);
	}

	@Override
	public void setContent(String text) {
		setContent(new HTML("<div>" + text + "</div"));		
	}
	
	@Override
	public void showConfirmationBox(String title, String message, ClickHandler yesButtonHandler, ClickHandler noButtonHandler) {		
		if (noButtonHandler == null) {
			noButtonHandler = closeHandler;
		}	
				
		PredefinedButton.YES.setClickHandler(yesButtonHandler);
		PredefinedButton.NO.setClickHandler(noButtonHandler);
		setPredefinedButtons(PredefinedButton.YES, PredefinedButton.NO);
		setText(title);
		setContent(message);
		display();
	}

	@Override
	public void showErrorBox(String title, String message) {
		if (title == null || title.trim().isEmpty()) {
			title = DEFAULT_ERROR_MESSAGE;
		}		
			
		PredefinedButton.OK.setClickHandler(closeHandler);
		setPredefinedButtons(PredefinedButton.OK);
		setText(title);
		setContent(message);
		display();
	}

	@Override
	public void hide() {
		contentPanel.clear();
		buttonsPanel.clear();
		buttonsPanel.setVisible(false);
		super.hide();
	}
	
	public void setPredefinedButtons(PredefinedButton... buttons) {
		this.buttons.clear();	    
	    for (PredefinedButton b : buttons) {
	    	this.buttons.add(b);
	    }
	    createButtons();
	}
	
	private void createButtons() {
		buttonsPanel.clear();
		
		for (int i = 0; i < buttons.size(); i++) {
			PredefinedButton b = buttons.get(i);
			Button button = new Button(b.getText());
			button.addClickHandler(b.getClickHandler());
			buttonsPanel.add(button);
		}
		
		buttonsPanel.setVisible(true);
	}
}
