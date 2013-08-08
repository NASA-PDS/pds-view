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

package gov.nasa.pds.search.core;

import gov.nasa.pds.search.core.cli.options.Flag;
import gov.nasa.pds.search.core.cli.options.InvalidOptionException;
import gov.nasa.pds.search.core.constants.Constants;
import gov.nasa.pds.search.core.extractor.RegistryExtractor;
import gov.nasa.pds.search.core.indexer.solr.SolrIndexer;
import gov.nasa.pds.search.core.logging.ToolsLevel;
import gov.nasa.pds.search.core.logging.ToolsLogRecord;
import gov.nasa.pds.search.core.logging.formatter.SearchCoreFormatter;
import gov.nasa.pds.search.core.logging.handler.SearchCoreFileHandler;
import gov.nasa.pds.search.core.logging.handler.SearchCoreStreamHandler;
import gov.nasa.pds.search.core.util.Debugger;
import gov.nasa.pds.search.core.util.PropertiesUtil;
import gov.nasa.pds.search.core.util.ToolInfo;
import gov.nasa.pds.search.core.util.Utility;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;

/**
 * CLI the Search Core Tool.
 * 
 * @author jpadams
 *
 */
public class SearchCoreLauncher {

	/** Default PDS Config File Path, if not is specified. **/
	private static final String PDS3_CONFIG_PATH = "/conf/pds/pds3";

	/** 
	 * Search Environment Variable to use if not specified via CLI.
	 * Specifies the specific Solr instance to output the index. 
	 */
	private static final String SEARCH_SERVICE_ENVVAR = "SEARCH_SERVICE_HOME";

	/** Prefix used in the properties files. **/
	private static final String PROPS_PREFIX = "search.core";
	
	/** Key used in the properties file for the configuration home **/
	private static final String PROPS_CONFIG_KEY = "config-home";
	
	/** Key used in the properties file for the search home **/
	private static final String PROPS_SEARCH_KEY = "search-home";
	
	/** Key used in the properties file for the Registry URL **/
	private static final String PROPS_REGISTRY_KEY = "registry-url";	

	/** @see gov.nasa.pds.search.core.cli.options.Flag#ALL **/
	private boolean allFlag;

	/** @see gov.nasa.pds.search.core.cli.options.Flag#EXTRACTOR **/
	private boolean extractorFlag;

	/** @see gov.nasa.pds.search.core.cli.options.Flag#SOLR **/
	private boolean solrFlag;

	/** @see gov.nasa.pds.search.core.cli.options.Flag#CLEAN **/
	private boolean clean;

	/** @see gov.nasa.pds.search.core.cli.options.Flag#MAX **/
	private int queryMax;
	
	/** @see gov.nasa.pds.search.core.cli.options.Flag#MAX **/
	private String logFile;

	/** @see gov.nasa.pds.search.core.cli.options.Flag#SEARCH_HOME **/
	private File searchHome;

	/** @see gov.nasa.pds.search.core.cli.options.Flag#REGISTRY **/
	private List<String> registryUrlList;

	/** @see gov.nasa.pds.search.core.cli.options.Flag#CONFIG_HOME **/
	private List<String> configHomeList;

	/** @see gov.nasa.pds.search.core.cli.options.Flag#PROPERTIES **/
	private List<File> propsFilesList;
	
	/** The severity level to set for the tool. */
	private Level severityLevel;

	/** Logger. **/
	private static Logger log = Logger.getLogger(SearchCoreLauncher.class.getName());

	/**
	 * Constructor method to initialize all globals.
	 */
	public SearchCoreLauncher() {
		this.allFlag = true;
		this.extractorFlag = false;
		this.solrFlag = false;
		this.clean = true;

		this.queryMax = -1;

		this.searchHome = null;

		this.registryUrlList = new ArrayList<String>();
		this.configHomeList = new ArrayList<String>();
		this.propsFilesList = new ArrayList<File>();
		
		this.severityLevel = ToolsLevel.INFO;
		
		Debugger.debugFlag = false; 
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
	        "PDS Search Core Run Log\n"));
	    log.log(new ToolsLogRecord(ToolsLevel.CONFIGURATION,
	        "Version                     " + ToolInfo.getVersion()));
	    log.log(new ToolsLogRecord(ToolsLevel.CONFIGURATION,
	        "Time                        " + Utility.getDateTime()));
	    log.log(new ToolsLogRecord(ToolsLevel.CONFIGURATION,
	        "Severity Level              " + severityLevel.getName()));
	    
	    for (String url : this.registryUrlList) {
		    log.log(new ToolsLogRecord(ToolsLevel.CONFIGURATION,
		    		"Registry URL                " + url));
	    }
	    
	    log.log(new ToolsLogRecord(ToolsLevel.CONFIGURATION,
	    		"Search Home                 " + this.searchHome));
	    
	    for (String configHome : this.configHomeList) {
	    	log.log(new ToolsLogRecord(ToolsLevel.CONFIGURATION,
	    			"Config Home                 " + configHome));
	    }
	    
	    for (File file : this.propsFilesList) {
		    log.log(new ToolsLogRecord(ToolsLevel.CONFIGURATION,
		    		"Search Core Config          " + file.getAbsolutePath()));
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
	      logger.addHandler(new SearchCoreFileHandler(logFile, severityLevel,
	          new SearchCoreFormatter()));
	    } else {
	      logger.addHandler(new SearchCoreStreamHandler(System.out,
	          severityLevel, new SearchCoreFormatter()));
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
				"search-core -" + Flag.REGISTRY.getShortName() + " <"
						+ Flag.REGISTRY.getArgName() + "> [options]", null,
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
	 * @throws org.apache.commons.cli.ParseException
	 */
	public final CommandLine parse(final String[] args) throws ParseException {
		final CommandLineParser parser = new GnuParser();
		return parser.parse(Flag.getOptions(), args);
	}

	/**
	 * Examines the command-line arguments passed into the Harvest Tool and
	 * takes the appropriate action based on what flags were set.
	 * 
	 * @param line
	 *            A class representation of the command-line arguments.
	 * 
	 * @throws Exception
	 *             If there was an error while querying the options that were
	 *             set on the command-line.
	 */
	@SuppressWarnings("unchecked")
	public final void query(final CommandLine line) throws Exception {
		final List<Option> processedOptions = Arrays.asList(line.getOptions());
		for (final Option o : processedOptions) {
			if (o.getOpt().equals(Flag.HELP.getShortName())) {
				displayHelp();
				System.exit(0);
			} else if (o.getOpt().equals(Flag.VERSION.getShortName())) {
				displayVersion();
				System.exit(0);
			} else if (o.getOpt().equals(Flag.ALL.getShortName())) {
				this.allFlag = true;
			} else if (o.getOpt().equals(Flag.EXTRACTOR.getShortName())) {
				this.extractorFlag = true;
				this.allFlag = false;
			} else if (o.getOpt().equals(Flag.SOLR.getShortName())) {
				this.solrFlag = true;
				this.allFlag = false;
			} else if (o.getOpt().equals(Flag.DEBUG.getShortName())) {
				Debugger.debugFlag = true;
				Debugger.debug("-------------------------------");
				Debugger.debug("---- RUNNING IN DEBUG MODE ----");
				Debugger.debug("-------------------------------");
			} else if (o.getOpt().equals(Flag.REGISTRY.getShortName())) {
				this.registryUrlList = o.getValuesList();
			} else if (o.getOpt().equals(Flag.SEARCH_HOME.getShortName())) {
				setSearchHome(o.getValue().trim());
			} else if (o.getOpt().equals(Flag.MAX.getShortName())) {
				try {
					this.queryMax = Integer.parseInt(o.getValue());
				} catch (NumberFormatException e) {
					throw new InvalidOptionException(
							"Query Max value must be an integer value.");
				}

			} else if (o.getOpt().equals(Flag.CONFIG_HOME.getShortName())) {
				this.configHomeList.add(Utility.getAbsolutePath("Config Dir", o.getValue().trim(), true));
			} else if (o.getOpt().equals(Flag.PROPERTIES.getShortName())) {
				for (Object value : o.getValuesList()) {
					this.propsFilesList
							.add(new File(Utility.getAbsolutePath("Properties File",
									((String) value).trim(), false)));
				}
				setProperties(this.propsFilesList);
			} else if (o.getOpt().equals(Flag.CLEAN.getShortName())) {
				this.clean = false;
			}
		}

		// Check for required values

		// Verify a registry URL was specified
		if (this.registryUrlList.size() == 0) {
			throw new InvalidOptionException(
					"Registry URL must be specified with "
							+ Flag.REGISTRY.getShortName() + " or "
							+ Flag.PROPERTIES.getShortName() + " flags.");
		}

		// Set config directory to pds if not specified
		if (this.configHomeList.size() == 0) {
			setDefaultConfigHome();
		}

		// Set Search Home if not specified
		if (this.searchHome == null) {
			setDefaultSearchHome();
		}
		
		setLogger();
	}

	/**
	 * Set the properties for a given registry/search-home/etc.
	 * 
	 * TODO May want to refactor. Very similar code in
	 * RegistryExtractor.getProductClassList
	 * 
	 * @param propsFileList
	 * @throws InvalidOptionException	thrown if file does not exist
	 */
	public final void setProperties(final List<File> propsFileList)
			throws InvalidOptionException {
		Map<String, String> mappings = new HashMap<String, String>();

		String registryUrl, configHome, searchHome;
		for (File propsFile : propsFileList) {
			mappings = PropertiesUtil.getPropertiesMap(propsFile, PROPS_PREFIX);
			
			registryUrl = mappings.get(PROPS_REGISTRY_KEY);
			configHome = mappings.get(PROPS_CONFIG_KEY);
			searchHome = mappings.get(PROPS_SEARCH_KEY);
			
			if (registryUrl != null) {
				this.registryUrlList.add(registryUrl);
			} else {
				throw new InvalidOptionException(
						"Registry URL must be specified in properties file - "
								+ propsFile.getAbsolutePath());
			}

			if (configHome != null) {
				this.configHomeList.add(Utility.getAbsolutePath("Config Dir", configHome, true));
			} else {
				throw new InvalidOptionException(
						"Config home must be specified in properties file - "
								+ propsFile.getAbsolutePath());
			}

			if (searchHome != null) {
				setSearchHome(searchHome);
			}
		}

	}

	/**
	 * Performs verification the directory given contains the PC_PROPS file and
	 * ensures the path is reset to absolute. Refactored into a separate method
	 * since it is called from 2 places.
	 * 
	 * @param configHome
	 *            A directory path (relative or absolute) that contains the
	 *            product_classes.txt and accompanying config files
	 * @throws InvalidOptionException	thrown if directory does not exist
	 */
	@Deprecated
	public final void addConfigHome(final String configHome)
			throws InvalidOptionException {
		this.configHomeList.add(Utility.getAbsolutePath("Config Dir", configHome, true));

		// Check that the config dir contains the product class properties file
		if (!Arrays.asList((new File(this.configHomeList.get(0))).list())
				.contains(Constants.PC_PROPS)) {
			throw new InvalidOptionException(Constants.PC_PROPS
					+ " does not exist in directory " + this.configHomeList);
		}
	}

	/**
	 * Sets the searchHome global variable after getting its absolute
	 * path and ensuring its files existence.
	 * 
	 * @param searchHome
	 * @throws InvalidOptionException	thrown if directory does not exist
	 */
	public final void setSearchHome(final String searchHome)
			throws InvalidOptionException {
		this.searchHome = new File(Utility.getAbsolutePath(
				"Search Home", searchHome, true));
	}

	/**
	 * Sets the default value for the searchHome global.
	 * 
	 * @throws InvalidOptionException	thrown if directory does not exist
	 */
	public final void setDefaultSearchHome()
			throws InvalidOptionException {
		String path = System.getenv(SEARCH_SERVICE_ENVVAR);

		if (path == null) {
			path = System.getProperty("user.dir");
		} else {
			path += System.getProperty("file.separator") + "pds";
		}

		this.searchHome = new File(Utility.getAbsolutePath(
				"Search Home", path, true));
	}

	/**
	 * Sets the default for the configuration home directory.
	 */
	public final void setDefaultConfigHome() {
		this.configHomeList
				.add((new File(System.getProperty("java.class.path")))
						.getParentFile().getParent() + PDS3_CONFIG_PATH);
	}

	/**
	 * Execute Search Core components depending on the flags specified.
	 */
	public final void execute() {

		if (this.allFlag || this.extractorFlag) {
			try {
				runRegistryExtractor();
			} catch (Exception e) {
				System.err.println("Error running Registry Extractor.");
				e.printStackTrace();
			}
		}

		if (this.allFlag || this.solrFlag) {
			try {
				runSolrIndexer();
			} catch (Exception e) {
				System.err.println("Error running Solr Indexer.");
				e.printStackTrace();
			}
		}
	}

	/**
	 * Runs the Registry Extractor component.
	 * 
	 * @throws Exception
	 */
	private void runRegistryExtractor() throws Exception {
		RegistryExtractor extractor = null;
		for (int i = 0; i < this.registryUrlList.size(); i++) {
			this.log.info("\n\tRegistry URL: " + this.registryUrlList.get(i)
					+ "\n\tSearch Home: "
					+ this.searchHome.getAbsolutePath()
					+ "\n\tConfig Home: " + this.configHomeList.get(i));
			extractor = new RegistryExtractor(
					this.searchHome.getAbsolutePath(),
					new File(this.configHomeList.get(i)),
					this.registryUrlList.get(i), this.clean);
			if (this.queryMax > -1) {
				extractor.setQueryMax(this.queryMax);
			}

			extractor.run();
		}
		extractor.close();
	}

	/**
	 * Runs the SolrIndexer component.
	 * 
	 * @throws IOException
	 * @throws ParseException
	 * @throws Exception
	 */
	private void runSolrIndexer() throws IOException, ParseException, Exception {
		this.log.info("\nRunning Solr Indexer to create new SOLR_INDEX.XML ...\n");

		File indexDir = new File(this.searchHome.getAbsolutePath()
				+ "/index");
		if (!indexDir.isDirectory()) {
			indexDir.mkdir();
		}

		String[] args = { this.searchHome.getAbsolutePath() + "/index",
				this.searchHome.getAbsolutePath() + "/registry-data" };
		SolrIndexer.main(args);
	}

	/**
	 * Main Method to capture CLI arguments.
	 * 
	 * @param args			CLI arguments
	 * @throws Exception	CLI parsing exception
	 */
	public static void main(final String[] args) throws Exception {
		if (args.length == 0) {
			System.out.println();
			throw new Exception("\nType 'search-core -h' for usage");
		}
		try {
			final SearchCoreLauncher launcher = new SearchCoreLauncher();
			final CommandLine commandline = launcher.parse(args);
			launcher.query(commandline);
			launcher.execute();
			// launcher.closeHandlers();
		} catch (final ParseException e) {
			throw new Exception("Command-line parse failure: " 
					+ e.getMessage());
		}
	}
}
