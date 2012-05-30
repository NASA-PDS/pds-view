package gov.nasa.pds.search.core;

import gov.nasa.pds.search.core.cli.options.Flag;
import gov.nasa.pds.search.core.cli.options.InvalidOptionException;
import gov.nasa.pds.search.core.constants.Constants;
import gov.nasa.pds.search.core.index.Indexer;
import gov.nasa.pds.search.core.index.SolrIndexer;
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

	private Logger LOG = Logger.getLogger(this.getClass().getName());

	private File searchServiceHome;
	private boolean allFlag;
	private boolean extractorFlag;
	private boolean solrFlag;
	private boolean pdsFlag;
	private boolean debug;
	private String registryUrl;
	private int queryMax;

	public SearchCoreLauncher() {
		this.searchServiceHome = null;
		this.allFlag = true;
		this.extractorFlag = false;
		this.solrFlag = false;
		this.pdsFlag = false;
		this.debug = false;
		this.registryUrl = null;
		this.queryMax = -1;

	}

	/**
	 * Displays tool usage.
	 * 
	 */
	public final void displayHelp() {
		final int maxWidth = 80;
		final HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp(maxWidth, "search-core <SEARCH_SERVICE_HOME> [options]", null,
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
			} else if (o.getOpt().equals(Flag.PDS.getShortName())) {
				this.pdsFlag = true;
				this.allFlag = false;
			} else if (o.getOpt().equals(Flag.DEBUG.getShortName())) {
				this.debug = true;
			} else if (o.getOpt().equals(Flag.REGISTRY.getShortName())) {
				this.registryUrl = o.getValue();
			} else if (o.getOpt().equals(Flag.MAX.getShortName())) {
				try {
					this.queryMax = Integer.parseInt(o.getValue());
				} catch (NumberFormatException e) {
					throw new InvalidOptionException("Query Max value must be an integer value.");
				}
				
			}
		}

		if (line.getArgList().size() != 0) {
			this.searchServiceHome = new File(line.getArgList().get(0).toString());
			if (!this.searchServiceHome.exists()) {
				throw new InvalidOptionException("Search Service Home does not exist: "
						+ this.searchServiceHome);
			}
		} else {
			throw new InvalidOptionException(
					"Search Service Home not found in command-line.");
		}

		if (this.registryUrl == null) {
			this.registryUrl = Constants.REGISTRY_URL;
		}
	}

	public void execute() {

		if (this.allFlag || this.extractorFlag) {
			try {
				runRegistryExtractor();
			} catch (Exception e) {
				System.err.println("Error running TSE.");
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

		if (this.allFlag || this.pdsFlag) {
			try {
				runIndexer();
			} catch (Exception e) {
				System.err.println("Error running Indexer.");
				e.printStackTrace();
			}
		}
	}

	private void runRegistryExtractor() throws Exception {
		// TODO - Remove all references to TSE
		this.LOG.info("Running TSE to create new TSE directory...");
		//String[] args = { this.registryUrl, this.searchServiceHome.getAbsolutePath(), this.queryMax };
		//RegistryExtractor.main(args);
		RegistryExtractor extractor = new RegistryExtractor(this.registryUrl, this.searchServiceHome.getAbsolutePath());
		if (this.queryMax > -1)
			extractor.setQueryMax(this.queryMax);
		
		extractor.run();
	}

	private void runSolrIndexer() throws IOException, ParseException, Exception {
		this.LOG.info("\nRunning Solr Indexer to create new SOLR_INDEX.XML ...\n");
		String[] args = { this.searchServiceHome.getAbsolutePath() + "/index",
				this.searchServiceHome.getAbsolutePath() + "/tse/extract" };
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
			System.out.println("\nType 'search-core -h' for usage");
			System.exit(0);
		}
		try {
			final SearchCoreLauncher launcher = new SearchCoreLauncher();
			final CommandLine commandline = launcher.parse(args);
			launcher.query(commandline);
			launcher.execute();
			// launcher.closeHandlers();
		} catch (final ParseException pEx) {
			System.err.println("Command-line parse failure: "
					+ pEx.getMessage());
			System.exit(1);
		} catch (final Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			System.exit(1);
		}
	}
}
