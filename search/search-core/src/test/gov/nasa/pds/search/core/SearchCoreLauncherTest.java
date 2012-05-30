package gov.nasa.pds.search.core;

import gov.nasa.pds.search.core.SearchCoreLauncher;
import gov.nasa.pds.search.core.RegistryExtractor;
import gov.nasa.pds.search.core.index.Indexer;
import gov.nasa.pds.search.core.index.SolrIndexer;

import org.junit.Ignore;
import org.junit.Test;

public class SearchCoreLauncherTest {

	private final static String SOLR_HOME = "src/test/old_solr_home";
	
    /**
     * Test RegistryExtractor
     * @throws Exception 
     */
    //@Ignore
    @Test
    public void testRegistryExtractor() throws Exception {
        String[] args = { "../", "-e", "-m", "5" };
        SearchCoreLauncher.main(args);
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
     * Test SearchCoreLauncher
     * @throws Exception 
     */
    @Ignore
    @Test
    public void testDefault() throws Exception {
        String[] args = { "../", "-r", "http://localhost:8080/registry" };
        SearchCoreLauncher.main(args);
    }
	
}
