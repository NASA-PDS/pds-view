// Copyright 2013, by the California Institute of Technology.
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
package gov.nasa.pds.report.logging.formatter;

import gov.nasa.pds.report.logging.ToolsLevel;
import gov.nasa.pds.report.logging.ToolsLogRecord;

import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * @author jpadams
 * @version $Revision$
 *
 */
public abstract class PDSFormatter extends Formatter {
	
	protected static String lineFeed = System.getProperty("line.separator", "\n");
	protected static String doubleLineFeed = lineFeed + lineFeed;
	protected static String sectionBreak = lineFeed+ "=================================================="
			+ lineFeed;

	protected StringBuffer summary;

	protected int numWarnings;
	protected int numErrors;

	public PDSFormatter() {
		this.summary = new StringBuffer("Summary:" + doubleLineFeed);
		this.numWarnings = 0;
		this.numErrors = 0;
	}

	/* (non-Javadoc)
	 * @see java.util.logging.Formatter#format(java.util.logging.LogRecord)
	 */
	@Override
	public String format(LogRecord record) {
		if (record instanceof ToolsLogRecord) {
			ToolsLogRecord tlr = (ToolsLogRecord) record;
			StringBuffer message = new StringBuffer();
			if (tlr.getLevel().intValue() == ToolsLevel.NOTIFICATION.intValue()) {
				return tlr.getMessage() + lineFeed;
			}
			if (tlr.getLevel().intValue() == ToolsLevel.WARNING.intValue()) {
				++this.numWarnings;
			} else if (tlr.getLevel().intValue() == ToolsLevel.SEVERE
					.intValue()) {
				++this.numErrors;
			}
			if (tlr.getLevel().intValue() != ToolsLevel.CONFIGURATION
					.intValue()) {
				if (tlr.getLevel().intValue() == ToolsLevel.SEVERE.intValue()) {
					message.append("ERROR");
				} else {
					message.append(tlr.getLevel().getName());
				}
				message.append(":   ");
			}
			if (tlr.getFilename() != null) {
				message.append("[" + tlr.getFilename() + "] ");
			}
			if (tlr.getLine() != -1) {
				message.append("line " + tlr.getLine() + ": ");
			}
			message.append(tlr.getMessage());
			message.append(lineFeed);

			return message.toString();
		} else {
			return "******* " + record.getMessage() + " ************"
					+ lineFeed;
		}
	}
	
	abstract protected void processSummary();

	public String getTail(Handler handler) {
		StringBuffer report = new StringBuffer("");

		processSummary();

		report.append(lineFeed);
		report.append(this.summary);
		report.append(doubleLineFeed + "End of Log" + doubleLineFeed);

		return report.toString();
	}

}
