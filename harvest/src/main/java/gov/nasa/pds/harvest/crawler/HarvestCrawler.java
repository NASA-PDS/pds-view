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
import java.util.logging.Level;
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
public class HarvestCrawler extends ProductCrawler {
    private static Logger log = Logger.getLogger(HarvestCrawler.class.getName());
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
        String[] reqMetadata = {PDSCoreMetKeys.PRODUCT_VERSION,
                                PDSCoreMetKeys.LOGICAL_ID,
                                PDSCoreMetKeys.OBJECT_TYPE};
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
     * Crawl a PDS Inventory file. Method will register the inventory file
     * first before attempting to register the product files it is pointing
     * to in the file.
     *
     * @param inventoryFile The PDS Inventory file.
     *
     * @throws InventoryReaderException
     */
    public void crawlInventory(File inventoryFile)
    throws InventoryReaderException {
        handleFile(inventoryFile);
        boolean isTable = false;
        try {
            XMLExtractor extractor = new XMLExtractor(inventoryFile);
            extractor.setDefaultNamespace(
                    metExtractorConfig.getNamespaceContext().
                    getDefaultNamepsace());
            extractor.setNamespaceContext(
                    metExtractorConfig.getNamespaceContext());
            if(!"".equals(extractor.getValueFromDoc("//Inventory"))) {
                isTable = true;
            }
            String isPrimary = extractor.getValueFromDoc(
                    "//*[ends-with(name(), 'Identification_Area')]" +
                    "/is_primary_collection");
            if((!"".equals(isPrimary)) && (!Boolean.parseBoolean(isPrimary))) {
                return;
            }
        } catch (Exception e) {
            throw new InventoryReaderException(e.getMessage());
        }
        InventoryReader reader = null;
        if(isTable) {
            reader = new InventoryTableReader(inventoryFile,
                    metExtractorConfig.getNamespaceContext());
        }
        else {
            reader = new InventoryXMLReader(inventoryFile,
                    metExtractorConfig.getNamespaceContext());
        }
        for(InventoryEntry entry = reader.getNext(); entry != null;) {
            handleFile(entry.getFile());
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
        String validTags[] = {"Product", "Context"};
        log.log(new ToolsLogRecord(ToolsLevel.INFO,
                "Begin processing.", product));
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
                        CoreXPaths.map.get(PDSCoreMetKeys.OBJECT_TYPE));
                if(metExtractorConfig.hasObjectType(objectType)) {
                    log.log(new ToolsLogRecord(ToolsLevel.NOTIFICATION,
                            Status.DISCOVERY, product));
                    return true;
                } else {
                    log.log(new ToolsLogRecord(ToolsLevel.SKIP,
                            "\'" + objectType + "\' is not an object type" +
                            " found in the policy file.", product));
                    return false;
                }
            } catch (XPathExpressionException e) {
                log.log(new ToolsLogRecord(ToolsLevel.SEVERE,
                        "Problem getting 'object_type': " + e.getMessage(),
                        product));
                return false;
            }
        }
    }
}
