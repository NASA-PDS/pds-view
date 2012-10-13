// Copyright 1999-2005 California Institute of Technology. ALL RIGHTS
// RESERVED. U.S. Government Sponsorship acknowledged.
//

package jpl.pds.parser;

import java.io.*;
import java.util.*;
import java.net.*;
import jpl.eda.xmlquery.XMLQuery;
import jpl.eda.xmlquery.Result;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Handle product validation request.
 *
 * 1) Read product file into StringBuffer. Insert any referenced files.
 * 2) Validate product file using Patti's Antlr parser and create xml file.
 * 3) Validate xml file using SAX parser and schema.
 * 4) Returns result of validation as XML file.
 *
 * @author J. Crichton
 */
public class ValidateLabelHandler {
    /**
     * Handle the request for validation.
     *
     * @param  q XMLQuery containing the validation query.
     * @param file Label file to be validated.
     * @param xsdFilename XSD schema file used for validating the product.
     * @return XMLQuery with validation results.
     */
    public final XMLQuery validate(final XMLQuery q, final File file, final String xsdFilename) {
        Document document = null;
        String rootName = null;

        // Only one file will be passed
        if (file == null) return q;

        // Check if the user wants a compatible MIME type
        List mimes = q.getMimeAccept();
        if (mimes.contains("*/*") || mimes.contains("text/*") || mimes.contains("text/xml")
            || mimes.isEmpty()) {
            String labelFilename = file.getAbsolutePath();
            StringBuffer mergedLabelfile = new StringBuffer();

            RunLog runLog = new RunLog();
            //System.err.println("ODL PARSING ...");

            String hostname = null;
            try {
                InetAddress localHost = InetAddress.getLocalHost();
                hostname = localHost.getHostName();
            } catch (UnknownHostException ignore) {}

            runLog.append("<?xml version=\"1.0\"?>", 0);
            if (hostname.equals("biker"))
                runLog.append("<!DOCTYPE odlValidation PUBLIC \"-//JPL/DTD OODT odlValidation 1.0//EN\" \"http://biker:9002/dtd/odlValidation.dtd\">", 0);
            else
                runLog.append("<!DOCTYPE odlValidation PUBLIC \"-//JPL/DTD OODT odlValidation 1.0//EN\" \"http://starbrite.jpl.nasa.gov/dtd/odlValidation.dtd\">", 0);
            runLog.append("<odlValidation>", 0);

            // Get host and file. Read in product. Write XML <product> element
            runLog.append("<product>", 2);
            runLog.append("<host>" + hostname + "</host>", 4);
            runLog.append("<xsd>" + xsdFilename + "</xsd>", 4);
            runLog.append("<file>" + labelFilename + "</file>", 4);

            // Merge referenced files with label file
            if (!runLog.foundErrorsWarnings()) {
                try {
                    ReadLabelFile reader = new ReadLabelFile(mergedLabelfile, runLog);
                    reader.read(labelFilename);
                } catch(FileNotFoundException ex) {
                    runLog.errorMessage("Error opening label file: " + ex.getMessage(), 4);
                } catch (IOException ex) {
                    runLog.errorMessage("Error reading label file: " + ex.getMessage(), 4);
                }
            }

            // Load DS XSD schema file
            Document xsdDocument = null;
            if (!runLog.foundErrorsWarnings()) try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                factory.setValidating(false);
                factory.setNamespaceAware(true);
                DocumentBuilder builder = factory.newDocumentBuilder();
                xsdDocument = builder.parse(xsdFilename);
            } catch (Exception ex) {
                runLog.errorMessage("Error loading DS schema: " + ex.getMessage(), 4);
            }

            // Check root name for valid DS XSD
            if (!runLog.foundErrorsWarnings()) {
                // Check root name for valid DS XSD
                Node xmlNode = xsdDocument.getDocumentElement();
                NodeList nodes = xmlNode.getChildNodes();
                for (int i=0; i<nodes.getLength(); i++) {
                    Node node = nodes.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        rootName = ((Element)node).getAttribute("name");
                        break;
                    }
                }
            }

            // Output err & warn count and close product tag
            runLog.append("<numberErrors>" + runLog.getErrorCount() + "</numberErrors>", 4);
            runLog.append("<numberWarnings>0</numberWarnings>", 4);
            runLog.append("</product>", 2);

            // Validate product with ODL syntax parser. Write XML <syntaxValidation> element
            ByteArrayOutputStream xmlOutputStream = null;
            if (!runLog.foundErrorsWarnings()) {
                BufferedReader reader = new BufferedReader(new StringReader(mergedLabelfile.toString()));
                document = ODLExtendedParser.parseFile("MergedLabelFile", reader, rootName, runLog); // parse it
            }

            // Validate product with SAX semantic parser. Write XML <semanticValidation> element
            boolean saxValidated = false;
            if (document != null) {
                SAXValidation saxValidation = new SAXValidation(runLog);
                saxValidated = saxValidation.validateFile(document, xsdFilename);
            }

            // Finish XML output and send XML stream
            runLog.append("</odlValidation>", 0);
            Result result = result = new Result("1", "text/html", "", "", Collections.EMPTY_LIST, runLog.toString());
            q.getResults().add(result);
        }
        return q;
    }

/////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
      * Main entry for testing the ValidateLabelHandler.
      *
      * @param args Command line arguments.
      */
    public static void main(final String[] args) {
        // if we have product and schema
        if (args.length == 2) {
            String queryString = "OFSN = " + args[0] + " AND XSD = " + args[1];
            XMLQuery query = new XMLQuery(queryString, /*id*/"cli-1",
                /*title*/"Command-line Query",
                /*desc*/"This query came from the cmd-line and is directed to the ParseHandler",
                /*ddId*/null, /*resultModeId*/null, /*propType*/null, /*propLevels*/null,
                XMLQuery.DEFAULT_MAX_RESULTS, null);
            ValidateLabelHandler handler = new ValidateLabelHandler();
            File file = new File(args[0]);
            String[] metaNames = {"MISSION_NAME", "TARGET_NAME","ROVER_MOTION_COUNTER"};
            try {
                handler.validate(query, file, args[1]);
                //System.out.println(query.getXMLDocString());
                List results = query.getResults();
                Result result = (Result)results.get(0);
                BufferedReader reader = new BufferedReader(new InputStreamReader(result.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    System.err.println(line);
                }
            } catch (Exception e) {
                System.err.println("Exception " + e.getClass().getName() + " querying for file "
                    + args[0] + ": " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.err.println("Usage: java jpl.pds.parser.ValidateLabelHandler <label file> <schema URL>");
        }
    }
}

