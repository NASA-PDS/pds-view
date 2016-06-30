package gov.nasa.arc.pds.lace.client;

import gov.nasa.arc.pds.lace.client.event.CreateLabelEvent;
import gov.nasa.arc.pds.lace.client.event.LabelChangedEvent;
import gov.nasa.arc.pds.lace.client.event.LoginStateChangeEvent;
import gov.nasa.arc.pds.lace.client.event.SystemFailureEvent;
import gov.nasa.arc.pds.lace.client.event.SystemFailureEvent.EventDetails;
import gov.nasa.arc.pds.lace.client.presenter.EditorPresenter;
import gov.nasa.arc.pds.lace.client.presenter.PageHeaderPresenter;
import gov.nasa.arc.pds.lace.client.presenter.PopupPresenter;
import gov.nasa.arc.pds.lace.client.presenter.ProjectsPresenter;
import gov.nasa.arc.pds.lace.client.presenter.TreePresenter;
import gov.nasa.arc.pds.lace.client.resources.HtmlResources;
import gov.nasa.arc.pds.lace.client.service.LabelContentsServiceAsync;
import gov.nasa.arc.pds.lace.shared.Container;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;

/**
 * Implements a controller that controls the application wide events.
 *
 */
@Singleton
public class AppController implements ValueChangeHandler<String> {

	private static final String START_CLASS = "start";
	private static final String PROJECTS_CLASS = "projects";
	private static final String EDITOR_CLASS = "editor";

	private EventBus bus;
	private Provider<TreePresenter> treeProvider;
	private Provider<EditorPresenter> editorProvider;
	private Provider<PageHeaderPresenter> pageHeaderProvider;
	private Provider<PopupPresenter> popupProvider;
	private LabelContentsServiceAsync labelService;
	private Provider<ProjectsPresenter> projectsProvider;

	private Container rootContainer;

	/**
	 * Creates a new instance of the controller.
	 *
	 * @param bus the event bus to use for sending and receiving events
	 * @param treeProvider a provider for the tree component
	 * @param editorProvider a provider for the editor component
	 * @param pageHeaderProvider a provider for the page header component
	 * @param popupProvider a provider for the popup component
	 * @param projectsProvider a provider for the projects screen
	 * @param labelService the label editor RPC service for server interaction
	 */
	@Inject
	public AppController(
			EventBus bus,
			Provider<TreePresenter> treeProvider,
			Provider<EditorPresenter> editorProvider,
			Provider<PageHeaderPresenter> pageHeaderProvider,
			Provider<PopupPresenter> popupProvider,
			Provider<ProjectsPresenter> projectsProvider,
			LabelContentsServiceAsync labelService
	) {
		this.bus = bus;
		this.treeProvider = treeProvider;
		this.editorProvider = editorProvider;
		this.pageHeaderProvider = pageHeaderProvider;
		this.popupProvider = popupProvider;
		this.projectsProvider = projectsProvider;
		this.labelService = labelService;

		bus.addHandler(SystemFailureEvent.TYPE, new SystemFailureEvent.Handler() {

			@Override
			public void onEvent(EventDetails data) {
				handleSystemError(data);
			}
		});

		bus.addHandler(LoginStateChangeEvent.TYPE, new LoginStateChangeEvent.Handler() {
			@Override
			public void onEvent(Void data) {
				checkAuthenticated();
			}
		});

		bus.addHandler(LabelChangedEvent.TYPE, new LabelChangedEvent.Handler() {
			@Override
			public void onEvent(LabelChangedEvent.Data data) {
				if (data.getRootContainer() != null) {
					rootContainer = data.getRootContainer();
				}
			}
		});
	}

	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		// TODO
	}

	/**
	 * Shows the login screen, which is used when the user is not
	 * yet authenticated.
	 */
	public void loadLoginScreen() {
		String loginURL = GWT.getHostPageBaseURL();
		if (!loginURL.endsWith("/")) {
			loginURL += "/";
		}
		loginURL += "auth/login";
		Window.Location.assign(loginURL);
	}

	/**
	 * Opens a new window with the preview of a label
	 */
	public void loadPreviewScreen() {

		String previewURL = GWT.getHostPageBaseURL();
		if (!previewURL.endsWith("/")) {
			previewURL += "/";
		}
		previewURL += "preview.action";

		Window.open(previewURL,"_blank","toolbar=no, location=no, scrollbars=yes, resizable=yes");
	}

	/**
	 * Log out by calling the logout action, which invalidates
	 * the session and redirects to the login screen
	 */
	public void performLogout() {

		String logoutURL = GWT.getHostPageBaseURL();
		if (!logoutURL.endsWith("/")) {
			logoutURL += "/";
		}
		logoutURL += "logout.action";

		Window.Location.assign(logoutURL);
	}

	/**
	 * Loads the editor page. Removes the start screen view
	 * and replace it with the view for the editor page.
	 *
	 * @param rootElement the root element name to show as the top-level element
	 * @param namespaceURI the namespace of the root element
	 * in the tree on the editor page
	 */
	public void loadEditorPage(final String rootElement, final String namespaceURI) {
		// Show the editor page
		showEditor();

		if (rootElement != null) {
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				@Override
				public void execute() {
					bus.fireEvent(new CreateLabelEvent(rootElement, namespaceURI));
				}
			});
		}

	}

	private void showEditor() {
		// Page footer
		HTML footer = new HTML();
		footer.setHTML(HtmlResources.INSTANCE.getFooterHtml().getText());
		footer.getElement().setId("footer");

		// Tree view
		FlowPanel treePanel = new FlowPanel();
		treePanel.getElement().setId("treePanel");
		treePanel.add(treeProvider.get().asWidget());

		// Split panel consists of the tree view on the left and the editor on the right.
		SplitLayoutPanel splitPanel = new SplitLayoutPanel();
		splitPanel.getElement().setId("splitPanel");
		splitPanel.addWest(treePanel, 230.0D);
		splitPanel.setWidgetMinSize(treePanel, 100);
		splitPanel.add(editorProvider.get().asWidget());

		// Page header
		PageHeaderPresenter pageHeaderPresenter = pageHeaderProvider.get();
		pageHeaderPresenter.enableExportButton();
		pageHeaderPresenter.enableSettingsButton();
		pageHeaderPresenter.enablePreviewButton();
		pageHeaderPresenter.enableLogoutButton();

		// Main panel consists of the page header, footer and the split panel.
		DockLayoutPanel mainPanel = new DockLayoutPanel(Style.Unit.PX);
		mainPanel.addNorth(pageHeaderPresenter.asWidget(), 60.0D);
		mainPanel.addSouth(footer, 25.0D);
		mainPanel.add(splitPanel);

		// Add the main panel to the root panel.
		showWidget(mainPanel, EDITOR_CLASS);
	}

	private void loadProjectsPage(String userID) {
		ProjectsPresenter projects = projectsProvider.get();

		HTML footer = new HTML();
		footer.setHTML(HtmlResources.INSTANCE.getFooterHtml().getText());
		footer.getElement().setId("footer");

		// Page header
		PageHeaderPresenter pageHeaderPresenter = pageHeaderProvider.get();
		pageHeaderPresenter.enableCreateButton();
		pageHeaderPresenter.enableImportButton();
		pageHeaderPresenter.enableLogoutButton();
		pageHeaderPresenter.setPageTitle("Label Manager");
		pageHeaderPresenter.enableReturnLink(true); // Enable the link to return to the PDS Tools site.
		pageHeaderPresenter.allowTitleEditing(false);

		// Main panel consists of the page header, footer and the projects panel.
		DockLayoutPanel mainPanel = new DockLayoutPanel(Style.Unit.PX);
		mainPanel.addNorth(pageHeaderPresenter.asWidget(), 60.0D);
		mainPanel.addSouth(footer, 25.0D);
		mainPanel.add(projects);

		showWidget(mainPanel, PROJECTS_CLASS);
	}

	private void showWidget(IsWidget widget, String bodyClass) {
		// Remove the special class names which is set when the initial page
		// is loaded and remove all child widgets of the root layout panel.
		Document.get().getBody().removeClassName(START_CLASS);
		Document.get().getBody().removeClassName(PROJECTS_CLASS);
		Document.get().getBody().removeClassName(EDITOR_CLASS);

		if (bodyClass != null) {
			Document.get().getBody().addClassName(bodyClass);
		}
		RootLayoutPanel.get().clear();
		RootLayoutPanel.get().add(widget);
	}

	private void showCommunicationError() {
	    bus.fireEvent(new SystemFailureEvent("Error communicating with the server. Refresh the page to try again."));
	}

	private void handleSystemError(EventDetails data) {
		if (data.getFeedback() != null) {
			data.getFeedback().hide();
		}

		popupProvider.get().showErrorBox(data.getMessage());
	}

	private void checkAuthenticated() {
		labelService.getUser(new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				showCommunicationError();
			}

			@Override
			public void onSuccess(String userID) {
				if (userID == null) {
					loadLoginScreen();
				} else {
					loadProjectsPage(userID);
				}
			}

		});
	}

	/**
	 * Gets the top-level container for the current label.
	 *
	 * @return the root container
	 */
	public Container getRootContainer() {
		return rootContainer;
	}

}
