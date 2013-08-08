package gov.nasa.pds.search.core.constants;

/**
 * Constants used throughout the Search Core
 * @author jpadams
 *
 */
public final class Constants {
	/**
	 * Default filename for Product Classes properties file that is required in
	 * config directory.
	 **/
	@Deprecated
	public static final String PC_PROPS = "product-classes.txt";

	/** Default maximum number of queried records to be returned from registry. **/
	public static final int QUERY_MAX = 999999999;

	/** Attribute for version of a product. **/
	public static final String VERSION_ID_SLOT = "version_id";

	/** Run Log File Name. **/
	public static final String LOG_FNAME = "run.log";

	/** Default start datetime for UNK/N/A/NULL datetime fields. **/
	public static final String DEFAULT_STARTTIME = "1965-01-01T00:00:00.000Z";

	/** Default stop datetime for UNK/N/A/NULL datetime fields. **/
	public static final String DEFAULT_STOPTIME = "3000-01-01T00:00:00.000Z";

	/** Valid PDS4 representation for unknown values. **/
	public static String[] VALID_UNK_VALUES = { "N/A", "UNK", "NULL",
			"UNKNOWN", "" };
	
	/** Directory name where XML files will be stored after data is extracted from registry **/
	public static String REGISTRY_DATA_DIR = "registry-data";
}
