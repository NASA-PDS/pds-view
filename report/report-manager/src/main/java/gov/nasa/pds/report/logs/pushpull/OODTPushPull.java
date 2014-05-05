package gov.nasa.pds.report.logs.pushpull;

import java.io.File;
import java.util.LinkedList;

public interface OODTPushPull {
    
    public static final int DEFAULT_PORT = 9999;
    
    public static final String STAGING_TAG_NAME = "dataInfo";
    
    public static final String STAGING_ATTRIBUTE_NAME = "stagingArea";

	public void pull() throws Exception;
	
	public int getNumFilesPulled();
	
	public void setPort(int port);

	public int getPort();

	public void setPropertiesFile(File propertiesFile);

	public File getPropertiesFile();

	public void setSitesFiles(LinkedList<File> sitesFiles);
	
	public void addSitesFile(File sitesFile);

	public LinkedList<File> getRemoteSpecsFile();
}
