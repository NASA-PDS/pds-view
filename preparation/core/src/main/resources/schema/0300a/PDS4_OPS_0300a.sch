<?xml version="1.0" encoding="UTF-8"?>
  <!-- PDS4 Schematron for PDS4_0.3.0.0.a  Fri Sep 14 18:01:16 PDT 2012 -->
  <!-- Generated from the PDS4 Information Model V0.3.0.0.a -->
  <!-- *** This PDS4 schematron file is a preliminary deliverable. *** -->
  <!-- *** It is being made available for review and testing. *** -->
<sch:schema xmlns:sch="http://purl.oclc.org/dsdl/schematron" queryBinding="xslt2">

  <sch:title>Sample Schematron using XPath 2.0</sch:title>

  <sch:ns uri="http://pds.nasa.gov/pds4/pds/v03" prefix="pds"/>

		   <!-- ================================================ -->
		   <!-- NOTE:  There are two types of schematron rules.  -->
		   <!--        One type includes rules written for       -->
		   <!--        specific situations. The other type are   -->
		   <!--        generated to validate enumerated value    -->
		   <!--        lists. These two types of rules have been -->
		   <!--        merged together in the rules below.       -->
		   <!-- ================================================ -->
  <sch:pattern>
    <sch:rule context="pds:Agency">
      <sch:assert test="pds:name = ('European_Space_Agency', 'National_Aeronautics_and_Space_Administration')">
        The attribute pds:name must be equal to one of the following values 'European_Space_Agency', 'National_Aeronautics_and_Space_Administration'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Array">
      <sch:assert test="pds:encoding_type = ('Binary', 'Character')">
        The attribute pds:encoding_type must be equal to one of the following values 'Binary', 'Character'.</sch:assert>
      <sch:assert test="pds:axis_index_order = ('Last_Index_Fastest')">
        The attribute pds:axis_index_order must be equal to the value 'Last_Index_Fastest'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Array_2D">
      <sch:assert test="pds:axes = ('2')">
        The attribute pds:axes must be equal to the value '2'.</sch:assert>
      <sch:assert test="pds:encoding_type = ('Binary', 'Character')">
        The attribute pds:encoding_type must be equal to one of the following values 'Binary', 'Character'.</sch:assert>
      <sch:assert test="pds:axis_index_order = ('Last_Index_Fastest')">
        The attribute pds:axis_index_order must be equal to the value 'Last_Index_Fastest'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Array_2D_Image">
      <sch:assert test="pds:axes = ('2')">
        The attribute pds:axes must be equal to the value '2'.</sch:assert>
      <sch:assert test="pds:encoding_type = ('Binary', 'Character')">
        The attribute pds:encoding_type must be equal to one of the following values 'Binary', 'Character'.</sch:assert>
      <sch:assert test="pds:axis_index_order = ('Last_Index_Fastest')">
        The attribute pds:axis_index_order must be equal to the value 'Last_Index_Fastest'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Array_2D_Image/pds:Axis_Array[1]">
      <sch:assert test="pds:axis_name = ('Line', 'Sample')">
        The name of the first axis of an Array_2d_Image must be set to either Line or Sample.</sch:assert>
      <sch:assert test="pds:sequence_number eq '1'">
        The sequence number of the first axis of an Array_2d_Image must be set to 1.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Array_2D_Image/pds:Axis_Array[2]">
      <sch:assert test="pds:axis_name = ('Line', 'Sample')">
        The name of the second axis of an Array_2d_Image must be set to either Line or Sample.</sch:assert>
      <sch:assert test="pds:sequence_number eq '2'">
        The sequence number of the second axis of an Array_2d_Image must be set to 2.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Array_2D_Map">
      <sch:assert test="pds:axes = ('2')">
        The attribute pds:axes must be equal to the value '2'.</sch:assert>
      <sch:assert test="pds:encoding_type = ('Binary', 'Character')">
        The attribute pds:encoding_type must be equal to one of the following values 'Binary', 'Character'.</sch:assert>
      <sch:assert test="pds:axis_index_order = ('Last_Index_Fastest')">
        The attribute pds:axis_index_order must be equal to the value 'Last_Index_Fastest'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Array_2D_Spectrum">
      <sch:assert test="pds:axes = ('2')">
        The attribute pds:axes must be equal to the value '2'.</sch:assert>
      <sch:assert test="pds:encoding_type = ('Binary', 'Character')">
        The attribute pds:encoding_type must be equal to one of the following values 'Binary', 'Character'.</sch:assert>
      <sch:assert test="pds:axis_index_order = ('Last_Index_Fastest')">
        The attribute pds:axis_index_order must be equal to the value 'Last_Index_Fastest'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Array_3D">
      <sch:assert test="pds:axes = ('3')">
        The attribute pds:axes must be equal to the value '3'.</sch:assert>
      <sch:assert test="pds:encoding_type = ('Binary', 'Character')">
        The attribute pds:encoding_type must be equal to one of the following values 'Binary', 'Character'.</sch:assert>
      <sch:assert test="pds:axis_index_order = ('Last_Index_Fastest')">
        The attribute pds:axis_index_order must be equal to the value 'Last_Index_Fastest'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Array_3D_Image">
      <sch:assert test="pds:axes = ('3')">
        The attribute pds:axes must be equal to the value '3'.</sch:assert>
      <sch:assert test="pds:encoding_type = ('Binary', 'Character')">
        The attribute pds:encoding_type must be equal to one of the following values 'Binary', 'Character'.</sch:assert>
      <sch:assert test="pds:axis_index_order = ('Last_Index_Fastest')">
        The attribute pds:axis_index_order must be equal to the value 'Last_Index_Fastest'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Array_3D_Movie">
      <sch:assert test="pds:axes = ('3')">
        The attribute pds:axes must be equal to the value '3'.</sch:assert>
      <sch:assert test="pds:encoding_type = ('Binary', 'Character')">
        The attribute pds:encoding_type must be equal to one of the following values 'Binary', 'Character'.</sch:assert>
      <sch:assert test="pds:axis_index_order = ('Last_Index_Fastest')">
        The attribute pds:axis_index_order must be equal to the value 'Last_Index_Fastest'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Array_3D_Spectrum">
      <sch:assert test="pds:axes = ('3')">
        The attribute pds:axes must be equal to the value '3'.</sch:assert>
      <sch:assert test="pds:encoding_type = ('Binary', 'Character')">
        The attribute pds:encoding_type must be equal to one of the following values 'Binary', 'Character'.</sch:assert>
      <sch:assert test="pds:axis_index_order = ('Last_Index_Fastest')">
        The attribute pds:axis_index_order must be equal to the value 'Last_Index_Fastest'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Bundle">
      <sch:assert test="pds:bundle_type = ('Archive', 'Supplemental')">
        The attribute pds:bundle_type must be equal to one of the following values 'Archive', 'Supplemental'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Bundle_Member_Entry">
      <sch:assert test="pds:reference_type = ('bundle_has_browse_collection', 'bundle_has_calibration_collection', 'bundle_has_context_collection', 'bundle_has_data_collection', 'bundle_has_document_collection', 'bundle_has_geometry_collection', 'bundle_has_member_collection', 'bundle_has_schema_collection', 'bundle_has_spice_kernel_collection')">
        The attribute pds:reference_type must be equal to one of the following values 'bundle_has_browse_collection', 'bundle_has_calibration_collection', 'bundle_has_context_collection', 'bundle_has_data_collection', 'bundle_has_document_collection', 'bundle_has_geometry_collection', 'bundle_has_member_collection', 'bundle_has_schema_collection', 'bundle_has_spice_kernel_collection'.</sch:assert>
      <sch:assert test="pds:member_status = ('Primary', 'Secondary')">
        The attribute pds:member_status must be equal to one of the following values 'Primary', 'Secondary'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Checksum_Manifest">
      <sch:assert test="pds:external_standard_id = ('MD5Deep Version 4.2')">
        The attribute pds:external_standard_id must be equal to the value 'MD5Deep Version 4.2'.</sch:assert>
      <sch:assert test="pds:encoding_type = ('Character')">
        The attribute pds:encoding_type must be equal to the value 'Character'.</sch:assert>
      <sch:assert test="pds:record_delimiter = ('carriage_return line_feed')">
        The attribute pds:record_delimiter must be equal to the value 'carriage_return line_feed'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Collection">
      <sch:assert test="pds:collection_type = ('Browse', 'Calibration', 'Context', 'Data', 'Document', 'Geometry', 'Miscellaneous', 'SPICE', 'Schema')">
        The attribute pds:collection_type must be equal to one of the following values 'Browse', 'Calibration', 'Context', 'Data', 'Document', 'Geometry', 'Miscellaneous', 'SPICE', 'Schema'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:DD_Association">
      <sch:assert test="pds:reference_type = ('attribute_of', 'component_of', 'extension_of', 'restriction_of', 'subclass_of')">
        The attribute pds:reference_type must be equal to one of the following values 'attribute_of', 'component_of', 'extension_of', 'restriction_of', 'subclass_of'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:DD_Attribute">
      <sch:assert test="pds:steward_id = ('atm', 'geo', 'img', 'naif', 'ops', 'pds', 'ppi', 'rings', 'rs', 'sbn')">
        The attribute pds:steward_id must be equal to one of the following values 'atm', 'geo', 'img', 'naif', 'ops', 'pds', 'ppi', 'rings', 'rs', 'sbn'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:DD_Attribute_Full">
      <sch:assert test="pds:attribute_concept = ('ADDRESS', 'ANGLE', 'ATTRIBUTE', 'BIT', 'CHECKSUM', 'COLLECTION', 'CONSTANT', 'COSINE', 'COUNT', 'DELIMITER', 'DESCRIPTION', 'DEVIATION', 'DIRECTION', 'DISTANCE', 'DOI', 'DURATION', 'FACTOR', 'FLAG', 'FORMAT', 'GROUP', 'HOME', 'ID', 'LATITUDE', 'LENGTH', 'LIST', 'LOCATION', 'LOGICAL', 'LONGITUDE', 'MASK', 'MAXIMUM', 'MEAN', 'MEDIAN', 'MINIMUM', 'NAME', 'NOTE', 'NUMBER', 'OFFSET', 'ORDER', 'PARALLEL', 'PASSWORD', 'PATH', 'PATTERN', 'PIXEL', 'QUATERNION', 'RADIUS', 'RATIO', 'REFERENCE', 'RESOLUTION', 'ROLE', 'ROTATION', 'SCALE', 'SEQUENCE', 'SET', 'SIZE', 'STATUS', 'SUMMARY', 'SYNTAX', 'TEMPERATURE', 'TEXT', 'TITLE', 'TYPE', 'UNIT', 'UNKNOWN', 'VALUE', 'VECTOR')">
        The attribute pds:attribute_concept must be equal to one of the following values 'ADDRESS', 'ANGLE', 'ATTRIBUTE', 'BIT', 'CHECKSUM', 'COLLECTION', 'CONSTANT', 'COSINE', 'COUNT', 'DELIMITER', 'DESCRIPTION', 'DEVIATION', 'DIRECTION', 'DISTANCE', 'DOI', 'DURATION', 'FACTOR', 'FLAG', 'FORMAT', 'GROUP', 'HOME', 'ID', 'LATITUDE', 'LENGTH', 'LIST', 'LOCATION', 'LOGICAL', 'LONGITUDE', 'MASK', 'MAXIMUM', 'MEAN', 'MEDIAN', 'MINIMUM', 'NAME', 'NOTE', 'NUMBER', 'OFFSET', 'ORDER', 'PARALLEL', 'PASSWORD', 'PATH', 'PATTERN', 'PIXEL', 'QUATERNION', 'RADIUS', 'RATIO', 'REFERENCE', 'RESOLUTION', 'ROLE', 'ROTATION', 'SCALE', 'SEQUENCE', 'SET', 'SIZE', 'STATUS', 'SUMMARY', 'SYNTAX', 'TEMPERATURE', 'TEXT', 'TITLE', 'TYPE', 'UNIT', 'UNKNOWN', 'VALUE', 'VECTOR'.</sch:assert>
      <sch:assert test="pds:type = ('PDS3', 'PDS4')">
        The attribute pds:type must be equal to one of the following values 'PDS3', 'PDS4'.</sch:assert>
      <sch:assert test="pds:registration_authority_id = ('0001_NASA_PDS_1')">
        The attribute pds:registration_authority_id must be equal to the value '0001_NASA_PDS_1'.</sch:assert>
      <sch:assert test="pds:steward_id = ('atm', 'geo', 'img', 'naif', 'ops', 'pds', 'ppi', 'rings', 'rs', 'sbn')">
        The attribute pds:steward_id must be equal to one of the following values 'atm', 'geo', 'img', 'naif', 'ops', 'pds', 'ppi', 'rings', 'rs', 'sbn'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:DD_Class">
      <sch:assert test="pds:steward_id = ('atm', 'geo', 'img', 'naif', 'ops', 'pds', 'ppi', 'rings', 'rs', 'sbn')">
        The attribute pds:steward_id must be equal to one of the following values 'atm', 'geo', 'img', 'naif', 'ops', 'pds', 'ppi', 'rings', 'rs', 'sbn'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:DD_Class_Full">
      <sch:assert test="pds:steward_id = ('atm', 'geo', 'img', 'naif', 'ops', 'pds', 'ppi', 'rings', 'rs', 'sbn')">
        The attribute pds:steward_id must be equal to one of the following values 'atm', 'geo', 'img', 'naif', 'ops', 'pds', 'ppi', 'rings', 'rs', 'sbn'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:DD_Value_Domain">
      <sch:assert test="pds:unit_of_measure_type = ('UnitOfMeasure_AmountOfSubstance', 'UnitOfMeasure_Angle', 'UnitOfMeasure_AngularVelocity', 'UnitOfMeasure_Area', 'UnitOfMeasure_Frequency', 'UnitOfMeasure_Length', 'UnitOfMeasure_MapScale', 'UnitOfMeasure_Mass', 'UnitOfMeasure_Misc', 'UnitOfMeasure_None', 'UnitOfMeasure_OpticalPathLength', 'UnitOfMeasure_Pressure', 'UnitOfMeasure_Radiance', 'UnitOfMeasure_Rates', 'UnitOfMeasure_SolidAngle', 'UnitOfMeasure_Storage', 'UnitOfMeasure_Temperature', 'UnitOfMeasure_Time', 'UnitOfMeasure_Velocity', 'UnitOfMeasure_Voltage', 'UnitOfMeasure_Volume')">
        The attribute pds:unit_of_measure_type must be equal to one of the following values 'UnitOfMeasure_AmountOfSubstance', 'UnitOfMeasure_Angle', 'UnitOfMeasure_AngularVelocity', 'UnitOfMeasure_Area', 'UnitOfMeasure_Frequency', 'UnitOfMeasure_Length', 'UnitOfMeasure_MapScale', 'UnitOfMeasure_Mass', 'UnitOfMeasure_Misc', 'UnitOfMeasure_None', 'UnitOfMeasure_OpticalPathLength', 'UnitOfMeasure_Pressure', 'UnitOfMeasure_Radiance', 'UnitOfMeasure_Rates', 'UnitOfMeasure_SolidAngle', 'UnitOfMeasure_Storage', 'UnitOfMeasure_Temperature', 'UnitOfMeasure_Time', 'UnitOfMeasure_Velocity', 'UnitOfMeasure_Voltage', 'UnitOfMeasure_Volume'.</sch:assert>
      <sch:assert test="pds:value_data_type = ('ASCII_AnyURI', 'ASCII_Boolean', 'ASCII_DOI', 'ASCII_Date_DOY', 'ASCII_Date_Time_UTC', 'ASCII_Date_Time_YMD', 'ASCII_Date_YMD', 'ASCII_File_Specification_Name', 'ASCII_Integer', 'ASCII_LID', 'ASCII_LIDVID', 'ASCII_MD5_Checksum', 'ASCII_NonNegative_Integer', 'ASCII_Numeric_Base16', 'ASCII_Numeric_Base2', 'ASCII_Real', 'ASCII_Short_String_Collapsed', 'ASCII_Short_String_Preserved', 'ASCII_Text_Preserved', 'ASCII_Time', 'ASCII_VID')">
        The attribute pds:value_data_type must be equal to one of the following values 'ASCII_AnyURI', 'ASCII_Boolean', 'ASCII_DOI', 'ASCII_Date_DOY', 'ASCII_Date_Time_UTC', 'ASCII_Date_Time_YMD', 'ASCII_Date_YMD', 'ASCII_File_Specification_Name', 'ASCII_Integer', 'ASCII_LID', 'ASCII_LIDVID', 'ASCII_MD5_Checksum', 'ASCII_NonNegative_Integer', 'ASCII_Numeric_Base16', 'ASCII_Numeric_Base2', 'ASCII_Real', 'ASCII_Short_String_Collapsed', 'ASCII_Short_String_Preserved', 'ASCII_Text_Preserved', 'ASCII_Time', 'ASCII_VID'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:DD_Value_Domain_Full">
      <sch:assert test="pds:conceptual_domain = ('BOOLEAN', 'INTEGER', 'NAME', 'NUMERIC', 'REAL', 'SHORT_STRING', 'TEXT', 'TIME', 'TYPE', 'UNKNOWN')">
        The attribute pds:conceptual_domain must be equal to one of the following values 'BOOLEAN', 'INTEGER', 'NAME', 'NUMERIC', 'REAL', 'SHORT_STRING', 'TEXT', 'TIME', 'TYPE', 'UNKNOWN'.</sch:assert>
      <sch:assert test="pds:unit_of_measure_type = ('UnitOfMeasure_AmountOfSubstance', 'UnitOfMeasure_Angle', 'UnitOfMeasure_AngularVelocity', 'UnitOfMeasure_Area', 'UnitOfMeasure_Frequency', 'UnitOfMeasure_Length', 'UnitOfMeasure_MapScale', 'UnitOfMeasure_Mass', 'UnitOfMeasure_Misc', 'UnitOfMeasure_None', 'UnitOfMeasure_OpticalPathLength', 'UnitOfMeasure_Pressure', 'UnitOfMeasure_Radiance', 'UnitOfMeasure_Rates', 'UnitOfMeasure_SolidAngle', 'UnitOfMeasure_Storage', 'UnitOfMeasure_Temperature', 'UnitOfMeasure_Time', 'UnitOfMeasure_Velocity', 'UnitOfMeasure_Voltage', 'UnitOfMeasure_Volume')">
        The attribute pds:unit_of_measure_type must be equal to one of the following values 'UnitOfMeasure_AmountOfSubstance', 'UnitOfMeasure_Angle', 'UnitOfMeasure_AngularVelocity', 'UnitOfMeasure_Area', 'UnitOfMeasure_Frequency', 'UnitOfMeasure_Length', 'UnitOfMeasure_MapScale', 'UnitOfMeasure_Mass', 'UnitOfMeasure_Misc', 'UnitOfMeasure_None', 'UnitOfMeasure_OpticalPathLength', 'UnitOfMeasure_Pressure', 'UnitOfMeasure_Radiance', 'UnitOfMeasure_Rates', 'UnitOfMeasure_SolidAngle', 'UnitOfMeasure_Storage', 'UnitOfMeasure_Temperature', 'UnitOfMeasure_Time', 'UnitOfMeasure_Velocity', 'UnitOfMeasure_Voltage', 'UnitOfMeasure_Volume'.</sch:assert>
      <sch:assert test="pds:value_data_type = ('ASCII_AnyURI', 'ASCII_Boolean', 'ASCII_DOI', 'ASCII_Date_DOY', 'ASCII_Date_Time_UTC', 'ASCII_Date_Time_YMD', 'ASCII_Date_YMD', 'ASCII_File_Specification_Name', 'ASCII_Integer', 'ASCII_LID', 'ASCII_LIDVID', 'ASCII_MD5_Checksum', 'ASCII_NonNegative_Integer', 'ASCII_Numeric_Base16', 'ASCII_Numeric_Base2', 'ASCII_Real', 'ASCII_Short_String_Collapsed', 'ASCII_Short_String_Preserved', 'ASCII_Text_Preserved', 'ASCII_Time', 'ASCII_VID')">
        The attribute pds:value_data_type must be equal to one of the following values 'ASCII_AnyURI', 'ASCII_Boolean', 'ASCII_DOI', 'ASCII_Date_DOY', 'ASCII_Date_Time_UTC', 'ASCII_Date_Time_YMD', 'ASCII_Date_YMD', 'ASCII_File_Specification_Name', 'ASCII_Integer', 'ASCII_LID', 'ASCII_LIDVID', 'ASCII_MD5_Checksum', 'ASCII_NonNegative_Integer', 'ASCII_Numeric_Base16', 'ASCII_Numeric_Base2', 'ASCII_Real', 'ASCII_Short_String_Collapsed', 'ASCII_Short_String_Preserved', 'ASCII_Text_Preserved', 'ASCII_Time', 'ASCII_VID'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Data_Set_PDS3">
      <sch:assert test="pds:archive_status = ('ARCHIVED', 'ARCHIVED_ACCUMULATING', 'IN_LIEN_RESOLUTION', 'IN_LIEN_RESOLUTION_ACCUMULATING', 'IN_PEER_REVIEW', 'IN_PEER_REVIEW_ACCUMULATING', 'IN_QUEUE', 'IN_QUEUE_ACCUMULATING', 'LOCALLY_ARCHIVED', 'LOCALLY_ARCHIVED_ACCUMULATING', 'PRE_PEER_REVIEW', 'PRE_PEER_REVIEW_ACCUMULATING', 'SAFED', 'SUPERSEDED')">
        The attribute pds:archive_status must be equal to one of the following values 'ARCHIVED', 'ARCHIVED_ACCUMULATING', 'IN_LIEN_RESOLUTION', 'IN_LIEN_RESOLUTION_ACCUMULATING', 'IN_PEER_REVIEW', 'IN_PEER_REVIEW_ACCUMULATING', 'IN_QUEUE', 'IN_QUEUE_ACCUMULATING', 'LOCALLY_ARCHIVED', 'LOCALLY_ARCHIVED_ACCUMULATING', 'PRE_PEER_REVIEW', 'PRE_PEER_REVIEW_ACCUMULATING', 'SAFED', 'SUPERSEDED'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Display_2D_Image">
      <sch:assert test="pds:line_display_direction = ('Down', 'Up')">
        The attribute pds:line_display_direction must be equal to one of the following values 'Down', 'Up'.</sch:assert>
      <sch:assert test="pds:sample_display_direction = ('Right')">
        The attribute pds:sample_display_direction must be equal to the value 'Right'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Document_File">
      <sch:assert test="pds:external_standard_id = ('ASCII', 'Encapsulated_Postscript', 'GIF', 'HTML', 'JPEG', 'LaTEX', 'Microsoft_Word', 'PDF', 'PDF-A', 'PNG', 'Postscript', 'Rich_Text', 'TIFF', 'UTF-8')">
        The attribute pds:external_standard_id must be equal to one of the following values 'ASCII', 'Encapsulated_Postscript', 'GIF', 'HTML', 'JPEG', 'LaTEX', 'Microsoft_Word', 'PDF', 'PDF-A', 'PNG', 'Postscript', 'Rich_Text', 'TIFF', 'UTF-8'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Document_Format">
      <sch:assert test="pds:format_type = ('multiple_file', 'single_file')">
        The attribute pds:format_type must be equal to one of the following values 'multiple_file', 'single_file'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Element_Array">
      <sch:assert test="pds:data_type = ('ComplexLSB16', 'ComplexLSB8', 'ComplexMSB16', 'ComplexMSB8', 'IEEE754LSBDouble', 'IEEE754LSBSingle', 'IEEE754MSBDouble', 'IEEE754MSBSingle', 'SignedBitString', 'SignedByte', 'SignedLSB2', 'SignedLSB4', 'SignedLSB8', 'SignedMSB2', 'SignedMSB4', 'SignedMSB8', 'UnsignedBitString', 'UnsignedByte', 'UnsignedLSB2', 'UnsignedLSB4', 'UnsignedLSB8', 'UnsignedMSB2', 'UnsignedMSB4', 'UnsignedMSB8')">
        The attribute pds:data_type must be equal to one of the following values 'ComplexLSB16', 'ComplexLSB8', 'ComplexMSB16', 'ComplexMSB8', 'IEEE754LSBDouble', 'IEEE754LSBSingle', 'IEEE754MSBDouble', 'IEEE754MSBSingle', 'SignedBitString', 'SignedByte', 'SignedLSB2', 'SignedLSB4', 'SignedLSB8', 'SignedMSB2', 'SignedMSB4', 'SignedMSB8', 'UnsignedBitString', 'UnsignedByte', 'UnsignedLSB2', 'UnsignedLSB4', 'UnsignedLSB8', 'UnsignedMSB2', 'UnsignedMSB4', 'UnsignedMSB8'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Encoded_Binary">
      <sch:assert test="pds:external_standard_id = ('System')">
        The attribute pds:external_standard_id must be equal to the value 'System'.</sch:assert>
      <sch:assert test="pds:encoding_type = ('Binary')">
        The attribute pds:encoding_type must be equal to the value 'Binary'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Encoded_Byte_Stream">
      <sch:assert test="pds:encoding_type = ('Binary')">
        The attribute pds:encoding_type must be equal to the value 'Binary'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Encoded_Image">
      <sch:assert test="pds:external_standard_id = ('GIF', 'JPEG', 'PDF', 'PDF-A', 'PNG', 'TIFF')">
        The attribute pds:external_standard_id must be equal to one of the following values 'GIF', 'JPEG', 'PDF', 'PDF-A', 'PNG', 'TIFF'.</sch:assert>
      <sch:assert test="pds:encoding_type = ('Binary')">
        The attribute pds:encoding_type must be equal to the value 'Binary'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Facility">
      <sch:assert test="pds:type = ('Laboratory', 'Observatory')">
        The attribute pds:type must be equal to one of the following values 'Laboratory', 'Observatory'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Field_Binary">
      <sch:assert test="pds:data_type = ('ASCII_AnyURI', 'ASCII_Boolean', 'ASCII_DOI', 'ASCII_Date', 'ASCII_Date_DOY', 'ASCII_Date_Time', 'ASCII_Date_Time_DOY', 'ASCII_Date_Time_UTC', 'ASCII_Date_Time_YMD', 'ASCII_Date_YMD', 'ASCII_Directory_Path_Name', 'ASCII_File_Name', 'ASCII_File_Specification_Name', 'ASCII_Integer', 'ASCII_LID', 'ASCII_LIDVID', 'ASCII_LIDVID_LID', 'ASCII_MD5_Checksum', 'ASCII_NonNegative_Integer', 'ASCII_Numeric_Base16', 'ASCII_Numeric_Base2', 'ASCII_Numeric_Base8', 'ASCII_Real', 'ASCII_String', 'ASCII_Time', 'ASCII_VID', 'ComplexLSB16', 'ComplexLSB8', 'ComplexMSB16', 'ComplexMSB8', 'IEEE754LSBDouble', 'IEEE754LSBSingle', 'IEEE754MSBDouble', 'IEEE754MSBSingle', 'SignedBitString', 'SignedByte', 'SignedLSB2', 'SignedLSB4', 'SignedLSB8', 'SignedMSB2', 'SignedMSB4', 'SignedMSB8', 'UTF8_String', 'UnsignedBitString', 'UnsignedByte', 'UnsignedLSB2', 'UnsignedLSB4', 'UnsignedLSB8', 'UnsignedMSB2', 'UnsignedMSB4', 'UnsignedMSB8')">
        The attribute pds:data_type must be equal to one of the following values 'ASCII_AnyURI', 'ASCII_Boolean', 'ASCII_DOI', 'ASCII_Date', 'ASCII_Date_DOY', 'ASCII_Date_Time', 'ASCII_Date_Time_DOY', 'ASCII_Date_Time_UTC', 'ASCII_Date_Time_YMD', 'ASCII_Date_YMD', 'ASCII_Directory_Path_Name', 'ASCII_File_Name', 'ASCII_File_Specification_Name', 'ASCII_Integer', 'ASCII_LID', 'ASCII_LIDVID', 'ASCII_LIDVID_LID', 'ASCII_MD5_Checksum', 'ASCII_NonNegative_Integer', 'ASCII_Numeric_Base16', 'ASCII_Numeric_Base2', 'ASCII_Numeric_Base8', 'ASCII_Real', 'ASCII_String', 'ASCII_Time', 'ASCII_VID', 'ComplexLSB16', 'ComplexLSB8', 'ComplexMSB16', 'ComplexMSB8', 'IEEE754LSBDouble', 'IEEE754LSBSingle', 'IEEE754MSBDouble', 'IEEE754MSBSingle', 'SignedBitString', 'SignedByte', 'SignedLSB2', 'SignedLSB4', 'SignedLSB8', 'SignedMSB2', 'SignedMSB4', 'SignedMSB8', 'UTF8_String', 'UnsignedBitString', 'UnsignedByte', 'UnsignedLSB2', 'UnsignedLSB4', 'UnsignedLSB8', 'UnsignedMSB2', 'UnsignedMSB4', 'UnsignedMSB8'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Field_Bit">
      <sch:assert test="pds:data_type = ('SignedBitString', 'UnsignedBitString')">
        The attribute pds:data_type must be equal to one of the following values 'SignedBitString', 'UnsignedBitString'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Field_Character">
      <sch:assert test="pds:data_type = ('ASCII_AnyURI', 'ASCII_Boolean', 'ASCII_DOI', 'ASCII_Date', 'ASCII_Date_DOY', 'ASCII_Date_Time', 'ASCII_Date_Time_DOY', 'ASCII_Date_Time_UTC', 'ASCII_Date_Time_YMD', 'ASCII_Date_YMD', 'ASCII_Directory_Path_Name', 'ASCII_File_Name', 'ASCII_File_Specification_Name', 'ASCII_Integer', 'ASCII_LID', 'ASCII_LIDVID', 'ASCII_LIDVID_LID', 'ASCII_MD5_Checksum', 'ASCII_NonNegative_Integer', 'ASCII_Numeric_Base16', 'ASCII_Numeric_Base2', 'ASCII_Numeric_Base8', 'ASCII_Real', 'ASCII_String', 'ASCII_Time', 'ASCII_VID', 'UTF8_String')">
        The attribute pds:data_type must be equal to one of the following values 'ASCII_AnyURI', 'ASCII_Boolean', 'ASCII_DOI', 'ASCII_Date', 'ASCII_Date_DOY', 'ASCII_Date_Time', 'ASCII_Date_Time_DOY', 'ASCII_Date_Time_UTC', 'ASCII_Date_Time_YMD', 'ASCII_Date_YMD', 'ASCII_Directory_Path_Name', 'ASCII_File_Name', 'ASCII_File_Specification_Name', 'ASCII_Integer', 'ASCII_LID', 'ASCII_LIDVID', 'ASCII_LIDVID_LID', 'ASCII_MD5_Checksum', 'ASCII_NonNegative_Integer', 'ASCII_Numeric_Base16', 'ASCII_Numeric_Base2', 'ASCII_Numeric_Base8', 'ASCII_Real', 'ASCII_String', 'ASCII_Time', 'ASCII_VID', 'UTF8_String'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Field_Delimited">
      <sch:assert test="pds:data_type = ('ASCII_AnyURI', 'ASCII_Boolean', 'ASCII_DOI', 'ASCII_Date', 'ASCII_Date_DOY', 'ASCII_Date_Time', 'ASCII_Date_Time_DOY', 'ASCII_Date_Time_UTC', 'ASCII_Date_Time_YMD', 'ASCII_Date_YMD', 'ASCII_Directory_Path_Name', 'ASCII_File_Name', 'ASCII_File_Specification_Name', 'ASCII_Integer', 'ASCII_LID', 'ASCII_LIDVID', 'ASCII_LIDVID_LID', 'ASCII_MD5_Checksum', 'ASCII_NonNegative_Integer', 'ASCII_Numeric_Base16', 'ASCII_Numeric_Base2', 'ASCII_Numeric_Base8', 'ASCII_Real', 'ASCII_String', 'ASCII_Time', 'ASCII_VID', 'UTF8_String')">
        The attribute pds:data_type must be equal to one of the following values 'ASCII_AnyURI', 'ASCII_Boolean', 'ASCII_DOI', 'ASCII_Date', 'ASCII_Date_DOY', 'ASCII_Date_Time', 'ASCII_Date_Time_DOY', 'ASCII_Date_Time_UTC', 'ASCII_Date_Time_YMD', 'ASCII_Date_YMD', 'ASCII_Directory_Path_Name', 'ASCII_File_Name', 'ASCII_File_Specification_Name', 'ASCII_Integer', 'ASCII_LID', 'ASCII_LIDVID', 'ASCII_LIDVID_LID', 'ASCII_MD5_Checksum', 'ASCII_NonNegative_Integer', 'ASCII_Numeric_Base16', 'ASCII_Numeric_Base2', 'ASCII_Numeric_Base8', 'ASCII_Real', 'ASCII_String', 'ASCII_Time', 'ASCII_VID', 'UTF8_String'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Header">
      <sch:assert test="pds:external_standard_id = ('ASCII', 'FITS', 'ISIS2', 'ISIS3', 'ISIS_History', 'ODL', 'PDS1', 'PDS2', 'PDS3', 'PDS_DSV', 'TIFF', 'VICAR')">
        The attribute pds:external_standard_id must be equal to one of the following values 'ASCII', 'FITS', 'ISIS2', 'ISIS3', 'ISIS_History', 'ODL', 'PDS1', 'PDS2', 'PDS3', 'PDS_DSV', 'TIFF', 'VICAR'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Header_Encoded">
      <sch:assert test="pds:external_standard_id = ('TIFF')">
        The attribute pds:external_standard_id must be equal to the value 'TIFF'.</sch:assert>
      <sch:assert test="pds:encoding_type = ('Binary')">
        The attribute pds:encoding_type must be equal to the value 'Binary'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Identification_Area">
      <sch:assert test="pds:product_class = ('Product_AIP', 'Product_Attribute_Definition', 'Product_Browse', 'Product_Bundle', 'Product_Collection', 'Product_Context', 'Product_DIP', 'Product_DIP_Deep_Archive', 'Product_Data_Set_PDS3', 'Product_Document', 'Product_File_Repository', 'Product_File_Text', 'Product_Instrument_Host_PDS3', 'Product_Instrument_PDS3', 'Product_Mission_PDS3', 'Product_Observational', 'Product_Proxy_PDS3', 'Product_SIP', 'Product_SPICE_Kernel', 'Product_Service', 'Product_Software', 'Product_Subscription_PDS3', 'Product_Target_PDS3', 'Product_Thumbnail', 'Product_Update', 'Product_Volume_PDS3', 'Product_Volume_Set_PDS3', 'Product_XML_Schema', 'Product_Zipped')">
        The attribute pds:product_class must be equal to one of the following values 'Product_AIP', 'Product_Attribute_Definition', 'Product_Browse', 'Product_Bundle', 'Product_Collection', 'Product_Context', 'Product_DIP', 'Product_DIP_Deep_Archive', 'Product_Data_Set_PDS3', 'Product_Document', 'Product_File_Repository', 'Product_File_Text', 'Product_Instrument_Host_PDS3', 'Product_Instrument_PDS3', 'Product_Mission_PDS3', 'Product_Observational', 'Product_Proxy_PDS3', 'Product_SIP', 'Product_SPICE_Kernel', 'Product_Service', 'Product_Software', 'Product_Subscription_PDS3', 'Product_Target_PDS3', 'Product_Thumbnail', 'Product_Update', 'Product_Volume_PDS3', 'Product_Volume_Set_PDS3', 'Product_XML_Schema', 'Product_Zipped'.</sch:assert>
      <sch:assert test="pds:information_model_version = ('0.3.0.0.a')">
        The attribute pds:information_model_version must be equal to the value '0.3.0.0.a'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="/*">
      <sch:assert test="name() = ('Product_Attribute_Definition','Product_Browse', 'Product_Bundle', 'Product_Collection', 'Product_Context', 'Product_Document', 'Product_File_Repository', 'Product_File_Text', 'Product_Observational', 'Product_Service', 'Product_Software', 'Product_SPICE_Kernel', 'Product_Thumbnail', 'Product_Update', 'Product_XML_Schema', 'Product_Zipped','Product_Data_Set_PDS3', 'Product_Instrument_Host_PDS3', 'Product_Instrument_PDS3','Product_Mission_PDS3', 'Product_Proxy_PDS3', 'Product_Subscription_PDS3', 'Product_Target_PDS3', 'Product_Volume_PDS3', 'Product_Volume_Set_PDS3', 'Product_AIP', 'Product_DIP', 'Product_SIP', 'Product_DIP_Deep_Archive')">
        The ROOT element is not one of the allowed product types.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Instrument">
      <sch:assert test="pds:type = ('Accelerometer', 'Alpha_Particle_Detector', 'Alpha_Particle_Xray_Spectrometer', 'Altimeter', 'Anemometer', 'Atomic_Force_Microscope', 'Barometer', 'Biology_Experiments', 'Bolometer', 'Camera', 'Cosmic_Ray_Detector', 'Dust_Detector', 'Electrical_Probe', 'Energetic_Particle_Detector', 'Gamma_Ray_Detector', 'Gas_Analyzer', 'Grinding_And_Drilling_Tool', 'Imager', 'Imaging_Spectrometer', 'Inertial_Measurement_Unit', 'Infrared_Spectrometer', 'Laser_Induced_Breakdown_Spectrometer', 'Magnetometer', 'Mass_Spectrometer', 'Meteorology', 'Microwave_Spectrometer', 'Moessbauer_Spectrometer', 'Naked_Eye', 'Neutral_Particle_Detector', 'Neutron_Detector', 'Other', 'Photometer', 'Plasma_Analyzer', 'Plasma_Detector', 'Plasma_Wave_Spectrometer', 'Polarimeter', 'RADAR', 'Radio_Science', 'Radio_Spectrometer', 'Radio_Telescope', 'Radiometer', 'Reflectometer', 'Spectrograph_Imager', 'Spectrometer', 'Thermal_And_Electrical_Conductivity_Probe', 'Thermal_Imager', 'Thermal_Probe', 'Thermometer', 'Ultraviolet_Spectrometer', 'Wet_Chemistry_Laboratory', 'X-ray_Defraction_Spectrometer', 'X-ray_Detector', 'X-ray_Fluorescence', 'X-ray_Fluorescence_Spectrometer')">
        The attribute pds:type must be equal to one of the following values 'Accelerometer', 'Alpha_Particle_Detector', 'Alpha_Particle_Xray_Spectrometer', 'Altimeter', 'Anemometer', 'Atomic_Force_Microscope', 'Barometer', 'Biology_Experiments', 'Bolometer', 'Camera', 'Cosmic_Ray_Detector', 'Dust_Detector', 'Electrical_Probe', 'Energetic_Particle_Detector', 'Gamma_Ray_Detector', 'Gas_Analyzer', 'Grinding_And_Drilling_Tool', 'Imager', 'Imaging_Spectrometer', 'Inertial_Measurement_Unit', 'Infrared_Spectrometer', 'Laser_Induced_Breakdown_Spectrometer', 'Magnetometer', 'Mass_Spectrometer', 'Meteorology', 'Microwave_Spectrometer', 'Moessbauer_Spectrometer', 'Naked_Eye', 'Neutral_Particle_Detector', 'Neutron_Detector', 'Other', 'Photometer', 'Plasma_Analyzer', 'Plasma_Detector', 'Plasma_Wave_Spectrometer', 'Polarimeter', 'RADAR', 'Radio_Science', 'Radio_Spectrometer', 'Radio_Telescope', 'Radiometer', 'Reflectometer', 'Spectrograph_Imager', 'Spectrometer', 'Thermal_And_Electrical_Conductivity_Probe', 'Thermal_Imager', 'Thermal_Probe', 'Thermometer', 'Ultraviolet_Spectrometer', 'Wet_Chemistry_Laboratory', 'X-ray_Defraction_Spectrometer', 'X-ray_Detector', 'X-ray_Fluorescence', 'X-ray_Fluorescence_Spectrometer'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Instrument_Host">
      <sch:assert test="pds:type = ('Earth_Based', 'Rover', 'Spacecraft')">
        The attribute pds:type must be equal to one of the following values 'Earth_Based', 'Rover', 'Spacecraft'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Internal_Reference">
      <sch:assert test="if (pds:lid_reference) then starts-with(pds:lid_reference,'urn:nasa:pds:') else true()">
        The value of the attribute lid_reference must start with 'urn:nasa:pds:'</sch:assert>
      <sch:assert test="if (pds:lidvid_reference) then starts-with(pds:lidvid_reference,'urn:nasa:pds:') else true()">
        The value of the attribute lidvid_reference must start with 'urn:nasa:pds:'</sch:assert>
      <sch:assert test="if (pds:lidvid_reference) then contains(pds:lidvid_reference,'::') else true()">
        The value of the attribute lidvid_reference must include a value that contains '::' followed by version id</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Inventory">
      <sch:assert test="((pds:reference_type eq 'inventory_has_member_product') and (count(pds:Record_Delimited/pds:Field_Delimited) eq 2))">
        Inventory.Field_Delimited does not match the expected number of instances</sch:assert>
      <sch:assert test="pds:offset eq '0'">
        Inventory.offset must have a value of '0'</sch:assert>
      <sch:assert test="pds:encoding_type = ('Character')">
        The attribute pds:encoding_type must be equal to the value 'Character'.</sch:assert>
      <sch:assert test="pds:reference_type = ('inventory_has_member_product')">
        The attribute pds:reference_type must be equal to the value 'inventory_has_member_product'.</sch:assert>
      <sch:assert test="pds:record_delimiter = ('carriage_return line_feed')">
        The attribute pds:record_delimiter must be equal to the value 'carriage_return line_feed'.</sch:assert>
      <sch:assert test="pds:external_standard_id = ('PDS_DSV V1.0')">
        The attribute pds:external_standard_id must be equal to the value 'PDS_DSV V1.0'.</sch:assert>
      <sch:assert test="pds:field_delimiter = ('comma', 'horizontal_tab', 'semicolon', 'vertical_bar')">
        The attribute pds:field_delimiter must be equal to one of the following values 'comma', 'horizontal_tab', 'semicolon', 'vertical_bar'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Inventory/pds:Record_Delimited/pds:Field_Delimited[1]">
      <sch:assert test="pds:field_number eq '1'">
        The first field of an Inventory must have field_number set to 1.</sch:assert>
      <sch:assert test="pds:maximum_field_length eq '1'">
        The first field of an Inventory must have maximum_field_length set to 1.</sch:assert>
      <sch:assert test="pds:data_type eq 'ASCII_String'">
        The first field of an Inventory must have data type set to 'ASCII_String'.</sch:assert>
      <sch:assert test="pds:name eq 'Member_Status'">
        The first field of an Inventory must have name set to 'Member_Status'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Inventory/pds:Record_Delimited/pds:Field_Delimited[2]">
      <sch:assert test="pds:field_number eq '2'">
        The second field of an Inventory must have field_number set to 2.</sch:assert>
      <sch:assert test="(pds:data_type eq 'ASCII_LIDVID_LID')">
        The second field of an Inventory must have data_type set to 'ASCII_LIDVID_LID'.</sch:assert>
      <sch:assert test="(pds:name eq 'LIDVID_LID')">
        The second field of an Inventory must have name set to 'LIDVID_LID'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Investigation">
      <sch:assert test="pds:type = ('Individual_Investigation', 'Mission', 'Observing_Campaign', 'Other_Investigation')">
        The attribute pds:type must be equal to one of the following values 'Individual_Investigation', 'Mission', 'Observing_Campaign', 'Other_Investigation'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Investigation_Area">
      <sch:assert test="pds:type = ('Individual_Investigation', 'Mission', 'Observing_Campaign', 'Other_Investigation')">
        The attribute pds:type must be equal to one of the following values 'Individual_Investigation', 'Mission', 'Observing_Campaign', 'Other_Investigation'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Investigation_Area/pds:Internal_Reference">
      <sch:assert test="every $ref in (pds:reference_type) satisfies $ref = ('data_to_investigation', 'collection_to_investigation', 'bundle_to_investigation')">
        The attribute reference_type must be set to one of the following values 'data_to_investigation', 'collection_to_investigation', 'bundle_to_investigation'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Node">
      <sch:assert test="pds:name = ('Engineering', 'European Space Agency', 'Geosciences', 'Imaging', 'Management', 'Navigation_Ancillary_Information_Facility', 'Planetary_Atmospheres', 'Planetary_Plasma_Interactions', 'Planetary_Rings', 'Radio_Science', 'Small_Bodies')">
        The attribute pds:name must be equal to one of the following values 'Engineering', 'European Space Agency', 'Geosciences', 'Imaging', 'Management', 'Navigation_Ancillary_Information_Facility', 'Planetary_Atmospheres', 'Planetary_Plasma_Interactions', 'Planetary_Rings', 'Radio_Science', 'Small_Bodies'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Observing_System_Component">
      <sch:assert test="pds:observing_system_component_type = ('Analyst', 'Artificial_Illumination', 'Ground-based_Laboratory', 'Ground-based_Observatory', 'Ground-based_Telescope', 'Instrument', 'Literature_Search', 'PDS_Archived_Data', 'Spacecraft')">
        The attribute pds:observing_system_component_type must be equal to one of the following values 'Analyst', 'Artificial_Illumination', 'Ground-based_Laboratory', 'Ground-based_Observatory', 'Ground-based_Telescope', 'Instrument', 'Literature_Search', 'PDS_Archived_Data', 'Spacecraft'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Observing_System_Component/pds:Internal_Reference">
      <sch:assert test="every $ref in (pds:reference_type) satisfies $ref = ('is_instrument', 'is_instrument_host')">
        The attribute reference_type must be set to one of the following values 'is_instrument', 'is_instrument_host'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:PDS_Affiliate">
      <sch:assert test="pds:affiliation_type = ('Affiliate', 'Data_Provider', 'Manager', 'Technical_Staff')">
        The attribute pds:affiliation_type must be equal to one of the following values 'Affiliate', 'Data_Provider', 'Manager', 'Technical_Staff'.</sch:assert>
      <sch:assert test="pds:team_name = ('Engineering', 'Geosciences', 'Headquarters', 'Imaging', 'Management', 'National_Space_Science_Data_Center', 'Navigation_Ancillary_Information_Facility', 'Planetary_Atmospheres', 'Planetary_Plasma_Interactions', 'Planetary_Rings', 'Radio_Science', 'Small_Bodies')">
        The attribute pds:team_name must be equal to one of the following values 'Engineering', 'Geosciences', 'Headquarters', 'Imaging', 'Management', 'National_Space_Science_Data_Center', 'Navigation_Ancillary_Information_Facility', 'Planetary_Atmospheres', 'Planetary_Plasma_Interactions', 'Planetary_Rings', 'Radio_Science', 'Small_Bodies'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Primary_Result_Summary">
      <sch:assert test="pds:data_regime = ('Dust', 'Electric_Field', 'Electrons', 'Far_Infrared', 'Gamma_Ray', 'Infrared', 'Ions', 'Magnetic_Field', 'Microwave', 'Millimeter', 'Near_Infrared', 'Particles', 'Radio', 'Sub-Millimeter', 'Ultraviolet', 'Visible', 'X-Ray')">
        The attribute pds:data_regime must be equal to one of the following values 'Dust', 'Electric_Field', 'Electrons', 'Far_Infrared', 'Gamma_Ray', 'Infrared', 'Ions', 'Magnetic_Field', 'Microwave', 'Millimeter', 'Near_Infrared', 'Particles', 'Radio', 'Sub-Millimeter', 'Ultraviolet', 'Visible', 'X-Ray'.</sch:assert>
      <sch:assert test="pds:type = ('Altimetry', 'Astrometry', 'Count', 'E/B-Field_Vectors', 'Gravity_Model', 'Image', 'Magnetometry', 'Map', 'Null_Result', 'Occultation', 'Photometry', 'Polarimetry', 'Radiometry', 'Shape_Model', 'Spectrum')">
        The attribute pds:type must be equal to one of the following values 'Altimetry', 'Astrometry', 'Count', 'E/B-Field_Vectors', 'Gravity_Model', 'Image', 'Magnetometry', 'Map', 'Null_Result', 'Occultation', 'Photometry', 'Polarimetry', 'Radiometry', 'Shape_Model', 'Spectrum'.</sch:assert>
      <sch:assert test="pds:purpose = ('Calibration', 'Checkout', 'Engineering', 'Navigation', 'Science')">
        The attribute pds:purpose must be equal to one of the following values 'Calibration', 'Checkout', 'Engineering', 'Navigation', 'Science'.</sch:assert>
      <sch:assert test="pds:processing_level_id = ('Calibrated', 'Derived', 'Raw', 'Reduced')">
        The attribute pds:processing_level_id must be equal to one of the following values 'Calibrated', 'Derived', 'Raw', 'Reduced'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Product_AIP/pds:Information_Package_Component/pds:Internal_Reference">
      <sch:assert test="every $ref in (pds:reference_type) satisfies $ref = ('package_to_collection', 'package_to_bundle', 'package_to_product')">
        The attribute reference_type must be set to one of the following values 'package_to_collection', 'package_to_bundle', 'package_to_product'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Product_Bundle/pds:Reference_List/pds:Internal_Reference">
      <sch:assert test="every $ref in (pds:reference_type) satisfies $ref = ('bundle_to_errata', 'bundle_to_document')">
        The attribute reference_type must be set to one of the following values 'bundle_to_errata', 'bundle_to_document'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Product_Collection/pds:Reference_List/pds:Internal_Reference">
      <sch:assert test="every $ref in (pds:reference_type) satisfies $ref = ('collection_to_resource', 'collection_to_associate', 'collection_to_calibration', 'collection_to_geometry', 'collection_to_spice_kernel', 'collection_curated_by_node', 'collection_to_document', 'collection_to_browse', 'collection_to_context', 'collection_to_data', 'collection_to_document', 'collection_to_schema', 'collection_to_errata', 'collection_to_bundle')">
        The attribute reference_type must be set to one of the following values 'collection_to_resource', 'collection_to_associate', 'collection_to_calibration', 'collection_to_geometry', 'collection_to_spice_kernel', 'collection_curated_by_node', 'collection_to_document', 'collection_to_browse', 'collection_to_context', 'collection_to_data', 'collection_to_document', 'collection_to_schema', 'collection_to_errata', 'collection_to_bundle'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Product_Context/pds:Reference_List/pds:Internal_Reference">
      <sch:assert test="every $ref in (pds:reference_type) satisfies $ref = ('context_to_resource', 'context_to_associate', 'context_to_document', 'context_to_collection', 'context_to_bundle', 'instrument_host_to_investigation', 'instrument_host_to_document', 'instrument_host_to_target', 'instrument_to_instrument_host', 'instrument_to_document', 'investigation_to_target', 'investigation_to_document', 'node_to_personnel', 'node_to_agency', 'node_to_manager', 'node_to_operator', 'node_to_data_archivist')">
        The attribute reference_type must be set to one of the following values 'context_to_resource', 'context_to_associate', 'context_to_document', 'context_to_collection', 'context_to_bundle', 'instrument_host_to_investigation', 'instrument_host_to_document', 'instrument_host_to_target', 'instrument_to_instrument_host', 'instrument_to_document', 'investigation_to_target', 'investigation_to_document', 'node_to_personnel', 'node_to_agency', 'node_to_manager', 'node_to_operator', 'node_to_data_archivist'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Product_DIP/pds:Information_Package_Component/pds:Internal_Reference">
      <sch:assert test="every $ref in (pds:reference_type) satisfies $ref = ('package_to_collection', 'package_to_bundle', 'package_to_product')">
        The attribute reference_type must be set to one of the following values 'package_to_collection', 'package_to_bundle', 'package_to_product'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Product_DIP_Deep_Archive/pds:Information_Package_Component/pds:Internal_Reference">
      <sch:assert test="every $ref in (pds:reference_type) satisfies $ref = ('package_to_collection', 'package_to_bundle', 'package_to_product')">
        The attribute reference_type must be set to one of the following values 'package_to_collection', 'package_to_bundle', 'package_to_product'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Product_Document/pds:Reference_List/pds:Internal_Reference">
      <sch:assert test="every $ref in (pds:reference_type) satisfies $ref = ('document_to_associate', 'document_to_investigation', 'document_to_instrument_host', 'document_to_instrument', 'document_to_target')">
        The attribute reference_type must be set to one of the following values 'document_to_associate', 'document_to_investigation', 'document_to_instrument_host', 'document_to_instrument', 'document_to_target'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Product_Observational/pds:Reference_List/pds:Internal_Reference">
      <sch:assert test="every $ref in (pds:reference_type) satisfies $ref = ('data_to_resource', 'data_to_calibration_document', 'data_to_calibration_product', 'data_to_raw_product', 'data_to_calibrated_product', 'data_to_geometry', 'data_to_spice_kernel', 'data_to_thumbnail', 'data_to_document', 'data_curated_by_node', 'data_to_browse')">
        The attribute reference_type must be set to one of the following values 'data_to_resource', 'data_to_calibration_document', 'data_to_calibration_product', 'data_to_raw_product', 'data_to_calibrated_product', 'data_to_geometry', 'data_to_spice_kernel', 'data_to_thumbnail', 'data_to_document', 'data_curated_by_node', 'data_to_browse'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Product_Operational/pds:Reference_List/pds:Internal_Reference">
      <sch:assert test="every $ref in (pds:reference_type) satisfies $ref = ('operational_to_resource', 'operational_to_associate', 'operational_to_document')">
        The attribute reference_type must be set to one of the following values 'operational_to_resource', 'operational_to_associate', 'operational_to_document'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Product_SIP/pds:Information_Package_Component/pds:Internal_Reference">
      <sch:assert test="every $ref in (pds:reference_type) satisfies $ref = ('package_to_collection', 'package_to_bundle', 'package_to_product')">
        The attribute reference_type must be set to one of the following values 'package_to_collection', 'package_to_bundle', 'package_to_product'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Product_Zipped/pds:Internal_Reference">
      <sch:assert test="every $ref in (pds:reference_type) satisfies $ref = ('zip_to_package')">
        The attribute reference_type must be set to one of the following values 'zip_to_package'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Quaternion">
      <sch:assert test="pds:type = ('SPICE', 'Spacecraft_Telemetry')">
        The attribute pds:type must be equal to one of the following values 'SPICE', 'Spacecraft_Telemetry'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Quaternion_Component">
      <sch:assert test="pds:data_type = ('ASCII_Real')">
        The attribute pds:data_type must be equal to the value 'ASCII_Real'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Resource">
      <sch:assert test="pds:type = ('Information.Agency', 'Information.Instrument', 'Information.Instrument_Host', 'Information.Investigation', 'Information.Node', 'Information.Person', 'Information.Resource', 'Information.Science_Portal', 'Information.Target', 'System.Browse', 'System.Directory_Listing', 'System.Registry_Query', 'System.Search', 'System.Transform', 'System.Transport')">
        The attribute pds:type must be equal to one of the following values 'Information.Agency', 'Information.Instrument', 'Information.Instrument_Host', 'Information.Investigation', 'Information.Node', 'Information.Person', 'Information.Resource', 'Information.Science_Portal', 'Information.Target', 'System.Browse', 'System.Directory_Listing', 'System.Registry_Query', 'System.Search', 'System.Transform', 'System.Transport'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:SPICE_Kernel">
      <sch:assert test="pds:encoding_type = ('Binary', 'Character')">
        The attribute pds:encoding_type must be equal to one of the following values 'Binary', 'Character'.</sch:assert>
      <sch:assert test="pds:kernel_type = ('CK', 'DBK', 'DSK', 'EK', 'FK', 'IK', 'LSK', 'MK', 'PCK', 'SCLK', 'SPK')">
        The attribute pds:kernel_type must be equal to one of the following values 'CK', 'DBK', 'DSK', 'EK', 'FK', 'IK', 'LSK', 'MK', 'PCK', 'SCLK', 'SPK'.</sch:assert>
      <sch:assert test="pds:external_standard_id = ('SPICE')">
        The attribute pds:external_standard_id must be equal to the value 'SPICE'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Service_Description">
      <sch:assert test="pds:encoding_type = ('Character')">
        The attribute pds:encoding_type must be equal to the value 'Character'.</sch:assert>
      <sch:assert test="pds:external_standard_id = ('WADL', 'WSDL')">
        The attribute pds:external_standard_id must be equal to one of the following values 'WADL', 'WSDL'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Stream_Text">
      <sch:assert test="pds:encoding_type = ('Character')">
        The attribute pds:encoding_type must be equal to the value 'Character'.</sch:assert>
      <sch:assert test="pds:record_delimiter = ('carriage_return line_feed')">
        The attribute pds:record_delimiter must be equal to the value 'carriage_return line_feed'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Table_Binary">
      <sch:assert test="pds:encoding_type = ('Binary')">
        The attribute pds:encoding_type must be equal to the value 'Binary'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Table_Character">
      <sch:assert test="pds:encoding_type = ('Character')">
        The attribute pds:encoding_type must be equal to the value 'Character'.</sch:assert>
      <sch:assert test="pds:record_delimiter = ('carriage_return line_feed')">
        The attribute pds:record_delimiter must be equal to the value 'carriage_return line_feed'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Table_Delimited">
      <sch:assert test="pds:encoding_type = ('Character')">
        The attribute pds:encoding_type must be equal to the value 'Character'.</sch:assert>
      <sch:assert test="pds:record_delimiter = ('carriage_return line_feed')">
        The attribute pds:record_delimiter must be equal to the value 'carriage_return line_feed'.</sch:assert>
      <sch:assert test="pds:external_standard_id = ('PDS_DSV V1.0')">
        The attribute pds:external_standard_id must be equal to the value 'PDS_DSV V1.0'.</sch:assert>
      <sch:assert test="pds:field_delimiter = ('comma', 'horizontal_tab', 'semicolon', 'vertical_bar')">
        The attribute pds:field_delimiter must be equal to one of the following values 'comma', 'horizontal_tab', 'semicolon', 'vertical_bar'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Target">
      <sch:assert test="pds:type = ('Asteroid', 'Comet', 'Dust', 'Dwarf_Planet', 'Galaxy', 'Globular_Cluster', 'Meteorite', 'Meteoroid', 'Meteoroid_Stream', 'Nebula', 'Open_Cluster', 'Planet', 'Planetary_Nebula', 'Planetary_System', 'Plasma_Cloud', 'Ring', 'Satellite', 'Star', 'Star_Cluster', 'Sun', 'Terrestrial_Sample', 'Trans-Neptunian_Object')">
        The attribute pds:type must be equal to one of the following values 'Asteroid', 'Comet', 'Dust', 'Dwarf_Planet', 'Galaxy', 'Globular_Cluster', 'Meteorite', 'Meteoroid', 'Meteoroid_Stream', 'Nebula', 'Open_Cluster', 'Planet', 'Planetary_Nebula', 'Planetary_System', 'Plasma_Cloud', 'Ring', 'Satellite', 'Star', 'Star_Cluster', 'Sun', 'Terrestrial_Sample', 'Trans-Neptunian_Object'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Target_Identification/pds:Internal_Reference">
      <sch:assert test="every $ref in (pds:reference_type) satisfies $ref = ('data_to_target', 'collection_to_target', 'bundle_to_target')">
        The attribute reference_type must be set to one of the following values 'data_to_target', 'collection_to_target', 'bundle_to_target'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Telescope">
      <sch:assert test="pds:coordinate_source = ('Aerial survey - North American (1983) datum', 'Astronomical', 'Doppler determined - WGS 72 datum', 'Geodetic - Adindan datum', 'Geodetic - Australian datum', 'Geodetic - Campo Inchauspe (Argentina) datum', 'Geodetic - Cape (South Africa) datum', 'Geodetic - Corregio Alegre (Brazil) datum', 'Geodetic - European 1979 datum', 'Geodetic - European datum', 'Geodetic - GRS 80 datum', 'Geodetic - Hermannskogel datum', 'Geodetic - Indian datum', 'Geodetic - La Canoa (Venezuela) datum', 'Geodetic - New Zealand datum', 'Geodetic - North American (1927) datum', 'Geodetic - Old Hawaiian datum', 'Geodetic - Ordnance Survey of Great Britain (1936) datum', 'Geodetic - Ordnance Survey of Great Britain (SN) 1980 datum', 'Geodetic - Potsdam datum', 'Geodetic - Puerto Rican (1940) datum', 'Geodetic - South American datum', 'Geodetic - Tokyo datum', 'Geodetic - WGS 84 datum', 'Geodetic - datum unknown', 'Satellite determined - datum unknown', 'Unknown')">
        The attribute pds:coordinate_source must be equal to one of the following values 'Aerial survey - North American (1983) datum', 'Astronomical', 'Doppler determined - WGS 72 datum', 'Geodetic - Adindan datum', 'Geodetic - Australian datum', 'Geodetic - Campo Inchauspe (Argentina) datum', 'Geodetic - Cape (South Africa) datum', 'Geodetic - Corregio Alegre (Brazil) datum', 'Geodetic - European 1979 datum', 'Geodetic - European datum', 'Geodetic - GRS 80 datum', 'Geodetic - Hermannskogel datum', 'Geodetic - Indian datum', 'Geodetic - La Canoa (Venezuela) datum', 'Geodetic - New Zealand datum', 'Geodetic - North American (1927) datum', 'Geodetic - Old Hawaiian datum', 'Geodetic - Ordnance Survey of Great Britain (1936) datum', 'Geodetic - Ordnance Survey of Great Britain (SN) 1980 datum', 'Geodetic - Potsdam datum', 'Geodetic - Puerto Rican (1940) datum', 'Geodetic - South American datum', 'Geodetic - Tokyo datum', 'Geodetic - WGS 84 datum', 'Geodetic - datum unknown', 'Satellite determined - datum unknown', 'Unknown'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Terminological_Entry">
      <sch:assert test="pds:language = ('English')">
        The attribute pds:language must be equal to the value 'English'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Transfer_Manifest">
      <sch:assert test="pds:encoding_type = ('Character')">
        The attribute pds:encoding_type must be equal to the value 'Character'.</sch:assert>
      <sch:assert test="pds:record_delimiter = ('carriage_return line_feed')">
        The attribute pds:record_delimiter must be equal to the value 'carriage_return line_feed'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Uniformly_Sampled">
      <sch:assert test="pds:sampling_parameter_scale = ('Exponential', 'Linear', 'Logarithmic')">
        The attribute pds:sampling_parameter_scale must be equal to one of the following values 'Exponential', 'Linear', 'Logarithmic'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Update_Entry/pds:Internal_Reference">
      <sch:assert test="every $ref in (pds:reference_type) satisfies $ref = ('data_to_update', 'collection_to_update', 'bundle_to_update')">
        The attribute reference_type must be set to one of the following values 'data_to_update', 'collection_to_update', 'bundle_to_update'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Vector">
      <sch:assert test="pds:type = ('Position', 'Velocity')">
        The attribute pds:type must be equal to one of the following values 'Position', 'Velocity'.</sch:assert>
      <sch:assert test="pds:data_type = ('ASCII_Real')">
        The attribute pds:data_type must be equal to the value 'ASCII_Real'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Volume_PDS3">
      <sch:assert test="pds:archive_status = ('ARCHIVED', 'ARCHIVED_ACCUMULATING', 'IN_LIEN_RESOLUTION', 'IN_LIEN_RESOLUTION_ACCUMULATING', 'IN_PEER_REVIEW', 'IN_PEER_REVIEW_ACCUMULATING', 'IN_QUEUE', 'IN_QUEUE_ACCUMULATING', 'LOCALLY_ARCHIVED', 'LOCALLY_ARCHIVED_ACCUMULATING', 'PRE_PEER_REVIEW', 'PRE_PEER_REVIEW_ACCUMULATING', 'SAFED', 'SUPERSEDED')">
        The attribute pds:archive_status must be equal to one of the following values 'ARCHIVED', 'ARCHIVED_ACCUMULATING', 'IN_LIEN_RESOLUTION', 'IN_LIEN_RESOLUTION_ACCUMULATING', 'IN_PEER_REVIEW', 'IN_PEER_REVIEW_ACCUMULATING', 'IN_QUEUE', 'IN_QUEUE_ACCUMULATING', 'LOCALLY_ARCHIVED', 'LOCALLY_ARCHIVED_ACCUMULATING', 'PRE_PEER_REVIEW', 'PRE_PEER_REVIEW_ACCUMULATING', 'SAFED', 'SUPERSEDED'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:XML_Schema">
      <sch:assert test="pds:offset eq '0'">
        XML_Schema.offset must have a value of '0'</sch:assert>
      <sch:assert test="pds:encoding_type = ('Character')">
        The attribute pds:encoding_type must be equal to the value 'Character'.</sch:assert>
      <sch:assert test="pds:external_standard_id = ('Schematron ISO/IEC 19757-3:2006', 'XML Schema Version 1.1')">
        The attribute pds:external_standard_id must be equal to one of the following values 'Schematron ISO/IEC 19757-3:2006', 'XML Schema Version 1.1'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Zip">
      <sch:assert test="pds:container_type = ('GZIP', 'LZIP', 'TAR', 'ZIP')">
        The attribute pds:container_type must be equal to one of the following values 'GZIP', 'LZIP', 'TAR', 'ZIP'.</sch:assert>
    </sch:rule>
  </sch:pattern>
</sch:schema>
