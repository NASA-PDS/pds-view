package gov.nasa.pds.report.setup.runnable;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;

import gov.nasa.pds.report.setup.model.Profile;
import gov.nasa.pds.report.setup.properties.EnvProperties;
import gov.nasa.pds.report.setup.util.SFTPUtil;
import gov.nasa.pds.report.setup.util.SawmillUtil;

public class Copy implements Runnable {

	private Logger log = Logger.getLogger(this.getClass().getName());
	
	private Thread thread;
	private SFTPUtil util;
	private String sawHome;
	private String profName;
	private String dbCmd;
	
	public Copy(final String logDestPath, final EnvProperties env, final Profile profile, final boolean isNew) throws FileNotFoundException {
		this.thread = new Thread(this);
		this.util = new SFTPUtil(logDestPath, profile);
		this.profName = profile.getName();
		
		this.sawHome = env.getSawmillHome();
		// If new, build DB, else update DB via command line
		if (isNew) {
			this.dbCmd = "bd";
		} else {
			this.dbCmd = "ud";
		}	
	}
	
	public final void run() {
		this.util.getLogs();

		Runtime rt = Runtime.getRuntime();
		try {
			
			this.log.info("Executing: "+this.sawHome+"/sawmill.cgi -p "+this.profName+" -a "+this.dbCmd);
			Process pr = rt.exec(this.sawHome+"/sawmill.cgi -p "+this.profName+" -a "+this.dbCmd);
			
			/*BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));
			String line=null;
            while((line = input.readLine()) != null) {
                this._log.info("IN SEPARATE THREAD: "+line);
            }*/
            
            if (pr.exitValue() == 0) {
				this.log.info(this.profName+" database build complete.");
			} else {
				this.log.info("Error trying to build "+this.profName+" database");
			}

		} catch (IOException e) {
			this.log.warning(e.getMessage());
		}
	}
	
	public final void start() {
		this.thread.start();
	}
	
	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		// TODO Auto-generated method stub

	}

}
