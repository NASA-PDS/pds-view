package gov.nasa.pds.tools.validate;

import gov.nasa.pds.tools.label.ExceptionType;

import java.util.Collection;

/**
 * Defines an interface for receiving problems during the validation.
 */
public interface ProblemListener {

	/**
	 * Indicates another problem during the validation.
	 *
	 * @param problem the validation problem encountered
	 */
	void addProblem(ValidationProblem problem);

	/**
	 * Gets the number of error problems encountered.
	 *
	 * @return the error count
	 */
	int getErrorCount();

	/**
	 * Gets the number of warning problems encountered.
	 *
	 * @return the warning count
	 */
	int getWarningCount();

	/**
	 * Gets the number of informational problems encountered.
	 *
	 * @return the info count
	 */
	int getInfoCount();

	/**
	 * Tests whether a target has had problems reported.
	 *
	 * @param location the target location
	 * @param includeChildren true, if problems for child locations should be included
	 * @return true, if problems have been reported
	 */
	boolean hasProblems(String location, boolean includeChildren);

	/**
	 * Gets the maximum severity of problems related to a location.
	 *
	 * @param location the target location
	 * @param includeChildren true, if problems in children of the target should be included
	 * @return the maximum problem severity
	 */
	ExceptionType getSeverity(String location, boolean includeChildren);

	/**
	 * Gets a collection of problems for a given location.
	 *
	 * @param location the target location
	 * @param includeChildren true, if problems for child locations should be included
	 * @return a collection of problems
	 */
	Collection<ValidationProblem> getProblemsForLocation(String location, boolean includeChildren);

}
