// Copyright 2006-2012, by the California Institute of Technology.
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
package gov.nasa.pds.harvest.pdap.logging;

import java.util.logging.Level;
import java.util.logging.LogRecord;

public class ToolsLogRecord extends LogRecord {
    private String identifier;
    private int line;

    public ToolsLogRecord(Level level, String message) {
        this(level, message, null, -1);
    }

    public ToolsLogRecord(Level level, String message, String identifier) {
        this(level, message, identifier, -1);
    }

    public ToolsLogRecord(Level level, String message, String identifier,
            int line) {
        super(level, message);
        this.identifier = identifier;
        this.line = line;
    }

    public String getIdentifier() {
        return identifier;
    }

    public int getLine() {
        return line;
    }
}
