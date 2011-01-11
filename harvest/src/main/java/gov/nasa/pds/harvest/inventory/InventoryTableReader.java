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
package gov.nasa.pds.harvest.inventory;

import gov.nasa.pds.harvest.constants.Constants;
import gov.nasa.pds.harvest.util.XMLExtractor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FilenameUtils;

/**
 * Class that supports reading of a table-version of the PDS
 * Inventory file.
 *
 * @author mcayanan
 *
 */
public class InventoryTableReader implements InventoryReader {
    /** The field number of the LID-VID. */
    private int lidvidFieldLocation;

    /** The field number of the file reference. */
    private int filenameFieldLocation;

    /** The field number of the checksum. */
    private int checksumFieldLocation;

    /** Reads the external data file of the Inventory file. */
    private LineNumberReader reader;

    /** The directory path of the inventory file. */
    private String parentDirectory;

    /** The data file being read. */
    private File dataFile;

    /**
     * Constructor.
     *
     * @param file A PDS Inventory file.
     *
     * @throws InventoryReaderException If an error occurred while reading
     * the Inventory file.
     */
    public InventoryTableReader(File file)
    throws InventoryReaderException {
        filenameFieldLocation = 0;
        checksumFieldLocation = 0;
        lidvidFieldLocation = 0;
        dataFile = null;
        parentDirectory = file.getParent();
        XMLExtractor extractor = new XMLExtractor();
        try {
            extractor.parse(file);
            dataFile = new File(FilenameUtils.separatorsToSystem(
                    extractor.getValueFromDoc(
                            Constants.DATA_FILE_XPATH)));
            if (!dataFile.isAbsolute()) {
                dataFile = new File(file.getParent(), dataFile.toString());
            }
            reader = new LineNumberReader(new FileReader(dataFile));
            filenameFieldLocation = Integer.parseInt(
                    extractor.getValueFromDoc(
                            Constants.FILE_SPEC_FIELD_NUM_XPATH));
            lidvidFieldLocation = Integer.parseInt(
                    extractor.getValueFromDoc(
                            Constants.LIDVID_FIELD_NUM_XPATH));
        } catch (Exception e) {
            throw new InventoryReaderException(e.getMessage());
        }
    }

    /**
     * Constructor.
     *
     * @param file A PDS Inventory file
     *
     * @throws InventoryReaderException If an error occurred while reading
     * the Inventory file.
     */
    public InventoryTableReader(String file)
    throws InventoryReaderException {
        this(new File(file));
    }

    /**
     * Gets the data file that is being read.
     *
     * @return the data file.
     */
    public File getDataFile() {
        return dataFile;
    }

    /**
     * Gets the line number that was just read.
     *
     * @return the line number.
     */
    public int getLineNumber() {
        return reader.getLineNumber();
    }

    /**
     * Gets the next product file reference in the PDS Inventory file.
     *
     * @return A class representation of the next product file reference
     * in the PDS inventory file. If the end-of-file has been reached,
     * a null value will be returned.
     *
     * @throws InventoryReaderException If an error occurred while reading
     * the Inventory file.
     *
     */
    public InventoryEntry getNext() throws InventoryReaderException {
        String line = "";
        try {
            line = reader.readLine();
            if (line == null || line.equals("")) {
                reader.close();
                return null;
            }
        } catch (IOException i) {
            throw new InventoryReaderException(i.getMessage());
        }
        File file = null;
        String lidvid = "";
        String tokens[] = line.split(",");
        if (filenameFieldLocation != 0) {
            file = new File(FilenameUtils.separatorsToSystem(
                    tokens[filenameFieldLocation-1].trim()));
            if (!file.isAbsolute()) {
                file = new File(parentDirectory, file.toString());
            }
        }
        if (lidvidFieldLocation != 0) {
            lidvid = tokens[lidvidFieldLocation-1].trim();
        }
        return new InventoryEntry(file, null, lidvid);
    }
}
