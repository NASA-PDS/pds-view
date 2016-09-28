// Copyright 2009-2016, by the California Institute of Technology.
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
// $Id: $
package gov.nasa.pds.security;

import java.io.File;

/**
 * Class that represents contents found in an PDS Personnel file
 * 
 * @author hyunlee
 *
 */
public class PersEntry {
    private File file;
    private String checksum;

    public PersEntry(File file, String checksum) {
        if(!file.exists()) {
            this.file = file.getAbsoluteFile();
        } else {
            this.file = file;
        }
        this.checksum = checksum;
    }

    public File getFile() {
        return file;
    }

    public String getChecksum() {
        return checksum;
    }
}
