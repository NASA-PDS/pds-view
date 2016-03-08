package gov.nasa.arc.pds.lace.client.view;

import gov.nasa.arc.pds.lace.client.presenter.PageHeaderPresenter;
import gov.nasa.arc.pds.lace.client.resources.Resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Implements the view of the page header component.
 */
public class PageHeaderView extends Composite implements PageHeaderPresenter.Display {

	private static PageHeaderViewUiBinder uiBinder = GWT.create(PageHeaderViewUiBinder.class);

	interface PageHeaderViewUiBinder extends UiBinder<Widget, PageHeaderView> { }

	private PageHeaderPresenter presenter;

	@UiField
	InlineLabel templateName;

	@UiField
	Button importButton;

	@UiField
	Button exportButton;

	@UiField
	FlowPanel headerPanel;

	@UiField
	FormPanel form;

	@UiField
	Hidden fileName;

	@UiField
	Hidden desiredName;
	
	@UiField
	Image  laceLogo;
	
	@UiField
	Image nasaLogo;
	
	/**
	 * Creates a new view instance.
	 */
	public PageHeaderView() {
		initWidget(uiBinder.createAndBindUi(this));
		headerPanel.getElement().setId("header");
		nasaLogo.setResource(Resources.INSTANCE.getNasaLogo());
		laceLogo.setResource(Resources.INSTANCE.getLaceLogo());
		laceLogo.addStyleName("lace");				
	}

	@Override
	public void setPresenter(PageHeaderPresenter presenter) {
		this.presenter = presenter;
	}

	@UiHandler("importButton")
	void onImport(ClickEvent event) {
		if (presenter != null) {
			presenter.onImport();
		}
	}

	@UiHandler("exportButton")
	void onExport(ClickEvent event) {
		if (presenter != null) {
			presenter.onExport();
		}
	}

	@Override
	public void setTemplateName(String name) {
		templateName.setText(name);
	}

	@Override
	public void setDownloadFileName(String name) {
		this.fileName.setValue(name);
	}

	@Override
	public void setDownloadDesiredName(String name) {
		this.desiredName.setValue(name);
	}

	@Override
	public void performDownload() {
		String action = GWT.getHostPageBaseURL() + "designer/fileDownload";

		GWT.log("Setting upload form action to '" + action + "'.");
		form.setAction(action);
		form.setMethod(FormPanel.METHOD_GET);
		form.submit();
	}

}
