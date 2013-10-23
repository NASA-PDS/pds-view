package gov.nasa.pds.search.core.constants;

/**
 * Constants used throughout the Search Core
 * @author jpadams
 *
 */
public final class Constants {

	/** Run Log File Name. **/
	public static final String LOG_FNAME = "run.log";
	
	/** Valid PDS4 representation for unknown values. **/
	public static final String[] VALID_UNK_VALUES = { "N/A", "UNK", "NULL",
			"UNKNOWN", "" };
	
	/** Directory name where Solr Documents will be stored after data is extracted from data source **/
	public static final String SOLR_DOC_DIR = "solr-docs";
	
	/** Directory name where Solr Index Documents will be stored after the initial documents are modified
	 * 	by the SolrIndexer and prepared for posting to the Search Service
	 */
	public static final String SOLR_INDEX_DIR = "index";
	
	public static final String SOLR_INDEX_PREFIX = "solr_index.xml.";
	
	public static final String SEARCH_TOOLS = "search-tools.xml";
	
	/** Default start datetime for UNK/N/A/NULL datetime fields. **/
	public static final String DEFAULT_STARTTIME = "1965-01-01T00:00:00.000Z";

	/** Default stop datetime for UNK/N/A/NULL datetime fields. **/
	public static final String DEFAULT_STOPTIME = "3000-01-01T00:00:00.000Z";

	/** Default URL used for Search Service if not specified **/
	public static final String DEFAULT_SERVICE_URL = "http://localhost:8080/search-service";
	
	public static final String DEFAULT_SEARCH_HOME = "/usr/local/search-service/pds";
}
