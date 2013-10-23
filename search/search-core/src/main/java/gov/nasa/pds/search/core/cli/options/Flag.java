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
//	$Id$
//

package gov.nasa.pds.search.core.cli.options;

import gov.nasa.pds.search.core.constants.Constants;

import org.apache.commons.cli.Options;

/**
 * Describes all the flags used in the CLI
 * 
 * @author jpadams
 * 
 */
public enum Flag {
	/** Flag to run all components of index. */
	ALL("a", "all", "Run all components of the Search Core [default]"),

	/** Flag to specify a product class configuration directory. */
	CONFIG_HOME(
			"c",
			"config-home",
			"directories",
			String.class,
			true,
			"Specify the product class configuration home directory."
					+ "Multiple directories can be specified to accompany"
					+ " multiple registries. (Default: $SEARCH_CORE_HOME/conf)"),

	/**
	 * Flag to turn OFF removal of all directories from previous Search Core
	 * execution.
	 */
	CLEAN(
			"C",
			"clean-dirs",
			"Removal of all directories from previous Search Core execution output."
					+ " These directories will still be backed up in the Search Home directory."
					+ " (Default: True)"),

	/** Flag to run in debug mode. */
	DEBUG("d", "debug", "Turn on developer debugger."),

	/** Flag to run the Registry Extractor component. */
	EXTRACTOR("e", "extractor",
			"Execute component to extract data from registry"),

	/** Flag to display the help. */
	HELP("h", "help", "Display usage."),

	/** Flag to output the logging to a file. */
	LOG("l", "log-file", "file name", String.class,
			"Specify a log file name. Default is standard out."),
			
	/** Flag to specify query max for registry. */
	MAX("m", "query-max", "integer", Integer.class,
			"Specify the maximum number of registry values to be returned from query."),
					
					
	/** Flag to post the index to the Search Service. */
	POST("P", "solr-post", "Execute component to post the index to the Search Service."),
					
	/** Flag to specify a configuration file. */
	PROPERTIES(
			"p",
			"properties-file",
			"files",
			String.class,
			true,
			"Specify properties file containing Search Home, Registry URL,"
					+ " and search core configurations home directory. Multiple"
					+ " files can be specified."),

	/** Flag to specify at least one Registry URL. */
	PRIMARY(
			"r",
			"primary-registry",
			"urls",
			String.class,
			true,
			"Specify the primary Registry Service instance(s) to query. "
					+ "Multiple registries can be specified. These registries will "
					+ "be used for all queries.  "),

	/** Flag to specify at least one Registry URL. */
	SECONDARY(
			"R",
			"secondary-registry",
			"urls",
			String.class,
			true,
			"Specify secondary Registry Service instance(s) to query. "
					+ "Multiple registries can be specified. These registries will "
					+ "only be used after a query fails against all primary registries."),

	/** Flag to specify Search Home. */
	SEARCH_HOME(
			"H",
			"search-home",
			"directory",
			String.class,
			"Specify the the directory for the Search Service core to add the index. "
					+ "For the PDS core, this value should be $SEARCH_SERVICE_HOME/pds. "
					+ "(Default: /usr/local/search-service/pds directory)"),

	/** Flag to generate a Solr Index. */
	SERVICE_URL("s", "service-url", "url", String.class, "Specify the Search Service URL endpoint."
			+ "Default: " + Constants.DEFAULT_SERVICE_URL),					
					
	/** Flag to generate a Solr Index. */
	SOLR("i", "solr-indexer", "Execute component to generate a Solr Index"),

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
