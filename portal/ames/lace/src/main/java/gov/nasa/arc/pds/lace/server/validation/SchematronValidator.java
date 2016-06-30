package gov.nasa.arc.pds.lace.server.validation;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SchematronValidator {

	private static final String[] TRANSFORMATIONS = {
		"schematron/single-assertions.xslt",
		"schematron/hoist-context.xslt",
		"iso-schematron-xslt2/iso_dsdl_include.xsl",
		"iso-schematron-xslt2/iso_abstract_expand.xsl",
		"iso-schematron-xslt2/iso_svrl_for_xslt2.xsl",
	};

	private TransformerFactory transformerFactory;
	private Transformer schematronTransformer;

	public SchematronValidator(File schematronFile) throws TransformerException, MalformedURLException, IOException, URISyntaxException {
		transformerFactory = new net.sf.saxon.TransformerFactoryImpl();
		StreamSource schematronSource = new StreamSource(schematronFile);

		DOMResult schematronResult = applyTransforms(schematronSource, TRANSFORMATIONS);
		schematronTransformer = transformerFactory.newTransformer(new DOMSource(schematronResult.getNode()));
	}

	public Node validate(Source source) throws TransformerException {
		DOMResult result = new DOMResult();
		schematronTransformer.transform(source, result);
		return result.getNode();
	}

	public void findErrors(Node document, FailureHandler handler) throws TransformerException, XPathExpressionException {
		DOMSource documentSource = new DOMSource(document);
		Node errorDocument = validate(new DOMSource(document));
		XPathFactory xPathFactory = new net.sf.saxon.xpath.XPathFactoryImpl();
		
		XPath failurePath = xPathFactory.newXPath();
		NodeList failures = (NodeList) failurePath.evaluate(
				"//*:failed-assert[namespace-uri()='http://purl.oclc.org/dsdl/svrl']",
				new DOMSource(errorDocument),
				XPathConstants.NODESET
		);

		for (int i=0; i < failures.getLength(); ++i) {
			Node failure = failures.item(i);
			String location = ((Attr) failure.getAttributes().getNamedItem("location")).getValue();
			String message = failure.getTextContent().trim();

			XPath documentPath = xPathFactory.newXPath();
			Node failureNode = (Node) documentPath.evaluate(location, documentSource, XPathConstants.NODE);
			handler.handleFailure(failureNode, message);
		}
	}

	private DOMResult applyTransforms(Source firstSource, String... transformScripts) throws TransformerException, IOException, URISyntaxException {
		DOMResult result = null;

		for (String script : transformScripts) {
			Source source = (result==null ? firstSource : new DOMSource(result.getNode()));
			result = new DOMResult();

			Source scriptSource = new StreamSource(getSchematronResource(script));
			Transformer transformer = transformerFactory.newTransformer(scriptSource);
			transformer.transform(source, result);
		}

		return result;
	}

	public File getSchematronResource(String resourceName) throws URISyntaxException {
		URL resourceURL = getClass().getResource("/" + resourceName);
		return new File(resourceURL.toURI());
	}

}
