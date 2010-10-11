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
import gov.nasa.jpl.oodt.cas.crawl.action.CrawlerActionRepo;
import gov.nasa.pds.harvest.context.InventoryReaderException;
import gov.nasa.pds.harvest.crawler.HarvestCrawler;
import gov.nasa.pds.harvest.crawler.actions.AssociationPublisherAction;
import gov.nasa.pds.harvest.crawler.actions.LogMissingReqMetadataAction;
import gov.nasa.pds.harvest.crawler.actions.RegistryUniquenessCheckerAction;
import gov.nasa.pds.harvest.crawler.metadata.extractor.PDSMetExtractorConfig;
import gov.nasa.pds.harvest.ingest.RegistryIngester;
import gov.nasa.pds.harvest.policy.*;
import gov.nasa.pds.harvest.security.SecuredUser;

import java.io.File;
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
    private SecuredUser securedUser;
    private HarvestCrawler crawler;
    private List<String> objectTypes;

    /**
     * Constructor
     *
     * @param registryURL The registry location.
     * @param candidates Class containing the products to look for and what
     * metadata to extract.
     *
     * @throws MalformedURLException
     */
    public Harvester(String registryURL, Candidate candidates)
    throws MalformedURLException {
        this(registryURL, candidates, null);
    }

    /**
     *
     *
     * @param registryURL The registry location.
     * @param candidates Class containing the products to look for and what
     * metadata to extract.
     * @param user If security is enabled in the registry, this parameter
     * authenticates the user running the tool.
     *
     * @throws MalformedURLException
     */
    public Harvester(String registryURL, Candidate candidates,
            SecuredUser user) throws MalformedURLException {
        crawler = new HarvestCrawler(new PDSMetExtractorConfig(candidates));
        objectTypes = new ArrayList<String>();
        for(ProductMetadata p : candidates.getProductMetadata()) {
            objectTypes.add(p.getObjectType());
        }
        crawler.setRegistryUrl(registryURL);
        if(user != null) {
            this.securedUser = user;
            crawler.setIngester(
                    new RegistryIngester(user.getName(), user.getToken()));
        }
        else {
            this.securedUser = null;
            crawler.setIngester(new RegistryIngester());
        }
        crawler.setActionRepo(createCrawlerActions());
    }

    /**
     * Creates the different crawler actions to take while traversing
     * through a directory.
     *
     * @return A class containing the crawler actions.
     */
    private CrawlerActionRepo createCrawlerActions() {
        CrawlerActionRepo repo = new CrawlerActionRepo();
        List<CrawlerAction> actions = new ArrayList<CrawlerAction>();
        actions.add(new RegistryUniquenessCheckerAction(
                crawler.getRegistryUrl(), crawler.getRegistryIngester()));
        actions.add(new LogMissingReqMetadataAction(
                crawler.getRequiredMetadata()));
        if(securedUser != null) {
            actions.add(new AssociationPublisherAction(
                    crawler.getRegistryUrl(), securedUser.getName(),
                    securedUser.getToken()));
        }
        else {
            actions.add(
                    new AssociationPublisherAction(crawler.getRegistryUrl()));
        }
        repo.loadActions(actions);
        return repo;
    }

    /**
     * Harvests the products in the given directory.
     *
     * @param directory A starting directory.
     * @param filePatterns Specify file patterns to search for while crawling
     * the directories.
     */
    public void harvest(File directory, List<String> filePatterns) {
        crawler.crawl(directory, filePatterns);
    }

    /**
     * Harvests the products given in the PDS4 Inventory file.
     * This method will first register the given Inventory file,
     * then proceed to crawl the file for references to PDS4
     * data products.
     *
     * @param bundle a PDS4 bundle file
     *
     * @throws InventoryReaderException
     */
    public void harvestBundle(File bundle) throws InventoryReaderException {
        crawler.crawlBundle(bundle);
    }

    /**
     * Harvests the products given in the PDS4 Inventory file.
     * This method will first register the given Inventory file,
     * then proceed to crawl the file for references to PDS4
     * data products.
     *
     * @param collection a PDS4 collection file
     *
     * @throws InventoryReaderException
     *
     */
    public void harvestCollection(File collection)
    throws InventoryReaderException {
        crawler.crawlCollection(collection);
    }
}
