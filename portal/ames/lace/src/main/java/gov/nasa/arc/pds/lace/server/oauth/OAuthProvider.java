package gov.nasa.arc.pds.lace.server.oauth;

import gov.nasa.arc.pds.lace.server.AuthenticationProvider;

import java.io.IOException;
import java.util.UUID;

import org.apache.oltu.oauth2.client.request.OAuthBearerClientRequest;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest.AuthenticationRequestBuilder;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest.TokenRequestBuilder;
import org.apache.oltu.oauth2.client.response.OAuthResourceResponse;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class OAuthProvider {

	private String clientID;
	private String clientSecret;
	private String state;

	protected OAuthProvider(
			String clientID,
			String clientSecret
	) {
		this.clientID = clientID;
		this.clientSecret = clientSecret;

		// Initialize the state to a random string.
		state = UUID.randomUUID().toString();
	}

	public OAuthClientRequest getAuthorizationRequest(String redirectURI) throws OAuthSystemException {
		AuthenticationRequestBuilder builder =
        		OAuthClientRequest.authorizationLocation(getAuthorizationEndpoint());
        builder.setClientId(clientID);
        builder.setResponseType("code");
        builder.setScope(getAuthorizationScope());
        builder.setRedirectURI(redirectURI);
        builder.setState(state);
        return builder.buildQueryMessage();
	}

	public OAuthClientRequest getTokenRequest(String code, String redirectURI) throws OAuthSystemException {
		TokenRequestBuilder builder =
				OAuthClientRequest.tokenLocation(getTokenEndpoint());
		builder.setCode(code);
		builder.setClientId(clientID);
		builder.setClientSecret(clientSecret);
		builder.setRedirectURI(redirectURI);
		builder.setGrantType(GrantType.AUTHORIZATION_CODE);
		return builder.buildBodyMessage();
	}

	public OAuthClientRequest getUserInfoRequest(String accessToken) throws OAuthSystemException {
		OAuthBearerClientRequest builder = new OAuthBearerClientRequest(getUserInfoEndpoint(accessToken));
		builder.setAccessToken(accessToken);
		OAuthClientRequest request =
				useBearerAuthorization() ? builder.buildHeaderMessage() : builder.buildQueryMessage();
		addResourceHeaders(request, accessToken);
		return request;
	}

	public OAuthUserInfo parseUserInfo(OAuthResourceResponse response) throws JsonProcessingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode root = mapper.readTree(response.getBody());

		OAuthUserInfo info = new OAuthUserInfo();
		info.setIssuer(getIssuer());
		info.setSubject(root.get(getSubjectProperty()).asText());
		info.setGivenName(root.get(getGivenNameProperty()).asText());
		info.setFamilyName(root.get(getFamilyNameProperty()).asText());
		info.setFormattedName(root.get(getFormattedNameProperty()).asText());
		info.setEmail(root.get(getEmailProperty()).asText());
		return info;
	}

	protected boolean useBearerAuthorization() {
		return true;
	}

	protected abstract String getIssuer();

	protected abstract String getAuthorizationEndpoint();

	protected abstract String getTokenEndpoint();

	protected abstract String getUserInfoEndpoint(String accessToken);

	protected abstract String getAuthorizationScope();

	protected abstract void addResourceHeaders(OAuthClientRequest request, String accessToken);

	protected abstract String getSubjectProperty();

	protected abstract String getGivenNameProperty();

	protected abstract String getFamilyNameProperty();

	protected abstract String getFormattedNameProperty();

	protected abstract String getEmailProperty();

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public abstract AuthenticationProvider getAuthenticationProvider();

}
