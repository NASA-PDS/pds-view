//	Copyright 2009-2010, by the California Institute of Technology.
//	ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
//	Any commercial use must be negotiated with the Office of Technology 
//	Transfer at the California Institute of Technology.
//	
//	This software is subject to U. S. export control laws and regulations 
//	(22 C.F.R. 120-130 and 15 C.F.R. 730-774). To the extent that the software 
//	is subject to U.S. export control laws and regulations, the recipient has 
//	the responsibility to obtain export licenses or other export authority as 
//	may be required before exporting such information to foreign countries or 
//	providing access to foreign nationals.
//	
//	$Id$
//

package gov.nasa.pds.registry.model;

import java.util.HashSet;

import javax.persistence.MappedSuperclass;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@MappedSuperclass
@XmlRootElement(name = "regisryObject", namespace = "http://registry.pds.nasa.gov")
@XmlType(name = "registryObjectType", namespace = "http://registry.pds.nasa.gov")
@XmlAccessorType(XmlAccessType.FIELD)
public class RegistryObject extends Identifiable {

	private static final long serialVersionUID = 1477919575493686135L;

	// Logical identifier supplied by submitter
	@XmlAttribute
	private String lid;

	// Name for display
	@XmlAttribute
	private String name;

	// Type of registry object which should map to policy about the slots
	@XmlAttribute
	private String objectType;

	// Submitted, Approved, Deprecated, etc.
	@XmlAttribute
	private ObjectStatus status;

	// Brief description of the entry
	@XmlAttribute
	private String description;

	// Version provided by registry
	@XmlAttribute
	private String version;

	// Version provided by user
	@XmlAttribute
	private String userVersion;

	/**
	 * Meant for JAXB only
	 */
	public RegistryObject() {
	}

	/**
	 * Construct a registry object without a version
	 * 
	 * @param guid
	 *            globally unique identifier
	 * @param home
	 *            registry URI
	 * @param lid
	 *            logical identifier
	 * @param name
	 * @param objectType
	 */
	public RegistryObject(String guid, String home, String lid, String name,
			String objectType) {
		super(guid, home, new HashSet<Slot>());
		this.lid = lid;
		this.name = name;
		this.objectType = objectType;
	}

	public RegistryObject(RegistryObject object) {
		super(object.getGuid(), object.getHome(), object.getSlots());
		this.lid = object.getLid();
		this.name = object.getName();
		this.objectType = object.getObjectType();
		this.status = object.getStatus();
		this.description = object.getDescription();
		this.version = object.getVersion();
		this.userVersion = object.getUserVersion();
	}

	/**
	 * @return the lid
	 */
	public String getLid() {
		return lid;
	}

	/**
	 * @param lid
	 *            the lid to set
	 */
	public void setLid(String lid) {
		this.lid = lid;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the objectType
	 */
	public String getObjectType() {
		return objectType;
	}

	/**
	 * @param objectType
	 *            the objectType to set
	 */
	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

	/**
	 * @return the status
	 */
	public ObjectStatus getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(ObjectStatus status) {
		this.status = status;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @param version
	 *            the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * @return the userVersion
	 */
	public String getUserVersion() {
		return userVersion;
	}

	/**
	 * @param userVersion
	 *            the userVersion to set
	 */
	public void setUserVersion(String userVersion) {
		this.userVersion = userVersion;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((lid == null) ? 0 : lid.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((objectType == null) ? 0 : objectType.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result
				+ ((userVersion == null) ? 0 : userVersion.hashCode());
		result = prime * result + ((version == null) ? 0 : version.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		RegistryObject other = (RegistryObject) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (lid == null) {
			if (other.lid != null)
				return false;
		} else if (!lid.equals(other.lid))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (objectType == null) {
			if (other.objectType != null)
				return false;
		} else if (!objectType.equals(other.objectType))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		if (userVersion == null) {
			if (other.userVersion != null)
				return false;
		} else if (!userVersion.equals(other.userVersion))
			return false;
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;
		return true;
	}

}
