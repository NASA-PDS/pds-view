// Copyright 2006-2010, by the California Institute of Technology.
// ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
// Any commercial use must be negotiated with the Office of Technology Transfer
// at the California Institute of Technology.
//
// This software is subject to U. S. export control laws and regulations
// (22 C.F.R. 120-130 and 15 C.F.R. 730-774). To the extent that the software
// is subject to U.S. export control laws and regulations, the recipient has
// the responsibility to obtain export licenses or other export authority as
// may be required before exporting such information to foreign countries or
// providing access to foreign nationals.
//
// $Id$
package gov.nasa.pds.harvest.security;

import javax.ws.rs.core.MultivaluedMap;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.core.util.MultivaluedMapImpl;

/**
 * Class that provides an interface to the PDS Security Service
 * 
 * @author mcayanan
 *
 */
public class SecurityClient {
	public final static String AUTHENTICATE = "authenticate";
	public final static String IS_TOKEN_VALID = "isTokenValid";
	public final static String LOGOUT = "logout";
	private WebResource securityResource;
	private String mediaType;
	
	/**
	 * Constructor
	 */
	public SecurityClient(String baseURL) {
		ClientConfig clientConfig = new DefaultClientConfig();
		securityResource = Client.create(clientConfig).resource(baseURL);
	}

	public boolean isTokenValid(String token) throws SecurityClientException {
		MultivaluedMap<String, String> params = new MultivaluedMapImpl();
		params.add("tokenid", token);
		ClientResponse response = securityResource.path(IS_TOKEN_VALID).queryParams(
				params).accept(mediaType).post(ClientResponse.class);

		if(response.getStatus() == ClientResponse.Status.OK.getStatusCode()) {
			String booleanValue = response.getEntity(String.class);
			booleanValue = booleanValue.split("=")[1].trim();
			return Boolean.parseBoolean(booleanValue);
		} else {
			throw new SecurityClientException(
					"Security service token validation request failed. HTTP Status Code received: "
					+ response.getStatus());
		}
	}
	
	public String authenticate(String username, String password) throws SecurityClientException {
		MultivaluedMap<String, String> params = new MultivaluedMapImpl();
		params.add("username", username);
		params.add("password", password);
		ClientResponse response = securityResource.path(AUTHENTICATE).queryParams(
				params).accept(mediaType).post(ClientResponse.class);
		if(response.getStatus() == ClientResponse.Status.OK.getStatusCode()) {
			String token = response.getEntity(String.class);
			token = token.split("=", 2)[1].trim();
			return token;
		} else {
			throw new SecurityClientException(
					"Security service token ID request failure. HTTP Status Code received: "
					+ response.getStatus());
		}
	}
	
	public void logout(String token) throws SecurityClientException {
		ClientResponse response = securityResource.path(LOGOUT).queryParam(
				"subjectid", token).accept(mediaType).post(ClientResponse.class);
		
		if(response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
			throw new SecurityClientException(
					"Security service logout request failure. HTTP Status Code received: "
					+ response.getStatus());
		}
	}
}
