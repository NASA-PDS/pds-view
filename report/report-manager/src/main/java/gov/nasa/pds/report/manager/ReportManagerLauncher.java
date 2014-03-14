//	Copyright 2013, by the California Institute of Technology.
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
//	$Id: RSUpdateLauncher.java 11670 2013-06-20 17:14:33Z jpadams $
//

package gov.nasa.pds.report.manager;

import gov.nasa.pds.report.manager.cli.options.Flag;
import gov.nasa.pds.report.manager.cli.options.InvalidOptionException;
import gov.nasa.pds.report.manager.exception.ReportManagerFatalException;
import gov.nasa.pds.report.manager.logging.ToolsLevel;
import gov.nasa.pds.report.manager.logging.ToolsLogRecord;
import gov.nasa.pds.report.manager.logging.formatter.ReportManagerFormatter;
import gov.nasa.pds.report.manager.logging.handler.PDSFileHandler;
import gov.nasa.pds.report.manager.logging.handler.PDSStreamHandler;
import gov.nasa.pds.report.manager.logs.LogsManagerException;
import gov.nasa.pds.report.manager.util.ToolInfo;
import gov.nasa.pds.report.manager.util.Utility;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;
import org.omg.CORBA.portable.ApplicationException;

public class ReportManagerLauncher {

	/** logger object. */
	private static Logger log = Logger.getLogger(ReportManagerLauncher.class
			.getName());

	/** @see gov.nasa.pds.search.core.cli.options.Flag#LOG **/
	private String logFile;

	/** @see gov.nasa.pds.search.core.cli.options.Flag#PULL **/
	private boolean pullFlag;

	/** @see gov.nasa.pds.search.core.cli.options.Flag#PP_PORT **/
	private int ppPort;

	/** @see gov.nasa.pds.search.core.cli.options.Flag#PP_PROPERTIES **/
	private String ppResources;

	/** @see gov.nasa.pds.search.core.cli.options.Flag#PP_SPECS **/
	private String ppSpec;

	/** The severity level to set for the tool. */
	private Level severityLevel;

	public ReportManagerLauncher() {
		this.pullFlag = false;
		this.ppPort = -1;
		this.ppResources = "";
		this.ppSpec = "";
		this.severityLevel = ToolsLevel.INFO;
	}

	  /**
	   * Set the verbosity level and above to include in the reporting.
	   * @param v '1' for info, '2' for warnings, and '3' for errors
	   * @throws ApplicationException
	   */
	  private void setVerbose(int v) throws Exception {
	    if (v < 0 || v > 3) {
	      throw new Exception("Invalid value entered for 'v' flag. "
	          + "Valid values can only be 0, 1, 2, or 3");
	    }
	    if (v == 0) {
	      this.severityLevel = ToolsLevel.DEBUG;
	    } else if (v == 1) {
	    	this.severityLevel = ToolsLevel.INFO;
	    } else if (v == 2) {
	    	this.severityLevel = ToolsLevel.WARNING;
	    } else if (v == 3) {
	    	this.severityLevel = ToolsLevel.SEVERE;
	    }
	  }

	  /**
	   * Logs header information for the log output.
	   *
	   */
	  private void logHeader() {
	    log.log(new ToolsLogRecord(ToolsLevel.CONFIGURATION,
	        "PDS Report Service Manager Run Log\n"));
	    log.log(new ToolsLogRecord(ToolsLevel.CONFIGURATION,
	        "Version                     " + ToolInfo.getVersion()));
	    log.log(new ToolsLogRecord(ToolsLevel.CONFIGURATION,
	        "Time                        " + Utility.getDateTime()));
	    log.log(new ToolsLogRecord(ToolsLevel.CONFIGURATION,
	        "Severity Level              " + severityLevel.getName()));

	    log.log(new ToolsLogRecord(ToolsLevel.CONFIGURATION,
	    		"\n\n"));
	  }

	  /**
	   * Sets the appropriate handlers for the logging.
	   *
	   * @throws IOException If a log file was specified and could not
	   * be read.
	   */
	  private void setLogger() throws IOException {
	    Logger logger = Logger.getLogger("");
	    logger.setLevel(Level.ALL);
	    Handler []handler = logger.getHandlers();
	    for (int i = 0; i < logger.getHandlers().length; i++) {
	      logger.removeHandler(handler[i]);
	    }
	    if (this.logFile != null) {
	      logger.addHandler(new PDSFileHandler(this.logFile, this.severityLevel,
	          new ReportManagerFormatter()));
	    } else {
	      logger.addHandler(new PDSStreamHandler(System.out,
	    		  this.severityLevel, new ReportManagerFormatter()));
	    }

	    logHeader();
	  }

	/**
	 * Displays tool usage.
	 *
	 */
	public final void displayHelp() {
		final int maxWidth = 80;
		final HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp(maxWidth,
				"report-mgr [options]", null,
				Flag.getOptions(), null);
	}

	/**
	 * Displays the current version and disclaimer notice.
	 *
	 */
	public final void displayVersion() {
		System.err.println("\n" + ToolInfo.getName());
		System.err.println(ToolInfo.getVersion());
		System.err.println("Release Date: " + ToolInfo.getReleaseDate());
		System.err.println(ToolInfo.getCopyright() + "\n");
	}

	/**
	 * A method to parse the command-line arguments.
	 *
	 * @param args
	 *            The command-line arguments
	 * @return A class representation of the command-line arguments
	 *
	 * @throws ParseException
	 *             If there was an error during parsing.
	 */
	public final CommandLine parse(final String[] args) throws ParseException {
		CommandLineParser parser = new GnuParser();
		return parser.parse(Flag.getOptions(), args);
	}

	/**
	 * Examines the command-line arguments passed into the RSUpdate Tool and
	 * takes the appropriate action based on what flags were set.
	 *
	 * @param line
	 *            A class representation of the command-line arguments.
	 *
	 * @throws Exception
	 *             If there was an error while querying the options that were
	 *             set on the command-line.
	 */
	public final void query(final CommandLine line) throws Exception {
		List<Option> processedOptions = Arrays.asList(line.getOptions());
		for (Option o : processedOptions) {
			if (o.getOpt().equals(Flag.HELP.getShortName())) {
				displayHelp();
				System.exit(0);
			} else if (o.getOpt().equals(Flag.VERBOSE.getShortName())) {
				try {
					setVerbose(Integer.parseInt(o.getValue().trim()));
				} catch (NumberFormatException e) {
					throw new InvalidOptionException(
							"Invalid value entered for 'v' flag. "
									+ "Valid values can only be 0, 1, 2, or 3");
				}
			} else if (o.getOpt().equals(Flag.VERSION.getShortName())) {
				displayVersion();
				System.exit(0);
			} else if (o.getOpt().equals(Flag.PULL.getShortName())) {
				this.pullFlag = true;
			} else if (o.getOpt().equals(Flag.PP_PORT.getShortName())) {
				try {
					this.ppPort = Integer.parseInt(o.getValue());
				} catch (NumberFormatException e) {
					throw new ReportManagerFatalException("Invalid Port Number. Must be Integer value.");
				}
			} else if (o.getOpt().equals(Flag.PP_PROPERTIES.getShortName())) {
					this.ppResources = o.getValue();
			} else if (o.getOpt().equals(Flag.PP_SPECS.getShortName())) {
				this.ppSpec = o.getValue();
			}
		}

		setLogger();
	}

	/**
	 * Closes the handlers for the logger.
	 *
	 */
	private void closeHandlers() {
		Logger logger = Logger.getLogger("");
		Handler[] handlers = logger.getHandlers();
		for (int i = 0; i < logger.getHandlers().length; i++) {
			handlers[i].close();
		}
	}

	/**
	 * Executes the necessary functions in order to copy the log files and
	 * update the database.
	 * @throws LogsManagerException
	 *
	 * @throws ReportManagerException
	 * @throws SQLException
	 */
	public void execute() throws LogsManagerException {
		ReportServiceManager rsMgr = new ReportServiceManager(this.ppPort, this.ppResources, this.ppSpec);

		if (this.pullFlag) {
			rsMgr.pullLogs();
		}
	}
/*	private void execute() throws RSUpdateException, SQLException {
		try {
			 Use propsHome to create DBUtil object
			DBUtil util = new DBUtil(this.propsHome);
			List<Profile> pList = new ArrayList<Profile>();


			 * If profileName is given, query for profile, otherwise get all
			 * active Profiles from DB

			if (this.profileName != null) {
				pList.add(util.findByProfileName(profileName));
			} else {
				pList = util.findAllProfiles();
			}

			 Use propsHome to get Environment Properties
			EnvProperties env = new EnvProperties(this.propsHome);

			ReportServiceUpdate rsUpdate = null;
			LocalLogSource logPath = new LocalLogSource(env.getSawmillLogHome());
			for (Profile profile : pList) {
				logPath.setProfileNode(profile.getNode());
				logPath.setProfileName(profile.getName());

				rsUpdate = new ReportServiceUpdate(logPath, profile.getName(),
						false);

				List<LogSource> logSets = profile.getLogSetList();

				try {
					if (this.transferFlag) {
						for (LogSource ls : logSets) {
							rsUpdate.transferLogs(ls.getHostname(), ls.getUsername(),
									ls.getPassword(), ls.getPathname(), ls.getLogSrcName());
						}
					}
				} catch (NullPointerException e) {
					throw new RSUpdateException("Error transferring logs: " + e.getMessage());
				}



				 * Depending on whether sawmillFlag was specified, update
				 * Sawmill DB

				try {
					if (this.sawmillFlag) {
						rsUpdate.updateSawmill(env.getSawmillHome());
					}
				} catch (NullPointerException e) {
					throw new RSUpdateException("Profile not found: " + e.getMessage());
				}
			}
		} catch (IOException e) {
			throw new RSUpdateException(
					"Properties file path is invalid.  Please re-enter.");
		}
	}*/

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length == 0) {
			System.out.println("\nType 'report-mgr -h' for usage");
			System.exit(0);
		}
		try {
			ReportManagerLauncher launcher = new ReportManagerLauncher();
			CommandLine commandline = launcher.parse(args);
			launcher.query(commandline);
			launcher.execute();

			launcher.closeHandlers();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			System.exit(1);
		}
	}

}
