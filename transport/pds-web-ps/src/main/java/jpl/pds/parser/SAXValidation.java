// Copyright 1999-2005 California Institute of Technology. ALL RIGHTS
// RESERVED. U.S. Government Sponsorship acknowledged.
//

package jpl.pds.parser;

import java.io.*;
import java.net.*;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.apache.xml.serialize.LineSeparator;

/**
 * Use SAX parser and schema file to validate the XML
 * version of the product file.  The handlers are overridden to
 * capture the error messages.
 *
 * @author J. Crichton
 */
public class SAXValidation extends DefaultHandler {

    /**
     * Create Sax Validation object.
     *
     * @param runLog XML output stream.
     */
    public SAXValidation(final RunLog runLog) {
        super();
        this.runLog = runLog;
        this.xmlFilename = "XMLFile";
    }

    /**
     * Validates the product XML file using the  DS schema.
     *
     * @param document Product in DOM format.
     * @param xsdFilename Data Set XSD schema filename.
     * @param runLog RunLog object used for writing messages to cmd window and output log.
     * @return True if XML validates.
     */
    public final boolean validateFile(final Document document, final String xsdFilename) {
        this.xsdFilename = xsdFilename;
        ByteArrayOutputStream xmlOutputStream = null;
        int initialErrorCount = runLog.getErrorCount();
        int initialWarningCount = runLog.getWarningCount();

        runLog.append("<semanticValidation>", 2);
        try {
            //
            // Convert label DOM  to XML buffer
            //
            OutputFormat format = new OutputFormat(document);
            format.setLineSeparator(LineSeparator.Windows);
            format.setIndenting(true);
            format.setLineWidth(0);
            format.setPreserveSpace(false);

            try {
                //Serializing and save to ByteArray
                xmlOutputStream = new ByteArrayOutputStream();
                XMLSerializer serializer = new XMLSerializer(xmlOutputStream, format);
                serializer.asDOMSerializer();
                serializer.serialize(document);
            } catch(IOException ex) {
                runLog.errorMessage("IO Exception serializing DOM to XML internal file</errorMessage>", 4);
                return false;
            }
            if (debug) try {
                FileOutputStream fos = new FileOutputStream("label.xml");
                fos.write(xmlOutputStream.toByteArray());
                fos.close();
            } catch(IOException e) {
                System.err.println("Error: IO Exception creating XML file label.xml");
            }


            //
            // Use SAXreader to validate with schema
            //

            // create parser
            XMLReader xmlReader = null;
            try {
                xmlReader = XMLReaderFactory.createXMLReader(DEFAULT_PARSER_NAME);
                xmlReader.setContentHandler(this);
                xmlReader.setErrorHandler(this);
            } catch (Exception ex) {
                runLog.errorMessage("Unable to instantiate SAX parser", 4);
                return false;
            }

            // set parser features
            String featureId = null;
            try {
                featureId = NAMESPACES_FEATURE_ID;
                xmlReader.setFeature(NAMESPACES_FEATURE_ID, DEFAULT_NAMESPACES);
                featureId = NAMESPACE_PREFIXES_FEATURE_ID;
                xmlReader.setFeature(NAMESPACE_PREFIXES_FEATURE_ID, DEFAULT_NAMESPACE_PREFIXES);
                featureId = VALIDATION_FEATURE_ID;
                xmlReader.setFeature(VALIDATION_FEATURE_ID, DEFAULT_VALIDATION);
                featureId = SCHEMA_VALIDATION_FEATURE_ID;
                xmlReader.setFeature(SCHEMA_VALIDATION_FEATURE_ID, DEFAULT_SCHEMA_VALIDATION);
                featureId = SCHEMA_FULL_CHECKING_FEATURE_ID;
                xmlReader.setFeature(SCHEMA_FULL_CHECKING_FEATURE_ID, DEFAULT_SCHEMA_FULL_CHECKING);
                featureId = DYNAMIC_VALIDATION_FEATURE_ID;
                xmlReader.setFeature(DYNAMIC_VALIDATION_FEATURE_ID, DEFAULT_DYNAMIC_VALIDATION);
                featureId = SCHEMA_LOCATION_PROPERTY_ID;
                xmlReader.setProperty(SCHEMA_LOCATION_PROPERTY_ID, xsdFilename);
            } catch (Exception e) {
                runLog.warningMessage("Parser does not support feature (" + featureId + ")</errorMessage>", 4);
                return false;
            }

            // setup access to source lines for error messages
            ByteArrayInputStream xmlStream = new ByteArrayInputStream(xmlOutputStream.toByteArray());
            xmlLineReader = new LineNumberReader(new BufferedReader(new InputStreamReader(xmlStream)));

            // parse xml file
            try {
                ByteArrayInputStream xmlInputStream = new ByteArrayInputStream(xmlOutputStream.toByteArray());
                xmlReader.parse(new InputSource(xmlInputStream));
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
                runLog.errorMessage("SAX Error: " + ex.getMessage(), 4);
                return false;
            }

        } finally {
            runLog.append("<numberErrors>" + (runLog.getErrorCount()-initialErrorCount) + "</numberErrors>", 4);
            runLog.append("<numberWarnings>" + (runLog.getWarningCount()-initialWarningCount) + "</numberWarnings>", 4);
            runLog.append("</semanticValidation>", 2);
        }
        return (!runLog.foundErrorsWarnings());
    }

    //
    // ErrorHandler methods
    //

    /**
     * Overrides warnings and print warning message.
     *
     * @param ex Exception information.
     */
    public final void warning(final SAXParseException ex) {
        printError("Warning",  ex);
    }

    /**
     * Overrides error and print error message.
     *
     * @param ex Exception information.
     */
    public final void error(final SAXParseException ex) {
        printError("Error",  ex);
    }

    /**
     * Overrides fatalError and prints fatal error message.
     *
     * @param ex Exception information.
     */
    public final void fatalError(final SAXParseException ex) {
        printError("Fatal Error",  ex);
    }

    /**
     * Prints the error message.
     *
     * @param type Error message type: warn, error, or fatal.
     * @param ex Exception information.
     */
    private void printError(final String type, final SAXParseException ex) {
        StringBuffer message = new StringBuffer();

        // get error line from xml source if error is not in xsd file
        String errorLine = null;
        try {
            while (xmlLineReader.getLineNumber() < ex.getLineNumber())
                errorLine = xmlLineReader.readLine();
        } catch (IOException e) {System.err.println("printError Exception: " + e.getMessage());}
        if (errorLine == null || ex.getLineNumber() <= 3) {
            message.append("Error in " + xsdFilename + NL);
        } else {
            message.append(type + " in " + xmlFilename + " [" + ex.getLineNumber()
                + ":" + ex.getColumnNumber() + "]" + NL);
            message.append(errorLine.trim() + NL);
        }

        String msg = ex.getMessage();
        int beg = msg.indexOf(": ") + 2;
        if (msg.startsWith("cvc-enumeration-valid: ")) {
           // eliminate long list of valid values
           int end = msg.indexOf("'[");
           if (end > 0) msg = msg.substring(0,end-1)+ " list.";
        } else if (msg.startsWith("cvc-complex-type.2.4.a:")) {
           // eliminate long list of valid values
           int end = msg.indexOf(" The content must match");
           if (end > 0) msg = msg.substring(0,end-1);
        }
        // eliminate useless error code
        if (beg > 2) msg = msg.substring(beg);
        message.append(msg);
        if (type.equals("Warning")) runLog.warningMessage(message.toString(), 4);
        else runLog.errorMessage(message.toString(), 4);
    }

    /** Newline used by PDS. */
    private static final String NL = "\r\n";

    /** Runlog for  output stream. */
    private RunLog runLog;

    /** XML filename being validated. */
    private String xmlFilename;

    /** XSD filename being validated. */
    private String xsdFilename;

    /** Input stream that keeps track of lines. */
    private LineNumberReader xmlLineReader = null;

    /** Debug flag. */
    private boolean debug = false;

    /** Schema used for validating the XML file. */
    private static final String SCHEMA_LOCATION_PROPERTY = "jpl.pds.parser.SAXvalidation.schemaLocation";

    /** Default parser name. */
    private static final String DEFAULT_PARSER_NAME = "org.apache.xerces.parsers.SAXParser";

    /** Namespaces feature id. */
    private static final String NAMESPACES_FEATURE_ID = "http://xml.org/sax/features/namespaces";
    /** True if using NAMESPACES_FEATURE. */
    private static final boolean DEFAULT_NAMESPACES = true;

    /** Namespace prefixes feature id. */
    private static final String NAMESPACE_PREFIXES_FEATURE_ID = "http://xml.org/sax/features/namespace-prefixes";
    /** True if using NAMESPACE_PREFIXES_FEATURE. */
    private static final boolean DEFAULT_NAMESPACE_PREFIXES = true;

    /** Validation feature id. */
    private static final String VALIDATION_FEATURE_ID = "http://xml.org/sax/features/validation";
    /** True if using VALIDATION_FEATURE. */
    private static final boolean DEFAULT_VALIDATION = true;

    /** Schema validation feature id. */
    private static final String SCHEMA_VALIDATION_FEATURE_ID = "http://apache.org/xml/features/validation/schema";
    /** True if using SCHEMA_VALIDATION_FEATURE. */
    private static final boolean DEFAULT_SCHEMA_VALIDATION = true;

    /** Schema full checking feature id. */
    private static final String SCHEMA_FULL_CHECKING_FEATURE_ID = "http://apache.org/xml/features/validation/schema-full-checking";
    /** True if using SCHEMA_FULL_CHECKING_FEATURE. */
    private static final boolean DEFAULT_SCHEMA_FULL_CHECKING = true;

    /** Dynamic validation feature id. */
    private static final String DYNAMIC_VALIDATION_FEATURE_ID = "http://apache.org/xml/features/validation/dynamic";
    /** True if using DYNAMIC_VALIDATION_FEATURE. */
    private static final boolean DEFAULT_DYNAMIC_VALIDATION = true;

    /** External schmema property id. */
    private static final String SCHEMA_LOCATION_PROPERTY_ID = "http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation";
}
