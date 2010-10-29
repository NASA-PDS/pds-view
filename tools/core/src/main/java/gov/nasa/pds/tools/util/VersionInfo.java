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

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

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

  public final static String MODEL_VERSION = "model.version";

  public final static String SCHEMA_DIR = "schema";
  
  public final static String BASE_TYPES = "Base_Types";
  
  public final static String EXTENDED_TYPES = "Extended_Types";

  private final static Properties props = new Properties();

  static {
    try {
      props.load(VersionInfo.class.getResourceAsStream("/core.properties"));
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

  public static String getModelVersion() {
    return props.getProperty(MODEL_VERSION);
  }

  public static List<String> getSchemas() {
    return Arrays.asList(getSafeResourceListing(SCHEMA_DIR + "/"
        + getModelVersion()));
  }

  public static List<String> getSchemas(String modelVersion) {
    return Arrays
        .asList(getSafeResourceListing(SCHEMA_DIR + "/" + modelVersion));
  }
  
  public static List<String> getSupportedModels() {
    return Arrays.asList(getSafeResourceListing(SCHEMA_DIR));
  }

  private static String[] getSafeResourceListing(String path) {
    try {
      return getResourceListing(path);
    } catch (URISyntaxException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (UnsupportedOperationException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static String[] getResourceListing(String path)
      throws URISyntaxException, IOException {
    URL dirURL = VersionInfo.class.getClassLoader().getResource(path);
    if (dirURL != null && dirURL.getProtocol().equals("file")) {
      /* A file path: easy enough */
      return new File(dirURL.toURI()).list();
    }

    if (dirURL == null) {
      /*
       * In case of a jar file, we can't actually find a directory. Have to
       * assume the same jar as clazz.
       */
      String me = VersionInfo.class.getName().replace(".", "/") + ".class";
      dirURL = VersionInfo.class.getClassLoader().getResource(me);
    }

    if (dirURL.getProtocol().equals("jar")) {
      /* A JAR path */
      String jarPath = dirURL.getPath().substring(5,
          dirURL.getPath().indexOf("!")); // strip out only the JAR file
      JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
      Enumeration<JarEntry> entries = jar.entries(); // gives ALL entries in jar
      Set<String> result = new HashSet<String>(); // avoid duplicates in case it
      // is a subdirectory
      while (entries.hasMoreElements()) {
        String name = entries.nextElement().getName();
        if (name.startsWith(path)) { // filter according to the path
          String entry = name.substring(path.length());
          int checkSubdir = entry.indexOf("/");
          if (checkSubdir >= 0) {
            // if it is a subdirectory, we just return the directory name
            entry = entry.substring(0, checkSubdir);
          }
          result.add(entry);
        }
      }
      return result.toArray(new String[result.size()]);
    }

    throw new UnsupportedOperationException("Cannot list files for URL "
        + dirURL);
  }

  public static void main(String[] args) throws Exception {
    System.out.println(VersionInfo.getSchemas());
    System.out.println(VersionInfo.getSchemas(VersionInfo.getModelVersion()));
    System.out.println(VersionInfo.getSupportedModels());
  }

}
