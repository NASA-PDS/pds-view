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

import java.io.Serializable;
import java.util.List;

/**
 * The display container for specification link. Necessary to be able to use an
 * object representation of SpecificationLink on the client side of GWT (with a minimum of
 * overhead).
 * 
 * @see gov.nasa.pds.registry.model.SpecificationLink
 * 
 * @author hyunlee
 */
public class ViewSpecificationLink extends ViewRegistryObject implements Serializable {

	private static final long serialVersionUID = 1L;

	private String serviceBinding;
	
	private String specificationObject;
	
	private String usageDescription;
	
	private List<String> usageParameters;

	public String getServiceBinding() {
		return this.serviceBinding;
	}

	public void setServiceBinding(String serviceBinding) {
		this.serviceBinding = serviceBinding;
	}
	
	public String getSpecificationObject() {
		return this.specificationObject;
	}
	
	public void setSpecificationObject(String specObj) {
		this.specificationObject = specObj;
	}
	
	public String getUsageDescription() {
		return this.usageDescription;
	}
	
	public void setUsageDescription(String usageDesc) {
		this.usageDescription = usageDesc;
	}
	
	/**
	 * Get 
	 */
	public List<String> getUsageParameters() {
		return this.usageParameters;
	}

	/**
	 * 
	 */
	public void setUsageParameters(List<String> usageParams) {
		this.usageParameters = usageParams;
	}
}
