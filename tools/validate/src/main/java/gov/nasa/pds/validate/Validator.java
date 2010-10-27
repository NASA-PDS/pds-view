package gov.nasa.pds.validate;

import gov.nasa.pds.validate.report.Report;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

public abstract class Validator {
    protected Report report;
    protected Schema schema;

    public Validator(Report report) {
        this.report = report;
        this.schema = null;
    }

    public void setSchema(List<File> schemaFiles) throws SAXException {
        List<StreamSource> schemas = new ArrayList<StreamSource>();
        for (File schema : schemaFiles) {
            schemas.add(new StreamSource(schema));
        }
        SchemaFactory factory = SchemaFactory
          .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        this.schema = factory.newSchema((Source[]) schemas.toArray());
    }

    public abstract void validate(File file) throws SAXException, IOException;
}
