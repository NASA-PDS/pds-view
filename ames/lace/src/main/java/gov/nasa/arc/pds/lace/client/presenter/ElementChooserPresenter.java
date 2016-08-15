package gov.nasa.arc.pds.lace.client.presenter;

import gov.nasa.arc.pds.lace.client.event.ClearEvent;
import gov.nasa.arc.pds.lace.client.event.RequestNewLabelEvent;
import gov.nasa.arc.pds.lace.client.event.SystemFailureEvent;
import gov.nasa.arc.pds.lace.client.service.LabelContentsServiceAsync;
import gov.nasa.arc.pds.lace.client.view.ElementChooserView;

import java.util.Collection;

import javax.inject.Inject;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.ImplementedBy;

/**
 * Implements the presenter for a widget that displays a list of namespaces
 * and allows the user to insert an element from the list of available 
 * elements for the selected namespace.
 */
public class ElementChooserPresenter extends Presenter<ElementChooserPresenter.Display> {
	
	/**
	 * Defines an interface that the view must implement.
	 */
	@ImplementedBy(ElementChooserView.class)
	public interface Display extends  Presenter.Display<ElementChooserPresenter> {
		
		/**
		 * Sets the list of namespaces.
		 * 
		 * @param namespaces a list of namespaces
		 */
		void setNamespaces(Collection<String> namespaces);
		
		/**
		 * Sets the list of element names.		 
		 * 
		 * @param elementNames a list of element names
		 */
		void setElementNames(Collection<String> elementNames);
		
		/**
		 * Gets the default value of the element names dropdown.
		 * 
		 * @return the default value of the element names dropdown
		 */
		String getDefaultValue();

		/**
		 * Sets the additional schema files that will be used when creating
		 * a new label.
		 * 
		 * @param result an array of schema file names
		 */
		void setSchemaFiles(String[] result);
		
		/**
		 * Gets the name of the schema file to upload.
		 * 
		 * @return the schema file name
		 */
		String getUploadFilename();
		
	}
	
	private PopupPresenter popup;
	private LabelContentsServiceAsync service;
	
	/**
	 * Creates an instance of the element chooser presenter.
	 *
	 * @param view the view to use for user interaction and display
	 * @param popup the popup to use for displaying error messages
	 * @param service the label editor RPC service, for communication with the server
	 */
	@Inject
	public ElementChooserPresenter(
			Display view,
			final PopupPresenter popup,
			LabelContentsServiceAsync service
	) {
		super(view);
		view.setPresenter(this);
		this.service = service;
		this.popup = popup;
		
		refreshNamespaces();
		refreshSchemaFiles();
	}

	private void refreshNamespaces() {
		service.getNamespaces(new AsyncCallback<Collection<String>>() {

			@Override
			public void onFailure(Throwable caught) {
				popup.showErrorBox("Communication Error", "Error communicating with server. Refresh to retry.");
			}

			@Override
			public void onSuccess(Collection<String> result) {
				getView().setNamespaces(result);
			}
			
		});
	}

	private void refreshSchemaFiles() {
		service.getDefaultSchemaFiles(new AsyncCallback<String[]>() {

			@Override
			public void onFailure(Throwable caught) {
				popup.showErrorBox("Communication Error", "Error communicating with server. Refresh to retry.");
			}

			@Override
			public void onSuccess(String[] result) {
				getView().setSchemaFiles(result);
			}
			
		});
	}
	
	/**
	 * Gets the list of top-level elements for the given namespace and loads them in the view.
	 * 
	 * @param namespace
	 */
	public void loadTopLevelElementNames(final String namespace) {		
		service.getElementNamesByNamespace(namespace, new AsyncCallback<Collection<String>>() {

			@Override
			public void onFailure(Throwable caught) {
				popup.showErrorBox("Communication Error", "Error communicating with server. Refresh to retry.");
			}

			@Override
			public void onSuccess(Collection<String> elementNames) {
				getView().setElementNames(elementNames);
			}
			
		});	
	}
	
	/**
	 * Cancels the element insertion by firing ClearEvent event.
	 */
	public void cancel() {
		fireEvent(new ClearEvent());
	}
	
	public void onChooseElement(String selectedNS, String selectedElement) {
		fireEvent(new RequestNewLabelEvent(selectedElement, selectedNS));
	}

	/**
	 * Tests whether the schema file name the user is requesting to upload
	 * is valid.
	 * 
	 * @return true, if the schema file name is specified
	 */
	public boolean isUploadValid() {
		String fileName = getView().getUploadFilename();
		int lastSlashPos = fileName.indexOf('/');
		if (lastSlashPos < 0) {
			lastSlashPos = fileName.indexOf('\\');
		}

		if (lastSlashPos >= 0) {
			fileName = fileName.substring(lastSlashPos+1);
		}

		return !fileName.trim().isEmpty();
	}
	
	/**
	 * Handles an invalid schema file upload request.
	 */
	public void onInvalidUploadRequest() {
		fireEvent(new SystemFailureEvent("No File was selected. Please browse to the label file that you want to import."));
	}

	/**
	 * Handles the completion of a schema file upload.
	 * Refreshes the schema file list.
	 */
	public void onUploadComplete() {
		// Do the RPC to add the uploaded file to the default schema files,
		// and refresh the schema file list.
		service.addDefaultSchemaFile(getView().getUploadFilename(), new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				fireEvent(new SystemFailureEvent("Error uploading the local schema file."));
			}

			@Override
			public void onSuccess(Void result) {
				refreshSchemaFiles();
				refreshNamespaces();
			}
			
		});
	}

	public void onRequestDeleteSchemaFile(String name) {
		service.removeDefaultSchemaFile(name, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable e) {
				fireEvent(new SystemFailureEvent("Unable to delete schema file: " + e.getMessage()));
			}

			@Override
			public void onSuccess(Void result) {
				refreshSchemaFiles();
				refreshNamespaces();
			}
			
		});
	}

}
