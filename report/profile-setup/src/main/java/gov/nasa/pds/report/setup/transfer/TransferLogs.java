package gov.nasa.pds.report.setup.transfer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.logging.Logger;

import gov.nasa.pds.report.setup.model.LogSet;
import gov.nasa.pds.report.setup.model.Profile;
import gov.nasa.pds.report.transfer.SawmillException;
import gov.nasa.pds.report.transfer.launch.SawmillUpdateLauncher;
import gov.nasa.pds.report.transfer.launch.LogTransferLauncher;
import gov.nasa.pds.report.transfer.properties.EnvProperties;
import gov.nasa.pds.report.transfer.util.FileUtil;
import gov.nasa.pds.report.transfer.util.RemoteFileTransfer;
import gov.nasa.pds.report.transfer.util.SFTPConnect;

/**
 * Current implementation to create profiles and copy logs.
 * Updates are only made if new log sources are added
 * 
 * @author jpadams
 *
 */
public class TransferLogs implements Runnable {

	private Logger log = Logger.getLogger(this.getClass().getName());
	private Thread thread;
	private Profile profile;
	private String logPath;
	//private String realPath;
	private boolean isNewProfile;
	private EnvProperties env;

	public TransferLogs(final String realPath, final String logDestPath, final Profile profile, final boolean isNew) throws FileNotFoundException {
		this.thread = new Thread(this);
		this.logPath = logDestPath;
		this.profile = profile;
		this.isNewProfile = isNew;

		try {
			this.env = new EnvProperties(realPath);
		} catch (IOException e) {
			e.printStackTrace();
			this.log.warning(e.getMessage());
		}
	}

	/**
	 * Implementation of run method from Runnable interface.
	 */
	public final void run() {
		LogSet ls;
		String logDest;
		LogTransferLauncher transfer;
		SawmillUpdateLauncher sawmill;
		try {
			for (Iterator<LogSet> it = this.profile.getNewLogSets().iterator(); it.hasNext();) {
				ls = it.next();
				transfer = new LogTransferLauncher(this.env, this.logPath, ls.getHostname(), ls.getUsername(), ls.getPassword(), ls.getPathname(), ls.getLabel());
				transfer.execute();
			}

			sawmill = new SawmillUpdateLauncher(env, this.profile.getName(), this.isNewProfile);
			sawmill.execute();
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
