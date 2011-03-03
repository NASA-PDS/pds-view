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

	private Logger _log = Logger.getLogger(this.getClass().getName());
	
	private Thread _thread;
	private SFTPUtil _util;
	private String _sawHome;
	private String _profName;
	private String _dbCmd;
	
	public Copy(String logDestPath, EnvProperties env, Profile profile, boolean isNew) throws FileNotFoundException {
		this._thread = new Thread(this);
		this._util = new SFTPUtil(logDestPath, profile);
		this._profName = profile.getName();
		
		this._sawHome = env.getSawmillHome();
		// If new, build DB, else update DB via command line
		if (isNew)
			this._dbCmd = "bd";
		else
			this._dbCmd = "ud";	
	}
	
	public void run() {
		this._util.getLogs();

		Runtime rt = Runtime.getRuntime();
		try {
			
			this._log.info("Executing: "+this._sawHome+"/sawmill.cgi -p "+this._profName+" -a "+this._dbCmd);
			Process pr = rt.exec(this._sawHome+"/sawmill.cgi -p "+this._profName+" -a "+this._dbCmd);
			
			/*BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));
			String line=null;
            while((line = input.readLine()) != null) {
                this._log.info("IN SEPARATE THREAD: "+line);
            }*/
            
            if (pr.exitValue() == 0)
            	this._log.info(this._profName+" database build complete.");
            else
            	this._log.info("Error trying to build "+this._profName+" database");

		} catch (IOException e) {
			this._log.warning(e.getMessage());
		}
	}
	
	public void start() {
		this._thread.start();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
