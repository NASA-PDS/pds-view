package gov.nasa.arc.pds.lace.server.oauth;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.oltu.oauth2.client.request.OAuthClientRequest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class OpenIDConnectProvider extends OAuthProvider {

	/*
	PAYPAL(OAuthProviderType.PAYPAL, "https://api.paypal.com/v1/identity/openidconnect/userinfo"),

	LINKEDIN(OAuthProviderType.LINKEDIN, "http://api.linkedin.com/v1/people");
*/
	private static final String CONFIGURATION_PATH = "/.well-known/openid-configuration";

	private String authorizationEndpoint;
	private String tokenEndpoint;
	private String userInfoEndpoint;

	protected OpenIDConnectProvider(String clientID, String clientSecret, String providerBaseURL) {
		super(clientID, clientSecret);
		getEndpoints(providerBaseURL);
	}

	private void getEndpoints(String providerBaseURL) {
		try {
			URL configurationURL = new URL(providerBaseURL + CONFIGURATION_PATH);
			InputStream in = configurationURL.openStream();
			ObjectMapper mapper = new ObjectMapper();
			JsonNode root = mapper.readTree(in);
			authorizationEndpoint = root.get("authorization_endpoint").asText();
			tokenEndpoint = root.get("token_endpoint").asText();
			userInfoEndpoint = root.get("userinfo_endpoint").asText();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public String getAuthorizationEndpoint() {
		return authorizationEndpoint;
	}

	@Override
	public String getTokenEndpoint() {
		return tokenEndpoint;
	}

	@Override
	public String getUserInfoEndpoint(String accessToken) {
		return userInfoEndpoint;
	}

	@Override
	protected String getAuthorizationScope() {
		return "openid profile email";
	}

	@Override
	protected void addResourceHeaders(OAuthClientRequest request, String accessToken) {
		// No special headers needed.
	}

	@Override
	protected String getGivenNameProperty() {
		return "given_name";
	}

	@Override
	protected String getFamilyNameProperty() {
		return "family_name";
	}

	@Override
	protected String getFormattedNameProperty() {
		return "name";
	}

	@Override
	protected String getEmailProperty() {
		return "email";
	}

	@Override
	protected String getSubjectProperty() {
		return "sub";
	}

}
