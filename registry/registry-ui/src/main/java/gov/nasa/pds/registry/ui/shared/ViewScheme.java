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
 * The display container for a scheme service. Necessary to be able to use an object
 * representation of ClassificationScheme on the client side of GWT (with a minimum of
 * overhead).
 * 
 * @see gov.nasa.pds.registry.model.ClassificationScheme
 * 
 * @author hyunlee
 */
public class ViewScheme extends ViewRegistryObject implements IsSerializable {

	private static final long serialVersionUID = 1L;

	private boolean isInternal;
	
	// UniqueCode, EmbeddedPath, NonUniqueCode
	private String nodeType;
	
	public Boolean getIsInternal() {
		return this.isInternal;
	}
	
	public void setIsInternal(Boolean isInternal) {
		this.isInternal = isInternal;
	}
	
	public String getNodeType() {
		return this.nodeType;
	}
	
	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}
}
