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

package gov.nasa.pds.tools.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * This class provides the means to retrieve underlying supported versions of
 * standards.
 * 
 * @author pramirez
 * 
 */
public class VersionInfo {
  
  public final static String XML_VERSION = "xml.version";
  
  public final static String LIBRARY_VERSION = "library.version";
  
  public final static String STANDARDS_VERSION = "standards-ref.version";
  
  public final static String PDS_VERSION = "pds.version";
  
  public final static String COPYRIGHT = "core.copyright";
  
  public final static String CORE_SCHEMAS = "core.schemas";
  
  private final static Properties props = new Properties();
  
  private final static List<String> schemas = new ArrayList<String>();
  
  static {
    try { 
      props.load(VersionInfo.class.getResourceAsStream("/core.properties"));
      String [] schema = props.getProperty(CORE_SCHEMAS).split(",");
      for (int i = 0; i < schema.length; i++) {
        schemas.add(schema[i].trim());
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
  
  public static String getXMLVersion() {
    return props.getProperty(XML_VERSION);
  }
  
  public static String getLibraryVersion() {
    return props.getProperty(LIBRARY_VERSION);
  }
  
  public static String getStandardsVersion() {
    return props.getProperty(STANDARDS_VERSION);
  }
  
  public static String getPDSVersion() {
    return props.getProperty(PDS_VERSION);
  }
  
  public static String getCopyright() {
    return props.getProperty(COPYRIGHT);
  }
  
  public static List<String> getSchemas() {
    return schemas;
  }
  
}
