//	Copyright 2009-2013, by the California Institute of Technology.
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
//	$Id
//
package gov.nasa.pds.search.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

/**
 * Class to get tool release information.
 * 
 * @author jpadams
 * 
 */
public class ToolInfo {
	public static final String FILE = "search-core.properties";

	public static final String NAME = "search-core.name";

	public static final String VERSION = "search-core.version";

	public static final String RELEASE_DATE = "search-core.date";

	public static final String COPYRIGHT = "search-core.copyright";

	private static final Properties props = new Properties();

	static {
		try {
			final URL propertyFile = ToolInfo.class.getResource(FILE);
			final InputStream in = propertyFile.openStream();
			props.load(in);
		} catch (final IOException io) {
			throw new RuntimeException(io.getMessage());
		}
	}

	/**
	 * Get copyright information.
	 * 
	 * @return The copyright info.
	 */
	public static String getCopyright() {
		return props.getProperty(COPYRIGHT);
	}

	/**
	 * Get the name of the tool.
	 * 
	 * @return The tool name.
	 */
	public static String getName() {
		return props.getProperty(NAME);
	}

	/**
	 * Get the release date.
	 * 
	 * @return The tool release date.
	 */
	public static String getReleaseDate() {
		return props.getProperty(RELEASE_DATE);
	}

	/**
	 * Get the version.
	 * 
	 * @return The tool version.
	 */
	public static String getVersion() {
		return props.getProperty(VERSION);
	}
}
