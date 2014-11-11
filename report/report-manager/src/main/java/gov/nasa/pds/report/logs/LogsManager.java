package gov.nasa.pds.report.logs;

import java.util.Properties;

public interface LogsManager {

	public static final String OUTPUT_DIR_NAME = "pull";
	
	public void pullLogFiles(Properties nodeProps) throws LogsManagerException;
	
}
