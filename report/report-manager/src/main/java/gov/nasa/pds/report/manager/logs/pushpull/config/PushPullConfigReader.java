package gov.nasa.pds.report.manager.logs.pushpull.config;

/**
 * Class used to read and extract information from the PushPull XML
 * configuration files
 * 
 * @author jpadams
 *
 */
public class PushPullConfigReader {

	private String configPath;
	
	public PushPullConfigReader(String configPath) {
		this.configPath = configPath;
	}
	
	public void setConfigPath(String configPath) {
		this.configPath = configPath;
	}
}
