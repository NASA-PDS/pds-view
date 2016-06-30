package gov.nasa.arc.pds.lace.client;

import javax.inject.Singleton;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.inject.client.AbstractGinModule;

/**
 * Implements the Gin module configuration for
 * the application as a collection of bindings.
 * Other bindings are implemented as annotations
 * on classes. For example, <code>Singleton</code>
 * or <code>ImplementedBy</code>
 */
public class AppModule extends AbstractGinModule {

	@Override
	protected void configure() {
		/*
		 * The singleton event bus for the application.
		 *
		 * The binding tells Gin that whenever it sees a dependency on an EventBus,
		 * it should satisfy the dependency using a SimpleEventBus.
		 */
		bind(EventBus.class).to(SimpleEventBus.class).in(Singleton.class);		
	}

}
