// Copyright 2009, by the California Institute of Technology.
// ALL RIGHTS RESERVED. United States Government sponsorship acknowledged.
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
package gov.nasa.pds.citool.ingestor;

/**
 * Class that holds constants used in ingest.
 *
 * @author hyunlee
 *
 */
public class Constants {
   
    public static final String LID_PREFIX = "urn:nasa:pds:";
    
    public static final String TARGET_PROD = "Product_Target_PDS3";
    
    public static final String MISSION_PROD = "Product_Mission_PDS3";
    
    public static final String INST_PROD = "Product_Instrument_PDS3";
    
    public static final String INSTHOST_PROD = "Product_Instrument_Host_PDS3";
    
    public static final String DS_PROD = "Product_Data_Set_PDS3";
    
    public static final String GUEST_PROD = "Product_PDS_Guest";
    
    public static final String AFFIL_PROD = "Product_PDS_Affiliate";
    
    // or Collection_Volume_Set_PDS3 ????
    public static final String VOLUME_PROD = "Collection_Volume_PDS3";
    
    public static final String FILE_PROD = "Product_File_Repository";
    
    public static final String RESOURCE_PROD = "Product_Resource";
    
    public static final String MISSION_OBJ = "MISSION";
    
    public static final String DATASET_OBJ = "DATA_SET";
    
    public static final String INST_OBJ = "INSTRUMENT";
    
    public static final String INSTHOST_OBJ = "INSTRUMENT_HOST";
    
    public static final String TARGET_OBJ = "TARGET";
    
    public static final String RESOURCE_OBJ = "RESOURCE";
    
    public static final String VOLUME_OBJ = "VOLUME";
    
    public static final String HAS_MISSION = "has_investigation";
    
    public static final String HAS_INST = "has_instrument";
    
    public static final String HAS_INSTHOST = "has_instrument_host";
    
    public static final String HAS_DATASET = "has_data_set";
    
    public static final String HAS_TARGET = "has_target";
    
    public static final String HAS_RESOURCE = "has_resource";
    
    public static final String HAS_FILE = "has_file";
    
    public static final String PRODUCT_VERSION = "version_id";
    
}