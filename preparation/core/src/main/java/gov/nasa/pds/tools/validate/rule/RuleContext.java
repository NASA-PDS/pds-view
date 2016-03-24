package gov.nasa.pds.tools.validate.rule;

import gov.nasa.pds.tools.validate.ProblemListener;
import gov.nasa.pds.tools.validate.TargetRegistrar;

import java.io.File;

import org.apache.commons.chain.impl.ContextBase;

/**
 * Implements a type-safe context for using validation rules
 * in commands and chains.
 */
public class RuleContext extends ContextBase {

    private static final long serialVersionUID = 1L;
    /** The key used to retrieve the current validation target from the
     * execution context.
     */
    public static final String TARGET_KEY = "validation.target";
    /** The key used to retrieve the definition listener from the
     * execution context.
     */
    public static final String LISTENER_KEY = "validation.listener";
    /** The key used to retrieve the validation registrar from the execution
     * context.
     */
    public static final String REGISTRAR_KEY = "validation.registrar";

    /** The key used to retrieve the rule manager from the context. */
    public static final String RULE_MANAGER_KEY = "validation.rule-manager";

    /** The key used to retrieve the parent target from the context. */
    public static final String PARENT_TARGET_KEY = "validation.parent-target";

    private boolean rootTarget = false;

    /**
     * Gets a value from the context in a type-safe manner.
     *
     * @param key the key
     * @param clazz the expected class
     * @return the value, or null if there is no value with that key
     */
    @SuppressWarnings("unchecked")
    public <T> T getContextValue(String key, Class<T> clazz) {
        return (T) get(key);
    }

    /**
     * Puts a value into the context in a type-safe manner.
     *
     * @param key the key
     * @param value the value
     */
    public <T> void putContextValue(String key, T value) {
        put(key, value);
    }

    public File getTarget() {
        return getContextValue(TARGET_KEY, File.class);
    }

    public void setTarget(File target) {
        putContextValue(TARGET_KEY, target);
    }

    public ProblemListener getProblemListener() {
        return getContextValue(LISTENER_KEY, ProblemListener.class);
    }

    public void setProblemListener(ProblemListener listener) {
        putContextValue(LISTENER_KEY, listener);
    }

    public TargetRegistrar getTargetRegistrar() {
        return getContextValue(REGISTRAR_KEY, TargetRegistrar.class);
    }

    public void setTargetRegistrar(TargetRegistrar registrar) {
        putContextValue(REGISTRAR_KEY, registrar);
    }

    /**
     * Gets the rule manager used to find other rules to apply.
     *
     * @return the rule manager
     */
    public ValidationRuleManager getRuleManager() {
        return getContextValue(RULE_MANAGER_KEY, ValidationRuleManager.class);
    }

    /**
     * Sets the rule manager to use for finding new rules.
     *
     * @param ruleManager the rule manager
     */
    public void setRuleManager(ValidationRuleManager ruleManager) {
        putContextValue(RULE_MANAGER_KEY, ruleManager);
    }

    /**
     * Gets the parent target location.
     *
     * @return the parent target location, or null if there is no parent target
     */
    public String getParentTarget() {
        return getContextValue(PARENT_TARGET_KEY, String.class);
    }

    /**
     * Sets the parent target location.
     *
     * @param parent the parent target location
     */
    public void setParentTarget(String parentLocation) {
        putContextValue(PARENT_TARGET_KEY, parentLocation);
    }

    /**
     * Tests whether this is the root target for the validation.
     *
     * @return true, if this context is for the root target
     */
    public boolean isRootTarget() {
        return rootTarget;
    }

    /**
     * Sets whether this is the root target for the validation.
     *
     * @param flag true, if this context is for the root target
     */
    public void setRootTarget(boolean flag) {
        rootTarget = flag;
    }

}
