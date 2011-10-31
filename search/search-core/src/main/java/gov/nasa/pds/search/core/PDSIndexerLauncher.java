package gov.nasa.pds.search.core;

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

import gov.nasa.pds.search.core.catalog.CatalogExtractor;
import gov.nasa.pds.search.core.index.Indexer;
import gov.nasa.pds.search.core.index.SolrIndexer;
import gov.nasa.pds.search.util.ToolInfo;
import gov.nasa.pds.search.core.cli.options.Flag;
import gov.nasa.pds.search.core.cli.options.InvalidOptionException;

public class PDSIndexerLauncher {

	private Logger LOG = Logger.getLogger(this.getClass().getName());

	private File solrHome;
	private boolean allFlag;
	private boolean extractorFlag;
	private boolean solrFlag;
	private boolean pdsFlag;
	private boolean debug;

	public PDSIndexerLauncher() {
		this.solrHome = null;
		this.allFlag = true;
		this.extractorFlag = false;
		this.solrFlag = false;
		this.pdsFlag = false;
		this.debug = false;

	}

	/**
	 * Displays tool usage.
	 * 
	 */
	public final void displayHelp() {
		final int maxWidth = 80;
		final HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp(maxWidth, "PDSIndexer <options>", null,
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
			}
		}

		if (line.getArgList().size() !=  0) {
			this.solrHome = new File(line.getArgList().get(0).toString());
			if (!this.solrHome.exists()) {
				throw new InvalidOptionException("Solr Home does not exist: "
						+ this.solrHome);
			} 
		} else {
			throw new InvalidOptionException(
					"Solr Home not found in command-line.");
		}
	}

	public void execute() {

		if (this.allFlag || this.extractorFlag) {
			try {
				runTse();
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

	private void runTse() throws Exception {
		this.LOG.info("Running TSE to create new TSE directory...");
		String[] args = { this.solrHome.getAbsolutePath() };
		CatalogExtractor.main(args);
	}

	private void runSolrIndexer() throws IOException, ParseException, Exception {
		this.LOG.info("\nRunning Solr Indexer to create new SOLR_INDEX.XML ...\n");
		String[] args = { this.solrHome.getAbsolutePath() + "/index",
				this.solrHome.getAbsolutePath() + "/tse/extract" };
		SolrIndexer.main(args);
	}

	private void runIndexer() throws IOException {
		this.LOG.info("\nRunning Indexer to create new CATALOG_INDEX...\n");
		String[] args = { this.solrHome.getAbsolutePath(),
				this.solrHome.getAbsolutePath() + "/tse/extract" };
		Indexer.main(args);

	}

	public static void main(String[] args) throws Exception {
		if (args.length == 0) {
			System.out.println("\nType 'PDSIndexer -h' for usage");
			System.exit(0);
		}
		try {
			final PDSIndexerLauncher launcher = new PDSIndexerLauncher();
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

		/*
		 * String solrHome = ""; if (args.length == 1) { solrHome = args[0]; }
		 * 
		 * //else // System.err.println("Error: Need to specify "; // base =
		 * System.getProperty("user.home") + "/dev/workspace/tse"; // base =
		 * System.getProperty("user.home") + "/tse"; PDSIndexerLauncher launcher
		 * = new PDSIndexerLauncher(solrHome); launcher.execute();
		 */
	}
}
