// Copyright 2006-2018, by the California Institute of Technology.
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
package gov.nasa.pds.tools.validate;

/**
 * Defines the types of problems that can be reported by validation rules.
 */
public enum ProblemType {

	MISSING_REQUIRED_RESOURCE("validation.error.missing_required_file"),

	FILE_NAMING_PROBLEM("validation.error.file_naming_problem"),

	UNLABELED_FILE("file.error.not_referenced_in_label"),

	MISSING_REFERENCED_FILE("label.error.missing_file"),

	INVALID_LABEL("validation.error.invalid_label"),

	EMPTY_FOLDER("validation.error.empty_folder"),

	UNKNOWN_VALUE("validation.error.unknown_value"),

  TABLE_DEFINITION_PROBLEM("validation.error.table_definition_problem"),

  TABLE_DEFINITION_MISMATCH("validation.error.table_definition_mismatch"),

	INVALID_FIELD_VALUE("validation.error.invalid_field_value"),

	INVALID_CHARACTER_STREAM("validation.error.invalid_character_stream"),

	DUPLICATE_IDENTIFIER("label.error.duplicate_identifier"),
	
	OUT_OF_MEMORY("validation.error.out_of_memory"),

	INTERNAL_ERROR("vadidation.error.internal_error"),
  
  CHECKSUM_MISMATCH("label.error.checksum_mismatch"),
  
  MISSING_CHECKSUM("label.error.missing_checksum"),
  
  SCHEMA_ERROR("label.error.schema"),
  
  SCHEMATRON_ERROR("label.error.schematron"),
  
  LABEL_UNRESOLVABLE_RESOURCE("label.error.unresolvable_resource"),
  
  MISSING_SCHEMA("label.error.missing_schema"),
  
  BAD_EXTENSION("label.error.bad_extension"),
  
  FILE_NAME_TOO_LONG("file.error.name_too_long"),
  
  FILE_NAME_HAS_INVALID_CHARS("file.error.name_has_invalid_characters"),
  
  UNALLOWED_FILE_NAME("file.error.unallowed_name"),
  
  UNALLOWED_BASE_NAME("file.error.unallowed_base_name"),
  
  DIR_NAME_TOO_LONG("directory.error.name_too_long"),
  
  DIR_NAME_HAS_INVALID_CHARS("directory.error.name_has_invalid_characters"),
  
  UNALLOWED_BUNDLE_SUBDIR_NAME("sub_directory.error.unallowed_name"),
  
  UNALLOWED_DIRECTORY_NAME("directory.error.unallowed_name"),
    
  INVALID_COLLECTION_NAME("bundle.error.invalid_collection_name"),
  
  UNEXPECTED_FILE_IN_BUNDLE_ROOT("bundle.error.invalid_file_in_root_directory"),
  
  // Referential Integrity Checking messages
  
  DUPLICATE_VERSIONS("integrity.error.duplicate_versions"),
  
  DUPLICATE_MEMBERS("integrity.error.duplicate_members"),
  
  
  // Catalog related messages
  
  CATALOG_UNRESOLVABLE_RESOURCE("catalog.error.unresolvable_resource"),
  
  CATALOG_UNRESOLVABLE_SCHEMA("catalog.error.unresolvable_schema"),
  
  CATALOG_UNRESOLVABLE_SCHEMATRON("catalog.error.unresolvable_schematron"),
  
  // Array Content messages
  
  ARRAY_INTERNAL_ERROR("array.error.internal_error"),
  
  ARRAY_DATA_FILE_READ_ERROR("array.error.bad_file_read"),
  
  ARRAY_VALUE_OUT_OF_DATA_TYPE_RANGE("array.error.value_out_of_data_type_range"),
  
  ARRAY_VALUE_OUT_OF_MIN_MAX_RANGE("array.error.value_out_of_min_max_range"),
  
  ARRAY_VALUE_OUT_OF_SCALED_MIN_MAX_RANGE("array.error.value_out_of_scaled_min_max_range"),
  
  ARRAY_VALUE_IS_SPECIAL_CONSTANT("array.info.is_special_constant"),
  
  // Table Content messages
  
  TABLE_INTERNAL_ERROR("table.error.internal_error"),
  
  TABLE_FILE_READ_ERROR("table.error.bad_file_read"),
  
  MISSING_CRLF("table.error.missing_CRLF"),
  
  RECORD_LENGTH_MISMATCH("table.error.record_length_mismatch"),
  
  RECORDS_MISMATCH("table.error.records_mismatch"),
  
  BIT_FIELD_MISMATCH("table.error.bit_field_mismatch"),
  
  FIELDS_MISMATCH("table.error.fields_mismatch"),
  
  FIELD_VALUE_OUT_OF_MIN_MAX_RANGE("table.error.field_value_out_of_min_max_range"),
  
  FIELD_VALUE_TOO_LONG("table.error.field_value_too_long"),
  
  FIELD_VALUE_DATA_TYPE_MISMATCH("table.error.field_value_data_type_mismatch"),
  
  BAD_FIELD_READ("table.error.bad_field_read"),
  
  FIELD_VALUE_NOT_A_NUMBER("table.error.field_value_not_a_number"),
  
  FIELD_VALUE_NOT_RIGHT_JUSTIFIED("table.error.field_value_not_right_justified"),
  
  FIELD_VALUE_NOT_LEFT_JUSTIFIED("table.error.field_value_not_left_justified"),
  
  FIELD_VALUE_FORMAT_SPECIFIER_MISMATCH("table.error.field_value_format_specifier_mismatch"),
  
  FIELD_VALUE_FORMAT_PRECISION_MISMATCH("table.error.field_value_format_precision_mismatch"),
  
  
  
  // Warning message types
  
  SCHEMA_WARNING("label.warning.schema"),
  
  MISSING_SCHEMATRON_SPEC("label.warning.missing_schematron_spec"),
  
  BAD_SCHEMATYPENS("label.warning.bad_schematypens"),
  
  MISSING_SCHEMATYPENS("label.warning.missing_schematypens"),
  
  SCHEMATRON_WARNING("label.warning.schematron"),
  
  FILE_REFERENCE_CASE_MISMATCH("label.warning.file_ref_case_mismatch"),
  
  ARRAY_INTERNAL_WARNING("array.warning.internal_warning"),
  
  MEMBER_NOT_FOUND("integrity.warning.member_not_found"),
  
  UNREFERENCED_MEMBER("integrity.warning.unreferenced_member"),
  
  // Info message types
  
  GENERAL_INFO("validation.info.general"),
  
  CHECKSUM_MATCHES("label.info.checksum_matches"),
  
  MISSING_CHECKSUM_INFO("label.info.missing_checksum"),
  
  SCHEMATRON_INFO("label.info.schematron"),
  
  BLANK_FIELD_VALUE("table.info.blank_field_value"),
  
  MEMBER_FOUND("integrity.info.member_found"),
  
  REFERENCED_MEMBER("integrity.info.referenced_member"),
  
  DUPLICATE_MEMBERS_INFO("integrity.info.duplicate_members"),
  
  // Debug messages (Should only be used for debugging purposes)
  CRLF_DETECTED("table.debug.record_has_CRLF"),
  
  RECORD_MATCH("table.debug.record_match"),
  
  GOOD_RECORD_LENGTH("table.debug.good_record_length"),
  
  FIELD_VALUE_FORMAT_MATCH("table.debug.field_value_format_match"),
  
  FIELD_VALUE_IN_MIN_MAX_RANGE("table.debug.field_value_in_min_max_range"),
  
  FIELD_VALUE_DATA_TYPE_MATCH("table.debug.field_value_matches_data_type"),
  
  BIT_FIELD_MATCH("table.debug.bit_field_match");
  
	private final String key;

	private ProblemType(String key) {
		this.key = key;
	}

	/**
	 * Gets the key for mapping the problem type to a UI string.
	 *
	 * @return the key string
	 */
	public String getKey() {
		return key;
	}

}
