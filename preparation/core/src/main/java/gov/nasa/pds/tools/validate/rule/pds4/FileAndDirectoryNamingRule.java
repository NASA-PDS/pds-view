package gov.nasa.pds.tools.validate.rule.pds4;

import gov.nasa.pds.tools.validate.rule.AbstractValidationRule;
import gov.nasa.pds.tools.validate.rule.ValidationTest;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implements a validation rule enforcing file and directory
 * naming standards.
 */
public class FileAndDirectoryNamingRule extends AbstractValidationRule {

	private static final int MAXIMUM_FILE_NAME_LENGTH = 255;

    private static final Pattern NAMING_PATTERN = Pattern.compile("[A-Za-z0-9][A-Za-z0-9_.-]*");

	private static final Set<String> PROHIBITED_BASE_NAMES = new HashSet<String>();
	static {
		PROHIBITED_BASE_NAMES.add("aux");
		PROHIBITED_BASE_NAMES.add("com1");
		PROHIBITED_BASE_NAMES.add("com2");
		PROHIBITED_BASE_NAMES.add("com3");
		PROHIBITED_BASE_NAMES.add("com4");
		PROHIBITED_BASE_NAMES.add("com5");
		PROHIBITED_BASE_NAMES.add("com6");
		PROHIBITED_BASE_NAMES.add("com7");
		PROHIBITED_BASE_NAMES.add("com8");
		PROHIBITED_BASE_NAMES.add("com9");
		PROHIBITED_BASE_NAMES.add("con");
		PROHIBITED_BASE_NAMES.add("core");
		PROHIBITED_BASE_NAMES.add("lpt1");
		PROHIBITED_BASE_NAMES.add("lpt2");
		PROHIBITED_BASE_NAMES.add("lpt3");
		PROHIBITED_BASE_NAMES.add("lpt4");
		PROHIBITED_BASE_NAMES.add("lpt5");
		PROHIBITED_BASE_NAMES.add("lpt6");
		PROHIBITED_BASE_NAMES.add("lpt7");
		PROHIBITED_BASE_NAMES.add("lpt8");
		PROHIBITED_BASE_NAMES.add("lpt9");
		PROHIBITED_BASE_NAMES.add("nul");
		PROHIBITED_BASE_NAMES.add("prn");
	}

	/**
	 * Checks that the files and directories in the target conform
	 * to the naming rules in sections 6C.1 and 6C.2.
	 */
	@ValidationTest
	public void checkFileAndDirectoryNaming() {
		checkFileAndDirectoryNaming(getTarget().listFiles());
	}

	void checkFileAndDirectoryNaming(File... list) {
		Map<String, String> seenNames = new HashMap<String, String>();

		for (File f : list) {
			checkFileOrDirectoryName(f, seenNames, f.isDirectory());
		}
	}

	// Default scope, for unit testing.
	void checkFileOrDirectoryName(File f, Map<String, String> seenNames, boolean isDirectory) {
	    String name = f.getName();

		// File names must be no longer than 255 characters.
		if (name.length() > MAXIMUM_FILE_NAME_LENGTH) {
			reportError(
					isDirectory ? PDS4Problems.DIRECTORY_NAME_TOO_LONG : PDS4Problems.FILE_NAME_TOO_LONG,
					f,
					-1,
					-1
			);
		}

		// File names must use legal characters.
		Matcher matcher = NAMING_PATTERN.matcher(name);
		if (!matcher.matches()) {
			reportError(
					isDirectory ? PDS4Problems.DIRECTORY_NAME_USES_INVALID_CHARACTER : PDS4Problems.FILE_NAME_USES_INVALID_CHARACTER,
					f,
					-1,
					-1
			);
		}

		// Additionally, directories cannot have extensions.
		if (isDirectory && name.contains(".")) {
			reportError(
					PDS4Problems.DIRECTORY_NAME_USES_INVALID_CHARACTER,
					f,
					-1,
					-1
			);
		}

		// File names must be unique, up to case-insensitivity.
		String lcName = name.toLowerCase(Locale.getDefault());
		if (!seenNames.containsKey(lcName)) {
			seenNames.put(lcName, name);
		} else {
			reportError(
					isDirectory ? PDS4Problems.DIRECTORY_NAME_CONFLICTS_IN_CASE : PDS4Problems.FILE_NAME_CONFLICTS_IN_CASE,
					f,
					-1,
					-1
			);
		}

		// Prohibited file names.
		if (!isDirectory && ("a.out".equalsIgnoreCase(name) || "core".equalsIgnoreCase(name))) {
			reportError(PDS4Problems.UNALLOWED_FILE_NAME, f, -1, -1);
		}

		// Prohibited base names or directory names.
		int lastDotPos = name.lastIndexOf('.');
		String baseName = (lastDotPos < 0 ? name : name.substring(0, lastDotPos));
		if (PROHIBITED_BASE_NAMES.contains(baseName)) {
			reportError(
					isDirectory ? PDS4Problems.UNALLOWED_DIRECTORY_NAME : PDS4Problems.UNALLOWED_BASE_NAME,
					f,
					-1,
					-1
			);
		}
	}

	@Override
	public boolean isApplicable(String location) {
		File f = new File(location);

		return f.isDirectory();
	}

}
