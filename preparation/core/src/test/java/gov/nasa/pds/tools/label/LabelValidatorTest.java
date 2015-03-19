package gov.nasa.pds.tools.label;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
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
		showExceptions(container);
		assertThat(container.hasFatal(), is(false));
		assertThat(container.hasError(), is(false));
	}

  private void showExceptions(ExceptionContainer container) {
    for (LabelException exception : container.getExceptions()) {
      System.out.println(
          String.format("%s:%d:%d: %s", exception.getExceptionType().toString(),
              exception.getLineNumber(), exception.getColumnNumber(),
              exception.getMessage()
          )
      );
		}
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
		showExceptions(container);
		assertThat(container.hasFatal(), is(false));
		assertThat(container.hasError(), is(true));
		// All errors should be on lines 3 or 4.
		for (LabelException exception : container.getExceptions()) {
		  if (exception.getExceptionType() == ExceptionType.ERROR) {
		    assertThat(exception.getLineNumber(), anyOf(is(3), is(4)));
		  }
		}
	}

	@Test
	public void testXIncludeWithGoodLabel() throws IOException, MalformedURLException, SAXException, ParserConfigurationException, TransformerException, MissingLabelSchemaException {
		LabelValidator validator = new LabelValidator();
		ExceptionContainer container = new ExceptionContainer();
		validator.setSchema(Collections.singletonList(new File("src/test/resources/PDS4_PDS_1301.xsd").toURI().toURL()));
		validator.setSchematrons(Collections.singletonList(pds4Schematron));
		validator.validate(container, new File("src/test/resources/XIncludeParent.xml"));
    showExceptions(container);
		assertThat(container.hasFatal(), is(false));
		assertThat(container.hasError(), is(false));
	}

	// Test that there is a Schematron error for a collection label
	// indicating that "comma" is not a valid delimiter.
  @Test
  public void testCollectionDelimiter() throws IOException, MalformedURLException, SAXException, ParserConfigurationException, TransformerException, MissingLabelSchemaException {
    LabelValidator validator = new LabelValidator();
    ExceptionContainer container = new ExceptionContainer();
    validator.setSchema(Collections.singletonList(new File("src/test/resources/PDS4_PDS_1301.xsd").toURI().toURL()));
    validator.setSchematrons(Collections.singletonList(pds4Schematron));
    validator.validate(container, new File("src/test/resources/Collection_data.xml"));
    showExceptions(container);
    assertThat(container.hasFatal(), is(false));
    assertThat(container.hasError(), is(false));
    assertThat(container.hasWarning(), is(true));

    for (LabelException exception : container.getExceptions()) {
      if (exception.getExceptionType() == ExceptionType.WARNING) {
        assertThat(exception.getLineNumber(), is(126));
      }
    }
  }

  // Test that XML validation messages have source locations.
  @Test
  public void testXMLValidationHasLocation() throws IOException, MalformedURLException, SAXException, ParserConfigurationException, TransformerException, MissingLabelSchemaException {
    LabelValidator validator = new LabelValidator();
    ExceptionContainer container = new ExceptionContainer();
    validator.setSchema(Collections.singletonList(new File("src/test/resources/PDS4_PDS_1301.xsd").toURI().toURL()));
    validator.setSchematrons(Collections.singletonList(pds4Schematron));
    validator.validate(container, new File("src/test/resources/ele_mom_tblChar.xml"));
    showExceptions(container);
    assertThat(container.hasFatal(), is(false));
    assertThat(container.hasError(), is(true));
    assertThat(container.hasWarning(), is(false));

    for (LabelException exception : container.getExceptions()) {
      if (exception.getExceptionType()==ExceptionType.ERROR && exception.getMessage().contains("matching wildcard")) {
        assertThat(exception.getLineNumber(), not(is(-1)));
      }
    }
  }

  // Test that errors on files containing CDATA sections and XML
  // entities have source locations.
  @Test
  public void testXMLSpecialConstructs() throws IOException, MalformedURLException, SAXException, ParserConfigurationException, TransformerException, MissingLabelSchemaException {
    LabelValidator validator = new LabelValidator();
    ExceptionContainer container = new ExceptionContainer();
    validator.setSchema(Collections.singletonList(new File("src/test/resources/PDS4_PDS_1301.xsd").toURI().toURL()));
    validator.setSchematrons(Collections.singletonList(pds4Schematron));
    validator.validate(container, new File("src/test/resources/ProductWithSpecialXML.xml"));
    showExceptions(container);
    assertThat(container.hasFatal(), is(false));
    assertThat(container.hasError(), is(true));
    assertThat(container.hasWarning(), is(true));

    for (LabelException exception : container.getExceptions()) {
      if (exception.getExceptionType()==ExceptionType.ERROR) {
        assertThat(exception.getLineNumber(), not(is(-1)));
      }
    }
  }

}
