package gov.nasa.arc.pds.lace.server;

import gov.nasa.arc.pds.lace.client.service.LabelContentsService;
import gov.nasa.arc.pds.lace.server.parse.LabelReader;
import gov.nasa.arc.pds.lace.server.parse.LabelWriter;
import gov.nasa.arc.pds.lace.server.parse.ModelAnalyzer;
import gov.nasa.arc.pds.lace.server.parse.ValidationAnalyzer;
import gov.nasa.arc.pds.lace.shared.Container;
import gov.nasa.arc.pds.lace.shared.InsertionPoint;
import gov.nasa.arc.pds.lace.shared.LabelItem;
import gov.nasa.arc.pds.lace.shared.LabelItemType;
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
import java.util.Iterator;
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
	public Container updateContainer(Container container, InsertionPoint insPoint, int index) {
		LabelItemType selectedType = insPoint.getAlternatives().get(index);
		List<LabelItem> list = new ArrayList<LabelItem>();
		insertContainer(insPoint, selectedType, selectedType.getInitialContents(), list);

		List<LabelItem> contents = container.getContents();
		int pos = contents.indexOf(insPoint);
		contents.remove(pos);

		for (LabelItem item : list) {
			LabelItem copy = item.copy();
			analyzer.expandInsertionPoints(copy);
			contents.add(pos, copy);
			++pos;
		}
		
		analyzer.mergeInsertionPoints(contents);
		container.setContents(contents);
		return container;
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
		List<LabelItem> content = container.getContents();
		SimpleItem changedItem  = new SimpleItem();
		int index;

		if ((index = content.indexOf(item)) != -1) {
			container.removeItem(index);
			item.setValue(value);
			changedItem = item;
			container.addItem(index, changedItem);
		} else {
			throw new NoSuchElementException("Could not find the simple item in the parent container.");
		}

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

	/* ------------------------------------------------------------------------------------------------------ *
	 * 													   													  *
	 *  Private/helper methods						   													      *
	 *  												   													  *
	 * -------------------------------------------------------------------------------------------------------*/

	/*
	 * Instantiates the XSLoader implementation and loads the XML schema.
	 */
	private void initialize(URI schemaURI) throws ClassCastException, SAXException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		writer = new LabelWriter(schemaURI);
		analyzer = new ModelAnalyzer(schemaURI);
	}

	/**
	 * Creates a Container object for the given type and contents.
	 */
	private Container createContainer(LabelItemType type, List<LabelItem> contents) {
		Container container = new Container();
		container.setType(type);
		List<LabelItem> newContents = new ArrayList<LabelItem>();
		for (LabelItem item : contents) {
			newContents.add(item.copy());
		}
		container.setContents(newContents);
		return container;
	}

	private void insertContainer(InsertionPoint insPoint, LabelItemType type, List<LabelItem> contents, List<LabelItem> labelItems) {
		Container container = createContainer(type, contents);

		// If this is an optional element insertion point with max occurrences of 1,
		// just add the container to the list. Otherwise, change the display type of
		// the insertion point to "plus_button" and split it.
		if (insPoint.getDisplayType().equals(InsertionPoint.DisplayType.OPTIONAL.getDisplayType())) {
			if (type.getMaxOccurrences() == 1) {
				labelItems.add(container);
				return;
			}

			insPoint.setDisplayType(InsertionPoint.DisplayType.PLUS_BUTTON.getDisplayType());
		} else if (insPoint.getDisplayType().equals(InsertionPoint.DisplayType.CHOICE.getDisplayType())) {
			insPoint.setDisplayType(InsertionPoint.DisplayType.PLUS_BUTTON.getDisplayType());
		}

		List<LabelItemType> alternatives = insPoint.getAlternatives();
		int start = insPoint.getUsedBefore() + 1;

		// Split the insertion point
		InsertionPoint insPoint1 = createInsertionPoint(
				alternatives,
				insPoint.getInsertFirst(),
				start,
				insPoint.getUsedBefore(),
				start,
				insPoint.getDisplayType()
		);
		InsertionPoint insPoint2 = createInsertionPoint(
				alternatives,
				start,
				insPoint.getInsertLast(),
				start,
				insPoint.getUsedAfter(),
				insPoint.getDisplayType()
		);

		labelItems.add(insPoint1);
		labelItems.add(container);
		labelItems.add(insPoint2);
	}

	private InsertionPoint createInsertionPoint(List<LabelItemType> alternatives,
			int insertFirst, int insertLast, int usedBefore, int usedAfter, String displayType) {
		InsertionPoint insPoint = new InsertionPoint();
		insPoint.setAlternatives(alternatives);
		insPoint.setInsertFirst(insertFirst);
		insPoint.setInsertLast(insertLast);
		insPoint.setUsedBefore(usedBefore);
		insPoint.setUsedAfter(usedAfter);
		insPoint.setDisplayType(displayType);
		return insPoint;
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
