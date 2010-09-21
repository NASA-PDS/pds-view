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

import gov.nasa.pds.harvest.util.XMLExtractor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.w3c.dom.NodeList;

/**
 * Class that supports reading of a table-version of the PDS
 * Inventory file.
 *
 * @author mcayanan
 *
 */
public class InventoryTableReader implements InventoryReader {
    public static String INVENTORY_FIELDS = "//*[starts-with(name(),'Table') = 'Table']" +
    		"/*[substring(name(),string-length(name()) - string-length('Field') + 1) = 'Field']";
    public static String DATA_FILE = "//File_Area/File/file_name";
    private int filenameFieldLocation;
    private int checksumFieldLocation;
    private BufferedReader reader;

    public InventoryTableReader(File label) throws InventoryReaderException {
        filenameFieldLocation = 0;
        checksumFieldLocation = 0;
        try {
            XMLExtractor extractor = new XMLExtractor(label);
            File dataFile = new File(extractor.getValueFromDoc(DATA_FILE));
            if(!dataFile.isAbsolute())
                dataFile = new File(label.getParent(), dataFile.toString());
            reader = new BufferedReader(new FileReader(dataFile));
            NodeList fields = extractor.getNodesFromDoc(INVENTORY_FIELDS);
            for(int i=0; (i < fields.getLength()) && (fields != null); i++) {
                String fieldName = extractor.getValueFromItem("field_name", fields.item(i));
                if("directory_path_name".equals(fieldName)) {
                    filenameFieldLocation = Integer.parseInt(
                            extractor.getValueFromItem("field_number", fields.item(i)));
                } else if("md5_checksum".equals(fieldName)) {
                    checksumFieldLocation = Integer.parseInt(
                            extractor.getValueFromItem("field_number", fields.item(i)));
                }
            }
        } catch (Exception e) {
            throw new InventoryReaderException(e.getMessage());
        }
    }

    public InventoryTableReader(String label) throws InventoryReaderException {
        this(new File(label));
    }

    public InventoryEntry getNext() throws InventoryReaderException {
        String line = "";
        try {
            line = reader.readLine();
            if(line == null || line.equals(""))
                return null;
        } catch (IOException i) {
            throw new InventoryReaderException(i.getMessage());
        }
        String file = "";
        String checksum = "";
        String tokens[] = line.split(",");
        if(filenameFieldLocation != 0)
            file = tokens[filenameFieldLocation-1];
        if(checksumFieldLocation != 0)
            checksum = tokens[checksumFieldLocation-1];

        return new InventoryEntry(new File(file), checksum);
    }

    public static void main(String args[]) {
        try {
            InventoryTableReader reader = new InventoryTableReader(args[0]);

            for(InventoryEntry entry = reader.getNext(); entry != null;) {
                System.out.println("Member Entry: " + entry.getFile());
                entry = reader.getNext();
            }

        } catch (InventoryReaderException e) {
            e.printStackTrace();
        }
    }
}
