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
  
  @Override
  public void error(TransformerException exception) throws TransformerException {
    throw exception;
  }

  @Override
  public void fatalError(TransformerException exception) throws TransformerException {
    throw exception;
  }

  @Override
  public void warning(TransformerException exception) throws TransformerException {
    throw exception;
  }

}
