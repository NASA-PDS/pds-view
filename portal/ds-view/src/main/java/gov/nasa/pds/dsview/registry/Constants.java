// Copyright 2012-2013, by the California Institute of Technology.
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
      
    public static final Map<String, String> bundlePds4ToRegistry = 
      new LinkedHashMap<String, String>();
      static {
    	  bundlePds4ToRegistry.put("IDENTIFIER", "identifier");
    	  bundlePds4ToRegistry.put("NAME", "title");
    	  bundlePds4ToRegistry.put("TYPE", "bundle_type");
    	  bundlePds4ToRegistry.put("DESCRIPTION", "bundle_description");
      }
    
	public static final Map<String, String> bundlePds4ToSearch = new LinkedHashMap<String, String>();
	static {
		bundlePds4ToSearch.put("IDENTIFIER", "identifier");
		bundlePds4ToSearch.put("NAME", "title");
		bundlePds4ToSearch.put("TYPE", "bundle_type");
		bundlePds4ToSearch.put("DESCRIPTION", "bundle_description");
		bundlePds4ToSearch.put("RESOURCES", "resource_ref");
	}
    	      
    public static final Map<String, String> bundleCitationPds4ToRegistry =
      new LinkedHashMap<String, String>();
      static {
          bundleCitationPds4ToRegistry.put("AUTHOR LIST", "citation_author_list");
	      bundleCitationPds4ToRegistry.put("EDITOR LIST", "citation_editor_list");
	      bundleCitationPds4ToRegistry.put("PUBLICATION YEAR", "citation_publication_year");
	      bundleCitationPds4ToRegistry.put("DESCRIPTION", "citation_desc");
      }
       
	public static final Map<String, String> bundleContextPds4ToRegistry = 
	  new LinkedHashMap<String, String>();
	  static {
		  bundleContextPds4ToRegistry.put("START DATE TIME", "observation_start_date_time");
    	  bundleContextPds4ToRegistry.put("STOP DATE_TIME", "observation_stop_date_time");
    	  bundleContextPds4ToRegistry.put("LOCAL MEAN SOLAR TIME", "observation_local_mean_solar_time");
    	  bundleContextPds4ToRegistry.put("LOCAL TRUE SOLAR TIME", "observation_local_true_solar_time");
    	  bundleContextPds4ToRegistry.put("SOLAR LONGITUDE", "observation_solar_longitude");
    	  bundleContextPds4ToRegistry.put("PRIMARY RESULT TYPE", "primary_result_type");
    	  bundleContextPds4ToRegistry.put("PRIMARY RESULT PURPOSE", "primary_result_purpose");
    	  bundleContextPds4ToRegistry.put("PRIMARY RESULT DATA REGIME", "primary_result_data_regime");
    	  bundleContextPds4ToRegistry.put("PRIMARY RESULT DESCRIPTION", "primary_result_description");
    	  bundleContextPds4ToRegistry.put("PRIMARY RESULT PROCESSING LEVEL", "primary_result_processing_level_id");
    	  bundleContextPds4ToRegistry.put("INVESTIGATION NAME", "investigation_name");
    	  //bundleContextPds4ToRegistry.put("INVESTIGATION NAME", "investigation_ref");
    	  bundleContextPds4ToRegistry.put("OBSERVING SYSTEM NAME", "observing_system_name");
    	  bundleContextPds4ToRegistry.put("OBSERVING SYSTEM COMPONENT NAME", "observing_system_component_name");
    	  bundleContextPds4ToRegistry.put("TARGET NAME", "target_name");
    	  //bundleContextPds4ToRegistry.put("TARGET NAME", "target_ref");
	  }
     
    public static final Map<String, String> collectionPds4ToRegistry = 
      new LinkedHashMap<String, String>();
      static {
    	  collectionPds4ToRegistry.put("IDENTIFIER", "identifier");
    	  collectionPds4ToRegistry.put("NAME", "title");
    	  collectionPds4ToRegistry.put("TYPE", "collection_type");    	  
      }
      
	public static final Map<String, String> collectionPds4ToSearch = new LinkedHashMap<String, String>();
	static {
		collectionPds4ToSearch.put("IDENTIFIER", "identifier");
		collectionPds4ToSearch.put("NAME", "title");
		collectionPds4ToSearch.put("TYPE", "collection_type");
		collectionPds4ToSearch.put("DESCRIPTION", "modification_description");
		collectionPds4ToSearch.put("RESOURCES",  "resource_ref");
	}
	
	public static final Map<String, String> targetPds4ToRegistry =
			new LinkedHashMap<String, String>();	
	static {
		targetPds4ToRegistry.put("IDENTIFIER", "identifier");
		targetPds4ToRegistry.put("NAME", "title");
		targetPds4ToRegistry.put("TYPE" , "target_type");
		targetPds4ToRegistry.put("DESCRIPTION", "target_description");
		targetPds4ToRegistry.put("REFERENCES", "external_reference_description");
	}
	  
	public static final Map<String, String> msnPds4ToRegistry = new LinkedHashMap<String, String>();
	static {
		msnPds4ToRegistry.put("IDENTIFIER", "identifier");
		msnPds4ToRegistry.put("NAME", "investigation_name");
		msnPds4ToRegistry.put("TYPE", "investigation_type");
		msnPds4ToRegistry.put("DESCRIPTION", "investigation_description");
		msnPds4ToRegistry.put("START DATE", "investigation_start_date");
		msnPds4ToRegistry.put("STOP DATE", "investigation_stop_date");
		msnPds4ToRegistry.put("REFERENCES", "external_reference_description");
	}

	public static final Map<String, String> instCtxPds4ToSearch = new LinkedHashMap<String, String>();
	static {
		instCtxPds4ToSearch.put("IDENTIFIER", "identifier");
		instCtxPds4ToSearch.put("NAME", "instrument_name");
		instCtxPds4ToSearch.put("TYPE", "instrument_type");
		instCtxPds4ToSearch.put("DESCRIPTION", "instrument_desc");
		instCtxPds4ToSearch.put("MODEL IDENTIFIER", "instrument_model_id");
		instCtxPds4ToSearch.put("NAIF INSTRUMENT IDENTIFIER", "instrument_naif_id");
		instCtxPds4ToSearch.put("SERIAL NUMBER", "instrument_serial_number");
		instCtxPds4ToSearch.put("REFERENCES", "external_reference_description");
	}
	
	public static final Map<String, String> instHostCtxPds4ToSearch = new LinkedHashMap<String, String>();
	static {
		instHostCtxPds4ToSearch.put("IDENTIFIER", "identifier");
		instHostCtxPds4ToSearch.put("NAME", "instrument_host_name");
		instHostCtxPds4ToSearch.put("VERSION IDENTIFIER", "instrument_host_version_id");
		instHostCtxPds4ToSearch.put("TYPE", "instrument_host_type");
		instHostCtxPds4ToSearch.put("DESCRIPTION", "instrument_host_desc");
		instHostCtxPds4ToSearch.put("NAIF INSTRUMENT IDENTIFIER", "instrument_host_naif_id");
		instHostCtxPds4ToSearch.put("SERIAL NUMBER", "instrument_host_serial_number");
		instHostCtxPds4ToSearch.put("REFERENCES", "external_reference_description");
	}
	
	public static final Map<String, String> observationalPds4ToSearch = new LinkedHashMap<String, String>();
    static {
        observationalPds4ToSearch.put("IDENTIFIER", "identifier");
        observationalPds4ToSearch.put("NAME", "title");
        observationalPds4ToSearch.put("TYPE", "data_class");
        observationalPds4ToSearch.put("FILE NAME", "file_name");
    }
}
