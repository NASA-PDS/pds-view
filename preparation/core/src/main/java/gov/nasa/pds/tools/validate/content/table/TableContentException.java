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
package gov.nasa.pds.tools.validate.content.table;

import gov.nasa.pds.tools.label.ExceptionType;
import gov.nasa.pds.tools.label.ContentException;

/**
 * Class that stores information about an error that has occurred during
 * table content validation.
 * 
 * @author mcayanan
 *
 */
public class TableContentException extends ContentException {
  private static final long serialVersionUID = -7554382870169790277L;
  private Integer table;
  private Integer record;
  private Integer field;

  /**
   * Constructor.
   * 
   * @param exceptionType The severity level.
   * @param messageKey The message.
   * @param source The data file uri of the exception.
   * @param label The associated label uri of the exception.
   * @param table The index of the table associated with the message.
   * @param record The index of the record associated with the message. 
   * @param field The index of the field associated with the message.
   */
  public TableContentException(ExceptionType exceptionType, String messageKey,
      String source, String label, Integer table, Integer record,
      Integer field) {
    super(exceptionType, messageKey, source, label);
    if (table == null) {
      this.table = -1;
    } else {
      this.table = table;
    }
    if (record == null) {
      this.record = -1;
    } else {
      this.record = record;
    }
    
    if (field == null) {
      this.field = -1;
    } else {
      this.field = field;
    }
  }

  /**
   * Constructor.
   * 
   * @param exceptionType The severity level.
   * @param messageKey The message.
   * @param source The data file uri of the exception.
   * @param label The associated label uri of the exception.
   * @param table The index of the table associated with the message.
   * @param record The index of the record associated with the message. 
   */
  public TableContentException(ExceptionType exceptionType, String message, 
      String source, String label, Integer table, Integer record) {
    this(exceptionType, message, source, label, table, record, -1);
  }
  
  /**
   * Constructor.
   * 
   * @param exceptionType The severity level.
   * @param messageKey The message.
   * @param source The data file uri of the exception.
   */
  public TableContentException(ExceptionType exceptionType, String message,
      String source) {
    this(exceptionType, message, source, "", -1, -1, -1);
  }

  /**
   * 
   * @return the table index.
   */
  public Integer getTable() {
    return this.table;
  }

  /**
   * 
   * @return the record index.
   */
  public Integer getRecord() {
    return this.record;
  }
  
  /**
   * 
   * @return the field index.
   */
  public Integer getField() {
    return this.field;
  }
}
