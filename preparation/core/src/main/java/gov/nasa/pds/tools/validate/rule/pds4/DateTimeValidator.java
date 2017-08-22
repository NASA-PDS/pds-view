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
package gov.nasa.pds.tools.validate.rule.pds4;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import gov.nasa.pds.label.object.FieldType;

/**
 * Class to validate datetime values.
 * 
 * @author mcayanan
 *
 */
public class DateTimeValidator {
  
  /**
   * Valid Day Of Year formats.
   * 
   */
  private static List<String> DOY_FORMATS = Arrays.asList(
    "YYYY", 
    "YYYY'Z'", 
    "YYYY-DDD", 
    "YYYY-DDD'Z'"
  );
  
  /**
   * Valid Day of Year datetime formats.
   * 
   */
  private static List<String> DATE_TIME_DOY_FORMATS = Arrays.asList(
    "YYYY", 
    "YYYY'Z'", 
    "YYYY-DDD'T'HH", 
    "YYYY-DDD'T'HH'Z'", 
    "YYYY-DDD'T'HH:mm",
    "YYYY-DDD'T'HH:mm'Z'", 
    "YYYY-DDD'T'HH:mm:ss", 
    "YYYY-DDD'T'HH:mm:ss'Z'",
    "YYYY-DDD'T'HH:mm:ss.SSS", 
    "YYYY-DDD'T'HH:mm:ss.SSS'Z'"
  );
      
  /**
   * Valid datetime UTC formats.
   * 
   */
  private static List<String> DATE_TIME_DOY_UTC_FORMATS = Arrays.asList(
    "YYYY'Z'", 
    "YYYY-DDD'T'HH'Z'", 
    "YYYY-DDD'T'HH:mm'Z'", 
    "YYYY-DDD'T'HH:mm:ss'Z'",
    "YYYY-DDD'T'HH:mm:ss.SSS'Z'"
  );
  
  /**
   * Valid datetime year month day formats.
   * 
   */
  private static List<String> DATE_TIME_YMD_FORMATS = Arrays.asList(
    "YYYY", 
    "YYYY'Z'", 
    "YYYY-MM-dd'T'HH", 
    "YYYY-MM-dd'T'HH'Z'", 
    "YYYY-MM-dd'T'HH:mm",
    "YYYY-MM-dd'T'HH:mm'Z'", 
    "YYYY-MM-dd'T'HH:mm:ss", 
    "YYYY-MM-dd'T'HH:mm:ss'Z'",
    "YYYY-MM-dd'T'HH:mm:ss.SSS", 
    "YYYY-MM-dd'T'HH:mm:ss.SSS'Z'"    
  );
  
  /**
   * Valid datetime year month day UTC formats.
   * 
   */
  private static List<String> DATE_TIME_YMD_UTC_FORMATS = Arrays.asList(
    "YYYY'Z'", 
    "YYYY-MM-dd'T'HH'Z'", 
    "YYYY-MM-dd'T'HH:mm'Z'",
    "YYYY-MM-dd'T'HH:mm:ss'Z'", 
    "YYYY-MM-dd'T'HH:mm:ss.SSS'Z'"    
  );
  
  /**
   * Valid year month day formats.
   * 
   */
  private static List<String> DATE_YMD_FORMATS = Arrays.asList(
    "YYYY", 
    "YYYY'Z'", 
    "YYYY-MM", 
    "YYYY-MM'Z'",
    "YYYY-MM-dd",
    "YYYY-MM-dd'Z'"
  );
  
  /**
   * Mapping of field datetime types to its list of valid datetime formats.
   * 
   */
  private static final HashMap<String, List<String>> DATE_TIME_FORMATS = new HashMap<String, List<String>>();
  static {
    DATE_TIME_FORMATS.put(FieldType.ASCII_DATE_DOY.getXMLType(), DOY_FORMATS);
    DATE_TIME_FORMATS.put(FieldType.ASCII_DATE_TIME_DOY.getXMLType(), DATE_TIME_DOY_FORMATS);
    DATE_TIME_FORMATS.put(FieldType.ASCII_DATE_TIME_DOY_UTC.getXMLType(), DATE_TIME_DOY_UTC_FORMATS);
    DATE_TIME_FORMATS.put(FieldType.ASCII_DATE_TIME_YMD.getXMLType(), DATE_TIME_YMD_FORMATS);
    DATE_TIME_FORMATS.put(FieldType.ASCII_DATE_TIME_YMD_UTC.getXMLType(), DATE_TIME_YMD_UTC_FORMATS);
    DATE_TIME_FORMATS.put(FieldType.ASCII_DATE_YMD.getXMLType(), DATE_YMD_FORMATS);
  }
  
  /**
   * Checks to see if the given datetime value matches its defined data type.
   * 
   * @param type The datetime type.
   * @param value The value to check against.
   * 
   * @return 'true' if the value matches its data type. 'false' otherwise.
   * 
   * @throws Exception
   */
  public static boolean isValid(FieldType type, String value) throws Exception {
    boolean success = false;
    if (DATE_TIME_FORMATS.containsKey(type.getXMLType())) {
      for (String format : DATE_TIME_FORMATS.get(type.getXMLType())) {
        DateTimeFormatter dtFormatter = DateTimeFormat.forPattern(format);
        try {
          dtFormatter.parseDateTime(value.trim());
          success = true;
          break;
        } catch (IllegalArgumentException e) {
          //Ignore
        }
      }
    } else {
      throw new Exception("'" + type.getXMLType()
        + "' is not one of the valid datetime formats: "
        + DATE_TIME_FORMATS.toString());
    }
    return success;
  }
}
