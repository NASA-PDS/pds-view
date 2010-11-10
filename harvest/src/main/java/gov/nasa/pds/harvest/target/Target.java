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
package gov.nasa.pds.harvest.target;

/**
 * Class representation of a target. Can either be a directory, collection,
 * or bundle.
 *
 * @author mcayanan
 *
 */
public class Target {
    private String file;
    private Type type;

    /**
     * Constructor.
     *
     * @param file A file.
     * @param type The target type.
     */
    public Target(String file, Type type) {
        this.file = file;
        this.type = type;
    }

    /**
     * Gets the name of the file.
     *
     * @return the file name.
     */
    public String getFilename() {
        return file;
    }

    /**
     * Returns the target type.
     *
     * @return A target type.
     */
    public Type getType() {
        return type;
    }
}
