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

import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * This class allows one to relate a source and target registry object along a
 * named relationship. The source and target can be identified by either the
 * guid or the pairing of a lid vid. The home indicates where the registered
 * originated.
 * 
 * @author pramirez
 * 
 */

@Entity
@XmlRootElement(name = "association", namespace = "http://registry.pds.nasa.gov")
@XmlType(name = "")
@XmlAccessorType(XmlAccessType.FIELD)
public class Association extends RegistryObject {

	private static final long serialVersionUID = -7276791609858383804L;

	/**
	 * Global unique identifier of the source registry object.
	 */
	@XmlAttribute
	private String sourceGuid;

	/**
	 * Local identifier of the source artifact. This references a unique set of
	 * artifacts in the registry.
	 */
	@XmlAttribute
	private String sourceLid;

	/**
	 * Version of the source artifact. When used in conjunction with the source
	 * local identifier this maps to one artifact in a registry.
	 */
	@XmlAttribute
	private String sourceVersionId;

	/**
	 * URI of the registry that the source registry object originates from.
	 */
	@XmlAttribute
	private String sourceHome;

	/**
	 * Global unique identifier for the target registry object.
	 */
	@XmlAttribute
	private String targetGuid;

	/**
	 * Local identifier of the target registry object.
	 */
	@XmlAttribute
	private String targetLid;

	/**
	 * Version of the target artifact. When used in in conjunction with the
	 * target local identifier this maps to one artifact in a registry.
	 */
	@XmlAttribute
	private String targetVersionId;

	/**
	 * URI of the registry that the target registry object originates from.
	 */
	@XmlAttribute
	private String targetHome;

	/**
	 * Named relation between source and target registry object.
	 */
	@XmlAttribute
	private String associationType;

	public Association() {
		this.setObjectType(Association.class.getSimpleName());
		this.setVersionName(null);
		this.setVersionId(null);
	}

	/**
	 * @return the local identifier of the source artifact
	 */
	public String getSourceLid() {
		return sourceLid;
	}

	/**
	 * @param sourceLid
	 *            the local identifier of the source artifact
	 */
	public void setSourceLid(String sourceLid) {
		this.sourceLid = sourceLid;
	}

	/**
	 * @return the version of the source artifact
	 */
	public String getSourceVersionId() {
		return sourceVersionId;
	}

	/**
	 * @param sourceVersion
	 *            the version of the source's local identifier
	 */
	public void setSourceVersionId(String sourceVersionId) {
		this.sourceVersionId = sourceVersionId;
	}

	/**
	 * @return the local identifier of the target artifact
	 */
	public String getTargetLid() {
		return targetLid;
	}

	/**
	 * @param targetLid
	 *            the local identifier of the target artifact
	 */
	public void setTargetLid(String targetLid) {
		this.targetLid = targetLid;
	}

	/**
	 * @return the version of the target artifact
	 */
	public String getTargetVersionId() {
		return targetVersionId;
	}

	/**
	 * @param targetVersionId
	 *            the version of the target's local identifier
	 */
	public void setTargetVersionId(String targetVersionId) {
		this.targetVersionId = targetVersionId;
	}

	/**
	 * @return the type of association that exists between the source and target
	 *         artifact
	 */
	public String getAssociationType() {
		return associationType;
	}

	/**
	 * @param associationType
	 *            the type of association between the source and target
	 */
	public void setAssociationType(String associationType) {
		this.associationType = associationType;
	}

	public String getSourceGuid() {
		return sourceGuid;
	}

	public void setSourceGuid(String sourceGuid) {
		this.sourceGuid = sourceGuid;
	}

	public String getSourceHome() {
		return sourceHome;
	}

	public void setSourceHome(String sourceHome) {
		this.sourceHome = sourceHome;
	}

	public String getTargetGuid() {
		return targetGuid;
	}

	public void setTargetGuid(String targetGuid) {
		this.targetGuid = targetGuid;
	}

	public String getTargetHome() {
		return targetHome;
	}

	public void setTargetHome(String targetHome) {
		this.targetHome = targetHome;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((associationType == null) ? 0 : associationType.hashCode());
		result = prime * result
				+ ((sourceGuid == null) ? 0 : sourceGuid.hashCode());
		result = prime * result
				+ ((sourceHome == null) ? 0 : sourceHome.hashCode());
		result = prime * result
				+ ((sourceLid == null) ? 0 : sourceLid.hashCode());
		result = prime * result
				+ ((sourceVersionId == null) ? 0 : sourceVersionId.hashCode());
		result = prime * result
				+ ((targetGuid == null) ? 0 : targetGuid.hashCode());
		result = prime * result
				+ ((targetHome == null) ? 0 : targetHome.hashCode());
		result = prime * result
				+ ((targetLid == null) ? 0 : targetLid.hashCode());
		result = prime * result
				+ ((targetVersionId == null) ? 0 : targetVersionId.hashCode());
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
		Association other = (Association) obj;
		if (associationType == null) {
			if (other.associationType != null)
				return false;
		} else if (!associationType.equals(other.associationType))
			return false;
		if (sourceGuid == null) {
			if (other.sourceGuid != null)
				return false;
		} else if (!sourceGuid.equals(other.sourceGuid))
			return false;
		if (sourceHome == null) {
			if (other.sourceHome != null)
				return false;
		} else if (!sourceHome.equals(other.sourceHome))
			return false;
		if (sourceLid == null) {
			if (other.sourceLid != null)
				return false;
		} else if (!sourceLid.equals(other.sourceLid))
			return false;
		if (sourceVersionId == null) {
			if (other.sourceVersionId != null)
				return false;
		} else if (!sourceVersionId.equals(other.sourceVersionId))
			return false;
		if (targetGuid == null) {
			if (other.targetGuid != null)
				return false;
		} else if (!targetGuid.equals(other.targetGuid))
			return false;
		if (targetHome == null) {
			if (other.targetHome != null)
				return false;
		} else if (!targetHome.equals(other.targetHome))
			return false;
		if (targetLid == null) {
			if (other.targetLid != null)
				return false;
		} else if (!targetLid.equals(other.targetLid))
			return false;
		if (targetVersionId == null) {
			if (other.targetVersionId != null)
				return false;
		} else if (!targetVersionId.equals(other.targetVersionId))
			return false;
		return true;
	}

}
