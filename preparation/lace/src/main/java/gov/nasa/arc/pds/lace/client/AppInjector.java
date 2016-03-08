package gov.nasa.arc.pds.lace.client;

import gov.nasa.arc.pds.lace.client.presenter.ContainerPresenter;
import gov.nasa.arc.pds.lace.client.presenter.EditorPresenter;
import gov.nasa.arc.pds.lace.client.presenter.InsertionPointPresenter;
import gov.nasa.arc.pds.lace.client.presenter.PageHeaderPresenter;
import gov.nasa.arc.pds.lace.client.presenter.SimpleItemPresenter;
import gov.nasa.arc.pds.lace.client.presenter.TreePresenter;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;

/**
 * Defines a Gin injector for the application.
 */
@GinModules(AppModule.class)
public interface AppInjector extends Ginjector {

	/**
	 * Gets an instance of the event bus to use.
	 *
	 * @return an event bus
	 */
	EventBus getEventBus();

	/**
	 * Gets an instance of the tree presenter.
	 *
	 * @return a tree presenter
	 */
	TreePresenter getTreePresenter();

	/**
	 * Gets an instance of the container presenter.
	 *
	 * @return a container presenter
	 */
	ContainerPresenter getContainerPresenter();

	/**
	 * Gets an instance of the editor presenter.
	 *
	 * @return an instance of the editor presenter
	 */
	EditorPresenter getEditorPresenter();

	/**
	 * Gets an instance of the simple item presenter.
	 *
	 * @return an instance of the simple item presenter
	 */
	SimpleItemPresenter getSimpleItemPresenter();

	/**
	 * Gets an instance of the insertion point presenter.
	 *
	 * @return an instance of the insertion point presenter
	 */
	InsertionPointPresenter getInsertionPointPresenter();

	/**
	 * Gets an instance of the page header presenter.
	 *
	 * @return an instance of the page header presenter
	 */
	PageHeaderPresenter getPageHeaderPresenter();

}
