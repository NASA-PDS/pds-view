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
// $Id$
package gov.nasa.pds.report.manager.logging.formatter;

/**
 * Class that formats the Harvest logging messages.
 * 
 * @author jpadams
 * @author mcayanan
 * 
 */
public class ReportManagerFormatter extends PDSFormatter {

	private StringBuffer config;

	public ReportManagerFormatter() {
		super();
		this.config = new StringBuffer("PDS Report Manager Log"
				+ doubleLineFeed);
	}

	protected void processSummary() {
		this.summary.append("TBD");
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
		
		/*this.summary.append(sectionBreak);
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
				*/
	}
}
