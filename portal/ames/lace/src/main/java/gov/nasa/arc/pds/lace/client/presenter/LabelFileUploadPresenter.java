package gov.nasa.arc.pds.lace.client.presenter;

import gov.nasa.arc.pds.lace.client.event.ClearEvent;
import gov.nasa.arc.pds.lace.client.event.LabelChangedEvent;
import gov.nasa.arc.pds.lace.client.event.LabelImportedEvent;
import gov.nasa.arc.pds.lace.client.event.RootContainerChangedEvent;
import gov.nasa.arc.pds.lace.client.event.SystemFailureEvent;
import gov.nasa.arc.pds.lace.client.event.TemplateNameChangedEvent;
import gov.nasa.arc.pds.lace.client.service.LabelContentsServiceAsync;
import gov.nasa.arc.pds.lace.client.view.LabelFileUploadView;
import gov.nasa.arc.pds.lace.shared.Container;

import javax.inject.Inject;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.inject.ImplementedBy;

/**
 * Implements the presenter object for the label upload component.
 */
public class LabelFileUploadPresenter extends Presenter<LabelFileUploadPresenter.Display> {

	/**
	 * Defines an interface that the view must implement.
	 */
	@ImplementedBy(LabelFileUploadView.class)
	public interface Display extends Presenter.Display<LabelFileUploadPresenter> {

		/**
		 * Gets the form that holds the file upload widget.
		 *
		 * @return the form panel
		 */
		FormPanel getForm();

		/**
		 * Gets the name of the file to upload.
		 *
		 * @return the file name
		 */
		String getFileName();

		/**
		 * Sets the list of schema files that will be used, in addition
		 * to the predefined schema files.
		 * 
		 * @param result an array of file names
		 */
		void setSchemaFiles(String[] result);
	}

	private LabelContentsServiceAsync service;
	private Feedback feedback;
	private String fileName;
	private boolean invalidFile = false;
	
	@Inject
	public LabelFileUploadPresenter(
			Display view,
			Feedback feedback,
			LabelContentsServiceAsync service
	) {
		super(view);
		this.service = service;
		this.feedback = feedback;
		view.setPresenter(this);
		
		refreshSchemaFiles();
	}

	/**
	 * Handles canceling of the import action by firing ClearEvent event.
	 */
	public void onCancel() {
		fireEvent(new ClearEvent());
	}

	/**
	 * Performs import of a label file.
	 */
	public void doImport(ClickEvent event) {
		getView().getForm().submit();
	}

	/**
	 * Handles the form submission. This event is fired just before
	 * the form is submitted. We can take this opportunity to perform validation.
	 */
	public void onFormSubmit(SubmitEvent event) {
		fileName = getView().getFileName();
		invalidFile = false;
		
		if (fileName == null || fileName.trim().length() == 0) {
			fireEvent(new SystemFailureEvent("No File was selected. Please browse to the label file that you want to import."));
			invalidFile = true;
		}
	}

	/**
	 * Handles the SubmitCompleteEvent event which is fired when
	 * the form submission is successfully completed.

	 * @param event the submit completion event
	 */
	public void onSubmitComplete(final SubmitCompleteEvent event) {
		if (!invalidFile) {
			final String msg = "The system failed to read the file and match the contents. " 
							+ "Please check that the label file is a valid PDS4 label.";
						
			feedback.display("Importing Label", true);
			
			service.getContainerForFile(fileName, new AsyncCallback<Container>() {
	
				@Override
				public void onFailure(Throwable caught) {
					GWT.log("The RPC call failed to upload the label file: " + caught.getMessage());
					fireEvent(new SystemFailureEvent(msg, feedback));
				}
	
				@Override
				public void onSuccess(Container container) {
					if (container.getContents().size() == 0) {
						fireEvent(new SystemFailureEvent(msg, feedback));
						return;
					}

					fireEvent(new LabelImportedEvent(container, fileName));
					fireEvent(new LabelChangedEvent(container));
					fireEvent(new RootContainerChangedEvent(container));
					fireEvent(new TemplateNameChangedEvent(fileName));
					
					feedback.hide();
					feedback.display("Label successfully imported", false);					
				}
			});
		}	
	}
	
	private void refreshSchemaFiles() {
		service.getDefaultSchemaFiles(new AsyncCallback<String[]>() {

			@Override
			public void onFailure(Throwable caught) {
				fireEvent(new SystemFailureEvent("Error communicating with server. Refresh to retry."));
			}

			@Override
			public void onSuccess(String[] result) {
				getView().setSchemaFiles(result);
			}
			
		});
	}

	/**
	 * Handles a request to delete a local schema file.
	 * 
	 * @param name the name of the local schema file
	 */
	public void onRequestDeleteSchemaFile(String name) {
		service.removeDefaultSchemaFile(name, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable e) {
				fireEvent(new SystemFailureEvent("Unable to delete schema file: " + e.getMessage()));
			}

			@Override
			public void onSuccess(Void result) {
				refreshSchemaFiles();
			}
			
		});
	}

	public void onSchemaUploadComplete(final String filename) {
		// Do the RPC to add the uploaded file to the default schema files,
		// and refresh the schema file list.
		service.addDefaultSchemaFile(filename, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				fireEvent(new SystemFailureEvent("Error uploading the local schema file."));
			}

			@Override
			public void onSuccess(Void result) {
				refreshSchemaFiles();
			}
			
		});
	}

}
