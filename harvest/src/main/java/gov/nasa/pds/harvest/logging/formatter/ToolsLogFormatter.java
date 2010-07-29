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
package gov.nasa.pds.harvest.logging.formatter;

import gov.nasa.pds.harvest.logging.ToolsLogRecord;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class ToolsLogFormatter extends Formatter {
	private static String lineFeed = System.getProperty("line.separator", "\n");
	
	public String format(LogRecord record) {
		ToolsLogRecord tlr = (ToolsLogRecord) record;
		StringBuffer message = new StringBuffer();
		
		message.append(tlr.getLevel().getName());
		message.append(":   ");

		if(tlr.getFilename() != null) {
			message.append("[" + tlr.getFilename() + "] ");
		}
		if(tlr.getLine() != -1) {
			message.append("line " + tlr.getLine() + ": ");
		}
		message.append(tlr.getMessage());
		message.append(lineFeed);
		
		return message.toString();
	}
}
