<?xml version="1.0" encoding="UTF-8"?>
  <!-- PDS4 Schematron for PDS4_0.7.0.0.j  Wed Apr 11 09:21:23 PDT 2012 -->
  <!-- Generated from the PDS4 Information Model V0.7.0.0.j -->
  <!-- *** This PDS4 schematron file is a preliminary deliverable. *** -->
  <!-- *** It is being made available for review and testing. *** -->
<sch:schema xmlns:sch="http://purl.oclc.org/dsdl/schematron" queryBinding="xslt2">

  <sch:title>Sample Schematron using XPath 2.0</sch:title>

  <sch:ns uri="http://pds.nasa.gov/schema/pds4/pds/v07" prefix="pds"/>

		   <!-- =================================================== -->
		   <!-- NOTE:  There are two sections of schematron rules.  -->
		   <!--        The first section includes rules             -->
		   <!--        written for specific situations.             -->
		   <!--        The second section includes rules            -->
		   <!--        generated from enumerated lists.             -->
		   <!-- =================================================== -->

		   <!-- =================================================== -->
		   <!-- NOTE:  The following section includes rules         -->
		   <!--        written for specific situations.             -->
		   <!-- =================================================== -->

  <sch:pattern>
    <sch:rule context="pds:Array_2D_Image/pds:Axis_Array[1]">
      <sch:assert test="pds:name = ('LINE', 'SAMPLE')"/>
      <sch:assert test="pds:sequence_number eq '1'"/>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Array_2D_Image/pds:Axis_Array[2]">
      <sch:assert test="pds:name = ('LINE', 'SAMPLE')"/>
      <sch:assert test="pds:sequence_number eq '2'"/>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Delivery_Manifest">
      <sch:assert test="pds:offset eq '0'"/>
      <sch:assert test="pds:encoding_type eq 'CHARACTER'"/>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Delivery_Manifest/pds:Record_Character/pds:Field_Character[1]">
      <sch:assert test="pds:field_number eq '1'"/>
      <sch:assert test="pds:field_location eq '1'"/>
      <sch:assert test="pds:data_type eq 'ASCII_MD5_Checksum'"/>
      <sch:assert test="pds:name eq 'MD5_Checksum'"/>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Delivery_Manifest/pds:Record_Character/pds:Field_Character[2]">
      <sch:assert test="pds:field_number eq '2'"/>
      <sch:assert test="pds:field_location eq '17'"/>
      <sch:assert test="pds:data_type eq 'ASCII_File_Specification_Name'"/>
      <sch:assert test="pds:name eq 'File_Specification_Name'"/>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Identification_Area">
      <sch:assert test="pds:information_model_version='0.7.0.0.j'">
        The value of information_model_version must be set to one of the following values '0.7.0.0.j'</sch:assert>
      <sch:assert test="starts-with(pds:logical_identifier,'urn:nasa:pds:')">
        The value of the attribute logical_identifier must start with 'urn:nasa:pds:'</sch:assert>
      <sch:assert test="every $ref in (pds:product_class) satisfies $ref = ('Product_Archive_Bundle', 'Product_Browse', 'Product_Collection', 'Product_Document', 'Product_Observational', 'Product_SPICE_Kernel', 'Product_Thumbnail', 'Product_Update')">
        The attribute reference_type must be set to one of the following values 'Product_Archive_Bundle', 'Product_Browse', 'Product_Collection', 'Product_Document', 'Product_Observational', 'Product_SPICE_Kernel', 'Product_Thumbnail', 'Product_Update'</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Inventory_LIDVID_Primary/pds:Record_Character/pds:Field_Character[2]">
      <sch:assert test="pds:field_number eq '2'"/>
      <sch:assert test="pds:data_type eq 'ASCII_File_Specification_Name'"/>
      <sch:assert test="pds:name eq 'File_Specification_Name'"/>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Inventory_LIDVID_Secondary/pds:Record_Character/pds:Field_Character[1]">
      <sch:assert test="pds:field_number eq '1'"/>
      <sch:assert test="pds:field_location  eq '1'"/>
      <sch:assert test="pds:data_type eq 'ASCII_LIDVID'"/>
      <sch:assert test="pds:name eq 'LIDVID'"/>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Inventory_LID_Secondary/pds:Record_Character/pds:Field_Character[1]">
      <sch:assert test="pds:field_number eq '1'"/>
      <sch:assert test="pds:field_location  eq '1'"/>
      <sch:assert test="pds:data_type eq 'ASCII_LID'"/>
      <sch:assert test="pds:name eq 'LID'"/>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Investigation_Area/pds:Internal_Reference">
      <sch:assert test="if (pds:reference_type) then pds:reference_type = ('has_investigation') else true ()">
        The attribute reference_type must be set to 'has_investigation'.</sch:assert>
      <sch:assert test="starts-with(pds:logical_identifier,'urn:nasa:pds:')">
        The value of the attribute logical_identifier must start with 'urn:nasa:pds:'</sch:assert>
      <sch:assert test="if (pds:lidvid_reference) then contains(pds:lidvid_reference,'::') else true()">
        The value of the attribute lidvid_reference must include a value that contains '::' followed by version id</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Observing_System_Component/pds:Internal_Reference">
      <sch:assert test="every $ref in (pds:reference_type) satisfies $ref = ('has_instrument', 'has_instrument_host', 'has_detector')">
        The attribute reference_type must be set to one of the following values 'has_instrument', 'has_instrument_host', 'has_detector'.</sch:assert>
      <sch:assert test="starts-with(pds:logical_identifier,'urn:nasa:pds:')">
        The value of the attribute logical_identifier must start with 'urn:nasa:pds:'</sch:assert>
      <sch:assert test="if (pds:lidvid_reference) then contains(pds:lidvid_reference,'::') else true()">
        The value of the attribute lidvid_reference must include a value that contains '::' followed by version id</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Product_Collection/pds:Reference_List/pds:Internal_Reference">
      <sch:assert test="every $ref in (pds:reference_type) satisfies $ref = ('has_publication', 'has_resource', 'has_association', 'has_calibration', 'has_geometry', 'has_spice', 'curated_by_node', 'has_node', 'has_document', 'has_investigation', 'has_instrument_host', 'has_instrument', 'has_target')">
        The attribute reference_type must be set to one of the following values 'has_publication', 'has_resource', 'has_association', 'has_calibration', 'has_geometry', 'has_spice', 'curated_by_node', 'has_node', 'has_document', 'has_investigation', 'has_instrument_host', 'has_instrument', 'has_target'.</sch:assert>
      <sch:assert test="starts-with(pds:logical_identifier,'urn:nasa:pds:')">
        The value of the attribute logical_identifier must start with 'urn:nasa:pds:'</sch:assert>
      <sch:assert test="if (pds:lidvid_reference) then contains(pds:lidvid_reference,'::') else true()">
        The value of the attribute lidvid_reference must include a value that contains '::' followed by version id</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Product_Context/pds:Reference_List/pds:Internal_Reference">
      <sch:assert test="every $ref in (pds:reference_type) satisfies $ref = ('has_publication', 'has_resource', 'has_association', 'has_document', 'has_investigation', 'has_instrument_host', 'has_instrument', 'has_target')">
        The attribute reference_type must be set to one of the following values 'has_publication', 'has_resource', 'has_association', 'has_document', 'has_investigation', 'has_instrument_host', 'has_instrument', 'has_target'.</sch:assert>
      <sch:assert test="starts-with(pds:logical_identifier,'urn:nasa:pds:')">
        The value of the attribute logical_identifier must start with 'urn:nasa:pds:'</sch:assert>
      <sch:assert test="if (pds:lidvid_reference) then contains(pds:lidvid_reference,'::') else true()">
        The value of the attribute lidvid_reference must include a value that contains '::' followed by version id</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Product_Document/pds:Reference_List/pds:Internal_Reference">
      <sch:assert test="every $ref in (pds:reference_type) satisfies $ref = ('has_association', 'has_investigation', 'has_instrument_host', 'has_instrument', 'has_target')">
        The attribute reference_type must be set to one of the following values 'has_association', 'has_investigation', 'has_instrument_host', 'has_instrument', 'has_target'.</sch:assert>
      <sch:assert test="starts-with(pds:logical_identifier,'urn:nasa:pds:')">
        The value of the attribute logical_identifier must start with 'urn:nasa:pds:'</sch:assert>
      <sch:assert test="if (pds:lidvid_reference) then contains(pds:lidvid_reference,'::') else true()">
        The value of the attribute lidvid_reference must include a value that contains '::' followed by version id</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Product_Observational/pds:Reference_List/pds:Internal_Reference">
      <sch:assert test="every $ref in (pds:reference_type) satisfies $ref = ('has_publication', 'has_resource', 'has_association', 'has_calibration', 'has_geometry', 'has_spice', 'has_thumbnail', 'has_node', 'has_document', 'has_primary_collection', 'has_investigation', 'has_instrument_host', 'has_instrument', 'has_target')">
        The attribute reference_type must be set to one of the following values 'has_publication', 'has_resource', 'has_association', 'has_calibration', 'has_geometry', 'has_spice', 'has_thumbnail', 'has_node', 'has_document', 'has_primary_collection', 'has_investigation', 'has_instrument_host', 'has_instrument', 'has_target'.</sch:assert>
      <sch:assert test="starts-with(pds:logical_identifier,'urn:nasa:pds:')">
        The value of the attribute logical_identifier must start with 'urn:nasa:pds:'</sch:assert>
      <sch:assert test="if (pds:lidvid_reference) then contains(pds:lidvid_reference,'::') else true()">
        The value of the attribute lidvid_reference must include a value that contains '::' followed by version id</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Product_Operational/pds:Reference_List/pds:Internal_Reference">
      <sch:assert test="every $ref in (pds:reference_type) satisfies $ref = ('has_resource', 'has_association', 'has_document')">
        The attribute reference_type must be set to one of the following values 'has_resource', 'has_association', 'has_document'.</sch:assert>
      <sch:assert test="starts-with(pds:logical_identifier,'urn:nasa:pds:')">
        The value of the attribute logical_identifier must start with 'urn:nasa:pds:'</sch:assert>
      <sch:assert test="if (pds:lidvid_reference) then contains(pds:lidvid_reference,'::') else true()">
        The value of the attribute lidvid_reference must include a value that contains '::' followed by version id</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Target_Identification/pds:Internal_Reference">
      <sch:assert test="if (pds:reference_type) then pds:reference_type = ('has_target') else true ()">
        The attribute reference_type must be set to 'has_target'.</sch:assert>
      <sch:assert test="starts-with(pds:logical_identifier,'urn:nasa:pds:')">
        The value of the attribute logical_identifier must start with 'urn:nasa:pds:'</sch:assert>
      <sch:assert test="if (pds:lidvid_reference) then contains(pds:lidvid_reference,'::') else true()">
        The value of the attribute lidvid_reference must include a value that contains '::' followed by version id</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="pds:Update_Entry/pds:Internal_Reference">
      <sch:assert test="if (pds:reference_type) then pds:reference_type = ('has_update') else true ()">
        The attribute reference_type must be set to 'has_update'.</sch:assert>
      <sch:assert test="starts-with(pds:logical_identifier,'urn:nasa:pds:')">
        The value of the attribute logical_identifier must start with 'urn:nasa:pds:'</sch:assert>
      <sch:assert test="if (pds:lidvid_reference) then contains(pds:lidvid_reference,'::') else true()">
        The value of the attribute lidvid_reference must include a value that contains '::' followed by version id</sch:assert>
    </sch:rule>
  </sch:pattern>

		   <!-- =================================================== -->
		   <!-- NOTE:  The following section includes rules         -->
		   <!--        generated from enumerated lists.             -->
		   <!-- =================================================== -->

  <sch:pattern>
    <sch:rule context="Array">
      <sch:assert test="pds:encoding_type = ('BINARY')">
        The attribute encoding_type must be equal to 'BINARY'.</sch:assert>
      <sch:assert test="pds:axis_index_order = ('LAST_INDEX_FASTEST')">
        The attribute axis_index_order must be equal to 'LAST_INDEX_FASTEST'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="Array_2D">
      <sch:assert test="pds:axes = ('2')">
        The attribute axes must be equal to '2'.</sch:assert>
      <sch:assert test="pds:encoding_type = ('BINARY')">
        The attribute encoding_type must be equal to 'BINARY'.</sch:assert>
      <sch:assert test="pds:axis_index_order = ('LAST_INDEX_FASTEST')">
        The attribute axis_index_order must be equal to 'LAST_INDEX_FASTEST'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="Array_2D_Image">
      <sch:assert test="pds:axes = ('2')">
        The attribute axes must be equal to '2'.</sch:assert>
      <sch:assert test="pds:encoding_type = ('BINARY')">
        The attribute encoding_type must be equal to 'BINARY'.</sch:assert>
      <sch:assert test="pds:axis_index_order = ('LAST_INDEX_FASTEST')">
        The attribute axis_index_order must be equal to 'LAST_INDEX_FASTEST'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="Array_2D_Map">
      <sch:assert test="pds:axes = ('2')">
        The attribute axes must be equal to '2'.</sch:assert>
      <sch:assert test="pds:encoding_type = ('BINARY')">
        The attribute encoding_type must be equal to 'BINARY'.</sch:assert>
      <sch:assert test="pds:axis_index_order = ('LAST_INDEX_FASTEST')">
        The attribute axis_index_order must be equal to 'LAST_INDEX_FASTEST'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="Array_2D_Spectrum">
      <sch:assert test="pds:axes = ('2')">
        The attribute axes must be equal to '2'.</sch:assert>
      <sch:assert test="pds:encoding_type = ('BINARY')">
        The attribute encoding_type must be equal to 'BINARY'.</sch:assert>
      <sch:assert test="pds:axis_index_order = ('LAST_INDEX_FASTEST')">
        The attribute axis_index_order must be equal to 'LAST_INDEX_FASTEST'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="Array_3D">
      <sch:assert test="pds:axes = ('3')">
        The attribute axes must be equal to '3'.</sch:assert>
      <sch:assert test="pds:encoding_type = ('BINARY')">
        The attribute encoding_type must be equal to 'BINARY'.</sch:assert>
      <sch:assert test="pds:axis_index_order = ('LAST_INDEX_FASTEST')">
        The attribute axis_index_order must be equal to 'LAST_INDEX_FASTEST'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="Array_3D_Image">
      <sch:assert test="pds:axes = ('3')">
        The attribute axes must be equal to '3'.</sch:assert>
      <sch:assert test="pds:encoding_type = ('BINARY')">
        The attribute encoding_type must be equal to 'BINARY'.</sch:assert>
      <sch:assert test="pds:axis_index_order = ('LAST_INDEX_FASTEST')">
        The attribute axis_index_order must be equal to 'LAST_INDEX_FASTEST'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="Array_3D_Movie">
      <sch:assert test="pds:axes = ('3')">
        The attribute axes must be equal to '3'.</sch:assert>
      <sch:assert test="pds:encoding_type = ('BINARY')">
        The attribute encoding_type must be equal to 'BINARY'.</sch:assert>
      <sch:assert test="pds:axis_index_order = ('LAST_INDEX_FASTEST')">
        The attribute axis_index_order must be equal to 'LAST_INDEX_FASTEST'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="Array_3D_Spectrum">
      <sch:assert test="pds:axes = ('3')">
        The attribute axes must be equal to '3'.</sch:assert>
      <sch:assert test="pds:encoding_type = ('BINARY')">
        The attribute encoding_type must be equal to 'BINARY'.</sch:assert>
      <sch:assert test="pds:axis_index_order = ('LAST_INDEX_FASTEST')">
        The attribute axis_index_order must be equal to 'LAST_INDEX_FASTEST'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="Bundle">
      <sch:assert test="pds:bundle_type = ('Archive', 'Supplemental')">
        The attribute bundle_type must be equal to one of the following values 'Archive', 'Supplemental'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="Bundle_Member_Entry">
      <sch:assert test="pds:reference_type = ('has_browse_collection', 'has_calibration_collection', 'has_context_collection', 'has_data_collection', 'has_document_collection', 'has_geometry_collection', 'has_member_collection', 'has_schema_collection', 'has_spice_kernels_collection')">
        The attribute reference_type must be equal to one of the following values 'has_browse_collection', 'has_calibration_collection', 'has_context_collection', 'has_data_collection', 'has_document_collection', 'has_geometry_collection', 'has_member_collection', 'has_schema_collection', 'has_spice_kernels_collection'.</sch:assert>
      <sch:assert test="pds:member_status = ('Primary', 'Secondary_No_File', 'Secondary_With_File')">
        The attribute member_status must be equal to one of the following values 'Primary', 'Secondary_No_File', 'Secondary_With_File'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="Collection">
      <sch:assert test="pds:collection_type = ('Browse', 'Calibration', 'Context', 'Data', 'Document', 'Geometry', 'Miscellaneous', 'SPICE', 'Schema')">
        The attribute collection_type must be equal to one of the following values 'Browse', 'Calibration', 'Context', 'Data', 'Document', 'Geometry', 'Miscellaneous', 'SPICE', 'Schema'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="DD_Association">
      <sch:assert test="pds:reference_type = ('attribute_of', 'component_of', 'extension_of', 'restriction_of', 'subclass_of')">
        The attribute reference_type must be equal to one of the following values 'attribute_of', 'component_of', 'extension_of', 'restriction_of', 'subclass_of'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="DD_Attribute">
      <sch:assert test="pds:steward_id = ('atm', 'geo', 'img', 'naif', 'ops', 'pds', 'ppi', 'rings', 'rs', 'sbn')">
        The attribute steward_id must be equal to one of the following values 'atm', 'geo', 'img', 'naif', 'ops', 'pds', 'ppi', 'rings', 'rs', 'sbn'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="DD_Attribute_Full">
      <sch:assert test="pds:attribute_concept = ('ADDRESS', 'ANGLE', 'ATTRIBUTE', 'BIT', 'CHECKSUM', 'COLLECTION', 'CONSTANT', 'COSINE', 'COUNT', 'DELIMITER', 'DESCRIPTION', 'DEVIATION', 'DIRECTION', 'DISTANCE', 'DOI', 'DURATION', 'FACTOR', 'FLAG', 'FORMAT', 'GROUP', 'HOME', 'ID', 'LATITUDE', 'LENGTH', 'LIST', 'LOCATION', 'LOGICAL', 'LONGITUDE', 'MASK', 'MAXIMUM', 'MEAN', 'MEDIAN', 'MINIMUM', 'NAME', 'NOTE', 'NUMBER', 'OFFSET', 'ORDER', 'PARALLEL', 'PASSWORD', 'PATH', 'PATTERN', 'PIXEL', 'QUATERNION', 'RADIUS', 'RATIO', 'REFERENCE', 'RESOLUTION', 'ROLE', 'ROTATION', 'SCALE', 'SEQUENCE', 'SET', 'SIZE', 'STATUS', 'SUMMARY', 'SYNTAX', 'TEMPERATURE', 'TEXT', 'TITLE', 'TYPE', 'UNIT', 'UNKNOWN', 'VALUE', 'VECTOR')">
        The attribute attribute_concept must be equal to one of the following values 'ADDRESS', 'ANGLE', 'ATTRIBUTE', 'BIT', 'CHECKSUM', 'COLLECTION', 'CONSTANT', 'COSINE', 'COUNT', 'DELIMITER', 'DESCRIPTION', 'DEVIATION', 'DIRECTION', 'DISTANCE', 'DOI', 'DURATION', 'FACTOR', 'FLAG', 'FORMAT', 'GROUP', 'HOME', 'ID', 'LATITUDE', 'LENGTH', 'LIST', 'LOCATION', 'LOGICAL', 'LONGITUDE', 'MASK', 'MAXIMUM', 'MEAN', 'MEDIAN', 'MINIMUM', 'NAME', 'NOTE', 'NUMBER', 'OFFSET', 'ORDER', 'PARALLEL', 'PASSWORD', 'PATH', 'PATTERN', 'PIXEL', 'QUATERNION', 'RADIUS', 'RATIO', 'REFERENCE', 'RESOLUTION', 'ROLE', 'ROTATION', 'SCALE', 'SEQUENCE', 'SET', 'SIZE', 'STATUS', 'SUMMARY', 'SYNTAX', 'TEMPERATURE', 'TEXT', 'TITLE', 'TYPE', 'UNIT', 'UNKNOWN', 'VALUE', 'VECTOR'.</sch:assert>
      <sch:assert test="pds:registration_authority_id = ('0001_NASA_PDS_1')">
        The attribute registration_authority_id must be equal to '0001_NASA_PDS_1'.</sch:assert>
      <sch:assert test="pds:steward_id = ('atm', 'geo', 'img', 'naif', 'ops', 'pds', 'ppi', 'rings', 'rs', 'sbn')">
        The attribute steward_id must be equal to one of the following values 'atm', 'geo', 'img', 'naif', 'ops', 'pds', 'ppi', 'rings', 'rs', 'sbn'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="DD_Class">
      <sch:assert test="pds:steward_id = ('atm', 'geo', 'img', 'naif', 'ops', 'pds', 'ppi', 'rings', 'rs', 'sbn')">
        The attribute steward_id must be equal to one of the following values 'atm', 'geo', 'img', 'naif', 'ops', 'pds', 'ppi', 'rings', 'rs', 'sbn'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="DD_Value_Domain">
      <sch:assert test="pds:unit_of_measure_type = ('UnitOfMeasure_AmountOfSubstance', 'UnitOfMeasure_Angle', 'UnitOfMeasure_AngularVelocity', 'UnitOfMeasure_Area', 'UnitOfMeasure_Frequency', 'UnitOfMeasure_Length', 'UnitOfMeasure_MapScale', 'UnitOfMeasure_Mass', 'UnitOfMeasure_Misc', 'UnitOfMeasure_None', 'UnitOfMeasure_OpticalPathLength', 'UnitOfMeasure_Pressure', 'UnitOfMeasure_Radiance', 'UnitOfMeasure_Rates', 'UnitOfMeasure_SolidAngle', 'UnitOfMeasure_Storage', 'UnitOfMeasure_Temperature', 'UnitOfMeasure_Time', 'UnitOfMeasure_Velocity', 'UnitOfMeasure_Voltage', 'UnitOfMeasure_Volume')">
        The attribute unit_of_measure_type must be equal to one of the following values 'UnitOfMeasure_AmountOfSubstance', 'UnitOfMeasure_Angle', 'UnitOfMeasure_AngularVelocity', 'UnitOfMeasure_Area', 'UnitOfMeasure_Frequency', 'UnitOfMeasure_Length', 'UnitOfMeasure_MapScale', 'UnitOfMeasure_Mass', 'UnitOfMeasure_Misc', 'UnitOfMeasure_None', 'UnitOfMeasure_OpticalPathLength', 'UnitOfMeasure_Pressure', 'UnitOfMeasure_Radiance', 'UnitOfMeasure_Rates', 'UnitOfMeasure_SolidAngle', 'UnitOfMeasure_Storage', 'UnitOfMeasure_Temperature', 'UnitOfMeasure_Time', 'UnitOfMeasure_Velocity', 'UnitOfMeasure_Voltage', 'UnitOfMeasure_Volume'.</sch:assert>
      <sch:assert test="pds:value_data_type = ('ASCII_AnyURI', 'ASCII_Boolean', 'ASCII_DOI', 'ASCII_Date_DOY', 'ASCII_Date_Time_UTC', 'ASCII_Date_Time_YMD', 'ASCII_Date_YMD', 'ASCII_File_Specification_Name', 'ASCII_Integer', 'ASCII_LID', 'ASCII_LIDVID', 'ASCII_MD5_Checksum', 'ASCII_NonNegative_Integer', 'ASCII_Numeric_Base16', 'ASCII_Numeric_Base2', 'ASCII_Real', 'ASCII_Short_String_Collapsed', 'ASCII_Short_String_Preserved', 'ASCII_Text_Preserved', 'ASCII_Time', 'ASCII_VID')">
        The attribute value_data_type must be equal to one of the following values 'ASCII_AnyURI', 'ASCII_Boolean', 'ASCII_DOI', 'ASCII_Date_DOY', 'ASCII_Date_Time_UTC', 'ASCII_Date_Time_YMD', 'ASCII_Date_YMD', 'ASCII_File_Specification_Name', 'ASCII_Integer', 'ASCII_LID', 'ASCII_LIDVID', 'ASCII_MD5_Checksum', 'ASCII_NonNegative_Integer', 'ASCII_Numeric_Base16', 'ASCII_Numeric_Base2', 'ASCII_Real', 'ASCII_Short_String_Collapsed', 'ASCII_Short_String_Preserved', 'ASCII_Text_Preserved', 'ASCII_Time', 'ASCII_VID'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="DD_Value_Domain_Full">
      <sch:assert test="pds:conceptual_domain = ('BOOLEAN', 'INTEGER', 'NAME', 'NUMERIC', 'REAL', 'SHORT_STRING', 'TEXT', 'TIME', 'TYPE', 'UNKNOWN')">
        The attribute conceptual_domain must be equal to one of the following values 'BOOLEAN', 'INTEGER', 'NAME', 'NUMERIC', 'REAL', 'SHORT_STRING', 'TEXT', 'TIME', 'TYPE', 'UNKNOWN'.</sch:assert>
      <sch:assert test="pds:unit_of_measure_type = ('UnitOfMeasure_AmountOfSubstance', 'UnitOfMeasure_Angle', 'UnitOfMeasure_AngularVelocity', 'UnitOfMeasure_Area', 'UnitOfMeasure_Frequency', 'UnitOfMeasure_Length', 'UnitOfMeasure_MapScale', 'UnitOfMeasure_Mass', 'UnitOfMeasure_Misc', 'UnitOfMeasure_None', 'UnitOfMeasure_OpticalPathLength', 'UnitOfMeasure_Pressure', 'UnitOfMeasure_Radiance', 'UnitOfMeasure_Rates', 'UnitOfMeasure_SolidAngle', 'UnitOfMeasure_Storage', 'UnitOfMeasure_Temperature', 'UnitOfMeasure_Time', 'UnitOfMeasure_Velocity', 'UnitOfMeasure_Voltage', 'UnitOfMeasure_Volume')">
        The attribute unit_of_measure_type must be equal to one of the following values 'UnitOfMeasure_AmountOfSubstance', 'UnitOfMeasure_Angle', 'UnitOfMeasure_AngularVelocity', 'UnitOfMeasure_Area', 'UnitOfMeasure_Frequency', 'UnitOfMeasure_Length', 'UnitOfMeasure_MapScale', 'UnitOfMeasure_Mass', 'UnitOfMeasure_Misc', 'UnitOfMeasure_None', 'UnitOfMeasure_OpticalPathLength', 'UnitOfMeasure_Pressure', 'UnitOfMeasure_Radiance', 'UnitOfMeasure_Rates', 'UnitOfMeasure_SolidAngle', 'UnitOfMeasure_Storage', 'UnitOfMeasure_Temperature', 'UnitOfMeasure_Time', 'UnitOfMeasure_Velocity', 'UnitOfMeasure_Voltage', 'UnitOfMeasure_Volume'.</sch:assert>
      <sch:assert test="pds:value_data_type = ('ASCII_AnyURI', 'ASCII_Boolean', 'ASCII_DOI', 'ASCII_Date_DOY', 'ASCII_Date_Time_UTC', 'ASCII_Date_Time_YMD', 'ASCII_Date_YMD', 'ASCII_File_Specification_Name', 'ASCII_Integer', 'ASCII_LID', 'ASCII_LIDVID', 'ASCII_MD5_Checksum', 'ASCII_NonNegative_Integer', 'ASCII_Numeric_Base16', 'ASCII_Numeric_Base2', 'ASCII_Real', 'ASCII_Short_String_Collapsed', 'ASCII_Short_String_Preserved', 'ASCII_Text_Preserved', 'ASCII_Time', 'ASCII_VID')">
        The attribute value_data_type must be equal to one of the following values 'ASCII_AnyURI', 'ASCII_Boolean', 'ASCII_DOI', 'ASCII_Date_DOY', 'ASCII_Date_Time_UTC', 'ASCII_Date_Time_YMD', 'ASCII_Date_YMD', 'ASCII_File_Specification_Name', 'ASCII_Integer', 'ASCII_LID', 'ASCII_LIDVID', 'ASCII_MD5_Checksum', 'ASCII_NonNegative_Integer', 'ASCII_Numeric_Base16', 'ASCII_Numeric_Base2', 'ASCII_Real', 'ASCII_Short_String_Collapsed', 'ASCII_Short_String_Preserved', 'ASCII_Text_Preserved', 'ASCII_Time', 'ASCII_VID'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="Data_Set_PDS3">
      <sch:assert test="pds:archive_status = ('ARCHIVED', 'ARCHIVED_ACCUMULATING', 'IN_LIEN_RESOLUTION', 'IN_LIEN_RESOLUTION_ACCUMULATING', 'IN_PEER_REVIEW', 'IN_PEER_REVIEW_ACCUMULATING', 'IN_QUEUE', 'IN_QUEUE_ACCUMULATING', 'LOCALLY_ARCHIVED', 'LOCALLY_ARCHIVED_ACCUMULATING', 'PRE_PEER_REVIEW', 'PRE_PEER_REVIEW_ACCUMULATING', 'SAFED', 'SUPERSEDED')">
        The attribute archive_status must be equal to one of the following values 'ARCHIVED', 'ARCHIVED_ACCUMULATING', 'IN_LIEN_RESOLUTION', 'IN_LIEN_RESOLUTION_ACCUMULATING', 'IN_PEER_REVIEW', 'IN_PEER_REVIEW_ACCUMULATING', 'IN_QUEUE', 'IN_QUEUE_ACCUMULATING', 'LOCALLY_ARCHIVED', 'LOCALLY_ARCHIVED_ACCUMULATING', 'PRE_PEER_REVIEW', 'PRE_PEER_REVIEW_ACCUMULATING', 'SAFED', 'SUPERSEDED'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="Delivery_Manifest">
      <sch:assert test="pds:encoding_type = ('CHARACTER')">
        The attribute encoding_type must be equal to 'CHARACTER'.</sch:assert>
      <sch:assert test="pds:reference_type = ('has_member')">
        The attribute reference_type must be equal to 'has_member'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="Display_2D_Image">
      <sch:assert test="pds:line_display_direction = ('DOWN', 'UP')">
        The attribute line_display_direction must be equal to one of the following values 'DOWN', 'UP'.</sch:assert>
      <sch:assert test="pds:sample_display_direction = ('RIGHT')">
        The attribute sample_display_direction must be equal to 'RIGHT'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="Document_File">
      <sch:assert test="pds:encoding_type = ('BINARY', 'CHARACTER')">
        The attribute encoding_type must be equal to one of the following values 'BINARY', 'CHARACTER'.</sch:assert>
      <sch:assert test="pds:external_standard_id = ('ENCAPSULATED_POSTSCRIPT', 'GIF', 'HTML', 'JPG', 'LaTEX', 'MICROSOFT_WORD', 'PDF', 'PDF-A', 'PNG', 'POSTSCRIPT', 'RICH_TEXT', 'TEXT', 'TIFF')">
        The attribute external_standard_id must be equal to one of the following values 'ENCAPSULATED_POSTSCRIPT', 'GIF', 'HTML', 'JPG', 'LaTEX', 'MICROSOFT_WORD', 'PDF', 'PDF-A', 'PNG', 'POSTSCRIPT', 'RICH_TEXT', 'TEXT', 'TIFF'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="Document_Format">
      <sch:assert test="pds:format_type = ('HTML', 'PDF', 'PDF-A', 'TEXT')">
        The attribute format_type must be equal to one of the following values 'HTML', 'PDF', 'PDF-A', 'TEXT'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="Element_Array">
      <sch:assert test="pds:data_type = ('IEEE754LSBDouble', 'IEEE754LSBSingle', 'IEEE754MSBDouble', 'IEEE754MSBSingle', 'SignedByte', 'SignedLSB2', 'SignedLSB4', 'SignedLSB8', 'SignedMSB2', 'SignedMSB4', 'SignedMSB8', 'UnsignedByte', 'UnsignedLSB2', 'UnsignedLSB4', 'UnsignedLSB8', 'UnsignedMSB2', 'UnsignedMSB4', 'UnsignedMSB8')">
        The attribute data_type must be equal to one of the following values 'IEEE754LSBDouble', 'IEEE754LSBSingle', 'IEEE754MSBDouble', 'IEEE754MSBSingle', 'SignedByte', 'SignedLSB2', 'SignedLSB4', 'SignedLSB8', 'SignedMSB2', 'SignedMSB4', 'SignedMSB8', 'UnsignedByte', 'UnsignedLSB2', 'UnsignedLSB4', 'UnsignedLSB8', 'UnsignedMSB2', 'UnsignedMSB4', 'UnsignedMSB8'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="Encoded_Binary">
      <sch:assert test="pds:external_standard_id = ('System')">
        The attribute external_standard_id must be equal to 'System'.</sch:assert>
      <sch:assert test="pds:encoding_type = ('BINARY')">
        The attribute encoding_type must be equal to 'BINARY'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="Encoded_Byte_Stream">
      <sch:assert test="pds:encoding_type = ('BINARY')">
        The attribute encoding_type must be equal to 'BINARY'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="Encoded_Image">
      <sch:assert test="pds:external_standard_id = ('GIF', 'JPEG', 'PDF', 'PNG', 'TIFF')">
        The attribute external_standard_id must be equal to one of the following values 'GIF', 'JPEG', 'PDF', 'PNG', 'TIFF'.</sch:assert>
      <sch:assert test="pds:encoding_type = ('BINARY')">
        The attribute encoding_type must be equal to 'BINARY'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="Field_Binary">
      <sch:assert test="pds:data_type = ('ASCII_AnyURI', 'ASCII_Boolean', 'ASCII_DOI', 'ASCII_Date_DOY', 'ASCII_Date_Time_DOY', 'ASCII_Date_Time_UTC', 'ASCII_Date_Time_YMD', 'ASCII_Date_YMD', 'ASCII_File_Name', 'ASCII_File_Specification_Name', 'ASCII_Integer', 'ASCII_LID', 'ASCII_LIDVID', 'ASCII_MD5_Checksum', 'ASCII_NonNegative_Integer', 'ASCII_Numeric_Base16', 'ASCII_Numeric_Base2', 'ASCII_Real', 'ASCII_Short_String_Collapsed', 'ASCII_Short_String_Preserved', 'ASCII_Text_Preserved', 'ASCII_Time', 'ASCII_VID', 'ComplexLSB16', 'ComplexLSB8', 'ComplexMSB16', 'ComplexMSB8', 'IEEE754LSBDouble', 'IEEE754LSBSingle', 'IEEE754MSBDouble', 'IEEE754MSBSingle', 'SignedByte', 'SignedLSB2', 'SignedLSB4', 'SignedLSB8', 'SignedMSB2', 'SignedMSB4', 'SignedMSB8', 'UnsignedByte', 'UnsignedLSB2', 'UnsignedLSB4', 'UnsignedLSB8', 'UnsignedMSB2', 'UnsignedMSB4', 'UnsignedMSB8')">
        The attribute data_type must be equal to one of the following values 'ASCII_AnyURI', 'ASCII_Boolean', 'ASCII_DOI', 'ASCII_Date_DOY', 'ASCII_Date_Time_DOY', 'ASCII_Date_Time_UTC', 'ASCII_Date_Time_YMD', 'ASCII_Date_YMD', 'ASCII_File_Name', 'ASCII_File_Specification_Name', 'ASCII_Integer', 'ASCII_LID', 'ASCII_LIDVID', 'ASCII_MD5_Checksum', 'ASCII_NonNegative_Integer', 'ASCII_Numeric_Base16', 'ASCII_Numeric_Base2', 'ASCII_Real', 'ASCII_Short_String_Collapsed', 'ASCII_Short_String_Preserved', 'ASCII_Text_Preserved', 'ASCII_Time', 'ASCII_VID', 'ComplexLSB16', 'ComplexLSB8', 'ComplexMSB16', 'ComplexMSB8', 'IEEE754LSBDouble', 'IEEE754LSBSingle', 'IEEE754MSBDouble', 'IEEE754MSBSingle', 'SignedByte', 'SignedLSB2', 'SignedLSB4', 'SignedLSB8', 'SignedMSB2', 'SignedMSB4', 'SignedMSB8', 'UnsignedByte', 'UnsignedLSB2', 'UnsignedLSB4', 'UnsignedLSB8', 'UnsignedMSB2', 'UnsignedMSB4', 'UnsignedMSB8'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="Field_Bit">
      <sch:assert test="pds:data_type = ('SignedBitString', 'UnsignedBitString')">
        The attribute data_type must be equal to one of the following values 'SignedBitString', 'UnsignedBitString'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="Field_Character">
      <sch:assert test="pds:data_type = ('ASCII_AnyURI', 'ASCII_Boolean', 'ASCII_DOI', 'ASCII_Date_DOY', 'ASCII_Date_Time_DOY', 'ASCII_Date_Time_UTC', 'ASCII_Date_Time_YMD', 'ASCII_Date_YMD', 'ASCII_File_Name', 'ASCII_File_Specification_Name', 'ASCII_Integer', 'ASCII_LID', 'ASCII_LIDVID', 'ASCII_MD5_Checksum', 'ASCII_NonNegative_Integer', 'ASCII_Numeric_Base16', 'ASCII_Numeric_Base2', 'ASCII_Real', 'ASCII_Short_String_Collapsed', 'ASCII_Short_String_Preserved', 'ASCII_Text_Preserved', 'ASCII_Time', 'ASCII_VID')">
        The attribute data_type must be equal to one of the following values 'ASCII_AnyURI', 'ASCII_Boolean', 'ASCII_DOI', 'ASCII_Date_DOY', 'ASCII_Date_Time_DOY', 'ASCII_Date_Time_UTC', 'ASCII_Date_Time_YMD', 'ASCII_Date_YMD', 'ASCII_File_Name', 'ASCII_File_Specification_Name', 'ASCII_Integer', 'ASCII_LID', 'ASCII_LIDVID', 'ASCII_MD5_Checksum', 'ASCII_NonNegative_Integer', 'ASCII_Numeric_Base16', 'ASCII_Numeric_Base2', 'ASCII_Real', 'ASCII_Short_String_Collapsed', 'ASCII_Short_String_Preserved', 'ASCII_Text_Preserved', 'ASCII_Time', 'ASCII_VID'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="Field_Delimited">
      <sch:assert test="pds:data_type = ('ASCII_AnyURI', 'ASCII_Boolean', 'ASCII_DOI', 'ASCII_Date_DOY', 'ASCII_Date_Time_DOY', 'ASCII_Date_Time_UTC', 'ASCII_Date_Time_YMD', 'ASCII_Date_YMD', 'ASCII_File_Name', 'ASCII_File_Specification_Name', 'ASCII_Integer', 'ASCII_LID', 'ASCII_LIDVID', 'ASCII_MD5_Checksum', 'ASCII_NonNegative_Integer', 'ASCII_Numeric_Base16', 'ASCII_Numeric_Base2', 'ASCII_Real', 'ASCII_Short_String_Collapsed', 'ASCII_Short_String_Preserved', 'ASCII_Text_Preserved', 'ASCII_Time', 'ASCII_VID')">
        The attribute data_type must be equal to one of the following values 'ASCII_AnyURI', 'ASCII_Boolean', 'ASCII_DOI', 'ASCII_Date_DOY', 'ASCII_Date_Time_DOY', 'ASCII_Date_Time_UTC', 'ASCII_Date_Time_YMD', 'ASCII_Date_YMD', 'ASCII_File_Name', 'ASCII_File_Specification_Name', 'ASCII_Integer', 'ASCII_LID', 'ASCII_LIDVID', 'ASCII_MD5_Checksum', 'ASCII_NonNegative_Integer', 'ASCII_Numeric_Base16', 'ASCII_Numeric_Base2', 'ASCII_Real', 'ASCII_Short_String_Collapsed', 'ASCII_Short_String_Preserved', 'ASCII_Text_Preserved', 'ASCII_Time', 'ASCII_VID'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="File_PDF">
      <sch:assert test="pds:external_standard_id = ('PDF', 'PDF-A')">
        The attribute external_standard_id must be equal to one of the following values 'PDF', 'PDF-A'.</sch:assert>
      <sch:assert test="pds:encoding_type = ('BINARY')">
        The attribute encoding_type must be equal to 'BINARY'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="Header">
      <sch:assert test="pds:external_standard_id = ('ASCII', 'FITS', 'ISIS2', 'ISIS3', 'ISIS_HISTORY', 'ODL', 'PDS1', 'PDS2', 'PDS3', 'TIFF', 'VICAR')">
        The attribute external_standard_id must be equal to one of the following values 'ASCII', 'FITS', 'ISIS2', 'ISIS3', 'ISIS_HISTORY', 'ODL', 'PDS1', 'PDS2', 'PDS3', 'TIFF', 'VICAR'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="Header_Binary">
      <sch:assert test="pds:external_standard_id = ('TIFF')">
        The attribute external_standard_id must be equal to 'TIFF'.</sch:assert>
      <sch:assert test="pds:encoding_type = ('BINARY')">
        The attribute encoding_type must be equal to 'BINARY'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="Identification_Area">
      <sch:assert test="pds:information_model_version = ('0.7.0.0.j')">
        The attribute information_model_version must be equal to '0.7.0.0.j'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="Instrument">
      <sch:assert test="pds:type = ('Accelerometer', 'Altimeter', 'Camera', 'Cosmic_Ray_Detector', 'Detector_Array', 'Dust_Analyzer', 'Imaging_Spectrometer', 'Magnetometer', 'Meteorology', 'Naked_Eye', 'Other', 'Particle_Detector', 'Photometer', 'Plasma_Analyzer', 'Radio_Science', 'Radio_Spectrometer', 'Radio_Telescope', 'Radiometer', 'Reflectometer', 'Spectrometer')">
        The attribute type must be equal to one of the following values 'Accelerometer', 'Altimeter', 'Camera', 'Cosmic_Ray_Detector', 'Detector_Array', 'Dust_Analyzer', 'Imaging_Spectrometer', 'Magnetometer', 'Meteorology', 'Naked_Eye', 'Other', 'Particle_Detector', 'Photometer', 'Plasma_Analyzer', 'Radio_Science', 'Radio_Spectrometer', 'Radio_Telescope', 'Radiometer', 'Reflectometer', 'Spectrometer'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="Instrument_Host">
      <sch:assert test="pds:type = ('Earth_Based', 'Rover', 'Spacecraft')">
        The attribute type must be equal to one of the following values 'Earth_Based', 'Rover', 'Spacecraft'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="Inventory">
      <sch:assert test="pds:encoding_type = ('CHARACTER')">
        The attribute encoding_type must be equal to 'CHARACTER'.</sch:assert>
      <sch:assert test="pds:reference_type = ('has_member_LIDVID_Primary', 'has_member_LIDVID_Secondary', 'has_member_LID_Secondary')">
        The attribute reference_type must be equal to one of the following values 'has_member_LIDVID_Primary', 'has_member_LIDVID_Secondary', 'has_member_LID_Secondary'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="Investigation">
      <sch:assert test="pds:type = ('Individual_Investigation', 'Mission', 'Observing_Campaign', 'Other_Investigation')">
        The attribute type must be equal to one of the following values 'Individual_Investigation', 'Mission', 'Observing_Campaign', 'Other_Investigation'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="Investigation_Area">
      <sch:assert test="pds:type = ('Individual_Investigation', 'Mission', 'Observing_Campaign', 'Other_Investigation')">
        The attribute type must be equal to one of the following values 'Individual_Investigation', 'Mission', 'Observing_Campaign', 'Other_Investigation'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="Node">
      <sch:assert test="pds:name = ('Engineering', 'Geosciences', 'Imaging', 'Management', 'Navigation_Ancillary_Information_Facility', 'Planetary_Atmospheres', 'Planetary_Plasma_Interactions', 'Planetary_Rings', 'Radio_Science', 'Small_Bodies')">
        The attribute name must be equal to one of the following values 'Engineering', 'Geosciences', 'Imaging', 'Management', 'Navigation_Ancillary_Information_Facility', 'Planetary_Atmospheres', 'Planetary_Plasma_Interactions', 'Planetary_Rings', 'Radio_Science', 'Small_Bodies'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="Observing_System_Component">
      <sch:assert test="pds:observing_system_component_type = ('Analyst', 'Artificial_Illumination', 'Ground-based_Laboratory', 'Ground-based_Observatory', 'Ground-based_Telescope', 'Instrument', 'Literature_Search', 'PDS_Archived_Data', 'Spacecraft')">
        The attribute observing_system_component_type must be equal to one of the following values 'Analyst', 'Artificial_Illumination', 'Ground-based_Laboratory', 'Ground-based_Observatory', 'Ground-based_Telescope', 'Instrument', 'Literature_Search', 'PDS_Archived_Data', 'Spacecraft'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="PDS_Affiliate">
      <sch:assert test="pds:affiliation_type = ('Affiliate', 'Data_Provider', 'Manager', 'Technical_Staff')">
        The attribute affiliation_type must be equal to one of the following values 'Affiliate', 'Data_Provider', 'Manager', 'Technical_Staff'.</sch:assert>
      <sch:assert test="pds:team_name = ('Engineering', 'Geosciences', 'Headquarters', 'Imaging', 'Management', 'National_Space_Science_Data_Center', 'Navigation_Ancillary_Information_Facility', 'Planetary_Atmospheres', 'Planetary_Plasma_Interactions', 'Planetary_Rings', 'Radio_Science', 'Small_Bodies')">
        The attribute team_name must be equal to one of the following values 'Engineering', 'Geosciences', 'Headquarters', 'Imaging', 'Management', 'National_Space_Science_Data_Center', 'Navigation_Ancillary_Information_Facility', 'Planetary_Atmospheres', 'Planetary_Plasma_Interactions', 'Planetary_Rings', 'Radio_Science', 'Small_Bodies'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="Primary_Result_Description">
      <sch:assert test="pds:data_regime = ('Dust', 'Dust_Particle_Counts', 'Electric_Field', 'Electrons', 'Far_Infrared', 'Gamma_Ray', 'Impacts', 'Infrared', 'Ions', 'Magnetic_Field', 'Mass_Spectra', 'Microwave', 'Millimeter', 'Near_Infrared', 'Optical', 'Particles', 'Radio', 'Sub-Millimeter', 'Ultraviolet', 'Visible', 'X-Ray')">
        The attribute data_regime must be equal to one of the following values 'Dust', 'Dust_Particle_Counts', 'Electric_Field', 'Electrons', 'Far_Infrared', 'Gamma_Ray', 'Impacts', 'Infrared', 'Ions', 'Magnetic_Field', 'Mass_Spectra', 'Microwave', 'Millimeter', 'Near_Infrared', 'Optical', 'Particles', 'Radio', 'Sub-Millimeter', 'Ultraviolet', 'Visible', 'X-Ray'.</sch:assert>
      <sch:assert test="pds:type = ('Anaglyph', 'Aperture', 'Astrometry', 'B-Field_Vectors', 'Color', 'Column_Density', 'Comet_Nuclear_Properties', 'Count', 'Count_Rate', 'Data_Number', 'Digital_Elevation_Model', 'E-Field_Vectors', 'Electron', 'Environment', 'Filter_Curve', 'Filtered', 'Flux', 'Frequency', 'Geometry', 'Gravity_Model', 'Grism', 'Hardware_Temperature', 'High-Speed', 'Image', 'Image', 'Impact_Characteristics', 'Instrument', 'Ion', 'Lightcurve', 'Lines', 'Magnetometry', 'Magnitude', 'Magnitude', 'Map', 'Mass', 'Morphology', 'Null_Result', 'Parameters', 'Photometric', 'Photometry', 'Point_Spread_Function', 'Polarimetry', 'Pressure', 'Production_Rate', 'Pulse_Heights', 'Radiance', 'Radiometry', 'Shape_Model', 'Spectrometry', 'Spectrophotometry', 'Spectroscopy', 'Spectrum', 'Spherical_Harmonics', 'Spherical_Harmonics', 'Spin_State', 'Thermal', 'Time-Of-Flight', 'Triangular_Plate')">
        The attribute type must be equal to one of the following values 'Anaglyph', 'Aperture', 'Astrometry', 'B-Field_Vectors', 'Color', 'Column_Density', 'Comet_Nuclear_Properties', 'Count', 'Count_Rate', 'Data_Number', 'Digital_Elevation_Model', 'E-Field_Vectors', 'Electron', 'Environment', 'Filter_Curve', 'Filtered', 'Flux', 'Frequency', 'Geometry', 'Gravity_Model', 'Grism', 'Hardware_Temperature', 'High-Speed', 'Image', 'Image', 'Impact_Characteristics', 'Instrument', 'Ion', 'Lightcurve', 'Lines', 'Magnetometry', 'Magnitude', 'Magnitude', 'Map', 'Mass', 'Morphology', 'Null_Result', 'Parameters', 'Photometric', 'Photometry', 'Point_Spread_Function', 'Polarimetry', 'Pressure', 'Production_Rate', 'Pulse_Heights', 'Radiance', 'Radiometry', 'Shape_Model', 'Spectrometry', 'Spectrophotometry', 'Spectroscopy', 'Spectrum', 'Spherical_Harmonics', 'Spherical_Harmonics', 'Spin_State', 'Thermal', 'Time-Of-Flight', 'Triangular_Plate'.</sch:assert>
      <sch:assert test="pds:purpose = ('Calibration', 'Checkout', 'Engineering', 'Navigation', 'Science')">
        The attribute purpose must be equal to one of the following values 'Calibration', 'Checkout', 'Engineering', 'Navigation', 'Science'.</sch:assert>
      <sch:assert test="pds:reduction_level = ('Analytic', 'Calibrated', 'Derived', 'Raw')">
        The attribute reduction_level must be equal to one of the following values 'Analytic', 'Calibrated', 'Derived', 'Raw'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="Product_Zipped">
      <sch:assert test="pds:container_type = ('GZIP', 'LZIP', 'TAR', 'ZIP')">
        The attribute container_type must be equal to one of the following values 'GZIP', 'LZIP', 'TAR', 'ZIP'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="Quaternion">
      <sch:assert test="pds:type = ('type1', 'type2')">
        The attribute type must be equal to one of the following values 'type1', 'type2'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="SPICE_Kernel">
      <sch:assert test="pds:encoding_type = ('BINARY', 'CHARACTER')">
        The attribute encoding_type must be equal to one of the following values 'BINARY', 'CHARACTER'.</sch:assert>
      <sch:assert test="pds:kernel_type = ('CK', 'DBK', 'DSK', 'EK', 'FK', 'IK', 'LSK', 'MK', 'PCK', 'SCLK', 'SPK')">
        The attribute kernel_type must be equal to one of the following values 'CK', 'DBK', 'DSK', 'EK', 'FK', 'IK', 'LSK', 'MK', 'PCK', 'SCLK', 'SPK'.</sch:assert>
      <sch:assert test="pds:external_standard_id = ('SPICE')">
        The attribute external_standard_id must be equal to 'SPICE'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="Service_Description">
      <sch:assert test="pds:encoding_type = ('CHARACTER')">
        The attribute encoding_type must be equal to 'CHARACTER'.</sch:assert>
      <sch:assert test="pds:external_standard_id = ('WADL', 'WSDL')">
        The attribute external_standard_id must be equal to one of the following values 'WADL', 'WSDL'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="Stream_Text">
      <sch:assert test="pds:encoding_type = ('CHARACTER')">
        The attribute encoding_type must be equal to 'CHARACTER'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="Table_Binary">
      <sch:assert test="pds:encoding_type = ('BINARY')">
        The attribute encoding_type must be equal to 'BINARY'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="Table_Character">
      <sch:assert test="pds:encoding_type = ('CHARACTER')">
        The attribute encoding_type must be equal to 'CHARACTER'.</sch:assert>
      <sch:assert test="pds:record_delimiter = ('0x0A', '0x0D', '0x0D0x0A')">
        The attribute record_delimiter must be equal to one of the following values '0x0A', '0x0D', '0x0D0x0A'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="Table_Delimited">
      <sch:assert test="pds:encoding_type = ('CHARACTER')">
        The attribute encoding_type must be equal to 'CHARACTER'.</sch:assert>
      <sch:assert test="pds:record_delimiter = ('0x0A', '0x0D', '0x0D0x0A')">
        The attribute record_delimiter must be equal to one of the following values '0x0A', '0x0D', '0x0D0x0A'.</sch:assert>
      <sch:assert test="pds:external_standard_id = ('PDS_CSV')">
        The attribute external_standard_id must be equal to 'PDS_CSV'.</sch:assert>
      <sch:assert test="pds:field_delimiter = ('0x09', '0x2C', '0x3B', '0x7C')">
        The attribute field_delimiter must be equal to one of the following values '0x09', '0x2C', '0x3B', '0x7C'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="Target">
      <sch:assert test="pds:type = ('Asteroid', 'Comet', 'Dust', 'Dwarf_Planet', 'Galaxy', 'Globular_Cluster', 'Meteorite', 'Meteoroid', 'Meteoroid_Stream', 'Nebula', 'Open_Cluster', 'Planet', 'Planetary_Nebula', 'Planetary_System', 'Plasma_Cloud', 'Ring', 'Satellite', 'Star', 'Star_Cluster', 'Sun', 'Terrestrial_Sample', 'Trans-Neptunian_Object')">
        The attribute type must be equal to one of the following values 'Asteroid', 'Comet', 'Dust', 'Dwarf_Planet', 'Galaxy', 'Globular_Cluster', 'Meteorite', 'Meteoroid', 'Meteoroid_Stream', 'Nebula', 'Open_Cluster', 'Planet', 'Planetary_Nebula', 'Planetary_System', 'Plasma_Cloud', 'Ring', 'Satellite', 'Star', 'Star_Cluster', 'Sun', 'Terrestrial_Sample', 'Trans-Neptunian_Object'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="Terminological_Entry">
      <sch:assert test="pds:language = ('English')">
        The attribute language must be equal to 'English'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="Uniformly_Sampled">
      <sch:assert test="pds:sampling_parameter_scale = ('EXPONENTIAL', 'LINEAR', 'LOGARITHMIC')">
        The attribute sampling_parameter_scale must be equal to one of the following values 'EXPONENTIAL', 'LINEAR', 'LOGARITHMIC'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="Vector">
      <sch:assert test="pds:type = ('POSITION', 'VELOCITY')">
        The attribute type must be equal to one of the following values 'POSITION', 'VELOCITY'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="Volume_PDS3">
      <sch:assert test="pds:archive_status = ('ARCHIVED', 'ARCHIVED_ACCUMULATING', 'IN_LIEN_RESOLUTION', 'IN_LIEN_RESOLUTION_ACCUMULATING', 'IN_PEER_REVIEW', 'IN_PEER_REVIEW_ACCUMULATING', 'IN_QUEUE', 'IN_QUEUE_ACCUMULATING', 'LOCALLY_ARCHIVED', 'LOCALLY_ARCHIVED_ACCUMULATING', 'PRE_PEER_REVIEW', 'PRE_PEER_REVIEW_ACCUMULATING', 'SAFED', 'SUPERSEDED')">
        The attribute archive_status must be equal to one of the following values 'ARCHIVED', 'ARCHIVED_ACCUMULATING', 'IN_LIEN_RESOLUTION', 'IN_LIEN_RESOLUTION_ACCUMULATING', 'IN_PEER_REVIEW', 'IN_PEER_REVIEW_ACCUMULATING', 'IN_QUEUE', 'IN_QUEUE_ACCUMULATING', 'LOCALLY_ARCHIVED', 'LOCALLY_ARCHIVED_ACCUMULATING', 'PRE_PEER_REVIEW', 'PRE_PEER_REVIEW_ACCUMULATING', 'SAFED', 'SUPERSEDED'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="XML_Schema">
      <sch:assert test="pds:encoding_type = ('CHARACTER')">
        The attribute encoding_type must be equal to 'CHARACTER'.</sch:assert>
      <sch:assert test="pds:external_standard_id = ('Schematron', 'XML_Catalog', 'XML_Schema')">
        The attribute external_standard_id must be equal to one of the following values 'Schematron', 'XML_Catalog', 'XML_Schema'.</sch:assert>
    </sch:rule>
  </sch:pattern>
  <sch:pattern>
    <sch:rule context="Zipped_Member_Entry">
      <sch:assert test="pds:reference_type = ('contained_product', 'manifest_product')">
        The attribute reference_type must be equal to one of the following values 'contained_product', 'manifest_product'.</sch:assert>
    </sch:rule>
  </sch:pattern>
</sch:schema>
