//	Copyright 2013, by the California Institute of Technology.
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
//	$Id: SawmillDB.java 11670 2013-06-20 17:14:33Z jpadams $
//

package gov.nasa.pds.report.sawmill;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.logging.Logger;

/**
 * Class that interacts with Sawmill CLI and rebuilds/updates
 * Sawmill databases.
 * 
 * @author jpadams
 *
 */
public class SawmillClient {
	private Logger LOG = Logger.getLogger(this.getClass().getName());

	/**
	 * Based on the boolean entered, determines the Sawmill CLI
	 * db option.
	 * 
	 * @param rebuild	Boolean to determine whether or not to rebuild the
	 * 					Sawmill DB
	 * @return			The Sawmill CLI option flag to append to the CLI
	 * 					command
	 */
	private String getDbOption(boolean rebuild) {
		if (rebuild) {
			return "bd";
		} else {
			return "ud";
		}
	}

	/**
	 * Executes the Sawmill CLI with the various flags and specifications
	 * required to build a specific profile's database
	 * 
	 * @param sawmillHome
	 * @param profileName
	 * @param isNewProfile
	 * @throws Exception
	 */
	public void execute(String sawmillHome, String profileName,
			boolean isNewProfile) throws Exception {
		String dbOption = getDbOption(isNewProfile);

		BufferedReader input = null;
		Runtime rt = Runtime.getRuntime();
		Process pr = null;
		try {
			this.LOG.info("Executing: " + sawmillHome + "/sawmill -p "
					+ profileName + " -a " + dbOption);
			pr = rt.exec(sawmillHome + "/sawmill -p " + profileName
					+ " -a " + dbOption);

			input = new BufferedReader(new InputStreamReader(pr
					.getInputStream()));
			String line = null;
			while ((line = input.readLine()) != null) {
				if (line.contains("Error")) {
					throw new Exception(
							"Error while running sawmill software.");
				}
				this.LOG.info(line);
			}
		} finally {
			input.close();
			pr.destroy();
			this.LOG.info("Sawmill Update Complete.");
		}
	}
}