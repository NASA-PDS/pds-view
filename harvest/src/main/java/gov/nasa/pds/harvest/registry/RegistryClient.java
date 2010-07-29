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
package gov.nasa.pds.harvest.registry;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.ClientResponse;

import gov.nasa.pds.harvest.util.HttpUtils;
import gov.nasa.pds.harvest.util.Utility;
import gov.nasa.pds.registry.model.Association;
import gov.nasa.pds.registry.model.Product;

/**
 * Class that allows Harvest to talk to the Registry Service.
 * 
 * @author mcayanan
 *
 */
public class RegistryClient {
	private URI baseURI;
	private URI statusURI;
	private URI productsURI;
	private URI registryURI;
	private URI associationsURI;
	private boolean enableSecurity;
	private String token;
	
	public RegistryClient(String baseURI) throws URISyntaxException {
		this(baseURI, null);
	}
	
	public RegistryClient(String baseURI, String token) throws URISyntaxException {
		this.baseURI = new URI(baseURI);
		this.registryURI = new URI(this.baseURI.toString() + "/registry");
		this.token = token;
		if(this.token != null)
			enableSecurity = true;
		else
			enableSecurity = false;
		this.statusURI = new URI(this.registryURI.toString() + "/status");
		this.productsURI = new URI(this.registryURI.toString() + "/products");
		this.associationsURI = new URI(this.baseURI + "/" + "associations");
	}

	public boolean isRunning() {
		ClientResponse response = null;
		if(enableSecurity) {
			response = HttpUtils.get(statusURI, MediaType.TEXT_PLAIN_TYPE, token);
		} else {
			response = HttpUtils.get(statusURI, MediaType.TEXT_PLAIN_TYPE);
		}
		if(response.getStatus() == ClientResponse.Status.OK.getStatusCode()) {
			return true;
		} else {
			return false;
		}
	}	
	
	public boolean hasProduct(String logicalID) throws URISyntaxException {
		URI uri = new URI(Utility.toWellFormedURI(productsURI.toString() + "/" + logicalID));
		ClientResponse response = null;
		if(enableSecurity) {
			response = HttpUtils.get(uri, MediaType.TEXT_PLAIN_TYPE, token);
		} else {
			response = HttpUtils.get(uri, MediaType.TEXT_PLAIN_TYPE);
		}
		if(response.getStatus() == ClientResponse.Status.OK.getStatusCode()) {
			return true;
		} else {
			return false;
		}
	}

	public boolean hasProduct(String logicalID, String version) throws URISyntaxException {
		URI uri = new URI(Utility.toWellFormedURI(productsURI.toString() + "/" + logicalID + "/" + version));
		ClientResponse response = null;
		if(enableSecurity) {
			response = HttpUtils.get(uri, MediaType.TEXT_PLAIN_TYPE, token);
		} else {
			response = HttpUtils.get(uri, MediaType.TEXT_PLAIN_TYPE);
		}
		if(response.getStatus() == ClientResponse.Status.OK.getStatusCode()) {
			return true;
		} else {
			return false;
		}
	}
	
	public ClientResponse publishProduct(Product product) throws RegistryClientException, URISyntaxException {
		URI uri = productsURI;

		if(hasProduct(product.getLid(), product.getUserVersion())) {
			throw new RegistryClientException("Product and version already exists: " + 
					Utility.toWellFormedURI(uri.toString() + "/" + product.getLid() + "/" + product.getUserVersion()));
		}
		else if(hasProduct(product.getLid())) {
			uri = new URI(Utility.toWellFormedURI(uri.toString() + "/" + product.getLid()));
		}
		ClientResponse response = null;
		if(enableSecurity) {
			response = HttpUtils.post(uri, product, MediaType.APPLICATION_XML_TYPE, token);
		} else {
			response = HttpUtils.post(uri, product, MediaType.APPLICATION_XML_TYPE);
		}
		return response;
	}
	
	public ClientResponse publishAssociation(Association association) {
		ClientResponse response = null;
		if(enableSecurity) {
			response = HttpUtils.post(this.associationsURI, association, MediaType.APPLICATION_XML_TYPE, token);
		} else {
			response = HttpUtils.post(this.associationsURI, association, MediaType.APPLICATION_XML_TYPE);
		}
		return response;
	}
	
	public URI getBaseURI() {
		return baseURI;
	}
	
	public String getToken() {
		return token;
	}
	
	public void setToken(String token) {
		token = token;
	}
}
