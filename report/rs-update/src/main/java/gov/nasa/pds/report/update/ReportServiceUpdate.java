package gov.nasa.pds.report.transfer;

import gov.nasa.pds.report.transfer.model.LogPath;
import gov.nasa.pds.report.transfer.properties.EnvProperties;
import gov.nasa.pds.report.transfer.sawmill.SawmillDB;
import gov.nasa.pds.report.transfer.util.FileUtil;
import gov.nasa.pds.report.transfer.util.RemoteFileTransfer;
import gov.nasa.pds.report.transfer.util.SFTPConnect;

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
