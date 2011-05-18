// Copyright 2006-2010, by the California Institute of Technology.
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
package gov.nasa.pds.harvest.constants;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class that holds constants used in Harvest.
 *
 * @author mcayanan
 *
 */
public class Constants {
  /** The PDS namespace. */
  public static final String PDS_NAMESPACE =
    "http://pds.nasa.gov/schema/pds4/pds";

  public static final String BUNDLE = "Bundle";

  public static final String COLLECTION = "Collection";

  /** The LID in a product label. */
  public static final String LOGICAL_ID = "logical_identifier";

  /** The VID in a product label. */
  public static final String PRODUCT_VERSION = "version_id";

  /** The product_class in a product label. */
  public static final String OBJECT_TYPE = "product_class";

  /** The associations in a product label. */
  public static final String REFERENCES = "references";

  /** The title of a product label. */
  public static final String TITLE = "title";

  /** The unique identifier of a product in the registry. */
  public static final String PRODUCT_GUID = "product_guid";

  /** Metadata key to indicate if an association was verified. */
  public static final String VERIFIED = "verified";

  /** Holds the XPaths to the expected metadata in a product label. */
  public static final HashMap<String, String> coreXpathsMap =
    new HashMap<String, String>();

  /** The Identification Area XPath in a product label. */
  public static final String IDENTIFICATION_AREA_XPATH =
    "//*[starts-with(name(),'Identification_Area')]";

  static {
    coreXpathsMap.put(LOGICAL_ID, IDENTIFICATION_AREA_XPATH + "/"
            + LOGICAL_ID);
    coreXpathsMap.put(PRODUCT_VERSION, IDENTIFICATION_AREA_XPATH + "/"
            + PRODUCT_VERSION);
    coreXpathsMap.put(OBJECT_TYPE, IDENTIFICATION_AREA_XPATH + "/"
            + OBJECT_TYPE);
    coreXpathsMap.put(TITLE, IDENTIFICATION_AREA_XPATH + "/" + TITLE);
    coreXpathsMap.put(REFERENCES,
            "//*[ends-with(name(),'Member_Entry')]"
            + " | //*[ends-with(name(),'Reference_Entry')] | "
            + "//Reference_Entry_Generic");
  }

  /** Mapping of PDS3 to PDS4 names */
  public static final Map<String, String> pds3ToPds4Map =
    new HashMap<String, String>();

    static {
      pds3ToPds4Map.put("PRODUCT_CREATION_TIME",  "last_modification_date_time");
      pds3ToPds4Map.put("TARGET_NAME", "target_name");
      pds3ToPds4Map.put("INSTRUMENT_NAME", "instrument_name");
      pds3ToPds4Map.put("INSTRUMENT_ID", "instrument_name");
      pds3ToPds4Map.put("INSTRUMENT_HOST_NAME", "instrument_host_name");
    }

}
