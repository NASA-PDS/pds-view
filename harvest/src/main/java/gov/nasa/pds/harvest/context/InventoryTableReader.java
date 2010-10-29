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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.io.FilenameUtils;
import org.w3c.dom.NodeList;

/**
 * Class that supports reading of a table-version of the PDS
 * Inventory file.
 *
 * @author mcayanan
 *
 */
public class InventoryTableReader implements InventoryReader, InventoryKeys {
    public static String FILE_SPEC_FIELD_NUM_XPATH = "//*[starts-with("
        + "name(),'Table_Record')]/"
        + "Table_Field_File_Specification_Name/field_number";

    public static String LIDVID_FIELD_NUM_XPATH =
        "//Table_Record_Inventory_LIDVID/Table_Field_LIDVID/field_number | "
        + "//Table_Record_Inventory_LID/Table_Field_LID/field_number";

    public static String DATA_FILE_XPATH = "//File_Area/File/file_name";

    private int lidvidFieldLocation;
    private int filenameFieldLocation;
    private int checksumFieldLocation;
    private BufferedReader reader;
    private String parentDirectory;

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
    public InventoryTableReader(File file, PDSNamespaceContext context)
    throws InventoryReaderException {
        filenameFieldLocation = 0;
        checksumFieldLocation = 0;
        lidvidFieldLocation = 0;
        parentDirectory = file.getParent();
        try {
            XMLExtractor extractor = new XMLExtractor(file);
            extractor.setDefaultNamespace(context.getDefaultNamepsace());
            extractor.setNamespaceContext(context);
            File dataFile = new File(FilenameUtils.separatorsToSystem(
                    extractor.getValueFromDoc(DATA_FILE_XPATH)));
            if(!dataFile.isAbsolute()) {
                dataFile = new File(file.getParent(), dataFile.toString());
            }
            reader = new BufferedReader(new FileReader(dataFile));
            filenameFieldLocation = Integer.parseInt(
                    extractor.getValueFromDoc(FILE_SPEC_FIELD_NUM_XPATH));
            lidvidFieldLocation = Integer.parseInt(
                    extractor.getValueFromDoc(LIDVID_FIELD_NUM_XPATH));
        } catch (Exception e) {
            throw new InventoryReaderException(e.getMessage());
        }
    }

    /**
     * Constructor
     *
     * @param file A PDS Inventory file
     * @param context A PDSNamespaceContext object, which allows this
     * method to handle namespaces while extracting metadata from the
     * Inventory file.
     *
     * @throws InventoryReaderException
     */
    public InventoryTableReader(String file, PDSNamespaceContext context)
    throws InventoryReaderException {
        this(new File(file), context);
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
        String line = "";
        try {
            line = reader.readLine();
            if(line == null || line.equals(""))
                return null;
        } catch (IOException i) {
            throw new InventoryReaderException(i.getMessage());
        }
        File file = null;
        String lidvid = "";
        String tokens[] = line.split(",");
        if(filenameFieldLocation != 0) {
            file = new File(FilenameUtils.separatorsToSystem(
                    tokens[filenameFieldLocation-1].trim()));
            if(!file.isAbsolute()) {
                file = new File(parentDirectory, file.toString());
            }
        }
        if(lidvidFieldLocation != 0)
            lidvid = tokens[lidvidFieldLocation-1].trim();

        return new InventoryEntry(file, null, lidvid);
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
