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

	INTERNAL_ERROR("validation.error.internalError");

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
