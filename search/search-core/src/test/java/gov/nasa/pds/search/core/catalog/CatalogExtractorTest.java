package gov.nasa.pds.search.core.catalog;

import org.junit.Ignore;
import org.junit.Test;

public class CatalogExtractorTest {

	private final static String SOLR_HOME = "src/test/old_solr_home";
	
    /**
     * Test CatalogExtractor
     * @throws Exception 
     */
    @Ignore
    @Test
    public void testCatalogExtractor() throws Exception {
        String[] args = { SOLR_HOME };
        CatalogExtractor.main(args);
    }

    /**
     * Test SolrIndexer
     * @throws Exception 
     */
    @Ignore
    @Test
    public void testSolrIndexer() throws Exception {
    	String[] args = { SOLR_HOME+"/pds", SOLR_HOME+"/tse/extract" };
        CatalogExtractor.main(args);
    }
    
    /**
     * Test CatalogExtractor
     * @throws Exception 
     */
    @Ignore
    @Test
    public void testIndexer() throws Exception {
        String[] args = { SOLR_HOME, SOLR_HOME+"/tse/extract" };
        CatalogExtractor.main(args);
    }
    
    /**
     * Test PDSIndexerLauncher
     * @throws Exception 
     */
    @Ignore
    @Test
    public void testPDSIndexerLauncher() throws Exception {
        String[] args = { SOLR_HOME };
        CatalogExtractor.main(args);
    }
	
}
