// Copyright 2006-2010, by the California Institute of Technology.
// ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
// Any commercial use must be negotiated with the Office of Technology Transfer
// at the California Institute of Technology.
//
// This software is subject to U. S. export control laws and regulations
// (22 C.F.R. 120-130 and 15 C.F.R. 730-774). To the extent that the software
// is subject to U.S. export control laws and regulations, the recipient has
// the responsibility to obtain export licenses or other export authority as
// may be required before exporting such information to foreign countries or
// providing access to foreign nationals.
//
// $Id$
package gov.nasa.pds.report.update.cli.options;

import org.apache.commons.cli.Options;

/**
 * Class that holds the command-line option flags.
 *
 * @author jpadams
 *
 */
public enum Flag {
    /** Flag to display the help. */
    HELP("h", "help", "Display usage."),

    /** Flag to output the logging to a file. */
    LOG("l", "log-file", "file name", String.class,
            "Specify a log file name. Default is standard out."),

    /** Flag for the path where environment and database properties files are located. */
    PROPERTIES("p", "properties-home", "file path", String.class,
            "Specify the directory path where the *.properties files are located. Default location is ../conf/ ."),
    
    /** Flag for the name of a specific profile to update. */
    PROFILE_NAME("n", "profile-name", "profile name", String.class,
    		"Specify the name of a specific profile to update."),

    /** Flag to only transfer the logs and not update the Sawmill profile. */
    SAWMILL_OFF("s", "sawmill-off", "Specify flag to turn off updating the Sawmill DB after downloading the remote logs.  Default = ON");

    /** The short name of the flag. */
    private final String shortName;

    /** The long name of the flag. */
    private final String longName;

    /** An argument name for the flag, if it accepts argument values. */
    private final String argName;

    /** The type of argument values the flag accepts. */
    private final Object argType;

    /** A boolean value indicating if the flag accepts more than one
     * argument.
     */
    private final boolean allowsMultipleArgs;

    /** The flag description. */
    private final String description;

    /** A list of Option objects for command-line processing. */
    private static Options options;

    /**
     * Constructor.
     *
     * @param shortName The short name.
     * @param longName The long name.
     * @param description A description of the flag.
     */
    private Flag(final String shortName, final String longName,
            final String description) {
        this(shortName, longName, null, null, description);
    }

    /**
     * Constructor for flags that can take arguments.
     *
     * @param shortName The short name.
     * @param longName The long name.
     * @param argName The argument name.
     * @param argType The argument type.
     * @param description A description of the flag.
     */
    private Flag(final String shortName, final String longName,
            final String argName, final Object argType,
            final String description) {
        this(shortName, longName, argName, argType, false, description);
    }

    /**
     * Constructor for flags that can take arguments.
     *
     * @param shortName The short name.
     * @param longName The long name.
     * @param argName The argument name.
     * @param argType The argument type.
     * @param description A description of the flag.
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
     * Get the short name of the flag.
     *
     * @return The short name.
     */
    public String getShortName() {
        return shortName;
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
     * Get the argument name of the flag.
     *
     * @return The argument name.
     */
    public String getArgName() {
        return argName;
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

    static {
        options = new Options();

        options.addOption(new ToolsOption(HELP));
        options.addOption(new ToolsOption(LOG));
        options.addOption(new ToolsOption(PROPERTIES));
        options.addOption(new ToolsOption(PROFILE_NAME));
        options.addOption(new ToolsOption(SAWMILL_OFF));
    }

    /**
     * Get the command-line options.
     *
     * @return A class representation of the command-line options.
     */
    public static Options getOptions() {
        return options;
    }
}
