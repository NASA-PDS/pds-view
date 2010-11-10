package gov.nasa.pds.harvest.crawler;

import java.io.File;
import java.util.logging.Logger;

import gov.nasa.jpl.oodt.cas.crawl.action.CrawlerActionRepo;
import gov.nasa.pds.harvest.constants.Constants;
import gov.nasa.pds.harvest.crawler.metadata.extractor.PDSMetExtractorConfig;
import gov.nasa.pds.harvest.inventory.InventoryEntry;
import gov.nasa.pds.harvest.inventory.InventoryReader;
import gov.nasa.pds.harvest.inventory.InventoryTableReader;
import gov.nasa.pds.harvest.logging.ToolsLevel;
import gov.nasa.pds.harvest.logging.ToolsLogRecord;
import gov.nasa.pds.harvest.util.XMLExtractor;

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
    public CollectionCrawler(PDSMetExtractorConfig extractorConfig) {
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
        //Load crawlerActions first before crawling
        CrawlerActionRepo repo = new CrawlerActionRepo();
        repo.loadActions(getActions());
        setActionRepo(repo);
        handleFile(collection);
        try {
            XMLExtractor extractor = new XMLExtractor();
            extractor.parse(collection);
            String isPrimary = extractor.getValueFromDoc(
                    Constants.IS_PRIMARY_COLLECTION_XPATH);
            if ((!"".equals(isPrimary))
                    && (!Boolean.parseBoolean(isPrimary))) {
                return;
            }
            InventoryReader reader = new InventoryTableReader(collection);
            for (InventoryEntry entry = reader.getNext(); entry != null;) {
                handleFile(entry.getFile());
                entry = reader.getNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.log(new ToolsLogRecord(ToolsLevel.SEVERE, e.getMessage(),
                    collection));
        }
    }
}
