package gov.nasa.pds.report.logs;

import java.util.Properties;

public interface LogsManager {

	public static final String DIR_NAME = "staging";
	
	public void pullLogFiles(Properties nodeProps) throws LogsManagerException;
	
}
