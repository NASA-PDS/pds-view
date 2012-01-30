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

import java.util.List;

import gov.nasa.pds.tools.label.ObjectStatement;
import gov.nasa.pds.tools.label.Value;

/**
 * Class that is intended to find matching 'IDs' between two
 * objects found in a catalog file. 'IDs' in this context
 * means an attribute value that distinguishes multiple catalog
 * objects from each other. For example, REFERENCE objects
 * contain an attribute called REFERENCE_KEY_ID that makes the
 * object unique.
 * <br><br>
 * This class aids in programatically determining if two catalog
 * objects were meant to be the same when doing a comparison
 * function in the Catalog Ingest Tool.
 *
 * @author mcayanan
 *
 */
public class CatalogList {
    private List<ObjectStatement> catalogs;

    /**
     * Constructor
     *
     * @param catalogs A list of catalog objects.
     */
    public CatalogList(List<ObjectStatement> catalogs) {
        this.catalogs = catalogs;
    }

    /**
     * Returns a catalog object that contains the supplied id
     *
     * @param id The value that makes the catalog object unique.
     *
     * @return The catalog object that matches the supplied id.
     */
    public ObjectStatement get(Value id) {
        CatalogIndex index = new CatalogIndex();
        try {
            for (ObjectStatement c : catalogs) {
                String identifier = index.getIdentifier(
                        c.getIdentifier().getId());
                String value = c.getAttribute(identifier).getValue().toString();
                if (value.equals(id.toString())) {
                    return c;
                }
            }
        } catch(NullPointerException n) {
            //Don't do anything.
        }
        return null;
    }
}
