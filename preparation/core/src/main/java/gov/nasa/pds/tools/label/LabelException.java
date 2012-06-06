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

/**
 * @author pramirez
 *
 */
public class LabelException extends Exception {
  private static final long serialVersionUID = 6046402124396835033L;
  private ExceptionType exceptionType;
  private String messageKey;
  private String publicId;
  private String systemId;
  private Integer lineNumber;
  private Integer columnNumber;
  
  public LabelException(ExceptionType exceptionType, String messageKey, String publicId, String systemId, Integer lineNumber, Integer columnNumber) {
    this.exceptionType = exceptionType;
    this.messageKey = messageKey;
    this.publicId = publicId;
    this.systemId = systemId;
    this.lineNumber = lineNumber;
    this.columnNumber = columnNumber;
  }
  
  /**
   * Used to capture information captured from schematron
   * @param exceptionType
   * @param message
   * @param test
   * @param location
   * @param fileName
   */
  public LabelException(ExceptionType exceptionType, String message, String filepath, String location, String test) {
    this(exceptionType, message + "[Context: " + location + "; Test: " + test + "]", "", filepath, -1, -1);
  }
  
  public ExceptionType getExceptionType() {
    return this.exceptionType;
  }
  
  public String getMessage() {
    return this.messageKey;
  }
  
  public String getPublicId() {
    return this.publicId;
  }
  
  public String getSystemId() {
    return this.systemId;
  }
  
  public Integer getLineNumber() {
    return this.lineNumber;
  }
  
  public Integer getColumnNumber() {
    return this.columnNumber;
  }
  
  public String getSource() {
    return this.systemId;
  }
}
