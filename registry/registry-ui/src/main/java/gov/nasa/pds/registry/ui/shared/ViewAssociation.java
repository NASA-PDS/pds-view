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

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * The display container for an association. Necessary to be able to use an
 * object representation of Product on the client side of GWT (with a minimum of
 * overhead).
 * 
 * @see gov.nasa.pds.registry.model.Association
 * 
 * @author jagander
 */
public class ViewAssociation extends ViewRegistryObject implements
		IsSerializable {

	private static final long serialVersionUID = -7276791609858383804L;

	/**
	 * Global unique identifier of the source registry object.
	 */
	private String sourceGuid;

	/**
	 * Global unique identifier for the target registry object.
	 */
	private String targetGuid;

	private String sourceLid;
	
	private String targetLid;
	

	/**
	 * Named relation between source and target registry object.
	 */
	private String associationType;

	public String getSourceGuid() {
		return this.sourceGuid;
	}

	public void setSourceGuid(String sourceGuid) {
		this.sourceGuid = sourceGuid;
	}
	
	public String getSourceLid() {
		return this.sourceLid;
	}

	public void setSourceLid(String sourceLid) {
		this.sourceLid = sourceLid;
	}

	public String getTargetGuid() {
		return this.targetGuid;
	}

	public void setTargetGuid(String targetGuid) {
		this.targetGuid = targetGuid;
	}

	public String getTargetLid() {
		return this.targetLid;
	}
	
	public void setTargetLid(String targetLid) {
		this.targetLid = targetLid;
	}
	 
	public String getAssociationType() {
		return this.associationType;
	}

	public void setAssociationType(String associationType) {
		this.associationType = associationType;
	}
}
