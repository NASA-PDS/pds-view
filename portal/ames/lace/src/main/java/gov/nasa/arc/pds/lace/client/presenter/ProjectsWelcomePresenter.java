package gov.nasa.arc.pds.lace.client.presenter;

import gov.nasa.arc.pds.lace.client.view.ProjectsWelcomeView;

import javax.inject.Inject;

import com.google.inject.ImplementedBy;

/**
 * Implements a component that displays a welcome screen in the
 * project manager.
 */
public class ProjectsWelcomePresenter extends Presenter<ProjectsWelcomePresenter.Display> {

	/**
	 * Defines the interface a vew must implement.
	 */
	@ImplementedBy(ProjectsWelcomeView.class)
	public interface Display extends Presenter.Display<ProjectsWelcomePresenter> {
		
		/**
		 * Closes the welcome box.
		 */
		void closeBox();
	}

	/**
	 * Creates a new instance with a given view.
	 * 
	 * @param view the view to use for user interaction and display
	 */
	@Inject
	public ProjectsWelcomePresenter(Display view) {
		super(view);
		view.setPresenter(this);
	}
	
	/**
	 * Handles a request to close the welcome box.
	 */
	public void onRequestClose() {
		getView().closeBox();
	}
	
}
