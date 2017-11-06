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

import java.util.Collection;

import gov.nasa.pds.tools.label.ExceptionType;
import gov.nasa.pds.tools.label.LabelException;
import gov.nasa.pds.tools.label.ValidateExceptionHandler;

/**
 * Listener class intended to propagate the problems to a handler for
 * further processing.
 * 
 * @author mrose, mcayanan
 *
 */
public class ListenerExceptionPropagator implements ProblemListener {
  
  private ValidateExceptionHandler handler;
  int errorCount;
  int warningCount;
  int infoCount;

  public ListenerExceptionPropagator(ValidateExceptionHandler handler) {
    this.handler = handler;
  }

  @Override
  public void addProblem(ValidationProblem problem) {
    ExceptionType type;
    
    switch (problem.getProblem().getSeverity()) {
    case ERROR:
      type = ExceptionType.ERROR;
      ++errorCount;
      break;
    case WARNING:
      type = ExceptionType.WARNING;
      ++warningCount;
      break;
    default:
      type = ExceptionType.INFO;
      ++infoCount;
      break;
    }
    LabelException ex = new LabelException(
        type,
        problem.getMessage(),
        "",
        problem.getTarget().getLocation(),
        problem.getLineNumber(),
        problem.getColumnNumber()
    );
    handler.addException(ex);
  }
  
  
  
  @Override
  public void addProblem(LabelException exception) {
    ExceptionType type = exception.getExceptionType();
    if (ExceptionType.FATAL.equals(type) || ExceptionType.ERROR.equals(type)) {
      ++errorCount;
    } else if (ExceptionType.WARNING.equals(type)) {
      ++warningCount;
    } else {
      ++infoCount;
    }
    handler.addException(exception); 
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
  public boolean hasProblems(String location, boolean includeChildren) {
    return false;
  }

  @Override
  public ExceptionType getSeverity(String location, boolean includeChildren) {
    return null;
  }

  @Override
  public Collection<ValidationProblem> getProblemsForLocation(
      String location, boolean includeChildren) {
    return null;
  }

  @Override
  public void addLocation(String location) {
    handler.addLocation(location);
  }
  
  public void record(String location) {
    handler.record(location);
  }
  
  public void printHeader(String title) {
    handler.printHeader(title);
  }
}
