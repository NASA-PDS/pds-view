package gov.nasa.pds.tools.validate.rule.pds4;

import gov.nasa.pds.tools.validate.rule.AbstractValidationRule;
import gov.nasa.pds.tools.validate.rule.GenericProblems;
import gov.nasa.pds.tools.validate.rule.ValidationRule;
import gov.nasa.pds.tools.validate.rule.ValidationTest;

import java.io.File;
import java.io.FileFilter;

/**
 * Implements a rule that iterates over subdirectories, validating
 * each as a PDS4 folder.
 */
public class SubDirectoryRule extends AbstractValidationRule {

    @Override
    public boolean isApplicable(String location) {
        File f = new File(location);
        return f.exists() && f.isDirectory();
    }

    @ValidationTest
    public void testCollectionDirectories() {
        FileFilter filter = new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory();
            }
        };

        ValidationRule collectionRule = getContext().getRuleManager().findRuleByName("pds4.folder");

        if (collectionRule != null) {
            for (File dir : getContext().getTarget().listFiles(filter)) {
                try {
                    collectionRule.execute(getChildContext(dir));
                } catch (Exception e) {
                    reportError(GenericProblems.UNCAUGHT_EXCEPTION, dir, -1, -1, e.getMessage());
                }
            }
        }
    }

}
