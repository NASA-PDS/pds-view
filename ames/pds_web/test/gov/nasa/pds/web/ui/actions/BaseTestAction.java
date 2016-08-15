package gov.nasa.pds.web.ui.actions;

import gov.nasa.arc.pds.tools.util.StrUtils;
import gov.nasa.pds.web.BaseTestCase;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletContextEvent;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.Dispatcher;
import org.apache.struts2.tiles.StrutsTilesListener;
import org.apache.tiles.impl.BasicTilesContainer;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockServletConfig;
import org.springframework.mock.web.MockServletContext;
//import org.springframework.web.context.WebApplicationContext;
//import org.springframework.web.context.support.XmlWebApplicationContext;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.ActionProxyFactory;

@SuppressWarnings("nls")
public abstract class BaseTestAction extends BaseTestCase {

	private static Dispatcher dispatcher;
	protected ActionProxy proxy;
	protected static MockServletContext servletContext;
	protected static MockServletConfig servletConfig;
	protected MockHttpServletRequest request;
	protected MockHttpServletResponse response;

	private static boolean useStaticSetup = true;

	public static MockHttpSession sessionMap;

	static {
		final String property = System.getProperty("staticTestInit");
		if (property != null && !property.equals("")) {
			useStaticSetup = Boolean.parseBoolean(property);
		}

		if (useStaticSetup) {
			createTestContainer();
		}
	}

	// -------------------------------------------------------------------------

	@Override
	protected void tearDown() {
		ActionContext.setContext(null);
		clearAction();
	}

	/**
	 * Initializes the test container.
	 */
	private static void createTestContainer() {

		// ===== Struts setup:
		// Create and use a file system resource loader otherwise Tiles will not
		// find our configuration file. The default resource loader is able to
		// find struts.xml if it is in the classpath, but not tiles.xml.
		final FileSystemResourceLoader loader = new FileSystemResourceLoader();

		// spring 'classpath:' magic to find the struts.xml
		final String[] config = new String[] { "classpath:struts.xml" };

		servletContext = new MockServletContext("WebContent", loader);
		sessionMap = new MockHttpSession(servletContext);
//		final XmlWebApplicationContext appContext = new XmlWebApplicationContext();

		// Link the servlet context and the Spring context.
//		appContext.setServletContext(servletContext);
//		appContext.setConfigLocations(config);
//		appContext.refresh();
//		servletContext.setAttribute(
//				WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE,
//				appContext);

		servletContext.addInitParameter(BasicTilesContainer.DEFINITIONS_CONFIG,
				"WebContent/WEB-INF/tiles.xml");

		// Creating the tiles listener statically (not via class loader).
		final StrutsTilesListener tilesListener = new StrutsTilesListener();
		final ServletContextEvent event = new ServletContextEvent(
				servletContext);
		tilesListener.contextInitialized(event);

		// Use spring as the object factory for Struts
		/*
		 * final StrutsSpringObjectFactory ssf = new StrutsSpringObjectFactory(
		 * "auto", "true", servletContext); ObjectFactory.setObjectFactory(ssf);
		 */

		// Dispatcher is the guy that actually handles all requests. Pass in
		// an empty. Map as the parameters but if you want to change stuff like
		// what config files to read, you need to specify them here
		// (see Dispatcher's source code)
		dispatcher = new Dispatcher(servletContext,
				new HashMap<String, String>());
		dispatcher.init();
		Dispatcher.setInstance(dispatcher);
	}

	/**
	 * Created action class based on with no namespace and a matching action
	 * name to class name
	 * 
	 * @param clazz
	 *            Class for which to create Action
	 * @return Action class
	 * @throws Exception
	 *             Catch-all exception
	 */
	protected <T> T createAction(Class<T> clazz) throws Exception {
		return createAction(clazz, clazz.getSimpleName());
	}

	/**
	 * Created action class based on action name
	 * 
	 * @param clazz
	 *            Class for which to create Action
	 * @param name
	 *            Action name
	 * @return Action class
	 * @throws Exception
	 *             Catch-all exception
	 */
	protected <T> T createAction(Class<T> clazz, String name) throws Exception {
		return createAction(clazz, "", name);
	}

	/**
	 * Created action class based on namespace and name
	 * 
	 * @param clazz
	 *            Class for which to create Action
	 * @param namespace
	 *            Namespace of action
	 * @param name
	 *            Action name
	 * @return Action class
	 * @throws Exception
	 *             Catch-all exception
	 */
	@SuppressWarnings("unchecked")
	protected <T> T createAction(Class<T> clazz, String namespace, String name)
			throws Exception {

		// create a proxy class which is just a wrapper around the action call.
		// The proxy is created by checking the namespace and name against the
		// struts.xml configuration
		this.proxy = dispatcher.getContainer().getInstance(
				ActionProxyFactory.class).createActionProxy(namespace, name,
				null, true, false);

		// by default, don't pass in any request parameters
		this.proxy.getInvocation().getInvocationContext().setParameters(
				new HashMap());

		// do not execute the result after executing the action
		this.proxy.setExecuteResult(true);

		// set the actions context to the one which the proxy is using
		ActionContext.setContext(this.proxy.getInvocation()
				.getInvocationContext());
		this.request = new MockHttpServletRequest();
		this.response = new MockHttpServletResponse();
		ServletActionContext.setRequest(this.request);
		ServletActionContext.setResponse(this.response);
		ServletActionContext.setServletContext(servletContext);
		BaseAction action = (BaseAction) this.proxy.getAction();
		this.request.setSession(sessionMap);
		action.initSession();

		// HttpSession session = this.request.getSession();
		return (T) action;
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		sessionMap.clearAttributes();
	}

	protected File getTestDataDirectory() {
		final File root = new File(servletContext.getRealPath("/"))
				.getParentFile();
		final File testData = new File(root, "test-data");
		return testData;
	}

	protected abstract void clearAction();

	protected static void assertContainsError(final BaseAction action,
			final String key, final Object... arguments) {
		final String expected = action.getErrorMessage(key, arguments);

		// get errors in action without clearing
		List<String> errors = action.getErrorMessagesSimple();

		if (!errors.contains(expected)) {
			final String foundErrors = StrUtils.toString(errors);
			fail("Error message \"" + expected
					+ "\" was not found.\nThe following errors were found\n"
					+ foundErrors);
		}
	}
}
