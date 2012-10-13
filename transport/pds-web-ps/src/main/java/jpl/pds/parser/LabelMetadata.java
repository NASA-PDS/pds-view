// Copyright 1999-2005 California Institute of Technology. ALL RIGHTS
// RESERVED. U.S. Government Sponsorship acknowledged.
//

package jpl.pds.parser;

import java.io.*;
import java.net.*;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Search the label file in XML DOM format for the metanames.  Return meta values in
 * the runlog.
 *
 * @author J. Crichton
 */

public class LabelMetadata {

    /**
     * Retrieve the metadata and format in the runLog as XML.
     *
     * @param document Label file in DOM format.
     * @param metaNames Array of element names.
     * @param runLog Buffer for formatting the output.
     */
    public static void getMetadata(final Document document, final String[] metaNames, final RunLog runLog) {
        runLog.append("<metadata>", 2);
        try {
            if (metaNames == null || metaNames.length == 0) return;
            for (int i=0; i<metaNames.length; i++) {
                NodeList nodes = document.getElementsByTagName(metaNames[i]);
                for (int j=0; j<nodes.getLength(); j++) {
                    Node node = nodes.item(j);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        runLog.append("<element name=\"" + metaNames[i] + "\">"
                            + node.getFirstChild().getNodeValue()
                            + "</element>", 4);
                    }
                }
            }

            // Try this again with metaNames + "_VALUE_SEQUENCE"
            for (int i=0; i<metaNames.length; i++) {
                NodeList nodes = document.getElementsByTagName(metaNames[i] + "_VALUE_SEQUENCE");
                for (int j=0; j<nodes.getLength(); j++) {
                    Node node = nodes.item(j);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        NodeList childNodes = node.getChildNodes();
                        StringBuffer sequenceValues = new StringBuffer();
                        for (int k=0; k<childNodes.getLength(); k++) {
                            node = childNodes.item(k);
                            if (node.getNodeType() == Node.ELEMENT_NODE) {
                                if (k > 0) sequenceValues.append(',');
                                sequenceValues.append(node.getFirstChild().getNodeValue());
                            }
                        }
                        if (sequenceValues.length() > 0) {
                            runLog.append("<element name=\"" + metaNames[i] + "\">"
                                + sequenceValues.toString() + "</element>", 4);
                        }
                    }
                }
            }

        } finally {
            runLog.append("</metadata>", 2);
        }
    }
}
