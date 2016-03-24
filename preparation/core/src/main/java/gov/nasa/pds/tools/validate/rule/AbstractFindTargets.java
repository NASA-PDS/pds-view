package gov.nasa.pds.tools.validate.rule;

import gov.nasa.pds.tools.validate.TargetRegistrar;

import java.io.File;

public abstract class AbstractFindTargets extends AbstractValidationRule {

    @Override
    public boolean isApplicable(String location) {
        return true;
    }

    @ValidationTest
    public void findTargets() {
        TargetRegistrar registrar = getRegistrar();
        TreeWalker walker = new TreeWalker(registrar);
        walker.walkSubtree(getTarget(), null);
    }

    public abstract void handleDirectory(File dir, TargetRegistrar registrar);

    public abstract void handleFile(File f, TargetRegistrar registrar);

    private class TreeWalker extends AbstractFileSubtreeWalker<Void> {

        private TargetRegistrar registrar;

        public TreeWalker(TargetRegistrar registrar) {
            this.registrar = registrar;
        }

        @Override
        protected Void handleDirectory(File dir, Void state) throws Exception {
            AbstractFindTargets.this.handleDirectory(dir, registrar);
            return null;
        }

        @Override
        protected void handleFile(File f, Void state) throws Exception {
            AbstractFindTargets.this.handleFile(f, registrar);
        }

    }

}
