package gov.nasa.pds.tools.validate.rule.pds4;

import gov.nasa.pds.tools.validate.rule.AbstractValidationRule;
import gov.nasa.pds.tools.validate.rule.ValidationTest;

import java.io.File;

/**
 * Implements a validation rule that checks that all files are
 * referenced by some label.
 */
public class FindUnreferencedFiles extends AbstractValidationRule {

    @Override
    public boolean isApplicable(String location) {
        // This rule is applicable at the top level only.
        return getContext().isRootTarget();
    }

    /**
     * Iterate over unreferenced targets, reporting an error for each.
     */
    @ValidationTest
    public void findUnreferencedTargets() {
        // Only run the test if we are the root target, to avoid duplicate errors.
        if (getContext().isRootTarget()) {
            for (String location : getRegistrar().getUnreferencedTargets()) {
                reportError(PDS4Problems.UNLABELED_FILE, new File(location), -1, -1);
            }
        }
    }

}
