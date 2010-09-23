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
package gov.nasa.pds.harvest.crawler.metadata;

import java.util.HashMap;

/**
 * A mapping of metadata to XPath 2.0 expressions.
 *
 * @author mcayanan
 *
 */
public class CoreXPaths implements PDSCoreMetKeys {
    public static final HashMap<String,String> map = new HashMap<String,String>();

    static {
        map.put(LOGICAL_ID, "//*[ends-with(name(),'Identification_Area')]/logical_identifier");
        map.put(PRODUCT_VERSION, "//*[ends-with(name(),'Identification_Area')]/version_id");
        map.put(OBJECT_TYPE, "//*[ends-with(name(),'Identification_Area')]/object_type");
        map.put(TITLE, "//*[ends-with(name(),'Identification_Area')]/title");
        map.put(REFERENCES, "//*[ends-with(name(),'Member_Entry')] | //*[ends-with(name(),'Reference_Area')]/*[ends-with(name(),'Reference_Entry')]");
    }
}
