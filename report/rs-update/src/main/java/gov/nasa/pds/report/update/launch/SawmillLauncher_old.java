package gov.nasa.pds.report.update.launch;

import gov.nasa.pds.report.update.RSUpdateException;
import gov.nasa.pds.report.update.properties.EnvProperties;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;

public class SawmillLauncher_old {

	private Logger LOG = Logger.getLogger(this.getClass().getName());
	
	private String profileName;
	private String dbOption;
	private String sawHome;
	
	/**
	 * Constructor for web interface where environment is already defined
	 * @param env
	 * @param profileName
	 * @param isNewProfile
	 * @throws IOException
	 */
	public SawmillLauncher_old(EnvProperties env, String profileName, boolean isNewProfile) throws IOException {
		this.profileName = profileName;
		
		setDbOption(isNewProfile);

		this.sawHome = env.getSawmillHome();
	}
	
	public SawmillLauncher_old(String realPath, String profileName, boolean isNewProfile) throws IOException {
		this.profileName = profileName;
		
		setDbOption(isNewProfile);
		
		EnvProperties env = new EnvProperties(realPath);
		this.sawHome = env.getSawmillHome();
	}
	
	private void setDbOption(boolean isNew) { 
		if (isNew) {
			this.dbOption = "bd";
		} else {
			this.dbOption = "ud";
		}
	}
	
	public void execute() throws RSUpdateException, IOException {
		BufferedReader input = null;
		Runtime rt = Runtime.getRuntime();
		Process pr = null;
		try {
			this.LOG.info("Executing: " + this.sawHome + "/sawmill.cgi -p " + this.profileName + " -a "+this.dbOption);
			pr = rt.exec(this.sawHome + "/sawmill.cgi -p " + this.profileName + " -a " + this.dbOption);
		
			input = new BufferedReader(new InputStreamReader(pr.getInputStream()));
			String line=null;
	        while((line = input.readLine()) != null) {
	        	if (line.contains("Error")) {
	        		throw new RSUpdateException("Error while running sawmill.cgi.");
	        	}
	            this.LOG.info(line);
	        }
	
	        /*
	        if (pr.exitValue() == 0) {
				this.LOG.info("Sawmill profile: " + this.profileName + " build complete.");
			} else {
				this.LOG.info("Error trying to build " + this.profileName + " database");
			}*/
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			input.close();
			pr.destroy();
			this.LOG.info(String.valueOf(pr.exitValue()));
		}
	}
}
