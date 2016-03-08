package gov.nasa.arc.pds.lace.client.service;

import gov.nasa.arc.pds.lace.shared.Container;
import gov.nasa.arc.pds.lace.shared.InsertionPoint;
import gov.nasa.arc.pds.lace.shared.LabelItemType;
import gov.nasa.arc.pds.lace.shared.ResultType;
import gov.nasa.arc.pds.lace.shared.SimpleItem;
import gov.nasa.arc.pds.lace.shared.exception.SchemaInitializationException;

import java.io.IOException;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * Defines a service for accessing the PDS4 label contents model.
 */
@RemoteServiceRelativePath("labelContentsService")
public interface LabelContentsService extends RemoteService {

	/**
	 * Gets the root container contents for a particular element name.
	 *
	 * @param elementName the element name
	 * @return the container corresponding to the element, populated with the contents to display
	 */
	Container getRootContainer(String elementName);

	/**
	 * Inserts the type at position index within the alternatives of the
	 * specified insertion point and updates the contents of the container.
	 * 
	 * @param container the container to update
	 * @param insPoint the insertion point where the insertion should occur
	 * @param alternativeIndex the index of the insert option within the list of alternatives
	 * @param type the type within the list of insert option types to insert
	 * @return the result type object
	 */
	ResultType updateContainer(Container container, InsertionPoint insPoint,
			int alternativeIndex, LabelItemType type);

	/**
	 * Parses a file that has been uploaded to the server and returns
	 * the top-level container that has been populated to match the
	 * file contents.
	 *
	 * @param filePath the file path on the server
	 * @return the container corresponding to the file contents
	 */
	Container getContainerForFile(String filePath);

	/**
	 * Saves the simple item value.
	 *
	 * @param container the container that holds the item
	 * @param item the simple item
	 * @param value the value to save
	 * @return the new simple item after the new value is saved
	 */
	Container saveSimplelItem(Container container, SimpleItem item, String value);

	/**
	 * Writes the model to a server-side file, and returns the file
	 * name to use for downloading the exported label.
	 *
	 * @param container the root of the model to export
	 * @return the file name written by the server
	 * @throws IOException if there is an error writing the exported file
	 * @throws SchemaInitializationException if there is an error initializing the schema services
	 */
	String writeModel(Container container) throws IOException, SchemaInitializationException;

}
