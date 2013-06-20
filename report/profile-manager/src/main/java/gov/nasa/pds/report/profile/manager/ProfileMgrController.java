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
//	$Id: SearchCoreLauncher.java 11350 2013-02-22 00:55:41Z jpadams $
//

package gov.nasa.pds.report.profile.manager;

import gov.nasa.pds.report.update.ReportServiceUpdate;
import gov.nasa.pds.report.update.model.LogPath;
import gov.nasa.pds.report.update.model.LogSet;
import gov.nasa.pds.report.update.model.Profile;
import gov.nasa.pds.report.update.properties.EnvProperties;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

/**
 * Current implementation to create profiles and copy logs. Updates are only
 * made if new log sources are added
 * 
 * @author jpadams
 * 
 */
public class ProfileMgrController implements Runnable {

	private Logger log = Logger.getLogger(this.getClass().getName());

	private Thread thread;
	private LogPath logPath;
	private Profile profile;
	private boolean isNewProfile;
	private String sawmillHome;
	private String sawmillProfileHome;
	private String localPath;

	public ProfileMgrController(final String localPath, final LogPath logPath,
			final Profile profile, final boolean isNew) throws IOException {
		this.thread = new Thread(this);
		this.logPath = logPath;
		this.profile = profile;
		this.isNewProfile = isNew;
		this.localPath = localPath;

		EnvProperties env = new EnvProperties(localPath);
		this.sawmillHome = env.getSawmillHome();
		this.sawmillProfileHome = env.getSawmillProfileHome();
		this.logPath.setLogHome(env.getSawmillLogHome());
	}

	/**
	 * Implementation of run method from Runnable interface.
	 */
	public final void run() {
		ReportServiceUpdate rsUpdate = new ReportServiceUpdate(this.logPath, this.profile
				.getName(), this.isNewProfile);

		try {
			List<LogSet> newLogSets = this.profile.getNewLogSets();
			// ProfileConfigUtil cfg = new ProfileConfigUtil();
			rsUpdate.buildCfg(this.localPath, this.sawmillProfileHome,
					newLogSets);

			for (LogSet ls : newLogSets) {
				rsUpdate.transferLogs(ls.getHostname(), ls.getUsername(), ls
						.getPassword(), ls.getPathname(), ls.getLabel());
			}

			//rsUpdate.updateSawmill(this.sawmillHome);
		} catch (Exception e) {
			e.printStackTrace();
			this.log.warning(e.getClass().getName() + ": " + e.getMessage());
		}
	}

	/**
	 * Implementation for method of Runnable interface
	 */
	public final void start() {
		this.thread.start();
	}

}
