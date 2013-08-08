package gov.nasa.pds.search.core.schema;

import gov.nasa.pds.search.core.util.XMLValidationEventHandler;

import java.io.File;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class CoreConfigReader {
    public final static String CONFIG_PACKAGE = "gov.nasa.pds.search.core.schema";
    public final static String CONFIG_SCHEMA = "core-config.xsd";

    public static Product unmarshall(InputStream coreConfigXML)
    throws SAXParseException, JAXBException, SAXException {
        return unmarshall(new StreamSource(coreConfigXML));
    }

    public static Product unmarshall(File coreConfigXML)
    throws SAXParseException, JAXBException, SAXException {
        return unmarshall(new StreamSource(coreConfigXML));
    }

    public static Product unmarshall(StreamSource coreConfigXML)
    throws JAXBException, SAXException, SAXParseException {
        JAXBContext jc = JAXBContext.newInstance(CONFIG_PACKAGE);
        Unmarshaller um = jc.createUnmarshaller();
        SchemaFactory sf = SchemaFactory.newInstance(
                javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = null;
        try {
            schema = sf.newSchema(
                    CoreConfigReader.class.getResource(CONFIG_SCHEMA));
        } catch (SAXException se) {
            throw new SAXException("Problems parsing core configuration schema: "
                    + se.getMessage());
        }
        um.setSchema(schema);
        um.setEventHandler(new XMLValidationEventHandler());
        JAXBElement<Product> product = um.unmarshal(coreConfigXML, Product.class);
        return product.getValue();
    }
}
