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
package gov.nasa.pds.harvest.commandline.options;

/**
 * Class to hold the command-line option flags.
 *
 * @author mcayanan
 *
 */
public interface HarvestFlags {
    public static final int SHORT = 0;
    public static final int LONG = 1;
    public static final int ARGNAME = 2;

    public static final String HELP[] = {"h", "help"};
    public static final String LOG[] = {"l", "log-file", "file name"};
    public static final String PASSWORD[] = {"p", "password", "pwd"};
    public static final String REPORT[] = {"r", "report", "file name"};
    public static final String USERNAME[] = {"u", "username", "name"};
    public static final String VERSION[] = {"V", "version"};

    public static final String WHATIS_HELP = "Display usage.";
    public static final String WHATIS_LOG = "Specify a log file name. "
        + "Default is standard out.";
    public static final String WHATIS_PASSWORD = "Specify the password"
        + " associated with the username";
    public static final String WHATIS_REPORT = "Specify the report file name."
        + " Default is standard out.";
    public static final String WHATIS_USERNAME = "Specify a username to login"
        + " to the PDS security service";
    public static final String WHATIS_VERSION = "Display application version.";
}
