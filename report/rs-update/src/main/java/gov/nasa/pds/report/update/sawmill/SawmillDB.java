package gov.nasa.pds.report.update.sawmill;

import gov.nasa.pds.report.update.RSUpdateException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;

public class SawmillDB {
	private Logger LOG = Logger.getLogger(this.getClass().getName());

	// private String profileName;
	// private String dbOption;
	// private String sawHome;

	/**
	 * Constructor for web interface where environment is already defined
	 * 
	 * @param env
	 * @param profileName
	 * @param isNewProfile
	 * @throws IOException
	 */
	public SawmillDB() throws IOException {
	}

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
			this.LOG.info("Executing: " + sawmillHome + "/sawmill.cgi -p "
					+ profileName + " -a " + dbOption);
			pr = rt.exec(sawmillHome + "/sawmill.cgi -p " + profileName
					+ " -a " + dbOption);

			input = new BufferedReader(new InputStreamReader(pr
					.getInputStream()));
			String line = null;
			while ((line = input.readLine()) != null) {
				if (line.contains("Error")) {
					throw new RSUpdateException(
							"Error while running sawmill.cgi.");
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