package gov.nasa.pds.search.core.cli.options;

import gov.nasa.pds.search.core.constants.Constants;

import org.apache.commons.cli.Options;

public enum Flag {
	/** Flag to run all components of index */
	ALL("a", "all",
			"Run all components of the Search Core [default]"),
			
	/** Flag to specify a product class configuration directory. */
	CONFIG_HOME("c", "config-home", "directory", String.class, true,
			"Specify the product class configuration home directory (dir must contain product-classes.txt) (Default: $SEARCH_CORE_HOME/conf/pds/"),
			
	/** Flag to turn OFF removal of all directories from previous Search Core execution */
	CLEAN("C", "clean-dirs", "Removal of all directories from previous Search Core execution output. These directories will still be backed up in the Search Service Home directory. (Default: True)"),
			
	/** Flag to run in debug mode. */
	DEBUG("d", "debug", "Turn on debugger."),
			
	/** Flag to run the Registry Extractor component. */
	EXTRACTOR("e", "extractor", "Execute component to extract data from registry"),

	/** Flag to display the help. */
	HELP("h", "help", "Display usage."),
	
	/** Flag to specify Search Service Home. */
	SERVICE_HOME("H", "search-service-home", "directory", String.class,
			"Specify the Search Service Home directory. (Default: SEARCH_SERVICE_HOME environment variable)"),
	
	/** Flag to generate a Solr Index. */
	SOLR("i", "solr-indexer", "Execute component to generate a Solr Index"),

	/** Flag to specify query max for registry. */
	MAX("m", "query-max", "integer", Integer.class,
			"Specify the maximum number of registry values to be returned from query. (Default: " + Constants.QUERY_MAX + ")"),
	
	/** Flag to specify a configuration file. */
	PROPERTIES("p", "properties-file", "directory", String.class, true,
			"Specify properties file containing Search Service Home, Registry URL, and product class configuration home directory."),
			
	/** Flag to specify at least one Registry URL. */
	REGISTRY("r", "registry", "url", String.class, true,
			"Specify Registry Service instance to query. <REQUIRED>"),
			
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

	/**
	 * A boolean value indicating if the flag accepts more than one argument.
	 */
	private final boolean allowsMultipleArgs;

	/** The flag description. */
	private final String description;

	/** A list of Option objects for command-line processing. */
	private static Options options;

	static {
		options = new Options();

		options.addOption(new ToolsOption(HELP));
		options.addOption(new ToolsOption(VERSION));
		options.addOption(new ToolsOption(SOLR));
		options.addOption(new ToolsOption(EXTRACTOR));
		options.addOption(new ToolsOption(ALL));
		options.addOption(new ToolsOption(DEBUG));
		options.addOption(new ToolsOption(REGISTRY));
		options.addOption(new ToolsOption(SERVICE_HOME));
		options.addOption(new ToolsOption(MAX));
		options.addOption(new ToolsOption(CONFIG_HOME));
		options.addOption(new ToolsOption(CLEAN));
		options.addOption(new ToolsOption(PROPERTIES));
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
