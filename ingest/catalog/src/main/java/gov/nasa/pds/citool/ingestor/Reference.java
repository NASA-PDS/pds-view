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
package gov.nasa.pds.citool.ingestor;

import java.io.File;

/**
 * Class that holds metadata of an association.
 *
 * @author hyunlee
 *
 */
public class Reference {
    /** Logical identifier. */
    private String logicalId;

    /** Version. */
    private String version;

    /** The association type. */
    private String associationType;
  
    /**
     * Constructor.
     *
     */
    public Reference() {
        logicalId = null;
        version = null;
        associationType = null;
    }
    
    public Reference(String logicalId, String version, String associationType) {
    	this.logicalId = logicalId;
    	this.version = version;
    	this.associationType = associationType;
    }

    /**
     * Get the logical identifier.
     *
     * @return A LID.
     */
    public String getLogicalId() {
        return logicalId;
    }

    /**
     * Set the logical identifier.
     *
     * @param id A LID.
     */
    public void setLogicalId(String id) {
        logicalId = id;
    }

    /**
     * Get the version.
     *
     * @return A version ID.
     */
    public String getVersion() {
        return version;
    }

    /**
     * Set the version.
     *
     * @param ver A version ID.
     */
    public void setVersion(String ver) {
        version = ver;
        //hasVersion = true;
    }


    /**
     * Get the association type.
     *
     * @return An association type.
     */
    public String getAssociationType() {
        return associationType;
    }

    /**
     * Set the association type.
     *
     * @param type An association type.
     */
    public void setAssociationType(String type) {
        associationType = type;
    }
}
