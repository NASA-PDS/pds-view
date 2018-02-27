//	Copyright 2009-2018, by the California Institute of Technology.
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
package gov.nasa.pds.tools.label;

/**
 * Class to store messages related to content validation.
 * 
 * @author mcayanan
 *
 */
public class ContentException extends LabelException {
  /** Generated serialVersionUID/ */
  private static final long serialVersionUID = -5637803764059200287L;
  
  /** url to the label. */
  protected String label;

  /**
   * Constructor.
   * 
   * @param exceptionType exception type.
   * @param message message.
   * @param source url of the data file associated with this message.
   * @param label url of the label file.
   */
  public ContentException(ExceptionType exceptionType, String message, 
      String source, String label) {
    super(exceptionType, message, source);
    this.label = label;
  }
  
  /**
   * 
   * @return Returns the label.
   */
  public String getLabel() {
    return this.label;
  }
}
