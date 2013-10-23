package gov.nasa.pds.search.core.registry;

import gov.nasa.pds.search.core.schema.DataType;
import gov.nasa.pds.search.core.schema.Field;

import java.util.HashMap;
import java.util.Map;

/**
 * Enum to map the data types in the Search Core configurations
 * to the Solr Schema, and provide the suffix to be appended.
 * 
 * @author jpadams
 *
 */
public enum SolrSchemaField {
	
	/** Suffix affiliated with the {@link DataType} REQUIRED data type **/
	REQUIRED (DataType.REQUIRED, "_r"),
	
	/** Suffix affiliated with the {@link DataType.STRING} data type **/
	STRING (DataType.STRING, "_s"),
	
	/** Suffix affiliated with the {@link DataType.DATE} data type **/
	DATE (DataType.DATE, "_d"),
	
	/** Suffix affiliated with the {@link DataType.INTEGER} data type **/
	INTEGER (DataType.INTEGER, "_i"),
	
	/** Suffix affiliated with the {@link DataType} FLOAT data type **/
	FLOAT (DataType.FLOAT, "_f");
	
	private DataType type;
	private String suffix;
	private static Map<DataType, SolrSchemaField> lookup = new HashMap<DataType, SolrSchemaField>();;
	
	
	static {
		for (SolrSchemaField ssf : values()) {
			lookup.put(ssf.getType(), ssf);
		}
	}
	
	/**
	 * Constructor for the enum
	 * @param type		{@link DataType} object from JAXB bindings
	 * @param suffix	string suffix associated with type
	 */
	SolrSchemaField(DataType type, String suffix) {
		this.type = type;
		this.suffix = suffix;
	}
	
	/**
	 * Return the suffix from a given {@link DataType}
	 * @param	type 		data type specified in the search core config
	 * @return				the suffix to be appended to the {@link Field}
	 * 						name for the Search Service schema
	 * @see		DataType
	 */
	public static String getSuffix(DataType type) {
		return get(type).getSuffix();
	}
	
	/**
	 * Return a {@link SolrSchemaField} based on given {@link DataType}
	 * @param type
	 * @return
	 */
	public static SolrSchemaField get(DataType type) {
		return lookup.get(type);
	}
	
	/**
	 * Returns the {@link DataType}
	 * @return 	DataType object
	 */
	public DataType getType() {
		return this.type;
	}
	
	/**
	 * Return the string suffix
	 * @return	string suffix for the object
	 */
	public String getSuffix() {
		return this.suffix;
	}
	
	
}
