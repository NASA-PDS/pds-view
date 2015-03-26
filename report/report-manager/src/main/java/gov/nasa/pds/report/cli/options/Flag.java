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

import org.apache.commons.cli.Options;

/**
 * Describes all the flags used in the CLI
 * 
 * @author jpadams
 * 
 */
public enum Flag {
	
	/** Flag to instruct Logs Manager component to pull logs from nodes */
	PULL("p", "pull-logs", "Pull logs from remote sources."),
	
	/** Flag to instruct the Report Manager to process logs */
	PROCESS_LOGS("P", "process-logs", "Process already downloaded logs"),
	
	/** Flag to run a Sawmill command to update the Sawmill DB */
	UPDATE_SAWMILL("u", "update-sawmill", "Update the Sawmill DB"),
	
	/** Flag to run a Sawmill command to rebuild the Sawmill DB */
	REBUILD_SAWMILL("r", "rebuild-sawmill", "Rebuild the Sawmill DB"),
	
	/** Flag to run a Sawmill command to generate Sawmill reports */
	SAWMILL_REPORTS("R", "sawmill-reports", "Generate Sawmill reports"),
	
	/** Flag to filter by node name specified in profile  */
	NODE_PATTERN("n", "node-pattern", "pattern", String.class,
			"Only operate on profiles with a node whose name matches the " +
			"given RE pattern"),
	
	/** Flag to filter by profile ID */
	ID_PATTERN("i", "id-pattern", "pattern", String.class,
			"Only operate on profiles with an ID that matches the given RE " +
			"pattern"),
			
	/** Flag to specify a run name for generated reports */
	RUN_NAME("N", "run-name", "name", String.class,
			"Specify a run name for generated reports"),
	
	/** Flag to force a backup of logs in staging */
	FORCE_BACKUP("b", "backup",
			"Force a backup of all logs in the staging directory"), 
	
	/** Flag to cleanup logs in staging and old logs in final and backup */
	CLEANUP("c", "cleanup",
			"Cleanup logs in staging and old logs in final and backup"),
			
	/** Flags to filter log files by date specified in log filename */
	FROM_DATE("f", "from", "from_date", String.class,
			"Only pull log files on or after the date specified with the " +
			"format mm/dd/yyyy"),
	TO_DATE("t", "to", "to_date", String.class,
			"Only pull log files on or before the date specified with the " +
			"format mm/dd/yyyy"),
			
	/** PDS Default Flags for CLI - TODO should be refactored into commons enum **/
			
	/** Flag to display the help. */
	HELP("h", "help", "Display usage."),

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
