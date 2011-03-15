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

import javax.ws.rs.core.MediaType;

import org.apache.commons.httpclient.NameValuePair;

import com.sun.jersey.api.client.ClientResponse;

import gov.nasa.pds.harvest.util.HttpUtils;
import gov.nasa.pds.registry.model.Association;
import gov.nasa.pds.registry.model.ExtrinsicObject;

/**
 * Class that allows Harvest to talk to the Registry Service.
 *
 * @author mcayanan
 *
 */
public class RegistryClient {
    private String baseURI;
    private String productsURI;
    private String registryURI;
    private String associationsURI;
    private boolean enableSecurity;
    private String token;

    public RegistryClient(String baseURI) {
        this(baseURI, null);
    }

    public RegistryClient(String baseURI, String token) {
        this.baseURI = baseURI;
        this.registryURI = this.baseURI.toString() + "/registry";
        this.token = token;
        if(this.token != null)
            enableSecurity = true;
        else
            enableSecurity = false;
        this.productsURI = this.registryURI.toString() + "/products";
        this.associationsURI = this.registryURI.toString() + "/" + "associations";
    }

    public ClientResponse getLatestProduct(String lid) {
        String uri = productsURI + "/" + lid;
        ClientResponse response = null;
        if(enableSecurity) {
            response = HttpUtils.get(uri, MediaType.TEXT_PLAIN_TYPE, token);
        } else {
            response = HttpUtils.get(uri, MediaType.TEXT_PLAIN_TYPE);
        }
        return response;
    }

    public ClientResponse getProduct(String logicalID, String version) {
        String uri = productsURI + "/" + logicalID + "/" + version;
        ClientResponse response = null;
        if(enableSecurity) {
            response = HttpUtils.get(uri, MediaType.TEXT_PLAIN_TYPE, token);
        } else {
            response = HttpUtils.get(uri, MediaType.TEXT_PLAIN_TYPE);
        }
        return response;
    }

    public ClientResponse versionProduct(String user, ExtrinsicObject product,
        String lid) {
        return this.versionProduct(user, product, lid, true);
      }

    public ClientResponse versionProduct(String user, ExtrinsicObject product,
            String lid, Boolean major) {
        String uri = productsURI + "/" + lid;
        NameValuePair param = new NameValuePair();
        param.setName("major");
        param.setValue(major.toString());
        ClientResponse response = null;
        if(enableSecurity) {
            response = HttpUtils.post(uri, product, param,
                    MediaType.APPLICATION_XML_TYPE, token);
        } else {
            response = HttpUtils.post(uri, product, param,
                    MediaType.APPLICATION_XML_TYPE);
        }
        return response;
    }

    public ClientResponse publishProduct(String user,
        ExtrinsicObject product) {
        ClientResponse response = null;
        if(enableSecurity) {
            response = HttpUtils.post(productsURI, product,
                    MediaType.APPLICATION_XML_TYPE, token);
        } else {
            response = HttpUtils.post(productsURI, product,
                    MediaType.APPLICATION_XML_TYPE);
        }
        return response;
    }

    public ClientResponse publishAssociation(String user,
            Association association) {
        ClientResponse response = null;
        if(enableSecurity) {
            response = HttpUtils.post(this.associationsURI, association,
                    MediaType.APPLICATION_XML_TYPE, token);
        } else {
            response = HttpUtils.post(this.associationsURI, association,
                    MediaType.APPLICATION_XML_TYPE);
        }
        return response;
    }
}
