package gov.nasa.pds.search.core;

import gov.nasa.pds.search.core.cli.options.Flag;
import gov.nasa.pds.search.core.cli.options.InvalidOptionException;
import gov.nasa.pds.search.core.constants.Constants;
import gov.nasa.pds.search.core.extractor.RegistryExtractor;
import gov.nasa.pds.search.core.indexer.pds.Indexer;
import gov.nasa.pds.search.core.indexer.solr.SolrIndexer;
import gov.nasa.pds.search.util.ToolInfo;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;

public class SearchCoreLauncher {

	private static final String PDS_CONFIG_PATH="/conf/pds";
	private static final String SEARCH_SERVICE_ENVVAR="SEARCH_SERVICE_HOME";
	
	private Logger LOG = Logger.getLogger(this.getClass().getName());

	private File searchServiceHome;
	private boolean allFlag;
	private boolean extractorFlag;
	private boolean solrFlag;
	private boolean pdsFlag;
	private boolean debug;
	private String registryUrl;
	private int queryMax;
	private String configHome;
	private File propsFile;

	public SearchCoreLauncher() {
		this.searchServiceHome = null;
		this.allFlag = true;
		this.extractorFlag = false;
		this.solrFlag = false;
		this.pdsFlag = false;
		this.debug = false;
		this.registryUrl = null;
		this.queryMax = -1;
		this.configHome = null;
		this.propsFile = null;

	}

	/**
	 * Displays tool usage.
	 * 
	 */
	public final void displayHelp() {
		final int maxWidth = 80;
		final HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp(maxWidth, "search-core -" + Flag.REGISTRY.getShortName() + " <" + Flag.REGISTRY.getArgName() + "> [options]", null,
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
			} 
			/* Commented out - This creates separate index in catalog_index directory.
			else if (o.getOpt().equals(Flag.PDS.getShortName())) {
				this.pdsFlag = true;
				this.allFlag = false;
			}*/ else if (o.getOpt().equals(Flag.DEBUG.getShortName())) {
				this.debug = true;
			} else if (o.getOpt().equals(Flag.REGISTRY.getShortName())) {
				this.registryUrl = o.getValue();
			}  else if (o.getOpt().equals(Flag.SERVICE_HOME.getShortName())) {
				this.searchServiceHome = new File(getAbsolutePath("Search Service Home", o.getValue().trim())); 
			} else if (o.getOpt().equals(Flag.MAX.getShortName())) {
				try {
					this.queryMax = Integer.parseInt(o.getValue());
				} catch (NumberFormatException e) {
					throw new InvalidOptionException("Query Max value must be an integer value.");
				}
				
			} else if (o.getOpt().equals(Flag.CONFIG_HOME.getShortName())) {
				this.configHome = getAbsolutePath("Config Dir", o.getValue().trim());
				
				// Check that the config dir contains the product class properties file
				if (!Arrays.asList((new File(this.configHome)).list()).contains(Constants.PC_PROPS)) {
					throw new InvalidOptionException(Constants.PC_PROPS + " does not exist in directory "
							+ this.configHome);					
				}					
			} else if (o.getOpt().equals(Flag.PROPERTIES.getShortName())) {
				this.propsFile = new File(getAbsolutePath("Properties File", o.getValue().trim()));
			}
		}

		// Verify a registry URL was specified
		if (this.registryUrl == null) {
			throw new InvalidOptionException("Registry URL must be specified with " + Flag.REGISTRY.getShortName() + " flag.");
		}
		
		// Set Search Service Home is not specified
		if (this.searchServiceHome == null) {
			String path = System.getenv(SEARCH_SERVICE_ENVVAR);
			
			if (path.equals("")) {
				path = System.getProperty("user.dir");
			}
			
			System.out.println("search_service_home " + getAbsolutePath("Search Service Home", path));
			this.searchServiceHome = new File(getAbsolutePath("Search Service Home", path));
		}
		
		// Set config directory to pds if not specified
		if (this.configHome == null) {
			this.configHome = (new File(System.getProperty("java.class.path"))).getParentFile().getParent() + PDS_CONFIG_PATH;
		}
	}
	
	/**
	 * Method to convert the file path to absolute, if relative, and check if file exists
	 * 
	 * @param fileType
	 * @param filePath
	 * @return
	 * @throws InvalidOptionException
	 */
    private String getAbsolutePath(String fileType, String filePath) throws InvalidOptionException {
		String finalPath = "";
    	File testFile = new File(filePath);
		if (!testFile.isAbsolute()) {
			finalPath = System.getProperty("user.dir") + "/" + filePath;
		} else {
			finalPath = filePath;
		}
    	
    	if (!(new File(finalPath)).exists()) {
    		throw new InvalidOptionException(fileType + " does not exist: " + filePath);
    	}
    	
    	return finalPath;
    }

	public void execute() {

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

		/**
		 * TODO - Commenting this out for now because I don't see the purpose of this indexer code 
		 */
/*		if (this.allFlag || this.pdsFlag) {
			try {
				runIndexer();
			} catch (Exception e) {
				System.err.println("Error running Indexer.");
				e.printStackTrace();
			}
		}*/
	}

	private void runRegistryExtractor() throws Exception {
		// TODO - Remove all references to TSE
		this.LOG.info("Running Registry Extractor to create new XML data files...");
		//String[] args = { this.registryUrl, this.searchServiceHome.getAbsolutePath(), this.queryMax };
		//RegistryExtractor.main(args);
		RegistryExtractor extractor = new RegistryExtractor(this.registryUrl, this.searchServiceHome.getAbsolutePath(), this.configHome);
		if (this.queryMax > -1)
			extractor.setQueryMax(this.queryMax);
		
		extractor.run();
	}

	private void runSolrIndexer() throws IOException, ParseException, Exception {
		this.LOG.info("\nRunning Solr Indexer to create new SOLR_INDEX.XML ...\n");
		
		File indexDir = new File(this.searchServiceHome.getAbsolutePath() + "/index");
		if (!indexDir.isDirectory()) {
			indexDir.mkdir();
		}
		
		//String[] args = { this.searchServiceHome.getAbsolutePath() + "/index",
		//		this.searchServiceHome.getAbsolutePath() + "/tse/extract" };
		String[] args = { this.searchServiceHome.getAbsolutePath() + "/index",
				this.searchServiceHome.getAbsolutePath() + "/registry-data" };
		SolrIndexer.main(args);
	}

	private void runIndexer() throws IOException {
		this.LOG.info("\nRunning Indexer to create new CATALOG_INDEX...\n");
		String[] args = { this.searchServiceHome.getAbsolutePath(),
				this.searchServiceHome.getAbsolutePath() + "/tse/extract" };
		Indexer.main(args);

	}

	public static void main(String[] args) throws Exception {
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
		} catch (final ParseException pEx) {
			/*System.err.println("Command-line parse failure: "
					+ pEx.getMessage());
			System.exit(0);*/
			throw new Exception("Command-line parse failure: "
					+ pEx.getMessage());
		}/* catch (final Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			System.exit(0);
		}*/
	}
}
