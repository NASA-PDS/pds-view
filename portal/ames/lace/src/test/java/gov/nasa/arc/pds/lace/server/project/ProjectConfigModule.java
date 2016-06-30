package gov.nasa.arc.pds.lace.server.project;

import gov.nasa.arc.pds.lace.server.ServerConfiguration;

import com.google.inject.AbstractModule;

public class ProjectConfigModule extends AbstractModule {

	@Override
	protected void configure() {
		ServerConfiguration config = new ServerConfiguration();
		bind(ServerConfiguration.class).toInstance(config);
	}
	
}