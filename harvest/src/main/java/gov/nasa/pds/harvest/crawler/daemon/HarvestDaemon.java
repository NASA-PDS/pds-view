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
package gov.nasa.pds.harvest.crawler.daemon;

import java.util.List;
import java.util.logging.Logger;

import org.apache.xmlrpc.WebServer;

import gov.nasa.jpl.oodt.cas.crawl.action.CrawlerAction;
import gov.nasa.jpl.oodt.cas.crawl.daemon.CrawlDaemon;
import gov.nasa.jpl.oodt.cas.crawl.status.IngestStatus;
import gov.nasa.jpl.oodt.cas.crawl.status.IngestStatus.Result;
import gov.nasa.pds.harvest.crawler.PDSProductCrawler;
import gov.nasa.pds.harvest.crawler.actions.AssociationPublisherAction;
import gov.nasa.pds.harvest.crawler.stats.AssociationStats;
import gov.nasa.pds.harvest.logging.ToolsLevel;
import gov.nasa.pds.harvest.logging.ToolsLogRecord;

/**
 * Class that provides the capability to make the Harvest Tool run
 * continuously via a daemon.
 *
 * @author mcayanan
 *
 */
public class HarvestDaemon extends CrawlDaemon {
  /** To log messages */
  private static Logger log = Logger.getLogger(
      HarvestDaemon.class.getName());

  /** The port number to be used */
  private int daemonPort;

  /**
   * Constructor
   *
   * @param wait The time in seconds to wait in between crawls.
   * @param crawler The PDSProductCrawler to be used.
   * @param port The port nunmber to be used.
   */
  public HarvestDaemon(int wait, PDSProductCrawler crawler, int port) {
    super(wait, crawler, port);
    daemonPort = port;
  }

  /**
   * Starts the crawling mechanism.
   *
   */
  public void startCrawling() {
    WebServer server = new WebServer(daemonPort);
    server.addHandler("crawldaemon", this);
    server.start();

    log.log(new ToolsLogRecord(ToolsLevel.INFO, "Starting crawler daemon."));
    getCrawler().setInContinuousMode(true);

    while (isRunning()) {
      // okay, time to crawl
      long timeBefore = System.currentTimeMillis();
      getCrawler().crawl();
      long timeAfter = System.currentTimeMillis();
      setMilisCrawling((long) getMilisCrawling() + (timeAfter - timeBefore));
      setNumCrawls(getNumCrawls() + 1);

      int filesFound = getCrawler().getNumDiscoveredProducts()
      + getCrawler().getNumBadFiles() + getCrawler().getNumFilesSkipped();
      log.log(new ToolsLogRecord(ToolsLevel.NOTIFICATION, filesFound
          + " new file(s) found."));
      //Print out some statistics if new files were found
      if (filesFound != 0) {
        printSummary();
      }
      log.log(new ToolsLogRecord(ToolsLevel.INFO, "Sleeping for: ["
          + getWaitInterval() + "] seconds"));
      // take a nap
      try {
          Thread.currentThread().sleep(getWaitInterval() * 1000);
      } catch (InterruptedException ignore) {
      }
      getCrawler().clearCrawlStats();
      getCrawler().clearIngestStatus();
    }
    log.log(new ToolsLogRecord(ToolsLevel.INFO, "Crawl Daemon: Shutting "
        + "down gracefully", getCrawler().getProductPath()));
    log.log(new ToolsLogRecord(ToolsLevel.INFO, "Num Crawls: ["
        + getNumCrawls() + "]"));
    log.log(new ToolsLogRecord(ToolsLevel.INFO, "Total time spent crawling: "
        + "[" + (getMilisCrawling() / 1000.0) + "] seconds"));
    log.log(new ToolsLogRecord(ToolsLevel.INFO, "Average Crawl Time: ["
        + (getAverageCrawlTime() / 1000.0) + "] seconds"));
    server.shutdown();
  }

  /**
   * Prints a summary of the crawl.
   *
   */
  private void printSummary() {
    int filesProcessed = getCrawler().getNumDiscoveredProducts()
    + getCrawler().getNumBadFiles();
    log.log(new ToolsLogRecord(ToolsLevel.NOTIFICATION,
        getCrawler().getNumDiscoveredProducts()
        + " of " + filesProcessed + " file(s) are candidate products, "
        + getCrawler().getNumFilesSkipped() + " skipped"));
      List<IngestStatus> ingestStatus = getCrawler().getIngestStatus();
      int productsRegistered = 0;
      int productsNotRegistered = 0;
      for (IngestStatus status : ingestStatus) {
        if (status.getResult().equals(Result.SUCCESS)) {
          ++productsRegistered;
        } else if (status.getResult().equals(Result.FAILURE)) {
          ++productsNotRegistered;
        }
      }
      int totalProducts = productsRegistered + productsNotRegistered;
      log.log(new ToolsLogRecord(ToolsLevel.NOTIFICATION, productsRegistered
          + " of " + totalProducts + " candidate products registered."));
      AssociationStats aStats = getAssociationStats(
          getCrawler().getActions());
      int totalAssociations = aStats.getNumRegistered()
      + aStats.getNumNotRegistered();
      log.log(new ToolsLogRecord(ToolsLevel.NOTIFICATION,
          aStats.getNumRegistered() + " of " + totalAssociations
          + " associations registered, " + aStats.getNumSkipped()
          + " skipped."));
      aStats.clear();
  }

  /**
   * Gets the association stats object.
   *
   * @param actions A list of crawler actions.
   *
   * @return The object representation of association statistics.
   */
  private AssociationStats getAssociationStats(
      List<CrawlerAction> actions) {
    for(CrawlerAction action : actions) {
      if (action instanceof AssociationPublisherAction) {
        AssociationPublisherAction ap = (AssociationPublisherAction) action;
        return ap.getAssociationStats();
      }
    }
    return null;
  }

  /**
   * Get the crawler being used.
   *
   */
  public PDSProductCrawler getCrawler() {
    return (PDSProductCrawler) super.getCrawler();
  }
}
