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
	 * Local identifier of the source artifact. This references a unique set of
	 * artifacts in the registry.
	 */
	private String sourceLid;

	/**
	 * Version of the source artifact. When used in conjunction with the source
	 * local identifier this maps to one artifact in a registry.
	 */
	private String sourceVersion;

	/**
	 * URI of the registry that the source registry object originates from.
	 */
	private String sourceHome;

	/**
	 * Global unique identifier for the target registry object.
	 */
	private String targetGuid;

	/**
	 * Local identifier of the target registry object.
	 */
	private String targetLid;

	/**
	 * Version of the target artifact. When used in in conjunction with the
	 * target local identifier this maps to one artifact in a registry.
	 */
	private String targetVersion;

	/**
	 * URI of the registry that the target registry object originates from.
	 */
	private String targetHome;

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

	public String getSourceVersion() {
		return this.sourceVersion;
	}

	public void setSourceVersion(String sourceVersion) {
		this.sourceVersion = sourceVersion;
	}

	public String getSourceHome() {
		return this.sourceHome;
	}

	public void setSourceHome(String sourceHome) {
		this.sourceHome = sourceHome;
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

	public String getTargetVersion() {
		return this.targetVersion;
	}

	public void setTargetVersion(String targetVersion) {
		this.targetVersion = targetVersion;
	}

	public String getTargetHome() {
		return this.targetHome;
	}

	public void setTargetHome(String targetHome) {
		this.targetHome = targetHome;
	}

	public String getAssociationType() {
		return this.associationType;
	}

	public void setAssociationType(String associationType) {
		this.associationType = associationType;
	}

}
