package gov.nasa.arc.pds.lace.client.presenter;

import gov.nasa.arc.pds.lace.client.view.ProjectItemView;
import gov.nasa.arc.pds.lace.shared.project.ProjectItem;
import gov.nasa.arc.pds.lace.shared.project.ProjectItem.Type;

import java.util.Date;

import javax.inject.Inject;

import com.google.inject.ImplementedBy;

/**
 * Implements a component that displays the information for a single
 * project item in the list of projects.
 */
public class ProjectItemPresenter extends Presenter<ProjectItemPresenter.Display> {

	/**
	 * Defines the interface a vew must implement.
	 */
	@ImplementedBy(ProjectItemView.class)
	public interface Display extends Presenter.Display<ProjectItemPresenter> {

		/**
		 * Sets the item name.
		 * 
		 * @param name the new name
		 */
		void setItemName(String name);

		/**
		 * Sets the item type.
		 * 
		 * @param type the new item type
		 */
		void setItemType(Type type);

		/**
		 * Sets the item last updated date-time.
		 * 
		 * @param lastUpdated the last updated date-time
		 */
		void setLastUpdated(Date lastUpdated);
		
	}

	private String location;
	private ProjectsPresenter projectsPresenter;
	private String name;

	/**
	 * Creates a new instance with a given view.
	 * 
	 * @param view the view to use for user interaction and display
	 */
	@Inject
	public ProjectItemPresenter(Display view) {
		super(view);
		view.setPresenter(this);
	}
	
	/**
	 * Sets the component that manages the project list. This component will
	 * handle navigation and deletion requests.
	 * 
	 * @param projectsPresenter the projects presenter
	 */
	public void setProjectsPresenter(ProjectsPresenter projectsPresenter) {
		this.projectsPresenter = projectsPresenter;
	}
	
	/**
	 * Sets the location of the item.
	 * 
	 * @param newlocation the item location
	 */
	public void setItemLocation(String newlocation) {
		this.location = newlocation;
	}
	
	/**
	 * Gets the location of the item.
	 * 
	 * @return the item location
	 */
	public String getItemLocation() {
		return location;
	}
	
	/**
	 * Gets the item name.
	 * 
	 * @return the item name
	 */
	public String getItemName() {
		return name;
	}
	
	/**
	 * Sets the item name.
	 * 
	 * @param name the new name
	 */
	public void setItemName(String name) {
		this.name = name;
		getView().setItemName(name);
	}
	
	/**
	 * Sets the item type.
	 * 
	 * @param type the new item type
	 */
	public void setItemType(ProjectItem.Type type) {
		getView().setItemType(type);
	}
	
	/**
	 * Sets the item last updated date-time.
	 * 
	 * @param lastUpdated the last updated date-time
	 */
	public void setItemLastUpdated(Date lastUpdated) {
		getView().setLastUpdated(lastUpdated);
	}

	/**
	 * Handle a request by the user to delete a project item.
	 */
	public void onRequestDelete() {
		projectsPresenter.onRequestDelete(this);
	}

	public void onRequestNavigate() {
		projectsPresenter.onRequestNavigate(this);
	}

}
