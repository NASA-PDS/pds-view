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
package gov.nasa.pds.harvest.inventory;

import java.io.File;

/**
 * Class that holds metadata of an association.
 *
 * @author mcayanan
 *
 */
public class ReferenceEntry {
    /** Logical identifier. */
    private String logicalID;

    /** Version. */
    private String version;

    /** The association type. */
    private String associationType;

    /** The referenced object type. */
    private String objectType;

    /** Flag to indicate whether the association has a LID-VID reference. */
    private boolean hasVersion;

    /** The file associated with this entry */
    private File file;

    /** The location of this association in the file */
    private int lineNumber;

    /**
     * Constructor.
     *
     */
    public ReferenceEntry() {
        logicalID = null;
        version = null;
        associationType = null;
        objectType = null;
        lineNumber = -1;
        file = null;

        hasVersion = false;
    }

    /**
     * Get the logical identifier.
     *
     * @return A LID.
     */
    public String getLogicalID() {
        return logicalID;
    }

    /**
     * Set the logical identifier.
     *
     * @param id A LID.
     */
    public void setLogicalID(String id) {
        logicalID = id;
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
        hasVersion = true;
    }

    /**
     * Flag to indicate if the association contains
     * a version.
     *
     * @return 'true' if the association has a LID-VID
     * reference.
     */
    public boolean hasVersion() {
        return hasVersion;
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

    /**
     * Get the referenced object type.
     *
     * @return A referenced object type.
     */
    public String getObjectType() {
        return objectType;
    }

    /**
     * Set the reference object type.
     *
     * @param type A type.
     */
    public void setObjectType(String type) {
        objectType = type;
    }

    /**
     * Sets the file associated with the reference entry.
     *
     * @param file The file.
     */
    public void setFile(File file) {
        this.file = file;
    }

    /**
     * Gets the file.
     *
     * @return The file.
     */
    public File getFile() {
        return file;
    }

    /**
     * Sets the line number associated with the reference entry.
     *
     * @param num A line number.
     */
    public void setLineNumber(int num) {
        lineNumber = num;
    }

    /**
     * Gets the line number.
     *
     * @return The line number.
     */
    public int getLineNumber() {
        return lineNumber;
    }
}
