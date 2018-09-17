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

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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
    "yyyy", 
    "yyyy'Z'", 
    "yyyy-DDD", 
    "yyyy-DDD'Z'"
  );
  
  /**
   * Valid Day of Year datetime formats.
   * 
   */
  private static List<String> DATE_TIME_DOY_FORMATS = Arrays.asList(
    "yyyy", 
    "yyyy'Z'", 
    "yyyy-DDD'T'HH", 
    "yyyy-DDD'T'HH'Z'", 
    "yyyy-DDD'T'HH:mm",
    "yyyy-DDD'T'HH:mm'Z'", 
    "yyyy-DDD'T'HH:mm:ss", 
    "yyyy-DDD'T'HH:mm:ss'Z'",
    "yyyy-DDD'T'HH:mm:ss.S", 
    "yyyy-DDD'T'HH:mm:ss.S'Z'",
    "yyyy-DDD'T'HH:mm:ss.SS", 
    "yyyy-DDD'T'HH:mm:ss.SS'Z'",
    "yyyy-DDD'T'HH:mm:ss.SSS", 
    "yyyy-DDD'T'HH:mm:ss.SSS'Z'",
    "yyyy-DDD'T'HH:mm:ss.SSSS", 
    "yyyy-DDD'T'HH:mm:ss.SSSS'Z'",
    "yyyy-DDD'T'HH:mm:ss.SSSSS", 
    "yyyy-DDD'T'HH:mm:ss.SSSSS'Z'",    
    "yyyy-DDD'T'HH:mm:ss.SSSSSS", 
    "yyyy-DDD'T'HH:mm:ss.SSSSSS'Z'"
  );
      
  /**
   * Valid datetime UTC formats.
   * 
   */
  private static List<String> DATE_TIME_DOY_UTC_FORMATS = Arrays.asList(
    "yyyy'Z'", 
    "yyyy-DDD'T'HH'Z'", 
    "yyyy-DDD'T'HH:mm'Z'", 
    "yyyy-DDD'T'HH:mm:ss'Z'",
    "yyyy-DDD'T'HH:mm:ss.S'Z'",
    "yyyy-DDD'T'HH:mm:ss.SS'Z'",
    "yyyy-DDD'T'HH:mm:ss.SSS'Z'",
    "yyyy-DDD'T'HH:mm:ss.SSSS'Z'",
    "yyyy-DDD'T'HH:mm:ss.SSSSS'Z'",
    "yyyy-DDD'T'HH:mm:ss.SSSSSS'Z'"
  );
  
  /**
   * Valid datetime year month day formats.
   * 
   */
  private static List<String> DATE_TIME_YMD_FORMATS = Arrays.asList(
    "yyyy", 
    "yyyy'Z'", 
    "yyyy-MM-dd'T'HH", 
    "yyyy-MM-dd'T'HH'Z'", 
    "yyyy-MM-dd'T'HH:mm",
    "yyyy-MM-dd'T'HH:mm'Z'", 
    "yyyy-MM-dd'T'HH:mm:ss", 
    "yyyy-MM-dd'T'HH:mm:ss'Z'",
    "yyyy-MM-dd'T'HH:mm:ss.S", 
    "yyyy-MM-dd'T'HH:mm:ss.S'Z'",
    "yyyy-MM-dd'T'HH:mm:ss.SS", 
    "yyyy-MM-dd'T'HH:mm:ss.SS'Z'",    
    "yyyy-MM-dd'T'HH:mm:ss.SSS", 
    "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
    "yyyy-MM-dd'T'HH:mm:ss.SSSS", 
    "yyyy-MM-dd'T'HH:mm:ss.SSSS'Z'",
    "yyyy-MM-dd'T'HH:mm:ss.SSSSS", 
    "yyyy-MM-dd'T'HH:mm:ss.SSSSS'Z'",
    "yyyy-MM-dd'T'HH:mm:ss.SSSSSS", 
    "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'"
  );
  
  /**
   * Valid datetime year month day UTC formats.
   * 
   */
  private static List<String> DATE_TIME_YMD_UTC_FORMATS = Arrays.asList(
    "yyyy'Z'", 
    "yyyy-MM-dd'T'HH'Z'", 
    "yyyy-MM-dd'T'HH:mm'Z'",
    "yyyy-MM-dd'T'HH:mm:ss'Z'",
    "yyyy-MM-dd'T'HH:mm:ss.S'Z'",
    "yyyy-MM-dd'T'HH:mm:ss.SS'Z'",
    "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
    "yyyy-MM-dd'T'HH:mm:ss.SSSS'Z'",
    "yyyy-MM-dd'T'HH:mm:ss.SSSSS'Z'",
    "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'"
  );
  
  /**
   * Valid year month day formats.
   * 
   */
  private static List<String> DATE_YMD_FORMATS = Arrays.asList(
    "yyyy", 
    "yyyy'Z'", 
    "yyyy-MM", 
    "yyyy-MM'Z'",
    "yyyy-MM-dd",
    "yyyy-MM-dd'Z'"
  );
  
  /**
   * Mapping of field datetime types to its list of valid datetime formats.
   * 
   */
  public static final HashMap<String, List<String>> DATE_TIME_FORMATS = new HashMap<String, List<String>>();
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
        DateTimeFormatter dtformatter = DateTimeFormatter.ofPattern(format);
        try {
          LocalDateTime date = LocalDateTime.parse(value.trim(), dtformatter);
          success = true;
          break;
        } catch (IllegalArgumentException e) {
          //Ignore
        } catch (DateTimeParseException de) {
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
