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
import gov.nasa.pds.harvest.context.InventoryFileReaderException;
import gov.nasa.pds.harvest.context.InventoryXMLFileReader;
import gov.nasa.pds.harvest.crawler.metadata.PDSCoreMetKeys;
import gov.nasa.pds.harvest.crawler.metadata.extractor.PDSMetExtractor;
import gov.nasa.pds.harvest.crawler.metadata.extractor.PDSMetExtractorConfig;
import gov.nasa.pds.harvest.ingest.RegistryIngester;
import gov.nasa.pds.harvest.logging.ToolsLogRecord;
import gov.nasa.pds.harvest.util.XMLExtractor;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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

	public HarvestCrawler() {
		this.xmlExtractor = null;
		this.metExtractorConfig = null;
		String[] reqMetadata = {PDSCoreMetKeys.PRODUCT_VERSION, 
				                PDSCoreMetKeys.LOGICAL_ID, 
				                PDSCoreMetKeys.OBJECT_TYPE};
		setRequiredMetadata(Arrays.asList(reqMetadata));
		FILE_FILTER = new WildcardOSFilter("*");
	}
	
	public void setMetExtractorConfig(PDSMetExtractorConfig extractorConfig) {
		this.metExtractorConfig = extractorConfig;
	}
	
	public void setRegistryUrl(String url) throws MalformedURLException {
		setFilemgrUrl(url);
	}
	
	public String getRegistryUrl() {
		return getFilemgrUrl();
	}
	
	public RegistryIngester getRegistryIngester() {
		return (RegistryIngester) getIngester();
	}
	
	public void crawl(File dir, List<String> fileFilters) {
		if((fileFilters != null) && !(fileFilters.isEmpty())) {
			FILE_FILTER = new WildcardOSFilter(fileFilters);
		}
		crawl(dir);
	}
	
	public void crawlInventory(File inventoryFile) throws InventoryFileReaderException {
		handleFile(inventoryFile);
		InventoryXMLFileReader reader = new InventoryXMLFileReader(inventoryFile);
		for(;!reader.isEOF();) {
			InventoryEntry entry = reader.getNext();
			handleFile(entry.getFile());
		}
	}
	
	@Override
	protected void addKnownMetadata(File product, Metadata productMetadata) {
		//The parent class adds FILENAME, FILE_LOCATION, and PRODUCT_NAME
		//to the metadata. Not needed at the moment
	}
	
	@Override
	protected Metadata getMetadataForProduct(File product) {
		PDSMetExtractor metExtractor = new PDSMetExtractor(metExtractorConfig);
		try {
			return metExtractor.extractMetadata(product);
		} catch (MetExtractionException m) {
			log.log(new ToolsLogRecord(Level.SEVERE, 
					"Error while gathering metadata: " + m.getMessage(),
					product));
			return new Metadata();
		}
	}
	@Override
	protected boolean passesPreconditions(File product) {
		String validTags[] = {"Product", "Context"};
		try {
			xmlExtractor = new XMLExtractor(product);
		} catch(SAXException se) {
			SAXParseException spe = (SAXParseException) se.getException();
			log.log(new ToolsLogRecord(Level.SEVERE, spe.getMessage(), 
					product.toString(), spe.getLineNumber()));
			return false;
		} catch (Exception e) {
			log.log(new ToolsLogRecord(Level.SEVERE, 
					"Parse failure: " + e.getMessage(), product));
			return false;
		}
		String root = xmlExtractor.getDocNode().getNodeName();
		for(String tag : Arrays.asList(validTags)) {
			if(root.startsWith(tag)) {
				return true;
			}
		}
		log.log(new ToolsLogRecord(Level.WARNING, "File is not a product label",
				product));
		return false;			
	}
}
