<?xml version="1.0" encoding="UTF-8"?>
  <!-- PDS4 Schematron for PDS4_0.8.0.0.k  Thu Jun 14 14:12:46 PDT 2012 -->
  <!-- Generated from the PDS4 Information Model V0.8.0.0.k -->
  <!-- *** This PDS4 schematron file is a preliminary deliverable. *** -->
  <!-- *** It is being made available for review and testing. *** -->
<sch:schema xmlns:sch="http://purl.oclc.org/dsdl/schematron" queryBinding="xslt2">

  <sch:title>Sample Schematron using XPath 2.0</sch:title>

  <sch:ns uri="http://pds.nasa.gov/pds4/pds/v08" prefix="pds"/>

		   <!-- ================================================ -->
		   <!-- NOTE:  There are two types of schematron rules.  -->
		   <!--        One type includes rules written for       -->
		   <!--        specific situations. The other type are   -->
		   <!--        generated to validate enumerated value    -->
		   <!--        lists. These two types of rules have been -->
		   <!--        merged together in the rules below.       -->
		   <!-- ================================================ -->
  <sch:pattern>
    <sch:rule context="pds:Array">
      <sch:assert test="pds:encoding_type = ('Binary')">
        The attribute pds:encoding_type must be equal to the value 'Binary'.</sch:assert>
      <sch:assert test="pds:axis_index_order = ('Last_Index_Fastest')">
        The attribute pds:axis_index_order must be equal to the value 'Last_Index_Fastest'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Array_2D">
      <sch:assert test="pds:axes = ('2')">
        The attribute pds:axes must be equal to the value '2'.</sch:assert>
      <sch:assert test="pds:encoding_type = ('Binary')">
        The attribute pds:encoding_type must be equal to the value 'Binary'.</sch:assert>
      <sch:assert test="pds:axis_index_order = ('Last_Index_Fastest')">
        The attribute pds:axis_index_order must be equal to the value 'Last_Index_Fastest'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Array_2D_Image">
      <sch:assert test="pds:axes = ('2')">
        The attribute pds:axes must be equal to the value '2'.</sch:assert>
      <sch:assert test="pds:encoding_type = ('Binary')">
        The attribute pds:encoding_type must be equal to the value 'Binary'.</sch:assert>
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
      <sch:assert test="pds:encoding_type = ('Binary')">
        The attribute pds:encoding_type must be equal to the value 'Binary'.</sch:assert>
      <sch:assert test="pds:axis_index_order = ('Last_Index_Fastest')">
        The attribute pds:axis_index_order must be equal to the value 'Last_Index_Fastest'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Array_2D_Spectrum">
      <sch:assert test="pds:axes = ('2')">
        The attribute pds:axes must be equal to the value '2'.</sch:assert>
      <sch:assert test="pds:encoding_type = ('Binary')">
        The attribute pds:encoding_type must be equal to the value 'Binary'.</sch:assert>
      <sch:assert test="pds:axis_index_order = ('Last_Index_Fastest')">
        The attribute pds:axis_index_order must be equal to the value 'Last_Index_Fastest'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Array_3D">
      <sch:assert test="pds:axes = ('3')">
        The attribute pds:axes must be equal to the value '3'.</sch:assert>
      <sch:assert test="pds:encoding_type = ('Binary')">
        The attribute pds:encoding_type must be equal to the value 'Binary'.</sch:assert>
      <sch:assert test="pds:axis_index_order = ('Last_Index_Fastest')">
        The attribute pds:axis_index_order must be equal to the value 'Last_Index_Fastest'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Array_3D_Image">
      <sch:assert test="pds:axes = ('3')">
        The attribute pds:axes must be equal to the value '3'.</sch:assert>
      <sch:assert test="pds:encoding_type = ('Binary')">
        The attribute pds:encoding_type must be equal to the value 'Binary'.</sch:assert>
      <sch:assert test="pds:axis_index_order = ('Last_Index_Fastest')">
        The attribute pds:axis_index_order must be equal to the value 'Last_Index_Fastest'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Array_3D_Movie">
      <sch:assert test="pds:axes = ('3')">
        The attribute pds:axes must be equal to the value '3'.</sch:assert>
      <sch:assert test="pds:encoding_type = ('Binary')">
        The attribute pds:encoding_type must be equal to the value 'Binary'.</sch:assert>
      <sch:assert test="pds:axis_index_order = ('Last_Index_Fastest')">
        The attribute pds:axis_index_order must be equal to the value 'Last_Index_Fastest'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Array_3D_Spectrum">
      <sch:assert test="pds:axes = ('3')">
        The attribute pds:axes must be equal to the value '3'.</sch:assert>
      <sch:assert test="pds:encoding_type = ('Binary')">
        The attribute pds:encoding_type must be equal to the value 'Binary'.</sch:assert>
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
      <sch:assert test="pds:member_status = ('Primary', 'Secondary_No_File', 'Secondary_With_File')">
        The attribute pds:member_status must be equal to one of the following values 'Primary', 'Secondary_No_File', 'Secondary_With_File'.</sch:assert>
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
    <sch:rule context="pds:Delivery_Manifest">
      <sch:assert test="pds:offset eq '0'">
        The offset for a Delivery Manifest must be set to 0.</sch:assert>
      <sch:assert test="pds:encoding_type eq 'CHARACTER'">
        The encoding_type for a Delivery Manifest must be set to CHARACTER.</sch:assert>
      <sch:assert test="pds:encoding_type = ('Character')">
        The attribute pds:encoding_type must be equal to the value 'Character'.</sch:assert>
      <sch:assert test="pds:record_delimiter = ('carriage_return', 'carriage_return line_feed', 'line_feed')">
        The attribute pds:record_delimiter must be equal to one of the following values 'carriage_return', 'carriage_return line_feed', 'line_feed'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Delivery_Manifest/pds:Record_Character/pds:Field_Character[1]">
      <sch:assert test="pds:field_number eq '1'">
        The first field of a Delivery Manifest must have field_number set to 1.</sch:assert>
      <sch:assert test="pds:field_location eq '1'">
        The first field of a Delivery Manifest must have field_location set to 1.</sch:assert>
      <sch:assert test="pds:data_type eq 'ASCII_MD5_Checksum'">
        The first field of a Delivery Manifest must have data_type set to ASCII_MD5_Checksum.</sch:assert>
      <sch:assert test="pds:name eq 'MD5_Checksum'">
        The first field of a Delivery Manifest must have name set to MD5_Checksum.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Delivery_Manifest/pds:Record_Character/pds:Field_Character[2]">
      <sch:assert test="pds:field_number eq '2'">
        The second field of a Delivery Manifest must have field_number set to 2.</sch:assert>
      <sch:assert test="pds:field_location eq '17'">
        The second field of a Delivery Manifest must have field_location set to 17.</sch:assert>
      <sch:assert test="pds:data_type eq 'ASCII_File_Specification_Name'">
        The second field of a Delivery Manifest must have data_type set to ASCII_File_Specification_Name.</sch:assert>
      <sch:assert test="pds:name eq 'File_Specification_Name'">
        The second field of a Delivery Manifest must have name set to File_Specification_Name.</sch:assert>
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
      <sch:assert test="pds:external_standard_id = ('ASCII', 'Encapsulated_Postscript', 'GIF', 'HTML', 'JPG', 'LaTEX', 'Microsoft_Word', 'PDF', 'PDF-A', 'PNG', 'Postscript', 'Rich_Text', 'TIFF', 'UTF-8')">
        The attribute pds:external_standard_id must be equal to one of the following values 'ASCII', 'Encapsulated_Postscript', 'GIF', 'HTML', 'JPG', 'LaTEX', 'Microsoft_Word', 'PDF', 'PDF-A', 'PNG', 'Postscript', 'Rich_Text', 'TIFF', 'UTF-8'.</sch:assert>
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
      <sch:assert test="pds:data_type = ('IEEE754LSBDouble', 'IEEE754LSBSingle', 'IEEE754MSBDouble', 'IEEE754MSBSingle', 'SignedByte', 'SignedLSB2', 'SignedLSB4', 'SignedLSB8', 'SignedMSB2', 'SignedMSB4', 'SignedMSB8', 'UnsignedByte', 'UnsignedLSB2', 'UnsignedLSB4', 'UnsignedLSB8', 'UnsignedMSB2', 'UnsignedMSB4', 'UnsignedMSB8')">
        The attribute pds:data_type must be equal to one of the following values 'IEEE754LSBDouble', 'IEEE754LSBSingle', 'IEEE754MSBDouble', 'IEEE754MSBSingle', 'SignedByte', 'SignedLSB2', 'SignedLSB4', 'SignedLSB8', 'SignedMSB2', 'SignedMSB4', 'SignedMSB8', 'UnsignedByte', 'UnsignedLSB2', 'UnsignedLSB4', 'UnsignedLSB8', 'UnsignedMSB2', 'UnsignedMSB4', 'UnsignedMSB8'.</sch:assert>
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
    <sch:rule context="pds:Field_Binary">
      <sch:assert test="pds:data_type = ('ASCII_AnyURI', 'ASCII_Boolean', 'ASCII_DOI', 'ASCII_Date_DOY', 'ASCII_Date_Time_DOY', 'ASCII_Date_Time_UTC', 'ASCII_Date_Time_YMD', 'ASCII_Date_YMD', 'ASCII_File_Name', 'ASCII_File_Specification_Name', 'ASCII_Integer', 'ASCII_LID', 'ASCII_LIDVID', 'ASCII_MD5_Checksum', 'ASCII_NonNegative_Integer', 'ASCII_Numeric_Base16', 'ASCII_Numeric_Base2', 'ASCII_Real', 'ASCII_Short_String_Collapsed', 'ASCII_Short_String_Preserved', 'ASCII_String', 'ASCII_Text_Preserved', 'ASCII_Time', 'ASCII_VID', 'ComplexLSB16', 'ComplexLSB8', 'ComplexMSB16', 'ComplexMSB8', 'IEEE754LSBDouble', 'IEEE754LSBSingle', 'IEEE754MSBDouble', 'IEEE754MSBSingle', 'SignedByte', 'SignedLSB2', 'SignedLSB4', 'SignedLSB8', 'SignedMSB2', 'SignedMSB4', 'SignedMSB8', 'UTF8_String', 'UnsignedByte', 'UnsignedLSB2', 'UnsignedLSB4', 'UnsignedLSB8', 'UnsignedMSB2', 'UnsignedMSB4', 'UnsignedMSB8')">
        The attribute pds:data_type must be equal to one of the following values 'ASCII_AnyURI', 'ASCII_Boolean', 'ASCII_DOI', 'ASCII_Date_DOY', 'ASCII_Date_Time_DOY', 'ASCII_Date_Time_UTC', 'ASCII_Date_Time_YMD', 'ASCII_Date_YMD', 'ASCII_File_Name', 'ASCII_File_Specification_Name', 'ASCII_Integer', 'ASCII_LID', 'ASCII_LIDVID', 'ASCII_MD5_Checksum', 'ASCII_NonNegative_Integer', 'ASCII_Numeric_Base16', 'ASCII_Numeric_Base2', 'ASCII_Real', 'ASCII_Short_String_Collapsed', 'ASCII_Short_String_Preserved', 'ASCII_String', 'ASCII_Text_Preserved', 'ASCII_Time', 'ASCII_VID', 'ComplexLSB16', 'ComplexLSB8', 'ComplexMSB16', 'ComplexMSB8', 'IEEE754LSBDouble', 'IEEE754LSBSingle', 'IEEE754MSBDouble', 'IEEE754MSBSingle', 'SignedByte', 'SignedLSB2', 'SignedLSB4', 'SignedLSB8', 'SignedMSB2', 'SignedMSB4', 'SignedMSB8', 'UTF8_String', 'UnsignedByte', 'UnsignedLSB2', 'UnsignedLSB4', 'UnsignedLSB8', 'UnsignedMSB2', 'UnsignedMSB4', 'UnsignedMSB8'.</sch:assert>
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
      <sch:assert test="pds:data_type = ('ASCII_AnyURI', 'ASCII_Boolean', 'ASCII_DOI', 'ASCII_Date_DOY', 'ASCII_Date_Time_DOY', 'ASCII_Date_Time_UTC', 'ASCII_Date_Time_YMD', 'ASCII_Date_YMD', 'ASCII_File_Name', 'ASCII_File_Specification_Name', 'ASCII_Integer', 'ASCII_LID', 'ASCII_LIDVID', 'ASCII_MD5_Checksum', 'ASCII_NonNegative_Integer', 'ASCII_Numeric_Base16', 'ASCII_Numeric_Base2', 'ASCII_Real', 'ASCII_Short_String_Collapsed', 'ASCII_Short_String_Preserved', 'ASCII_String', 'ASCII_Text_Preserved', 'ASCII_Time', 'ASCII_VID', 'UTF8_String')">
        The attribute pds:data_type must be equal to one of the following values 'ASCII_AnyURI', 'ASCII_Boolean', 'ASCII_DOI', 'ASCII_Date_DOY', 'ASCII_Date_Time_DOY', 'ASCII_Date_Time_UTC', 'ASCII_Date_Time_YMD', 'ASCII_Date_YMD', 'ASCII_File_Name', 'ASCII_File_Specification_Name', 'ASCII_Integer', 'ASCII_LID', 'ASCII_LIDVID', 'ASCII_MD5_Checksum', 'ASCII_NonNegative_Integer', 'ASCII_Numeric_Base16', 'ASCII_Numeric_Base2', 'ASCII_Real', 'ASCII_Short_String_Collapsed', 'ASCII_Short_String_Preserved', 'ASCII_String', 'ASCII_Text_Preserved', 'ASCII_Time', 'ASCII_VID', 'UTF8_String'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Field_Delimited">
      <sch:assert test="pds:data_type = ('ASCII_AnyURI', 'ASCII_Boolean', 'ASCII_DOI', 'ASCII_Date_DOY', 'ASCII_Date_Time_DOY', 'ASCII_Date_Time_UTC', 'ASCII_Date_Time_YMD', 'ASCII_Date_YMD', 'ASCII_File_Name', 'ASCII_File_Specification_Name', 'ASCII_Integer', 'ASCII_LID', 'ASCII_LIDVID', 'ASCII_MD5_Checksum', 'ASCII_NonNegative_Integer', 'ASCII_Numeric_Base16', 'ASCII_Numeric_Base2', 'ASCII_Real', 'ASCII_Short_String_Collapsed', 'ASCII_Short_String_Preserved', 'ASCII_String', 'ASCII_Text_Preserved', 'ASCII_Time', 'ASCII_VID', 'UTF8_String')">
        The attribute pds:data_type must be equal to one of the following values 'ASCII_AnyURI', 'ASCII_Boolean', 'ASCII_DOI', 'ASCII_Date_DOY', 'ASCII_Date_Time_DOY', 'ASCII_Date_Time_UTC', 'ASCII_Date_Time_YMD', 'ASCII_Date_YMD', 'ASCII_File_Name', 'ASCII_File_Specification_Name', 'ASCII_Integer', 'ASCII_LID', 'ASCII_LIDVID', 'ASCII_MD5_Checksum', 'ASCII_NonNegative_Integer', 'ASCII_Numeric_Base16', 'ASCII_Numeric_Base2', 'ASCII_Real', 'ASCII_Short_String_Collapsed', 'ASCII_Short_String_Preserved', 'ASCII_String', 'ASCII_Text_Preserved', 'ASCII_Time', 'ASCII_VID', 'UTF8_String'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Header">
      <sch:assert test="pds:external_standard_id = ('ASCII', 'FITS', 'ISIS2', 'ISIS3', 'ISIS_History', 'ODL', 'PDS1', 'PDS2', 'PDS3', 'TIFF', 'VICAR')">
        The attribute pds:external_standard_id must be equal to one of the following values 'ASCII', 'FITS', 'ISIS2', 'ISIS3', 'ISIS_History', 'ODL', 'PDS1', 'PDS2', 'PDS3', 'TIFF', 'VICAR'.</sch:assert>
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
      <sch:assert test="pds:information_model_version = ('0.8.0.0.k')">
        The attribute pds:information_model_version must be equal to the value '0.8.0.0.k'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="/*">
      <sch:assert test="name() = ('Product_Attribute_Definition','Product_Browse','Product_Bundle','Product_Collection','Product_Context','Product_Document','Product_File_Repository','Product_File_Text','Product_Observational','Product_Service','Product_Software','Product_SPICE_Kernel','Product_Thumbnail','Product_XML_Schema','Product_Delivery_Manifest','Product_Update','Product_Zipped','Product_Instrument','Product_Instrument_Host','Product_Investigation','Product_Node','Product_Other','Product_PDS_Affiliate','Product_PDS_Guest','Product_Resource','Product_Target','Collection_Volume_PDS3','Collection_Volume_Set_PDS3','Product_Data_Set_PDS3','Product_Instrument_Host_PDS3','Product_Instrument_PDS3','Product_Mission_PDS3','Product_Proxy_PDS3','Product_Subscription_PDS3','Product_Target_PDS3')">
        The ROOT element is not one of the allowed product types.</sch:assert>
      <sch:assert test="name() eq pds:Identification_Area/pds:product_class">
        The name of the Product type is not equal to the value of the product_class attribute.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Instrument">
      <sch:assert test="pds:type = ('Accelerometer', 'Altimeter', 'Camera', 'Cosmic_Ray_Detector', 'Detector_Array', 'Dust_Analyzer', 'Imaging_Spectrometer', 'Magnetometer', 'Meteorology', 'Naked_Eye', 'Other', 'Particle_Detector', 'Photometer', 'Plasma_Analyzer', 'Radio_Science', 'Radio_Spectrometer', 'Radio_Telescope', 'Radiometer', 'Reflectometer', 'Spectrograph_Imager', 'Spectrometer')">
        The attribute pds:type must be equal to one of the following values 'Accelerometer', 'Altimeter', 'Camera', 'Cosmic_Ray_Detector', 'Detector_Array', 'Dust_Analyzer', 'Imaging_Spectrometer', 'Magnetometer', 'Meteorology', 'Naked_Eye', 'Other', 'Particle_Detector', 'Photometer', 'Plasma_Analyzer', 'Radio_Science', 'Radio_Spectrometer', 'Radio_Telescope', 'Radiometer', 'Reflectometer', 'Spectrograph_Imager', 'Spectrometer'.</sch:assert>
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
      <sch:assert test="((pds:reference_type eq 'inventory_has_LIDVID_Primary') and (count(pds:Record_Character/pds:Field_Character) eq 2)) or ((pds:reference_type eq 'inventory_has_LIDVID_Secondary') and (count(pds:Record_Character/pds:Field_Character) &gt;= 1) and (count(pds:Record_Character/pds:Field_Character) &lt;= 2)) or ((pds:reference_type eq 'inventory_has_LID_Secondary') and (count(pds:Record_Character/pds:Field_Character) &gt;= 1) and (count(pds:Record_Character/pds:Field_Character) &lt;= 2))">
        Inventory.Field_Character does not match the expected number of instances</sch:assert>
      <sch:assert test="pds:encoding_type = ('Character')">
        The attribute pds:encoding_type must be equal to the value 'Character'.</sch:assert>
      <sch:assert test="pds:reference_type = ('inventory_has_LIDVID_Primary', 'inventory_has_LIDVID_Secondary', 'inventory_has_LID_Secondary')">
        The attribute pds:reference_type must be equal to one of the following values 'inventory_has_LIDVID_Primary', 'inventory_has_LIDVID_Secondary', 'inventory_has_LID_Secondary'.</sch:assert>
      <sch:assert test="pds:record_delimiter = ('carriage_return', 'carriage_return line_feed', 'line_feed')">
        The attribute pds:record_delimiter must be equal to one of the following values 'carriage_return', 'carriage_return line_feed', 'line_feed'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Inventory/pds:Record_Character/pds:Field_Character[1]">
      <sch:assert test="pds:field_number eq '1'">
        The first field of an Inventory must have field_number set to 1.</sch:assert>
      <sch:assert test="pds:field_location  eq '1'">
        The first field of an Inventory must have field_location set to 1.</sch:assert>
      <sch:assert test="(pds:data_type eq 'ASCII_LIDVID') or (pds:data_type eq 'ASCII_LID')">
        The first field of an Inventory must have data_type set to 'ASCII_LIDVID' or 'ASCII_LID'.</sch:assert>
      <sch:assert test="(pds:name eq 'LIDVID') or (pds:name eq 'LID')">
        The first field of an Inventory must have name set to 'LIDVID' or 'LID'.</sch:assert>
      <sch:assert test="((pds:data_type eq 'ASCII_LIDVID') and (pds:name eq 'LIDVID')) or ((pds:data_type eq 'ASCII_LID') and (pds:name eq 'LID'))">
        The first field of an Inventory must have name set to 'LIDVID' or 'LID' and data_type set to 'ASCII_LIDVID' or 'ASCII_LID', respectively.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Inventory/pds:Record_Character/pds:Field_Character[2]">
      <sch:assert test="pds:field_number eq '2'">
        The second field of an Inventory must have field_number set to 2.</sch:assert>
      <sch:assert test="pds:data_type eq 'ASCII_File_Specification_Name'">
        The second field of an Inventory must have data type set to 'ASCII_File_Specification_Name'.</sch:assert>
      <sch:assert test="pds:name eq 'File_Specification_Name'">
        The second field of an Inventory must have name set to 'File_Specification_Name'.</sch:assert>
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
    <sch:rule context="pds:Primary_Result_Description">
      <sch:assert test="pds:data_regime = ('Dust', 'Dust_Particle_Counts', 'Electric_Field', 'Electrons', 'Far_Infrared', 'Gamma_Ray', 'Impacts', 'Infrared', 'Ions', 'Magnetic_Field', 'Mass_Spectra', 'Microwave', 'Millimeter', 'Near_Infrared', 'Optical', 'Particles', 'Radio', 'Sub-Millimeter', 'Ultraviolet', 'Visible', 'X-Ray')">
        The attribute pds:data_regime must be equal to one of the following values 'Dust', 'Dust_Particle_Counts', 'Electric_Field', 'Electrons', 'Far_Infrared', 'Gamma_Ray', 'Impacts', 'Infrared', 'Ions', 'Magnetic_Field', 'Mass_Spectra', 'Microwave', 'Millimeter', 'Near_Infrared', 'Optical', 'Particles', 'Radio', 'Sub-Millimeter', 'Ultraviolet', 'Visible', 'X-Ray'.</sch:assert>
      <sch:assert test="pds:type = ('Astrometry', 'B-Field_Vectors', 'Column_Density', 'Comet_Nuclear_Properties', 'Count', 'Count_Rate', 'Data_Number', 'Digital_Elevation_Model', 'E-Field_Vectors', 'Environment', 'Environment::Hardware_Temperature', 'Filter_Curve', 'Flux', 'Flux::Electron', 'Flux::Ion', 'Flux::Photometric', 'Gravity_Model', 'Gravity_Model::Spherical_Harmonics', 'Image', 'Image::Anaglyph', 'Image::Color', 'Image::Filtered', 'Impact_Characteristics', 'Instrument', 'Instrument::Geometry', 'Instrument::Parameters', 'Lightcurve', 'Magnetometry', 'Magnitude', 'Map', 'Map::Radiance', 'Map::Thermal', 'Morphology', 'Null_Result', 'Photometry', 'Photometry::Aperture', 'Photometry::High-Speed', 'Photometry::Magnitude', 'Point_Spread_Function', 'Polarimetry', 'Pressure', 'Production_Rate', 'Pulse_Heights', 'Radiometry', 'Shape_Model', 'Shape_Model::Spherical_Harmonics', 'Shape_Model::Triangular_Plate', 'Spectrometry', 'Spectrophotometry', 'Spectroscopy', 'Spectrum', 'Spectrum::Frequency', 'Spectrum::Grism', 'Spectrum::Image', 'Spectrum::Lines', 'Spectrum::Mass', 'Spectrum::Time-Of-Flight', 'Spin_State')">
        The attribute pds:type must be equal to one of the following values 'Astrometry', 'B-Field_Vectors', 'Column_Density', 'Comet_Nuclear_Properties', 'Count', 'Count_Rate', 'Data_Number', 'Digital_Elevation_Model', 'E-Field_Vectors', 'Environment', 'Environment::Hardware_Temperature', 'Filter_Curve', 'Flux', 'Flux::Electron', 'Flux::Ion', 'Flux::Photometric', 'Gravity_Model', 'Gravity_Model::Spherical_Harmonics', 'Image', 'Image::Anaglyph', 'Image::Color', 'Image::Filtered', 'Impact_Characteristics', 'Instrument', 'Instrument::Geometry', 'Instrument::Parameters', 'Lightcurve', 'Magnetometry', 'Magnitude', 'Map', 'Map::Radiance', 'Map::Thermal', 'Morphology', 'Null_Result', 'Photometry', 'Photometry::Aperture', 'Photometry::High-Speed', 'Photometry::Magnitude', 'Point_Spread_Function', 'Polarimetry', 'Pressure', 'Production_Rate', 'Pulse_Heights', 'Radiometry', 'Shape_Model', 'Shape_Model::Spherical_Harmonics', 'Shape_Model::Triangular_Plate', 'Spectrometry', 'Spectrophotometry', 'Spectroscopy', 'Spectrum', 'Spectrum::Frequency', 'Spectrum::Grism', 'Spectrum::Image', 'Spectrum::Lines', 'Spectrum::Mass', 'Spectrum::Time-Of-Flight', 'Spin_State'.</sch:assert>
      <sch:assert test="pds:purpose = ('Calibration', 'Checkout', 'Engineering', 'Navigation', 'Science')">
        The attribute pds:purpose must be equal to one of the following values 'Calibration', 'Checkout', 'Engineering', 'Navigation', 'Science'.</sch:assert>
      <sch:assert test="pds:reduction_level = ('Calibrated', 'Derived', 'Raw', 'Reduced')">
        The attribute pds:reduction_level must be equal to one of the following values 'Calibrated', 'Derived', 'Raw', 'Reduced'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Product_Bundle/pds:Reference_List/pds:Internal_Reference">
      <sch:assert test="every $ref in (pds:reference_type) satisfies $ref = ('bundle_to_errata')">
        The attribute reference_type must be set to one of the following values 'bundle_to_errata'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Product_Collection/pds:Reference_List/pds:Internal_Reference">
      <sch:assert test="every $ref in (pds:reference_type) satisfies $ref = ('data_collection_to_resource', 'data_collection_to_associate', 'data_collection_to_calibration', 'data_collection_to_geometry', 'data_collection_to_spice_kernel', 'data_collection_curated_by_node', 'data_collection_to_document', 'data_collection_to_browse', 'data_collection_to_context', 'data_collection_to_data', 'data_collection_to_document', 'data_collection_to_schema', 'data_collection_to_errata')">
        The attribute reference_type must be set to one of the following values 'data_collection_to_resource', 'data_collection_to_associate', 'data_collection_to_calibration', 'data_collection_to_geometry', 'data_collection_to_spice_kernel', 'data_collection_curated_by_node', 'data_collection_to_document', 'data_collection_to_browse', 'data_collection_to_context', 'data_collection_to_data', 'data_collection_to_document', 'data_collection_to_schema', 'data_collection_to_errata'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Product_Context/pds:Reference_List/pds:Internal_Reference">
      <sch:assert test="every $ref in (pds:reference_type) satisfies $ref = ('context_to_resource', 'context_to_associate', 'context_to_document', 'instrument_host_to_investigation', 'instrument_to_instrument_host', 'investigation_to_target', 'node_to_personnel', 'instrument_host_to_target')">
        The attribute reference_type must be set to one of the following values 'context_to_resource', 'context_to_associate', 'context_to_document', 'instrument_host_to_investigation', 'instrument_to_instrument_host', 'investigation_to_target', 'node_to_personnel', 'instrument_host_to_target'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Product_Delivery_Manifest/pds:Reference_List/pds:Internal_Reference">
      <sch:assert test="every $ref in (pds:reference_type) satisfies $ref = ('delivery_manifest_to_collection', 'delivery_manifest_to_bundle')">
        The attribute reference_type must be set to one of the following values 'delivery_manifest_to_collection', 'delivery_manifest_to_bundle'.</sch:assert>
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
      <sch:assert test="every $ref in (pds:reference_type) satisfies $ref = ('data_to_resource', 'data_to_calibration', 'data_to_geometry', 'data_to_spice_kernel', 'data_to_thumbnail', 'data_to_document', 'data_curated_by_node', 'data_to_browse', 'bundle_to_document', 'collection_to_document')">
        The attribute reference_type must be set to one of the following values 'data_to_resource', 'data_to_calibration', 'data_to_geometry', 'data_to_spice_kernel', 'data_to_thumbnail', 'data_to_document', 'data_curated_by_node', 'data_to_browse', 'bundle_to_document', 'collection_to_document'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Product_Operational/pds:Reference_List/pds:Internal_Reference">
      <sch:assert test="every $ref in (pds:reference_type) satisfies $ref = ('operational_to_resource', 'operational_to_associate', 'operational_to_document')">
        The attribute reference_type must be set to one of the following values 'operational_to_resource', 'operational_to_associate', 'operational_to_document'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Product_Zipped">
      <sch:assert test="pds:container_type = ('GZIP', 'LZIP', 'TAR', 'ZIP')">
        The attribute pds:container_type must be equal to one of the following values 'GZIP', 'LZIP', 'TAR', 'ZIP'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Quaternion">
      <sch:assert test="pds:type = ('type1', 'type2')">
        The attribute pds:type must be equal to one of the following values 'type1', 'type2'.</sch:assert>
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
      <sch:assert test="pds:record_delimiter = ('carriage_return', 'carriage_return line_feed', 'line_feed')">
        The attribute pds:record_delimiter must be equal to one of the following values 'carriage_return', 'carriage_return line_feed', 'line_feed'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Table_Delimited">
      <sch:assert test="pds:encoding_type = ('Character')">
        The attribute pds:encoding_type must be equal to the value 'Character'.</sch:assert>
      <sch:assert test="pds:record_delimiter = ('carriage_return', 'carriage_return line_feed', 'line_feed')">
        The attribute pds:record_delimiter must be equal to one of the following values 'carriage_return', 'carriage_return line_feed', 'line_feed'.</sch:assert>
      <sch:assert test="pds:external_standard_id = ('PDS_CSV')">
        The attribute pds:external_standard_id must be equal to the value 'PDS_CSV'.</sch:assert>
      <sch:assert test="pds:field_delimiter = ('comma', 'horizontal_tab', 'semicolon', 'verticle_bar')">
        The attribute pds:field_delimiter must be equal to one of the following values 'comma', 'horizontal_tab', 'semicolon', 'verticle_bar'.</sch:assert>
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
    <sch:rule context="pds:Terminological_Entry">
      <sch:assert test="pds:language = ('English')">
        The attribute pds:language must be equal to the value 'English'.</sch:assert>
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
      <sch:assert test="pds:encoding_type = ('Character')">
        The attribute pds:encoding_type must be equal to the value 'Character'.</sch:assert>
      <sch:assert test="pds:external_standard_id = ('Schematron', 'XML_Catalog', 'XML_Schema')">
        The attribute pds:external_standard_id must be equal to one of the following values 'Schematron', 'XML_Catalog', 'XML_Schema'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Zipped_Member_Entry">
      <sch:assert test="pds:reference_type = ('package_has_bundle', 'package_has_collection', 'package_has_delivery_manifest')">
        The attribute pds:reference_type must be equal to one of the following values 'package_has_bundle', 'package_has_collection', 'package_has_delivery_manifest'.</sch:assert>
    </sch:rule>
  </sch:pattern>
</sch:schema>
