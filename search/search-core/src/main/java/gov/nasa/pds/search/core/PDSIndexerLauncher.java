package gov.nasa.pds.search.core;

import java.io.IOException;
import java.util.logging.Logger;

import gov.nasa.pds.search.core.catalog.CatalogExtractor;
import gov.nasa.pds.search.core.index.Indexer;
import gov.nasa.pds.search.core.index.SolrIndexer;

public class PDSIndexerLauncher {
	
	private Logger LOG = Logger.getLogger(this.getClass().getName());

	private final String solrHome;
	
	public PDSIndexerLauncher(String solrHome) {
		this.solrHome = solrHome;
	}
	
	public void execute() {
		try {
			runTse();
		} catch (Exception e) {
			System.err.println("Error running TSE.");
			e.printStackTrace();
		}

		try {
			runSolrIndexer();
		} catch (Exception e) {
			System.err.println("Error running Solr Indexer.");
			e.printStackTrace();
		}
		
		try {
			runIndexer();
		} catch (Exception e) {
			System.err.println("Error running Indexer.");
			e.printStackTrace();
		}
	}
	
	private void runTse() throws Exception {
		this.LOG.info("Running TSE to create new TSE directory...");
		String[] args = { this.solrHome };
		CatalogExtractor.main(args);
	}
	
	private void runSolrIndexer() throws IOException {
		this.LOG.info("\nRunning Solr Indexer to create new SOLR_INDEX.XML ...\n");
		String[] args = { this.solrHome+"/index", this.solrHome+"/tse/extract" };
		SolrIndexer.main(args);
	}
	
	private void runIndexer() throws IOException {
		this.LOG.info("\nRunning Indexer to create new CATALOG_INDEX...\n");
		String[] args = { this.solrHome, this.solrHome+"/tse/extract" };
		Indexer.main(args);
		
	}
	
	public static void main(String[] args) throws Exception {
		String solrHome = "";
		if (args.length == 1) {
			solrHome = args[0];
		}
		
		//else
		//	System.err.println("Error: Need to specify ";
		// base = System.getProperty("user.home") + "/dev/workspace/tse";
		// base = System.getProperty("user.home") + "/tse";
		PDSIndexerLauncher launcher = new PDSIndexerLauncher(solrHome);
		launcher.execute();
	}
}
