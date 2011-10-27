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
 * representation of Service on the client side of GWT (with a minimum of
 * overhead).
 * 
 * @see gov.nasa.pds.registry.model.Service
 * 
 * @author hyunlee
 */
public class ViewService extends ViewRegistryObject implements IsSerializable {

	private static final long serialVersionUID = 1L;

	private List<ViewServiceBinding> serviceBindings;
	
	public void setServiceBindings(final List<ViewServiceBinding> serviceBindings) {
		this.serviceBindings = serviceBindings;
	}
	
	public List<ViewServiceBinding> getServiceBindings() {
		return this.serviceBindings; 
	}
	
	
}
