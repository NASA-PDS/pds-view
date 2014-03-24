package gov.nasa.pds.search.core.post;

import static org.junit.Assert.fail;
import gov.nasa.pds.search.core.constants.Constants;
import gov.nasa.pds.search.core.constants.TestConstants;
import gov.nasa.pds.search.core.logging.ToolsLevel;
import gov.nasa.pds.search.core.logging.ToolsLogRecord;
import gov.nasa.pds.search.core.test.SearchCoreTest;
import gov.nasa.pds.search.core.util.Utility;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.apache.http.client.methods.HttpGet;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Test SolrPost class. These tests are ignored, unless otherwise specified
 * because they require a localhost search service to be installed.
 * 
 * @author jpadams
 * 
 */
@RunWith(JUnit4.class)
public class SolrPostTest extends SearchCoreTest {

	private static SolrPost solrPost;

	@Rule
	public SingleTestRule test = new SingleTestRule("");

	@BeforeClass
	public static void oneTimeSetUp() throws SolrPostException {
		solrPost = new SolrPost(Constants.DEFAULT_SERVICE_URL);
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
	public void testPost() throws SolrPostException {
		try {
			solrPost.post(System.getProperty("user.dir") + "/"
					+ TestConstants.SEARCH_HOME_RELATIVE + "/"
					+ Constants.SOLR_INDEX_DIR, Arrays.asList(
					Constants.SOLR_INDEX_PREFIX, Constants.SEARCH_TOOLS));
		} catch (SolrPostException e) {
			e.printStackTrace();
			fail("SolrPost.post failed" + e.getMessage());
		}

		HttpGet httpget = new HttpGet(
				"http://localhost:8080/search-service/pds/product-search?q=*:*&wt=xml&indent=true");

		log.log(new ToolsLogRecord(ToolsLevel.DEBUG, "Executing request: "
				+ httpget.getURI()));

		// System.out.println("----------------------------------------");
		// System.out.println(responseBody);
		// System.out.println("----------------------------------------");

		InputStream responseStream = null;

		try {
			responseStream = Utility.execHttpRequest(httpget);

			/*
			 * StringWriter writer = new StringWriter();
			 * IOUtils.copy(responseStream, writer, "UTF-8");
			 * System.out.println(writer.toString());
			 */
		} catch (IOException e) {
			System.err.println("ERROR Querying localhost");
		} finally {
			try {
				responseStream.close();
			} catch (IOException e) {
				// Ignore exception
			}
		}
	}

	/**
	 * Test SolrPost Class
	 */
	@Test
	public void testOptimize() {
		try {
			solrPost.optimize();
		} catch (SolrPostException e) {
			e.printStackTrace();
			fail("SolrPost.optimize failed" + e.getMessage());
		}
	}

	@Test
	public void testBadSearchServiceUrl() {
		try {
			solrPost = new SolrPost("http://localhost:8080/url/does/not/exist/");
		} catch (SolrPostException e) {
			/* Expected */
		}
	}

	@Test
	@Ignore
	public void testBadSolrIndex() {
		try {
			solrPost.post(System.getProperty("user.dir") + "/"
					+ TestConstants.TEST_DIR_RELATIVE + "/" + "bad-index",
					Arrays.asList(Constants.SOLR_INDEX_PREFIX,
							Constants.SEARCH_TOOLS));
		} catch (SolrPostException e) {
			e.printStackTrace();
			fail("SolrPost.post failed with index that shouldn't fail. "
					+ e.getMessage());
		}
	}

}
