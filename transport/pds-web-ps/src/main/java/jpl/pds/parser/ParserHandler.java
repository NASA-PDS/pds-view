// Copyright 1999-2005 California Institute of Technology. ALL RIGHTS
// RESERVED. U.S. Government Sponsorship acknowledged.
//

package jpl.pds.parser;

import java.io.*;
import java.util.*;
import jpl.eda.xmlquery.XMLQuery;
import jpl.eda.xmlquery.QueryElement;
import jpl.eda.product.QueryHandler;

/**
 * Handle product queries for validation of PDS products and queries
 * for metadata values.
 *
 * This class is a product parser query handler that accepts queries of the form
 * OFSN = &lt;filename&gt; AND XSD=&lt;url&gt; AND RT=VALIDATION
 * OFSN = &lt;filename&gt; AND METANAMES=&lt;name1,name2,...&gt; AND RT=METADATA
 *
 * After decoding the query, ValidateLabelHandler or MetaDataHandler is used to
 * handle the query.
 *
 * @author J. Crichton
 */
public class ParserHandler implements QueryHandler {
    /**
     * Create ParserHandler object that validates the product or finds metadata
     * in the product.
     */
    public ParserHandler() {
       validateLabelHandler = new ValidateLabelHandler();
       metaDataHandler = new MetaDataHandler();
    }

    /**
     * Do the work of processing the query.
     *
     * @param query Query request for validation or metadata.
     * @return Query with the validation results or metadata values.
     */
    public final XMLQuery query(final XMLQuery query) {
        // We handle queries of the form ONLINE_FILE_SPECIFICATION_NAME =
        // some/file, so make sure there's at least three WHERE set elements, and
        // that they're an elemName of "ONLINE_FILE_SPECIFICATION_NAME", a literal
        // (with the filename), and a RELOP of "EQ".

        List where = query.getWhereElementSet();
        if (where.size() < 3) {
            System.err.println("ParserHandler received non-ONLINE_FILE_SPECIFICATION_NAME query");
            return query;
        }

        String filename = null;
        boolean gotFile = false;
        boolean gotFilename = false;

        boolean gotXsdElement = false;
        boolean gotXsdUrl = false;
        String xsdUrl = null;

        boolean gotMetaElement = false;
        boolean gotMetaNames = false;
        String[] metaNames = null;

        boolean gotReturnElement = false;
        boolean gotReturnType = false;
        String returnType = null;

        boolean gotOp = false;

        for (Iterator i = where.iterator(); i.hasNext();) {
            QueryElement queryElement = (QueryElement) i.next();
            if (queryElement.getRole().equals("LITERAL")) {
                // We pop of stack so value will be traversed after element
                if (!gotFile && gotFilename) {
                    filename = queryElement.getValue();
                    gotFile = true;
                } else if (!gotXsdUrl && gotXsdElement) {
                    xsdUrl = queryElement.getValue();
                    gotXsdUrl = true;
                } else if (!gotMetaNames && gotMetaElement) {
                    metaNames = queryElement.getValue().split(",");
                    for (int j=0; j<metaNames.length; j++) metaNames[j] = metaNames[j].trim();
                    gotMetaNames = true;
                } else if (!gotReturnType && gotReturnElement) {
                    returnType = queryElement.getValue();
                    gotReturnType = true;
                }
            } else if (queryElement.getRole().equals("elemName")
                && (queryElement.getValue().equals("ONLINE_FILE_SPECIFICATION_NAME")
                || queryElement.getValue().equals("OFSN"))) {
                gotFilename = true;
            } else if (queryElement.getRole().equals("elemName")
                && (queryElement.getValue().equals("XSD"))) {
                gotXsdElement = true;
            } else if (queryElement.getRole().equals("elemName")
                && (queryElement.getValue().equals("METANAMES"))) {
                gotMetaElement = true;
            } else if (queryElement.getRole().equals("elemName")
                && (queryElement.getValue().equals("RETURN_TYPE")
                || queryElement.getValue().equals("RT"))) {
                gotReturnElement = true;
            } else if (queryElement.getRole().equals("RELOP") && queryElement.getValue().equals("EQ")) {
                gotOp = true;
            }
        }

        if (!gotFile || !gotFilename || !gotOp || !gotReturnType
            || (!returnType.equals("VALIDATION") && !returnType.equals("METADATA"))) {
            System.err.println("ParserHandler received non-ONLINE_FILE_SPECIFICATION_NAME query");
            System.err.println("Query object is " + query);
            return query;
        }

        if (returnType.equals("VALIDATION") && !gotXsdUrl) {
            System.err.println("ParserHandler received VALIDATION request without XSD file");
            System.err.println("Query object is " + query);
            return query;
        }

        if (returnType.equals("METADATA") && !gotMetaNames) {
            System.err.println("ParserHander received METADATA request with Meta Names");
            System.err.println("Query object is " + query);
            return query;
        }

        // Okay, got the file names, now determine what to do.
        try {
            File productDir = new File(System.getProperty(ParserHandler.PRODUCT_DIR_PROPERTY, "C:"));
            File file = new File(productDir, filename);
            if (returnType.equals("VALIDATION"))
                return validateLabelHandler.validate(query, file, xsdUrl);
            if (returnType.equals("METADATA"))
                return metaDataHandler.queryForMetaData(query, file, metaNames);
            return query;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            System.err.println("Exception " + ex.getClass().getName() + " querying for file " + filename + ": "
                + ex.getMessage());
            ex.printStackTrace();
            return query;
        }
    }

    /** Handler for validating the product. */
    private ValidateLabelHandler validateLabelHandler;

   /** Handler for return the metadata values. */
    private MetaDataHandler metaDataHandler;

    /** Name of the property that specifies where products are kept. */
    private static final String PRODUCT_DIR_PROPERTY = "jpl.pds.parser.ParserHandler.productDir";

    /**
     * Main entry for testing ParserHandler.
     *
     * @param argv Command-line arguments.
     */
    public static void main(final String[] argv) {
        if (argv.length != 1) {
            System.err.println("Usage: java jpl.pds.parser.ParserHandler \"OFSN=<label file> AND XSD=<schema URL>"
                + " AND METANAMES=\\\"<name1>,<name2>,...\\\"\"");
            System.exit(1);
        }

        XMLQuery query = new XMLQuery(argv[0], /*id*/"cli-1",
            /*title*/"Command-line Query",
            /*desc*/"This query came from the command-line and is directed to the ParserHandler for the return type",
            /*ddId*/null, /*resultModeId*/null, /*propType*/null, /*propLevels*/null,
            XMLQuery.DEFAULT_MAX_RESULTS, null);
        ParserHandler handler = new ParserHandler();
        handler.query(query);
        System.out.println("\nquery.getXMLDocString() *****");
        System.out.println(query.getXMLDocString());
        System.out.println("\nquery.getResult() *****");
        System.out.println(query.getResult());
        System.exit(0);
    }
}
