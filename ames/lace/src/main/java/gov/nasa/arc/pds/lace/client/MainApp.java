package gov.nasa.arc.pds.lace.client;

import gov.nasa.arc.pds.lace.client.event.LoginStateChangeEvent;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;

/**
 * Implements the EntryPoint interface to act as a module entry point.
 * 
 */
public class MainApp implements EntryPoint {
	
	private final AppInjector injector = GWT.create(AppInjector.class);

	@SuppressWarnings("unused")
	private AppController controller;
	
	private EventBus bus;
	
	@Override	
	public void onModuleLoad() {
		// Instantiate the AppController.
		controller = injector.getAppController();
		bus = injector.getEventBus();
		bus.fireEvent(new LoginStateChangeEvent());
	}
	
}
