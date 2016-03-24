package gov.nasa.pds.tools.validate.rule.pds4;

import gov.nasa.pds.tools.validate.rule.AbstractValidationRule;
import gov.nasa.pds.tools.validate.rule.GenericProblems;
import gov.nasa.pds.tools.validate.rule.ValidationRule;
import gov.nasa.pds.tools.validate.rule.ValidationTest;

import java.io.File;

/**
 * Implements the rule that all files that look like labels in a folder
 * must be valid labels.
 */
public class LabelInFolderRule extends AbstractValidationRule {

    private static final String XML_SUFFIX = ".xml";

    @Override
    public boolean isApplicable(String location) {
        File f = new File(location);

        return f.isDirectory();
    }

    /**
     * Validates each file with a label suffix as a PDS4 label.
     */
    @ValidationTest
    public void validateLabelsInFolder() {
        ValidationRule labelRule = getContext().getRuleManager().findRuleByName("pds4.label");

        for (File f : getTarget().listFiles()) {
            if (f.getName().endsWith(XML_SUFFIX)) {
                try {
                    labelRule.execute(getChildContext(f));
                } catch (Exception e) {
                    reportError(GenericProblems.UNCAUGHT_EXCEPTION, f, -1, -1, e.getMessage());
                }
            }
        }
    }

}
