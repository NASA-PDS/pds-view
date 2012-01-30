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


package gov.nasa.pds.citool.logging;

import gov.nasa.pds.citool.logging.ToolsLevel;
import gov.nasa.pds.citool.logging.ToolsLogRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Class to format the report when running the Catalog Ingestion Tool
 * to compare catalog files.
 * 
 * @author mcayanan
 *
 */
public class CompareFormatter extends Formatter {
	private List<ToolsLogRecord> records;
	private StringBuffer config;
	private StringBuffer parameters;
	private StringBuffer body;
	private StringBuffer summary;
	private int numSame;
	private int numDifferent;
	private int numSkipped;
	
	private static String lineFeed = System.getProperty("line.separator", "\n");
	private static String doubleLineFeed = System.getProperty("line.separator", "\n") + System.getProperty("line.separator", "\n");
	
	public CompareFormatter() {
		records = new ArrayList<ToolsLogRecord>();
		config = new StringBuffer();
		parameters = new StringBuffer("Parameter Settings:" + doubleLineFeed);
		summary = new StringBuffer("Summary:" + doubleLineFeed);
		body = new StringBuffer("Compare Details:" + lineFeed);
		numSame = 0;
		numDifferent = 0;
		numSkipped = 0;
	}
	
	public String format(LogRecord record) {
		ToolsLogRecord toolsRecord = (ToolsLogRecord) record;
		
		if (toolsRecord.getLevel() == ToolsLevel.CONFIGURATION) {
			config.append("  " + toolsRecord.getMessage() + lineFeed);
		} 
		else if (toolsRecord.getLevel() == ToolsLevel.PARAMETER) {
			parameters.append("  " + toolsRecord.getMessage() + lineFeed);
		}
		else if (toolsRecord.getLevel() == ToolsLevel.NOTIFICATION && 
				  ("SAME".equals(toolsRecord.getMessage()) ||
				   "DIFFERENT".equals(toolsRecord.getMessage()) || 
				   "FAIL".equals(toolsRecord.getMessage()) ||
				   "SKIP".equals(toolsRecord.getMessage()))) {
			if("SAME".equals(toolsRecord.getMessage()))
				++numSame;
			else if("DIFFERENT".equals(toolsRecord.getMessage()))
				++numDifferent;
			else if("SKIP".equals(toolsRecord.getMessage()))
				++numSkipped;
			
			return processRecords(toolsRecord);
		} else {
			records.add(toolsRecord);
		}
		return "";
	}
	
	private String processRecords(ToolsLogRecord record) {
		
		body.append(lineFeed + "  " + record.getMessage() + ": " + record.getFile() + lineFeed);
		
		for(ToolsLogRecord tlr : records) {	
			if(tlr.getFile() != null && (record.getFile().equals(tlr.getFile()) || record.getFile().equals(tlr.getContext()))) {
				if("SOURCE".equalsIgnoreCase(tlr.getMessage()) && 
				  (tlr.getLevel() == CIToolLevel.INFO_NOTIFY || 
				   tlr.getLevel() == CIToolLevel.WARNING_NOTIFY ||
				   tlr.getLevel() == CIToolLevel.SEVERE_NOTIFY)) {
					if(tlr.getContext() != null) {
						body.append("         " + tlr.getMessage() + ": ");
						body.append("line " + tlr.getLine() + " of " + tlr.getFile() + lineFeed);
					}
				}
				else if(tlr.getLevel() == CIToolLevel.DIFF) {
					if("DONE".equals(tlr.getMessage()))
						body.append(lineFeed);
					else
						body.append("         " + tlr.getMessage() + lineFeed);
				}
				else if(tlr.getLevel() == CIToolLevel.SEVERE) {
					//body.append("      ERROR  ");
					body.append("      ");
					if(tlr.getLine() != -1)
						body.append("line " + tlr.getLine() + ": ");
					body.append(tlr.getMessage() + lineFeed);
				}
				else {
					//body.append("      " + tlr.getLevel().getName() + "  ");
					body.append("      ");
					if(tlr.getLine() != -1)
						body.append("line " + tlr.getLine() + ": ");
					body.append(tlr.getMessage() + lineFeed);
				}
			}
		}
		records = new ArrayList<ToolsLogRecord>();
		return "";
	}
	
	private void processSummary() {
		if(numDifferent == 0 && numSame == 0 && numSkipped == 0) {
			summary.append("  [ ] Differences Found" + lineFeed);
			summary.append("  [ ] No Differences Found" + lineFeed);			
		}
		else if(numDifferent == 0 && numSkipped == 0) {
			summary.append("  [ ] Differences Found" + lineFeed);
			summary.append("  [X] No Differences Found" + lineFeed);
		}
		else {
			summary.append("  [X] Differences Found" + lineFeed);
			summary.append("  [ ] No Differences Found" + lineFeed);			
		}	
	}
	
	public String getTail(Handler handler) {
		StringBuffer report = new StringBuffer("");
		
		processSummary();

		report.append("PDS Catalog Ingestion Tool Report" + doubleLineFeed);
		report.append(config);
		report.append(lineFeed);
		report.append(parameters);
		report.append(lineFeed);
		report.append(summary);
		report.append(lineFeed);
		report.append(body);
		
		report.append(doubleLineFeed + "End of Report" + lineFeed);
		return report.toString();
	}
}
