// Copyright 2009, by the California Institute of Technology.
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

package gov.nasa.pds.citool.ri;

/**
 * Interface containing the different referential integrity
 * types.
 *
 * @author mcayanan
 *
 */
public enum RIType {
    REFERENCE("Reference"),
    PERSONNEL("Personnel"),
    TARGET("Target"),
    INSTRUMENT_HOST("Instrument_Host"),
    INSTRUMENT("Instrument"),
    MISSION("Mission"),
    VOLUME("Volume"),
    DATA_SET("Data_Set"),
    DATA_SET_COLLECTION("Data_set_Collection");

    public final static String PARENT = "Parent";
    public final static String CHILD = "Child";
    private final String name;

    private RIType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
