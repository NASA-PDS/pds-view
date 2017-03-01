// Copyright 2006-2017, by the California Institute of Technology.
// ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
// Any commercial use must be negotiated with the Office of Technology Transfer
// at the California Institute of Technology.
//
// This software is subject to U. S. export control laws and regulations
// (22 C.F.R. 120-130 and 15 C.F.R. 730-774). To the extent that the software
// is subject to U.S. export control laws and regulations, the recipient has
// the responsibility to obtain export licenses or other export authority as
// may be required before exporting such information to foreign countries or
// providing access to foreign nationals.
//
// $Id$
package gov.nasa.pds.tools.validate;

import gov.nasa.pds.tools.validate.ProblemDefinition;
import gov.nasa.pds.tools.validate.rule.GenericProblems;

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
		if (GenericProblems.UNCAUGHT_EXCEPTION.equals(defn)) {
		  this.message = defn.getMessage();
		  this.message += ": " + message;
		} else {
		  this.message = message==null ? defn.getMessage() : message;
		}
	}

  public ValidationProblem(ProblemDefinition defn, ValidationTarget target, int lineNumber, int columnNumber) {
    this(defn, target, lineNumber, columnNumber, null);
  }	
	
  public ValidationProblem(ProblemDefinition defn, ValidationTarget target, String message) {
    this(defn, target, -1, -1, message);
  }
	
	public ValidationProblem(ProblemDefinition defn, ValidationTarget target) {
	  this(defn, target, -1, -1, null);
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
