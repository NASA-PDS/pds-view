package gov.nasa.pds.search.core.constants;

public class Constants { 
	/** Default filename for Product Classes properties file that is required in config directory **/
	public final static String PC_PROPS = "product-classes.txt";

	/** Default maximum number of queried records to be returned from registry **/
	public final static int QUERY_MAX = 999999999;
	
	/** Attribute for version of a product **/
	public final static String PRODUCT_VERSION = "version_id";
	
	/** Run Log File Name **/
	public final static String LOG_FNAME="run.log";

	/** Default start datetime for UNK/N/A/NULL datetime fields **/
	public static final String DEFAULT_STARTTIME="1965-01-01T00:00:00.000Z";
	
	/** Default stop datetime for UNK/N/A/NULL datetime fields **/
	public static final String DEFAULT_STOPTIME="3000-01-01T00:00:00.000Z";
	
	/** Valid PDS4 representation for unknown values **/
	public static String[] VALID_UNK_VALUES = { "N/A", "UNK", "NULL", "UNKNOWN", "" };
}
