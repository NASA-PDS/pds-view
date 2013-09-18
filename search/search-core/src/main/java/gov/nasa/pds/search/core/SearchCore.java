package gov.nasa.pds.search.core;

import gov.nasa.pds.search.core.constants.Constants;
import gov.nasa.pds.search.core.exception.SearchCoreFatalException;
import gov.nasa.pds.search.core.extractor.RegistryExtractor;
import gov.nasa.pds.search.core.indexer.solr.IndexerException;
import gov.nasa.pds.search.core.indexer.solr.SolrIndexer;
import gov.nasa.pds.search.core.logging.ToolsLevel;
import gov.nasa.pds.search.core.logging.ToolsLogRecord;
import gov.nasa.pds.search.core.post.SolrPost;
import gov.nasa.pds.search.core.post.SolrPostException;
import gov.nasa.pds.search.core.registry.ProductClassException;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;

public class SearchCore {
	/** Flag to ensure the directory prep method is only run once **/
	private boolean prep = false;
	
	/** @see gov.nasa.pds.search.core.cli.options.Flag#CONFIG_HOME **/
	private List<String> configHomeList;
	
	/** @see gov.nasa.pds.search.core.cli.options.Flag#SEARCH_HOME **/
	private File searchHome;
	
	/** Logger. **/
	private static Logger log = Logger.getLogger(SearchCore.class.getName());

	public SearchCore(File searchHome) {
		this.searchHome = searchHome;
		this.configHomeList = null;
	}
	
	public SearchCore(File searchHome, List<String> configHomeList) {
		this.searchHome = searchHome;
		this.configHomeList = configHomeList;
	}
	
	/**
	 * Runs the Registry Extractor component.
	 * 
	 * @throws Exception
	 */
	public void runRegistryExtractor(List<String> primaryRegistries, 
			List<String> secondaryRegistries, int queryMax, boolean clean) throws Exception {
		RegistryExtractor extractor = null;

		File outputDir = prepForExtractorRun(clean);
		for (String configHome : this.configHomeList) {
			extractor = new RegistryExtractor(
					new File(configHome),
					primaryRegistries,
					secondaryRegistries,
					outputDir);
			
			if (queryMax > -1) {
				extractor.setQueryMax(queryMax);
			}
	
			extractor.run();
		}
		
        log.log(new ToolsLogRecord(ToolsLevel.SUCCESS,
        		"Completed extracting data from data source.\n"));
	}
	
	/**
	 * Runs the SolrIndexer component.
	 * 
	 * @throws IOException
	 * @throws ParseException
	 * @throws Exception
	 */
	public void runSolrIndexer() throws IndexerException {
		log.log(new ToolsLogRecord(ToolsLevel.INFO,
				"Running Solr Indexer to create new solr documents for indexing ..."));

		File indexDir = new File(this.searchHome.getAbsolutePath()
				+ "/" + Constants.SOLR_INDEX_DIR);
		if (!indexDir.isDirectory()) {
			indexDir.mkdir();
		}

		String[] args = { this.searchHome.getAbsolutePath() + "/" + Constants.SOLR_INDEX_DIR,
				this.searchHome.getAbsolutePath() + "/" + Constants.SOLR_DOC_DIR };
		SolrIndexer.main(args);
		
        log.log(new ToolsLogRecord(ToolsLevel.SUCCESS,
        		"Completed transforming data into Solr Lucene index\n"));
	}
	
	/**
	 * 
	 * @throws SolrPostException
	 */
	public void runSolrPost(String serviceUrl) throws SolrPostException {
		log.log(new ToolsLogRecord(ToolsLevel.INFO,
				"Running Solr Post to Post Data To Search Service ..."));

		File indexDir = new File(this.searchHome.getAbsolutePath()
				+ "/" + Constants.SOLR_INDEX_DIR);
		if (!indexDir.isDirectory()) {
			throw new SolrPostException("Index directory " + indexDir.getAbsolutePath() + " does not exist.");
		}

		SolrPost solrPost = new SolrPost(serviceUrl);
		
		solrPost.clean();	// Remove old index
		solrPost.postIndex(this.searchHome.getAbsolutePath() + "/" + Constants.SOLR_INDEX_DIR, 
				Arrays.asList(Constants.SOLR_INDEX_PREFIX, Constants.SEARCH_TOOLS));	// Post the new data
		
        log.log(new ToolsLogRecord(ToolsLevel.SUCCESS,
        		"Completed posting data to the Search Service\n"));
	}
	
	/**
	 * Create the output directory for the Registry Extractor data.
	 * 
	 * @param outDir	The base directory where the registry-data directory is
	 *            		created.
	 * @param clean		Boolean parameter used to determine whether or not the
	 *            		previous run data should be removed. This is used in case user
	 *            		decides to append to previous runs data.
	 * @throws ProductClassException	thrown when directories cannot be created
	 */
	public File prepForExtractorRun(boolean clean)
			throws SearchCoreFatalException {
		String searchHomeString = this.searchHome.getAbsolutePath();
		File outputDir = getExtractorOutputDir(searchHomeString);
		if (!this.prep) {
			try {
			    log.log(new ToolsLogRecord(ToolsLevel.DEBUG, "Creating directory "
			    		+ searchHomeString));
				// Create registry directory to hold XML files containing desired
				// registry data
	
				// Back up old Solr Documents directory if it exists
				if (outputDir.isDirectory()) {
					File backupDir = new File(searchHomeString, Constants.SOLR_DOC_DIR + "_old");
	
					if (backupDir.isDirectory()) {
						FileUtils.deleteDirectory(backupDir);
					}
	
					// Remove dataDir if clean flag is true
					if (clean) {
						FileUtils.moveDirectory(outputDir, backupDir);
					} else {
						FileUtils.copyDirectory(outputDir, backupDir);
					}
				}
	
				FileUtils.forceMkdir(outputDir);
	
			} catch (IOException e) {
			      log.log(new ToolsLogRecord(ToolsLevel.SEVERE, e.getMessage(),
			    		  searchHomeString));
				throw new SearchCoreFatalException("Could not setup directories in "
						+ outputDir.getAbsolutePath());
			}
			this.prep = true;
		}
		
		return outputDir;
	}
	
	public File getExtractorOutputDir(String outDir) {
		return new File(outDir, Constants.SOLR_DOC_DIR);
	}

	/**
	 * @return the configHomeList
	 */
	public List<String> getConfigHomeList() {
		return configHomeList;
	}

	/**
	 * @param configHomeList the configHomeList to set
	 */
	public void setConfigHomeList(List<String> configHomeList) {
		this.configHomeList = configHomeList;
	}
	
}
