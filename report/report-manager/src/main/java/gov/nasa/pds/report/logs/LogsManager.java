package gov.nasa.pds.report.logs;

import java.util.Properties;

public interface LogsManager {

	// TODO: This will create the proper directories, pull the logs,
	// and--later--reformat them as needed
	public void pullLogFiles(Properties nodeProps) throws LogsManagerException;
	
}
