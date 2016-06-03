package gov.nasa.pds.tools.validate;

import gov.nasa.pds.tools.validate.ProblemDefinition;

import java.util.Collection;

public class ValidationProblem {

	private int problemDefinitionId;
	private ValidationTarget target;
	private int lineNumber;
	private int columnNumber;
	private Collection<ValidationTarget> ancestorTargets;
	private String message;

	public ValidationProblem(ProblemDefinition defn, ValidationTarget target, int lineNumber, int columnNumber, String message) {
		problemDefinitionId = defn.getID();
		this.target = target;
		this.lineNumber = lineNumber;
		this.columnNumber = columnNumber;
		this.message = message==null ? defn.getMessage() : message;
	}

	public ProblemDefinition getProblem() {
		return ProblemDefinition.findByID(problemDefinitionId);
	}

	public int getProblemDefinitionId() {
		return problemDefinitionId;
	}

	public void setProblemDefinitionId(int defnId) {
		this.problemDefinitionId = defnId;
	}

	public ValidationTarget getTarget() {
		return target;
	}

	public void setTarget(ValidationTarget target) {
		this.target = target;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(int n) {
		lineNumber = n;
	}

	public int getColumnNumber() {
		return columnNumber;
	}

	public void setColumnNumber(int n) {
		columnNumber = n;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Collection<ValidationTarget> getAncestorTargets() {
		return ancestorTargets;
	}

	public void setAncestorTargets(Collection<ValidationTarget> ancestorTargets) {
		this.ancestorTargets = ancestorTargets;
	}

}
