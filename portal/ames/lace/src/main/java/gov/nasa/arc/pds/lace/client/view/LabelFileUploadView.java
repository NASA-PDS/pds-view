package gov.nasa.arc.pds.lace.client.view;

/**
 * Implements a widget for importing (uploading) a label file.
 */
import gov.nasa.arc.pds.lace.client.presenter.LabelFileUploadPresenter;
import gov.nasa.arc.pds.lace.client.presenter.SchemaItemPresenter;
import gov.nasa.arc.pds.lace.client.presenter.SchemaItemPresenter.Handler;

import javax.inject.Inject;
import javax.inject.Provider;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;

public class LabelFileUploadView extends Composite implements LabelFileUploadPresenter.Display {

	interface LabelFileUploadViewUiBinder extends UiBinder<Widget, LabelFileUploadView> { /*empty*/ }
	
	private static final String LABEL_CELL_WIDTH = "75em";
	
	private static LabelFileUploadViewUiBinder uiBinder = GWT.create(LabelFileUploadViewUiBinder.class);	

	private LabelFileUploadPresenter presenter;

	@UiField
	FormPanel form;

	@UiField
	Button importButton;

	@UiField
	Button cancelButton;

	@UiField
	FileUpload upload;

	@UiField
	HorizontalPanel filePanel;
	
	@UiField
	InlineLabel file;
	
	@UiField
	FlowPanel localSchemas;
	
	@UiField
	FileUpload schemaUpload;
	
	@UiField
	FormPanel schemaUploadForm;

	private Provider<SchemaItemPresenter> itemProvider;

	private Handler schemaFileDeleteHandler;
	
	/**
	 * Creates a new instance of <code>LabelFileUploadView</code>.
	 * 
	 * @param itemProvider a provider of the component for each local schema file item
	 */
	@Inject
	public LabelFileUploadView(
			Provider<SchemaItemPresenter> itemProvider
	) {
		this.itemProvider = itemProvider;
		initWidget(uiBinder.createAndBindUi(this));
		filePanel.setCellWidth(file, LABEL_CELL_WIDTH);
		setupForm();
		
		// Point the form at a service.
		schemaUploadForm.setAction(GWT.getModuleBaseURL()  + "fileUpload");

	    // Because the form has a FileUpload widget, we'll need to set the
	    // form to use the POST method, and multipart MIME encoding.
		schemaUploadForm.setEncoding(FormPanel.ENCODING_MULTIPART);
		schemaUploadForm.setMethod(FormPanel.METHOD_POST);

		SchemaItemPresenter.Handler handler = new SchemaItemPresenter.Handler() {
			@Override
			public void onRequestDelete(String name) {
				presenter.onRequestDeleteSchemaFile(name);
			}
		};
		this.schemaFileDeleteHandler = handler;
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
	public String getFileName() {
		// Remove cfakepath to get the file name--see PTOOL-126.
		return upload.getFilename().replace("C:\\fakepath\\", "");
	}
	
	@UiHandler("cancelButton")
	public void onCancel(ClickEvent event) {
		presenter.onCancel();
	}

	@UiHandler("importButton")
	public void onImport(ClickEvent event) {
		presenter.doImport(event);
	}

	@UiHandler("form")
	public void onSubmit(SubmitEvent event) {
		presenter.onFormSubmit(event);
	}

	@Override
	public void setSchemaFiles(String[] result) {
		localSchemas.clear();
		for (String s : result) {
			SchemaItemPresenter item = itemProvider.get();
			item.setItemName(s);
			item.setHandler(schemaFileDeleteHandler);
			localSchemas.add(item);
		}
	}
	
	@UiHandler("schemaUploadForm")
	void onUploadComplete(SubmitCompleteEvent event) {
		presenter.onSchemaUploadComplete(schemaUpload.getFilename());
	}
	
}
