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

	public String getTargetGuid() {
		return this.targetGuid;
	}

	public void setTargetGuid(String targetGuid) {
		this.targetGuid = targetGuid;
	}

	public String getAssociationType() {
		return this.associationType;
	}

	public void setAssociationType(String associationType) {
		this.associationType = associationType;
	}

}
