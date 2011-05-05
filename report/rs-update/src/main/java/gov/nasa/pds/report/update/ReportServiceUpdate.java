package gov.nasa.pds.report.update;

import gov.nasa.pds.report.update.util.RemoteFileTransfer;
import gov.nasa.pds.report.update.model.LogPath;
import gov.nasa.pds.report.update.properties.EnvProperties;
import gov.nasa.pds.report.update.sawmill.SawmillDB;
import gov.nasa.pds.report.update.util.FileUtil;
import gov.nasa.pds.report.update.util.SFTPConnect;

import java.io.IOException;

public class ReportServiceUpdate {
	/**
	 * Constructor for web interface when log destination directory is already determined
	 * 
	 * @param env
	 * @param logPath
	 * @param hostname
	 * @param username
	 * @param password
	 * @param pathname
	 * @param label
	 */
	public ReportServiceUpdate() { }

	public void transferLogs(String hostname, String username, String password, String pathname, String logPath) {
		//this.logDest += label;
		FileUtil dirStruct = new FileUtil();
		dirStruct.createDirStruct(logPath);
		
		RemoteFileTransfer transfer = new SFTPConnect();
		transfer.getLogs(hostname, username, password, pathname, logPath);
	}
	
	public void updateSawmill(String sawmillHome, String profileName, boolean isNewProfile) throws IOException, RSUpdateException {
		SawmillDB sawmill = new SawmillDB();
		sawmill.execute(sawmillHome, profileName, isNewProfile);
	}
}
