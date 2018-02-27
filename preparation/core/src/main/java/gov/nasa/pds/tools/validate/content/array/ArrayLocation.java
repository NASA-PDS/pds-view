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

/**
 * Class that holds a specific location in an Array.
 * 
 * @author mcayanan
 *
 */
public final class ArrayLocation {
  /** The label associated with the data. */
  private String label;
  
  /** The data file associated with the record. */
  private String dataFile;
  
  /** The index of the table associated with the record. */
  private int array;
  
  /** The index of the record. */
  private Axis plane;  
  
  /** The row axis. */
  private Axis row;
  
  /** The column axis. */
  private Axis column;
  
  /**
   * Constructor.
   * 
   * @param label The url to the label.
   * @param dataFile The url to the data file.
   * @param array The array index.
   * @param plane The plane axis.
   * @param row The row axis.
   * @param column The column axis.
   */
  public ArrayLocation(String label, String dataFile, int array, Axis plane, Axis row, Axis column) {
    this.label = label;
    this.dataFile = dataFile;
    this.array = array;
    this.plane = plane;
    this.row = row;
    this.column = column;
  }
  
  /**
   * 
   * @return the label.
   */
  public String getLabel() {
    return this.label;
  }
  
  /**
   * 
   * @return the data file.
   */
  public String getDataFile() {
    return this.dataFile;
  }
  
  /**
   * 
   * @return the array index.
   */
  public int getArray() {
    return this.array;
  }
  
  /**
   * 
   * @return the plane.
   */
  public Axis getPlane() {
    return this.plane;
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
}
