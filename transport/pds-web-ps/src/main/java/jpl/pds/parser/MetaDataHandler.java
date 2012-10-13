// Copyright 1999-2005 California Institute of Technology. ALL RIGHTS
// RESERVED. U.S. Government Sponsorship acknowledged.
//

package jpl.pds.parser;

import java.io.*;
import java.util.*;
import java.net.*;

import jpl.eda.xmlquery.XMLQuery;
import jpl.eda.xmlquery.Result;

import org.w3c.dom.Document;

/**
 * Handle Metadata request.
 * 1) Read product file into StringBuffer. Insert any referenced files.
 * 2) Validate ODL file using Patti's Antlr parser and create xml DOM.
 * 3) Search xml DOM for metadata.
 * 4) Return metadata as XML stream.
 *
 * @author J. Crichton
 */
public class MetaDataHandler {
    /**
     * Handle the request for metadata.
     *
     * @param  q XMLQuery containing the metadata names requested.
     * @param file Label file to be searched for metadata.
     * @param metaNames List of metadata names.
     * @return XMLQuery with metadata.
     */
    public final XMLQuery queryForMetaData(final XMLQuery q, final File file,
        final String[] metaNames) {

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

            // Output err & warn count and close product tag
            runLog.append("<numberErrors>" + runLog.getErrorCount() + "</numberErrors>", 4);
            runLog.append("<numberWarnings>0</numberWarnings>", 4);
            runLog.append("</product>", 2);

            // Validate product with ODL syntax parser. Write XML <syntaxValidation> element
            ByteArrayOutputStream xmlOutputStream = null;
            if (!runLog.foundErrorsWarnings()) {
                BufferedReader reader = new BufferedReader(new StringReader(mergedLabelfile.toString()));
                document = ODLExtendedParser.parseFile("MergedLabelFile", reader, null, runLog); // parse it
            }

            // Return the metadata
            LabelMetadata.getMetadata(document, metaNames, runLog);

            // Finish XML output and send XML stream
            runLog.append("</odlValidation>", 0);
            Result result = result = new Result("1", "text/html", "", "", Collections.EMPTY_LIST, runLog.toString());
            q.getResults().add(result);
        }
        return q;
    }

/////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
      * Main entry for testing the MetaDataHandler.
      *
      * @param args Command line arguments.
      */
    public static void main(final String[] args) {
        // if we have product and schema
        if (args.length == 2) {
            String queryString = "OFSN = " + args[0] + " AND METANAMES = " + args[1];
            XMLQuery query = new XMLQuery(queryString, /*id*/"cli-1",
                /*title*/"Command-line Query",
                /*desc*/"This query came from the cmd-line and is directed to the ParseHandler",
                /*ddId*/null, /*resultModeId*/null, /*propType*/null, /*propLevels*/null,
                XMLQuery.DEFAULT_MAX_RESULTS, null);
            MetaDataHandler handler = new MetaDataHandler();
            File file = new File(args[0]);
            String[] metaNames = {"MISSION_NAME", "TARGET_NAME","ROVER_MOTION_COUNTER"};
            metaNames = args[1].split(",");
            try {
                handler.queryForMetaData(query, file, metaNames);
                //System.out.println(query.getXMLDocString());
                List results = query.getResults();
                Result result = (Result)results.get(0);
                BufferedReader reader = new BufferedReader(new InputStreamReader(result.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    System.err.println(line);
                }
            } catch (Exception e) {
                System.err.println(e.getClass().getName() + " querying for file "
                    + args[0] + ": " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.err.println("Usage: java jpl.pds.parser.MetaDataHandler <label file> <schema URL>");
        }
    }
}

