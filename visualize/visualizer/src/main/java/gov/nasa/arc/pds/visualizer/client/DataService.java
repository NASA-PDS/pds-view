package gov.nasa.arc.pds.visualizer.client;

import gov.nasa.pds.domain.PDSObject;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * This is the main data service for the visualizer app.
 */
@RemoteServiceRelativePath("dataService")
public interface DataService extends RemoteService {

  /**
   * Gets data from a PDS4 collection, archive or inventory.
   * @param readDataFromFile - XML file of the data source. This is a relative pathname
   * @param archiveRoot  - the root of the PDS archive
   * @return - list of children data items
   * @throws exception upon error
   */
	PDSObject[] parseCollection(String archiveRoot, String readDataFromFile) throws Exception;
  
  /** 
   * Gets data for an image product.
   * @param archiveRoot - archiveRoot the root of the PDS archive
   * @param readDataFromFile - XML file of the data source. This is a relative pathname
   * @return list of children data items
   * @throws Exception exception upon error
   */
  PDSObject[] parseImageProduct(String archiveRoot, String readDataFromFile) throws Exception;
  
  /** 
   * Gets data for a table product.
   * @param archiveRoot - archiveRoot the root of the PDS archive
   * @param readDataFromFile - XML file of the data source. This is a relative pathname.
   * @return list of children data items
   * @throws Exception exception upon error
   */
  PDSObject[] parseTableCharacterProduct(String archiveRoot, String readDataFromFile) throws Exception;
  
  
  /** 
   * Gets data for PDS4 document product.
   * @param archiveRoot - archiveRoot the root of the PDS archive
   * @param readDataFromFile - XML file of the data source. This is a relative pathname
   * @return-  list of children data items
   * @throws Exception exception upon error
   */
  PDSObject[] parseDocumentProduct(String archiveRoot, String readDataFromFile) throws Exception;

  /**
   * Gets data for PDS4 object, by reading an initialization list. 
   * @return - list of data items corresponding to entries in the initialization list
   * @throws Exception non recoverable upon error
   */
  PDSObject[] fetchToplevel() throws Exception;
  
}