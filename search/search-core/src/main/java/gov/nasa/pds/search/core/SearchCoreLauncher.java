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
import gov.nasa.pds.search.core.post.SolrPost;
import gov.nasa.pds.search.core.post.SolrPostException;
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

	// TODO Should refactor these properties values elsewhere, enum 
	/** Prefix used in the properties files. **/
	private static final String PROPS_PREFIX = "search.core";
	
	/** Key used in the properties file for the configuration home **/
	private static final String PROPS_CONFIG_KEY = "config-home";
	
	/** Key used in the properties file for the Registry URL **/
	private static final String PROPS_PRIMARY_REGISTRY_KEY = "primary-registry";
	
	/** Key used in the properties file for the Registry URL **/
	private static final String PROPS_SECONDARY_REGISTRY_KEY = "secondary-registry";

	/** @see gov.nasa.pds.search.core.cli.options.Flag#ALL **/
	private boolean allFlag;

	/** @see gov.nasa.pds.search.core.cli.options.Flag#CLEAN **/
	private boolean clean;
	
	/** @see gov.nasa.pds.search.core.cli.options.Flag#EXTRACTOR **/
	private boolean extractorFlag;
	
	/** @see gov.nasa.pds.search.core.cli.options.Flag#SOLR **/
	private boolean solrFlag;
	
	/** @see gov.nasa.pds.search.core.cli.options.Flag#SOLR_POST **/
	private boolean postFlag;

	/** @see gov.nasa.pds.search.core.cli.options.Flag#MAX **/
	private int queryMax;
	
	/** @see gov.nasa.pds.search.core.cli.options.Flag#LOG **/
	private String logFile;
	
	/** @see gov.nasa.pds.search.core.cli.options.Flag#SERVICE_URL **/
	private String serviceUrl;

	/** @see gov.nasa.pds.search.core.cli.options.Flag#SEARCH_HOME **/
	private File searchHome;

	/** @see gov.nasa.pds.search.core.cli.options.Flag#CONFIG_HOME **/
	private List<String> configHomeList;
	
	/** @see gov.nasa.pds.search.core.cli.options.Flag#PRIMARY **/
	private List<String> primaryRegistries;
	
	/** @see gov.nasa.pds.search.core.cli.options.Flag#SECONDARY **/
	private List<String> secondaryRegistries;

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
		this.clean = true;
		this.extractorFlag = false;
		this.solrFlag = false;
		this.postFlag = false;
		
		this.queryMax = -1;

		this.searchHome = null;
		this.serviceUrl = null;

		this.configHomeList = new ArrayList<String>();
		this.primaryRegistries = new ArrayList<String>();
		this.secondaryRegistries = new ArrayList<String>();
		
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
	    
	    for (String url : this.primaryRegistries) {
		    log.log(new ToolsLogRecord(ToolsLevel.CONFIGURATION,
		    		"Registry URL                " + url));
	    }
	    
	    log.log(new ToolsLogRecord(ToolsLevel.CONFIGURATION,
	    	"Search Home                 " + this.searchHome));
	    
	    log.log(new ToolsLogRecord(ToolsLevel.CONFIGURATION,
	    	"Search Service URL          " + this.serviceUrl));
	    
	    for (String configHome : this.configHomeList) {
	    	log.log(new ToolsLogRecord(ToolsLevel.CONFIGURATION,
	    			"Config Home                 " + configHome));
	    }
	    
	    for (File file : this.propsFilesList) {
		    log.log(new ToolsLogRecord(ToolsLevel.CONFIGURATION,
		    		"Search Core Properties      " + file.getAbsolutePath()));
	    }
	    
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
	      logger.addHandler(new SearchCoreFileHandler(this.logFile, this.severityLevel,
	          new SearchCoreFormatter()));
	    } else {
	      logger.addHandler(new SearchCoreStreamHandler(System.out,
	    		  this.severityLevel, new SearchCoreFormatter()));
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
				"search-core [options]", null,
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
			if (o.getOpt().equals(Flag.ALL.getShortName())) {
				this.allFlag = true;
			} else if (o.getOpt().equals(Flag.CLEAN.getShortName())) {
				this.clean = false;
			} else if (o.getOpt().equals(Flag.CONFIG_HOME.getShortName())) {
				this.configHomeList.add(Utility.getAbsolutePath("Config Dir", o.getValue().trim(), true));
			} else if (o.getOpt().equals(Flag.DEBUG.getShortName())) {
				Debugger.debugFlag = true;
				Debugger.debug("-----------------------------------------");
				Debugger.debug("---- RUNNING IN DEVELOPER DEBUG MODE ----");
				Debugger.debug("-----------------------------------------");
			} else if (o.getOpt().equals(Flag.EXTRACTOR.getShortName())) {
				this.extractorFlag = true;
				this.allFlag = false;
			} else if (o.getOpt().equals(Flag.HELP.getShortName())) {
				displayHelp();
				System.exit(0);
			} else if (o.getOpt().equals(Flag.LOG.getShortName())) {
				this.logFile = o.getValue().trim();
			} else if (o.getOpt().equals(Flag.MAX.getShortName())) {
				try {
					this.queryMax = Integer.parseInt(o.getValue());
				} catch (NumberFormatException e) {
					throw new InvalidOptionException(
							"Query Max value must be an integer value.");
				}
			} else if (o.getOpt().equals(Flag.PRIMARY.getShortName())) {
				this.primaryRegistries = o.getValuesList();
			} else if (o.getOpt().equals(Flag.PROPERTIES.getShortName())) {
				for (Object value : o.getValuesList()) {
					this.propsFilesList
							.add(new File(Utility.getAbsolutePath("Properties File",
									((String) value).trim(), false)));
				}
			}  else if (o.getOpt().equals(Flag.POST.getShortName())) {
				this.postFlag = true;
				this.allFlag = false;
			} else if (o.getOpt().equals(Flag.SEARCH_HOME.getShortName())) {
				setSearchHome(o.getValue().trim());
			} else if (o.getOpt().equals(Flag.SECONDARY.getShortName())) {
				this.secondaryRegistries = o.getValuesList();
			} else if (o.getOpt().equals(Flag.SERVICE_URL.getShortName())) {
				this.serviceUrl = o.getValue().trim();
			} else if (o.getOpt().equals(Flag.SOLR.getShortName())) {
				this.solrFlag = true;
				this.allFlag = false;
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
			} else {
				
			}
		}

		// Check for required values

		// Set config directory to pds if not specified
		if (this.configHomeList.isEmpty() && this.propsFilesList.isEmpty()) {
			setDefaultConfigHome();
		}

		// Set Search Home if not specified
		if (this.searchHome == null) {
			setDefaultSearchHome();
		}
		
		// Set Search Service URL if not specified
		if (this.serviceUrl == null) {
			setDefaultServiceUrl();
		}
		
		setLogger();
	}

	/**
	 * Set the properties for a given registry/search-home/etc.
	 * 
	 * 
	 * @param propsFileList
	 * @throws InvalidOptionException	thrown if file does not exist
	 */
	public final void setProperties(final File propsFile)
			throws InvalidOptionException {
		Map<String, String> mappings = new HashMap<String, String>();

		String primaryRegistry, secondaryRegistry, configHome;
		//for (File propsFile : propsFileList) {
			mappings = PropertiesUtil.getPropertiesMap(propsFile, PROPS_PREFIX);
			
			primaryRegistry = mappings.get(PROPS_PRIMARY_REGISTRY_KEY);
			secondaryRegistry = mappings.get(PROPS_SECONDARY_REGISTRY_KEY);
			configHome = mappings.get(PROPS_CONFIG_KEY);
			//searchHome = mappings.get(PROPS_SEARCH_KEY);
			
			if (primaryRegistry != null) {
				this.primaryRegistries.clear();
				this.primaryRegistries.add(primaryRegistry);
			}
			
			if (secondaryRegistry != null) {
				this.secondaryRegistries.clear();
				this.secondaryRegistries.add(secondaryRegistry);
			} 

			if (configHome != null) {
				this.configHomeList.clear();
				this.configHomeList.add(Utility.getAbsolutePath("Config Dir", configHome, true));
			} else if (this.configHomeList.isEmpty()){
				throw new InvalidOptionException(
						"Config home must be specified in properties file - "
								+ propsFile.getAbsolutePath()
								+ " or by using the "
								+ Flag.CONFIG_HOME.getShortName() + "flag via "
								+ "the command-line interface.");
			}

			/*if (searchHome != null) {
				setSearchHome(searchHome);
			} else if (this.searchHome == null) {
				throw new InvalidOptionException(
						"Search Home must be specified in properties file - "
								+ propsFile.getAbsolutePath()
								+ " or by using the "
								+ Flag.SEARCH_HOME.getShortName() + "flag via "
								+ "the command-line interface.");
			}*/
		//}

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
			path = Constants.DEFAULT_SEARCH_HOME;
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
				.add(getDefaultConfigHome());
	}
	
	/**
	 * Returns the default config home for use by setting defaults
	 * @return
	 */
	private final String getDefaultConfigHome() {
		return new File(System.getProperty("java.class.path"))
		.getParentFile().getParent() + PDS3_CONFIG_PATH;
	}
	
	public final void setDefaultServiceUrl() {
		this.serviceUrl = Constants.DEFAULT_SERVICE_URL;
	}

	/**
	 * Execute Search Core components depending on the flags specified.
	 */
	public final void execute() {
		SearchCore core = new SearchCore(this.searchHome, this.configHomeList);
		if (this.allFlag || this.extractorFlag) {
			try {
				if (this.propsFilesList.isEmpty()) {
					//RegistryExtractor.prepForRun(this.searchHome.getAbsolutePath(), this.clean);
					
					//runRegistryExtractor();
					core.runRegistryExtractor(this.primaryRegistries, this.secondaryRegistries,
							this.queryMax, this.clean);
				} else {
					
					for (File propFile : this.propsFilesList) {
						setProperties(propFile);
						core.setConfigHomeList(this.configHomeList);
						core.runRegistryExtractor(this.primaryRegistries, this.secondaryRegistries,
								this.queryMax, this.clean);
						
						//RegistryExtractor.prepForRun(this.searchHome.getAbsolutePath(), this.clean);
						//runRegistryExtractor();
					}
				}
			} catch (Exception e) {
				log.log(new ToolsLogRecord(ToolsLevel.SEVERE,
						"Error running Registry Extractor."));
				e.printStackTrace();
			}
		}

		if (this.allFlag || this.solrFlag) {
			try {
				core.runSolrIndexer();
			} catch (Exception e) {
				log.log(new ToolsLogRecord(ToolsLevel.SEVERE,
						"Error running Solr Indexer."));
				e.printStackTrace();
			}
		}
		
		if (this.allFlag || this.postFlag) {
			try {
				core.runSolrPost(this.serviceUrl);
			} catch (Exception e) {
				log.log(new ToolsLogRecord(ToolsLevel.SEVERE,
						"Error running Solr Post."));
				e.printStackTrace();
			}
		}
	}

	/**
	 * Runs the Registry Extractor component.
	 * 
	 * @throws Exception
	 */
	/*private void runRegistryExtractor() throws Exception {
		RegistryExtractor extractor = null;

		for (String configHome : this.configHomeList) {
			extractor = new RegistryExtractor(
					this.searchHome.getAbsolutePath(),
					new File(configHome),
					this.primaryRegistries,
					this.secondaryRegistries);
			

	
			extractor.run();
		}
		
        log.log(new ToolsLogRecord(ToolsLevel.SUCCESS,
        		"Completed extracting data from data source.\n"));
	}*/


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
