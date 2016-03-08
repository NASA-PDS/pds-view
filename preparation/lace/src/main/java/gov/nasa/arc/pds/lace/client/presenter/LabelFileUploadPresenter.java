package gov.nasa.arc.pds.lace.client.presenter;

import gov.nasa.arc.pds.lace.client.event.ClearEvent;
import gov.nasa.arc.pds.lace.client.event.ContainerChangedEvent;
import gov.nasa.arc.pds.lace.client.event.TemplateNameChangedEvent;
import gov.nasa.arc.pds.lace.client.service.LabelContentsServiceAsync;
import gov.nasa.arc.pds.lace.shared.Container;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.inject.Inject;

/**
 * Implements the presenter object for the label upload component.
 */
public class LabelFileUploadPresenter extends Presenter<LabelFileUploadPresenter.Display> {

	/**
	 * Defines an interface that the view must implement.
	 */
	public interface Display extends Presenter.Display<LabelFileUploadPresenter> {

		/**
		 * Gets the form that holds the file upload widget.
		 *
		 * @return the form panel
		 */
		FormPanel getForm();

		//TODO: Figure out why this method is needed.
		void setFile();

		/**
		 * Gets the name of the file to upload.
		 *
		 * @return the file name
		 */
		String getFileName();

		/**
		 * Gets the name of the template as entered by the user.
		 *
		 * @return the new template name
		 */
		String getTemplateName();

	}

	private EventBus bus;
	private LabelContentsServiceAsync service;

	@Inject
	public LabelFileUploadPresenter(
			Display view,
			EventBus bus,
			PopupPresenter popup,
			LabelContentsServiceAsync service
	) {
		super(view);
		this.bus = bus;
		this.service = service;
		view.setPresenter(this);
	}

	public void onCancel() {
		bus.fireEvent(new ClearEvent(""));
	}

	/**
	 * Handles the click of the import button.
	 */
	public void handleImportButtonClickEvent(ClickEvent event) {
		getView().setFile();
		getView().getForm().submit();
	}

	/**
	 * Handles the form submission. This event is fired just before
	 * the form is submitted. We can take this opportunity to perform validation.
	 */
	public void onFormSubmit(SubmitEvent event) {
	}

	/**
	 * Handles the SubmitCompleteEvent event which is fired when the form submission is successfully completed.

	 * @param event the submit completion event
	 */
	public void onSubmitComplete(final SubmitCompleteEvent event) {
		service.getContainerForFile(getView().getFileName(), new AsyncCallback<Container>() {

			@Override
			public void onFailure(Throwable caught) {
				bus.fireEvent(new ClearEvent(event.getResults()));
				//TODO: Show error!
			}

			@Override
			public void onSuccess(Container result) {
				bus.fireEvent(new ContainerChangedEvent(result, true));
				bus.fireEvent(new TemplateNameChangedEvent(getView().getTemplateName()));
				bus.fireEvent(new ClearEvent(event.getResults()));
			}

		});
	}

}
