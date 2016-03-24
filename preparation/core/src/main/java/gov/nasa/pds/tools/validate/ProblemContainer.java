package gov.nasa.pds.tools.validate;

import gov.nasa.pds.tools.validate.ProblemDefinition.Severity;
import gov.nasa.pds.tools.validate.ValidationProblem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Implements a problem listener that accumulates problems in a list.
 */
public class ProblemContainer implements ProblemListener {

	private List<ValidationProblem> problems = new ArrayList<ValidationProblem>();

    private int errorCount;
    private int warningCount;
    private int infoCount;

	@Override
	public void addProblem(ValidationProblem problem) {
		problems.add(problem);

        switch (problem.getProblem().getSeverity()) {
        case INFO:
            ++infoCount;
            break;
        case WARNING:
            ++warningCount;
            break;
        default:
            ++errorCount;
            break;
        }
	}

	/**
	 * Gets the problems encountered.
	 *
	 * @return the problems
	 */
	public List<ValidationProblem> getProblems() {
		return problems;
	}

	/**
	 * Gets the number of problems reported.
	 *
	 * @return the count of problems
	 */
	public int getProblemCount() {
		return problems.size();
	}

	/**
	 * Clears all problems.
	 */
	public void clear() {
	    problems.clear();
	    errorCount = 0;
	    warningCount = 0;
	    infoCount = 0;
	}

    @Override
    public int getErrorCount() {
        return errorCount;
    }

    @Override
    public int getWarningCount() {
        return warningCount;
    }

    @Override
    public int getInfoCount() {
        return infoCount;
    }

    @Override
    public Collection<ValidationProblem> getProblemsForLocation(
            String location, boolean includeChildren) {
        Collection<ValidationProblem> foundProblems = new ArrayList<ValidationProblem>();

        for (ValidationProblem problem : problems) {
            if (problem.getTarget().getLocation().equals(location)
                || (includeChildren && problem.getTarget().getLocation().startsWith(location))) {
                foundProblems.add(problem);
            }
        }

        return foundProblems;
    }

    @Override
    public boolean hasProblems(String location, boolean includeChildren) {
        for (ValidationProblem problem : problems) {
            if ((includeChildren && problem.getTarget().getLocation().startsWith(location))
                || problem.getTarget().getLocation().equals(location)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Severity getSeverity(String location, boolean includeChildren) {
        Severity severity = Severity.NONE;

        for (ValidationProblem problem : problems) {
            if ((includeChildren && problem.getTarget().getLocation().startsWith(location))
                || problem.getTarget().getLocation().equals(location)) {
                if (problem.getProblem().getSeverity().compareTo(severity) > 0) {
                    severity = problem.getProblem().getSeverity();
                }
            }
        }

        return severity;
    }

}
