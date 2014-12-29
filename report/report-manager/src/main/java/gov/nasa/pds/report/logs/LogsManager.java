package gov.nasa.pds.report.logs;

import java.util.Properties;

public interface LogsManager {

	public void pullLogFiles(Properties nodeProps) throws LogsManagerException;
	
}
