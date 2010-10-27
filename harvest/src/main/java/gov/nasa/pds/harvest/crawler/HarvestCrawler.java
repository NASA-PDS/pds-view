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
package gov.nasa.pds.harvest.crawler;

import gov.nasa.jpl.oodt.cas.crawl.ProductCrawler;
import gov.nasa.jpl.oodt.cas.metadata.Metadata;
import gov.nasa.jpl.oodt.cas.metadata.exceptions.MetExtractionException;
import gov.nasa.pds.harvest.context.InventoryEntry;
import gov.nasa.pds.harvest.context.InventoryReader;
import gov.nasa.pds.harvest.context.InventoryReaderException;
import gov.nasa.pds.harvest.context.InventoryTableReader;
import gov.nasa.pds.harvest.context.InventoryXMLReader;
import gov.nasa.pds.harvest.crawler.metadata.CoreXPaths;
import gov.nasa.pds.harvest.crawler.metadata.PDSCoreMetKeys;
import gov.nasa.pds.harvest.crawler.metadata.extractor.PDSMetExtractor;
import gov.nasa.pds.harvest.crawler.metadata.extractor.PDSMetExtractorConfig;
import gov.nasa.pds.harvest.crawler.status.Status;
import gov.nasa.pds.harvest.ingest.RegistryIngester;
import gov.nasa.pds.harvest.logging.ToolsLevel;
import gov.nasa.pds.harvest.logging.ToolsLogRecord;
import gov.nasa.pds.harvest.util.XMLExtractor;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Class that extends the Cas-Crawler to crawl a directory or
 * PDS inventory file and register products to the PDS Registry
 * Service.
 *
 * @author mcayanan
 *
 */
public class HarvestCrawler extends ProductCrawler implements PDSCoreMetKeys {
    private static Logger log = Logger.getLogger(
            HarvestCrawler.class.getName());
    private static final String IS_PRIMARY_XPATH =
      "//*[starts-with(name(), 'Identification_Area')]/is_primary_collection";
    private PDSMetExtractorConfig metExtractorConfig;
    private XMLExtractor xmlExtractor;

    /**
     * Constructor
     *
     * @param extractorConfig A configuration class that tells the crawler
     * what data product types to look for and what metadata to extract.
     */
    public HarvestCrawler(PDSMetExtractorConfig extractorConfig) {
        this.xmlExtractor = null;
        this.metExtractorConfig = extractorConfig;
        String[] reqMetadata = {PRODUCT_VERSION,
                                LOGICAL_ID,
                                OBJECT_TYPE};
        setRequiredMetadata(Arrays.asList(reqMetadata));
        FILE_FILTER = new WildcardOSFilter("*");
    }

    /**
     * Sets the registry location.
     *
     * @param url A url of the registry location.
     * @throws MalformedURLException
     */
    public void setRegistryUrl(String url) throws MalformedURLException {
        setFilemgrUrl(url);
    }

    /**
     * Gets the registry location.
     *
     * @return A url of the registry location.
     */
    public String getRegistryUrl() {
        return getFilemgrUrl();
    }

    /**
     * Gets the registry ingester.
     *
     * @return A registry ingester object.
     */
    public RegistryIngester getRegistryIngester() {
        return (RegistryIngester) getIngester();
    }

    /**
     * Crawls a directory.
     *
     * @param dir A directory
     * @param fileFilters A list of filters to allow the crawler
     * to touch only specific files.
     */
    public void crawl(File dir, List<String> fileFilters) {
        if((fileFilters != null) && !(fileFilters.isEmpty())) {
            FILE_FILTER = new WildcardOSFilter(fileFilters);
        }
        crawl(dir);
    }

    /**
     * Crawl a PDS4 collection file. Method will register the collection
     * first before attempting to register the product files it is pointing
     * to.
     *
     * @param collection The PDS4 Collection file.
     *
     * @throws InventoryReaderException
     */
    public void crawlCollection(File collection)
    throws InventoryReaderException {
        handleFile(collection);
        try {
            XMLExtractor extractor = new XMLExtractor(collection);
            extractor.setDefaultNamespace(
                    metExtractorConfig.getNamespaceContext().
                    getDefaultNamepsace());
            extractor.setNamespaceContext(
                    metExtractorConfig.getNamespaceContext());
            String isPrimary = extractor.getValueFromDoc(IS_PRIMARY_XPATH);
            if((!"".equals(isPrimary)) && (!Boolean.parseBoolean(isPrimary))) {
                return;
            }
        } catch (Exception e) {
            throw new InventoryReaderException(e.getMessage());
        }
        InventoryReader reader = new InventoryTableReader(collection,
                    metExtractorConfig.getNamespaceContext());
        for(InventoryEntry entry = reader.getNext(); entry != null;) {
            handleFile(entry.getFile());
            entry = reader.getNext();
        }
    }

    /**
     * Crawl a PDS4 bundle file. The bundle will be registered first, then
     * the method will proceed to crawling the collection file it points to.
     *
     * @param bundle The PDS4 bundle file.
     *
     * @throws InventoryReaderException
     */
    public void crawlBundle(File bundle) throws InventoryReaderException {
        handleFile(bundle);
        InventoryReader reader = new InventoryXMLReader(bundle,
                metExtractorConfig.getNamespaceContext());
        for(InventoryEntry entry = reader.getNext(); entry != null;) {
            crawlCollection(entry.getFile());
            entry = reader.getNext();
        }
    }

    @Override
    protected void addKnownMetadata(File product, Metadata productMetadata) {
        //The parent class adds FILENAME, FILE_LOCATION, and PRODUCT_NAME
        //to the metadata. Not needed at the moment
    }

    /**
     * Extracts metadata from the given product.
     *
     * @param product A PDS file.
     *
     * @return A Metadata object, which holds metadata from the product.
     *
     */
    @Override
    protected Metadata getMetadataForProduct(File product) {
        PDSMetExtractor metExtractor =
            new PDSMetExtractor(metExtractorConfig);
        try {
            return metExtractor.extractMetadata(product);
        } catch (MetExtractionException m) {
            log.log(new ToolsLogRecord(ToolsLevel.SEVERE,
                    "Error while gathering metadata: " + m.getMessage(),
                    product));
            return null;
        }
    }
    /**
     * Determines whether the supplied file passes the necessary
     * pre-conditions for the file to be registered.
     *
     * @param product A file.
     *
     * @return true if the file passes.
     */
    @Override
    protected boolean passesPreconditions(File product) {
        log.log(new ToolsLogRecord(ToolsLevel.INFO, "Begin processing.",
                product));
        boolean passFlag = true;
        try {
            xmlExtractor = new XMLExtractor(product);
            xmlExtractor.setDefaultNamespace(
                    metExtractorConfig.getNamespaceContext().
                    getDefaultNamepsace());
            xmlExtractor.setNamespaceContext(
                    metExtractorConfig.getNamespaceContext());
        } catch(SAXException se) {
            SAXParseException spe = (SAXParseException) se.getException();
            log.log(new ToolsLogRecord(ToolsLevel.SEVERE, spe.getMessage(),
                    product.toString(), spe.getLineNumber()));
            passFlag = false;
        } catch (Exception e) {
            log.log(new ToolsLogRecord(ToolsLevel.SEVERE,
                    "Parse failure: " + e.getMessage(), product));
            passFlag = false;
        }
        if(passFlag == false) {
            log.log(new ToolsLogRecord(ToolsLevel.NOTIFICATION,
                    Status.BADFILE, product));
            return false;
        }
        else  {
            try {
                String objectType = xmlExtractor.getValueFromDoc(
                        CoreXPaths.map.get(OBJECT_TYPE));
                if("".equals(objectType)) {
                    log.log(new ToolsLogRecord(ToolsLevel.SKIP,
                           "No " + OBJECT_TYPE + " element found.", product));
                    passFlag = false;
                } else if(metExtractorConfig.hasObjectType(objectType)) {
                    log.log(new ToolsLogRecord(ToolsLevel.NOTIFICATION,
                            Status.DISCOVERY, product));
                    passFlag = true;
                } else {
                    log.log(new ToolsLogRecord(ToolsLevel.SKIP,
                            "\'" + objectType + "\' is not an object type" +
                            " found in the policy file.", product));
                    passFlag = false;
                }
            } catch (XPathExpressionException e) {
                log.log(new ToolsLogRecord(ToolsLevel.SEVERE,
                        "Problem getting '" + OBJECT_TYPE + "': "
                        + e.getMessage(), product));
                log.log(new ToolsLogRecord(ToolsLevel.NOTIFICATION,
                        Status.BADFILE, product));
                return false;
            }
        }
        return passFlag;
    }
}
