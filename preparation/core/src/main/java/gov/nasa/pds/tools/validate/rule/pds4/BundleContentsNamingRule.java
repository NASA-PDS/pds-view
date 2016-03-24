package gov.nasa.pds.tools.validate.rule.pds4;

import gov.nasa.pds.tools.validate.ProblemDefinition;
import gov.nasa.pds.tools.validate.rule.AbstractValidationRule;
import gov.nasa.pds.tools.validate.rule.ValidationTest;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implements a validation rule ensuring that only valid files and
 * directories appear in the root directory of a bundle.
 */
public class BundleContentsNamingRule extends AbstractValidationRule {

    private static final String DIRECTORY_ALLOWED_CHARACTERS = "[A-Za-z0-9_-]";

    private static final String[] ALLOWED_BUNDLE_NAME_PREFIXES = {
        "browse",
        "calibration",
        "context",
        "data",
        "document",
        "geometry",
        "miscellaneous",
        "xml_schema",
        "spice_kernels",
    };

    private static final Pattern[] ALLOWED_DIRECTORY_NAME_PATTERNS = new Pattern[ALLOWED_BUNDLE_NAME_PREFIXES.length];
    static {
        for (int i=0; i < ALLOWED_BUNDLE_NAME_PREFIXES.length; ++i) {
            StringBuilder builder = new StringBuilder(ALLOWED_BUNDLE_NAME_PREFIXES[i]);
            builder.append("(_");
            builder.append(DIRECTORY_ALLOWED_CHARACTERS);
            builder.append("*)?");

            ALLOWED_DIRECTORY_NAME_PATTERNS[i] = Pattern.compile(builder.toString());
        }
    }

    private static final String ALLOWED_FILE_NAMES[] = {
        "bundle(_[A-Za-z0-9_.-]*)?\\.xml",
        "readme.html",
        "readme.txt",
    };

    private static final Pattern[] ALLOWED_FILE_NAME_PATTERNS = new Pattern[ALLOWED_FILE_NAMES.length];
    static {
        for (int i=0; i < ALLOWED_FILE_NAMES.length; ++i) {
            ALLOWED_FILE_NAME_PATTERNS[i] = Pattern.compile(ALLOWED_FILE_NAMES[i]);
        }
    }

	/**
	 * Checks that files and directories at the root of the bundle are
	 * valid. See section 2B.2.2.1.
	 */
	@ValidationTest
	public void checkFileAndDirectoryNaming() {
	    for (File f : getTarget().listFiles()) {
	        if (!f.isDirectory()) {
	            checkFileNaming(f, PDS4Problems.UNEXPECTED_FILE_IN_BUNDLE_ROOT, ALLOWED_FILE_NAME_PATTERNS);
	        } else {
	            checkFileNaming(f, PDS4Problems.INVALID_COLLECTION_NAME, ALLOWED_DIRECTORY_NAME_PATTERNS);
	        }
	    }
	}

	private void checkFileNaming(File f, ProblemDefinition problem, Pattern... allowedPatterns) {
	    for (Pattern pat : allowedPatterns) {
	        Matcher matcher = pat.matcher(f.getName());
	        if (matcher.matches()) {
	            return;
	        }
	    }

	    // If we get here, no pattern matched.
	    reportError(problem, f, 1, -1);
    }

    @Override
	public boolean isApplicable(String location) {
		File f = new File(location);

		return f.isDirectory();
	}

}
