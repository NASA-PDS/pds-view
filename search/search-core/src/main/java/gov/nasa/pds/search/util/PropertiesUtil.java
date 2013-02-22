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
//	$Id$
//

package gov.nasa.pds.search.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Utility class used to read Properties files
 * 
 * @author jpadams
 * 
 */
public class PropertiesUtil {

	/**
	 * Static method to read a properties file and return a name-value mapping
	 * given a prefix for the property key
	 * 
	 * @param propsFile
	 * @param prefix
	 * @return
	 */
	public static Map<String, String> getPropertiesMap(File propsFile,
			String prefix) {
		Map<String, String> mappings = new HashMap<String, String>();
		Properties props = new Properties();
		try {
			props.load(new FileInputStream(propsFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (Object key : props.keySet()) {
			String value = null;
			if (((String) key).startsWith(prefix)) {
				value = props.getProperty((String) key);
				String name = ((String) key).substring(prefix.length() + 1);
				mappings.put(name, value);
			}
		}
		return mappings;
	}
}
