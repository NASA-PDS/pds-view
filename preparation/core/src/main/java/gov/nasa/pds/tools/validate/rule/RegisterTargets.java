package gov.nasa.pds.tools.validate.rule;

import gov.nasa.pds.tools.validate.TargetRegistrar;
import gov.nasa.pds.tools.validate.TargetType;

import java.io.File;

/**
 * Implements a rule that inserts this target into the target
 * registry, if not already present, and also adds all of its
 * child targets.
 */
public class RegisterTargets extends AbstractValidationRule {

    @Override
    public boolean isApplicable(String location) {
        return true;
    }

    @ValidationTest
    public void registerTargets() {
        TargetRegistrar registrar = getRegistrar();

        String targetLocation = getTarget().getAbsolutePath();
        String parentLocation = getParentTarget();
        TargetType type = getTarget().isDirectory() ? TargetType.FOLDER : TargetType.FILE;

        if (registrar.getRoot()==null || !registrar.hasTarget(targetLocation)) {
            registrar.addTarget(parentLocation, type, targetLocation);
        }

        if (getTarget().isDirectory()) {
            for (File child : getTarget().listFiles()) {
                String childLocation = child.getAbsolutePath();
                TargetType childType = child.isDirectory() ? TargetType.FOLDER : TargetType.FILE;
                registrar.addTarget(targetLocation, childType, childLocation);
            }
        }
    }

}
