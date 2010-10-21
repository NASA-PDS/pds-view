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
package gov.nasa.pds.harvest.context;

import gov.nasa.pds.harvest.policy.Namespace;
import gov.nasa.pds.harvest.util.PDSNamespaceContext;
import gov.nasa.pds.harvest.util.XMLExtractor;

import java.io.File;

import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.io.FilenameUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Class that supports the reading of an XML version of the
 * PDS Inventory file.
 *
 * @author mcayanan
 *
 */
public class InventoryXMLReader implements InventoryReader {
    public static final String MEMBER_ENTRY =
                      "//*[ends-with(name(),'Member_Entry')]";
    private String parentDirectory;
    private int index;
    private XMLExtractor extractor;
    private NodeList memberEntries;

    /**
     * Constructor.
     *
     * @param file A PDS Inventory file
     * @param context A PDSNamespaceContext object, which allows this
     * method to handle namespaces while extracting metadata from the
     * Inventory file.
     *
     * @throws InventoryReaderException
     */
    public InventoryXMLReader(File file, PDSNamespaceContext context)
    throws InventoryReaderException {
        index = 0;
        parentDirectory = file.getParent();
        try {
            extractor = new XMLExtractor(file);
            extractor.setDefaultNamespace(context.getDefaultNamepsace());
            extractor.setNamespaceContext(context);
            memberEntries = extractor.getNodesFromDoc(MEMBER_ENTRY);
        } catch (Exception e) {
            throw new InventoryReaderException(
                    "Error reading inventory file: " + e.getMessage());
        }
    }

    /**
     * Gets the next product file reference in the PDS Inventory file.
     *
     * @return A class representation of the next product file reference
     * in the PDS inventory file. If the end-of-file has been reached,
     * a null value will be returned.
     *
     */
    public InventoryEntry getNext() throws InventoryReaderException {
        if(index >= memberEntries.getLength())
            return null;

        Node entry = memberEntries.item(index++);
        File file = null;
        String checksum = null;
        try {
            file = new File(
                    FilenameUtils.separatorsToSystem(
                    extractor.getValueFromItem("directory_path_name", entry))
                    );
            checksum = extractor.getValueFromItem("md5_checksum", entry);
        } catch (XPathExpressionException x) {
            throw new InventoryReaderException(x.getMessage());
        }
        if(!file.isAbsolute()) {
            file = new File(parentDirectory, file.toString());
        }
        return new InventoryEntry(file, checksum);
    }

    public static void main(String args[]) {
        try {
            Namespace ns = new Namespace();
            ns.setPrefix("pds");
            ns.setUri("http://pds.nasa.gov/schema/pds4/pds");
            PDSNamespaceContext context =
                new PDSNamespaceContext(ns, ns.getUri());
            InventoryTableReader reader =
                new InventoryTableReader(args[0], context);

            for(InventoryEntry entry = reader.getNext(); entry != null;) {
                System.out.println("Member Entry: " + entry.getFile());
                entry = reader.getNext();
            }

        } catch (InventoryReaderException e) {
            e.printStackTrace();
        }
    }
}
