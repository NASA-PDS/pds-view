package gov.nasa.arc.pds.lace.server;

import gov.nasa.arc.pds.lace.client.service.LabelContentsService;
import gov.nasa.arc.pds.lace.server.parse.LabelReader;
import gov.nasa.arc.pds.lace.server.parse.LabelWriter;
import gov.nasa.arc.pds.lace.server.project.ProjectManager;
import gov.nasa.arc.pds.lace.server.schema.SchemaDefaults;
import gov.nasa.arc.pds.lace.server.schema.SchemaManager;
import gov.nasa.arc.pds.lace.server.validation.DOMValidator;
import gov.nasa.arc.pds.lace.server.validation.FailureHandler;
import gov.nasa.arc.pds.lace.shared.AttributeItem;
import gov.nasa.arc.pds.lace.shared.Container;
import gov.nasa.arc.pds.lace.shared.InsertOption;
import gov.nasa.arc.pds.lace.shared.InsertionPoint;
import gov.nasa.arc.pds.lace.shared.ItemAttributes;
import gov.nasa.arc.pds.lace.shared.LabelElement;
import gov.nasa.arc.pds.lace.shared.LabelItem;
import gov.nasa.arc.pds.lace.shared.LabelItemType;
import gov.nasa.arc.pds.lace.shared.ResultType;
import gov.nasa.arc.pds.lace.shared.SimpleItem;
import gov.nasa.arc.pds.lace.shared.ValidationResult;
import gov.nasa.arc.pds.lace.shared.exception.SchemaInitializationException;
import gov.nasa.arc.pds.lace.shared.exception.ServiceException;
import gov.nasa.arc.pds.lace.shared.project.ProjectItem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Implementation of the label template contents service.
 */
@Singleton
public class LabelContentsServiceImpl extends RemoteServiceServlet implements LabelContentsService {

	private static final Logger LOG = LoggerFactory.getLogger(LabelContentsServiceImpl.class);

	private static final long serialVersionUID = 1L;

	private Provider<SchemaDefaults> schemaDefaultsProvider;
	private Provider<SchemaManager> schemaManagerProvider;
	private Provider<LabelReader> readerProvider;
	private Provider<LabelWriter> writerProvider;
	private Provider<DOMValidator> validatorProvider;
	private MessageTranslator translator;

	private Provider<HttpSession> sessionProvider;
	private Provider<ProjectManager> projectManagerProvider;
	private SessionAttributesManager sessionAttributesManager;

	private ServerConfiguration serverConfig;

	private IDGenerator idGenerator;

	/**
	 * Creates a new instance of the service.
	 *
	 * @param serverConfig the server configuration
	 * @param schemaDefaultsProvider a provider for the configured schema defaults
	 * @param schemaManagerProvider a provider for schema managers
	 * @param readerProvider a provider for getting a label reader
	 * @param writerProvider a provider for getting a label writer
	 * @param validatorProvider a provider for getting a label validator
	 * @param translator an error message translator
	 * @param sessionProvider a provider of the HTTP session
	 * @param projectManagerProvider a provider for the project manager
	 * @param sessionAttributesManager the manager of session attributes
	 * @param idGenerator a factory for unique element IDs
	 */
	@Inject
	public LabelContentsServiceImpl(
			ServerConfiguration serverConfig,
			Provider<SchemaDefaults> schemaDefaultsProvider,
			Provider<SchemaManager> schemaManagerProvider,
			Provider<LabelReader> readerProvider,
			Provider<LabelWriter> writerProvider,
			Provider<DOMValidator> validatorProvider,
			MessageTranslator translator,
			Provider<HttpSession> sessionProvider,
			Provider<ProjectManager> projectManagerProvider,
			SessionAttributesManager sessionAttributesManager,
			IDGenerator idGenerator
	) {
		this.serverConfig = serverConfig;
		this.schemaDefaultsProvider = schemaDefaultsProvider;
		this.schemaManagerProvider = schemaManagerProvider;
		this.readerProvider = readerProvider;
		this.writerProvider = writerProvider;
		this.validatorProvider = validatorProvider;
		this.translator = translator;
		this.sessionProvider = sessionProvider;
		this.projectManagerProvider = projectManagerProvider;
		this.sessionAttributesManager = sessionAttributesManager;
		this.idGenerator = idGenerator;
	}

	@Override
	public Container getRootContainer(String elementName, String elementNS) {
		SchemaManager schemaManager = getSchemaManager();
		Container container = schemaManager.getAnalyzer().getContainerForElement(elementName, elementNS);
		schemaManager.addValidValues(container, null);

		ProjectManager manager = projectManagerProvider.get();

		String location;
		try {
			location = manager.createNewLabel(getUser(), "");
		} catch (IOException e) {
			e.printStackTrace();
			throw new IllegalStateException("Error creating new label: " + e.getMessage());
		}

		saveLabel("Untitled.xml", container, location);
		setLocation(location);

		return container;
	}

	@Override
	public ResultType updateContainer(
			Container container,
			InsertionPoint insPoint,
			int alternativeIndex,
			LabelItemType type
	) {
		List<LabelItem> contents = container.getContents();
		List<LabelItem> oldContents = new ArrayList<LabelItem>(contents);

		// TODO: move this code to doInsert().
		// Remove the selected insertion point from the contents.
		int pos = contents.indexOf(insPoint);
		contents.remove(pos);

		// Insert a new container.
		List<LabelItem> list = getSchemaManager().getAnalyzer().doInsert(insPoint, alternativeIndex, type);
		// TODO: move this code to doInsert().
		for (LabelItem item : list) {
			contents.add(pos, item);
			++pos;
		}
		// Merge the insertion points, if any, in the updated contents list.
		getSchemaManager().getAnalyzer().mergeInsertionPoints(contents);

		// Create a list of new items by comparing the old and updated contents list.
		List<LabelItem> newItems = new ArrayList<LabelItem>();
		for (LabelItem item : contents) {
			if (!oldContents.contains(item)) {
				newItems.add(item);
			}
		}

		// Find the start and end indices to determine the range of
		// objects that should be removed from the old contents list.
		int from = -1;
		int to = -1;
		for (int i = 0; i < oldContents.size(); i++) {
			LabelItem item = oldContents.get(i);
			if (!contents.contains(item)) {
				if (from == -1) {
					from = i;
				}
				to = i;
			}
		}

		assert from != -1;
		assert to != -1;

		ResultType result = new ResultType();
		result.setNewItems(newItems);
		result.setFromIndex(from);
		result.setToIndex(to);

		getSchemaManager().addValidValues(result.getNewItems(), container);

		return result;
	}

	@Override
	public ResultType pasteElement(
			Container container,
			LabelElement element,
			InsertionPoint insPoint
	) {
		// Find the alternative index.
		LabelItemType type = element.getType();
		int index = -1;
		int alternativeIndex = -1;
		for (InsertOption option : insPoint.getAlternatives()) {
			++index;
			for (LabelItemType optionType : option.getTypes()) {
				GWT.log("Checking drop target: "
						+ "{" + type.getElementName() + ":" + type.getElementNamespace() + "}"
						+ " ==? "
						+ "{" + optionType.getElementName() + ":" + optionType.getElementNamespace() + "}");
				if (type.getElementName().equals(optionType.getElementName())
						&& type.getElementNamespace().equals(optionType.getElementNamespace())) {
					alternativeIndex = index;
					break;
				}
			}
		}

		if (alternativeIndex < 0) {
			throw new IllegalArgumentException("Cannot paste element at this position.");
		}

		// Insert an empty element of the right type.
		ResultType result = updateContainer(container, insPoint, alternativeIndex, type);

		// Generate unique IDs for the element to paste.
		updateElementIDs(element);

		// Now replace the empty element with the actual contents.
		ListIterator<LabelItem> it = result.getNewItems().listIterator();
		while (it.hasNext()) {
			LabelItem item = it.next();
			if (item instanceof LabelElement) {
				LabelElement newElement = (LabelElement) item;
				LabelItemType newType = newElement.getType();

				if (newType.getElementName().equals(type.getElementName())
						&& newType.getElementNamespace().equals(type.getElementNamespace())) {

					// Replace the insert option in the pasted element with the one generated
					// from the model, so it matches the adjacent insertion points instead of
					// the state at the time it was cut or copied.
					element.setInsertOption(newElement.getInsertOption());

					it.set(element);
					break;
				}
			}
		}

		return result;
	}

	/**
	 * Visit every element in a model fragment, setting each element ID to
	 * a unique value.
	 *
	 * @param element the root of the model fragment
	 */
	private void updateElementIDs(LabelElement element) {
		element.setID(idGenerator.getID());

		if (element instanceof Container) {
			for (LabelItem childItem : ((Container) element).getContents()) {
				if (childItem instanceof LabelElement) {
					updateElementIDs((LabelElement) childItem);
				}
			}
		}
	}

	@Override
	public Container getContainerForFile(String filePath) {
		String fileName = new File(filePath).getName();
		File uploadedFile = new File(serverConfig.getUploadRoot(), fileName);

		Container container = getContainerForRealPath(uploadedFile.getAbsolutePath());
		ProjectManager manager = projectManagerProvider.get();

		String location;
		try {
			location = manager.createNewLabel(getUser(), "");
		} catch (IOException e) {
			e.printStackTrace();
			throw new IllegalStateException("Error creating new label: " + e.getMessage());
		}

		saveLabel(fileName, container, location);
		setLocation(location);

		return container;
	}

	/**
	 * Saves the current version of the label.
	 *
	 * @param name the label name
	 * @param root the root model element
	 * @param location the label location
	 */
	private void saveLabel(String name, Container root, String location) {
		ProjectManager manager = projectManagerProvider.get();
		OutputStream out = null;

		try {
			File labelFile = manager.getLabelFile(getUser(), location);
			MessageDigest digest = MessageDigest.getInstance("MD5");
			out = new DigestOutputStream(new FileOutputStream(labelFile), digest);
			writerProvider.get().writeLabel(getSchemaManager(), root, out);
			out.close();
			labelFile.setWritable(true, false);

			if (name != null) {
				manager.setProjectItemName(getUser(), location, name);
			}
			manager.setLabelLastUpdatedIfNecessary(getUser(), location, new Date(), digest.digest());
		} catch (IOException e) {
			e.printStackTrace();
			throw new IllegalStateException("Error writing label file: " + e.getMessage());
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			throw new IllegalStateException("Error writing label file: " + e.getMessage());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw new IllegalStateException("Error writing label file: " + e.getMessage());
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					// Ignore - no way to recover.
				}
			}
		}

	}

	public String getLocation() {
		HttpSession session = sessionProvider.get();
		return (String) session.getAttribute(SessionConstants.CURRENT_PROJECT_LOCATION_PROPERTY);
	}

	private void setLocation(String location) {
		HttpSession session = sessionProvider.get();
		session.setAttribute(SessionConstants.CURRENT_PROJECT_LOCATION_PROPERTY, location);
	}

	@Override
	public void saveAttribute(LabelItem parentItem, AttributeItem attribute, String value) {
		// Nothing to do, yet.
	}

	@Override
	public Container saveSimplelItem(Container container, SimpleItem item, String value) {
		int index;

		if ((index = container.getContents().indexOf(item)) != -1) {
			container.removeItem(index);
			SimpleItem changedItem = item.copy();
			changedItem.setValue(value);
			container.addItem(index, changedItem);
		} else {
			throw new NoSuchElementException("Could not find the simple item in the parent container.");
		}

		// TODO: Should it return SimpleItem instead?
		return container;
	}

	/**
	 * Gets a container that is the top-level model object corresponding
	 * to a file in the file system.
	 *
	 * @param path the path to the file
	 * @return the container model object describing the file
	 */
	// Default scope so it can be called from unit tests.
	//TODO: Throw an exception for the client rather than returning an empty container.
	Container getContainerForRealPath(String path) {
		LabelReader reader = readerProvider.get();

		try {
			reader.setSchemaManager(getSchemaManager());
			Container container = reader.readLabel(new FileInputStream(path));
			getSchemaManager().addValidValues(container, null);
			return container;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return new Container();
		} catch (SAXException e) {
			e.printStackTrace();
			return new Container();
		} catch (IOException e) {
			e.printStackTrace();
			return new Container();
		}
	}

	@Override
	public String writeModel(Container container) throws IOException, SchemaInitializationException {
		File uploadDir = serverConfig.getUploadRoot();

		File tempFile = File.createTempFile("export", ".xml", uploadDir);
		FileOutputStream out = new FileOutputStream(tempFile);

		try {
			System.out.println("Writing label to " + tempFile.getPath());
			writerProvider.get().writeLabel(getSchemaManager(), container, out);
		} catch (ParserConfigurationException ex) {
			throw new SchemaInitializationException("Error writing the container", ex);
		}

		out.close();
		tempFile.setWritable(true, false);
		return tempFile.getName();
	}

	@Override
	public ResultType deleteElement(Container container, LabelElement element) {
		List<LabelItem> contents = container.getContents();
		List<LabelItem> oldContents = new ArrayList<LabelItem>();
		for (LabelItem item : contents) {
			oldContents.add(item);
		}

		getSchemaManager().getAnalyzer().doDelete(container, element);

		// Create a list of new items by comparing the old and updated contents list.
		List<LabelItem> newItems = new ArrayList<LabelItem>();
		for (LabelItem item : contents) {
			if (!oldContents.contains(item)) {
				newItems.add(item);
			}
		}

		// Find the start and end indices to determine the range of
		// objects that should be removed from the old contents list.
		int from = -1;
		int to = -1;
		for (int i = 0; i < oldContents.size(); i++) {
			LabelItem item = oldContents.get(i);
			if (!contents.contains(item)) {
				if (from == -1) {
					from = i;
				}
				to = i;
			}
		}

		assert from != -1;
		assert to != -1;

		ResultType result = new ResultType();
		result.setNewItems(newItems);
		result.setFromIndex(from);
		result.setToIndex(to);

		getSchemaManager().addValidValues(result.getNewItems(), container);

		return result;
	}

	@Override
	public ValidationResult validateModel(Container root) throws Exception {
		// Save the current label.
		saveLabel(null, root, getLocation());

		// Get the variable pattern string.
		String variablePattern = getVariablePattern(getLocation());

		DOMValidator validator = validatorProvider.get();
		LabelWriter writer = writerProvider.get();

		final ValidationResult result = new ValidationResult();
		result.setVariablePattern(variablePattern);

		Document doc = writer.createDocument(getSchemaManager().getDocumentBuilder(), root);
		FailureHandler handler = new FailureHandler() {
			@Override
			public void handleFailure(Node node, String message) {
				LabelElement e = (LabelElement) node.getUserData(LabelWriter.LABEL_ITEM_FOR_NODE);
				if (e != null) {
					MessageTranslator.Translation translation = translator.translate(message);

					if (translation == null) {
						result.addMessage(e.getID(), message, null, null);
					} else if (!translation.shouldOmit()) {
						result.addMessage(e.getID(), translation.translate(message), translation.getAttributeName(message), translation.getValue(message));
					}
				}
			}
		};

		validator.validate(getSchemaManager(), doc, handler);

		return result;
	}

	private String getVariablePattern(String location) throws IOException {
		ProjectManager manager = projectManagerProvider.get();
		String variableSetting = manager.getProjectItemAttribute(getUser(), location, ItemAttributes.VARIABLE_STYLE);

		if (ItemAttributes.FREEMARKER_STYLE.equals(variableSetting)) {
			return ".*\\$.*";
		} else if (ItemAttributes.VELOCITY_STYLE.equals(variableSetting)) {
			return ".*\\$\\{.*\\}.*";
		} else {
			return null;
		}
	}

	private void refreshSchemaManager() {
		HttpSession session = sessionProvider.get();
		sessionAttributesManager.removeAttribute(session.getId(), SessionConstants.CURRENT_SCHEMA_MANAGER_PROPERTY);

		// Initialize the schema manager so it's ready for use.
		getSchemaManager();
	}

	private SchemaManager getSchemaManager() {
		HttpSession session = sessionProvider.get();
		if (sessionAttributesManager.getAttribute(session.getId(), SessionConstants.CURRENT_SCHEMA_MANAGER_PROPERTY, SchemaManager.class) == null) {
			LOG.debug("Creating schema manager for user '{}'", session.getAttribute(SessionConstants.USER_ID_PROPERTY));

			SchemaManager schemaManager = schemaManagerProvider.get();
			ProjectManager projectManager = projectManagerProvider.get();
			SchemaDefaults defaults = schemaDefaultsProvider.get();

			// Add the uploaded local schema files.
			try {
				for (File f : projectManager.getDefaultSchemaFiles(getUser())) {
					schemaManager.addSchemaFile(f.toURI());
				}
			} catch (IOException e) {
				System.err.println("Error reading uploaded schema file: " + e.getMessage());
				e.printStackTrace();
			}

			// Add any default schema files not overridden by the user's uploads.
			for (URI uri : defaults.getSchemaURIs()) {
				schemaManager.addSchemaFile(uri);
			}

			schemaManager.loadSchemas();
			sessionAttributesManager.setAttribute(session.getId(), SessionConstants.CURRENT_SCHEMA_MANAGER_PROPERTY, schemaManager);
		}

		return sessionAttributesManager.getAttribute(session.getId(), SessionConstants.CURRENT_SCHEMA_MANAGER_PROPERTY, SchemaManager.class);
	}

	@Override
	public Collection<String> getNamespaces() {
		return getSchemaManager().getAnalyzer().getNamespaces();
	}

	@Override
	public Collection<String> getElementNamesByNamespace(String namespace) {
		return getSchemaManager().getAnalyzer().getElementNamesByNamespace(namespace);
	}

	@Override
	public LabelItemType getTypeForElement(String elementName, String elementNS) {
		return getSchemaManager().getAnalyzer().getTypeForElement(elementName, elementNS);
	}

	@Override
	public String getUser() {
		HttpSession session = sessionProvider.get();
		String userID = (String) session.getAttribute(SessionConstants.USER_ID_PROPERTY);

		LOG.debug("Getting current user: {}", userID);
		return userID;
	}

	@Override
	public void setUser(String userID) throws ServiceException {
		LOG.debug("Setting current user: {}", userID);

		if (userID==null || userID.isEmpty()) {
			throw new IllegalArgumentException("User ID cannot be empty");
		}

		ProjectManager manager = projectManagerProvider.get();
		try {
			manager.createUserIfNeccesary(userID);
		} catch (IOException e) {
			e.printStackTrace();
			throw new ServiceException("Cannot create user directory: " + e.getMessage());
		}
		HttpSession session = sessionProvider.get();
		session.setAttribute(SessionConstants.USER_ID_PROPERTY, userID);

		// Create a new schema manager so there is no delay in
		// getting the available namespaces and elements when
		// the new label dialog is shown.
		refreshSchemaManager();
	}

	@Override
	public ProjectItem[] getProjectsItems(String location) throws ServiceException {
		String userID = getUser();
		if (userID==null || userID.isEmpty()) {
			throw new IllegalAccessError("User must be authenticated.");
		}

		ProjectManager manager = projectManagerProvider.get();
		try {
			return manager.getProjectItems(userID, location);
		} catch (IOException e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public Container getContainerForLocation(String location) throws ServiceException {
		ProjectManager manager = projectManagerProvider.get();
		try {
			File labelFile = manager.getLabelFile(getUser(), location);
			manager.copyDefaults(getUser(), location);
			setLocation(location);
			refreshSchemaManager();
			return getContainerForRealPath(labelFile.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
			throw new ServiceException("Should not happen: label file does not exist: " + e.getMessage());
		}
	}

	@Override
	public void deleteProjectItem(String location) throws ServiceException {
		ProjectManager manager = projectManagerProvider.get();
		try {
			manager.deleteProjectItem(getUser(), location);
		} catch (IOException e) {
			System.err.println("Error deleting project item (user=" + getUser() + ", location=" + location + "): " + e.getMessage());
			e.printStackTrace();
			throw new ServiceException("Error deleting label: " + e.getMessage());
		}
	}

	@Override
	public String[] getDefaultSchemaFiles() throws ServiceException {
		try {
			List<String> result = new ArrayList<String>();
			ProjectManager manager = projectManagerProvider.get();

			for (File f : manager.getDefaultSchemaFiles(getUser())) {
				result.add(f.getName());
			}

			return result.toArray(new String[result.size()]);
		} catch (IOException e) {
			e.printStackTrace();
			throw new ServiceException("Error reading the user's default configuration: " + e.getMessage());
		}
	}

	@Override
	public void addDefaultSchemaFile(String filename) throws ServiceException {
		File uploadedFile = new File(serverConfig.getUploadRoot(), getLastPathComponent(filename));

		ProjectManager manager = projectManagerProvider.get();
		try {
			manager.addDefaultSchemaFile(getUser(), uploadedFile);
		} catch (IOException e) {
			e.printStackTrace();
			throw new ServiceException("Error adding new schema file: " + e.getMessage());
		}

		refreshSchemaManager();
	}

	private String getLastPathComponent(String path) {
		int lastSlashPos = path.lastIndexOf('/');
		lastSlashPos = Math.max(lastSlashPos, path.lastIndexOf('\\'));

		if (lastSlashPos >= 0) {
			return path.substring(lastSlashPos+1);
		} else {
			return path;
		}
	}

	@Override
	public void removeDefaultSchemaFile(String filename) throws ServiceException {
		ProjectManager manager = projectManagerProvider.get();

		try {
			manager.removeDefaultSchemaFile(getUser(), filename);
		} catch (IOException e) {
			e.printStackTrace();
			throw new IllegalArgumentException("Cannot delete schema file: " + e.getMessage());
		}
	}

	@Override
	public void setLabelName(String newName) {
		ProjectManager manager = projectManagerProvider.get();

		try {
			manager.setProjectItemName(getUser(), getLocation(), newName);
			manager.setLabelLastUpdated(getUser(), getLocation(), new Date());
		} catch (IOException e) {
			e.printStackTrace();
			throw new IllegalStateException("Error changing the item name: " + e.getMessage());
		}
	}

	@Override
	public String getItemAttribute(String key) {
		ProjectManager manager = projectManagerProvider.get();

		try {
			return manager.getProjectItemAttribute(getUser(), getLocation(), key);
		} catch (IOException e) {
			e.printStackTrace();
			throw new IllegalStateException("Error changing the item name: " + e.getMessage());
		}
	}

	@Override
	public void setItemAttribute(String key, String value) {
		ProjectManager manager = projectManagerProvider.get();

		try {
			manager.setProjectItemAttribute(getUser(), getLocation(), key, value);
			manager.setLabelLastUpdated(getUser(), getLocation(), new Date());
		} catch (IOException e) {
			e.printStackTrace();
			throw new IllegalStateException("Error changing the item name: " + e.getMessage());
		}
	}

}
