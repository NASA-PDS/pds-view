package gov.nasa.pds.tools.label;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collections;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

@RunWith(JUnit4.class)
public class LabelValidatorTest {

	private static Transformer pds4Schematron;

	@BeforeClass
	public static void init() throws MalformedURLException, TransformerConfigurationException, TransformerException {
		SchematronTransformer transformer = new SchematronTransformer();
		pds4Schematron = transformer.transform(new File("src/test/resources/PDS4_PDS_1301.sch").toURI().toURL());
	}

	@Test
	public void testSimpleProduct() throws IOException, MalformedURLException, SAXException, ParserConfigurationException, TransformerException, MissingLabelSchemaException {
		LabelValidator validator = new LabelValidator();
		ExceptionContainer container = new ExceptionContainer();
		validator.setSchema(Collections.singletonList(new File("src/test/resources/PDS4_PDS_1301.xsd").toURI().toURL()));
		validator.setSchematrons(Collections.singletonList(pds4Schematron));
		validator.validate(container, new File("src/test/resources/SimpleProduct.xml"));
		for (LabelException exception : container.getExceptions()) {
			System.out.println(exception.getExceptionType().toString() + ": " + exception.getMessage());
		}
		assertThat(container.hasFatal(), is(false));
		assertThat(container.hasError(), is(false));
	}

	@Test(expected=SAXParseException.class)
	public void testXMLError() throws IOException, MalformedURLException, SAXException, ParserConfigurationException, TransformerException, MissingLabelSchemaException {
		LabelValidator validator = new LabelValidator();
		ExceptionContainer container = new ExceptionContainer();
		validator.setSchema(Collections.singletonList(new File("src/test/resources/PDS4_PDS_1301.xsd").toURI().toURL()));
		validator.setSchematrons(Collections.singletonList(pds4Schematron));
		validator.validate(container, new File("src/test/resources/ProductWithXMLError.xml"));
	}

	@Test
	public void testSchematronError() throws IOException, MalformedURLException, SAXException, ParserConfigurationException, TransformerException, MissingLabelSchemaException {
		LabelValidator validator = new LabelValidator();
		ExceptionContainer container = new ExceptionContainer();
		validator.setSchema(Collections.singletonList(new File("src/test/resources/PDS4_PDS_1301.xsd").toURI().toURL()));
		validator.setSchematrons(Collections.singletonList(pds4Schematron));
		validator.validate(container, new File("src/test/resources/ProductWithSchematronError.xml"));
		for (LabelException exception : container.getExceptions()) {
			System.out.println(exception.getExceptionType().toString() + ": " + exception.getMessage());
		}
		assertThat(container.hasFatal(), is(false));
		assertThat(container.hasError(), is(true));
		// All errors should be on line 4.
		for (LabelException exception : container.getExceptions()) {
			assertThat(exception.getLineNumber(), is(-1));
		}
	}

	@Test
	public void testXIncludeWithGoodLabel() throws IOException, MalformedURLException, SAXException, ParserConfigurationException, TransformerException, MissingLabelSchemaException {
		LabelValidator validator = new LabelValidator();
		ExceptionContainer container = new ExceptionContainer();
		validator.setSchema(Collections.singletonList(new File("src/test/resources/PDS4_PDS_1301.xsd").toURI().toURL()));
		validator.setSchematrons(Collections.singletonList(pds4Schematron));
		validator.validate(container, new File("src/test/resources/XIncludeParent.xml"));
		for (LabelException exception : container.getExceptions()) {
			System.out.println(exception.getExceptionType().toString() + ": " + exception.getMessage());
		}
		assertThat(container.hasFatal(), is(false));
		assertThat(container.hasError(), is(false));
	}

}
