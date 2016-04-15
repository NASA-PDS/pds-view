// Copyright 2006-2016, by the California Institute of Technology.
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
package gov.nasa.pds.harvest.search.crawler;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.jpl.oodt.cas.crawl.action.CrawlerActionRepo;
import gov.nasa.jpl.oodt.cas.metadata.Metadata;
import gov.nasa.pds.harvest.search.constants.Constants;
import gov.nasa.pds.harvest.search.crawler.metadata.extractor.Pds4MetExtractorConfig;
import gov.nasa.pds.harvest.search.logging.ToolsLevel;
import gov.nasa.pds.harvest.search.logging.ToolsLogRecord;
import gov.nasa.pds.registry.model.ExtrinsicObject;

/**
 * A crawler class for a PDS Collection file.
 *
 * @author mcayanan
 *
 */
public class CollectionCrawler extends PDSProductCrawler {
  /** Logger object. */
  private static Logger log = Logger.getLogger(
      CollectionCrawler.class.getName());

  /**
   * Constructor.
   *
   * @param extractorConfig A configuration class for the metadata
   * extractor.
   */
  public CollectionCrawler(Pds4MetExtractorConfig extractorConfig) {
    super(extractorConfig);
  }

  /**
   * Crawl a PDS4 collection file. Method will register the collection
   * first before attempting to register the product files it is pointing
   * to.
   *
   * @param collection The PDS4 Collection file.
   *
   */
  public void crawl(File collection) {
    //Load actions first before crawling
    CrawlerActionRepo repo = new CrawlerActionRepo();
    repo.loadActions(getActions());
    setActionRepo(repo);
    if (collection.canRead()) {
      handleFile(collection);
      Constants.collections.add(collection);
    } else {
      log.log(new ToolsLogRecord(ToolsLevel.SEVERE, "Unreadable target: "
          + collection));
    }
  }
  
  protected boolean generateDoc(File product, Metadata productMetadata) {
    try {
      ExtrinsicObject extrinsic = createProduct(productMetadata, product);
      getSearchDocGenerator().generate(extrinsic, productMetadata);
      String lidvid = extrinsic.getLid() + "::" + extrinsic.getSlot(
          Constants.PRODUCT_VERSION).getValues().get(0);
      LOG.log(new ToolsLogRecord(Level.INFO, 
          "Successfully generated document file for " + lidvid + ".", product));
      Constants.collectionMap.put(extrinsic.getLid(), extrinsic);
    } catch (Exception e) {
       LOG.log(new ToolsLogRecord(Level.SEVERE, 
           "Exception generating document: " + e.getMessage(), product));
      return false;
    }
    return true;
  }  
}
