package gov.nasa.pds.report.constants;

/**
 * 
 * @author jpadams
 * 
 */
public abstract class Constants {
	
	/** Name of environment variable specifying home of software application **/
	public static final String HOME_ENV_VAR = "REPORT_MGR_HOME";
	
	public static final String LOGS_HOME_ENV_VAR = "LOGS_HOME";
	
	public static final String PROPERTIES_PATH = "/etc/push_pull_framework.properties";
	
	public static final String REMOTE_SPECS_PATH = "/etc/conf/RemoteSpecs.xml";
	
	public static final String DEFAULT_CONFIG_PATH = "../conf/default.properties";
	
	public static final String CRYPT_PASSWORD = "report_service";
	
	// Variables used to define the keys that specify attributes of a node in
	// a map
	public static final String NODE_ID_KEY = "id";
	public static final String NODE_NODE_KEY = "node";
	public static final String NODE_HOST_KEY = "host";
	public static final String NODE_USER_KEY = "user";
	public static final String NODE_PATH_KEY = "path";
	public static final String NODE_PASSWORD_KEY = "password";
	public static final String NODE_ENCRYPT_KEY = "encrypt";
	public static final String NODE_XFER_TYPE_KEY = "xfertype";
	public static final String NODE_STAGING_DIR_KEY = "stagingDir";
	
}
