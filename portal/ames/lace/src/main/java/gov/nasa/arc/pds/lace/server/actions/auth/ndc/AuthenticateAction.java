package gov.nasa.arc.pds.lace.server.actions.auth.ndc;

import gov.nasa.arc.pds.lace.server.AuthenticationProvider;
import gov.nasa.arc.pds.lace.server.BaseAction;
import gov.nasa.arc.pds.lace.server.LabelContentsServiceImpl;
import gov.nasa.arc.pds.lace.server.SessionConstants;
import gov.nasa.arc.pds.lace.server.project.ProjectManager;
import gov.nasa.arc.pds.lace.server.project.ProjectManager.UserRegistrationState;

import javax.inject.Inject;
import javax.inject.Provider;

import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements an action that responds to a successful NDC
 * authentication by a user.
 */
@SuppressWarnings("serial")
@Results({
	@Result(name=AuthenticateAction.AUTH_ERROR, type="httpheader", params={"status", "401"}),
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
	private String requestedURI;

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
		remoteUser = getRequest().getHeader("REMOTE_USER");
		LOG.debug("Successful NDC login, REMOTE_USER='{}'", remoteUser);

		if (remoteUser==null || remoteUser.trim().isEmpty() || remoteUser.trim().equals("(null)")) {
			return AUTH_ERROR;
		}

		String userID = remoteUser + "%NDC";

		LOG.info("Logging in user '{}' in session '{}'",
				getSession().getAttribute(SessionConstants.USER_LOGIN_ID_PROPERTY),
				getSession().getId());

		// Create the user, if necessary, in "approved" state.
		ProjectManager projectManager = projectManagerProvider.get();
		projectManager.createUserIfNeccesary(userID);
		projectManager.setUserRegistrationState(userID, UserRegistrationState.APPROVED);

		getSession().setAttribute(SessionConstants.USER_LOGIN_ID_PROPERTY, userID);
		getSession().setAttribute(SessionConstants.AUTHENTICATION_PROVIDER, AuthenticationProvider.NDC);

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

	public String getRequestedURI() {
		return requestedURI;
	}

}
