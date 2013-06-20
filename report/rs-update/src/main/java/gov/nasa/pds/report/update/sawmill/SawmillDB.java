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
//	$Id$
//

package gov.nasa.pds.report.update.sawmill;

import gov.nasa.pds.report.update.RSUpdateException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;

public class SawmillDB {
	private Logger LOG = Logger.getLogger(this.getClass().getName());

	private String getDbOption(boolean isNew) {
		if (isNew) {
			return "bd";
		} else {
			return "ud";
		}
	}

	public void execute(String sawmillHome, String profileName,
			boolean isNewProfile) throws RSUpdateException, IOException {
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
					throw new RSUpdateException(
							"Error while running sawmill software.");
				}
				this.LOG.info(line);
			}

			/*
			 * if (pr.exitValue() == 0) { this.LOG.info("Sawmill profile: " +
			 * this.profileName + " build complete."); } else {
			 * this.LOG.info("Error trying to build " + this.profileName +
			 * " database"); }
			 */
		} finally {
			input.close();
			pr.destroy();
			this.LOG.info("Sawmill Update Complete.");
			// this.LOG.info(String.valueOf(pr.exitValue()));
		}
	}
}