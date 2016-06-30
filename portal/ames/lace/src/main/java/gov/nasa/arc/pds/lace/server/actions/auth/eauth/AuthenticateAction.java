package gov.nasa.arc.pds.lace.server.actions.auth.eauth;

import gov.nasa.arc.pds.lace.server.AuthenticationProvider;
import gov.nasa.arc.pds.lace.server.BaseAction;
import gov.nasa.arc.pds.lace.server.LabelContentsServiceImpl;
import gov.nasa.arc.pds.lace.server.SessionConstants;
import gov.nasa.arc.pds.lace.server.project.ProjectManager;
import gov.nasa.arc.pds.lace.server.project.ProjectManager.UserRegistrationState;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.inject.Inject;
import javax.inject.Provider;

import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements an action that responds to a successful eAuth (IDMax/Launchpad)
 * authentication by a user.
 */
@SuppressWarnings("serial")
@Results({
	@Result(name=AuthenticateAction.AUTH_ERROR, location="/login", type="redirect"),
	@Result(name=AuthenticateAction.MAIN_PAGE, location="/", type="redirect"),
	@Result(name=AuthenticateAction.REQUESTED_PAGE, location="${requestedURI}", type="redirect")
})
public class AuthenticateAction extends BaseAction {

	static final String MAIN_PAGE = "main-page";
	static final String REQUESTED_PAGE = SUCCESS;
	static final String AUTH_ERROR = ERROR;

	private static final Logger LOG = LoggerFactory.getLogger(AuthenticateAction.class);

	private Provider<ProjectManager> projectManagerProvider;
	private Provider<LabelContentsServiceImpl> serviceProvider;

	private String remoteUser;
	private String emailAddr;
	private String requestedURI;

	private Map<String, String> headers = new TreeMap<String, String>();

	/**
	 * Creates a new instance of the action.
	 *
	 * @param projectManagerProvider a provider for an instance of the project manager
	 * @param serviceProvider the RPC service implementation to use
	 */
	@Inject
	AuthenticateAction(
			Provider<ProjectManager> projectManagerProvider,
			Provider<LabelContentsServiceImpl> serviceProvider
	) {
		this.projectManagerProvider = projectManagerProvider;
		this.serviceProvider = serviceProvider;
	}

	/**
	 * Gets the user ID from an HTTP header and log in the user.
	 */
	@Override
	public String executeInner() throws Exception {
		remoteUser = getRequest().getHeader("remote_user");
		if (remoteUser==null || remoteUser.trim().isEmpty()) {
			return AUTH_ERROR;
		}

		emailAddr = getRequest().getHeader("primaryemail");
		if (emailAddr==null || emailAddr.trim().isEmpty()) {
			return AUTH_ERROR;
		}
		LOG.debug("Successful eAuth login, remote_user='{}'", remoteUser);

		String userID = emailAddr;

		// Create the user, if necessary, in "approved" state.
		ProjectManager projectManager = projectManagerProvider.get();
		projectManager.createUserIfNeccesary(userID);
		projectManager.setUserRegistrationState(userID, UserRegistrationState.APPROVED);

		Map<String, String> properties = new HashMap<String, String>();
		properties.put("source", "EAUTH");
		properties.put("agencyuid", remoteUser);
		projectManager.setUserProperties(userID, properties);

		getSession().setAttribute(SessionConstants.USER_LOGIN_ID_PROPERTY, userID);
		getSession().setAttribute(SessionConstants.AUTHENTICATION_PROVIDER, AuthenticationProvider.IDMAX);

		// All logged in. Tell the service the user's ID.
		LabelContentsServiceImpl service = serviceProvider.get();
		service.setUser(userID);

		requestedURI = (String) getSession().getAttribute(SessionConstants.REQUESTED_URI);
		if (requestedURI == null) {
			LOG.debug("CallbackAction: redirecting to main page");
			return MAIN_PAGE;
		} else {
			LOG.debug("CallbackAction: redirecting to requested page: {}", requestedURI);
			return REQUESTED_PAGE;
		}
	}

	/**
	 * Gets the authenticated user ID.
	 *
	 * @return the user ID
	 */
	public String getRemoteUser() {
		return remoteUser;
	}

	public Collection<String> getHeaderNames() {
		return headers.keySet();
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public String getRequestedURI() {
		return requestedURI;
	}

}
