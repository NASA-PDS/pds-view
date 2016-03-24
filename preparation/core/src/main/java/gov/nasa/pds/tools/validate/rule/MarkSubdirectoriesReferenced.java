package gov.nasa.pds.tools.validate.rule;

import java.io.File;

/**
 * Implements a validation rule that marks all subdirectories as referenced.
 */
public class MarkSubdirectoriesReferenced extends AbstractValidationRule {

    @Override
    public boolean isApplicable(String location) {
        return new File(location).isDirectory();
    }

    @ValidationTest
    public void markSubdirectoriesReferenced() {
        for (File f : getTarget().listFiles()) {
            if (f.isDirectory()) {
                // Directories reference themselves.
                String location = f.getAbsolutePath();
                getRegistrar().addTargetReference(location, location);
            }
        }
    }

}
