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
    public final static int SHORT = 0;
    public final static int LONG = 1;
    public final static int ARGNAME = 2;

    public final static String HELP[] = {"h", "help"};
    public final static String LOG[] = {"l", "log-file", "file name"};
    public final static String PASSWORD[] = {"p", "password", "pwd"};
    public final static String REPORT[] = {"r", "report", "file name"};
    public final static String USERNAME[] = {"u", "username", "name"};
    public final static String VERSION[] = {"V", "version"};

    public final static String WHATIS_HELP = "Display usage.";
    public final static String WHATIS_LOG = "Specify a log file name. Default is standard out.";
    public final static String WHATIS_PASSWORD = "Specify the password associated with the username";
    public final static String WHATIS_REPORT = "Specify the report file name. Default is standard out.";
    public final static String WHATIS_USERNAME = "Specify a username to login to the PDS security service";
    public final static String WHATIS_VERSION = "Display application version.";
}
