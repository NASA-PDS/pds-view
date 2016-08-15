package gov.nasa.arc.pds.lace.server.oauth;

import gov.nasa.arc.pds.lace.server.AuthenticationProvider;

public class GoogleProvider extends OpenIDConnectProvider {

	public GoogleProvider(String clientID, String clientSecret) {
		super(clientID, clientSecret, "https://accounts.google.com");
	}

	@Override
	protected String getIssuer() {
		return "accounts.google.com";
	}

	@Override
	public AuthenticationProvider getAuthenticationProvider() {
		return AuthenticationProvider.LINKED_IN;
	}

}