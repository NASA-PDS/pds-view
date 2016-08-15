package gov.nasa.arc.pds.lace.server.actions.auth;

import gov.nasa.arc.pds.lace.server.BaseAction;
import gov.nasa.arc.pds.lace.server.LabelContentsServiceImpl;
import gov.nasa.arc.pds.lace.server.SessionConstants;
import gov.nasa.arc.pds.lace.server.oauth.OAuthProvider;
import gov.nasa.arc.pds.lace.server.oauth.OAuthUserInfo;
import gov.nasa.arc.pds.lace.server.project.ProjectManager;
import gov.nasa.arc.pds.lace.server.project.ProjectManager.UserRegistrationState;

import javax.inject.Inject;
import javax.inject.Provider;

import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.client.response.OAuthResourceResponse;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements an action used to finish the authentication after
 * a redirect from the OAuth provider.
 */
@SuppressWarnings("serial")
@Results({
		@Result(name=CallbackAction.OAUTH_ERROR, location="login", type="redirectAction"),
		@Result(name=CallbackAction.REGISTER, location="register", type="redirectAction"),
		@Result(name=CallbackAction.MAIN_PAGE, location="/", type="redirect"),
		@Result(name=CallbackAction.REQUESTED_PAGE, location="${requestedURI}", type="redirect")
})
public class CallbackAction extends BaseAction {

	private static final Logger LOG = LoggerFactory.getLogger(CallbackAction.class);

	static final String REGISTER = "register";
	static final String MAIN_PAGE = "main-page";
	static final String REQUESTED_PAGE = SUCCESS;
	static final String OAUTH_ERROR = ERROR;

	private String code;
	private String requestedURI;
	private Provider<ProjectManager> projectManagerProvider;
	private Provider<LabelContentsServiceImpl> serviceProvider;
	private OAuthUserInfo userInfo;

	/**
	 * Creates a new instance of the action.
	 *
	 * @param projectManagerProvider a provider for an instance of the project manager
	 * @param serviceProvider a provider for the label RPC service implementation
	 */
	@Inject
	public CallbackAction(
			Provider<ProjectManager> projectManagerProvider,
			Provider<LabelContentsServiceImpl> serviceProvider
	) {
		this.projectManagerProvider = projectManagerProvider;
		this.serviceProvider = serviceProvider;
	}

	@Override
	public String executeInner() throws Exception {
		LOG.debug("CallbackAction: in executeInner");

		OAuthProvider provider = getSessionAttribute(SessionConstants.OAUTH_PROVIDER, OAuthProvider.class);
		String callbackURL = (String) getSession().getAttribute(SessionConstants.OAUTH_CALLBACK);
		OAuthClient client = new OAuthClient(new URLConnectionClient());

		String accessToken = null;
		try {
			OAuthClientRequest tokenRequest = provider.getTokenRequest(code, callbackURL);
			OAuthJSONAccessTokenResponse response = client.accessToken(tokenRequest, OAuthJSONAccessTokenResponse.class);
			accessToken = response.getAccessToken();
		} catch (OAuthSystemException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (OAuthProblemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			OAuthClientRequest infoRequest = provider.getUserInfoRequest(accessToken);
			OAuthResourceResponse response = client.resource(infoRequest, "GET", OAuthResourceResponse.class);
			userInfo = provider.parseUserInfo(response);
		} catch (OAuthSystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OAuthProblemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		getSession().setAttribute(SessionConstants.USER_LOGIN_ID_PROPERTY, userInfo.getEmail());
		getSession().setAttribute(SessionConstants.AUTHENTICATION_PROVIDER, provider.getAuthenticationProvider());

		setSessionAttribute(SessionConstants.USER_INFO_PROPERTY, userInfo);

		ProjectManager manager = projectManagerProvider.get();
		UserRegistrationState state = manager.getUserRegistrationState(userInfo.getEmail());
		if (state != UserRegistrationState.APPROVED) {
			LOG.debug("CallbackAction: redirecting to registration page");
			return REGISTER;
		}

		// All logged in. Tell the service the user's ID.
		LabelContentsServiceImpl service = serviceProvider.get();
		service.setUser((String) getSession().getAttribute(SessionConstants.USER_LOGIN_ID_PROPERTY));

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
	 * Sets the encrypted authentication code returned from the OAuth provider.
	 *
	 * @param code the authentication code
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * Gets the URI that was requested when it was detected that the user
	 * must authenticate.
	 *
	 * @return the requested URI
	 */
	public String getRequestedURI() {
		return requestedURI;
	}

}
