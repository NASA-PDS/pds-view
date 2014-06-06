package gov.nasa.pds.report;

import java.io.File;

import gov.nasa.pds.report.constants.Constants;
import gov.nasa.pds.report.logs.LogsManager;
import gov.nasa.pds.report.logs.LogsManagerException;
import gov.nasa.pds.report.util.Utility;

public class ReportServiceManager {
	
	public ReportServiceManager() {
		// TODO Some work to set up whatever needs to flow downstream to rest of classes
	}
	
	public void pullLogs() throws LogsManagerException {
		// TODO LogsManager initialization and call to pull log files goes here
		// logsMgr.pullLogFiles();
	}
	
	private File getFile(String path) throws LogsManagerException {
		if (path.equals("")) {
			path = Utility.getHomeDirectory() + Constants.PROPERTIES_PATH;
		}
		
		File file = new File(Utility.getAbsolutePath(path));
		if (file.isFile()) {
			return file;
		}
		
		throw new LogsManagerException(path + " not found.");
	}
	
}
