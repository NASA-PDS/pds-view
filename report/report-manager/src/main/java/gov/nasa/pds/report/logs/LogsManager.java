package gov.nasa.pds.report.logs;

import java.util.List;

public interface LogsManager {

	public void pullLogFiles() throws LogsManagerException;
	
	public void createStagingAreas(List<String> pathList) throws LogsManagerException;
}
