package gov.nasa.arc.pds.lace.client.view;

import gov.nasa.arc.pds.lace.client.presenter.PopupPresenter;

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

	private static PopupViewUiBinder uiBinder = GWT.create(PopupViewUiBinder.class);

	interface PopupViewUiBinder extends UiBinder<Widget, PopupView> { /*empty*/ }

	@UiField
	Label closeButton;
	
	@UiField
	FlowPanel contentPanel;
	
	@UiField
	FlowPanel buttonsPanel;
	
	@UiField
	Button okButton;
	
	@UiField
	Button cancelButton;
	
	private PopupPresenter presenter;
	
	/**
	 * Creates an instance of <code>PopupView</code>
	 */
	public PopupView() {
		setWidget(uiBinder.createAndBindUi(this));		
		setGlassEnabled(true);
		setAutoHideEnabled(true);		
		
		addCloseHandler(new CloseHandler<PopupPanel>() {

			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				close();				
			}
			
		});
	}
	
	@Override
	public void setPresenter(PopupPresenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void display() {
		center();
	}

	@Override
	public void clear() {
		contentPanel.clear();
	}
	
	@Override
	public void setContent(Widget widget) {
		contentPanel.add(widget);
	}
	
	@UiHandler("closeButton")
	void onClose(ClickEvent event) {
		close();
	}
	
	private void close() {
		if (presenter != null) {
			presenter.onClose();
		}
	}

	@Override
	public void setContent(String text) {
		setContent(new HTML("<div>" + text + "</div"));		
	}
	
	@Override
	public void setConfirmation(ClickHandler yesButtonHandler, ClickHandler noButtonHandler) {
		okButton.setText("Yes");
		cancelButton.setText("No");
		
		// Add click handlers.
		okButton.addClickHandler(yesButtonHandler);
		cancelButton.addClickHandler(noButtonHandler);
		
		buttonsPanel.setVisible(true);
	}
}
