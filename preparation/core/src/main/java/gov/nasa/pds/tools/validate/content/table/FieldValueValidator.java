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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.google.common.primitives.UnsignedLong;

import gov.nasa.pds.label.object.FieldDescription;
import gov.nasa.pds.label.object.FieldType;
import gov.nasa.pds.label.object.RecordLocation;
import gov.nasa.pds.label.object.TableRecord;
import gov.nasa.pds.tools.label.ExceptionType;
import gov.nasa.pds.tools.validate.ProblemListener;
import gov.nasa.pds.tools.validate.rule.pds4.DateTimeValidator;

/**
 * Class that performs content validation on the field values of a given
 *  record.
 * 
 * @author mcayanan
 *
 */
public class FieldValueValidator {
  /** List of invalid values. */
  private final List<String> INF_NAN_VALUES = Arrays.asList("INF", "-INF", "+INF", "NAN", "-NAN", "+NAN");
  
  /** List of valid datetime formats. */
  private static final Map<String, String> DATE_TIME_VALID_FORMATS = new HashMap<String, String>();
  static{
    DATE_TIME_VALID_FORMATS.put(FieldType.ASCII_DATE_DOY.getXMLType(), "YYYY[Z], YYYY-DOY[Z]");
    DATE_TIME_VALID_FORMATS.put(FieldType.ASCII_DATE_TIME_DOY.getXMLType(), 
        "YYYY[Z], YYYY-DOYThh[Z], YYYY-DOYThh:mm[Z], YYYY-DOYThh:mm:ss[.fff][Z]");
    DATE_TIME_VALID_FORMATS.put(FieldType.ASCII_DATE_TIME_DOY_UTC.getXMLType(), 
        "YYYYZ, YYYY-DOYThhZ, YYYY-DOYThh:mmZ, YYYY-DOYThh:mm:ss[.fff]Z");
    DATE_TIME_VALID_FORMATS.put(FieldType.ASCII_DATE_TIME_YMD.getXMLType(), 
        "YYYY[Z], YYYY-MM-DDThh[Z], YYYY-MM-DDThh:mm[Z], YYYY-MM-DDThh:mm:ss[.fff][Z]");
    DATE_TIME_VALID_FORMATS.put(FieldType.ASCII_DATE_TIME_YMD_UTC.getXMLType(), 
        "YYYYZ, YYYY-MM-DDThhZ, YYYY-MM-DDThh:mmZ, YYYY-MM-DDThh:mm:ss[.fff]Z");
    DATE_TIME_VALID_FORMATS.put(FieldType.ASCII_DATE_YMD.getXMLType(), 
        "YYYY[Z], YYYY-MM[Z], YYYY-MM-DD[Z]");
  }
  
  /** Container to capture messages. */
  private ProblemListener listener;
  
  private Pattern formatPattern;
  
  /**
   * Constructor.
   * 
   * @param target The label.
   * @param dataFile The data file.
   */
  public FieldValueValidator(ProblemListener listener) {
    this.listener = listener;
    this.formatPattern = Pattern.compile("%([\\+,-])?([0-9]+)(\\.([0-9]+))?([doxfeEs])");
  }
  
  /**
   * Validates the field values in the given record.
   * 
   * @param record The record containing the fields to validate.
   * @param fields An array of the field descriptions.
   */
  public void validate(TableRecord record, FieldDescription[] fields) {
    for (int i = 0; i < fields.length; i++) {
      try {
        String value = record.getString(i+1);
        // Check that the length of the field value does not exceed the maximum
        // field length, if specified
        if (fields[i].getMaxLength() != -1) {
          if (value.trim().length() > fields[i].getMaxLength()) {
            String message = "The length of the value '" + value.trim()
              + "' exceeds the defined max field length (expected max "
              + fields[i].getMaxLength() + ", got " + value.trim().length() + ")"; 
            addTableException(ExceptionType.ERROR, 
                    message,
                    record.getLocation(),
                    (i + 1));              
          }        
        }
        // Check that the value of the field matches the defined data type
        if (!value.trim().isEmpty()) {
          try {
            checkType(value.trim(), fields[i].getType());
            addTableException(ExceptionType.INFO, 
                "Value '" + value.trim() + "' matches its data type '"
                    + fields[i].getType().getXMLType() + "'.",
                record.getLocation(),
                (i + 1));
          } catch (Exception e) {
            String message = "Value does not match its data type '"
                + fields[i].getType().getXMLType() + "': " + e.getMessage(); 
            addTableException(ExceptionType.ERROR, 
                    message,
                    record.getLocation(),
                    (i + 1));
          }
          // Check that the format of the field value in the table matches the defined formation of the field
          if (!fields[i].getFormat().isEmpty()) {
            checkFormat(value, fields[i].getFormat(), i + 1, record.getLocation());        
          }
          // Check that the field value is within the defined min/max values
          if (fields[i].getMinimum() != null || fields[i].getMaximum() != null) {
            checkMinMax(value.trim(), fields[i].getMinimum(), fields[i].getMaximum(), 
              i + 1, record.getLocation());            
          } 
        } else {
          addTableException(ExceptionType.INFO, "Field is blank.", 
              record.getLocation(), (i+1));
        }
      } catch (Exception e) {
        addTableException(ExceptionType.ERROR, 
                "Error while getting field value: " + e.getMessage(),
                record.getLocation(),
                (i + 1));
      }
    }
  }
  
  /**
   * Checks that the given value is within the min/max range.
   * 
   * @param value The field value to validate.
   * @param minimum The minimum value.
   * @param maximum The maximum value.
   * @param recordLocation The record location where the field is located.
   */
  private void checkMinMax(String value, Double minimum, Double maximum, 
      int fieldIndex, RecordLocation recordLocation) {
    value = value.trim();
    if (NumberUtils.isCreatable(value)) {
      Number number = NumberUtils.createNumber(value);
      if (minimum != null) {
        if (number.doubleValue() < minimum.doubleValue()) {
          String message = "Field has a value '" + value
              + "' that is less than the defined minimum value '" + minimum.toString() +  "'. "; 
          addTableException(ExceptionType.ERROR, 
                  message,
                  recordLocation,
                  fieldIndex);
        } else {
          String message = "Field has a value '" + value
              + "' that is greater than the defined minimum value '" + minimum.toString() +  "'. "; 
          addTableException(ExceptionType.INFO, 
              message,
              recordLocation,
              fieldIndex);          
        }
      }
      if (maximum != null) {
        if (number.doubleValue() > maximum.doubleValue()) {
          String message = "Field has a value '" + value
              + "' that is greater than the defined maximum value '" + maximum.toString() +  "'. "; 
          addTableException(ExceptionType.ERROR,
             message,
             recordLocation,
             fieldIndex);
        } else {
          String message = "Field has a value '" + value
              + "' that is less than the defined maximum value '" + maximum.toString() +  "'. "; 
          addTableException(ExceptionType.INFO,
             message,
             recordLocation,
             fieldIndex);          
        }
      }
    } else {
      // Value cannot be converted to a number
      String message = "Cannot cast field value '" + value
          + "' to a Number data type to validate against the min/max values defined in the label.";
      addTableException(ExceptionType.ERROR,
              message,
              recordLocation,
              fieldIndex);
    }
  }
  
  /**
   * Checks that the given value matches its defined field type.
   * 
   * @param value The field value to validate.
   * @param type The field type to check against.
   * 
   * @throws Exception If the value was found to be invalid.
   */
  private void checkType(String value, FieldType type) throws Exception {
    //File and directory naming rules are checked in the FileAndDirectoryNamingRule class
    if (INF_NAN_VALUES.contains(value)) {
      throw new Exception(value + " is not allowed");
    }
    if (FieldType.ASCII_INTEGER.getXMLType().equals(type.getXMLType())) {
      String pattern = "[+-]?\\d+";
      if (value.matches(pattern)) {
        try {
          Long.parseLong(value);
        } catch (NumberFormatException e) {
          throw new Exception("Could not convert to long: " + value);
        }
      } else {
        throw new Exception("'" + value + "' does not match the pattern '" + pattern + "'");
      }
    } else if (FieldType.ASCII_NONNEGATIVE_INTEGER.getXMLType().equals(type.getXMLType())) {
      String pattern = "[+]?\\d+";
      if (value.matches(pattern)) {
        try {
          UnsignedLong.valueOf(value);
        } catch (NumberFormatException e) {
          throw new Exception("Could not convert to an unsigned long: " + value);
        }
      } else {
        throw new Exception("'" + value + "' does not match the pattern '" + pattern + "'");        
      }
    } else if (FieldType.ASCII_REAL.getXMLType().equals(type.getXMLType())) {
      String pattern = "[-+]?[0-9]+(\\.?[0-9]+)?([eE][-+]?[0-9]+)?";
      if (value.matches(pattern)) {
        try {
          Double.parseDouble(value);
        } catch (NumberFormatException e) {
          throw new Exception("Could not convert to a double: " + value);
        }
      } else {
        throw new Exception("'" + value + "' does not match the pattern '" + pattern + "'");        
      }
    } else if (FieldType.ASCII_NUMERIC_BASE2.getXMLType().equals(type.getXMLType())) {
      String pattern = "[0-1]{1,255}";
      if (value.matches(pattern)) {
        try {
          Integer.parseInt(value, 2);
        } catch (NumberFormatException e) {
          throw new Exception("Could not convert to a base-2 integer: " + value);
        }
      } else {
        throw new Exception("'" + value + "' does not match the pattern '" + pattern + "'");
      }
    } else if (FieldType.ASCII_NUMERIC_BASE8.getXMLType().equals(type.getXMLType())) {
      String pattern = "[0-7]{1,255}";
      if (value.matches(pattern)) {
        try {
          Integer.parseInt(value, 8);
        } catch (NumberFormatException e) {
          throw new Exception("Could not convert to a base-8 integer: " + value);
        }
      } else {
        throw new Exception("'" + value + "' does not match the pattern '" + pattern + "'");
      }
    } else if (FieldType.ASCII_NUMERIC_BASE16.getXMLType().equals(type.getXMLType())) {
      String pattern = "[0-9a-fA-F]{1,255}";
      if (value.matches(pattern)) {
        try {
          Integer.parseInt(value, 16);
        } catch (NumberFormatException e) {
          throw new Exception("Could not convert to a base-16 integer: " + value);
        }
      } else {
        throw new Exception("'" + value + "' does not match the pattern '" + pattern + "'");
      }
    } else if (FieldType.ASCII_MD5_CHECKSUM.getXMLType().equals(type.getXMLType())) {
      String pattern = "[0-9a-fA-F]{32}";
      if (value.matches(pattern)) {
        try {
          Integer.parseInt(value, 16);
        } catch (NumberFormatException e) {
          throw new Exception("Could not convert to a base-16 integer: " + value);
        }
      } else {
        throw new Exception("'" + value + "' does not match the pattern '" + pattern + "'");
      }
    } else if (FieldType.ASCII_ANYURI.getXMLType().equals(type.getXMLType())) {
      try {
        URI uri = new URI(value);
      } catch (URISyntaxException e) {
        throw new Exception(e.getMessage());
      }
    } else if (FieldType.ASCII_DOI.getXMLType().equals(type.getXMLType())) {
      String pattern = "10\\.\\S+/\\S+";
      if (!value.matches(pattern)) {
        throw new Exception("'" + value + "' does not match the pattern '" + pattern + "'");
      }
    } else if (FieldType.ASCII_LID.getXMLType().equals(type.getXMLType())) {
      String pattern = "urn:[a-z]+:[a-z]+:([0-9a-z-._]:?)+";
      if (!value.matches(pattern)) {
        throw new Exception("'" + value + "' does not match the pattern '" + pattern + "'");
      }
    } else if (FieldType.ASCII_LIDVID.getXMLType().equals(type.getXMLType())) {
      String pattern = "urn:[a-z]+:[a-z]+:([0-9a-z-._]:?)+::[1-9][0-9]*\\.[0-9]+";
      if (!value.matches(pattern)) {
        throw new Exception("'" + value + "' does not match the pattern '" + pattern + "'");
      }      
    } else if (FieldType.ASCII_LIDVID_LID.getXMLType().equals(type.getXMLType())) {
      // Can accept a LID or LIDVID?
      String lidvidPattern = "urn:[a-z]+:[a-z]+:([0-9a-z-._]:?)+::[1-9][0-9]*\\.[0-9]+";
      if (!value.matches(lidvidPattern)) {
        String lidPattern = "urn:[a-z]+:[a-z]+:([0-9a-z-._]:?)+";
        if (!value.matches(lidPattern)) {
          throw new Exception("'" + value + "' does not match the patterns '" + lidvidPattern
              + "' or '" + lidPattern + "'");
        }
      }
    } else if (FieldType.ASCII_VID.getXMLType().equals(type.getXMLType())) {
      String pattern = "[1-9][0-9]*\\.[0-9]+(\\.[0-9]+)?(\\.[0-9]+)?";
      if (!value.matches(pattern)) {
        throw new Exception("'" + value + "' does not match the pattern '" + pattern + "'");
      }
    } else if (FieldType.ASCII_STRING.getXMLType().equals(type.getXMLType())) {
      StringBuffer buffer = new StringBuffer(value);
      for (int i = 0; i < buffer.length(); i++) {
        if (buffer.charAt(i) > 127) {
          if (value.length() > 100) {
            value = value.substring(0, 100) + "...";
          }
          throw new Exception("'" + value + "' contains non-ASCII character: " + buffer.charAt(i));
        }
      }   
    } else if (FieldType.UTF8_STRING.getXMLType().equals(type.getXMLType())) {
      if (value.contains("\\s")) {
        if (value.length() > 100) {
          value = value.substring(0, 100) + "...";
        }        
        throw new Exception("'" + value + "' contains whitespace character(s)");
      }
    } else if (FieldType.ASCII_DATE_DOY.getXMLType().equals(type.getXMLType()) || 
        FieldType.ASCII_DATE_TIME_DOY.getXMLType().equals(type.getXMLType()) || 
        FieldType.ASCII_DATE_TIME_DOY_UTC.getXMLType().equals(type.getXMLType()) ||
        FieldType.ASCII_DATE_TIME_YMD.getXMLType().equals(type.getXMLType()) || 
        FieldType.ASCII_DATE_TIME_YMD_UTC.getXMLType().equals(type.getXMLType()) ||
        FieldType.ASCII_DATE_YMD.getXMLType().equals(type.getXMLType())) {
      if(!DateTimeValidator.isValid(type, value)) {
        throw new Exception("Could not parse " + value + " using these patterns '"
            + DATE_TIME_VALID_FORMATS.get(type.getXMLType()) + "'");
      }
    } else if (FieldType.ASCII_DIRECTORY_PATH_NAME.getXMLType().equals(type.getXMLType())) {
      String[] dirs = value.split("/");
      String pattern = "[A-Za-z0-9][A-Za-z0-9_-]*[A-Za-z0-9]";
      for (int i = 0; i < dirs.length; i++) {
        if (!dirs[i].matches(pattern)) {
          throw new Exception(dirs[i] + " does not match the pattern '" + pattern + "'");
        }
        if (dirs[i].length() > 255) {
          throw new Exception(dirs[i] + " is longer than 255 characters");
        }
      }
    } else if (FieldType.ASCII_FILE_NAME.getXMLType().equals(type.getXMLType())) {
      String pattern = "[A-Za-z0-9][A-Za-z0-9-_\\.]*[A-Za-z0-9]\\.[A-Za-z0-9]+";
      if (!value.matches(pattern)) {
        throw new Exception(value + " does not match the pattern '" + pattern + "'");        
      }
      if (value.length() > 255) {
        throw new Exception(value + " is longer than 255 characters");
      }      
    } else if (FieldType.ASCII_FILE_SPECIFICATION_NAME.getXMLType().equals(type.getXMLType())) {
      String dirPattern = "/?([A-Za-z0-9][A-Za-z0-9_-]*[A-Za-z0-9]/?)*";
      String dir = FilenameUtils.getFullPath(value);
      if (!dir.isEmpty()) {
        if (dir.length() > 255) {
          throw new Exception("The directory spec '" + dir + "' is longer than 255 characters");
        }
        if (!dir.matches(dirPattern)) {
          throw new Exception("The directory spec '" + dir + "' does not match the pattern '" + dirPattern + "'");  
        }
      }
      String fnPattern = "[A-Za-z0-9][A-Za-z0-9-_\\.]*[A-Za-z0-9]\\.[A-Za-z0-9]+";
      String name = FilenameUtils.getName(value);
      if (name.isEmpty()) {
        throw new Exception("No filename spec found in '" + value + "'."); 
      } else if (!name.matches(fnPattern)) {
        throw new Exception("The filename spec '" + name + "' does not match the pattern '" + fnPattern + "'");        
      }
      if (name.length() > 255) {
        throw new Exception("The filename spec '" + name + "' is longer than 255 characters");
      }
    }
  }
  
  /**
   * Check that the given value matches the defined field format.
   * 
   * @param value The value to check.
   * @param format The defined field format.
   * @param fieldIndex Where the field value is located.
   * @param recordLocation The record location where the field is located.
   */
  private void checkFormat(String value, String format, int fieldIndex, 
      RecordLocation recordLocation) {
    Matcher matcher = formatPattern.matcher(format);
    int precision = -1;
    boolean isValid = true;
    if (matcher.matches()) {
      int width = Integer.parseInt(matcher.group(2));
      if (matcher.group(4) != null) {
        precision = Integer.parseInt(matcher.group(4));
      }
      String specifier = matcher.group(5);
      if (matcher.group(1) != null) {
        String regexLeadingWhitespace = "\\s+.*";
        String regexTrailingWhitespace = ".*\\s+";

        String justified = matcher.group(1);
        if ("+".equals(justified)) {
          //check if there is trailing whitespace
          if (Pattern.matches(regexTrailingWhitespace, value)) {
            addTableException(ExceptionType.ERROR, 
                    "The value '" + value + "' is not right-justified.",
                    recordLocation,
                    fieldIndex);
            isValid = false;
          }
        } else if ("-".equals(justified)) {
          if (Pattern.matches(regexLeadingWhitespace, value)) {
            addTableException(ExceptionType.ERROR, 
                "The value '" + value + "' is not left-justified.",
                recordLocation,
                fieldIndex);    
            isValid = false;
          }
        }
      }
      try {
        if (specifier.matches("[eE]")) {
          String p = "[-+]?[0-9]+(\\.?[0-9]+)([eE][-+]?[0-9]+)";
          if (value.trim().matches(p)) {
            Double.parseDouble(value.trim());
          } else {
            throw new NumberFormatException("Value does not match pattern");
          }
        } else if (specifier.equals("f")) {
          String p = "[-+]?[0-9]+(\\.[0-9]+)";
          if (value.trim().matches(p)) {
            Double.parseDouble(value.trim());
          } else {
            throw new NumberFormatException("Value does not match pattern");
          }
        } else if (specifier.equals("d")) {
          Integer.parseInt(value.trim());
        } else if (specifier.equals("o")) {
          int v = Integer.parseInt(value.trim(), 8);
          if (v < 0) {
            throw new NumberFormatException("Value is negative.");
          }
        } else if (specifier.equals("x")) {
          int v = Integer.parseInt(value.trim(), 16);
          if (v < 0) {
            throw new NumberFormatException("Value is negative.");
          }
        }
      } catch (NumberFormatException e) {
        addTableException(ExceptionType.ERROR, 
            "The value '" + value.trim() + "' does not match the "
                + "defined field format specifier '" + specifier + "'.",
            recordLocation,
            fieldIndex);
      }
      if (value.trim().length() > width) {
        addTableException(ExceptionType.ERROR, 
                "The length of the value '" + value.trim() + "' exceeds the max "
                + "width set in the defined field format "
                + "(max " + width + ", got " + value.trim().length() + ").",
                recordLocation,
                fieldIndex);
        isValid = false;
      }
      if (precision != -1) {
        if (specifier.matches("[feE]")) {
          String[] tokens = value.trim().split("[eE]", 1);
          int length = 0;
          if (tokens[0].indexOf(".") != -1) {
            length = tokens[0].substring(tokens[0].indexOf(".") + 1).length();
          }
          if (length != precision) {
            addTableException(ExceptionType.ERROR, 
                    "The number of digits to the right of the decimal point "
                    + "in the value '" + value.trim() + "' does not equal the "
                    + "precision set in the defined field format "
                    + "(expected " + precision + ", got " + length + ").",
                    recordLocation,
                    fieldIndex);
            isValid = false;
          }
        }
      }
      if (isValid) {
        addTableException(ExceptionType.INFO, 
            "Value '" + value + "' conforms to the defined field format '" + format + "'",
            recordLocation, fieldIndex);
      }
    }
  }
  
  /**
   * Adds a TableContentException to the Exception Container.
   * 
   * @param exceptionType The severity.
   * @param message The exception message.
   * @param recordLocation The record location where the field is located.
   * @param field The index of the field.
   */
  private void addTableException(ExceptionType exceptionType, String message, 
      RecordLocation recordLocation, int field) {
    listener.addProblem(
        new TableContentException(exceptionType, 
            message,
            recordLocation.getDataFile(),
            recordLocation.getLabel(),
            recordLocation.getTable(),
            recordLocation.getRecord(),
            field));
  }
}
