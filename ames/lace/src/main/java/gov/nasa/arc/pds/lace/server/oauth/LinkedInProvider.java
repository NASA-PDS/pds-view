package gov.nasa.arc.pds.lace.server.oauth;

import gov.nasa.arc.pds.lace.server.AuthenticationProvider;

import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.common.OAuthProviderType;

public class LinkedInProvider extends OAuthProvider {

	public LinkedInProvider(String clientID, String clientSecret) {
		super(clientID, clientSecret);
	}

	@Override
	protected String getAuthorizationEndpoint() {
		return OAuthProviderType.LINKEDIN.getAuthzEndpoint();
	}

	@Override
	protected String getTokenEndpoint() {
		return OAuthProviderType.LINKEDIN.getTokenEndpoint();
	}

	@Override
	protected String getUserInfoEndpoint(String accessToken) {
		return "https://api.linkedin.com/v1/people/~:(id,first-name,last-name,formatted-name,email-address)"
				+ "?oauth2_access_token=" + accessToken;
	}

	@Override
	protected String getAuthorizationScope() {
		return "r_basicprofile r_emailaddress";
	}

	@Override
	protected void addResourceHeaders(OAuthClientRequest request, String accessToken) {
		// Ensure that the provider sends a JSON response.
		request.addHeader("x-li-format", "json");
	}

	@Override
	protected String getIssuer() {
		return "linkedin.com";
	}

	@Override
	protected String getSubjectProperty() {
		return "id";
	}

	@Override
	protected String getGivenNameProperty() {
		return "firstName";
	}

	@Override
	protected String getFamilyNameProperty() {
		return "lastName";
	}

	@Override
	protected String getFormattedNameProperty() {
		return "formattedName";
	}

	@Override
	protected String getEmailProperty() {
		return "emailAddress";
	}

	@Override
	protected boolean useBearerAuthorization() {
		return false;
	}

	@Override
	public AuthenticationProvider getAuthenticationProvider() {
		return AuthenticationProvider.LINKED_IN;
	}

}
