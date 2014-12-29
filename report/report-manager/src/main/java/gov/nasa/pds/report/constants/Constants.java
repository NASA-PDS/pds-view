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
	
	// ------------------------------------------------------------------------
	// Node attribute keys
	// ------------------------------------------------------------------------
	
	public static final String NODE_ID_KEY = "id";
	public static final String NODE_NODE_KEY = "node";
	public static final String NODE_HOST_KEY = "host";
	public static final String NODE_USER_KEY = "user";
	public static final String NODE_PATH_KEY = "path";
	public static final String NODE_PASSWORD_KEY = "password";
	public static final String NODE_ENCRYPT_KEY = "encrypt";
	public static final String NODE_XFER_TYPE_KEY = "xfertype";
	public static final String NODE_FILENAME_PATTERN_KEY = "filename_pattern";
	public static final String NODE_PROCESSES_KEY = "processes";
	public static final String NODE_SAWMILL_PROFILE = "sawmill_profile";
	public static final String NODE_REPORT_LIST = "sawmill_report";
	public static final String NODE_SAWMILL_OUTPUT = "sawmill_output";
	
	// ------------------------------------------------------------------------
	// System properties defined in defaults file
	// ------------------------------------------------------------------------
	
	// The key for the system property that specifies the root of the
	// directory tree where logs are stored
	public static final String DIR_ROOT_PROP = "gov.nasa.pds.report.dir.root";
	
	// The key for the system property that specifies the root of the
	// directory tree where profiles are stored
	public static final String PROFILE_HOME_PROP =
			"gov.nasa.pds.report.profile.dir";
	
	// The key for the system property that specifies the directory where the
	// Sawmill executable resides
	public static final String SAWMILL_HOME_PROP =
			"gov.nasa.pds.report.sawmill.home";
	
	// The key for the system property that specifies how many milliseconds to
	// wait before timing out a job run on the command line
	public static final String COMMANDLINE_TIMEOUT_PROP =
			"gov.nasa.pds.report.commandline.timeout";
	
	// The key for the system property that specifies how old a file should be
	// (in milliseconds) before we no longer hold on to it
	public static final String LOG_AGE_PROP = "gov.nasa.pds.report.log.age";
	

	// ------------------------------------------------------------------------
	// Major directories in the directory tree
	// ------------------------------------------------------------------------
	
	public static final String STAGING_DIR = "staging";
	public static final String PROCESSING_DIR = "processing";
	public static final String SAWMILL_DIR = "final";
	public static final String BACKUP_DIR = "backup";
	
}
