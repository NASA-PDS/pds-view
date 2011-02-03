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

package gov.nasa.pds.registry.model.naming;

import gov.nasa.pds.registry.model.RegistryObject;

import java.util.Comparator;

public class DefaultVersioner implements Versioner {
	private final static String INITIAL_VERSION = "1.0";

	private Comparator<RegistryObject> comparator = new Comparator<RegistryObject>() {
		public int compare(RegistryObject o1, RegistryObject o2) {
			String[] version1 = o1.getVersionName().split("\\.");
			String[] version2 = o2.getVersionName().split("\\.");
			int majorVersion1 = Integer.parseInt(version1[0]);
			int majorVersion2 = Integer.parseInt(version2[0]);

			if (majorVersion1 < majorVersion2) {
				return -1;
			} else if (majorVersion1 > majorVersion2) {
				return 1;
			}

			int minorVersion1 = Integer.parseInt(version1[1]);
			int minorVersion2 = Integer.parseInt(version2[1]);

			if (minorVersion1 < minorVersion2) {
				return -1;
			} else if (minorVersion1 > minorVersion2) {
				return 1;
			}

			return 0;
		}
	};

	/**
	 * This comparator imposes orderings that are inconsistent with equals as it
	 * only compares the version attribute on Registry Objects. Should only be
	 * used to sort a list of RegistryObjects with the same lid.
	 */
	public Comparator<RegistryObject> getComparator() {
		return this.comparator;
	}

	public String getInitialVersion() {
		return INITIAL_VERSION;
	}

	public String getNextVersion(String currentVersion) {
		return getNextVersion(currentVersion, true);
	}

	public String getNextVersion(String currentVersion, boolean major) {
		if (currentVersion == null) {
			return null;
		}

		String[] versionInfo = currentVersion.split("\\.");
		Integer majorVersion = new Integer(versionInfo[0]);
		Integer minorVersion = new Integer(versionInfo[1]);
		return (major) ? (majorVersion + 1) + ".0" : versionInfo[0] + "."
				+ (minorVersion + 1);
	}
}
