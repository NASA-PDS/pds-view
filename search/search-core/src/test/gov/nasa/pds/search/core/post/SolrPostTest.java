package gov.nasa.pds.search.core.post;

import static org.junit.Assert.*;
import gov.nasa.pds.search.core.constants.Constants;
import gov.nasa.pds.search.core.constants.TestConstants;
import gov.nasa.pds.search.core.logging.ToolsLevel;
import gov.nasa.pds.search.core.logging.formatter.SearchCoreFormatter;
import gov.nasa.pds.search.core.logging.handler.SearchCoreStreamHandler;
import gov.nasa.pds.search.core.post.SolrPost;
import gov.nasa.pds.search.core.post.SolrPostException;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.logging.Logger;

import org.apache.solr.client.solrj.SolrServerException;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import junit.framework.TestCase;

@RunWith(JUnit4.class)
public class SolrPostTest {

	private static SolrPost solrPost;
	
	@BeforeClass
	public static void oneTimeSetUp() throws SolrPostException {
		solrPost = new SolrPost(Constants.DEFAULT_SERVICE_URL);
		//org.apache.log4j.BasicConfigurator.configure();
		
		/*try {
			this.solrPost = new SolrPost(TestConstants.SOLR_SERVER_URL, 
					System.getProperty("user.dir") + "/" + TestConstants.SEARCH_HOME_RELATIVE + "/index", 
					System.getProperty("user.dir") + "/" + TestConstants.FACET_DIR_RELATIVE);
		} catch (MalformedURLException e) {
			fail("Bad Solr Server URL - " + TestConstants.SOLR_SERVER_URL);
		} */
		//File testDir = new File(TestConstants.ABSOLUTE_PATH + TestConstants.SERVICE_HOME_RELATIVE);
		//testDir.mkdirs();
    	Logger logger = Logger.getLogger("");
	    logger.addHandler(new SearchCoreStreamHandler(System.out,
	    		  ToolsLevel.DEBUG, new SearchCoreFormatter()));
	}
	
	@After 
	public void tearDown() {
		//File testDir = new File(TestConstants.ABSOLUTE_PATH + TestConstants.SERVICE_HOME_RELATIVE);
		//testDir.delete();
	}
	
	/**
	 * Test SolrPost Class
	 */
	@Test
	public void testClean() {
		try {
		solrPost.clean();
		} catch (SolrPostException e) {
			e.printStackTrace();
			fail("SolrPost.clean failed" + e.getMessage());
		}
	}
	
	/**
	 * Test SolrPost Class
	 */
	@Test
	public void testPost() {
		try {
			solrPost.post(System.getProperty("user.dir") + "/" + TestConstants.TEST_DIR_RELATIVE + "/" + Constants.SOLR_INDEX_DIR, Arrays.asList(Constants.SOLR_INDEX_PREFIX, Constants.SEARCH_TOOLS));
		} catch (SolrPostException e) {
			e.printStackTrace();
			fail("SolrPost.post failed" + e.getMessage());
		}
	}

	/**
	 * Test SolrPost Class
	 */
	@Test
	@Ignore
	public void testOptimize() {
		try {
			solrPost.optimize();
		} catch (SolrPostException e) {
			e.printStackTrace();
			fail("SolrPost.optimize failed" + e.getMessage());
		}
	}
	
}
