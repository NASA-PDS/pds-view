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
 * The display container for a node service. Necessary to be able to use an object
 * representation of ClassificationNode on the client side of GWT (with a minimum of
 * overhead).
 * 
 * @see gov.nasa.pds.registry.model.ClassificationNode
 * 
 * @author hyunlee
 */
public class ViewClassificationNode extends ViewRegistryObject implements IsSerializable {

	private static final long serialVersionUID = 1L;

	/*
	 * Reference to the guid of the parent ClassificationNode or ClassificationScheme
	 */
	private String parent;
	
	/* 
	 * A code with in a standard coding scheme that should be unique in respect
	 * to all siblings.
	 */
	private String code;
	
	/*
	 * Represents the traversal from the root scheme down to the node. 
	 */
	private String path;
	
	public String getParent() {
		return this.parent;
	}
	
	public void setParent(String parent) {
		this.parent = parent;
	}
	
	public String getCode() {
		return this.code;
	}
	
	public void setCode(String code) {
		this.code = code;
	}
	
	public String getPath() {
		return this.path;
	}
	
	public void setPath(String path) {
		this.path = path;
	}
}
