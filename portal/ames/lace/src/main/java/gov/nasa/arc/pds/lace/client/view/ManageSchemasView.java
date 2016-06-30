package gov.nasa.arc.pds.lace.client.view;

import gov.nasa.arc.pds.lace.client.presenter.ManageSchemasPresenter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 *
 */
public class ManageSchemasView extends DialogBox implements ManageSchemasPresenter.Display {

	interface Binder extends UiBinder<Widget, ManageSchemasView> { /*empty*/ }
	
	private static final Binder BINDER = GWT.create(Binder.class);	
	
	private ManageSchemasPresenter presenter;
	
	@UiField
	Label closeButton;
	
	@UiField
	FlowPanel contentPanel;
	
	@UiField
	FlowPanel buttonsPanel;
	
	/**
	 * Creates an instance of <code>PopupView</code>
	 */
	public ManageSchemasView() {
		setWidget(BINDER.createAndBindUi(this));		
		setModal(true);
		setGlassEnabled(true);
		setAutoHideEnabled(false);
		
		addCloseHandler(new CloseHandler<PopupPanel>() {
			
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				presenter.onRequestClose();
			}
			
		});
	}
	
	@Override
	public void setPresenter(ManageSchemasPresenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void display() {
		center();
	}

	@UiHandler("closeButton")
	void onClose(ClickEvent event) {
		presenter.onRequestClose();
	}

	@Override
	public void hide() {
		contentPanel.clear();
		buttonsPanel.clear();
		buttonsPanel.setVisible(false);
		super.hide();
	}
	
}
