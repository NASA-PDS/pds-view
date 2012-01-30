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

package gov.nasa.pds.citool.logging;

import java.util.List;
import java.util.ArrayList;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import org.apache.commons.io.FilenameUtils;

import gov.nasa.pds.tools.label.Label;
import gov.nasa.pds.citool.logging.ToolsLogRecord;
import gov.nasa.pds.citool.status.Status;

public class ValidateFormatter extends Formatter {
	private int numPassed;
	private int numFailed;
	private int numSkipped;
	private int numRIPassed;
	private int numRIFailed;
	private int numRISkipped;
	private int numNewStandardValues;
	private List<ToolsLogRecord> records;
	private StringBuffer config;
	private StringBuffer parameters;
	private StringBuffer body;
	private StringBuffer summary;
	private boolean reportRI;
	private boolean reportSV;
	private static String lineFeed = System.getProperty("line.separator", "\n");
	private static String doubleLineFeed = System.getProperty("line.separator", "\n") + System.getProperty("line.separator", "\n");

	public ValidateFormatter() {
		numPassed = 0;
		numFailed = 0;
		numSkipped = 0;
		numRIPassed = 0;
		numRIFailed = 0;
		numRISkipped = 0;
		numNewStandardValues = 0;
		reportRI = false;
		reportSV = false;
		records = new ArrayList<ToolsLogRecord>();
		config = new StringBuffer();
		parameters = new StringBuffer("Parameter Settings:" + doubleLineFeed);
		summary = new StringBuffer("Summary:" + doubleLineFeed);
		body = new StringBuffer("Validation Details:" + doubleLineFeed);
	}

	/* (non-Javadoc)
     * @see java.util.logging.Formatter#format(java.util.logging.LogRecord)
     */
	public String format(LogRecord record) {
		ToolsLogRecord toolsRecord = (ToolsLogRecord) record;

		if (toolsRecord.getLevel() == CIToolLevel.CONFIGURATION) {
			config.append("  " + toolsRecord.getMessage() + lineFeed);
		}
		else if (toolsRecord.getLevel() == CIToolLevel.PARAMETER) {
			parameters.append("  " + toolsRecord.getMessage() + lineFeed);
		}
		else if( (record.getLevel() == CIToolLevel.NOTIFICATION) && "BEGIN_RI".equals(toolsRecord.getMessage())) {
			reportRI = true;
			body.append(doubleLineFeed + "Referential Integrity Details:" + doubleLineFeed);
		}
		else if( (record.getLevel() == CIToolLevel.NOTIFICATION) && "BEGIN_SV".equals(toolsRecord.getMessage())) {
			reportSV = true;
			body.append(doubleLineFeed + "New Standard Values Found:" + doubleLineFeed);
		}
		else if( (record.getLevel() == CIToolLevel.NOTIFICATION) && reportSV ) {
			body.append(toolsRecord.getMessage() + lineFeed);
			++numNewStandardValues;
		}
		else if (toolsRecord.getLevel() == CIToolLevel.NOTIFICATION && (toolsRecord.getMessage().startsWith(Status.PASS.getName()) ||
				toolsRecord.getMessage().startsWith(Status.SKIP.getName()) || toolsRecord.getMessage().startsWith(Status.FAIL.getName()))) {
			if(reportRI)
				return processRIRecords(toolsRecord);
			else
				return processRecords(toolsRecord);
		} else {
			records.add(toolsRecord);
		}

		return "";
	}

	private String processRIRecords(ToolsLogRecord record) {
		if (record.getMessage().startsWith("PASS"))
			numRIPassed++;
		else if (record.getMessage().startsWith("FAIL"))
			numRIFailed++;
		else if (record.getMessage().startsWith("SKIP"))
			numRISkipped++;

		body.append(lineFeed + "  " + record.getMessage() + lineFeed);
		body.append("    Parent File(s): " + FilenameUtils.getName(record.getFile()) + lineFeed);

		for (ToolsLogRecord tlr : records) {
			if (tlr.getFile() != null && (record.getFile().equals(tlr.getFile()) || record.getFile().equals(tlr.getContext()))) {
				if (tlr.getLevel() == CIToolLevel.NOTIFICATION)
					body.append("    " + tlr.getMessage() + lineFeed);
				else if("SOURCE".equalsIgnoreCase(tlr.getMessage()) &&
						  (tlr.getLevel() == CIToolLevel.INFO_NOTIFY ||
						   tlr.getLevel() == CIToolLevel.WARNING_NOTIFY ||
						   tlr.getLevel() == CIToolLevel.SEVERE_NOTIFY)) {
							if(tlr.getContext() != null) {
								body.append("         " + tlr.getMessage() + ": ");
								if(tlr.getLine() != -1)
									body.append("line " + tlr.getLine() + ": ");
								body.append(FilenameUtils.getName(tlr.getFile()) + lineFeed);
							}
						}
				else if(tlr.getLevel() == CIToolLevel.DIFF) {
					if("DONE".equals(tlr.getMessage()))
						body.append(lineFeed);
					else
						body.append("         " + tlr.getMessage() + lineFeed);
				}
				else {
					body.append("      ");
					if("BEGIN".equals(tlr.getMessage()))
						body.append("Begin checking: " + FilenameUtils.getName(tlr.getFile()) + lineFeed);
					else if("END".equals(tlr.getMessage()))
						body.append("End checking: " + FilenameUtils.getName(tlr.getFile()) + lineFeed);
					else {
						if (tlr.getLine() != -1)
							body.append("line " + tlr.getLine() + ": ");
						if(tlr.getContext() != null)
							body.append(FilenameUtils.getName(tlr.getFile()) + ": ");
						body.append(tlr.getMessage() + lineFeed);
					}
				}
			}
		}
		records = new ArrayList<ToolsLogRecord>();
		return "";
	}

	private String processRecords(ToolsLogRecord record) {
		if (record.getMessage().equals("PASS"))
			numPassed++;
		else if (record.getMessage().equals("FAIL"))
			numFailed++;
		else if (record.getMessage().equals("SKIP"))
			numSkipped++;

		body.append(lineFeed + "  " + record.getMessage() + ": " + record.getFile() + lineFeed);

		for (ToolsLogRecord tlr : records) {
			if (tlr.getFile() != null && (record.getFile().equals(tlr.getFile()) || record.getFile().equals(tlr.getContext()))) {
				if (tlr.getLevel() == CIToolLevel.NOTIFICATION) {
					if(tlr.getContext() != null)
						body.append("    " + tlr.getMessage() + ": " + tlr.getFile() + lineFeed);

				}
				else if("SOURCE".equalsIgnoreCase(tlr.getMessage()) &&
						  (tlr.getLevel() == CIToolLevel.INFO_NOTIFY ||
						   tlr.getLevel() == CIToolLevel.WARNING_NOTIFY ||
						   tlr.getLevel() == CIToolLevel.SEVERE_NOTIFY)) {
							if(tlr.getContext() != null) {
								body.append("         " + tlr.getMessage() + ": ");
								if(tlr.getLine() != -1)
									body.append("line " + tlr.getLine() + ": ");
								body.append(tlr.getFile() + lineFeed);
							}
						}
				else if(tlr.getLevel() == CIToolLevel.DIFF) {
					if("DONE".equals(tlr.getMessage()))
						body.append(lineFeed);
					else
						body.append("         " + tlr.getMessage() + lineFeed);
				}
				else if (tlr.getLevel() != CIToolLevel.SEVERE) {
					body.append("      " + tlr.getLevel().getName() + "  ");
					if (tlr.getLine() != -1)
						body.append("line " + tlr.getLine() + ": ");
					body.append(tlr.getMessage() + lineFeed);
				}
				else {
					body.append("      ERROR  ");
					if (tlr.getLine() != -1)
						body.append("line " + tlr.getLine() + ": ");
					body.append(tlr.getMessage() + lineFeed);
				}
			}
		}
		records = new ArrayList<ToolsLogRecord>();
		return "";
	}

	private void processSummary() {
		int totalFiles = numPassed + numFailed + numSkipped;
		int totalValidated = numPassed + numFailed;
		int totalRIChecks = numRIPassed + numRIFailed;
		summary.append("  " + totalValidated + " of " + totalFiles + " validated, " + numSkipped + " skipped" + lineFeed);
		summary.append("  " + numPassed + " of " + totalValidated + " passed" + doubleLineFeed);
		summary.append("  " + totalRIChecks + " referential integrity check(s) made, " + numRISkipped + " skipped" + lineFeed);
		summary.append("  " + numRIPassed + " of " + totalRIChecks + " passed" + doubleLineFeed);
		summary.append("  " + numNewStandardValues + " new standard value(s) found" + doubleLineFeed);
	}

	public String getTail(Handler handler) {
		StringBuffer report = new StringBuffer("");

		processSummary();

		report.append("PDS Catalog Ingestion Tool Report" + doubleLineFeed);
		report.append(config + lineFeed);
		report.append(parameters + lineFeed);
		report.append(summary + lineFeed);
		report.append(body + lineFeed);
		report.append(lineFeed + "End of Report" + lineFeed);
		return report.toString();
	}

}
