package gov.nasa.arc.pds.lace.server;

import gov.nasa.arc.pds.lace.client.service.LabelContentsService;
import gov.nasa.arc.pds.lace.server.parse.LabelReader;
import gov.nasa.arc.pds.lace.server.parse.LabelWriter;
import gov.nasa.arc.pds.lace.server.parse.ModelAnalyzer;
import gov.nasa.arc.pds.lace.server.parse.ValidationAnalyzer;
import gov.nasa.arc.pds.lace.shared.Container;
import gov.nasa.arc.pds.lace.shared.InsertionPoint;
import gov.nasa.arc.pds.lace.shared.LabelItem;
import gov.nasa.arc.pds.lace.shared.ResultType;
import gov.nasa.arc.pds.lace.shared.SimpleItem;
import gov.nasa.arc.pds.lace.shared.exception.SchemaInitializationException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Implementation of the label template contents service.
 */
public class LabelContentsServiceImpl extends RemoteServiceServlet implements LabelContentsService {

	private static final long serialVersionUID = 1L;

	private static final String SCHEMA_0300A = "PDS4_OPS_0300a.xsd";
	private static final String SCHEMA_0300A_NS = "http://pds.nasa.gov/pds4/pds/v03";

//	private static final String SCHEMA_0310B = "PDS4_PDS_0310b.xsd";
//	private static final String SCHEMA_0310B_NS = "http://pds.nasa.gov/pds4/pds/v03";

	private String schemaNamespace;
	private ModelAnalyzer analyzer = null;
	private LabelWriter writer = null;
	private ValidationAnalyzer validation = new ValidationAnalyzer();

	/**
	 * Creates an instance of the service.
	 * @throws URISyntaxException if there is an error creating a URI to the schema
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws ClassNotFoundException
	 * @throws SAXException
	 * @throws ClassCastException
	 */
	public LabelContentsServiceImpl() throws URISyntaxException, ClassCastException, SAXException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		schemaNamespace = SCHEMA_0300A_NS;
		URI schemaURI = getClass().getResource(SCHEMA_0300A).toURI();
		initialize(schemaURI);
	}

	public LabelContentsServiceImpl(URI schemaURI, String schemaNamespace) throws ClassCastException, SAXException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		this.schemaNamespace = schemaNamespace;
		initialize(schemaURI);
	}

	//TODO: Should have namespace as an explicit argument, here and in the LabelContentsService.
	@Override
	public Container getRootContainer(String elementName) {
		Container container = analyzer.getContainerForElement(elementName, schemaNamespace);
		validation.addValidValues(container);
		analyzer.expandInsertionPoints(container);
		return container;
	}

	@Override
	public ResultType updateContainer(Container container, InsertionPoint insPoint, int index) {
		List<LabelItem> contents = container.getContents();
		List<LabelItem> oldContents = new ArrayList<LabelItem>();
		for (LabelItem item : contents) {
			oldContents.add(item);
		}
		
		// Remove the selected insertion point from the contents.
		int pos = contents.indexOf(insPoint);
		contents.remove(pos);
		
		// Insert a new container.
		List<LabelItem> list = analyzer.doInsert(insPoint, index);
		for (LabelItem item : list) {
			contents.add(pos, item);
			++pos;
		}								
		
		// Merge the insertion points, if any, in the updated contents list. 
		analyzer.mergeInsertionPoints(contents);
		
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
		
		ResultType type = new ResultType();
		type.setContents(newItems);
		type.setFromIndex(from);
		type.setToIndex(to);
		return type;
	}

	@Override
	public Container getContainerForFile(String filePath) {
		String fileName = new File(filePath).getName();
		String newPath =
			getServletContext().getRealPath("") + File.separator + "upload" + File.separator + fileName;

		return getContainerForRealPath(newPath);
	}

	@Override
	public Container saveSimplelItem(Container container, SimpleItem item, String value) {				
		int index;
		
		if ((index = container.getContents().indexOf(item)) != -1) {
			container.removeItem(index);
			SimpleItem changedItem = (SimpleItem) item.copy();
			changedItem.setValue(value);
			container.addItem(index, changedItem);
		} else {
			throw new NoSuchElementException("Could not find the simple item in the parent container.");
		}

		// TODO: change the return type to SimpleItem?
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
	Container getContainerForRealPath(String path) {
		LabelReader reader = null;
		try {
			URI schemaURI = getClass().getResource(SCHEMA_0300A).toURI();
			reader = new LabelReader(schemaURI);
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new Container();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new Container();
		}

		try {
			Container container = reader.readLabel(new FileInputStream(path));
			validation.addValidValues(container);
			return container;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new Container();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new Container();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new Container();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new Container();
		}
	}

	/*
	 * Instantiates the XSLoader implementation and loads the XML schema.
	 */
	private void initialize(URI schemaURI) throws ClassCastException, SAXException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		writer = new LabelWriter(schemaURI);
		analyzer = new ModelAnalyzer(schemaURI);
	}
	
	@Override
	public String writeModel(Container container) throws IOException, SchemaInitializationException {
		@SuppressWarnings("deprecation")
		String uploadPath = getThreadLocalRequest().getRealPath("upload");
		File tempFile = File.createTempFile("export", ".xml", new File(uploadPath));
		FileOutputStream out = new FileOutputStream(tempFile);

		try {
			System.out.println("Writing label to " + tempFile.getPath());
			writer.writeLabel(container, out);
		} catch (ParserConfigurationException ex) {
			throw new SchemaInitializationException("Error writing the container", ex);
		}

		out.close();
		return tempFile.getName();
	}
}
