package gov.nasa.arc.pds.lace.client.view;

import gov.nasa.arc.pds.lace.client.presenter.PopupPresenter;
import gov.nasa.arc.pds.lace.client.resources.Resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class PopupView extends DialogBox implements PopupPresenter.Display {

	private static PopupViewUiBinder uiBinder = GWT.create(PopupViewUiBinder.class);

	interface PopupViewUiBinder extends UiBinder<Widget, PopupView> {
	}

	@UiField
	Image closeButton;
	
	@UiField
	FlowPanel contentPanel;
	
	private PopupPresenter presenter;
	
	public PopupView() {
		setWidget(uiBinder.createAndBindUi(this));		
		setGlassEnabled(true);
		//setAutoHideEnabled(true);		
		closeButton.setResource(Resources.INSTANCE.getCloseButtonIcon());
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
	void onCancelClick(ClickEvent event) {
		if (presenter != null) {
			presenter.onClose();
		}
	}
}
