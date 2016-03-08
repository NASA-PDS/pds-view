package gov.nasa.arc.pds.lace.client.view;

import gov.nasa.arc.pds.lace.client.presenter.LabelFileUploadPresenter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class LabelFileUploadView extends Composite implements LabelFileUploadPresenter.Display {

	private static LabelFileUploadViewUiBinder uiBinder = GWT.create(LabelFileUploadViewUiBinder.class);

	interface LabelFileUploadViewUiBinder extends UiBinder<Widget, LabelFileUploadView> {
	}

	private LabelFileUploadPresenter presenter;

	@UiField
	FormPanel form;

	@UiField
	TextBox templateName;

	@UiField
	Button uploadButton;

	@UiField
	Button cancelButton;

	@UiField
	FileUpload upload;

	public LabelFileUploadView() {
		initWidget(uiBinder.createAndBindUi(this));
		setupForm();
	}

	@Override
	public void setPresenter(LabelFileUploadPresenter presenter) {
		this.presenter = presenter;
	}

	private void setupForm() {
		// Point the form at a service.
		form.setAction(GWT.getModuleBaseURL()  + "fileUpload");

	    // Because the form has a FileUpload widget, we'll need to set the
	    // form to use the POST method, and multipart MIME encoding.
	    form.setEncoding(FormPanel.ENCODING_MULTIPART);
	    form.setMethod(FormPanel.METHOD_POST);

	    form.addSubmitCompleteHandler(new SubmitCompleteHandler() {
	        @Override
			public void onSubmitComplete(SubmitCompleteEvent event) {
	        	if (presenter != null) {
	        		presenter.onSubmitComplete(event);
	        	}
	        }
	      });
	}

	@Override
	public FormPanel getForm() {
		return form;
	}

	@Override
	public void setFile() {
		templateName.setValue(templateName.getText());
	}

	@UiHandler("cancelButton")
	public void onCancel(ClickEvent event) {
		presenter.onCancel();
	}

	/**
	 * Adds an event handler to the form.
	 *
	 * @param event
	 */
	@UiHandler("uploadButton")
	public void onImportButtonClickEvent(ClickEvent event) {
		presenter.handleImportButtonClickEvent(event);
	}

	@UiHandler("form")
	public void onSubmit(SubmitEvent event) {
		presenter.onFormSubmit(event);
	}

	@Override
	public String getFileName() {
		return upload.getFilename();
	}

	@Override
	public String getTemplateName() {
		return templateName.getValue();
	}

}
