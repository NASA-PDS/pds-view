package gov.nasa.arc.pds.lace.server.parse;

import gov.nasa.arc.pds.lace.server.schema.DefaultSchemaManagerModule;

import javax.servlet.http.HttpSession;

import com.google.inject.AbstractModule;

public class ParsingTestModule extends AbstractModule {

	@Override
	protected void configure() {
		install(new DefaultSchemaManagerModule());
		
		HttpSession session = new MockHttpSession();
		bind(HttpSession.class).toInstance(session);
	}

}
