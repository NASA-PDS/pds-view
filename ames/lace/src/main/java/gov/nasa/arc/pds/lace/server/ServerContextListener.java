package gov.nasa.arc.pds.lace.server;

import org.apache.struts2.dispatcher.ng.filter.StrutsPrepareAndExecuteFilter;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;

/**
 * Implements a module for configuring servlets through Guice.
 */
public class ServerContextListener extends GuiceServletContextListener {

	@Override
	protected Injector getInjector() {
		Injector injector = Guice.createInjector(new ServletModule() {
			@Override
			protected void configureServlets() {
				install(new ServerModule(getServletContext().getRealPath("WEB-INF/data")));

				// The authentication filter.
				filter ("/*").through(new AuthenticationFilter());

				// The Struts filter.
				filter("*.action", "/auth/*").through(new StrutsPrepareAndExecuteFilter());

				// Our servlets, including GWT RPCs.
				serve("/designer/labelContentsService").with(LabelContentsServiceImpl.class);
				serve("/designer/fileUpload").with(FileUploadServlet.class);
				serve("/designer/fileDownload").with(DownloadFileServlet.class);
			}
		});

		StrutsObjectFactory.setInjector(injector);
		return injector;
	}

}
