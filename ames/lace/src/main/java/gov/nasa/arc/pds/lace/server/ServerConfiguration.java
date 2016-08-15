package gov.nasa.arc.pds.lace.server;

import java.io.File;

import javax.inject.Singleton;

/**
 * Implements an object holding server configuration parameters
 * needed by other server-side components. This object can be
 * injected into those components using Guice.
 */
@Singleton
public class ServerConfiguration {

	private static final String PROJECT_ROOT_DIRECTORY = "projects";
	private static final String UPLOAD_DIRECTORY = "upload";

	private File dataRoot;

	/**
	 * Sets the root directory for all data created by the application.
	 *
	 * @param dataRoot the data root directory
	 */
	public void setDataRoot(File dataRoot) {
		this.dataRoot = dataRoot;
	}

	/**
	 * Gets the root directory for user projects and labels.
	 *
	 * @return the project root directory
	 */
	public File getProjectRoot() {
		return new File(dataRoot, PROJECT_ROOT_DIRECTORY);
	}

	/**
	 * Gets the root directory for uploaded files.
	 *
	 * @return the upload root directory
	 */
	public File getUploadRoot() {
		return new File(dataRoot, UPLOAD_DIRECTORY);
	}

}
