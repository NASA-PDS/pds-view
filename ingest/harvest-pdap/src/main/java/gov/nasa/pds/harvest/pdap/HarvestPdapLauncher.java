// Copyright 2006-2015, by the California Institute of Technology.
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
package gov.nasa.pds.harvest.pdap;

import gov.nasa.pds.harvest.pdap.constants.Constants;
import gov.nasa.pds.harvest.pdap.commandline.options.InvalidOptionException;
import gov.nasa.pds.harvest.pdap.commandline.options.Flag;
import gov.nasa.pds.harvest.pdap.logging.ToolsLevel;
import gov.nasa.pds.harvest.pdap.logging.ToolsLogRecord;
import gov.nasa.pds.harvest.pdap.logging.formatter.HarvestPdapFormatter;
import gov.nasa.pds.harvest.pdap.logging.handler.HarvestPdapFileHandler;
import gov.nasa.pds.harvest.pdap.logging.handler.HarvestPdapStreamHandler;
import gov.nasa.pds.harvest.pdap.policy.PdapService;
import gov.nasa.pds.harvest.pdap.policy.Policy;
import gov.nasa.pds.harvest.pdap.policy.PolicyReader;
import gov.nasa.pds.harvest.pdap.stats.HarvestPdapStats;
import gov.nasa.pds.harvest.pdap.util.ToolInfo;
import gov.nasa.pds.harvest.pdap.util.Utility;
import gov.nasa.pds.harvest.registry.PdsRegistryService;
import gov.nasa.pds.registry.client.RegistryClient;
import gov.nasa.pds.registry.client.SecurityContext;
import gov.nasa.pds.registry.exception.RegistryServiceException;
import gov.nasa.pds.registry.model.RegistryPackage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;

/**
 * Wrapper class to the Harvest-PDAP Tool. Class handles command-line
 * processing as well as reading of the policy config file.
 *
 * @author mcayanan
 *
 */
public class HarvestPdapLauncher {
  /** logger object. */
  private static Logger log = Logger.getLogger(
      HarvestPdapLauncher.class.getName());

  /** Policy file. */
  private File policy;

  /** The internal policy file. */
  private InputStream globalPolicy;

  /** password of the authorized user. */
  private String password;

  /** an authorized user. */
  private String username;

  /** URL of the registry service. */
  private PdsRegistryService registryService;

  /** Path to the self sign on keystore. */
  private String keystore;

  /** A log file name. */
  private String logFile;

  /** Security context to support handling of the PDS Security. */
  private SecurityContext securityContext;

  /** Keystore password for the self sign certificate. */
  private String keystorePassword;

  /** The GUID of the registry package created before harvesting of products.
   */
  private String registryPackageGuid;

  /** The registry package name to be used when harvesting products. */
  private String registryPackageName;

  /** The severity level to set for the tool. */
  private Level severityLevel;

  /** Indicates the number of products to register concurrently during
   *  batch mode.
   */
  private int batchMode;

  /**
   * Default constructor.
   *
   */
  public HarvestPdapLauncher() {
    policy = null;
    password = null;
    username = null;
    registryService = null;
    keystore = null;
    logFile = null;
    securityContext = null;
    registryPackageGuid = null;
    registryPackageName = null;
    keystorePassword = null;
    severityLevel = ToolsLevel.INFO;
    batchMode = 0;

    globalPolicy = this.getClass().getResourceAsStream("global-policy.xml");
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
   * Displays tool usage.
   *
   */
  public final void displayHelp() {
    int maxWidth = 80;
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp(maxWidth, "harvest-pdap <options>",
        null, Flag.getOptions(), null);
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
   * Examines the command-line arguments passed into the Harvest-PDAP Tool
   * and takes the appropriate action based on what flags were set.
   *
   * @param line A class representation of the command-line arguments.
   *
   * @throws Exception If there was an error while querying the options
   * that were set on the command-line.
   */
  public final void query(final CommandLine line) throws Exception {
    keystore = System.getProperty("pds.security.keystore");
    List<Option> processedOptions = Arrays.asList(line.getOptions());
    for (Option o : processedOptions) {
      if (o.getOpt().equals(Flag.HELP.getShortName())) {
        displayHelp();
        System.exit(0);
      } else if (o.getOpt().equals(Flag.VERSION.getShortName())) {
        displayVersion();
        System.exit(0);
      } else if (o.getOpt().equals(Flag.CONFIG.getShortName())) {
        policy = new File(o.getValue());
        if (!policy.exists()) {
          throw new InvalidOptionException("Policy file does not exist: "
              + policy);
        }
      } else if (o.getOpt().equals(Flag.KEYSTOREPASS.getShortName())) {
        keystorePassword = o.getValue();
      } else if (o.getOpt().equals(Flag.PASSWORD.getShortName())) {
        password = o.getValue();
      } else if (o.getOpt().equals(Flag.USERNAME.getShortName())) {
        username = o.getValue();
      } else if (o.getOpt().equals(Flag.LOG.getShortName())) {
        logFile = o.getValue();
      } else if (o.getOpt().equals(Flag.VERBOSE.getShortName())) {
        setVerbose(Integer.parseInt(o.getValue()));
      } else if (o.getOpt().equals(Flag.BATCHMODE.getShortName())) {
        try {
          if (o.getValue() != null) {
            batchMode = Integer.parseInt(o.getValue());
            if (batchMode <= 0) {
              throw new Exception("Must enter a value greater than 0 for "
                  + "the '-b' flag option.");
            }
          } else {
            batchMode = Constants.DEFAULT_BATCH_MODE;
          }
        } catch (NumberFormatException n) {
          throw new Exception("Invalid value entered for '-b' flag option: "
              + n.getMessage());
        }
      }
    }
    if (policy == null) {
      throw new Exception("Missing '-c' flag option. Policy file must be "
          + "specified.");
    }
    if ((username != null && password == null)
        || (username == null && password != null)) {
      throw new InvalidOptionException(
        "Username and/or password must be specified.");
    }
    if ( (username != null) && (password != null) ) {
      if (keystore == null) {
        throw new Exception("\'pds.security.keystore\' java property not "
            + "set.");
      } else if (keystorePassword == null) {
        throw new Exception("Keystore password must be specified.");
      }
    }
    if (keystore != null) {
      if (!new File(keystore).exists()) {
        throw new Exception("Keystore file does not exist: " + keystore);
      }
      securityContext = new SecurityContext(keystore, keystorePassword,
          keystore, keystorePassword);
    }
    setLogger();
  }

  /**
   * Set the verbosity level and above to include in the reporting.
   * @param v '0' for Debug, '1' for info, '2' for warnings,
   *  and '3' for errors
   * @throws ApplicationException
   */
  private void setVerbose(int v) throws Exception {
    if (v < 0 || v > 3) {
      throw new Exception("Invalid value entered for 'v' flag. "
          + "Valid values can only be 0, 1, 2, or 3");
    }
    if (v == 0) {
      severityLevel = ToolsLevel.DEBUG;
    } else if (v == 1) {
      severityLevel = ToolsLevel.INFO;
    } else if (v == 2) {
      severityLevel = ToolsLevel.WARNING;
    } else if (v == 3) {
      severityLevel = ToolsLevel.SEVERE;
    }
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
    if (logFile != null) {
      logger.addHandler(new HarvestPdapFileHandler(logFile, severityLevel,
          new HarvestPdapFormatter()));
    } else {
      logger.addHandler(new HarvestPdapStreamHandler(System.out,
          severityLevel, new HarvestPdapFormatter()));
    }
  }

  /**
   * The report header for the log.
   *
   * @param policy The policy file.
   */
  private void logHeader(Policy policy) {
    String registryUrl = policy.getPdsRegistry().getUrl();
    List<String> pdaps = new ArrayList<String>();
    for (PdapService pdap : policy.getPdapServices().getPdapService()) {
      pdaps.add(pdap.getUrl());
    }
    log.log(new ToolsLogRecord(ToolsLevel.CONFIGURATION,
        "PDS Harvest-PDAP Tool Log\n"));
    log.log(new ToolsLogRecord(ToolsLevel.CONFIGURATION,
        "Version                     " + ToolInfo.getVersion()));
    log.log(new ToolsLogRecord(ToolsLevel.CONFIGURATION,
        "Time                        " + Utility.getDateTime()));
    log.log(new ToolsLogRecord(ToolsLevel.CONFIGURATION,
        "Severity Level              " + severityLevel.getName()));
    log.log(new ToolsLogRecord(ToolsLevel.CONFIGURATION,
        "PDAP Target(s)              " + pdaps));
    if (batchMode != 0) {
      log.log(new ToolsLogRecord(ToolsLevel.CONFIGURATION,
        "Batch Mode                  " + batchMode));
    }
    log.log(new ToolsLogRecord(ToolsLevel.CONFIGURATION,
        "Registry Location           " + registryUrl));
    log.log(new ToolsLogRecord(ToolsLevel.CONFIGURATION,
        "Registry Package Name       " + registryPackageName));
    log.log(new ToolsLogRecord(ToolsLevel.CONFIGURATION,
        "Registration Package GUID   " + registryPackageGuid + "\n"));
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
   * Creates a registry package to associate with the product registration.
   *
   * @param policy Policy config file.
   *
   * @throws RegistryServiceException If an error occurred creating the
   * registry package.
   */
  private void createRegistryPackage(Policy policy)
  throws RegistryServiceException {
    DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    RegistryPackage registryPackage = new RegistryPackage();
    if (policy.getPdsRegistry().getPackageName() != null) {
      registryPackageName = policy.getPdsRegistry().getPackageName();
    } else {
      registryPackageName = "Harvest-PDAP Package " + dateFormat.format(
          new Date().getTime());
    }
    registryPackage.setName(registryPackageName);
    if (policy.getPdsRegistry().getPackageDescription() != null) {
      registryPackage.setDescription(policy.getPdsRegistry()
          .getPackageDescription());
    } else {
      List<String> services = new ArrayList<String>();
      for (PdapService ps : policy.getPdapServices().getPdapService()) {
        services.add(ps.getUrl());
      }
      registryPackage.setDescription("This package contains registration "
          + "of products from the following PDAP-Compliant Services: "
          + services);
    }
    registryPackageGuid = registryService.createPackage(registryPackage);
  }

  private void deleteRegistryPackage(String guid, String registryURL)
      throws Exception {
    RegistryClient client = null;
    if ( (username != null) && (password != null) ) {
      client = new RegistryClient(registryURL, securityContext, username,
          password);
    } else {
      client = new RegistryClient(registryURL);
    }
    try {
      client.deleteObject(guid, RegistryPackage.class);
      log.log(new ToolsLogRecord(ToolsLevel.INFO, "Deleted package with the "
          + "following guid: " + guid));
    } catch (RegistryServiceException rse) {
      throw new Exception("Registry Service error occurred while "
          + "attempting to create a Registry Package: " + rse.getMessage());
    }
  }

  /**
   * Perform the harvesting of the products.
   *
   * @param policy Policy config file.
   */
  private void doHarvesting(Policy policy) {
    HarvesterPdap harvester = new HarvesterPdap(registryService,
        policy.getProductMetadata(), policy.getResourceMetadata());
    harvester.setBatchMode(batchMode);
    harvester.harvest(policy.getPdapServices());
  }

  /**
   * Process main.
   *
   * @param args command-line arguments.
   */
  private void processMain(String []args) {
    if (args.length == 0) {
      System.out.println("\nType 'harvest-pdap -h' for usage");
      System.exit(0);
    }
    String registryUrl = null;
    try {
      CommandLine commandline = parse(args);
      query(commandline);
      PolicyReader reader = new PolicyReader();
      Policy policy = reader.unmarshall(this.policy, true);
      Policy globalPolicy = reader.unmarshall(this.globalPolicy, false);
      registryUrl = policy.getPdsRegistry().getUrl();
      // Combine productMetadata sections from user-specified policy and
      // global policy
      if (globalPolicy.getProductMetadata().getStaticMetadata() != null) {
        policy.getProductMetadata().getStaticMetadata().getSlot().addAll(
          globalPolicy.getProductMetadata().getStaticMetadata().getSlot());
      }
      policy.getProductMetadata().getDynamicMetadata().getElement().addAll(
          globalPolicy.getProductMetadata().getDynamicMetadata().getElement());

      // Initialize RegistryService object before creating package
      if ( (username != null) && (password != null) ) {
        registryService = new PdsRegistryService(
            policy.getPdsRegistry().getUrl(), securityContext, username,
            password);
      } else {
        registryService = new PdsRegistryService(policy.getPdsRegistry()
            .getUrl());
      }
      createRegistryPackage(policy);
      logHeader(policy);
      doHarvesting(policy);
    } catch (ParseException pe) {
      System.err.println("Command-line parse failure: " + pe.getMessage());
    } catch (JAXBException je) {
      //Don't do anything
    } catch (Exception e) {
      System.err.println(e.getMessage());
    } finally {
      if ( (registryPackageGuid != null) &&
          ((HarvestPdapStats.numDatasetsRegistered == 0) &&
          (HarvestPdapStats.numResourcesRegistered == 0)) ) {
        if (registryUrl != null) {
          log.log(new ToolsLogRecord(ToolsLevel.INFO, "Nothing registered. "
            + "Deleting package '" + registryPackageGuid + "'."));
          try {
            deleteRegistryPackage(registryPackageGuid, registryUrl);
          } catch (Exception e) {
            System.out.println("Error occurred while trying to delete empty "
              + "package '" + registryPackageGuid + "': " + e.getMessage());
          }
        }
      }
      closeHandlers();
    }

  }

  public static void main(final String []args) {
    new HarvestPdapLauncher().processMain(args);
  }
}
