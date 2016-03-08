package gov.nasa.arc.pds.lace.client.presenter;

import gov.nasa.arc.pds.lace.client.event.ClearEvent;
import gov.nasa.arc.pds.lace.client.event.ClearEventHandler;
import gov.nasa.arc.pds.lace.client.event.ContainerChangedEvent;
import gov.nasa.arc.pds.lace.client.event.TemplateNameChangedEvent;
import gov.nasa.arc.pds.lace.client.event.ContainerChangedEvent.EventDetails;
import gov.nasa.arc.pds.lace.client.service.LabelContentsServiceAsync;
import gov.nasa.arc.pds.lace.shared.Container;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * Implements the presenter of the page header component.
 */
public class PageHeaderPresenter extends Presenter<PageHeaderPresenter.Display> {

	/**
	 * Defines an interface that the view must implement.
	 */
	public interface Display extends Presenter.Display<PageHeaderPresenter> {

		/**
		 * Sets the name of the template.
		 *
		 * @param name the new template name
		 */
		void setTemplateName(String name);

		/**
		 * Sets the name of the exported label file to download.
		 *
		 * @param fileName the name of the exported label file
		 */
		void setDownloadFileName(String fileName);

		/**
		 * Sets the desired name for saving the exported label.
		 *
		 * @param desiredName the desired name
		 */
		void setDownloadDesiredName(String desiredName);

		/**
		 * Performs the download of the exported label file.
		 */
		void performDownload();

	}

	private Provider<LabelFileUploadPresenter> fileUploadProvider;
	private PopupPresenter popup;
	private LabelContentsServiceAsync labelService;
	private Container rootContainer;

	/**
	 * Creates a new instance of the page header presenter.
	 *
	 * @param view the view for user interaction and display
	 * @param bus the event bus for catching and firing events
	 * @param popup the popup form for the file upload controls
	 * @param fileUploadProvider a provider for the file upload form
	 * @param labelService the RPC for label actions
	 */
	@Inject
	public PageHeaderPresenter(
			Display view,
			EventBus bus,
			final PopupPresenter popup,
			Provider<LabelFileUploadPresenter> fileUploadProvider,
			LabelContentsServiceAsync labelService
	) {
		super(view);
		this.fileUploadProvider = fileUploadProvider;
		this.popup = popup;
		this.labelService = labelService;
		view.setPresenter(this);

		bus.addHandler(ClearEvent.TYPE, new ClearEventHandler() {
			@Override
			public void onEvent(String data) {
				popup.hide();
			}
		});

		bus.addHandler(TemplateNameChangedEvent.TYPE, new TemplateNameChangedEvent.Handler() {
			@Override
			public void onEvent(String data) {
				getView().setTemplateName(data);
			}
		});

		bus.addHandler(ContainerChangedEvent.TYPE, new ContainerChangedEvent.Handler() {
			@Override
			public void onEvent(EventDetails details) {
				if (details.isRootContainer()) {
					rootContainer = details.getContainer();
				}				
			}
		});
	}

	/**
	 * Handles a user request to import an existing label.
	 * We let the {@link gov.nasa.arc.pds.lace.client.presenter.LabelFileUploadPresenter}
	 * to the actual importing.
	 */
	public void onImport() {
		popup.setText("Import a PDS4 Label");
		popup.setContent(fileUploadProvider.get().asWidget());
		popup.display();
	}

	/**
	 * Handles a request by the user to export the label.
	 */
	public void onExport() {
		labelService.writeModel(rootContainer, new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				//TODO: Report error to the user.
			}

			@Override
			public void onSuccess(String result) {
				getView().setDownloadFileName(result);
				getView().setDownloadDesiredName("label.xml");
				getView().performDownload();
			}

		});
	}

}
