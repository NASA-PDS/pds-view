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
package gov.nasa.pds.harvest.pdap.logging.formatter;

import gov.nasa.pds.harvest.pdap.logging.ToolsLevel;
import gov.nasa.pds.harvest.pdap.logging.ToolsLogRecord;
import gov.nasa.pds.harvest.pdap.stats.HarvestPdapStats;

import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Class that formats the Harvest logging messages.
 *
 * @author mcayanan
 *
 */
public class HarvestPdapFormatter extends Formatter {
  private static String lineFeed = System.getProperty("line.separator", "\n");
  private static String doubleLineFeed = lineFeed + lineFeed;

  private StringBuffer config;
  private StringBuffer summary;

  private int numWarnings;

  private int numErrors;

  public HarvestPdapFormatter() {
    config = new StringBuffer("PDS Harvest-PDAP Tool Log" + doubleLineFeed);
    summary = new StringBuffer("Summary:" + doubleLineFeed);
    numWarnings = 0;
    numErrors = 0;
  }

  public String format(LogRecord record) {
    if (record instanceof ToolsLogRecord) {
      ToolsLogRecord tlr = (ToolsLogRecord) record;
      StringBuffer message = new StringBuffer();
      if (tlr.getLevel().intValue() == ToolsLevel.NOTIFICATION.intValue()) {
        return tlr.getMessage() + lineFeed;
      }
      if (tlr.getLevel().intValue() == ToolsLevel.WARNING.intValue()) {
        ++numWarnings;
      } else if (tlr.getLevel().intValue() == ToolsLevel.SEVERE.intValue()) {
        ++numErrors;
      }
      if (tlr.getLevel().intValue() != ToolsLevel.CONFIGURATION.intValue()) {
        if (tlr.getLevel().intValue() == ToolsLevel.SEVERE.intValue()) {
          message.append("ERROR");
        } else {
          message.append(tlr.getLevel().getName());
        }
        message.append(":   ");
      }
      if (tlr.getIdentifier() != null) {
        message.append("[" + tlr.getIdentifier() + "] ");
      }
      if (tlr.getLine() != -1) {
        message.append("line " + tlr.getLine() + ": ");
      }
      message.append(tlr.getMessage());
      message.append(lineFeed);

      return message.toString();
    } else {
      return "******* " + record.getMessage() + " ************" + lineFeed;
    }
  }

  private void processSummary() {
    int totalDatasetsRegistered = HarvestPdapStats.numDatasetsRegistered
    + HarvestPdapStats.numDatasetsNotRegistered;

    int totalResourcesRegistered = HarvestPdapStats.numResourcesRegistered
    + HarvestPdapStats.numResourcesNotRegistered;

    summary.append(HarvestPdapStats.numDatasetsProcessed
        + " dataset(s) processed" + lineFeed);
    summary.append(numErrors + " error(s), " + numWarnings + " warning(s)"
        + doubleLineFeed);
    summary.append(HarvestPdapStats.numDatasetsRegistered + " of "
        + totalDatasetsRegistered + " datasets registered." + doubleLineFeed);
    summary.append(HarvestPdapStats.numResourcesRegistered + " of "
        + totalResourcesRegistered + " resources registered."
        + doubleLineFeed);
  }

  public String getTail(Handler handler) {
    StringBuffer report = new StringBuffer("");

    processSummary();

    report.append(lineFeed);
    report.append(summary);
    report.append(doubleLineFeed + "End of Log" + doubleLineFeed);

    return report.toString();
  }
}
