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
package gov.nasa.pds.harvest;

import gov.nasa.jpl.oodt.cas.crawl.action.CrawlerAction;
import gov.nasa.pds.harvest.crawler.BundleCrawler;
import gov.nasa.pds.harvest.crawler.CollectionCrawler;
import gov.nasa.pds.harvest.crawler.PDS3ProductCrawler;
import gov.nasa.pds.harvest.crawler.PDSProductCrawler;
import gov.nasa.pds.harvest.crawler.actions.AssociationPublisherAction;
import gov.nasa.pds.harvest.crawler.actions.RegistryUniquenessCheckerAction;
import gov.nasa.pds.harvest.crawler.actions.ValidateProductAction;
import gov.nasa.pds.harvest.crawler.daemon.HarvestDaemon;
import gov.nasa.pds.harvest.crawler.metadata.extractor.Pds3MetExtractorConfig;
import gov.nasa.pds.harvest.crawler.metadata.extractor.Pds4MetExtractorConfig;
import gov.nasa.pds.harvest.ingest.RegistryIngester;
import gov.nasa.pds.harvest.policy.Policy;
import gov.nasa.pds.harvest.security.SecuredUser;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Front end class to the Harvest tool.
 *
 * @author mcayanan
 *
 */
public class Harvester {
  /** An authorized user. */
  private SecuredUser securedUser;

  /** URL of the registry service. */
  private String registryUrl;

  /** An ingester for the PDS Registry Service. */
  private RegistryIngester ingester;

  /** Flag to enable/disable validation. */
  private boolean doValidation;

  /** The port number to use for the daemon if running Harvest in continuous
   *  mode.
   */
  private int daemonPort;

  /** The wait interval in seconds in between crawls when running Harvest in
   *  continuous mode.
   */
  private int waitInterval;

  /**
   * Constructor.
   *
   * @param registryUrl The registry location.
   *
   */
  public Harvester(String registryUrl) {
    this.registryUrl = registryUrl;
    this.securedUser = null;
    this.registryUrl = registryUrl;
    this.ingester = new RegistryIngester();
    this.doValidation = true;
    this.daemonPort = -1;
    this.waitInterval = -1;
  }

  /**
   * Sets the security for the Harvest tool.
   *
   * @param user An authorized user.
   */
  public void setSecuredUser(SecuredUser user) {
    this.securedUser = user;
    this.ingester = new RegistryIngester(user.getName(), user.getPassword());
  }

  /**
   * Sets the daemon port.
   *
   * @param port The port number to use.
   */
  public void setDaemonPort(int port) {
    this.daemonPort = port;
  }

  /**
   * Sets the wait interval in seconds in between crawls.
   *
   * @param interval The wait interval in seconds.
   */
  public void setWaitInterval(int interval) {
    this.waitInterval = interval;
  }

  /**
   * Get the default crawler actions.
   *
   * @return A list of default crawler actions.
   * @throws RegistryClientException
   */
  private List<CrawlerAction> getDefaultCrawlerActions() {
    List<CrawlerAction> ca = new ArrayList<CrawlerAction>();
    ca.add(new RegistryUniquenessCheckerAction(registryUrl, this.ingester));
    if (securedUser != null) {
      ca.add(new AssociationPublisherAction(registryUrl,
          securedUser.getName(), securedUser.getPassword()));
    } else {
      ca.add(new AssociationPublisherAction(registryUrl));
    }
    if (doValidation) {
      ca.add(new ValidateProductAction());
    }
    return ca;
  }

  /**
   * Harvest the products specified in the given policy.
   *
   * @param policy An object representation of the configuration file that
   *  specifies what to harvest.
   *
   * @throws MalformedURLException If the registry url is malformed.
   */
  public void harvest(Policy policy) throws MalformedURLException {
    boolean doCrawlerPersistance = false;
    if (waitInterval != -1 && daemonPort != -1) {
      doCrawlerPersistance = true;
    }
    Pds4MetExtractorConfig pds4MetExtractorConfig = new Pds4MetExtractorConfig(
        policy.getCandidates().getProductMetadata());
    boolean enableValidation = policy.getValidation().isEnabled();
    List<PDSProductCrawler> crawlers = new ArrayList<PDSProductCrawler>();
    // Crawl bundles
    for (String bundle : policy.getBundles().getFile()) {
      BundleCrawler bc = new BundleCrawler(pds4MetExtractorConfig);
      bc.setProductPath(bundle);
      crawlers.add(bc);
    }
    // Crawl collections
    for (String collection : policy.getCollections().getFile()) {
      CollectionCrawler cc = new CollectionCrawler(pds4MetExtractorConfig);
      cc.setProductPath(collection);
      crawlers.add(cc);
    }
    // Crawl directories
    for (String directory : policy.getDirectories().getPath()) {
      PDSProductCrawler pc = new PDSProductCrawler(pds4MetExtractorConfig);
      pc.setProductPath(directory);
      List<String> filters = policy.getDirectories().getFilePattern();
      if (!filters.isEmpty()) {
        pc.setFileFilter(filters);
      }
      crawlers.add(pc);
    }
    // Crawl a PDS3 directory
    if (policy.getPds3Directory().getPath() != null) {
      PDS3ProductCrawler p3c = new PDS3ProductCrawler();
      p3c.setPDS3MetExtractorConfig(new Pds3MetExtractorConfig(
          policy.getCandidates().getPds3ProductMetadata()));
      p3c.setProductPath(policy.getPds3Directory().getPath());
      List<String> filters = policy.getPds3Directory().getFilePattern();
      if (!filters.isEmpty()) {
        p3c.setFileFilter(filters);
      }
      crawlers.add(p3c);
    }
    // Perform crawl while looping through the crawler list if
    // crawler persistance is disabled.
    for (PDSProductCrawler crawler : crawlers) {
      if (crawler instanceof PDS3ProductCrawler) {
        doValidation = false;
      } else {
        doValidation = enableValidation;
      }
      crawler.setRegistryUrl(registryUrl);
      crawler.setIngester(ingester);
      crawler.addActions(getDefaultCrawlerActions());
      if (!doCrawlerPersistance) {
        crawler.crawl();
      }
    }
    // If crawler persistance is enabled, use the HarvestDaemon object to
    // do the crawling
    if (doCrawlerPersistance) {
      new HarvestDaemon(waitInterval, crawlers, daemonPort).startCrawling();
    }
  }
}
