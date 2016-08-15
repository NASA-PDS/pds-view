package gov.nasa.arc.pds.lace.server.actions;

import gov.nasa.arc.pds.lace.server.AuthenticationProvider;
import gov.nasa.arc.pds.lace.server.BaseAction;
import gov.nasa.arc.pds.lace.server.SessionConstants;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements an action that allows the user to logout from the application
 */
@SuppressWarnings("serial")
@Results({
	@Result(name=LogoutAction.LOG_OUT, location="auth/login", type="redirectAction"),
})
public class LogoutAction extends BaseAction {

	private static final Logger LOG = LoggerFactory.getLogger(LogoutAction.class);

	static final String LOG_OUT = "logout";

	private String rootURL;

	@Inject
	private void setRootUrl(@Named("lace.root-url") String rootURL) {
		if (rootURL==null || rootURL.isEmpty()) {
			String requestURL = getRequest().getRequestURL().toString();
			int actionPos = requestURL.indexOf("/logout");
			rootURL = requestURL.substring(0, actionPos);
		}
		if (!rootURL.endsWith("/")) {
			rootURL += "/";
		}
		this.rootURL = rootURL;
	}

	/**
	 * Gets the root URL for LACE.
	 *
	 * @return the root URL for LACE
	 */
	public String getRootUrl() {
		return rootURL;
	}

	@Override
	public String executeInner() throws Exception {
		LOG.info("Logging out user '{}' in session '{}'",
				getSession().getAttribute(SessionConstants.USER_LOGIN_ID_PROPERTY),
				getSession().getId());

		AuthenticationProvider authProvider = (AuthenticationProvider) getSession().getAttribute(SessionConstants.AUTHENTICATION_PROVIDER);
		if (authProvider == AuthenticationProvider.NDC) {
			// There is no way to log out of HTTP basic authentication, so tell the
			// user to close the browser.
			return SUCCESS;
		}

		getSession().removeAttribute(SessionConstants.USER_LOGIN_ID_PROPERTY);

		//invalidate session and unbind any bounded objects
		getSession().invalidate();

		return LOG_OUT;
	}
}
