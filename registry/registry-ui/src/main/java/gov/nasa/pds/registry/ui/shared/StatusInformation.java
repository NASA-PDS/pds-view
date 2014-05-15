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

/**
 * The display container for registry status. Necessary to be able to use an
 * object representation of Slot on the client side of GWT (with a minimum of
 * overhead).
 * 
 * NOTE: this flattens StatusInfo and RegistryStatus into a single container
 * 
 * NOTE: this is not currently wired to anything as there is no use case for
 * retrieving status at this time
 * 
 * @see gov.nasa.pds.registry.model.StatusInfo
 * @see gov.nasa.pds.registry.model.RegistryStatus
 * 
 * @author jagander
 */
public class StatusInformation implements Serializable {

	private static final long serialVersionUID = 1L;

	// status string
	private String status;

	// server started date tring
	private String serverStarted;

	private long associations;

	private long extrinsics;

	private long services;

	private long classificationSchemes;

	private long classificationNodes;

	private long packages;
	
	private long events;

	//private String home;

	private String registryVersion = "1.6.0";

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getServerStarted() {
		return this.serverStarted;
	}
	
	public void setServerStarted(String serverStarted) {
		this.serverStarted = serverStarted;
	}

	public long getAssociations() {
		return associations;
	}

	public void setAssociations(long associations) {
		this.associations = associations;
	}

	public long getExtrinsics() {
		return extrinsics;
	}

	public void setExtrinsics(long extrinsics) {
		this.extrinsics = extrinsics;
	}

	public long getServices() {
		return services;
	}

	public void setServices(long services) {
		this.services = services;
	}

	public long getClassificationSchemes() {
		return classificationSchemes;
	}

	public void setClassificationSchemes(long classificationSchemes) {
		this.classificationSchemes = classificationSchemes;
	}

	public long getClassificationNodes() {
		return classificationNodes;
	}

	public void setClassificationNodes(long classificationNodes) {
		this.classificationNodes = classificationNodes;
	}

	public long getPackages() {
		return packages;
	}

	public void setPackages(long packages) {
		this.packages = packages;
	}
	
	public long getEvents() {
		return events;
	}

	public void setEvents(long events) {
		this.events = events;
	}

	public String getRegistryVersion() {
		return registryVersion;
	}

	public void setRegistryVersion(String registryVersion) {
		this.registryVersion = registryVersion;
	}
/*
	public String getHome() {
		return home;
	}

	public void setHome(String home) {
		this.home = home;
	}
*/
	public StatusInformation() {
		// instantiated externally due to dependencies in client side of GWT
	}
}
