// Copyright 2006-2015, by the California Institute of Technology.
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
package gov.nasa.pds.harvest.pdap.constants;

public class Constants {
  /** The VID in a product label. */
  public static final String PRODUCT_VERSION = "version_id";

  /** The LID prefix for a data set. */
  public static final String LID_PREFIX = "urn:esa:psa:context_pds3:data_set:data_set";

  /** The LID prefix for a resource. */
  public static final String RESOURCE_PREFIX = "urn:esa:psa:context_pds3:resource:resource";

  /** The data set product class. */
  public static final String DATA_SET_PRODUCT_CLASS = "Product_Data_Set_PDS3";

  /** The resource product class. */
  public static final String RESOURCE_PRODUCT_CLASS = "Product_Context";

  public static final String URN_ILLEGAL_CHARACTERS = "[%/\\\\?#\"&<>\\[\\]^`\\{\\|\\}~]";

  public static final int DEFAULT_BATCH_MODE = 50;
}
