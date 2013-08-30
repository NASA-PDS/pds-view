// Copyright 2006-2013, by the California Institute of Technology.
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
// $Id: HarvestFormatter.java 10509 2012-05-10 15:45:25Z mcayanan $
package gov.nasa.pds.search.core.logging.formatter;

import gov.nasa.pds.search.core.logging.ToolsLevel;
import gov.nasa.pds.search.core.logging.ToolsLogRecord;
import gov.nasa.pds.search.core.stats.SearchCoreStats;

import java.io.File;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Class that formats the Harvest logging messages.
 * 
 * @author jpadams
 * @author mcayanan
 * 
 */
public class SearchCoreFormatter extends Formatter {
	private static String lineFeed = System.getProperty("line.separator", "\n");
	private static String doubleLineFeed = lineFeed + lineFeed;
	private static String sectionBreak = lineFeed+ "=================================================="
			+ lineFeed;

	private StringBuffer config;
	private StringBuffer summary;

	private int numWarnings;

	private int numErrors;

	public SearchCoreFormatter() {
		this.config = new StringBuffer("PDS Search Core Tool Log"
				+ doubleLineFeed);
		this.summary = new StringBuffer("Summary:" + doubleLineFeed);
		this.numWarnings = 0;
		this.numErrors = 0;
	}

	public String format(LogRecord record) {
		if (record instanceof ToolsLogRecord) {
			ToolsLogRecord tlr = (ToolsLogRecord) record;
			StringBuffer message = new StringBuffer();
			if (tlr.getLevel().intValue() == ToolsLevel.NOTIFICATION.intValue()) {
				return tlr.getMessage() + lineFeed;
			}
			if (tlr.getLevel().intValue() == ToolsLevel.WARNING.intValue()) {
				++this.numWarnings;
				++SearchCoreStats.numWarnings;
			} else if (tlr.getLevel().intValue() == ToolsLevel.SEVERE
					.intValue()) {
				++this.numErrors;
				++SearchCoreStats.numErrors;
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

	private void processSummary() {
		/*if (!SearchCoreStats.missingSlots.isEmpty()) {
			this.summary.append(sectionBreak);
			this.summary.append("Missing Slots:" + lineFeed);
			for (String key : SearchCoreStats.missingSlots.keySet()) {
				this.summary.append(key + lineFeed);
				for (String slotName : SearchCoreStats.missingSlots.get(key)) {
					this.summary.append("--- " + slotName + lineFeed);
				}
				this.summary.append(lineFeed);
			}
			this.summary.append(doubleLineFeed);
		}

		if (!SearchCoreStats.missingAssociations.isEmpty()) {
			this.summary.append(sectionBreak);
			this.summary.append("Missing Associations:" + lineFeed);
			for (String key : SearchCoreStats.missingAssociations.keySet()) {
				this.summary.append(key + lineFeed);
				for (String slotName : SearchCoreStats.missingAssociations
						.get(key)) {
					this.summary.append("----- " + slotName + lineFeed);
				}
				this.summary.append(lineFeed);
			}
			this.summary.append(doubleLineFeed);
		}

		if (!SearchCoreStats.missingAssociationTargets.isEmpty()) {
			this.summary.append(sectionBreak);
			this.summary.append("Missing Associated Objects:" + lineFeed);
			for (String key : SearchCoreStats.missingAssociationTargets
					.keySet()) {
				this.summary.append(key + lineFeed);
				for (String slotName : SearchCoreStats.missingAssociationTargets
						.get(key)) {
					this.summary.append("----- " + slotName + lineFeed);
				}
				this.summary.append(lineFeed);
			}
		}*/
		this.summary.append(sectionBreak);
		this.summary.append("The Numbers: " + lineFeed);
		this.summary.append("-- Number of Warnings: "
				+ SearchCoreStats.numWarnings + lineFeed);
		this.summary.append("-- Number of Errors: " + SearchCoreStats.numErrors
				+ lineFeed);
		this.summary.append("-- Bad Registries: " + SearchCoreStats.badRegistries
				+ lineFeed);
		this.summary.append("-- Number of Missing Associations: " + SearchCoreStats.missingAssociations.size()
				+ lineFeed);
		this.summary.append("-- Association Cache Hits: "
				+ SearchCoreStats.assocCacheHits + lineFeed);
		this.summary.append("-- Number of products: "
				+ SearchCoreStats.numProducts + lineFeed);
		this.summary.append(sectionBreak);
		this.summary.append("Processing Time: " + lineFeed);

		for (String key : SearchCoreStats.runTimesMap.keySet()) {
			this.summary.append("-- " + key + ": "
					+ SearchCoreStats.runTimesMap.get(key) + lineFeed);
		}

		this.summary.append(sectionBreak);
		this.summary.append("Total Processing Time: "
				+ SearchCoreStats.getOverallTime() + lineFeed);
	}

	public String getTail(Handler handler) {
		StringBuffer report = new StringBuffer("");

		processSummary();

		report.append(lineFeed);
		report.append(this.summary);
		report.append(doubleLineFeed + "End of Log" + doubleLineFeed);

		return report.toString();
	}
}
