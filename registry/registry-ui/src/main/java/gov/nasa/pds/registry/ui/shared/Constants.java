// Copyright 2009-2014, by the California Institute of Technology.
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
package gov.nasa.pds.registry.ui.shared;

public abstract class Constants {
	
	public static final int MAX_STR_SIZE = 128;
	
	public static final String[] status = {
		"Submitted", "Approved", "Deprecated"
	};
	
	public static final String[] eventTypes = {
		"Created", "Approved", "Deleted", "Updated",
		"Deprecated", "Versioned", "Undeprecated", "Replicated"
	};

	public static final String[] assocTypes = {
		"file_ref",
		"member_ref",
		"urn:registry:AssociationType:HasMember"
	};
	
	public static final String[] objectTypes = {
		"Ingest_LDD", // modified build4a
		"Product", 
		"Product_AIP", // added build3a
		"Product_Ancillary", // added build5b+
		"Product_Attribute_Definition",
		"Product_Browse",
		"Product_Bundle",
		"Product_Class_Definition", // added build3a
		"Product_Collection",
		"Product_Context",
		"Product_DIP", // added build3a
		"Product_DIP_Deep_Archive", // added build3a
		"Product_Data_Set_PDS3",
		"Product_Document",
		"Product_File_Repository",
		"Product_File_Text",
		"Product_Instrument_Host_PDS3",
		"Product_Instrument_PDS3",
		"Product_Mission_PDS3",
		"Product_Native", // added build4b
		"Product_Observational",
		"Product_Proxy_PDS3",
		"Product_SIP", // added build3a
		"Product_SIP_Deep_Archive", // added build5b+
		"Product_SPICE_Kernel",
		"Product_Service",
		"Product_Software",
		"Product_Subscription_PDS3",
		"Product_Target_PDS3",
		"Product_Thumbnail",
		"Product_Update",
		"Product_Volume_PDS3", // added build3a
		"Product_Volume_Set_PDS3", // added build3a
		"Product_XML_Schema",
		"Product_Zipped"
	};
}

