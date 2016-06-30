package gov.nasa.arc.pds.lace.client.presenter;

import gov.nasa.arc.pds.lace.client.AppController;
import gov.nasa.arc.pds.lace.client.event.ClearEvent;
import gov.nasa.arc.pds.lace.client.event.LabelImportedEvent;
import gov.nasa.arc.pds.lace.client.event.LoginStateChangeEvent;
import gov.nasa.arc.pds.lace.client.event.RequestNewLabelEvent;
import gov.nasa.arc.pds.lace.client.event.RootContainerChangedEvent;
import gov.nasa.arc.pds.lace.client.event.SystemFailureEvent;
import gov.nasa.arc.pds.lace.client.event.TemplateNameChangedEvent;
import gov.nasa.arc.pds.lace.client.service.LabelContentsServiceAsync;
import gov.nasa.arc.pds.lace.client.view.PageHeaderView;
import gov.nasa.arc.pds.lace.shared.Container;

import javax.inject.Inject;
import javax.inject.Provider;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.ImplementedBy;

/**
 * Implements the presenter of the page header component.
 */
public class PageHeaderPresenter extends Presenter<PageHeaderPresenter.Display> {

	/**
	 * Defines an interface that the view must implement.
	 */
	@ImplementedBy(PageHeaderView.class)
	public interface Display extends Presenter.Display<PageHeaderPresenter> {

		/**
		 * Enables or disables the link to return to the PDS Tools site.
		 *
		 * @param enable true, if the link should be allowed
		 */
		void enableReturnLink(boolean enable);

		/**
		 * Sets the page title.
		 *
		 * @param title the page title
		 */
		void  setPageTitle(String title);

		/**
		 * Sets whether to allow the page title (label name) to be edited.
		 *
		 * @param allow true, if page title should be editable.
		 */
		void allowTitleEditing(boolean allow);

		/**
		 * Sets the name of the label.
		 *
		 * @param name the new label name
		 */
		void setLabelName(String name);

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

		/**
		 * Enables the create button.
		 */
		void enableCreateButton();

		/**
		 * Enables the import button.
		 */
		void enableImportButton();

		/**
		 * Enables the export button.
		 */
		void enableExportButton();

		/**
		 * Enables the settings button.
		 */
		void enableSettingsButton();

		/**
		 * Enables the preview button.
		 */
		void enablePreviewButton();

		/**
		 * Enables the logout button.
		 */
		void enableLogoutButton();

	}

	private static final String DEFAULT_LABEL_NAME = "Untitled.xml";

	private Provider<LabelFileUploadPresenter> fileUploadProvider;
	private PopupPresenter popup;
	private LabelContentsServiceAsync labelService;
	private Container rootContainer;
	private String labelName = DEFAULT_LABEL_NAME;

	private Provider<ElementChooserPresenter> chooserProvider;
	private Provider<Feedback> feedbackProvider;

	private AppController controller;

	private Provider<LabelSettingsPresenter> settingsProvider;

	/**
	 * Creates a new instance of the page header presenter.
	 *
	 * @param view the view for user interaction and display
	 * @param popup the popup form for the file upload controls
	 * @param fileUploadProvider a provider for the file upload form
	 * @param chooserProvider a provider for the top-level element chooser
	 * @param settingsProvider a provider for the label settings component
	 * @param feedbackProvider a provider for the feedback component
	 * @param labelService the RPC for label actions
	 * @param controller the main application view controller
	 */
	@Inject
	public PageHeaderPresenter(
			Display view,
			final PopupPresenter popup,
			Provider<LabelFileUploadPresenter> fileUploadProvider,
			Provider<ElementChooserPresenter> chooserProvider,
			Provider<LabelSettingsPresenter> settingsProvider,
			Provider<Feedback> feedbackProvider,
			LabelContentsServiceAsync labelService,
			AppController controller
	) {
		super(view);
		this.fileUploadProvider = fileUploadProvider;
		this.chooserProvider = chooserProvider;
		this.settingsProvider = settingsProvider;
		this.feedbackProvider = feedbackProvider;
		this.popup = popup;
		this.labelService = labelService;
		this.controller = controller;

		view.setPresenter(this);
		view.setLabelName(labelName);
	}

	@Override
	protected void addEventHandlers() {

		addEventHandler(LabelImportedEvent.TYPE, new LabelImportedEvent.Handler() {

			@Override
			public void onEvent(LabelImportedEvent.EventDetails data) {
				popup.hide();
				controller.loadEditorPage(null, null);
			}
		});

		addEventHandler(ClearEvent.TYPE, new ClearEvent.Handler() {

			@Override
			public void onEvent(String data) {
				popup.hide();
			}
		});

		addEventHandler(TemplateNameChangedEvent.TYPE, new TemplateNameChangedEvent.Handler() {

			@Override
			public void onEvent(String name) {
				handleLabelNameChangedEvent(name);
			}
		});

		addEventHandler(RootContainerChangedEvent.TYPE, new RootContainerChangedEvent.Handler() {
			@Override
			public void onEvent(RootContainerChangedEvent.Data data) {
				rootContainer = data.getRootContainer();
			}
		});

		addEventHandler(RequestNewLabelEvent.TYPE, new RequestNewLabelEvent.Handler() {
			@Override
			public void onEvent(final RequestNewLabelEvent.Data data) {
				popup.hide();
				controller.loadEditorPage(data.getElementName(), data.getNamespaceURI());
			}
		});
	}

	private void handleLabelNameChangedEvent(String name) {
		if (name == null || name.trim().length() == 0) {
			name = DEFAULT_LABEL_NAME;
		}
		labelName = name;
		getView().setLabelName(name);
	}

	/**
	 * Handles a user request to import an existing label.
	 * We let the {@link gov.nasa.arc.pds.lace.client.presenter.LabelFileUploadPresenter}
	 * do the actual importing.
	 */
	public void onImport() {
		popup.setText("Import a Label");
		popup.setContent(fileUploadProvider.get().asWidget());
		popup.display();
	}

	/**
	 * Handles a request by the user to export the label.
	 */
	public void onExport() {
		if (rootContainer.isComplete()) {
			doExport();
		} else {
			String msg = "The label you are trying to export is not a valid label "
						+ "and is missing required values.<br />"
						+ "Do you STILL want to export the INCOMPLETE label?";

			ClickHandler yesButtonHandler = new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					popup.hide();
					doExport();
				}
			};

			popup.showConfirmationBox("Warning" , msg, yesButtonHandler, null);
		}
	}

	private void doExport() {
		labelService.writeModel(rootContainer, new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				fireEvent(new SystemFailureEvent("Server communication error. Unable to export the label."));
			}

			@Override
			public void onSuccess(String result) {
				Display view = getView();
				view.setDownloadFileName(result);
				view.setDownloadDesiredName(labelName);
				view.performDownload();
			}

		});
	}

	/**
	 * Handles a request by the user to preview the label.
	 */
	public void onPreview() {

		//'A deferred command is executed after the browser event loop returns'
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				doPreview();
			}
		});
	}

	private void doPreview() {

		labelService.writeModel(rootContainer, new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				fireEvent(new SystemFailureEvent("Server communication error. Unable to preview the label."));
			}

			@Override
			public void onSuccess(String result) {
				controller.loadPreviewScreen();
			}

		});
	}

	/**
	 * Handles a request by the user to logout.
	 */
	public void onLogout() {

		//save label before logging out
		labelService.writeModel(rootContainer, new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				fireEvent(new SystemFailureEvent("Server communication error. Unable to log out."));
			}

			@Override
			public void onSuccess(String result) {
				controller.performLogout();
			}

		});
	}

	/**
	 * Handles a request by the user to create a new label.
	 */
	public void onNewLabel() {
		ElementChooserPresenter chooser = chooserProvider.get();
		popup.setText("Choose a Root Element");
		popup.setContent(chooser.asWidget());
		popup.display();
	}

	/**
	 * Handles a request by the user to save a new label name.
	 *
	 * @param name the new label name
	 */
	public void saveLabelName(String name) {
		if (name == null || name.trim().length() == 0) {
			name = DEFAULT_LABEL_NAME;
		}

		final String labelName = name;
		labelService.setLabelName(labelName, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				fireEvent(new SystemFailureEvent("Server communication error. Unable to change label name."));
			}

			@Override
			public void onSuccess(Void result) {
				handleLabelNameChangedEvent(labelName);
				feedbackProvider.get().display("Label name saved.", false);
			}

		});
	}

	/**
	 * Handles a user request to return to the projects page.
	 */
	public void onRequestReturn() {
		fireEvent(new LoginStateChangeEvent());
	}

	/**
	 * Enables or disables the link to return to the PDS Tools site.
	 *
	 * @param enable true, if the link should be allowed
	 */
	public void enableReturnLink(boolean enable) {
		getView().enableReturnLink(enable);
	}

	/**
	 * Sets the page title.
	 *
	 * @param title the page title
	 */
	public void setPageTitle(String title) {
		getView().setPageTitle(title);
	}

	/**
	 * Sets whether to allow the page title (label name) to be edited.
	 *
	 * @param allow true, if page title should be editable.
	 */
	public void allowTitleEditing(boolean allow) {
		getView().allowTitleEditing(allow);
	}

	/**
	 * Enables the create button.
	 */
	public void enableCreateButton() {
		getView().enableCreateButton();
	}

	/**
	 * Enables the import button.
	 */
	public void enableImportButton() {
		getView().enableImportButton();
	}

	/**
	 * Enables the export button.
	 */
	public void enableExportButton() {
		getView().enableExportButton();
	}

	/**
	 * Enables the preview button.
	 */
	public void enablePreviewButton() {
		getView().enablePreviewButton();
	}

	/**
	 * Enables the logout button.
	 */
	public void enableLogoutButton() {
		getView().enableLogoutButton();
	}

	/**
	 * Enables the settings button.
	 */
	public void enableSettingsButton() {
		getView().enableSettingsButton();
	}

	/**
	 * Handles a request by the user to change the label settings.
	 */
	public void onSettings() {
		settingsProvider.get().display();
	}

}
