package gov.nasa.pds.tools.validate.rule.pds4;

import gov.nasa.pds.tools.validate.ProblemListener;
import gov.nasa.pds.tools.validate.ValidationProblem;
import gov.nasa.pds.tools.validate.ValidationTarget;
import gov.nasa.pds.tools.label.ExceptionHandler;
import gov.nasa.pds.tools.label.LabelException;

import java.io.File;

/**
 * Implements an exception handler for XML parsing and Schematron
 * errors.
 */
public class ExceptionProcessor implements ExceptionHandler {

    private ProblemListener listener;
    private File target;

    public ExceptionProcessor(ProblemListener listener, File target) {
        this.listener = listener;
        this.target = target;
    }

    @Override
    public void addException(LabelException exception) {
        ValidationProblem problem = new ValidationProblem(PDS4Problems.INVALID_LABEL, new ValidationTarget(target), exception.getLineNumber(), exception.getColumnNumber(), exception.getMessage());
        listener.addProblem(problem);
    }

}
