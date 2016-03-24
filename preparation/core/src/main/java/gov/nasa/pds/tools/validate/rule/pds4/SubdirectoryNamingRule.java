package gov.nasa.pds.tools.validate.rule.pds4;

import gov.nasa.pds.tools.validate.rule.AbstractValidationRule;
import gov.nasa.pds.tools.validate.rule.ValidationTest;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implements a rule that checks for children of a directory
 * using illegal names. These are directories that can only
 * occur in the root directory of a bundle.
 */
public class SubdirectoryNamingRule extends AbstractValidationRule {

    private static final Pattern BUNDLE_LABEL_PATTERN = Pattern.compile("bundle(_.*)\\.xml", Pattern.CASE_INSENSITIVE);

    private static final String[] ILLEGAL_DIRECTORY_NAMES = {
        "browse",
        "calibration",
        "context",
        "data",
        "document",
        "geometry",
        "miscellaneous",
        "spice_kernels",
        "xml_schema"
    };

    private static String illegalNamePatternStr;
    static {
        StringBuilder builder = new StringBuilder();
        for (String s : ILLEGAL_DIRECTORY_NAMES) {
            if (builder.length() > 0) {
                builder.append("|");
            }
            builder.append(s);
        }

        illegalNamePatternStr = builder.toString();
    }

    private static final Pattern ILLEGAL_NAME_PATTERN = Pattern.compile(illegalNamePatternStr);

    @Override
    public boolean isApplicable(String location) {
        File f = new File(location);

        if (!f.isDirectory()) {
            return false;
        }

        // Check for bundle(_.*)?\.xml file.
        for (String childName : f.list()) {
            Matcher matcher = BUNDLE_LABEL_PATTERN.matcher(childName);
            if (matcher.matches()) {
                return false;
            }
        }

        return true;
    }

    /**
     * Checks for illegal subdirectory names.
     */
    @ValidationTest
    public void checkIllegalDirectoryNames() {
        for (File f : getTarget().listFiles()) {
            if (f.isDirectory()) {
                Matcher matcher = ILLEGAL_NAME_PATTERN.matcher(f.getName());
                if (matcher.matches()) {
                    reportError(PDS4Problems.UNALLOWED_BUNDLE_SUBDIRECTORY_NAME, f, -1, -1);
                }
            }
        }
    }

}
