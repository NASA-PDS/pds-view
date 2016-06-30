package gov.nasa.arc.pds.lace.server.validation;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import gov.nasa.arc.pds.lace.server.schema.SchemaManager;
import gov.nasa.arc.pds.lace.server.schema.DefaultSchemaManagerModule;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

@Guice(modules = {DefaultSchemaManagerModule.class})
public class DOMValidatorTest {

	private static final String INCOMPLETE_LABEL_PATH = "src/test/resources/incomplete-product.xml";

	private static final String NODE_ERRORS = "node-errors";
	
	@Inject
	private SchemaManager schemaManager;
	
	@Inject
	private DOMValidator validator;
	
	private XPathFactory xPathFactory;
	
	@BeforeMethod
	public void init() {
		xPathFactory = new net.sf.saxon.xpath.XPathFactoryImpl();
	}
	
	@Test
	public void testIncompleteLabel() throws ParserConfigurationException, SAXException, IOException, XPathExpressionException, TransformerException {
		Document document = loadXML(INCOMPLETE_LABEL_PATH);
		validator.validate(schemaManager, document, new FailureHandler() {
			@Override
			public void handleFailure(Node node, String message) {
				addError(node, message);
			}
		});
		
		List<String> messages;
		
		messages = getErrors(document, "//*:Product_Observational[namespace-uri()='http://pds.nasa.gov/pds4/pds/v03']");
		assertEquals(messages.size(), 0);
		
		messages = getErrors(document, "//*:File_Area_Observational[namespace-uri()='http://pds.nasa.gov/pds4/pds/v03']");
		assertEquals(messages.size(), 1);
		assertTrue(messages.get(0).contains("is not complete"));
		
		messages = getErrors(document, "//*:Internal_Reference[namespace-uri()='http://pds.nasa.gov/pds4/pds/v03']/*:reference_type[namespace-uri()='http://pds.nasa.gov/pds4/pds/v03']");
		assertTrue(messages.size() >= 1);
		boolean foundEnumerationMessage = false;
		for (String message : messages) {
			foundEnumerationMessage |= message.contains("one of the following values");
		}
		assertTrue(foundEnumerationMessage, "Did not find enumeration message for 'reference_type'.");
	}
	
	private Document loadXML(String path) throws SAXException, IOException, ParserConfigurationException {
		SAXParserFactory saxFactory = SAXParserFactory.newInstance();
		saxFactory.setNamespaceAware(true);
		saxFactory.setValidating(false);
		SAXParser parser = saxFactory.newSAXParser();

		Document document = schemaManager.getDocumentBuilder().newDocument();
		parser.parse(new File(path), new DocumentCreationHandler(document));
		return document;
	}
	
	private void addError(Node node, String message) {
		@SuppressWarnings("unchecked")
		List<String> messages = (List<String>) node.getUserData(NODE_ERRORS);
		if (messages == null) {
			messages = new ArrayList<String>();
			node.setUserData(NODE_ERRORS, messages, null);
		}
		messages.add(message);
	}
	
	private List<String> getErrors(Node root, String xPath) throws XPathExpressionException {
		DOMSource source = new DOMSource(root);
		Node node = (Node) xPathFactory.newXPath().evaluate(xPath, source, XPathConstants.NODE);
		@SuppressWarnings("unchecked")
		List<String> messages = (List<String>) node.getUserData(NODE_ERRORS);
		return (messages==null ? new ArrayList<String>() : messages);
	}
}
