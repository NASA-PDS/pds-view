package gov.nasa.pds.search.core.catalog;

import gov.nasa.pds.search.core.PDSIndexerLauncher;
import gov.nasa.pds.search.core.index.Indexer;
import gov.nasa.pds.search.core.index.SolrIndexer;

import org.junit.Ignore;
import org.junit.Test;

public class PDSIndexerLauncherTest {

	private final static String SOLR_HOME = "src/test/old_solr_home";
	
    /**
     * Test CatalogExtractor
     * @throws Exception 
     */
    @Ignore
    @Test
    public void testCatalogExtractor() throws Exception {
        String[] args = { };
        CatalogExtractor.main(args);
    }

    /**
     * Test SolrIndexer
     * @throws Exception 
     */
    @Ignore
    @Test
    public void testSolrIndexer() throws Exception {
    	//String[] args = { SOLR_HOME+"/pds", SOLR_HOME+"/tse/extract" };
    	String[] args = { "../", "../tse/extract" };
        SolrIndexer.main(args);
    }
    
    /**
     * Test CatalogExtractor
     * @throws Exception 
     */
    @Ignore
    @Test
    public void testIndexer() throws Exception {
        //String[] args = { SOLR_HOME, SOLR_HOME+"/tse/extract" };
    	String[] args = { "../", "../tse/extract" };
        Indexer.main(args);
    }
    
    /**
     * Test PDSIndexerLauncher
     * @throws Exception 
     */
    @Ignore
    @Test
    public void testPDSIndexerLauncher() throws Exception {
        String[] args = { "../" };
        PDSIndexerLauncher.main(args);
    }
	
}
