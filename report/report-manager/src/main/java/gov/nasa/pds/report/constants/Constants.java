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
	public static final String NODE_STAGING_PATH = "staging_path";
	public static final String NODE_DETAIL_REFORMAT_INPUT = "input_log_pattern";
	public static final String NODE_DETAIL_REFORMAT_OUTPUT = "output_log_pattern";
	public static final String NODE_LINE_REFORMAT_INPUT = "input_line_pattern";
	public static final String NODE_LINE_REFORMAT_OUTPUT = "output_line_pattern";
	public static final String NODE_SIMPLE_COMMAND_KEY = "simple_command";
	public static final String NODE_NCFTP_VERSION = "ncftp_version";
	public static final String NODE_NCFTP_REFORMAT_OUTPUT = "output_ncftp_pattern";
	public static final String NODE_COPY_INPUT = "copy_input";
	
	// ------------------------------------------------------------------------
	// System properties defined in defaults file
	// ------------------------------------------------------------------------
	
	// The root of the directory tree where logs are stored
	public static final String DIR_ROOT_PROP = "gov.nasa.pds.report.dir.root";
	
	// The root of the directory tree where profiles are stored
	public static final String PROFILE_HOME_PROP =
			"gov.nasa.pds.report.profile.dir";
	
	// The directory where the Sawmill executable resides
	public static final String SAWMILL_HOME_PROP =
			"gov.nasa.pds.report.sawmill.home";
	
	// How many milliseconds to wait before timing out a job run on the command
	// line
	public static final String COMMANDLINE_TIMEOUT_PROP =
			"gov.nasa.pds.report.commandline.timeout";
	
	// How old a file should be (in milliseconds) before we no longer hold on
	// to it
	public static final String LOG_AGE_PROP = "gov.nasa.pds.report.log.age";
	
	// The root of the directory tree where Sawmill reports are placed
	public static final String SAWMILL_REPORT_PROP =
			"gov.nasa.pds.report.sawmill.report.root";
	
	// What SawmillInterface implementation to use to execute Sawmill
	// operations
	public static final String SAWMILL_INTERFACE_PROP =
			"gov.nasa.pds.report.sawmill.interface";
	
	// Where the Sawmill script is written if SawmillScriptWriter is the
	// SawmillInterface being used
	public static final String SAWMILL_SCRIPT_PROP =
			"gov.nasa.pds.report.sawmill.SawmillScriptWriter.output.location";
	
	// The number of error-causing lines tolerated in log beings reformatted
	// before we give up on the file
	public static final String REFORMAT_ERRORS_PROP =
			"gov.nasa.pds.report.processing.errors.allowed";
	
	// The type of DateLogFilter to use
	public static final String DATE_FILTER_PROP =
			"gov.nasa.pds.report.date.filter";
	
	// The length (in days) of the reporting window at the beginning of each
	// month
	public static final String REPORT_WINDOW_PROP =
			"gov.nasa.pds.report.report.window";
	
	// ------------------------------------------------------------------------
	// Major directories in the log storge and processing tree
	// ------------------------------------------------------------------------
	
	public static final String STAGING_DIR = "staging";
	public static final String PROCESSING_DIR = "processing";
	public static final String SAWMILL_DIR = "final";
	public static final String BACKUP_DIR = "backup";
	
}
