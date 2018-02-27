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

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;

import org.apache.commons.lang3.Range;

import com.google.common.primitives.UnsignedInteger;
import com.google.common.primitives.UnsignedLong;
import com.sun.media.jai.codec.SeekableStream;

import gov.nasa.arc.pds.xml.generated.Array;
import gov.nasa.arc.pds.xml.generated.ElementArray;
import gov.nasa.arc.pds.xml.generated.ObjectStatistics;
import gov.nasa.arc.pds.xml.generated.SpecialConstants;
import gov.nasa.pds.label.object.ArrayObject;
import gov.nasa.pds.objectAccess.DataType.NumericDataType;
import gov.nasa.pds.tools.label.ExceptionType;
import gov.nasa.pds.tools.label.LabelException;
import gov.nasa.pds.tools.validate.ProblemListener;

/**
 * Class that performs content validation on Array objects.
 * 
 * @author mcayanan
 *
 */
public class ArrayContentValidator {
  
  /** Container to capture messages. */
  private ProblemListener listener;
  
  /** The label associated with the Array being validated. */
  private URL label;
  
  /** The data file containing the array content. */
  private URL dataFile;
  
  /** The index of the array. */
  private int arrayIndex;
  
  /**
   * Constructor.
   * 
   * @param listener to capture messages.
   * @param label the label file.
   * @param dataFile the data file.
   * @param arrayIndex the index of the array.
   */
  public ArrayContentValidator(ProblemListener listener, URL label, 
      URL dataFile, int arrayIndex) {
    this.listener = listener;
    this.label = label;
    this.dataFile = dataFile;
    this.arrayIndex = arrayIndex;
  }
  
  /**
   * Validates the given array.
   * 
   * @param array Object representation of the array as described in the label.
   * @param arrayObject Object representation of the array.
   */
  public void validate(Array array, ArrayObject arrayObject) {
    NumericDataType dataType = Enum.valueOf(NumericDataType.class, 
        array.getElementArray().getDataType());
    Axis planes = null;
    Axis rows = null;
    Axis columns = null;
    if (array.getAxes() == 2) {
        rows = new Axis(array.getAxisArraies().get(0).getAxisName(),
            array.getAxisArraies().get(0).getElements());
        columns = new Axis(array.getAxisArraies().get(1).getAxisName(),
            array.getAxisArraies().get(1).getElements());
    } else if (array.getAxes() == 3) {
      planes = new Axis(array.getAxisArraies().get(0).getAxisName(),
          array.getAxisArraies().get(0).getElements());
      rows = new Axis(array.getAxisArraies().get(1).getAxisName(),
          array.getAxisArraies().get(1).getElements());
      columns = new Axis(array.getAxisArraies().get(2).getAxisName(),
          array.getAxisArraies().get(2).getElements());
    }
    SeekableStream si = null;
    int plane = 0;
    int start_plane = 0;
    if (planes == null) {
      start_plane = -2;
      planes = new Axis(-1);
    }
    int row = 0;
    int column = 0;
    try {
      si = SeekableStream.wrapInputStream(arrayObject.getInputStream(), false);
      for (plane = start_plane; plane < planes.getElement(); plane++) {
        for (row = 0; row < rows.getElement(); row++) {
          for (column = 0; column < columns.getElement(); column++) {
            ArrayLocation location = new ArrayLocation(label.toString(), 
                dataFile.toString(), arrayIndex, 
                new Axis(planes.getName(), (plane+1)), 
                new Axis(rows.getName(), (row+1)), 
                new Axis(columns.getName(), (column+1)));
            Number value = null;
            Range rangeChecker = null;
            switch (dataType) {
            case SignedByte:
              value = si.readByte();
              rangeChecker = Range.between(Byte.MIN_VALUE, Byte.MAX_VALUE);
              break;
            case UnsignedByte:
              value = si.readUnsignedByte();
              rangeChecker = Range.between(0, 255);
              break;
            case UnsignedLSB2:
              value = si.readUnsignedShortLE();
              rangeChecker = Range.between(0, 65535);
              break;
            case SignedLSB2:
              value = si.readShortLE();
              rangeChecker = Range.between(Short.MIN_VALUE, Short.MAX_VALUE);
              break;
            case UnsignedMSB2:
              value = si.readUnsignedShort();
              rangeChecker = Range.between(0, 65535);
              break;              
            case SignedMSB2:
              value = si.readShort();
              rangeChecker = Range.between(Short.MIN_VALUE, Short.MAX_VALUE);
              break;
            case UnsignedLSB4:
              value = si.readUnsignedIntLE();
              rangeChecker = Range.between(UnsignedInteger.ZERO, UnsignedInteger.MAX_VALUE);
              break;
            case SignedLSB4:
              value = si.readIntLE();
              rangeChecker = Range.between(Integer.MIN_VALUE, Integer.MAX_VALUE);
              break;
            case UnsignedMSB4:
              value = si.readUnsignedInt();
              rangeChecker = Range.between(UnsignedInteger.ZERO, UnsignedInteger.MAX_VALUE);
              break;
            case SignedMSB4:
              value = si.readInt();
              rangeChecker = Range.between(Integer.MIN_VALUE, Integer.MAX_VALUE);
              break;
            case UnsignedLSB8:
              value = UnsignedLong.valueOf(si.readLongLE());
              rangeChecker = Range.between(UnsignedLong.ZERO, UnsignedLong.MAX_VALUE);
              break;
            case SignedLSB8:
              value = Long.valueOf(si.readLongLE());
              rangeChecker = Range.between(Long.MIN_VALUE, Long.MAX_VALUE);
              break;
            case UnsignedMSB8:
              value = UnsignedLong.valueOf(si.readLong());
              rangeChecker = Range.between(UnsignedLong.ZERO, UnsignedLong.MAX_VALUE);
              break;        
            case SignedMSB8:
              value = Long.valueOf(si.readLong());
              rangeChecker = Range.between(Long.MIN_VALUE, Long.MAX_VALUE);
              break;
            case IEEE754LSBSingle:
              value = si.readFloatLE();
              rangeChecker = Range.between(Long.MIN_VALUE, Long.MAX_VALUE);
              break;
            case IEEE754MSBSingle:
              value = si.readFloat();
              rangeChecker = Range.between(Long.MIN_VALUE, Long.MAX_VALUE);
              break;
            case IEEE754LSBDouble:
              value = si.readDoubleLE();
              rangeChecker = Range.between(Double.MIN_VALUE, Double.MAX_VALUE);
              break;
            case IEEE754MSBDouble:
              value = si.readDouble();
              rangeChecker = Range.between(Double.MIN_VALUE, Double.MAX_VALUE);
              break;
            }
            boolean isSpecialConstant = false;
            if (array.getSpecialConstants() != null) {
              isSpecialConstant = isSpecialConstant(value, array.getSpecialConstants());
            }
            if (!isSpecialConstant) {
              if (!rangeChecker.contains(value)) {
                  addArrayException(ExceptionType.ERROR,
                    "Value is not within the valid range of the data type '"
                      + dataType.name() + "': " + value.toString(),
                    location
                     );
              }
              if (array.getObjectStatistics() != null) {
                // At this point, it seems like it only makes sense
                // to check that the values are within the min/max values
                checkObjectStats(value, array.getElementArray(),
                    array.getObjectStatistics(), location);
              }
            } else {
              addArrayException(ExceptionType.INFO,
                  "Value is a special constant defined in the label: "
                      + value.toString(),
                  location
              );              
            }
          }
        }
      }
    } catch (IOException io) {
      listener.addProblem(new LabelException(ExceptionType.FATAL, 
          "Error occurred while reading data file: " + io.getMessage(), 
          label.toString()));
    }
  }
  
  /**
   * Checks if the given value is a Special Constant defined in the label.
   * 
   * @param value The value to check.
   * @param constants An object representation of the Special_Constants area
   * in a label.
   * 
   * @return true if the given value is a Special Constant.
   */
  private boolean isSpecialConstant(Number value, SpecialConstants constants) {
    if (constants.getErrorConstant() != null) {
      if (value.toString().equals(constants.getErrorConstant())) {
        return true;
      }
    } 
    if (constants.getInvalidConstant() != null) { 
      if (value.toString().equals(constants.getInvalidConstant())) {
        return true;
      }
    }
    if (constants.getMissingConstant() != null) {
      if (value.toString().equals(constants.getMissingConstant())) {
        return true;
      }
    }
    if (constants.getHighInstrumentSaturation() != null) {
      if (value.toString().equals(constants.getHighInstrumentSaturation())) {
        return true;
      }
    }
    if (constants.getHighRepresentationSaturation() != null) {
      if (value.toString().equals(constants.getHighRepresentationSaturation())) {
        return true;
      }
    }
    if (constants.getLowInstrumentSaturation() != null) {
      if (value.toString().equals(constants.getLowInstrumentSaturation())) {
        return true;
      }
    }
    if (constants.getLowRepresentationSaturation() != null) {
      if (value.toString().equals(constants.getLowRepresentationSaturation())) {
        return true;
      }
    }
    if (constants.getNotApplicableConstant() != null) {
      if (value.toString().equals(constants.getNotApplicableConstant())) {
        return true;
      }
    }
    if (constants.getSaturatedConstant() != null) {
      if (value.toString().equals(constants.getSaturatedConstant())) {
        return true;
      }
    }
    if (constants.getUnknownConstant() != null) {
      if (value.toString().equals(constants.getUnknownConstant())) {
        return true;
      }
    }
    if (constants.getValidMaximum() != null) {
      if (value.toString().equals(constants.getValidMaximum())) {
        return true;
      }
    }
    if (constants.getValidMinimum() != null) {
      if (value.toString().equals(constants.getValidMinimum())) {
        return true;
      }
    }
    return false;
  }
  
  /**
   * Checks the given number against the object statistics characteristics
   * as defined in the product label.
   * 
   * @param value The element value.
   * @param elementArray The Element Array.
   * @param objectStats The Object Statistics.
   * @param location The location of the given element value.
   */
  private void checkObjectStats(Number value, ElementArray elementArray, 
      ObjectStatistics objectStats, ArrayLocation location) {
    if (objectStats.getMinimum() != null) {
      if (value.doubleValue() < objectStats.getMinimum()) {
        addArrayException(ExceptionType.ERROR, 
            "Value is less than the minimum value in the label (min="
            + objectStats.getMinimum().toString()
            + ", got=" + value.toString() + ").", location);
      }
    }
    if (objectStats.getMaximum() != null) {
      if (value.doubleValue() > objectStats.getMaximum()) {
        addArrayException(ExceptionType.ERROR, 
            "Value is greater than the maximum value in the label (max="
            + objectStats.getMinimum().toString()
            + ", got=" + value.toString() + ").", location);        
      }
    }
    double scalingFactor = 1.0;
    double valueOffset = 0.0;
    boolean checkScaledValue = false;
    if (elementArray.getScalingFactor() != null) {
      scalingFactor = elementArray.getScalingFactor();
      checkScaledValue = true;
    }
    if (elementArray.getValueOffset() != null) {
      valueOffset = elementArray.getValueOffset();
      checkScaledValue = true;
    }
    if (checkScaledValue) {
      double scaledValue = (value.doubleValue() * scalingFactor) + valueOffset;
      if (objectStats.getMinimumScaledValue() != null) {
        if (compare(scaledValue, objectStats.getMinimumScaledValue()) == -1) {
          addArrayException(ExceptionType.ERROR, 
              "Scaled value is less than the scaled minimum value in the label (min="
              + objectStats.getMinimumScaledValue().toString()
              + ", got=" + value.toString() + ").", location);          
        }
      }
      if (objectStats.getMaximumScaledValue() != null) {
        if (compare(scaledValue, objectStats.getMaximumScaledValue()) == 1) {
          addArrayException(ExceptionType.ERROR, 
              "Scaled value is greater than the scaled maximum value in the label (max="
              + objectStats.getMaximumScaledValue().toString()
              + ", got=" + value.toString() + ").", location);             
        }
      }
    }
  }
  
  /**
   * Compares 2 double values. If the values have different
   * precisions, this method will set the precisions to the 
   * same scale before doing a comparison.
   * 
   * @param value The element value.
   * @param minMax The min or max value to compare against.
   * 
   * @return -1 if value is less than minMax, 0 if they are equal
   *  and 1 if value is greater than minMax.
   */
  private int compare(Double value, Double minMax) {
    BigDecimal bdValue = new BigDecimal(value.toString());
    BigDecimal bdMinMax = new BigDecimal(minMax.toString());
    if (bdValue.precision() == bdMinMax.precision()) {
      return bdValue.compareTo(bdMinMax);
    } else if (bdValue.precision() > bdMinMax.precision()) {
      BigDecimal scaledValue = bdValue.setScale(bdMinMax.precision(), RoundingMode.HALF_UP);
      return scaledValue.compareTo(bdMinMax);
    } else {
      BigDecimal scaledMinMax = bdMinMax.setScale(bdValue.precision(), RoundingMode.HALF_UP);
      return bdValue.compareTo(scaledMinMax);
    }
  }
  
  /**
   * Records an Array Content related message to the listener.
   * 
   * @param exceptionType exception type.
   * @param message The message to record.
   * @param location The array location associated with the message.
   */
  private void addArrayException(ExceptionType exceptionType, String message, 
      ArrayLocation location) {
    listener.addProblem(
        new ArrayContentException(exceptionType, 
            message,
            location.getDataFile(),
            location.getLabel(),
            location.getArray(),
            location.getRow(),
            location.getColumn(),
            location.getPlane()));
  }
}
