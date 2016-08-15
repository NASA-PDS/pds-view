package gov.nasa.arc.pds.lace.client.presenter;

import gov.nasa.arc.pds.lace.client.AppController;
import gov.nasa.arc.pds.lace.client.event.LabelChangedEvent;
import gov.nasa.arc.pds.lace.client.event.RootContainerChangedEvent;
import gov.nasa.arc.pds.lace.client.event.SystemFailureEvent;
import gov.nasa.arc.pds.lace.client.event.TemplateNameChangedEvent;
import gov.nasa.arc.pds.lace.client.service.LabelContentsServiceAsync;
import gov.nasa.arc.pds.lace.client.view.ProjectsView;
import gov.nasa.arc.pds.lace.shared.Container;
import gov.nasa.arc.pds.lace.shared.project.ProjectItem;

import javax.inject.Inject;
import javax.inject.Provider;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.ImplementedBy;

/**
 * Implements the presenter for a widget that displays the
 * editor (edit view) which consists of a title and a content.
 */
public class ProjectsPresenter extends Presenter<ProjectsPresenter.Display> {

	/**
	 * Defines an interface that the view must implement.
	 */
	@ImplementedBy(ProjectsView.class)
	public interface Display extends Presenter.Display<ProjectsPresenter> {

		/**
		 * Clears all items from the display.
		 */
		void clearItems();

		/**
		 * Adds another project item to the display.
		 * 
		 * @param itemPresenter the item component
		 */
		void addItem(ProjectItemPresenter itemPresenter);
		
		/**
		 * Adds the specified widget to the content.
		 * Used to show the help screen.
		 * 
		 * @param widget the widget to add to content
		 */
		void add(IsWidget widget);
		
		/**
		 * Shows a message when there are no project items to display.
		 */
		void showEmptyItemsList();		
	}

	private LabelContentsServiceAsync labelService;
	private PopupPresenter popup;
	private Provider<ProjectItemPresenter> itemProvider;
	private AppController controller;
	private Feedback feedback;
	private Provider<ProjectsWelcomePresenter> welcomeProvider;
	
	/**
	 * Creates an instance of the editor presenter.
	 *
	 * @param view the display interface for the editor view
	 * @param labelService the server-side RPC for getting project information
	 * @param popup a popup panel for showing errors
	 * @param itemProvider a provider for components to display project items
	 * @param welcomeProvider a provider for the welcome screen component
	 * @param controller the main application view controller
	 * @param feedback a component to display feedback as we load labels
	 */
	@Inject
	public ProjectsPresenter(
			Display view,
			LabelContentsServiceAsync labelService,
			PopupPresenter popup,
			Provider<ProjectItemPresenter> itemProvider,
			Provider<ProjectsWelcomePresenter> welcomeProvider,
			AppController controller,
			Feedback feedback
	) {
		super(view);
		this.labelService = labelService;
		this.popup = popup;
		this.itemProvider = itemProvider;
		this.welcomeProvider = welcomeProvider;
		this.controller = controller;
		this.feedback = feedback;
		view.setPresenter(this);
	}

	/**
	 * Handles a request by the view or user to refresh the list
	 * of project items.
	 */
	public void onRequestRefresh() {
		labelService.getProjectsItems("", new AsyncCallback<ProjectItem[]>() {

			@Override
			public void onFailure(Throwable caught) {
				fireEvent(new SystemFailureEvent("Error communicating with the server. Refresh to try again."));
			}

			@Override
			public void onSuccess(ProjectItem[] result) {
				if (result.length == 0) {
					getView().clearItems();
					getView().showEmptyItemsList();
					// TODO: Show the welcome message only to the first time user.
					showWelcomeMessage();
				} else {
					showContentItems(result);
				}
			}
			
		});
	}
	
	private void showWelcomeMessage() {
		getView().add(welcomeProvider.get());
	}
	
	private void showContentItems(ProjectItem[] items) {
		getView().clearItems();
		for (ProjectItem item : items) {
			ProjectItemPresenter itemPresenter = itemProvider.get();
			itemPresenter.setProjectsPresenter(ProjectsPresenter.this);
			itemPresenter.setItemLocation(item.getLocation());
			itemPresenter.setItemName(item.getName());
			itemPresenter.setItemLastUpdated(item.getLastUpdated());
			itemPresenter.setItemType(item.getType());
			
			getView().addItem(itemPresenter);
		}
	}

	/**
	 * Handles a request to navigate to an existing label.
	 * 
	 * @param item the component for the label to navigate to
	 */
	public void onRequestNavigate(final ProjectItemPresenter item) {
		feedback.display("Loading " + item.getItemName(), true);
		labelService.getContainerForLocation(item.getItemLocation(), new AsyncCallback<Container>() {

			@Override
			public void onFailure(Throwable caught) {
				feedback.hide();
				fireEvent(new SystemFailureEvent("Error communicating with the server. Refresh to try again."));
			}

			@Override
			public void onSuccess(final Container container) {
				controller.loadEditorPage(null, null);
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					@Override
					public void execute() {
						fireEvent(new LabelChangedEvent(container));
						fireEvent(new RootContainerChangedEvent(container));
						fireEvent(new TemplateNameChangedEvent(item.getItemName()));
						feedback.hide();
					}
				});
			}
		});
	}

	/**
	 * Handles a request to delete an existing label.
	 * 
	 * @param item the component for the label to delete
	 */
	public void onRequestDelete(final ProjectItemPresenter item) {
		ClickHandler confirmDelete = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				labelService.deleteProjectItem(item.getItemLocation(), new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						popup.hide();
						fireEvent(new SystemFailureEvent(caught.getMessage()));
					}

					@Override
					public void onSuccess(Void result) {
						popup.hide();
						onRequestRefresh();
					}
				});
			}
		};
		ClickHandler cancelDelete = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				popup.hide();
			}
		};
		popup.showConfirmationBox(
				"Delete Label?",
				"Are you sure you want to delete the label " + item.getItemName() + "?",
				confirmDelete,
				cancelDelete
		);
	}
	
}
