package gov.nasa.arc.pds.lace.server.parse;

import gov.nasa.arc.pds.lace.shared.Container;
import gov.nasa.arc.pds.lace.shared.InsertOption;
import gov.nasa.arc.pds.lace.shared.InsertionPoint;
import gov.nasa.arc.pds.lace.shared.LabelElement;
import gov.nasa.arc.pds.lace.shared.LabelItem;
import gov.nasa.arc.pds.lace.shared.LabelItemType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Implements a facility that reads Schematron rules and augments
 * types in a model with the validation information for those
 * types.
 */
public class ValidationAnalyzer {

	private static final Map<String, String[]> VALID_VALUES_FOR_ELEMENT = new HashMap<String, String[]>();
	static {
		addValidValues("pds:Agency/pds:name", new String[] {
			    "European_Space_Agency",
			    "National_Aeronautics_and_Space_Administration",
			});
			addValidValues("pds:Array/pds:encoding_type", new String[] {
			    "Binary",
			    "Character",
			});
			addValidValues("pds:Array/pds:axis_index_order", new String[] {"Last_Index_Fastest"});
			addValidValues("pds:Array_2D/pds:axes", new String[] {"2"});
			addValidValues("pds:Array_2D/pds:encoding_type", new String[] {
			    "Binary",
			    "Character",
			});
			addValidValues("pds:Array_2D/pds:axis_index_order", new String[] {"Last_Index_Fastest"});
			addValidValues("pds:Array_2D_Image/pds:axes", new String[] {"2"});
			addValidValues("pds:Array_2D_Image/pds:encoding_type", new String[] {
			    "Binary",
			    "Character",
			});
			addValidValues("pds:Array_2D_Image/pds:axis_index_order", new String[] {"Last_Index_Fastest"});
			addValidValues("pds:Array_2D_Image/pds:Axis_Array[1]/pds:axis_name", new String[] {
			    "Line",
			    "Sample",
			});
			addValidValues("pds:Array_2D_Image/pds:Axis_Array[2]/pds:axis_name", new String[] {
			    "Line",
			    "Sample",
			});
			addValidValues("pds:Array_2D_Map/pds:axes", new String[] {"2"});
			addValidValues("pds:Array_2D_Map/pds:encoding_type", new String[] {
			    "Binary",
			    "Character",
			});
			addValidValues("pds:Array_2D_Map/pds:axis_index_order", new String[] {"Last_Index_Fastest"});
			addValidValues("pds:Array_2D_Spectrum/pds:axes", new String[] {"2"});
			addValidValues("pds:Array_2D_Spectrum/pds:encoding_type", new String[] {
			    "Binary",
			    "Character",
			});
			addValidValues("pds:Array_2D_Spectrum/pds:axis_index_order", new String[] {"Last_Index_Fastest"});
			addValidValues("pds:Array_3D/pds:axes", new String[] {"3"});
			addValidValues("pds:Array_3D/pds:encoding_type", new String[] {
			    "Binary",
			    "Character",
			});
			addValidValues("pds:Array_3D/pds:axis_index_order", new String[] {"Last_Index_Fastest"});
			addValidValues("pds:Array_3D_Image/pds:axes", new String[] {"3"});
			addValidValues("pds:Array_3D_Image/pds:encoding_type", new String[] {
			    "Binary",
			    "Character",
			});
			addValidValues("pds:Array_3D_Image/pds:axis_index_order", new String[] {"Last_Index_Fastest"});
			addValidValues("pds:Array_3D_Movie/pds:axes", new String[] {"3"});
			addValidValues("pds:Array_3D_Movie/pds:encoding_type", new String[] {
			    "Binary",
			    "Character",
			});
			addValidValues("pds:Array_3D_Movie/pds:axis_index_order", new String[] {"Last_Index_Fastest"});
			addValidValues("pds:Array_3D_Spectrum/pds:axes", new String[] {"3"});
			addValidValues("pds:Array_3D_Spectrum/pds:encoding_type", new String[] {
			    "Binary",
			    "Character",
			});
			addValidValues("pds:Array_3D_Spectrum/pds:axis_index_order", new String[] {"Last_Index_Fastest"});
			addValidValues("pds:Bundle/pds:bundle_type", new String[] {
			    "Archive",
			    "Supplemental",
			});
			addValidValues("pds:Bundle_Member_Entry/pds:reference_type", new String[] {
			    "bundle_has_browse_collection",
			    "bundle_has_calibration_collection",
			    "bundle_has_context_collection",
			    "bundle_has_data_collection",
			    "bundle_has_document_collection",
			    "bundle_has_geometry_collection",
			    "bundle_has_member_collection",
			    "bundle_has_schema_collection",
			    "bundle_has_spice_kernel_collection",
			});
			addValidValues("pds:Bundle_Member_Entry/pds:member_status", new String[] {
			    "Primary",
			    "Secondary",
			});
			addValidValues("pds:Checksum_Manifest/pds:external_standard_id", new String[] {"MD5Deep Version 4.2"});
			addValidValues("pds:Checksum_Manifest/pds:encoding_type", new String[] {"Character"});
			addValidValues("pds:Checksum_Manifest/pds:record_delimiter", new String[] {"carriage_return line_feed"});
			addValidValues("pds:Collection/pds:collection_type", new String[] {
			    "Browse",
			    "Calibration",
			    "Context",
			    "Data",
			    "Document",
			    "Geometry",
			    "Miscellaneous",
			    "SPICE",
			    "Schema",
			});
			addValidValues("pds:DD_Association/pds:reference_type", new String[] {
			    "attribute_of",
			    "component_of",
			    "extension_of",
			    "restriction_of",
			    "subclass_of",
			});
			addValidValues("pds:DD_Attribute/pds:steward_id", new String[] {
			    "atm",
			    "geo",
			    "img",
			    "naif",
			    "ops",
			    "pds",
			    "ppi",
			    "rings",
			    "rs",
			    "sbn",
			});
			addValidValues("pds:DD_Attribute_Full/pds:attribute_concept", new String[] {
			    "ADDRESS",
			    "ANGLE",
			    "ATTRIBUTE",
			    "BIT",
			    "CHECKSUM",
			    "COLLECTION",
			    "CONSTANT",
			    "COSINE",
			    "COUNT",
			    "DELIMITER",
			    "DESCRIPTION",
			    "DEVIATION",
			    "DIRECTION",
			    "DISTANCE",
			    "DOI",
			    "DURATION",
			    "FACTOR",
			    "FLAG",
			    "FORMAT",
			    "GROUP",
			    "HOME",
			    "ID",
			    "LATITUDE",
			    "LENGTH",
			    "LIST",
			    "LOCATION",
			    "LOGICAL",
			    "LONGITUDE",
			    "MASK",
			    "MAXIMUM",
			    "MEAN",
			    "MEDIAN",
			    "MINIMUM",
			    "NAME",
			    "NOTE",
			    "NUMBER",
			    "OFFSET",
			    "ORDER",
			    "PARALLEL",
			    "PASSWORD",
			    "PATH",
			    "PATTERN",
			    "PIXEL",
			    "QUATERNION",
			    "RADIUS",
			    "RATIO",
			    "REFERENCE",
			    "RESOLUTION",
			    "ROLE",
			    "ROTATION",
			    "SCALE",
			    "SEQUENCE",
			    "SET",
			    "SIZE",
			    "STATUS",
			    "SUMMARY",
			    "SYNTAX",
			    "TEMPERATURE",
			    "TEXT",
			    "TITLE",
			    "TYPE",
			    "UNIT",
			    "UNKNOWN",
			    "VALUE",
			    "VECTOR",
			});
			addValidValues("pds:DD_Attribute_Full/pds:type", new String[] {
			    "PDS3",
			    "PDS4",
			});
			addValidValues("pds:DD_Attribute_Full/pds:registration_authority_id", new String[] {"0001_NASA_PDS_1"});
			addValidValues("pds:DD_Attribute_Full/pds:steward_id", new String[] {
			    "atm",
			    "geo",
			    "img",
			    "naif",
			    "ops",
			    "pds",
			    "ppi",
			    "rings",
			    "rs",
			    "sbn",
			});
			addValidValues("pds:DD_Class/pds:steward_id", new String[] {
			    "atm",
			    "geo",
			    "img",
			    "naif",
			    "ops",
			    "pds",
			    "ppi",
			    "rings",
			    "rs",
			    "sbn",
			});
			addValidValues("pds:DD_Class_Full/pds:steward_id", new String[] {
			    "atm",
			    "geo",
			    "img",
			    "naif",
			    "ops",
			    "pds",
			    "ppi",
			    "rings",
			    "rs",
			    "sbn",
			});
			addValidValues("pds:DD_Value_Domain/pds:unit_of_measure_type", new String[] {
			    "UnitOfMeasure_AmountOfSubstance",
			    "UnitOfMeasure_Angle",
			    "UnitOfMeasure_AngularVelocity",
			    "UnitOfMeasure_Area",
			    "UnitOfMeasure_Frequency",
			    "UnitOfMeasure_Length",
			    "UnitOfMeasure_MapScale",
			    "UnitOfMeasure_Mass",
			    "UnitOfMeasure_Misc",
			    "UnitOfMeasure_None",
			    "UnitOfMeasure_OpticalPathLength",
			    "UnitOfMeasure_Pressure",
			    "UnitOfMeasure_Radiance",
			    "UnitOfMeasure_Rates",
			    "UnitOfMeasure_SolidAngle",
			    "UnitOfMeasure_Storage",
			    "UnitOfMeasure_Temperature",
			    "UnitOfMeasure_Time",
			    "UnitOfMeasure_Velocity",
			    "UnitOfMeasure_Voltage",
			    "UnitOfMeasure_Volume",
			});
			addValidValues("pds:DD_Value_Domain/pds:value_data_type", new String[] {
			    "ASCII_AnyURI",
			    "ASCII_Boolean",
			    "ASCII_DOI",
			    "ASCII_Date_DOY",
			    "ASCII_Date_Time_UTC",
			    "ASCII_Date_Time_YMD",
			    "ASCII_Date_YMD",
			    "ASCII_File_Specification_Name",
			    "ASCII_Integer",
			    "ASCII_LID",
			    "ASCII_LIDVID",
			    "ASCII_MD5_Checksum",
			    "ASCII_NonNegative_Integer",
			    "ASCII_Numeric_Base16",
			    "ASCII_Numeric_Base2",
			    "ASCII_Real",
			    "ASCII_Short_String_Collapsed",
			    "ASCII_Short_String_Preserved",
			    "ASCII_Text_Preserved",
			    "ASCII_Time",
			    "ASCII_VID",
			});
			addValidValues("pds:DD_Value_Domain_Full/pds:conceptual_domain", new String[] {
			    "BOOLEAN",
			    "INTEGER",
			    "NAME",
			    "NUMERIC",
			    "REAL",
			    "SHORT_STRING",
			    "TEXT",
			    "TIME",
			    "TYPE",
			    "UNKNOWN",
			});
			addValidValues("pds:DD_Value_Domain_Full/pds:unit_of_measure_type", new String[] {
			    "UnitOfMeasure_AmountOfSubstance",
			    "UnitOfMeasure_Angle",
			    "UnitOfMeasure_AngularVelocity",
			    "UnitOfMeasure_Area",
			    "UnitOfMeasure_Frequency",
			    "UnitOfMeasure_Length",
			    "UnitOfMeasure_MapScale",
			    "UnitOfMeasure_Mass",
			    "UnitOfMeasure_Misc",
			    "UnitOfMeasure_None",
			    "UnitOfMeasure_OpticalPathLength",
			    "UnitOfMeasure_Pressure",
			    "UnitOfMeasure_Radiance",
			    "UnitOfMeasure_Rates",
			    "UnitOfMeasure_SolidAngle",
			    "UnitOfMeasure_Storage",
			    "UnitOfMeasure_Temperature",
			    "UnitOfMeasure_Time",
			    "UnitOfMeasure_Velocity",
			    "UnitOfMeasure_Voltage",
			    "UnitOfMeasure_Volume",
			});
			addValidValues("pds:DD_Value_Domain_Full/pds:value_data_type", new String[] {
			    "ASCII_AnyURI",
			    "ASCII_Boolean",
			    "ASCII_DOI",
			    "ASCII_Date_DOY",
			    "ASCII_Date_Time_UTC",
			    "ASCII_Date_Time_YMD",
			    "ASCII_Date_YMD",
			    "ASCII_File_Specification_Name",
			    "ASCII_Integer",
			    "ASCII_LID",
			    "ASCII_LIDVID",
			    "ASCII_MD5_Checksum",
			    "ASCII_NonNegative_Integer",
			    "ASCII_Numeric_Base16",
			    "ASCII_Numeric_Base2",
			    "ASCII_Real",
			    "ASCII_Short_String_Collapsed",
			    "ASCII_Short_String_Preserved",
			    "ASCII_Text_Preserved",
			    "ASCII_Time",
			    "ASCII_VID",
			});
			addValidValues("pds:Data_Set_PDS3/pds:archive_status", new String[] {
			    "ARCHIVED",
			    "ARCHIVED_ACCUMULATING",
			    "IN_LIEN_RESOLUTION",
			    "IN_LIEN_RESOLUTION_ACCUMULATING",
			    "IN_PEER_REVIEW",
			    "IN_PEER_REVIEW_ACCUMULATING",
			    "IN_QUEUE",
			    "IN_QUEUE_ACCUMULATING",
			    "LOCALLY_ARCHIVED",
			    "LOCALLY_ARCHIVED_ACCUMULATING",
			    "PRE_PEER_REVIEW",
			    "PRE_PEER_REVIEW_ACCUMULATING",
			    "SAFED",
			    "SUPERSEDED",
			});
			addValidValues("pds:Display_2D_Image/pds:line_display_direction", new String[] {
			    "Down",
			    "Up",
			});
			addValidValues("pds:Display_2D_Image/pds:sample_display_direction", new String[] {"Right"});
			addValidValues("pds:Document_File/pds:external_standard_id", new String[] {
			    "ASCII",
			    "Encapsulated_Postscript",
			    "GIF",
			    "HTML",
			    "JPEG",
			    "LaTEX",
			    "Microsoft_Word",
			    "PDF",
			    "PDF-A",
			    "PNG",
			    "Postscript",
			    "Rich_Text",
			    "TIFF",
			    "UTF-8",
			});
			addValidValues("pds:Document_Format/pds:format_type", new String[] {
			    "multiple_file",
			    "single_file",
			});
			addValidValues("pds:Element_Array/pds:data_type", new String[] {
			    "ComplexLSB16",
			    "ComplexLSB8",
			    "ComplexMSB16",
			    "ComplexMSB8",
			    "IEEE754LSBDouble",
			    "IEEE754LSBSingle",
			    "IEEE754MSBDouble",
			    "IEEE754MSBSingle",
			    "SignedBitString",
			    "SignedByte",
			    "SignedLSB2",
			    "SignedLSB4",
			    "SignedLSB8",
			    "SignedMSB2",
			    "SignedMSB4",
			    "SignedMSB8",
			    "UnsignedBitString",
			    "UnsignedByte",
			    "UnsignedLSB2",
			    "UnsignedLSB4",
			    "UnsignedLSB8",
			    "UnsignedMSB2",
			    "UnsignedMSB4",
			    "UnsignedMSB8",
			});
			addValidValues("pds:Encoded_Binary/pds:external_standard_id", new String[] {"System"});
			addValidValues("pds:Encoded_Binary/pds:encoding_type", new String[] {"Binary"});
			addValidValues("pds:Encoded_Byte_Stream/pds:encoding_type", new String[] {"Binary"});
			addValidValues("pds:Encoded_Image/pds:external_standard_id", new String[] {
			    "GIF",
			    "JPEG",
			    "PDF",
			    "PDF-A",
			    "PNG",
			    "TIFF",
			});
			addValidValues("pds:Encoded_Image/pds:encoding_type", new String[] {"Binary"});
			addValidValues("pds:Facility/pds:type", new String[] {
			    "Laboratory",
			    "Observatory",
			});
			addValidValues("pds:Field_Binary/pds:data_type", new String[] {
			    "ASCII_AnyURI",
			    "ASCII_Boolean",
			    "ASCII_DOI",
			    "ASCII_Date",
			    "ASCII_Date_DOY",
			    "ASCII_Date_Time",
			    "ASCII_Date_Time_DOY",
			    "ASCII_Date_Time_UTC",
			    "ASCII_Date_Time_YMD",
			    "ASCII_Date_YMD",
			    "ASCII_Directory_Path_Name",
			    "ASCII_File_Name",
			    "ASCII_File_Specification_Name",
			    "ASCII_Integer",
			    "ASCII_LID",
			    "ASCII_LIDVID",
			    "ASCII_LIDVID_LID",
			    "ASCII_MD5_Checksum",
			    "ASCII_NonNegative_Integer",
			    "ASCII_Numeric_Base16",
			    "ASCII_Numeric_Base2",
			    "ASCII_Numeric_Base8",
			    "ASCII_Real",
			    "ASCII_String",
			    "ASCII_Time",
			    "ASCII_VID",
			    "ComplexLSB16",
			    "ComplexLSB8",
			    "ComplexMSB16",
			    "ComplexMSB8",
			    "IEEE754LSBDouble",
			    "IEEE754LSBSingle",
			    "IEEE754MSBDouble",
			    "IEEE754MSBSingle",
			    "SignedBitString",
			    "SignedByte",
			    "SignedLSB2",
			    "SignedLSB4",
			    "SignedLSB8",
			    "SignedMSB2",
			    "SignedMSB4",
			    "SignedMSB8",
			    "UTF8_String",
			    "UnsignedBitString",
			    "UnsignedByte",
			    "UnsignedLSB2",
			    "UnsignedLSB4",
			    "UnsignedLSB8",
			    "UnsignedMSB2",
			    "UnsignedMSB4",
			    "UnsignedMSB8",
			});
			addValidValues("pds:Field_Bit/pds:data_type", new String[] {
			    "SignedBitString",
			    "UnsignedBitString",
			});
			addValidValues("pds:Field_Character/pds:data_type", new String[] {
			    "ASCII_AnyURI",
			    "ASCII_Boolean",
			    "ASCII_DOI",
			    "ASCII_Date",
			    "ASCII_Date_DOY",
			    "ASCII_Date_Time",
			    "ASCII_Date_Time_DOY",
			    "ASCII_Date_Time_UTC",
			    "ASCII_Date_Time_YMD",
			    "ASCII_Date_YMD",
			    "ASCII_Directory_Path_Name",
			    "ASCII_File_Name",
			    "ASCII_File_Specification_Name",
			    "ASCII_Integer",
			    "ASCII_LID",
			    "ASCII_LIDVID",
			    "ASCII_LIDVID_LID",
			    "ASCII_MD5_Checksum",
			    "ASCII_NonNegative_Integer",
			    "ASCII_Numeric_Base16",
			    "ASCII_Numeric_Base2",
			    "ASCII_Numeric_Base8",
			    "ASCII_Real",
			    "ASCII_String",
			    "ASCII_Time",
			    "ASCII_VID",
			    "UTF8_String",
			});
			addValidValues("pds:Field_Delimited/pds:data_type", new String[] {
			    "ASCII_AnyURI",
			    "ASCII_Boolean",
			    "ASCII_DOI",
			    "ASCII_Date",
			    "ASCII_Date_DOY",
			    "ASCII_Date_Time",
			    "ASCII_Date_Time_DOY",
			    "ASCII_Date_Time_UTC",
			    "ASCII_Date_Time_YMD",
			    "ASCII_Date_YMD",
			    "ASCII_Directory_Path_Name",
			    "ASCII_File_Name",
			    "ASCII_File_Specification_Name",
			    "ASCII_Integer",
			    "ASCII_LID",
			    "ASCII_LIDVID",
			    "ASCII_LIDVID_LID",
			    "ASCII_MD5_Checksum",
			    "ASCII_NonNegative_Integer",
			    "ASCII_Numeric_Base16",
			    "ASCII_Numeric_Base2",
			    "ASCII_Numeric_Base8",
			    "ASCII_Real",
			    "ASCII_String",
			    "ASCII_Time",
			    "ASCII_VID",
			    "UTF8_String",
			});
			addValidValues("pds:Header/pds:external_standard_id", new String[] {
			    "ASCII",
			    "FITS",
			    "ISIS2",
			    "ISIS3",
			    "ISIS_History",
			    "ODL",
			    "PDS1",
			    "PDS2",
			    "PDS3",
			    "PDS_DSV",
			    "TIFF",
			    "VICAR",
			});
			addValidValues("pds:Header_Encoded/pds:external_standard_id", new String[] {"TIFF"});
			addValidValues("pds:Header_Encoded/pds:encoding_type", new String[] {"Binary"});
			addValidValues("pds:Identification_Area/pds:product_class", new String[] {
			    "Product_AIP",
			    "Product_Attribute_Definition",
			    "Product_Browse",
			    "Product_Bundle",
			    "Product_Collection",
			    "Product_Context",
			    "Product_DIP",
			    "Product_DIP_Deep_Archive",
			    "Product_Data_Set_PDS3",
			    "Product_Document",
			    "Product_File_Repository",
			    "Product_File_Text",
			    "Product_Instrument_Host_PDS3",
			    "Product_Instrument_PDS3",
			    "Product_Mission_PDS3",
			    "Product_Observational",
			    "Product_Proxy_PDS3",
			    "Product_SIP",
			    "Product_SPICE_Kernel",
			    "Product_Service",
			    "Product_Software",
			    "Product_Subscription_PDS3",
			    "Product_Target_PDS3",
			    "Product_Thumbnail",
			    "Product_Update",
			    "Product_Volume_PDS3",
			    "Product_Volume_Set_PDS3",
			    "Product_XML_Schema",
			    "Product_Zipped",
			});
			addValidValues("pds:Identification_Area/pds:information_model_version", new String[] {"0.3.0.0.a"});
			addValidValues("pds:Instrument/pds:type", new String[] {
			    "Accelerometer",
			    "Alpha_Particle_Detector",
			    "Alpha_Particle_Xray_Spectrometer",
			    "Altimeter",
			    "Anemometer",
			    "Atomic_Force_Microscope",
			    "Barometer",
			    "Biology_Experiments",
			    "Bolometer",
			    "Camera",
			    "Cosmic_Ray_Detector",
			    "Dust_Detector",
			    "Electrical_Probe",
			    "Energetic_Particle_Detector",
			    "Gamma_Ray_Detector",
			    "Gas_Analyzer",
			    "Grinding_And_Drilling_Tool",
			    "Imager",
			    "Imaging_Spectrometer",
			    "Inertial_Measurement_Unit",
			    "Infrared_Spectrometer",
			    "Laser_Induced_Breakdown_Spectrometer",
			    "Magnetometer",
			    "Mass_Spectrometer",
			    "Meteorology",
			    "Microwave_Spectrometer",
			    "Moessbauer_Spectrometer",
			    "Naked_Eye",
			    "Neutral_Particle_Detector",
			    "Neutron_Detector",
			    "Other",
			    "Photometer",
			    "Plasma_Analyzer",
			    "Plasma_Detector",
			    "Plasma_Wave_Spectrometer",
			    "Polarimeter",
			    "RADAR",
			    "Radio_Science",
			    "Radio_Spectrometer",
			    "Radio_Telescope",
			    "Radiometer",
			    "Reflectometer",
			    "Spectrograph_Imager",
			    "Spectrometer",
			    "Thermal_And_Electrical_Conductivity_Probe",
			    "Thermal_Imager",
			    "Thermal_Probe",
			    "Thermometer",
			    "Ultraviolet_Spectrometer",
			    "Wet_Chemistry_Laboratory",
			    "X-ray_Defraction_Spectrometer",
			    "X-ray_Detector",
			    "X-ray_Fluorescence",
			    "X-ray_Fluorescence_Spectrometer",
			});
			addValidValues("pds:Instrument_Host/pds:type", new String[] {
			    "Earth_Based",
			    "Rover",
			    "Spacecraft",
			});
			addValidValues("pds:Inventory/pds:encoding_type", new String[] {"Character"});
			addValidValues("pds:Inventory/pds:reference_type", new String[] {"inventory_has_member_product"});
			addValidValues("pds:Inventory/pds:record_delimiter", new String[] {"carriage_return line_feed"});
			addValidValues("pds:Inventory/pds:external_standard_id", new String[] {"PDS_DSV V1.0"});
			addValidValues("pds:Inventory/pds:field_delimiter", new String[] {
			    "comma",
			    "horizontal_tab",
			    "semicolon",
			    "vertical_bar",
			});
			addValidValues("pds:Investigation/pds:type", new String[] {
			    "Individual_Investigation",
			    "Mission",
			    "Observing_Campaign",
			    "Other_Investigation",
			});
			addValidValues("pds:Investigation_Area/pds:type", new String[] {
			    "Individual_Investigation",
			    "Mission",
			    "Observing_Campaign",
			    "Other_Investigation",
			});
			addValidValues("pds:Investigation_Area/pds:Internal_Reference/every", new String[] {
			    "every $ref in (pds:reference_type) satisfies $data_to_investigation",
			    "collection_to_investigation",
			    "bundle_to_investigation",
			});
			addValidValues("pds:Node/pds:name", new String[] {
			    "Engineering",
			    "European Space Agency",
			    "Geosciences",
			    "Imaging",
			    "Management",
			    "Navigation_Ancillary_Information_Facility",
			    "Planetary_Atmospheres",
			    "Planetary_Plasma_Interactions",
			    "Planetary_Rings",
			    "Radio_Science",
			    "Small_Bodies",
			});
			addValidValues("pds:Observing_System_Component/pds:observing_system_component_type", new String[] {
			    "Analyst",
			    "Artificial_Illumination",
			    "Ground-based_Laboratory",
			    "Ground-based_Observatory",
			    "Ground-based_Telescope",
			    "Instrument",
			    "Literature_Search",
			    "PDS_Archived_Data",
			    "Spacecraft",
			});
			addValidValues("pds:Observing_System_Component/pds:Internal_Reference/every", new String[] {
			    "every $ref in (pds:reference_type) satisfies $is_instrument",
			    "is_instrument_host",
			});
			addValidValues("pds:PDS_Affiliate/pds:affiliation_type", new String[] {
			    "Affiliate",
			    "Data_Provider",
			    "Manager",
			    "Technical_Staff",
			});
			addValidValues("pds:PDS_Affiliate/pds:team_name", new String[] {
			    "Engineering",
			    "Geosciences",
			    "Headquarters",
			    "Imaging",
			    "Management",
			    "National_Space_Science_Data_Center",
			    "Navigation_Ancillary_Information_Facility",
			    "Planetary_Atmospheres",
			    "Planetary_Plasma_Interactions",
			    "Planetary_Rings",
			    "Radio_Science",
			    "Small_Bodies",
			});
			addValidValues("pds:Primary_Result_Summary/pds:data_regime", new String[] {
			    "Dust",
			    "Electric_Field",
			    "Electrons",
			    "Far_Infrared",
			    "Gamma_Ray",
			    "Infrared",
			    "Ions",
			    "Magnetic_Field",
			    "Microwave",
			    "Millimeter",
			    "Near_Infrared",
			    "Particles",
			    "Radio",
			    "Sub-Millimeter",
			    "Ultraviolet",
			    "Visible",
			    "X-Ray",
			});
			addValidValues("pds:Primary_Result_Summary/pds:type", new String[] {
			    "Altimetry",
			    "Astrometry",
			    "Count",
			    "E/B-Field_Vectors",
			    "Gravity_Model",
			    "Image",
			    "Magnetometry",
			    "Map",
			    "Null_Result",
			    "Occultation",
			    "Photometry",
			    "Polarimetry",
			    "Radiometry",
			    "Shape_Model",
			    "Spectrum",
			});
			addValidValues("pds:Primary_Result_Summary/pds:purpose", new String[] {
			    "Calibration",
			    "Checkout",
			    "Engineering",
			    "Navigation",
			    "Science",
			});
			addValidValues("pds:Primary_Result_Summary/pds:processing_level_id", new String[] {
			    "Calibrated",
			    "Derived",
			    "Raw",
			    "Reduced",
			});
			addValidValues("pds:Product_AIP/pds:Information_Package_Component/pds:Internal_Reference/every", new String[] {
			    "every $ref in (pds:reference_type) satisfies $package_to_collection",
			    "package_to_bundle",
			    "package_to_product",
			});
			addValidValues("pds:Product_Bundle/pds:Reference_List/pds:Internal_Reference/every", new String[] {
			    "every $ref in (pds:reference_type) satisfies $bundle_to_errata",
			    "bundle_to_document",
			});
			addValidValues("pds:Product_Collection/pds:Reference_List/pds:Internal_Reference/every", new String[] {
			    "every $ref in (pds:reference_type) satisfies $collection_to_resource",
			    "collection_to_associate",
			    "collection_to_calibration",
			    "collection_to_geometry",
			    "collection_to_spice_kernel",
			    "collection_curated_by_node",
			    "collection_to_document",
			    "collection_to_browse",
			    "collection_to_context",
			    "collection_to_data",
			    "collection_to_document",
			    "collection_to_schema",
			    "collection_to_errata",
			    "collection_to_bundle",
			});
			addValidValues("pds:Product_Context/pds:Reference_List/pds:Internal_Reference/every", new String[] {
			    "every $ref in (pds:reference_type) satisfies $context_to_resource",
			    "context_to_associate",
			    "context_to_document",
			    "context_to_collection",
			    "context_to_bundle",
			    "instrument_host_to_investigation",
			    "instrument_host_to_document",
			    "instrument_host_to_target",
			    "instrument_to_instrument_host",
			    "instrument_to_document",
			    "investigation_to_target",
			    "investigation_to_document",
			    "node_to_personnel",
			    "node_to_agency",
			    "node_to_manager",
			    "node_to_operator",
			    "node_to_data_archivist",
			});
			addValidValues("pds:Product_DIP/pds:Information_Package_Component/pds:Internal_Reference/every", new String[] {
			    "every $ref in (pds:reference_type) satisfies $package_to_collection",
			    "package_to_bundle",
			    "package_to_product",
			});
			addValidValues("pds:Product_DIP_Deep_Archive/pds:Information_Package_Component/pds:Internal_Reference/every", new String[] {
			    "every $ref in (pds:reference_type) satisfies $package_to_collection",
			    "package_to_bundle",
			    "package_to_product",
			});
			addValidValues("pds:Product_Document/pds:Reference_List/pds:Internal_Reference/every", new String[] {
			    "every $ref in (pds:reference_type) satisfies $document_to_associate",
			    "document_to_investigation",
			    "document_to_instrument_host",
			    "document_to_instrument",
			    "document_to_target",
			});
			addValidValues("pds:Product_Observational/pds:Reference_List/pds:Internal_Reference/every", new String[] {
			    "every $ref in (pds:reference_type) satisfies $data_to_resource",
			    "data_to_calibration_document",
			    "data_to_calibration_product",
			    "data_to_raw_product",
			    "data_to_calibrated_product",
			    "data_to_geometry",
			    "data_to_spice_kernel",
			    "data_to_thumbnail",
			    "data_to_document",
			    "data_curated_by_node",
			    "data_to_browse",
			});
			addValidValues("pds:Product_Operational/pds:Reference_List/pds:Internal_Reference/every", new String[] {
			    "every $ref in (pds:reference_type) satisfies $operational_to_resource",
			    "operational_to_associate",
			    "operational_to_document",
			});
			addValidValues("pds:Product_SIP/pds:Information_Package_Component/pds:Internal_Reference/every", new String[] {
			    "every $ref in (pds:reference_type) satisfies $package_to_collection",
			    "package_to_bundle",
			    "package_to_product",
			});
			addValidValues("pds:Product_Zipped/pds:Internal_Reference/every", new String[] {"every $ref in (pds:reference_type) satisfies $zip_to_package"});
			addValidValues("pds:Quaternion/pds:type", new String[] {
			    "SPICE",
			    "Spacecraft_Telemetry",
			});
			addValidValues("pds:Quaternion_Component/pds:data_type", new String[] {"ASCII_Real"});
			addValidValues("pds:Resource/pds:type", new String[] {
			    "Information.Agency",
			    "Information.Instrument",
			    "Information.Instrument_Host",
			    "Information.Investigation",
			    "Information.Node",
			    "Information.Person",
			    "Information.Resource",
			    "Information.Science_Portal",
			    "Information.Target",
			    "System.Browse",
			    "System.Directory_Listing",
			    "System.Registry_Query",
			    "System.Search",
			    "System.Transform",
			    "System.Transport",
			});
			addValidValues("pds:SPICE_Kernel/pds:encoding_type", new String[] {
			    "Binary",
			    "Character",
			});
			addValidValues("pds:SPICE_Kernel/pds:kernel_type", new String[] {
			    "CK",
			    "DBK",
			    "DSK",
			    "EK",
			    "FK",
			    "IK",
			    "LSK",
			    "MK",
			    "PCK",
			    "SCLK",
			    "SPK",
			});
			addValidValues("pds:SPICE_Kernel/pds:external_standard_id", new String[] {"SPICE"});
			addValidValues("pds:Service_Description/pds:encoding_type", new String[] {"Character"});
			addValidValues("pds:Service_Description/pds:external_standard_id", new String[] {
			    "WADL",
			    "WSDL",
			});
			addValidValues("pds:Stream_Text/pds:encoding_type", new String[] {"Character"});
			addValidValues("pds:Stream_Text/pds:record_delimiter", new String[] {"carriage_return line_feed"});
			addValidValues("pds:Table_Binary/pds:encoding_type", new String[] {"Binary"});
			addValidValues("pds:Table_Character/pds:encoding_type", new String[] {"Character"});
			addValidValues("pds:Table_Character/pds:record_delimiter", new String[] {"carriage_return line_feed"});
			addValidValues("pds:Table_Delimited/pds:encoding_type", new String[] {"Character"});
			addValidValues("pds:Table_Delimited/pds:record_delimiter", new String[] {"carriage_return line_feed"});
			addValidValues("pds:Table_Delimited/pds:external_standard_id", new String[] {"PDS_DSV V1.0"});
			addValidValues("pds:Table_Delimited/pds:field_delimiter", new String[] {
			    "comma",
			    "horizontal_tab",
			    "semicolon",
			    "vertical_bar",
			});
			addValidValues("pds:Target/pds:type", new String[] {
			    "Asteroid",
			    "Comet",
			    "Dust",
			    "Dwarf_Planet",
			    "Galaxy",
			    "Globular_Cluster",
			    "Meteorite",
			    "Meteoroid",
			    "Meteoroid_Stream",
			    "Nebula",
			    "Open_Cluster",
			    "Planet",
			    "Planetary_Nebula",
			    "Planetary_System",
			    "Plasma_Cloud",
			    "Ring",
			    "Satellite",
			    "Star",
			    "Star_Cluster",
			    "Sun",
			    "Terrestrial_Sample",
			    "Trans-Neptunian_Object",
			});
			addValidValues("pds:Target_Identification/pds:Internal_Reference/every", new String[] {
			    "every $ref in (pds:reference_type) satisfies $data_to_target",
			    "collection_to_target",
			    "bundle_to_target",
			});
			addValidValues("pds:Telescope/pds:coordinate_source", new String[] {
			    "Aerial survey - North American (1983) datum",
			    "Astronomical",
			    "Doppler determined - WGS 72 datum",
			    "Geodetic - Adindan datum",
			    "Geodetic - Australian datum",
			    "Geodetic - Campo Inchauspe (Argentina) datum",
			    "Geodetic - Cape (South Africa) datum",
			    "Geodetic - Corregio Alegre (Brazil) datum",
			    "Geodetic - European 1979 datum",
			    "Geodetic - European datum",
			    "Geodetic - GRS 80 datum",
			    "Geodetic - Hermannskogel datum",
			    "Geodetic - Indian datum",
			    "Geodetic - La Canoa (Venezuela) datum",
			    "Geodetic - New Zealand datum",
			    "Geodetic - North American (1927) datum",
			    "Geodetic - Old Hawaiian datum",
			    "Geodetic - Ordnance Survey of Great Britain (1936) datum",
			    "Geodetic - Ordnance Survey of Great Britain (SN) 1980 datum",
			    "Geodetic - Potsdam datum",
			    "Geodetic - Puerto Rican (1940) datum",
			    "Geodetic - South American datum",
			    "Geodetic - Tokyo datum",
			    "Geodetic - WGS 84 datum",
			    "Geodetic - datum unknown",
			    "Satellite determined - datum unknown",
			    "Unknown",
			});
			addValidValues("pds:Terminological_Entry/pds:language", new String[] {"English"});
			addValidValues("pds:Transfer_Manifest/pds:encoding_type", new String[] {"Character"});
			addValidValues("pds:Transfer_Manifest/pds:record_delimiter", new String[] {"carriage_return line_feed"});
			addValidValues("pds:Uniformly_Sampled/pds:sampling_parameter_scale", new String[] {
			    "Exponential",
			    "Linear",
			    "Logarithmic",
			});
			addValidValues("pds:Update_Entry/pds:Internal_Reference/every", new String[] {
			    "every $ref in (pds:reference_type) satisfies $data_to_update",
			    "collection_to_update",
			    "bundle_to_update",
			});
			addValidValues("pds:Vector/pds:type", new String[] {
			    "Position",
			    "Velocity",
			});
			addValidValues("pds:Vector/pds:data_type", new String[] {"ASCII_Real"});
			addValidValues("pds:Volume_PDS3/pds:archive_status", new String[] {
			    "ARCHIVED",
			    "ARCHIVED_ACCUMULATING",
			    "IN_LIEN_RESOLUTION",
			    "IN_LIEN_RESOLUTION_ACCUMULATING",
			    "IN_PEER_REVIEW",
			    "IN_PEER_REVIEW_ACCUMULATING",
			    "IN_QUEUE",
			    "IN_QUEUE_ACCUMULATING",
			    "LOCALLY_ARCHIVED",
			    "LOCALLY_ARCHIVED_ACCUMULATING",
			    "PRE_PEER_REVIEW",
			    "PRE_PEER_REVIEW_ACCUMULATING",
			    "SAFED",
			    "SUPERSEDED",
			});
			addValidValues("pds:XML_Schema/pds:encoding_type", new String[] {"Character"});
			addValidValues("pds:XML_Schema/pds:external_standard_id", new String[] {
			    "Schematron ISO/IEC 19757-3:2006",
			    "XML Schema Version 1.1",
			});
			addValidValues("pds:Zip/pds:container_type", new String[] {
			    "GZIP",
			    "LZIP",
			    "TAR",
			    "ZIP",
			});
	}

	private static void addValidValues(String context, String[] values) {
		VALID_VALUES_FOR_ELEMENT.put(context.replaceAll("pds:", ""), values);
	}

	/**
	 * Adds the set of valid values to the type of any descendant of a container.
	 *
	 * @param root the root container
	 */
	public void addValidValues(Container root) {
		Set<String> seenTypes = new HashSet<String>();
		addValidValues(root.getType(), "", seenTypes);
	}

	private void addValidValues(LabelItem item, String parentName, Set<String> seenTypes) {
		if (item instanceof LabelElement) {
			addValidValues(((LabelElement) item).getType(), parentName, seenTypes);
		} else if (item instanceof InsertionPoint) {
			addValidValues((InsertionPoint) item, parentName, seenTypes);
		}
	}

	private void addValidValues(InsertionPoint item, String parentName, Set<String> seenTypes) {
		for (InsertOption alternative : item.getAlternatives()) {
			for (LabelItemType type : alternative.getTypes()) {
				addValidValues(type, parentName, seenTypes);
			}
		}	
	}

	private void addValidValues(LabelItemType type, String parentName, Set<String> seenTypes) {
		String name = type.getElementName();
		String context = parentName + "/" + name;

		if (seenTypes.contains(context)) {
			return;
		}
		seenTypes.add(context);

		if (type.getValidValues() == null && VALID_VALUES_FOR_ELEMENT.containsKey(context)) {
			type.setValidValues(VALID_VALUES_FOR_ELEMENT.get(context));
		}

		if (type.getInitialContents() != null) {
			for (LabelItem child : type.getInitialContents()) {
				addValidValues(child, name, seenTypes);
			}
		}
	}

	//	Map<String[], List<String>> validValuesForContext = new HashMap<String[], List<String>>();
	//
	//	public ValidationAnalyzer(URI rulesURI) throws FileNotFoundException {
	//		DOMImplementationRegistry registry = null;
	//		try {
	//			registry = DOMImplementationRegistry.newInstance();
	//		} catch (Exception e) {
	//			e.printStackTrace();
	//			throw new IllegalArgumentException("Cannot instantiate DOMImplementationRegistry: " + e.getMessage());
	//		}
	//
	//		DOMImplementationLS loadSave = (DOMImplementationLS) registry.getDOMImplementation("LS");
	//		LSParser parser = loadSave.createLSParser(DOMImplementationLS.MODE_SYNCHRONOUS, null);
	//		LSInput input = loadSave.createLSInput();
	//		input.setByteStream(new FileInputStream(new File(rulesURI)));
	//		Document rulesDoc = parser.parse(input);
	//
	//		loadRules(rulesDoc);
	//	}
	//
	//	private void loadRules(Document doc) {
	//		DOMSource source = new DOMSource(doc);
	//		DocumentNamespaceResolver context = new DocumentNamespaceResolver(doc);
	//		XPathFactory factory = XPathFactory.newInstance();
	//
	//		XPathExpression rulePath = getXPath("/sch:schema/sch:pattern/sch:rule", factory, context);
	//		try {
	//			NodeList rules = (NodeList) rulePath.evaluate(source, XPathConstants.NODESET);
	//			findAssertions(rules, factory, context);
	//		} catch (XPathExpressionException ex) {
	//			ex.printStackTrace();
	//			throw new IllegalArgumentException("Error evaluating an XPath: " + ex);
	//		}
	//	}
	//
	//	private void findAssertions(NodeList nodes, XPathFactory factory, NamespaceContext context) {
	//		for (int i=0; i < nodes.getLength(); ++i) {
	//			findAssertions(nodes.item(i), factory, context);
	//		}
	//	}
	//
	//	private void findAssertions(Node node, XPathFactory factory, NamespaceContext context) {
	//		String contextStr = node.getAttributes().getNamedItem("context").getTextContent();
	//		String[] ruleContext = contextStr.replaceAll("pds:", "").split("/");
	//
	//	}
	//
	//	private XPathExpression getXPath(String pathStr, XPathFactory factory, NamespaceContext context) {
	//		XPath path = factory.newXPath();
	//		path.setNamespaceContext(context);
	//		try {
	//			return path.compile(pathStr);
	//		} catch(XPathExpressionException ex) {
	//			throw new IllegalArgumentException("Error parsing an XPath: " + ex);
	//		}
	//	}
	//
	//	private static class DocumentNamespaceResolver implements NamespaceContext {
	//	    // the delegate
	//	    private Document sourceDocument;
	//
	//	    /**
	//	     * Creates a new instance of the resolver which uses a specfiied
	//	     * document to find namespaces and their prefixes.
	//	     *
	//	     * @param document
	//	     *            the XML document in which to look for namespaces and prefixes
	//	     */
	//	    public DocumentNamespaceResolver(Document document) {
	//	        sourceDocument = document;
	//	    }
	//
	//	    @Override
	//		public String getNamespaceURI(String prefix) {
	//	        if (prefix.equals(XMLConstants.DEFAULT_NS_PREFIX)) {
	//	            return sourceDocument.lookupNamespaceURI(null);
	//	        } else {
	//	            return sourceDocument.lookupNamespaceURI(prefix);
	//	        }
	//	    }
	//
	//	    @Override
	//		public String getPrefix(String namespaceURI) {
	//	        return sourceDocument.lookupPrefix(namespaceURI);
	//	    }
	//
	//	    @Override
	//		public Iterator<String> getPrefixes(String namespaceURI) {
	//	        // not implemented yet
	//	        return null;
	//	    }
	//
	//	}

}
