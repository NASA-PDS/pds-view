package gov.nasa.pds.report.setup.sawmill;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.logging.Logger;

import gov.nasa.pds.report.transfer.ReportServiceUpdate;
import gov.nasa.pds.report.transfer.model.LogPath;
import gov.nasa.pds.report.transfer.model.LogSet;
import gov.nasa.pds.report.transfer.model.Profile;
import gov.nasa.pds.report.transfer.properties.EnvProperties;

/**
 * Current implementation to create profiles and copy logs.
 * Updates are only made if new log sources are added
 * 
 * @author jpadams
 *
 */
public class SawmillController implements Runnable {

	private Logger log = Logger.getLogger(this.getClass().getName());
	private Thread thread;
	private Profile profile;
	private LogPath logPath;
	//private String realPath;
	private boolean isNewProfile;
	private EnvProperties env;

	public SawmillController(final LogPath logPath, final String realPath, final Profile profile, final boolean isNew) throws FileNotFoundException {
		this.thread = new Thread(this);
		this.logPath = logPath;
		this.profile = profile;
		this.isNewProfile = isNew;

		try {
			this.env = new EnvProperties(realPath);
		} catch (IOException e) {
			e.printStackTrace();
			this.log.warning(e.getMessage());
		}
		
		this.logPath.setLogHome(this.env.getSawmillLogHome());
	}

	/**
	 * Implementation of run method from Runnable interface.
	 */
	public final void run() {
		LogSet ls = null;
		ReportServiceUpdate sawmill = new ReportServiceUpdate();
		//SawmillUpdateLauncher sawmill;
		try {
			for (Iterator<LogSet> it = this.profile.getNewLogSets().iterator(); it.hasNext();) {
				ls = it.next();
				this.logPath.setLogSetLabel(ls.getLabel());
				//sawmill = new SawmillUpdate(this.env., this.logPath);
				//sawmill.transferLogs(ls.getHostname(), ls.getUsername(), ls.getPassword(), ls.getPathname(), ls.getLabel());
				sawmill.transferLogs(ls.getHostname(), ls.getUsername(), ls.getPassword(), ls.getPathname(), this.logPath.getPath());
			}

			sawmill.updateSawmill(this.env.getSawmillHome(), this.profile.getName(), this.isNewProfile);
		} catch (Exception e) {
			e.printStackTrace();
			this.log.warning(e.getMessage());
		}
	}

	/**
	 * Implementation for method of Runnable interface
	 */
	public final void start() {
		this.thread.start();
	}

}
