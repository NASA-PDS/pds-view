package gov.nasa.pds.objectAccess;

import gov.nasa.pds.domain.Array2DImageProduct;
import gov.nasa.pds.domain.Collection;
import gov.nasa.pds.domain.DocumentProduct;
import gov.nasa.pds.domain.PDSObject;
import gov.nasa.pds.domain.TableCharacterProduct;
import gov.nasa.pds.domain.TokenizedLabel;

/**
 * 
 * Provides access to PDS4 objects.
 *
 */
public interface ObjectProvider {

	 /**
	  * Given a product XML, returns a full parsed product-specific data object (or an error).
	  * This object is typically populated with its child contents.
	  * Also returns its label in flattened form, HTML formatted.
	  * The default product type is a collection.
	  * 
	  * @param thisRelativeFilename filename of this object, relative to archive (bundle) root
	  * @return an object of <productType>
	  * @throws Exception upon error
	  */
	public abstract Array2DImageProduct parseImageProduct(
			String thisRelativeFilename) throws Exception;

	 /**
	  * Given a product XML, returns a full parsed product-specific data object (or an error).
	  * This object is typically populated with its child contents.
	  * Also returns its label in flattened form, HTML formatted.
	  * The default product type is a collection.
	  * 
	  * @param thisRelativeFilename filename of this object, relative to archive (bundle) root
	  * @return an object of <productType>
	  * @throws Exception upon error
	  */
	public abstract DocumentProduct parseDocumentProduct(
			String thisRelativeFilename) throws Exception;

	 /**
	  * Given a product XML, returns a full parsed product-specific data object (or an error).
	  * This object is typically populated with its child contents.
	  * Also returns its label in flattened form, HTML formatted.
	  * The default product type is a collection.
	  * 
	  * @param thisRelativeFilename filename of this object, relative to archive (bundle) root
	  * @return an object of <productType>
	  * @throws Exception upon error
	  */
	public abstract Collection parseCollection(String thisRelativeFilename)
			throws Exception;


	/**
	 * Parses a table object.
	 * @param thisRelativeFilename filename of this object, relative to archive (bundle) root
	 * @param page_size maximum number of elements to read 
	 * @return an object of <productType>
	 * @throws Exception upon error
	 */
	public abstract TableCharacterProduct parseTableCharacterProduct(
			String thisRelativeFilename, int page_size) throws Exception;

	/** 
	 * Given a parent path, returns an object representation.  The object representation includes its
	 * object type and the children references (unparsed). Also gets a child 
     * count for example to know whether your gui is going to page.
     * 
	 * @param thisRelativeFilename filename of this object, relative to archive (bundle) root
	 * @return an object with core attributes about its type and contents
	 * @throws Exception upon error
	 */
	public abstract PDSObject makePDS4Object(String thisRelativeFilename) throws Exception;

	/**
	 * 	 
	 * Parses a PDS file and returns a tokenized representation of the label, suitable for formatting into a display.
	 * 
	 * @param thisRelativeFilename the file to parse.
	 * @param separator An HTML separator that will be inserted between label name-value pairs.
	 * @return a TokenizedLabel object
	 * @throws Exception upon error
	 */
	public TokenizedLabel getTokenizedLabel(String thisRelativeFilename, String separator) throws Exception;

	/**
	 * Parses a PDS file and returns an HTML String representation of the label.
	 * 
	 * @param thisRelativeFilename the file to parse.
	 * @param separator An HTML separator that will be inserted between label name-value pairs.
	 * @return An HTML string representing the PDS Label.
	 * @throws Exception upon error
	 */
	public String getFlattenedLabel(String thisRelativeFilename, String separator) throws Exception;

}