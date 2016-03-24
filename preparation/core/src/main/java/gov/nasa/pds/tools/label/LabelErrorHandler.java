//	Copyright 2009-2010, by the California Institute of Technology.
//	ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
//	Any commercial use must be negotiated with the Office of Technology 
//	Transfer at the California Institute of Technology.
//	
//	This software is subject to U. S. export control laws and regulations 
//	(22 C.F.R. 120-130 and 15 C.F.R. 730-774). To the extent that the software 
//	is subject to U.S. export control laws and regulations, the recipient has 
//	the responsibility to obtain export licenses or other export authority as 
//	may be required before exporting such information to foreign countries or 
//	providing access to foreign nationals.
//	
//	$Id$
//

package gov.nasa.pds.tools.label;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * @author pramirez
 * 
 */
public class LabelErrorHandler implements ErrorHandler {
  private ExceptionHandler exceptions;

  public LabelErrorHandler(ExceptionHandler exceptions) {
    this.exceptions = exceptions;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.xml.sax.ErrorHandler#error(org.xml.sax.SAXParseException)
   */
  @Override
  public void error(SAXParseException exception) throws SAXException {
    exceptions.addException(new LabelException(ExceptionType.ERROR, exception
        .getMessage(), exception.getPublicId(), exception.getSystemId(),
        exception.getLineNumber(), exception.getColumnNumber()));
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.xml.sax.ErrorHandler#fatalError(org.xml.sax.SAXParseException)
   */
  @Override
  public void fatalError(SAXParseException exception) throws SAXException {
    exceptions.addException(new LabelException(ExceptionType.FATAL, exception
        .getMessage(), exception.getPublicId(), exception.getSystemId(),
        exception.getLineNumber(), exception.getColumnNumber()));
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.xml.sax.ErrorHandler#warning(org.xml.sax.SAXParseException)
   */
  @Override
  public void warning(SAXParseException exception) throws SAXException {
    exceptions.addException(new LabelException(ExceptionType.WARNING, exception
        .getMessage(), exception.getPublicId(), exception.getSystemId(),
        exception.getLineNumber(), exception.getColumnNumber()));
  }

}
