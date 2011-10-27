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
 * The display container for a registry object. Necessary to be able to use an object
 * representation of RegistryObject on the client side of GWT (with a minimum of
 * overhead).
 * 
 * @see gov.nasa.pds.registry.model.RegistryObject
 * 
 * @author hyunlee
 */
public class ViewRegistryObject implements IsSerializable {
	
	/**
	 * Global unique identifier
	 * 
	 * @see gov.nasa.pds.registry.model.Identifier#getGuid()
	 */
	private String guid;

	/**
	 * Logical identifier supplied by submitter
	 * 
	 * @see gov.nasa.pds.registry.model.RegistryObject#getLid()
	 */
	private String lid;

	/**
	 * Display name
	 * 
	 * @see gov.nasa.pds.registry.model.RegistryObject#getName()
	 */
	private String name;
	
	/**
	 * Display home
	 * 
	 * @see gov.nasa.pds.registry.model.Identifier#getHome()
	 */
	private String home;

	/**
	 * Type of registry object which should map to policy about the slots
	 * 
	 * @see gov.nasa.pds.registry.model.RegistryObject#getObjectType()
	 */
	private String objectType;

	/**
	 * Submitted, Approved, Deprecated, etc.
	 * 
	 * @see gov.nasa.pds.registry.model.RegistryObject#getStatus()
	 */
	private String status;

	/**
	 * Brief description of the entry
	 * 
	 * @see gov.nasa.pds.registry.model.RegistryObject#getDescription()
	 */
	private String description;

	/**
	 * Version provided by registry
	 * 
	 * @see gov.nasa.pds.registry.model.RegistryObject#getVersionName()
	 */
	private String versionName;

	/**
	 * Arbitrary name value pairs of properties associated with the product
	 * 
	 * @see gov.nasa.pds.registry.model.RegistryObject#getSlots()
	 */
	private List<ViewSlot> slots;

	public String getLid() {
		return this.lid;
	}

	public void setLid(String lid) {
		this.lid = lid;
	}
	
	public String getGuid() {
		return this.guid;
	}
	
	public void setGuid(String guid) {
		this.guid = guid;
	}
	
	public String getHome() {
		return this.home;
	}
	
	public void setHome(String home) {
		this.home = home;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getObjectType() {
		return this.objectType;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getVersionName() {
		return this.versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}
	
	/**
	 * Get the arbitrary property name value pairs
	 */
	public List<ViewSlot> getSlots() {
		return this.slots;
	}

	/**
	 * Set the arbitrary property name value pairs, should only be used when
	 * transforming an actual Product instance into a ViewProduct or in tests.
	 */
	public void setSlots(final List<ViewSlot> slots) {
		this.slots = slots;
	}
}
