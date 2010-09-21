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
import gov.nasa.pds.harvest.crawler.actions.ValidObjectTypeCheckerAction;
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

    public Harvester(String registryURL, List<CandidateProduct> candidateProducts)
                              throws MalformedURLException {
        this(registryURL, candidateProducts, null);
    }

    public Harvester(String registryURL, List<CandidateProduct> candidateProducts,
            SecuredUser user) throws MalformedURLException {
        crawler = new HarvestCrawler();
        crawler.setMetExtractorConfig(new PDSMetExtractorConfig(candidateProducts));
        objectTypes = new ArrayList<String>();
        for(CandidateProduct p : candidateProducts) {
            objectTypes.add(p.getObjectType());
        }
        crawler.setRegistryUrl(registryURL);
        if(user != null) {
            this.securedUser = user;
            crawler.setIngester(new RegistryIngester(user.getName(), user.getToken()));
        }
        else {
            this.securedUser = null;
            crawler.setIngester(new RegistryIngester());
        }
        crawler.setActionRepo(createCrawlerActions());
    }

    private CrawlerActionRepo createCrawlerActions() {
        CrawlerActionRepo repo = new CrawlerActionRepo();
        List<CrawlerAction> actions = new ArrayList<CrawlerAction>();
        actions.add(new RegistryUniquenessCheckerAction(crawler.getRegistryUrl(),
                crawler.getRegistryIngester()));
        actions.add(new ValidObjectTypeCheckerAction(objectTypes));
        actions.add(new LogMissingReqMetadataAction(crawler.getRequiredMetadata()));
        if(securedUser != null) {
            actions.add(new AssociationPublisherAction(crawler.getRegistryUrl(),
                    securedUser.getName(), securedUser.getToken()));
        }
        else {
            actions.add(new AssociationPublisherAction(crawler.getRegistryUrl()));
        }
        repo.loadActions(actions);
        return repo;
    }

    public void harvest(File directory, List<String> filePatterns) {
        crawler.crawl(directory, filePatterns);
    }

    public void harvestInventory(File inventory) throws InventoryReaderException {
        crawler.crawlInventory(inventory);
    }
}
