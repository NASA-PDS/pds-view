// Copyright 2009, by the California Institute of Technology.
// ALL RIGHTS RESERVED. United States Government sponsorship acknowledged.
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
package gov.nasa.pds.registry.ui.shared;

import java.util.List;
import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * The display container for a service. Necessary to be able to use an object
 * representation of Product on the client side of GWT (with a minimum of
 * overhead).
 * 
 * @see gov.nasa.pds.registry.model.ServiceBidning
 * 
 * @author hyunlee
 */
public class ViewServiceBinding extends ViewRegistryObject implements IsSerializable {

	private static final long serialVersionUID = 1L;

	/**
	 * URI to access that ServiceBinding
	 * 
	 * @see gov.nasa.pds.registry.model.Service#getAccessURI
	 */
	private String accessURI;

	/**
	 * Service attribute whose value MUST be the guid of its parent service
	 */
	private String service;
	
	private List<ViewSpecificationLink> specificationLinks;
	
	private String targetBinding;

	/**
	 * Get 
	 */
	public String getAccessURI() {
		return this.accessURI;
	}

	/**
	 * Set the accessURI attribute.
	 */
	public void setAccessURI(final String accessURI) {
		this.accessURI = accessURI;
	}

	/**
	 * Get 
	 */
	public String getService() {
		return this.service;
	}

	/**
	 * Set the service attribute.
	 */
	public void setService(final String service) {
		this.service = service;
	}
	
	public List<ViewSpecificationLink> getSpecificationLinks() {
		return this.specificationLinks;
	}
	
	public void setSpecificationLinks(final List<ViewSpecificationLink> specificationLinks) {
		this.specificationLinks = specificationLinks;
	}
	
	public String getTargetBinding() {
		return this.targetBinding;
	}
	
	public void setTargetBinding(String targetBinding) {
		this.targetBinding = targetBinding;
	}
}
