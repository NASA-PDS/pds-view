// Copyright 2006-2018, by the California Institute of Technology.
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
package gov.nasa.pds.tools.validate.content.array;

import java.util.Arrays;

import gov.nasa.pds.tools.label.ContentException;
import gov.nasa.pds.tools.label.ExceptionType;

/**
 * Class that holds messages related to Array Content Validation.
 * 
 * @author mcayanan
 *
 */
public class ArrayContentException extends ContentException {
  private static final long serialVersionUID = 602027709524944154L;
  private Integer array;
  private int[] location;
   
  /**
   * Constructor.
   * 
   * @param exceptionType exception type.
   * @param messageKey message or message key.
   * @param source The url of the data file associated with this message.
   * @param label The url of the label file.
   * @param array The index of the array associated with this message.
   * @param location The location associated with the message.
   */
  public ArrayContentException(ExceptionType exceptionType, 
      String messageKey, String source, String label, Integer array, 
      int[] location) {
    super(exceptionType, messageKey, source, label);
    if (array == null) {
      this.array = -1;
    } else {
      this.array = array;
    }
    if (location != null) {
      this.location = location;
    } else {
      this.location = null;
    }
  }
  
  /**
   * Constructor.
   * 
   * @param exceptionType exception type.
   * @param messageKey message or message key.
   * @param source The url of the data file associated with this message.
   */
  public ArrayContentException(ExceptionType exceptionType, String message, 
      String source) {
    this(exceptionType, message, source, "", null, null);
  }
  
  /**
   * 
   * @return return the index of the array.
   */
  public Integer getArray() {
    return this.array;
  }

  public String getLocation() {
    String loc = null;
    if (this.location != null) {
      loc = Arrays.toString(this.location);
      if (this.location.length > 1) {
        loc = loc.replaceAll("\\[", "\\(");
        loc = loc.replaceAll("\\]", "\\)");
      } else {
        loc = loc.replaceAll("\\[", "");
        loc = loc.replaceAll("\\]", "");
      }
    }
    return loc;
  }
}
