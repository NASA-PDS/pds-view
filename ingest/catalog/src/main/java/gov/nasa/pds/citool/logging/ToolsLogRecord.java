// Copyright 2006-2007, by the California Institute of Technology.
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
// $Id: ToolsLogRecord.java 5853 2010-02-06 22:54:00Z shardman $ 
//

package gov.nasa.pds.citool.logging;

import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * @author pramirez
 * @version $Revision: 5853 $
 * 
 */
public class ToolsLogRecord extends LogRecord {
    private final String file;
    private final String context;
    private final int line;

    public ToolsLogRecord(Level level, String message) {
        this(level, message, null, null);
    }
    
    /**
     * Constructs a log record 
     * @param level of error
     * @param message describing error
     * @param file in which error occured
     */
    public ToolsLogRecord(Level level, String message, String file) {
        this(level, message, file, null);
    }
    
    /**
     * Construct a log record 
     * @param level of error
     * @param message describing error
     * @param file in which error occured
     * @param line number at which occured
     */
    public ToolsLogRecord(Level level, String message, String file, int line) {
        this(level, message, file, null, line);
    }
    
    /**
     * Construct a log record 
     * @param level of error
     * @param message describing error
     * @param file in which error occured
     * @param context file which referenced file where error occured
     */
    public ToolsLogRecord(Level level, String message, String file, String context) {
        this(level, message, file, context, -1);
    }
    
    /**
     * Construct a log record
     * @param level of error
     * @param message describing error
     * @param file in which error occured
     * @param context file which referenced file where error occured
     * @param line number at which occured
     */
    public ToolsLogRecord(Level level, String message, String file, String context, int line) {
        super(level, message);
        this.file = file;
        this.context = context;
        this.line = line;
    }

    public String getFile() {return file;}
    
    public String getContext() {return context;}
    
    public int getLine() {return line;}
 
}
