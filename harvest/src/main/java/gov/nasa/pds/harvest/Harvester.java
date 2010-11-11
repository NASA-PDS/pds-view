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
import gov.nasa.pds.harvest.crawler.PDSProductCrawler;
import gov.nasa.pds.harvest.crawler.actions.AssociationPublisherAction;
import gov.nasa.pds.harvest.crawler.actions.RegistryUniquenessCheckerAction;
import gov.nasa.pds.harvest.crawler.actions.ValidateProductAction;
import gov.nasa.pds.harvest.crawler.metadata.extractor.PDSMetExtractorConfig;
import gov.nasa.pds.harvest.ingest.RegistryIngester;
import gov.nasa.pds.harvest.policy.Candidate;
import gov.nasa.pds.harvest.security.SecuredUser;
import gov.nasa.pds.harvest.target.Target;
import gov.nasa.pds.harvest.target.Type;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

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

    /** Product candidates defined in the policy file. */
    private Candidate candidates;

    /** An ingester for the PDS Registry Service. */
    private RegistryIngester ingester;

    /** Flag to enable/disable validation */
    private boolean doValidation;

    /**
     * Constructor.
     *
     * @param registryUrl The registry location.
     * @param candidates Class containing the products to look for and what
     * metadata to extract.
     *
     */
    public Harvester(String registryUrl, Candidate candidates) {
        this.registryUrl = registryUrl;
        this.candidates = candidates;
        this.securedUser = null;
        this.registryUrl = registryUrl;
        this.ingester = new RegistryIngester();
        this.doValidation = true;
    }

    /**
     * Sets the security for the Harvest tool.
     *
     * @param user An authorized user.
     */
    public void setSecuredUser(SecuredUser user) {
        this.securedUser = user;
        this.ingester = new RegistryIngester(user.getName(), user.getToken());
    }

    /**
     * Set the flag to perform validation while crawling. Set to true
     * by default.
     *
     * @param value A boolean value.
     */
    public void setDoValidation(boolean value) {
        this.doValidation = value;
    }

    /**
     * Get the default crawler actions.
     *
     * @return A list of default crawler actions.
     */
    private List<CrawlerAction> getDefaultCrawlerActions() {
        List<CrawlerAction> ca = new ArrayList<CrawlerAction>();
        ca.add(new RegistryUniquenessCheckerAction(registryUrl,
                this.ingester));
        if (securedUser != null) {
            ca.add(new AssociationPublisherAction(registryUrl,
                    securedUser.getName(), securedUser.getToken()));
        } else {
            ca.add(new AssociationPublisherAction(registryUrl));
        }
        if (doValidation) {
            ca.add(new ValidateProductAction());
        }
        return ca;
    }

    /**
     * Harvests the products in the given target.
     *
     * @param target A target file (directory, collection, or bundle).
     *
     * @throws ParserConfigurationException If an error occurred during
     * metadata extraction.
     * @throws MalformedURLException If an error occurred while setting
     * the registry URL to the crawler.
     */
    public void harvest(Target target) throws MalformedURLException,
    ParserConfigurationException {
        harvest(target, new ArrayList<String>());
    }

    /**
     * Harvests the products in the given file..
     *
     * @param target A target file (directory, collection, or bundle).
     * @param fileFilters Specify a list of file filters to search for
     * specific files in a target directory.
     *
     * @throws ParserConfigurationException If an error occurred during
     * metadata extraction.
     * @throws MalformedURLException If an error occurred while setting
     * the registry URL to the crawler.
     *
     */
    public void harvest(Target target, List<String> fileFilters)
    throws ParserConfigurationException, MalformedURLException {
        PDSProductCrawler crawler = null;
        PDSMetExtractorConfig config = new PDSMetExtractorConfig(candidates);
        if (Type.COLLECTION.equals(target.getType())) {
            crawler = new CollectionCrawler(config);
        } else if (Type.BUNDLE.equals(target.getType())) {
            crawler = new BundleCrawler(config);
        } else {
            //Default is to assume a directory.
            crawler = new PDSProductCrawler(config);
            if (!fileFilters.isEmpty()) {
                crawler.setFileFilter(fileFilters);
            }
        }
        crawler.setRegistryUrl(registryUrl);
        crawler.setIngester(ingester);
        crawler.addActions(getDefaultCrawlerActions());
        crawler.crawl(new File(target.getFilename()));
    }
}
