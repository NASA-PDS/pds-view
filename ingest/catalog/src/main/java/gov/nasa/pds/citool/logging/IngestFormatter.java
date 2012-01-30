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
 * to ingest catalog files.
 *
 * @author hyunlee
 *
 */
public class IngestFormatter extends Formatter {
	private List<ToolsLogRecord> records;
	private StringBuffer config;
	private StringBuffer parameters;
	private StringBuffer body;
	private StringBuffer summary;
	private boolean headerPrinted;

	private static String lineFeed = System.getProperty("line.separator", "\n");
	private static String doubleLineFeed = System.getProperty("line.separator", "\n") + System.getProperty("line.separator", "\n");

	public IngestFormatter() {
		records = new ArrayList<ToolsLogRecord>();
		config = new StringBuffer();
		headerPrinted = false;
		parameters = new StringBuffer("Parameter Settings:" + doubleLineFeed);
		summary = new StringBuffer("Summary:" + doubleLineFeed);
		body = new StringBuffer("Catalog Ingestion Details:" + lineFeed);
	}

	public String format(LogRecord record) {
		ToolsLogRecord toolsRecord = (ToolsLogRecord) record;

		if (toolsRecord.getLevel() == ToolsLevel.CONFIGURATION) {
			config.append("  " + toolsRecord.getMessage() + lineFeed);
		}
		else if (toolsRecord.getLevel() == ToolsLevel.PARAMETER) {
			parameters.append("  " + toolsRecord.getMessage() + lineFeed);
		}
		else
			return processRecords(toolsRecord);
		return "";
	}

	private String processRecords(ToolsLogRecord record) {
		body.append(lineFeed + "  " + record.getMessage());
		records = new ArrayList<ToolsLogRecord>();
		return "";
	}

	private void processSummary() {
		summary.append("  Catalog Ingestion is completed." + lineFeed);
		//summary.append("      Number of processed files: " + gov.nasa.pds.citool.ingestor.Ingestor.fileCount + lineFeed);
		summary.append("      Number of successful ingestion to the table: " + gov.nasa.pds.citool.ingestor.CatalogDB.okCount + lineFeed);
		summary.append("      Number of failed ingestion to the table: " + gov.nasa.pds.citool.ingestor.CatalogDB.failCount + lineFeed);
		//summary.append("      Number of new standard values ingested: " + gov.nasa.pds.citool.ingestor.CatalogDB.newStdValueCount + lineFeed);
	}

	public String getTail(Handler handler) {
		StringBuffer report = new StringBuffer("");

		processSummary();

		report.append(doubleLineFeed + "PDS Catalog Ingestion Tool Report" + doubleLineFeed);
		report.append(config);
		report.append(lineFeed);
		report.append(parameters);
		report.append(lineFeed);
		report.append(summary);
		report.append(lineFeed);
		report.append(body);
		report.append(doubleLineFeed + "End of Report" + doubleLineFeed);

		return report.toString();
	}
}
