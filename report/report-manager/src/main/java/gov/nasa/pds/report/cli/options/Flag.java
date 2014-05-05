//	Copyright 2009-2013, by the California Institute of Technology.
//	ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
//	Any commercial use must be negotiated with the Office of Technology 
//	Transfer at the California Institute of Technology.
//	
//	This software is subject to U. S. export control laws and regulations 
//	(22 C.F.R. 120-130 and 15 C.F.R. 730-774). To the extent that the software 
//	is subject to U.S. export control laws and regulations, the recipient has 
//	the responsibility to obtain export licenses or other export authority as 
//	may be required before exporting such information to foreign countries or 
//	providing access to foreign nationals.
//	
//	$Id: Flag.java 12298 2013-10-23 18:14:35Z jpadams $
//

package gov.nasa.pds.report.cli.options;

import gov.nasa.pds.report.logs.pushpull.OODTPushPull;

import org.apache.commons.cli.Options;

/**
 * Describes all the flags used in the CLI
 * 
 * @author jpadams
 * 
 */
public enum Flag {
	/** Flag to run Logs Manager component only */
	PULL("p", "pull-logs", "Pull logs from remote sources."),
	
	/** Flag to specify file path to PushPull properties file*/
	PP_PROPERTIES("r", "pushpull_properties", "file-path", 
			String.class, "File path to PushPull push_pull_framework.properties file. Default: $REPORT_MGR_HOME/etc/push_pull_framework.properties"),
	
	/** Flag to specify Push Pull Port Home  */
	PP_PORT("P", "cas_pp_port", "port", 
			String.class, "Port number to run CAS PushPull. Default: " + OODTPushPull.DEFAULT_PORT),
			
	/** Flag to specify Push Pull Resources Home */
	PP_SPECS("s", "pushpull_specs", "file-path", 
			String.class, "File path to CAS PushPull Remote Sites specification file. Default: $REPORT_MGR_HOME/etc/conf/RemoteSpecs.xml"),
	
	/** Flag to specify staging area for logs only */
	PP_STAGING("s", "logs_staging", "path", 
			String.class, "Base directory path to pull logs. Default: $LOGS_HOME environment variable."),
			
			
	/** PDS Default Flags for CLI - TODO should be refactored into commons enum **/
			
	/** Flag to display the help. */
	HELP("h", "help", "Display usage."),

	/** Flag to output the logging to a file. */
	LOG("l", "log-file", "file name", String.class,
			"Specify a log file name. Default is standard out."),

	/** Flag to change the severity level of the messaging in the report. */
	VERBOSE("v", "verbose", "level", int.class, "Specify the severity level "
			+ "and above to include in the log: "
			+ "(0=Debug, 1=Info, 2=Warning, 3=Error). "
			+ "Default is Info and above (level 1)."),

	/** Flag to display the version. */
	VERSION("V", "version", "Display application version.");

	/** The short name of the flag. */
	private final String shortName;

	/** The long name of the flag. */
	private final String longName;

	/** An argument name for the flag, if it accepts argument values. */
	private final String argName;

	/** The type of argument values the flag accepts. */
	private final Object argType;

	/** A boolean value indicating if the flag accepts more than one argument. */
	private final boolean allowsMultipleArgs;

	/** The flag description. */
	private final String description;

	/** A list of Option objects for command-line processing. */
	private static Options options;

	static {
		options = new Options();

		for (Flag flag : values()) {
			options.addOption(new ToolsOption(flag));
		}
	}

	/**
	 * Get the command-line options.
	 * 
	 * @return A class representation of the command-line options.
	 */
	public static Options getOptions() {
		return options;
	}

	/**
	 * Constructor.
	 * 
	 * @param shortName
	 *            The short name.
	 * @param longName
	 *            The long name.
	 * @param description
	 *            A description of the flag.
	 */
	private Flag(final String shortName, final String longName,
			final String description) {
		this(shortName, longName, null, null, description);
	}

	/**
	 * Constructor for flags that can take arguments.
	 * 
	 * @param shortName
	 *            The short name.
	 * @param longName
	 *            The long name.
	 * @param argName
	 *            The argument name.
	 * @param argType
	 *            The argument type.
	 * @param description
	 *            A description of the flag.
	 */
	private Flag(final String shortName, final String longName,
			final String argName, final Object argType,
			final boolean allowsMultipleArgs, final String description) {
		this.shortName = shortName;
		this.longName = longName;
		this.argName = argName;
		this.argType = argType;
		this.allowsMultipleArgs = allowsMultipleArgs;
		this.description = description;
	}

	/**
	 * Constructor for flags that can take arguments.
	 * 
	 * @param shortName
	 *            The short name.
	 * @param longName
	 *            The long name.
	 * @param argName
	 *            The argument name.
	 * @param argType
	 *            The argument type.
	 * @param description
	 *            A description of the flag.
	 */
	private Flag(final String shortName, final String longName,
			final String argName, final Object argType, final String description) {
		this(shortName, longName, argName, argType, false, description);
	}

	/**
	 * Find out if the flag can handle multiple arguments.
	 * 
	 * @return 'true' if yes.
	 */
	public boolean allowsMultipleArgs() {
		return allowsMultipleArgs;
	}

	/**
	 * Get the argument name of the flag.
	 * 
	 * @return The argument name.
	 */
	public String getArgName() {
		return argName;
	}

	/**
	 * Get the argument type of the flag.
	 * 
	 * @return The argument type.
	 */
	public Object getArgType() {
		return argType;
	}

	/**
	 * Get the flag description.
	 * 
	 * @return The description.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Get the long name of the flag.
	 * 
	 * @return The long name.
	 */
	public String getLongName() {
		return longName;
	}

	/**
	 * Get the short name of the flag.
	 * 
	 * @return The short name.
	 */
	public String getShortName() {
		return shortName;
	}
}
