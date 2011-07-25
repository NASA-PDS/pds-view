package gov.nasa.pds.report.update;

import gov.nasa.pds.report.update.model.LogPath;
import gov.nasa.pds.report.update.model.LogSet;
import gov.nasa.pds.report.update.sawmill.ProfileConfigUtil;
import gov.nasa.pds.report.update.sawmill.SawmillDB;
import gov.nasa.pds.report.update.util.Utility;
import gov.nasa.pds.report.update.util.FileUtil;
import gov.nasa.pds.report.update.util.RemoteFileTransfer;
import gov.nasa.pds.report.update.util.SFTPConnect;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

public class ReportServiceUpdate {

	private LogPath logPath;
	private String profileName;
	private boolean isNewProfile;

	private Logger log = Logger.getLogger(this.getClass().getName());
	
	public ReportServiceUpdate(LogPath logPath, String profileName,
			boolean isNewProfile) {
		this.logPath = logPath;
		this.profileName = profileName;
		this.isNewProfile = isNewProfile;
	}

	public void buildCfg(String localPath, String sawmillProfileHome,
			List<LogSet> newLogSets) throws IOException {
		ProfileConfigUtil cfg = new ProfileConfigUtil(this.logPath, localPath,
				sawmillProfileHome, this.profileName);
		cfg.buildCfg(newLogSets, this.isNewProfile);
	}

	public void transferLogs(String hostname, String username, String password,
			String remotePath, String label) throws IOException, RSUpdateException {
		this.logPath.setLogSetLabel(label);

		String baseLogPath = this.logPath.getPath();

		if (label.contains("pdsimg")) { // If label is pdsimg machine, special
										// case
			baseLogPath += "/" + Utility.getFileDate(); // Add date directory
															// to path
		}

		if (FileUtil.createDirStruct(baseLogPath)) {
			RemoteFileTransfer transfer = new SFTPConnect();
			transfer.getLogs(hostname, username, password, remotePath, baseLogPath);
		} else {
			throw new RSUpdateException("Error creating directory structure. Check directory privileges.");
		}
	}

	public void updateSawmill(String sawmillHome) throws IOException,
			RSUpdateException {
		SawmillDB sawmill = new SawmillDB();
		sawmill.execute(sawmillHome, this.profileName, this.isNewProfile);
	}
}
