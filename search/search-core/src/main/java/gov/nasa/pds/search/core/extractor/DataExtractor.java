package gov.nasa.pds.search.core.extractor;

import gov.nasa.pds.search.core.exception.SearchCoreException;

import java.io.File;
import java.util.List;

public interface DataExtractor {

	public void run() throws SearchCoreException;
	
	public List<File> getCoreConfigs(File configDir) throws SearchCoreException;
	
	public File getConfDir();
		
	public void setConfDir(File confDir);
		
	public int getQueryMax();
			
	public void setQueryMax(int queryMax);

}
