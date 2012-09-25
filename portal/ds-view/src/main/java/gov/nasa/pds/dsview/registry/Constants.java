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
package gov.nasa.pds.dsview.registry;

import java.util.Map;
import java.util.LinkedHashMap;
/**
 * Class that holds constants used in ds-view.
 *
 * @author hyunlee
 *
 */
public class Constants {
	
	// map for target object from pds3 label to the registry slot key
	public static final Map<String, String> targetPds3ToRegistry =
      new LinkedHashMap<String, String>();	
	  static {
		  targetPds3ToRegistry.put("TARGET_NAME", "target_name");
		  targetPds3ToRegistry.put("PRIMARY_BODY_NAME", "target_primary_body_name");
		  //targetPds3ToRegistry.put("ORBIT_DIRECTION", "orbit_direction");
		  //targetPds3ToRegistry.put("ROTATION_DIRECTION", "rotation_direction");
		  targetPds3ToRegistry.put("TARGET_TYPE" , "target_type");
		  targetPds3ToRegistry.put("TARGET_DESCRIPTION", "target_description");
		  targetPds3ToRegistry.put("REFERENCE_DESCRIPTION", "external_reference_description");
		  targetPds3ToRegistry.put("RESOURCE_LINK", "resource_link");
	  }

	public static final Map<String, String> msnPds3ToRegistry =
      new LinkedHashMap<String, String>();
      static {
    	  msnPds3ToRegistry.put("MISSION_NAME", "mission_name");
    	  msnPds3ToRegistry.put("MISSION_ALIAS", "alternate_id");
    	  msnPds3ToRegistry.put("MISSION_START_DATE", "mission_start_date");
    	  msnPds3ToRegistry.put("MISSION_STOP_DATE", "mission_stop_date");
    	  msnPds3ToRegistry.put("MISSION_DESCRIPTION", "mission_description");
    	  msnPds3ToRegistry.put("MISSION_OBJECTIVES_SUMMARY", "mission_objectives_summary");
    	  msnPds3ToRegistry.put("REFERENCE_DESCRIPTION", "external_reference_description");
      }
          
    public static final Map<String, String> dsPds3ToRegistry = 
      new LinkedHashMap<String, String>();     
      static {
    	  dsPds3ToRegistry.put("DATA_SET_NAME", "data_set_name");
    	  dsPds3ToRegistry.put("DATA_SET_ID", "data_set_id");
    	  dsPds3ToRegistry.put("NSSDC_DATA_SET_ID", "data_set_nssdc_collection_id");
    	  dsPds3ToRegistry.put("DATA_SET_TERSE_DESCRIPTION", "data_set_terse_description");
    	  dsPds3ToRegistry.put("DATASET_DESCRIPTION", "data_set_description");
    	  dsPds3ToRegistry.put("DATA_SET_RELEASE_DATE", "data_set_release_date");
    	  dsPds3ToRegistry.put("RESOURCE_LINK", "resource_link");
    	  //dsPds3ToRegistry.put("DATA_OBJECT_TYPE", "data_object_type");
    	  dsPds3ToRegistry.put("START_TIME", "data_set_start_date_time");
    	  dsPds3ToRegistry.put("STOP_TIME", "data_set_stop_date_time");
    	  dsPds3ToRegistry.put("MISSION_NAME", "mission_name");
    	  dsPds3ToRegistry.put("MISSION_START_DATE", "mission_start_date");
    	  dsPds3ToRegistry.put("MISSION_STOP_DATE", "mission_stop_date");
    	  dsPds3ToRegistry.put("TARGET_NAME", "target_name");
    	  dsPds3ToRegistry.put("TARGET_TYPE", "target_type");
    	  dsPds3ToRegistry.put("INSTRUMENT_HOST_ID", "instrument_host_id");
    	  dsPds3ToRegistry.put("INSTRUMENT_NAME", "instrument_name");
    	  dsPds3ToRegistry.put("INSTRUMENT_ID", "instrument_id");
    	  dsPds3ToRegistry.put("INSTRUMENT_TYPE", "instrument_type");
    	  dsPds3ToRegistry.put("NODE_NAME", "node_name");
    	  dsPds3ToRegistry.put("ARCHIVE_STATUS", "data_set_archive_status");
    	  dsPds3ToRegistry.put("CONFIDENCE_LEVEL_NOTE", "data_set_confidence_level_note");
    	  dsPds3ToRegistry.put("CITATION_DESCRIPTION", "data_set_citation_text");
    	  dsPds3ToRegistry.put("ABSTRACT_TEXT", "data_set_abstract_description");
    	  dsPds3ToRegistry.put("PRODUCER_FULL_NAME", "data_set_producer_full_name"); // node_to_data_archivist reference????? person_name
    	  dsPds3ToRegistry.put("TELEPHONE_NUMBER", "person_telephone_number"); // node_to_data_archivist   person_telephone_number
    	  dsPds3ToRegistry.put("RESOURCES", "resources");
      }
      
    public static final Map<String, String> instPds3ToRegistry = 
      new LinkedHashMap<String, String>();
      static {
    	  instPds3ToRegistry.put("INSTRUMENT_ID", "instrument_id");
    	  instPds3ToRegistry.put("INSTRUMENT_NAME", "instrument_name");   
    	  instPds3ToRegistry.put("INSTRUMENT_TYPE", "instrument_type");
    	  instPds3ToRegistry.put("INSTRUMENT_HOST_ID", "instrument_host_id");
    	  instPds3ToRegistry.put("INSTRUMENT_DESC", "instrument_description");
    	  instPds3ToRegistry.put("REFERENCE_DESCRIPTION", "external_reference_description");
      }
      
    public static final Map<String, String> instHostPds3ToRegistry = 
      new LinkedHashMap<String, String>();
      static {
        	instHostPds3ToRegistry.put("INSTRUMENT_HOST_ID", "instrument_host_id");
        	instHostPds3ToRegistry.put("INSTRUMENT_HOST_NAME", "instrument_host_name");
        	instHostPds3ToRegistry.put("INSTRUMENT_HOST_TYPE", "instrument_host_type");
        	instHostPds3ToRegistry.put("INSTRUMENT_HOST_DESC", "instrument_host_description");
        	instHostPds3ToRegistry.put("REFERENCE_DESCRIPTION", "external_reference_description");
      }
        
    public static final Map<String, String> nodePds3ToRegistry =
      new LinkedHashMap<String, String>();
      static {
        	nodePds3ToRegistry.put("NODE_ID", "alternate_id");  
        	nodePds3ToRegistry.put("NODE_NAME", "node_name");
        	nodePds3ToRegistry.put("INSTITUTION_NAME", "node_institution_name");
        	nodePds3ToRegistry.put("DESCRIPTION", "node_description");
      }

    public static final Map<String, String> personPds3ToRegistry =
      new LinkedHashMap<String, String>();
      static {
    	  personPds3ToRegistry.put("PDS_USER_ID", "pds_user_id");
    	  personPds3ToRegistry.put("FULL_NAME", "person_name");
    	  personPds3ToRegistry.put("TELEPHONE_NUMBER", "person_telephone_number");
    	  personPds3ToRegistry.put("INSTITUTION_NAME", "person_institution_name");
    	  personPds3ToRegistry.put("NODE_NAME", "person_team_name");
    	  personPds3ToRegistry.put("ELECTRONIC_MAIL_ID", "person_electronic_mail_address");
      }
          
    public static final Map<String, String> volumePds3ToRegistry =
      new LinkedHashMap<String, String>();
      static {
    	  volumePds3ToRegistry.put("VOLUME_ID", "volume_id");
    	  volumePds3ToRegistry.put("VOLUME_SET_ID" , "volume_set_id");
    	  volumePds3ToRegistry.put("VOLUME_NAME", "volume_name");
    	  volumePds3ToRegistry.put("VOLUME_VER_ID", "volume_version_id");
    	  volumePds3ToRegistry.put("PUBLISHED_DATE", "volume_publication_date");
   		  volumePds3ToRegistry.put("VOLUME_DESC", "volume_description");
   		  //volumePds3ToRegistry.put("LABEL_REV_NOTE", "label_rev_note"
      }
      
}
