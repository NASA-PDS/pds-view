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
  private Axis row;
  private Axis column;
  private Axis plane;
  
  /**
   * Constructor.
   * 
   * @param exceptionType exception type.
   * @param messageKey message or message key.
   * @param source The url of the data file associated with this message.
   * @param label The url of the label file.
   * @param array The index of the array associated with this message.
   * @param row The row associated with this message.
   * @param column The column associated with this message.
   */
  public ArrayContentException(ExceptionType exceptionType, 
      String messageKey, String source, String label, Integer array, 
      Axis row, Axis column) {
    this(exceptionType, messageKey, source, label, array, row, column, null);
  }
  
  /**
   * Constructor.
   * 
   * @param exceptionType exception type.
   * @param messageKey message or message key.
   * @param source The url of the data file associated with this message.
   * @param label The url of the label file.
   * @param array The index of the array associated with this message.
   * @param row The row associated with this message.
   * @param column The column associated with this message.
   * @param plane The plane associated with this message.
   */
  public ArrayContentException(ExceptionType exceptionType, 
      String messageKey, String source, String label, Integer array, 
      Axis row, Axis column, Axis plane) {
    super(exceptionType, messageKey, source, label);
    if (array == null) {
      this.array = -1;
    } else {
      this.array = array;
    }
    if (row == null) {
      this.row = null;
    } else {
      this.row = row;
    }
    if (column == null) {
      this.column = null;
    } else {
      this.column = column;
    }
    if (plane == null) {
      this.plane = null;
    } else {
      this.plane = plane;
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
    this(exceptionType, message, source, "", null, null, null);
  }
  
  /**
   * 
   * @return return the index of the array.
   */
  public Integer getArray() {
    return this.array;
  }
  
  /**
   * 
   * @return the row.
   */
  public Axis getRow() {
    return this.row;
  }
  
  /**
   * 
   * @return the column.
   */
  public Axis getColumn() {
    return this.column;
  }
  
  public Axis getPlane() {
    return this.plane;
  }
}
