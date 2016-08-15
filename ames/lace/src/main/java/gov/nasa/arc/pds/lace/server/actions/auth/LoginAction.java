package gov.nasa.arc.pds.lace.server.actions.auth;

import gov.nasa.arc.pds.lace.server.BaseAction;
import gov.nasa.arc.pds.lace.server.SessionConstants;
import gov.nasa.arc.pds.lace.server.oauth.GoogleProvider;
import gov.nasa.arc.pds.lace.server.oauth.LinkedInProvider;
import gov.nasa.arc.pds.lace.server.oauth.OAuthProvider;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

@SuppressWarnings("serial")
@Results({
	@Result(name=LoginAction.OAUTH_LOGIN, location="${authorizationURL}", type="redirect")
})
public class LoginAction extends BaseAction {

	// Default scope so they can be used in "Results" annotations.
	static final String OAUTH_LOGIN = "oauth-login";

	private String oauthProvider;
	private String authorizationURL;
	private String callbackURL;

	@Inject
	private void setRootURL(@Named("lace.root-url") String rootURL) {
		if (rootURL==null || rootURL.isEmpty()) {
			String requestURL = getRequest().getRequestURL().toString();
			int actionPos = requestURL.indexOf("/auth/login");
			rootURL = requestURL.substring(0, actionPos);
		}
		if (!rootURL.endsWith("/")) {
			rootURL += "/";
		}
		callbackURL = rootURL + "auth/callback";

		getSession().setAttribute(SessionConstants.OAUTH_CALLBACK, callbackURL);
	}

	@Override
	public String executeInner() throws Exception {
		if (oauthProvider == null) {
			return SUCCESS;
		}

		OAuthProvider provider = null;
		if (oauthProvider.equals("google")) {
			provider = new GoogleProvider("800350895357-uopvlsvri8g5fs34a3cte2qrucpb9ebt.apps.googleusercontent.com", "LV6_Fb5jU49QIVvM1dl2iR34");
		} else if (oauthProvider.equals("linkedin")) {
			provider = new LinkedInProvider("75i91222afhnr9", "UvMof7RynWMRYvlV");
		}

		setSessionAttribute(SessionConstants.OAUTH_PROVIDER, provider);

		OAuthClientRequest request = provider.getAuthorizationRequest(callbackURL);
        authorizationURL = request.getLocationUri();

        return OAUTH_LOGIN;
	}

	public void setOauthProvider(String provider) {
		oauthProvider = provider;
	}

	public String getAuthorizationURL() {
		return authorizationURL;
	}

}
