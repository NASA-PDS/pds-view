// Copyright 2009, by the California Institute of Technology.
// ALL RIGHTS RESERVED. United States Government sponsorship acknowledged.
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

package gov.nasa.pds.citool.catalog;

import java.util.HashMap;

/**
 * Class that contains a mapping of various catalog objects
 * to an identifier, which gives the object a "unique name". This aides
 * in programatically determining if two catalog objects were meant to
 * be the same when doing a comparison function in the Catalog Ingest Tool.
 *
 * @author mcayanan
 *
 */
public class CatalogIndex {
    private HashMap<String,String> catalogs;
    private final static int TYPE = 0;
    private final static int IDENTIFIER = 1;

    private final static String [][] CATALOGS = {
        {"DATA_SET", "DATA_SET_ID"},
        {"INSTRUMENT", "INSTRUMENT_ID"},
        {"INSTRUMENT_HOST", "INSTRUMENT_HOST_ID"},
        {"MISSION", "MISSION_NAME"},
        {"PERSONNEL", "PDS_USER_ID"},
        {"PERSONNEL_ELECTRONIC_MAIL", "ELECTRONIC_MAIL_ID"},
        {"REFERENCE", "REFERENCE_KEY_ID"},
        {"TARGET", "TARGET_NAME"},
    };

    public CatalogIndex() {
        catalogs = new HashMap<String, String>();
        for (int i = 0; i < CATALOGS.length; i++) {
            String[] cat = CATALOGS[i];
            catalogs.put(cat[TYPE], cat[IDENTIFIER]);
        }
    }

    /**
     * Gets the identifier of the given type.
     *
     * @param type The catalog type (MISSION, REFERENCE, INSTRUMENT, etc.)
     *
     * @return The identifier.
     */
    public String getIdentifier(String type) {
        return catalogs.get(type);
    }
}
