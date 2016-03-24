package gov.nasa.pds.tools.validate.rule;

import gov.nasa.pds.tools.validate.ProblemDefinition;
import gov.nasa.pds.tools.validate.ProblemListener;
import gov.nasa.pds.tools.validate.TargetRegistrar;
import gov.nasa.pds.tools.validate.ValidationTarget;
import gov.nasa.pds.tools.validate.ValidationProblem;
import gov.nasa.pds.tools.validate.ValidationResourceManager;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.apache.commons.chain.Context;

/**
 * The base class for validation rules. To implement validation rules,
 * write the validation tests as public void methods and annotate
 * them with the {@link ValidationTest} annotation. The tests will
 * be invoked in they appear within the class.
 */
public abstract class AbstractValidationRule implements ValidationRule {

	private RuleContext context;
	private ProblemListener listener;
    private String caption;

	/**
	 * {@inheritDoc}
	 *
	 * Invokes the validation tests in the rule in their declared order.
	 * Validation tests are denoted by public methods annotated with
	 * the {@link ValidationTest} annotation.
	 *
	 * @param theContext the context for the rule, which must be a {@link RuleContext}
	 * @throws {@inheritDoc}
	 */
	@Override
	public boolean execute(Context theContext) throws Exception {
		this.context = (RuleContext) theContext;
		listener = context.getProblemListener();

		// Run each annotated validation test.
		for (Method m : getClass().getMethods()) {
			Annotation a = m.getAnnotation(ValidationTest.class);
			if (a != null) {
				m.invoke(this, new Object[0]);
			}
		}

		return false;
	}

	/**
	 * Gets the rule context.
	 *
	 * @return the context
	 */
	protected RuleContext getContext() {
	    return context;
	}

	/**
	 * Gets a rule context for validating a file or directory inside
	 * the current target.
	 *
	 * @param child the child target
	 * @return a new rule context for validating the child
	 */
	protected RuleContext getChildContext(File child) {
	    RuleContext newContext = new RuleContext();

	    newContext.setProblemListener(context.getProblemListener());
	    newContext.setRuleManager(context.getRuleManager());
	    newContext.setTargetRegistrar(context.getTargetRegistrar());
        newContext.setTarget(child);
        newContext.setRootTarget(false);

	    return newContext;
	}

	/**
	 * Gets the target of this rule.
	 *
	 * @return the validation target
	 */
	protected File getTarget() {
		return context.getTarget();
	}

	/**
	 * Gets the parent target location for this rule.
	 *
	 * @return the parent target location, or null if no parent
	 */
	protected String getParentTarget() {
	    return context.getParentTarget();
	}

    /**
     * Gets the problem listener for this validation rule.
     *
     * @return the problem listener
     */
    protected ProblemListener getListener() {
        return listener;
    }

    /**
     * Gets the target registrar for this validation rule.
     *
     * @return the target registrar
     */
    protected TargetRegistrar getRegistrar() {
        return context.getTargetRegistrar();
    }

    /**
     * Reports an error to the validation listener.
     *
     * @param defn the problem definition
     * @param targetFile the validation target file containing the problem
     * @param lineNumber the line number, or -1 if no line number applies
     * @param columnNumber the column number, or -1 if no column number applies
     */
    protected void reportError(
            ProblemDefinition defn,
            File targetFile,
            int lineNumber,
            int columnNumber
    ) {
        ValidationProblem problem = new ValidationProblem(defn, new ValidationTarget(targetFile), lineNumber, columnNumber, defn.getMessage());
        listener.addProblem(problem);
    }

    /**
     * Reports an error to the validation listener with a custom message.
     *
     * @param defn the problem definition
     * @param target the validation target containing the problem
     * @param lineNumber the line number, or -1 if no line number applies
     * @param columnNumber the column number, or -1 if no column number applies
     * @param message the error message to report
     */
    protected void reportError(
            ProblemDefinition defn,
            File target,
            int lineNumber,
            int columnNumber,
            String message
    ) {
        ValidationProblem problem = new ValidationProblem(defn, new ValidationTarget(target), lineNumber, columnNumber, message);
        listener.addProblem(problem);
    }

	/**
	 * Tests whether a rule is applicable to a target location.
	 *
	 * @param location the target location
	 * @return true, if the rule is applicable to the target, false otherwise
	 */
	@Override
	public abstract boolean isApplicable(String location);

    @Override
    public final String getCaption() {
        return caption;
    }

    /**
     * Sets the caption for this chain.
     *
     * @param caption the new caption string
     */
    public final void setCaption(String caption) {
        this.caption = caption;
    }

}
