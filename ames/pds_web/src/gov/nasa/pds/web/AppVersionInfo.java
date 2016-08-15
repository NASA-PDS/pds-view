package gov.nasa.pds.web;

import gov.nasa.pds.tools.util.VersionInfo;

import java.io.IOException;
import java.util.Properties;

/**
 * Container class to hold application info. All values are static since they
 * cannot change without deploying a new version of the application, requiring a
 * restart.
 * 
 * @author jagander
 */
public class AppVersionInfo {

	/**
	 * ODL (Object Description Language) version
	 */
	private static String ODL_VERSION;

	/**
	 * Product Tools (aka Label Parser Library) version
	 */
	private static String LABEL_LIBRARY_VERSION;

	/**
	 * PDS standards version, indicates the last standards reference that
	 * validation rules were checked against
	 */
	private static String STANDARDS_VERSION;

	/**
	 * PDS Version info, currently fixed at PDS3
	 * 
	 * @see gov.nasa.pds.tools.util.VersionInfo#getPDSVersion()
	 */
	private static String PDS_VERSION;

	/**
	 * Volume validator version
	 */
	private static String VOLUME_VALIDATOR_VERSION;

	/**
	 * Key to retrieve volume validator version from a props file
	 */
	private static String VOLUME_VALIDATOR_VERSION_KEY = "volumeValidator.version"; //$NON-NLS-1$

	/**
	 * Table data slicer version
	 */
	private static String SLICER_VERSION;

	/**
	 * Key to retrieve data slicer version from a props file
	 */
	private static String SLICER_VERSION_KEY = "dataSlicer.version"; //$NON-NLS-1$

	/**
	 * Props container, loaded statically
	 */
	private final static Properties props = new Properties();

	static {
		System.out.println("Loading version.properties.");
		try {
			try {
				props.load(AppVersionInfo.class.getResourceAsStream("/version.properties")); //$NON-NLS-1$
			} catch (RuntimeException ex) {
				System.out.println("Failed to load version.properties: " + ex.toString());
				ex.printStackTrace();
				throw(ex);
			}
			System.out.println("Loaded version.properties - " + VOLUME_VALIDATOR_VERSION_KEY + "=" + props.get(VOLUME_VALIDATOR_VERSION_KEY));
		} catch (IOException e) {
			// re-throw as runtime so that it needn't be explicitly caught
			throw new RuntimeException(e);
		}
	}

	/**
	 * A noop constructor
	 */
	public AppVersionInfo() {
		// noop
	}

	/**
	 * Get the ODL version
	 * 
	 * @return the ODL version
	 */
	public static String getODLVersion() {
		if (ODL_VERSION == null) {
			ODL_VERSION = VersionInfo.getODLVersion();
		}
		return ODL_VERSION;
	}

	/**
	 * Get the label library version
	 * 
	 * @return the label library version
	 */
	public static String getLibraryVersion() {
		if (LABEL_LIBRARY_VERSION == null) {
			LABEL_LIBRARY_VERSION = VersionInfo.getLibraryVersion();
		}
		return LABEL_LIBRARY_VERSION;
	}

	/**
	 * Get the standards reference version that the validation tool was designed
	 * agtainst
	 * 
	 * @return the standards reference version
	 */
	public static String getStandardsRefVersion() {
		if (STANDARDS_VERSION == null) {
			STANDARDS_VERSION = VersionInfo.getStandardsRefVersion();
		}
		return STANDARDS_VERSION;
	}

	/**
	 * Get the PDS version
	 * 
	 * @return the PDS version the validator is built using
	 */
	public static String getPDSVersion() {
		if (PDS_VERSION == null) {
			PDS_VERSION = VersionInfo.getPDSVersion();
		}
		return PDS_VERSION;
	}

	/**
	 * Get the volume validation version
	 * 
	 * @return the volume validation version
	 */
	public static String getVolumeValidatorVersion() {
		if (VOLUME_VALIDATOR_VERSION == null) {
			VOLUME_VALIDATOR_VERSION = props
					.getProperty(VOLUME_VALIDATOR_VERSION_KEY);
		}
		return VOLUME_VALIDATOR_VERSION;
	}

	/**
	 * Get the data slicer version
	 * 
	 * @return the data slicer version
	 */
	public static String getSlicerVersion() {
		if (SLICER_VERSION == null) {
			SLICER_VERSION = props.getProperty(SLICER_VERSION_KEY);
		}
		return SLICER_VERSION;
	}

}
