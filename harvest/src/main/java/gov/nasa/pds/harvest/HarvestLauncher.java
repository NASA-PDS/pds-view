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
package gov.nasa.pds.harvest;

import gov.nasa.pds.harvest.commandline.options.HarvestFlags;
import gov.nasa.pds.harvest.commandline.options.InvalidOptionException;
import gov.nasa.pds.harvest.commandline.options.ToolsOption;
import gov.nasa.pds.harvest.crawler.HarvestCrawler;
import gov.nasa.pds.harvest.logging.ToolsLevel;
import gov.nasa.pds.harvest.logging.ToolsLogRecord;
import gov.nasa.pds.harvest.logging.formatter.HarvestFormatter;
import gov.nasa.pds.harvest.logging.handler.HarvestFileHandler;
import gov.nasa.pds.harvest.logging.handler.HarvestStreamHandler;
import gov.nasa.pds.harvest.policy.Policy;
import gov.nasa.pds.harvest.policy.PolicyReader;
import gov.nasa.pds.harvest.security.SecurityClient;
import gov.nasa.pds.harvest.security.SecurityClientException;
import gov.nasa.pds.harvest.security.SecuredUser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Wrapper class of the Harvest tool that handles the command-line processing
 *
 * @author mcayanan
 *
 */
public class HarvestLauncher implements HarvestFlags {
    private static Logger log = Logger.getLogger(HarvestCrawler.class.getName());
    private Options options;
    private final String PROPERTYFILE = "harvest.properties";
    private final String PROPERTYTOOLNAME = "harvest.name";
    private final String PROPERTYVERSION = "harvest.version";
    private final String PROPERTYDATE = "harvest.date";
    private final String PROPERTYCOPYRIGHT = "harvest.copyright";

    private File policy;
    private InputStream globalPolicy;
    private String password;
    private String username;
    private String registryURL;
    private String securityURL;
    private String logFile;

    /**
     * Default constructor
     *
     */
    public HarvestLauncher() {
        policy = null;
        password = null;
        username = null;
        registryURL = null;
        securityURL = null;
        logFile = null;

        options = buildOptions();
        globalPolicy = this.getClass().getResourceAsStream("global-policy.xml");
    }

    /**
     * Builds the command-line options for the Harvest Tool
     *
     * @return an Options class
     */
    private Options buildOptions() {
        ToolsOption to = null;
        Options options = new Options();

        options.addOption(new ToolsOption(HELP[SHORT], HELP[LONG], WHATIS_HELP));
        options.addOption(new ToolsOption(VERSION[SHORT], VERSION[LONG], WHATIS_VERSION));

        to = new ToolsOption(PASSWORD[SHORT], PASSWORD[LONG], WHATIS_PASSWORD);
        to.hasArg(PASSWORD[ARGNAME], String.class);
        options.addOption(to);

        to = new ToolsOption(USERNAME[SHORT], USERNAME[LONG], WHATIS_USERNAME);
        to.hasArg(USERNAME[ARGNAME], String.class);
        options.addOption(to);

        to = new ToolsOption(LOG[SHORT], LOG[LONG], WHATIS_LOG);
        to.hasArg(LOG[ARGNAME], String.class);
        options.addOption(to);

        return options;
    }

    /**
     * A method to parse the command-line arguments
     *
     * @param args The command-line arguments
     * @return A class representation of the command-line arguments
     *
     * @throws ParseException
     * @throws InvalidOptionException
     */
    public CommandLine parse(String[] args)
    throws ParseException, InvalidOptionException {
        CommandLineParser parser = new GnuParser();
        return parser.parse(options, args);
    }

    /**
     * Examines the command-line arguments passed into the Harvest Tool
     * and takes the appropriate action based on what flags were set.
     *
     * @param line A class representation of the command-line arguments.
     *
     * @throws Exception
     */
    public void query(CommandLine line) throws Exception {
        registryURL = System.getProperty("pds.registry");
        securityURL = System.getProperty("pds.security");
        if(registryURL == null) {
            System.err.println("\'pds.registry\' java property is not set.");
            System.exit(1);
        }
        List<Option> processedOptions = Arrays.asList(line.getOptions());
        for(Option o : processedOptions) {
            if(o.getOpt().equals(HELP[SHORT])) {
                displayHelp();
                System.exit(0);
            }
            else if(o.getOpt().equals(VERSION[SHORT])) {
                displayVersion();
                System.exit(0);
            }
            else if(o.getOpt().equals(PASSWORD[SHORT]))
                password = o.getValue();
            else if(o.getOpt().equals(USERNAME[SHORT]))
                username = o.getValue();
            else if(o.getOpt().equals(LOG[SHORT]))
                logFile = o.getValue();
        }
        if(line.getArgList().size() != 0) {
            policy = new File(line.getArgList().get(0).toString());
            if(!policy.exists()) {
                throw new InvalidOptionException(
                        "Policy file does not exist: " + policy);
            }
        }
        else {
            throw new InvalidOptionException(
                    "Policy file not found on the command-line.");
        }

        if((securityURL != null) && (username == null || password == null)) {
            throw new InvalidOptionException(
                    "Username and/or password must be specified.");
        }
        setLogger();
        logHeader();
    }

    /**
     * Logs header information for the log output.
     *
     * @throws IOException
     */
    private void logHeader() throws IOException {
        Properties p = new Properties();
        InputStream in = null;
        try {
           in = this.getClass().getResource(PROPERTYFILE).openStream();
           p.load(in);
        } finally {
            in.close();
        }
        SimpleDateFormat df = new SimpleDateFormat(
                "EEE, MMM dd yyyy 'at' hh:mm:ss a");
        Date date = Calendar.getInstance().getTime();

        log.log(new ToolsLogRecord(ToolsLevel.CONFIGURATION,
                "PDS Harvest Tool Log\n"));
        log.log(new ToolsLogRecord(ToolsLevel.CONFIGURATION,
                "Version             " + p.getProperty(PROPERTYVERSION)));
        log.log(new ToolsLogRecord(ToolsLevel.CONFIGURATION,
                "Time                " + df.format(date)));
        log.log(new ToolsLogRecord(ToolsLevel.CONFIGURATION,
                "Registry Location   " + registryURL.toString() + "\n"));
    }

    /**
     * Sets the appropriate handlers for the logging.
     *
     * @throws Exception
     */
    private void setLogger() throws Exception {
        Logger logger = Logger.getLogger("");
        logger.setLevel(Level.ALL);
        Handler []handler = logger.getHandlers();
        for(int i=0; i<logger.getHandlers().length; i++)
            logger.removeHandler(handler[i]);

        if(logFile != null) {
            logger.addHandler(new HarvestFileHandler(logFile,
                    Level.INFO, new HarvestFormatter()));
        }
        else {
            logger.addHandler(new HarvestStreamHandler(System.out,
                    Level.INFO, new HarvestFormatter()));
        }
    }

    /**
     * Displays the current version and disclaimer notice.
     *
     * @throws IOException
     */
    public void displayVersion() throws IOException {
        URL propertyFile = this.getClass().getResource(PROPERTYFILE);
        Properties p  = new Properties();
        InputStream in = null;
        try {
            in = propertyFile.openStream();
            p.load(in);
        } finally {
            in.close();
        }
        System.err.println("\n" + p.get(PROPERTYTOOLNAME));
        System.err.println(p.get(PROPERTYVERSION));
        System.err.println("Release Date: " + p.get(PROPERTYDATE));
        System.err.println(p.get(PROPERTYCOPYRIGHT) + "\n");
    }

    /**
     * Displays tool usage.
     *
     */
    public void displayHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(80, "Harvest <policy file> <options>",
                null, options, null);
    }

    /**
     * Closes the handlers for the logger
     *
     */
    private void closeHandlers() {
        Logger logger = Logger.getLogger("");
        Handler []handlers = logger.getHandlers();
        for(int i = 0; i < logger.getHandlers().length; i++)
            handlers[i].close();
    }

    public static void main(String []args) {
        if(args.length == 0) {
            System.out.println("\nType 'Harvest -h' for usage");
            System.exit(0);
        }
        Harvester harvester = null;
        SecurityClient securityClient = null;
        SecuredUser securedUser = null;
        try {
            HarvestLauncher launcher = new HarvestLauncher();
            CommandLine commandline = launcher.parse(args);
            launcher.query(commandline);
            Policy policy = PolicyReader.unmarshall(launcher.policy);
            Policy globalPolicy = PolicyReader.unmarshall(
                    launcher.globalPolicy);
            policy.add(globalPolicy);
            if(launcher.securityURL == null) {
                harvester = new Harvester(launcher.registryURL,
                        policy.getCandidates());
            } else {
                securityClient = new SecurityClient(launcher.securityURL);
                securedUser = new SecuredUser(launcher.username,
                        securityClient.authenticate(
                                launcher.username, launcher.password));
                harvester = new Harvester(launcher.registryURL,
                        policy.getCandidates(), securedUser);
            }
            for(String inventoryFile :
                policy.getInventoryFiles().getLocation()) {
                harvester.harvestInventory(new File(inventoryFile));
            }
            for(String directory :
                policy.getRootDirectories().getLocation()) {
                harvester.harvest(new File(directory),
                        policy.getRootDirectories().getFilePattern());
            }
            launcher.closeHandlers();
        } catch(JAXBException je) {
            //Don't do anything
        } catch (ParseException pEx) {
            System.err.println("Command-line parse failure: "
                    + pEx.getMessage());
            System.exit(1);
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
        } finally {
            if(securedUser != null) {
                try {
                    securityClient.logout(securedUser.getToken());
                } catch (SecurityClientException se) {
                    System.err.println(se.getMessage());
                    System.exit(1);
                }
            }
        }
    }
}
