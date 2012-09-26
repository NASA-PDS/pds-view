//	Copyright 2009-2012, by the California Institute of Technology.
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
import gov.nasa.pds.search.core.indexer.pds.Indexer;
import gov.nasa.pds.search.core.indexer.solr.SolrIndexer;
import gov.nasa.pds.search.util.PropertiesUtil;
import gov.nasa.pds.search.util.ToolInfo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
	private static final String PDS_CONFIG_PATH = "/conf/pds";

	/** Search Service Environment Variable to use if not specified via CLI. **/
	private static final String SEARCH_SERVICE_ENVVAR = "SEARCH_SERVICE_HOME";

	/** Prefix used in the properties files. **/
	private static final String PROPS_PREFIX = "search.service";

	/** @see gov.nasa.pds.search.core.cli.options.Flag#ALL **/
	private boolean allFlag;

	/** @see gov.nasa.pds.search.core.cli.options.Flag#EXTRACTOR **/
	private boolean extractorFlag;

	/** @see gov.nasa.pds.search.core.cli.options.Flag#SOLR **/
	private boolean solrFlag;

	/** @see gov.nasa.pds.search.core.cli.options.Flag#DEBUG **/
	private boolean debug;

	/** @see gov.nasa.pds.search.core.cli.options.Flag#CLEAN **/
	private boolean clean;

	/** @see gov.nasa.pds.search.core.cli.options.Flag#MAX **/
	private int queryMax;

	/** @see gov.nasa.pds.search.core.cli.options.Flag#SERVICE_HOME **/
	private File searchServiceHome;

	/** @see gov.nasa.pds.search.core.cli.options.Flag#REGISTRY **/
	private List<String> registryUrlList;

	/** @see gov.nasa.pds.search.core.cli.options.Flag#CONFIG_HOME **/
	private List<String> configHomeList;

	/** @see gov.nasa.pds.search.core.cli.options.Flag#PROPERTIES **/
	private List<File> propsFilesList;

	/** Logger. **/
	private Logger log = Logger.getLogger(this.getClass().getName());

	/**
	 * Constructor method to initialize all globals.
	 */
	public SearchCoreLauncher() {
		this.allFlag = true;
		this.extractorFlag = false;
		this.solrFlag = false;
		this.debug = false;
		this.clean = true;

		this.queryMax = -1;

		this.searchServiceHome = null;

		this.registryUrlList = new ArrayList<String>();
		this.configHomeList = new ArrayList<String>();
		this.propsFilesList = new ArrayList<File>();
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
				this.debug = true;
			} else if (o.getOpt().equals(Flag.REGISTRY.getShortName())) {
				this.registryUrlList = o.getValuesList();
			} else if (o.getOpt().equals(Flag.SERVICE_HOME.getShortName())) {
				setSearchServiceHome(o.getValue().trim());
			} else if (o.getOpt().equals(Flag.MAX.getShortName())) {
				try {
					this.queryMax = Integer.parseInt(o.getValue());
				} catch (NumberFormatException e) {
					throw new InvalidOptionException(
							"Query Max value must be an integer value.");
				}

			} else if (o.getOpt().equals(Flag.CONFIG_HOME.getShortName())) {
				addConfigHome(o.getValue().trim());
			} else if (o.getOpt().equals(Flag.PROPERTIES.getShortName())) {
				for (Object value : o.getValuesList()) {
					this.propsFilesList
							.add(new File(getAbsolutePath("Properties File",
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

		// Set Search Service Home if not specified
		if (this.searchServiceHome == null) {
			setDefaultSearchServiceHome();
		}
	}

	/**
	 * Method to convert the file path to absolute, if relative, and check if
	 * file exists.
	 * 
	 * @param fileType
	 *            File type denoted to allow for usable error msgs
	 * @param filePath
	 *            Current path given through the command-line
	 * @param isDir
	 *            Designates if filePath specified is a directory. False means
	 *            filePath is a file.
	 * @return	the absolute path from the input file path
	 * @throws InvalidOptionException	thrown if directory does not exist
	 */
	private String getAbsolutePath(final String fileType,
			final String filePath, final boolean isDir)
			throws InvalidOptionException {
		String finalPath = "";
		File tFile = new File(filePath);
		if (!tFile.isAbsolute()) {
			finalPath = System.getProperty("user.dir") + "/" + filePath;
		} else {
			finalPath = filePath;
		}

		tFile = new File(finalPath);
		if ((isDir && !tFile.isDirectory()) || (!isDir && !tFile.isFile())) {
			throw new InvalidOptionException(fileType + " does not exist: "
					+ filePath);
		}

		return finalPath;
	}

	/**
	 * Set the properties for a given registry/search-service-home/etc.
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

		for (File propsFile : propsFileList) {
			mappings = PropertiesUtil.getPropertiesMap(propsFile, PROPS_PREFIX);

			if (mappings.get("registry-url") != null) {
				this.registryUrlList.add(mappings.get("registry-url"));
			} else {
				throw new InvalidOptionException(
						"Registry URL must be specified in properties file - "
								+ propsFile.getAbsolutePath());
			}

			if (mappings.get("config-home") != null) {
				addConfigHome(mappings.get("config-home"));
			} else {
				throw new InvalidOptionException(
						"Config home must be specified in properties file - "
								+ propsFile.getAbsolutePath());
			}

			if (mappings.get("home") != null) {
				setSearchServiceHome(mappings.get("home"));
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
	public final void addConfigHome(final String configHome)
			throws InvalidOptionException {
		this.configHomeList
				.add(getAbsolutePath("Config Dir", configHome, true));

		// Check that the config dir contains the product class properties file
		if (!Arrays.asList((new File(this.configHomeList.get(0))).list())
				.contains(Constants.PC_PROPS)) {
			throw new InvalidOptionException(Constants.PC_PROPS
					+ " does not exist in directory " + this.configHomeList);
		}
	}

	/**
	 * Sets the searchServiceHome global variable after getting its absolute
	 * path and ensuring its files existence.
	 * 
	 * @param searchServiceHome
	 * @throws InvalidOptionException	thrown if directory does not exist
	 */
	public final void setSearchServiceHome(final String searchServiceHome)
			throws InvalidOptionException {
		this.searchServiceHome = new File(getAbsolutePath(
				"Search Service Home", searchServiceHome, true));
	}

	/**
	 * Sets the default value for the searchServiceHome global.
	 * 
	 * @throws InvalidOptionException	thrown if directory does not exist
	 */
	public final void setDefaultSearchServiceHome()
			throws InvalidOptionException {
		String path = System.getenv(SEARCH_SERVICE_ENVVAR);

		if (path == null) {
			path = System.getProperty("user.dir");
		}

		// System.out.println("search_service_home "
		// + getAbsolutePath("Search Service Home", path, true));
		this.searchServiceHome = new File(getAbsolutePath(
				"Search Service Home", path, true));
	}

	/**
	 * Sets the default for the configuration home directory.
	 */
	public final void setDefaultConfigHome() {
		this.configHomeList
				.add((new File(System.getProperty("java.class.path")))
						.getParentFile().getParent() + PDS_CONFIG_PATH);
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
		RegistryExtractor extractor = new RegistryExtractor(
				this.searchServiceHome.getAbsolutePath(), this.clean);
		for (int i = 0; i < this.registryUrlList.size(); i++) {
			this.log.info("\n\tRegistry URL: " + this.registryUrlList.get(i)
					+ "\n\tSearch Service Home: "
					+ this.searchServiceHome.getAbsolutePath()
					+ "\n\tConfig Home: " + this.configHomeList.get(i));
			extractor.setRegistryUrl(this.registryUrlList.get(i));
			extractor.setConfDir(new File(this.configHomeList.get(i)));

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

		File indexDir = new File(this.searchServiceHome.getAbsolutePath()
				+ "/index");
		if (!indexDir.isDirectory()) {
			indexDir.mkdir();
		}

		String[] args = { this.searchServiceHome.getAbsolutePath() + "/index",
				this.searchServiceHome.getAbsolutePath() + "/registry-data" };
		SolrIndexer.main(args);
	}

	/**
	 * Runs the PDS Indexer component.
	 * 
	 * @throws IOException
	 */
	@Deprecated private void runIndexer() throws IOException {
		this.log.info("\nRunning Indexer to create new CATALOG_INDEX...\n");
		String[] args = { this.searchServiceHome.getAbsolutePath(),
				this.searchServiceHome.getAbsolutePath() + "/tse/extract" };
		Indexer.main(args);

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
