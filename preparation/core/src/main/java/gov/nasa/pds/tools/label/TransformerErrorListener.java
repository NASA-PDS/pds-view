//  Copyright 2009-2014, by the California Institute of Technology.
//  ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
//  Any commercial use must be negotiated with the Office of Technology
//  Transfer at the California Institute of Technology.
//
//  This software is subject to U. S. export control laws and regulations
//  (22 C.F.R. 120-130 and 15 C.F.R. 730-774). To the extent that the software
//  is subject to U.S. export control laws and regulations, the recipient has
//  the responsibility to obtain export licenses or other export authority as
//  may be required before exporting such information to foreign countries or
//  providing access to foreign nationals.
//
//  $Id$
//
package gov.nasa.pds.tools.label;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

/**
 * Listener class to simply throw exceptions when an error occurs when
 * transforming a schematron. This prevents transformer error messages from
 * appearing on the standard out.
 *
 * @author mcayanan
 *
 */
public class TransformerErrorListener implements ErrorListener {
  private ExceptionHandler exceptions;

  /**
   * Constructor.
   *
   * @param exceptions A container to hold problems that occur during
   * the transform process.
   */
  public TransformerErrorListener(ExceptionHandler exceptions) {
    this.exceptions = exceptions;
  }

  @Override
  public void error(TransformerException exception)
      throws TransformerException {
    SourceLocator locator = exception.getLocator();
    exceptions.addException(new LabelException(ExceptionType.ERROR,
        exception.getMessage(), locator.getPublicId(),
        locator.getSystemId(), locator.getLineNumber(),
        locator.getColumnNumber()));
  }

  @Override
  public void fatalError(TransformerException exception) throws TransformerException {
    SourceLocator locator = exception.getLocator();
    exceptions.addException(new LabelException(ExceptionType.FATAL,
        exception.getMessage(), locator.getPublicId(),
        locator.getSystemId(), locator.getLineNumber(),
        locator.getColumnNumber()));
  }

  @Override
  public void warning(TransformerException exception) throws TransformerException {
    SourceLocator locator = exception.getLocator();
    exceptions.addException(new LabelException(ExceptionType.WARNING,
        exception.getMessage(), locator.getPublicId(),
        locator.getSystemId(), locator.getLineNumber(),
        locator.getColumnNumber()));
  }

}
