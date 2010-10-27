// Copyright 2006-2010, by the California Institute of Technology.
// ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
// Any commercial use must be negotiated with the Office of Technology Transfer
// at the California Institute of Technology.
//
// This software is subject to U. S. export control laws and regulations
// (22 C.F.R. 120-130 and 15 C.F.R. 730-774). To the extent that the software
// is subject to U.S. export control laws and regulations, the recipient has
// the responsibility to obtain export licenses or other export authority as
// may be required before exporting such information to foreign countries or
// providing access to foreign nationals.
//
// $Id$
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
