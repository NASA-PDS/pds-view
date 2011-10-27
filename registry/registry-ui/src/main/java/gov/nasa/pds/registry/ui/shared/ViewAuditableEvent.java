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
import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * The display container for a event. Necessary to be able to use an object
 * representation of Product on the client side of GWT (with a minimum of
 * overhead).
 * 
 * @see gov.nasa.pds.registry.model.AuditableEvent
 * 
 * @author hyunlee
 */
public class ViewAuditableEvent extends ViewRegistryObject implements IsSerializable {
	
	private static final long serialVersionUID = 1L;
	
	// Created, Approved, Deleted, Updated, Deprecated, Versioned, Undeprecated, Replicated
	private String eventType;
	
	private List<String> affectedObjects;
	
	private String requestId;
	
	private Date timestamp;
	
	private String user;
	
	public String getEventType() {
		return this.eventType;
	}
	
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}
	
	public List<String> getAffectedObjects() {
		return this.affectedObjects;
	}
	
	public void setAffectedObjects(List<String> affectedObjects) {
		this.affectedObjects = affectedObjects;
	}
	
	public String getRequestId() {
		return this.requestId;
	}
	
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	
	public Date getTimestamp() {
		return this.timestamp;
	}
	
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	
	public String getUser() {
		return this.user;
	}
	
	public void setUser(String user) {
		this.user = user;
	}
}
