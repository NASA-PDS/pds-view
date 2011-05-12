package gov.nasa.pds.report.update;

import gov.nasa.pds.report.update.cli.options.Flag;
import gov.nasa.pds.report.update.db.DBUtil;
import gov.nasa.pds.report.update.logging.ToolsLevel;
import gov.nasa.pds.report.update.logging.ToolsLogRecord;
import gov.nasa.pds.report.update.model.LogPath;
import gov.nasa.pds.report.update.model.LogSet;
import gov.nasa.pds.report.update.model.Profile;
import gov.nasa.pds.report.update.properties.EnvProperties;
import gov.nasa.pds.report.update.util.BasicUtil;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
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

public class RSUpdateLauncher {
	
	private static final String PROPS_HOME = "../conf/";
	
    /** logger object. */
    private static Logger log = Logger.getLogger(
            RSUpdateLauncher.class.getName());
	
	private String propsHome;
    private String profileName;
    private String logFile;
    private boolean sawmillFlag;
	
    public RSUpdateLauncher() {
    	this.propsHome = PROPS_HOME;
    	this.logFile = null;
    	this.profileName = null;
    	this.sawmillFlag = true;
    }
    
    /**
     * A method to parse the command-line arguments.
     *
     * @param args The command-line arguments
     * @return A class representation of the command-line arguments
     *
     * @throws ParseException If there was an error during parsing.
     */
    public final CommandLine parse(final String[] args)
    throws ParseException {
        CommandLineParser parser = new GnuParser();
        return parser.parse(Flag.getOptions(), args);
    }
    
    /**
     * Examines the command-line arguments passed into the RSUpdate Tool
     * and takes the appropriate action based on what flags were set.
     *
     * @param line A class representation of the command-line arguments.
     *
     * @throws Exception If there was an error while querying the options
     * that were set on the command-line.
     */
    public final void query(final CommandLine line) throws Exception {
        List<Option> processedOptions = Arrays.asList(line.getOptions());
        for (Option o : processedOptions) {
            if (o.getOpt().equals(Flag.HELP.getShortName())) {
                displayHelp();
                System.exit(0);
            } else if (o.getOpt().equals(Flag.PROPERTIES.getShortName())) {
                this.propsHome = o.getValue();
            } else if (o.getOpt().equals(Flag.LOG.getShortName())) {
                this.logFile = o.getValue();
            } else if (o.getOpt().equals(Flag.PROFILE_NAME.getShortName())) {
                this.profileName = o.getValue();
            }  else if (o.getOpt().equals(Flag.SAWMILL_OFF.getShortName())) {
                this.sawmillFlag = false;
            }
        }

        setLogger();
        logHeader();
    }
    
    /**
     * Logs header information for the log output.
     *
     */
    private void logHeader() {
        log.log(new ToolsLogRecord(ToolsLevel.CONFIGURATION,
                "PDS Sawmill Update Tool Log\n"));
        log.log(new ToolsLogRecord(ToolsLevel.CONFIGURATION,
                "Time                " + BasicUtil.getDateTime()));
        log.log(new ToolsLogRecord(ToolsLevel.CONFIGURATION,
                "Properties Home   " + this.propsHome + "\n"));
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
//        Handler []handler = logger.getHandlers();
        /*for (int i = 0; i < logger.getHandlers().length; i++) {
            logger.removeHandler(handler[i]);
        }
        if (logFile != null) {
            logger.addHandler(new HarvestFileHandler(logFile,
                    Level.INFO, new HarvestFormatter()));
        } else {
            logger.addHandler(new HarvestStreamHandler(System.out,
                    Level.INFO, new HarvestFormatter()));
        }*/
    }
    
    /**
     * Displays tool usage.
     *
     */
    public final void displayHelp() {
        int maxWidth = 80;
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(maxWidth, "Harvest <policy file> <options>",
                null, Flag.getOptions(), null);
    }

    /**
     * Closes the handlers for the logger.
     *
     */
    private void closeHandlers() {
        Logger logger = Logger.getLogger("");
        Handler []handlers = logger.getHandlers();
        for (int i = 0; i < logger.getHandlers().length; i++) {
            handlers[i].close();
        }
    }
    
    /**
     * Executes the necessary functions in order to copy the log files and update the database.
     * @throws RSUpdateException
     * @throws SQLException
     */
    private void execute() throws RSUpdateException, SQLException {
		try {
			/* Use propsHome to create DBUtil object */
	    	DBUtil util = new DBUtil(this.propsHome);
	    	List<Profile> pList = new ArrayList<Profile>();
    	
	    	/* If profileName is given, query for profile, otherwise get all active Profiles from DB */
	    	if (this.profileName != null) {
	    		pList.add(util.findByProfileName(profileName));
	    	} else {
	    		pList = util.findAllProfiles();
	    	}
	    	
	    	/* Use propsHome to get Environment Properties */
	    	EnvProperties env = new EnvProperties(this.propsHome);
	    	
	    	ReportServiceUpdate rsUpdate = null;
			LogPath logPath = new LogPath(env.getSawmillLogHome());
			for (Profile profile : pList) {
				logPath.setProfileNode(profile.getNode());
				logPath.setProfileName(profile.getName());
				
				rsUpdate = new ReportServiceUpdate(logPath, profile.getName(), false);

				List<LogSet> logSets = profile.getLogSetList();
				
				for (LogSet ls : logSets) {
					RSUpdateLauncher.log.info("In transfer - "+ ls.getLabel());
					rsUpdate.transferLogs(ls.getHostname(), ls.getUsername(), ls.getPassword(), ls.getPathname(), ls.getLabel());
				}
				
				/* Depending on whether sawmillFlag was specified, update Sawmill DB */
				if (this.sawmillFlag) {
					rsUpdate.updateSawmill(env.getSawmillHome());
				}
			}
		} catch (NullPointerException e) {
			throw new RSUpdateException("Profile not found.");
		} catch (IOException e) {
			throw new RSUpdateException("Properties file path is invalid.  Please re-enter.");
		}
    }
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("\nType 'RSUpdate -h' for usage");
            System.exit(0);
        }
        try {
            RSUpdateLauncher launcher = new RSUpdateLauncher();
            CommandLine commandline = launcher.parse(args);
            launcher.query(commandline);
            launcher.execute();
            
            //launcher.closeHandlers();
        } catch (Exception e) {
        	e.printStackTrace();
        	System.out.println(e.getMessage());
        	System.exit(1);
        }
	}

}
