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

	// number of objects stored in the registry
	private int storedDataObjects = 0;

	// number of artifacts registered
	private int registeredArtifacts = 0;

	// status string
	private String status;

	// server started date tring
	private String serverStarted;

	public int getStoredDataObjects() {
		return this.storedDataObjects;
	}

	public int getRegisteredArtifacts() {
		return this.registeredArtifacts;
	}

	public void setStoredDataObjects(int storedDataObjects) {
		this.storedDataObjects = storedDataObjects;
	}

	public void setRegisteredArtifacts(int registeredArtifacts) {
		this.registeredArtifacts = registeredArtifacts;
	}

	public void setServerStarted(String serverStarted) {
		this.serverStarted = serverStarted;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getServerStarted() {
		return this.serverStarted;
	}

	public String getStatus() {
		return this.status;
	}

	public StatusInformation() {
		// instantiated externally due to dependencies in client side of GWT
	}

}
