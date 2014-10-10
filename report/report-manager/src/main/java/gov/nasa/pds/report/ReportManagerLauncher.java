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

package gov.nasa.pds.report;

import gov.nasa.pds.report.cli.options.Flag;
import gov.nasa.pds.report.cli.options.InvalidOptionException;
import gov.nasa.pds.report.constants.Constants;
import gov.nasa.pds.report.logging.ToolsLevel;
import gov.nasa.pds.report.logging.ToolsLogRecord;
import gov.nasa.pds.report.logging.formatter.ReportManagerFormatter;
import gov.nasa.pds.report.logging.handler.PDSFileHandler;
import gov.nasa.pds.report.logging.handler.PDSStreamHandler;
import gov.nasa.pds.report.util.ToolInfo;
import gov.nasa.pds.report.util.Utility;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
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
	
	private ReportServiceManager rsMgr;
	
	/** The severity level to set for the tool. */
	private Level severityLevel;

	public ReportManagerLauncher() {
		this.pullFlag = false;
		this.rsMgr = new ReportServiceManager();
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
			} else if (o.getOpt().equals(Flag.NODE_PATTERN.getShortName())) {
				this.rsMgr.addProfileFilter(Constants.NODE_NODE_KEY,
						line.getOptionValue(Flag.NODE_PATTERN.getShortName()));
			} else if (o.getOpt().equals(Flag.ID_PATTERN.getShortName())) {
				this.rsMgr.addProfileFilter(Constants.NODE_ID_KEY,
						line.getOptionValue(Flag.ID_PATTERN.getShortName()));
			}
		}
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
	 */
	public void execute(){
		
		// Read the profiles from disk
		try{
			// TODO: Allow the user to override the profile location using
			// rsMgr.setProfileDir()
			this.rsMgr.readProfiles();
		}catch(IOException e){
			log.severe("An error occurred while reading the profile " +
					"properties: " + e.getMessage());
		}
		
		// Pull the logs if specified
		if (this.pullFlag) {
			this.rsMgr.pullLogs();
		}
		
	}
	
	private void loadConfiguration(){
		
		try{
			System.getProperties().load(new FileInputStream(
					Constants.DEFAULT_CONFIG_PATH));
		}catch(IOException e){
			log.severe("An error occurred while reading in the default " +
					"configuration from " + Constants.DEFAULT_CONFIG_PATH + 
					": " + e.getMessage());
			System.exit(1);
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
			launcher.loadConfiguration();
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
