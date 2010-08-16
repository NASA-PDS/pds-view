package gov.nasa.pds.registry.ui.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ViewRegistryObject implements IsSerializable {

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
	 * @see gov.nasa.pds.registry.model.RegistryObject#getVersion()
	 */
	private String version;

	/**
	 * Version provided by user
	 * 
	 * @see gov.nasa.pds.registry.model.RegistryObject#getUserVersion()
	 */
	private String userVersion;

	public String getLid() {
		return this.lid;
	}

	public void setLid(String lid) {
		this.lid = lid;
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

	public String getVersion() {
		return this.version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getUserVersion() {
		return this.userVersion;
	}

	public void setUserVersion(String userVersion) {
		this.userVersion = userVersion;
	}
}
