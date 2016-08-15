package gov.nasa.arc.pds.lace.client;

import gov.nasa.arc.pds.lace.client.presenter.AttributeItemPresenter;
import gov.nasa.arc.pds.lace.client.presenter.ContainerPresenter;
import gov.nasa.arc.pds.lace.client.presenter.InsertionPointPresenter;
import gov.nasa.arc.pds.lace.client.presenter.SimpleItemPresenter;

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
	 * Gets an instance of the app controller.
	 *
	 * @return an application controller
	 */
	AppController getAppController();

	/**
	 * Gets an instance of the container presenter.
	 *
	 * @return a container presenter
	 */
	ContainerPresenter getContainerPresenter();

	/**
	 * Gets an instance of the simple item presenter.
	 *
	 * @return an instance of the simple item presenter
	 */
	SimpleItemPresenter getSimpleItemPresenter();

	/**
	 * Gets an instance of the attribute presenter.
	 *
	 * @return an instance of the attribute presenter
	 */
	AttributeItemPresenter getAttributeItemPresenter();

	/**
	 * Gets an instance of the insertion point presenter.
	 *
	 * @return an instance of the insertion point presenter
	 */
	InsertionPointPresenter getInsertionPointPresenter();

}
