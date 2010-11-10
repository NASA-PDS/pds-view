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
package gov.nasa.pds.harvest.constants;

import java.util.HashMap;

/**
 * Class that holds constants used in Harvest.
 *
 * @author mcayanan
 *
 */
public class Constants {
    /** The PDS namespace. */
    public static final String PDS_NAMESPACE =
        "http://pds.nasa.gov/schema/pds4/pds";

    public static final String BUNDLE = "Bundle";

    public static final String COLLECTION = "Collection";

    /** The LID in a product label. */
    public static final String LOGICAL_ID = "logical_identifier";

    /** The VID in a product label. */
    public static final String PRODUCT_VERSION = "version_id";

    /** The product_class in a product label. */
    public static final String OBJECT_TYPE = "product_class";

    /** The associations in a product label. */
    public static final String REFERENCES = "references";

    /** The title of a product label. */
    public static final String TITLE = "title";

    /** Holds the XPaths to the expected metadata in a product label. */
    public static final HashMap<String, String> coreXpathsMap =
        new HashMap<String, String>();

    /** The Identification Area XPath in a product label. */
    public static final String IDENTIFICATION_AREA_XPATH =
        "//*[starts-with(name(),'Identification_Area')]";

    static {
        coreXpathsMap.put(LOGICAL_ID, IDENTIFICATION_AREA_XPATH + "/"
                + LOGICAL_ID);
        coreXpathsMap.put(PRODUCT_VERSION, IDENTIFICATION_AREA_XPATH + "/"
                + PRODUCT_VERSION);
        coreXpathsMap.put(OBJECT_TYPE, IDENTIFICATION_AREA_XPATH + "/"
                + OBJECT_TYPE);
        coreXpathsMap.put(TITLE, IDENTIFICATION_AREA_XPATH + "/" + TITLE);
        coreXpathsMap.put(REFERENCES,
                "//*[ends-with(name(),'Member_Entry')]"
                + " | //*[ends-with(name(),'Reference_Entry')] | "
                + "//Reference_Entry_Generic");
    }

    /** The file reference XPath in an Inventory file. */
    public static final String FILE_SPEC_XPATH = "file_specification_name";

    /** The MD5 checksum XPath in an Inventory file. */
    public static final String CHECKSUM_XPATH = "md5_checksum";

    /** The LID-VID or LID XPath for an association. */
    public static final String IDENTITY_REFERENCE_XPATH =
        "lidvid_reference | lid_reference";

    /** XPath that will indicate if a collection is primary. */
    public static final String IS_PRIMARY_COLLECTION_XPATH =
      "//*[starts-with(name(), 'Identification_Area')]/is_primary_collection";

    /** XPath to the field number of the column containing the file
     * reference.
     */
    public static final String FILE_SPEC_FIELD_NUM_XPATH = "//*[starts-with("
        + "name(),'Table_Record')]/"
        + "Table_Field_File_Specification_Name/field_number";

    /**
     * XPath to the field number of the column containing the LID-VID or LID
     * reference.
     */
    public static final String LIDVID_FIELD_NUM_XPATH =
        "//Table_Record_Inventory_LIDVID/Table_Field_LIDVID/field_number | "
        + "//Table_Record_Inventory_LID/Table_Field_LID/field_number";

    /** XPath to the external table file of a collection. */
    public static final String DATA_FILE_XPATH = "//File_Area/File/file_name";
}
