// Copyright 2006-2017, by the California Institute of Technology.
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

	MISSING_REQUIRED_RESOURCE("validation.error.missingRequiredFile"),

	FILE_NAMING_PROBLEM("validation.error.fileNamingProblem"),

	UNLABELED_FILE("validation.error.unlabeledFile"),

	MISSING_REFERENCED_FILE("validation.error.missingFile"),

	INVALID_LABEL("validation.error.invalidLabel"),

	EMPTY_FOLDER("validation.error.emptyFolder"),

	UNKNOWN_VALUE("validation.error.unknownValue"),

  TABLE_DEFINITION_PROBLEM("validation.error.tableDefinitionProblem"),

  TABLE_DEFINITION_MISMATCH("validation.error.tableDefinitionMismatch"),

	INVALID_FIELD_VALUE("validation.error.invalidFieldValue"),

	INVALID_CHARACTER_STREAM("validation.error.invalidCharacterStream"),

	DUPLICATE_IDENTIFIER("validation.error.duplicateIdentifier"),

	INTERNAL_ERROR("validation.error.internalError"),
  
  CHECKSUM_MISMATCH("validation.error.checksumMismatch"),
  
  MISSING_CHECKSUM("valdiation.error.missingChecksum"),
  
  // Warning message types
  
  FILE_REFERENCE_CASE_MISMATCH("validation.warning.fileRefCaseMismatch"),
  
  // Info message type. Don't think we need to categorize informational
  // messages.
  
  GENERAL_INFO("validation.info.general");
  
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
