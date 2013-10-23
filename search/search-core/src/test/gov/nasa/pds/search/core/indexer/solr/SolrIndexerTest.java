package gov.nasa.pds.search.core.indexer.solr;

import static org.junit.Assert.fail;
import gov.nasa.pds.search.core.constants.TestConstants;
import gov.nasa.pds.search.core.test.SearchCoreTest;

import java.io.File;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class SolrIndexerTest extends SearchCoreTest {

	@Test
	public void testSolrIndexer() {
		try {
			File indexDir = new File(System.getProperty("user.dir") + "/"
					+ TestConstants.SEARCH_HOME_RELATIVE + "/index");
			if (!indexDir.isDirectory()) {
				indexDir.mkdirs();
			}

			// String[] args = { this.searchServiceHome.getAbsolutePath() +
			// "/index",
			// this.searchServiceHome.getAbsolutePath() + "/tse/extract" };
			String[] args = {
					System.getProperty("user.dir") + "/"
							+ TestConstants.SEARCH_HOME_RELATIVE + "/index",
					System.getProperty("user.dir") + "/"
							+ TestConstants.SEARCH_HOME_RELATIVE
							+ "/registry-data" };
			SolrIndexer.main(args);
		} catch (Exception e) {
			e.printStackTrace();
			fail("Error running Solr Indexer.");
		}
	}

}
