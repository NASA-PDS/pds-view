package gov.nasa.arc.pds.lace.client.service;

import gov.nasa.arc.pds.lace.shared.AttributeItem;
import gov.nasa.arc.pds.lace.shared.Container;
import gov.nasa.arc.pds.lace.shared.InsertionPoint;
import gov.nasa.arc.pds.lace.shared.LabelElement;
import gov.nasa.arc.pds.lace.shared.LabelItem;
import gov.nasa.arc.pds.lace.shared.LabelItemType;
import gov.nasa.arc.pds.lace.shared.ResultType;
import gov.nasa.arc.pds.lace.shared.SimpleItem;
import gov.nasa.arc.pds.lace.shared.ValidationResult;
import gov.nasa.arc.pds.lace.shared.exception.SchemaInitializationException;
import gov.nasa.arc.pds.lace.shared.exception.ServiceException;
import gov.nasa.arc.pds.lace.shared.project.ProjectItem;

import java.io.IOException;
import java.util.Collection;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * Defines a service for accessing the PDS4 label contents model.
 */
@RemoteServiceRelativePath("labelContentsService")
public interface LabelContentsService extends RemoteService {

	/**
	 * Gets the user ID of the current user, or null if not authenticated.
	 *
	 * @return the user ID, or null if not authenticated
	 */
	String getUser();

	/**
	 * Sets the current user ID. This method should be removed once
	 * OpenID support is completed.
	 *
	 * @param userID the user ID
	 * @throw ServiceException if there is an error on the server side
	 */
	void setUser(String userID) throws ServiceException;

	/**
	 * Gets the project items for the current user in a particular folder.
	 *
	 * @param location the logical path to the project folder
	 * @return an array of project names
	 * @throw ServiceException if there is an error on the server side
	 */
	ProjectItem[] getProjectsItems(String location) throws ServiceException;

	/**
	 * Gets the root container contents for a particular element name.
	 *
	 * @param elementName the element name
	 * @param elementNS the namespace of the element
	 * @return the container corresponding to the element, populated with the contents to display
	 */
	Container getRootContainer(String elementName, String elementNS);

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
	 * Pastes an element into a container at an insertion point.
	 *
	 * @param container the container into which to paste the element
	 * @param element the element to paste
	 * @param insPoint the insertion point at which to paste the element
	 * @return the result type object
	 */
	public ResultType pasteElement(
			Container container,
			LabelElement element,
			InsertionPoint insPoint
	);

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
	 * Gets the root container for a label at a desired location on the server.
	 *
	 * @param location the label location
	 * @return the container for the root label element
	 */
	Container getContainerForLocation(String location) throws ServiceException;

	/**
	 * Deletes a project item at a location.
	 *
	 * @param location the location
	 * @throws ServiceException if there is an error deleting the item
	 */
	void deleteProjectItem(String location) throws ServiceException;

	/**
	 * Gets the list of schema files that will be used when creating a new label
	 * or importing a label.
	 *
	 * @return an array of schema files
	 * @throws ServiceException if there is an error reading the user configuration
	 */
	String[] getDefaultSchemaFiles() throws ServiceException;

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
	 * Saves an attribute value.
	 *
	 * @param parentItem the label item housing the attribute
	 * @param attribute the attribute
	 * @param value the new attribute value
	 */
	void saveAttribute(LabelItem parentItem, AttributeItem attribute, String value);

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

	/**
	 * Deletes a label element.
	 *
	 * @param container the container that the element to be deleted is a child of
	 * @param element the label element to be deleted
	 * @return the result type object
	 */
	ResultType deleteElement(Container container, LabelElement element);

	/**
	 * Validates a content model against the schema and schematron rules,
	 * and associates error messages with each label item.
	 *
	 * @param root the root label item of the content model
	 * @return an object with the validation result
	 * @throws Exception if there is an error validating the model
	 */
	ValidationResult validateModel(Container root) throws Exception;

	/**
	 * Gets a list of available namespaces for elements.
	 *
	 * @return a list of namespaces
	 */
	Collection<String> getNamespaces();

	/**
	 * Gets a list of top-level element names that are defined within the specified namespace.
	 *
	 * @param namespace
	 * @return a list of top-level element names
	 */
	Collection<String> getElementNamesByNamespace(String namespace);

	/**
	 * Gets the label item type for the specified element.
	 *
	 * @param elementName the element name
	 * @param elementNS the namespace of the element
	 * @return the label item type for the element
	 */
	LabelItemType getTypeForElement(String elementName, String elementNS);

	/**
	 * Adds an uploaded file to the default schema file set.
	 *
	 * @param filename the uploaded filename
	 * @throws ServiceException if there is an error while uploading the file
	 */
	void addDefaultSchemaFile(String filename) throws ServiceException;

	/**
	 * Removes a file from the default schema file list.
	 *
	 * @param filename the file name to remove
	 * @throws ServiceException if there is an error while deleting the file
	 */
	void removeDefaultSchemaFile(String filename) throws ServiceException;

	/**
	 * Changes the name of the current label.
	 *
	 * @param newName the new name
	 */
	void setLabelName(String newName);

	/**
	 * Gets an attribute of a project item.
	 *
	 * @param key the attribute key
	 * @param clazz
	 * @return
	 */
	String getItemAttribute(String key);

	/**
	 * Sets a project item attribute.
	 *
	 * @param key the attribute key
	 * @param value the attribute value
	 */
	void setItemAttribute(String key, String value);

}
