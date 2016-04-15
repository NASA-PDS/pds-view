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

//OODT imports
import gov.nasa.jpl.oodt.cas.crawl.action.CrawlerAction;
import gov.nasa.jpl.oodt.cas.crawl.action.CrawlerActionRepo;
import gov.nasa.jpl.oodt.cas.crawl.config.ProductCrawlerBean;
import gov.nasa.jpl.oodt.cas.crawl.status.IngestStatus;
import gov.nasa.jpl.oodt.cas.metadata.Metadata;
import gov.nasa.pds.harvest.search.constants.Constants;
import gov.nasa.pds.harvest.search.doc.SearchDocGenerator;
import gov.nasa.pds.harvest.search.file.FileObject;
import gov.nasa.pds.harvest.search.file.FileSize;
import gov.nasa.pds.harvest.search.logging.ToolsLevel;
import gov.nasa.pds.harvest.search.logging.ToolsLogRecord;
import gov.nasa.pds.harvest.search.util.Utility;
import gov.nasa.pds.registry.model.ExtrinsicObject;
import gov.nasa.pds.registry.model.Slot;

//JDK imports
import java.io.File;
import java.io.FileFilter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBException;

import org.apache.commons.io.FilenameUtils;

/**
 * @author mcayanan
 *
 * <p>
 * An abstract base class for Product Crawling. This class provides methods to
 * communicate with the file manager and parse met files that show how to ingest
 * a particular Product into the File Manager.
 * </p>
 *
 */
public abstract class ProductCrawler extends ProductCrawlerBean {

    /* our log stream */
    protected static Logger LOG = Logger.getLogger(ProductCrawler.class
            .getName());

    // filter to only find directories when doing a listFiles
    protected FileFilter DIR_FILTER = new FileFilter() {
        public boolean accept(File file) {
            return file.isDirectory();
        }
    };

    // filter to only find product files, not met files
    protected FileFilter FILE_FILTER = new FileFilter() {
        public boolean accept(File file) {
            return file.isFile();
        }
    };

    private CrawlerActionRepo actionRepo;
    private List<IngestStatus> ingestStatus = new ArrayList<IngestStatus>();
    private SearchDocGenerator searchDocGenerator;
    
    public void crawl() {
        this.crawl(new File(this.getProductPath()));
    }

    public void crawl(File dirRoot) {
        // Load actions
        if (this.getApplicationContext() != null)
            (this.actionRepo = new CrawlerActionRepo())
                    .loadActionsFromBeanFactory(this.getApplicationContext(), this
                            .getActionIds());

        if (dirRoot == null) {
            throw new IllegalArgumentException("Crawler does not have a root "
                + "directory specified.");
        } else {
            if (!dirRoot.exists()) {
                throw new IllegalArgumentException("Crawler is pointing to a "
                    + "directory that does not exist: " + dirRoot);
            } else if (!dirRoot.canRead()) {
              throw new IllegalArgumentException("Crawler is pointing to a "
                  + "directory that is not readable: " + dirRoot);
            }
        }

        // start crawling
        Stack<File> stack = new Stack<File>();
        stack.push(dirRoot.isDirectory() ? dirRoot : dirRoot.getParentFile());
        while (!stack.isEmpty()) {
            File dir = (File) stack.pop();

            File[] productFiles = null;
            if (this.isCrawlForDirs()) {
                productFiles = dir.listFiles(DIR_FILTER);
            } else {
                productFiles = dir.listFiles(FILE_FILTER);
            }

            for (int j = 0; (productFiles != null) && (j < productFiles.length); j++) {
                try {
                    this.handleFile(productFiles[j]);
                } catch (Exception e) {
                    LOG.log(Level.WARNING, "Failed to process file : "
                            + e.getMessage());
                }
            }

            if (!this.isNoRecur()) {
                File[] subdirs = dir.listFiles(DIR_FILTER);
                if (subdirs != null)
                    for (int j = 0; j < subdirs.length; j++)
                        stack.push(subdirs[j]);
            }
        }
    }

    public List<IngestStatus> getIngestStatus() {
        return ingestStatus;
    }

    public void clearIngestStatus() {
        ingestStatus.clear();
    }

    private synchronized boolean containsRequiredMetadata(
            Metadata productMetadata) {
        for (int i = 0; i < this.getRequiredMetadata().size(); i++) {
            if (productMetadata.getMetadata((String) this.getRequiredMetadata()
                    .get(i)) == null) {
                LOG.log(Level.WARNING, "Missing required metadata field "
                        + this.getRequiredMetadata().get(i));
                return false;
            }
        }
        return true;
    }

    protected void addKnownMetadata(File product, Metadata productMetadata) {
        if (productMetadata.getMetadata(PRODUCT_NAME) == null)
			productMetadata.addMetadata(PRODUCT_NAME, product
					.getName());
        if (productMetadata.getMetadata(FILENAME) == null)
			productMetadata.addMetadata(FILENAME, product
					.getName());
        if (productMetadata.getMetadata(FILE_LOCATION) == null)
			productMetadata.addMetadata(FILE_LOCATION, product
					.getAbsoluteFile().getParentFile().getAbsolutePath());
    }

    protected void handleFile(File product) {
        final IngestStatus.Result ingestResult;
        final String message;
        if (this.passesPreconditions(product)) {
            Metadata productMetadata = new Metadata();
            productMetadata.addMetadata(this.getGlobalMetadata().getHashtable());
            productMetadata.addMetadata(this.getMetadataForProduct(product).getHashtable(), true);
            this.addKnownMetadata(product, productMetadata);

            boolean isRequiredMetadataPresent = this.containsRequiredMetadata(productMetadata);
            boolean isPreIngestActionsComplete = this.performPreIngestActions(product, productMetadata);

            if (this.isSkipIngest()) {
                ingestResult = IngestStatus.Result.SKIPPED;
                message = "Crawler ingest turned OFF.";
                LOG.log(Level.INFO, "Skipping ingest of product: ["
                    + product.getAbsolutePath() + "]");
            } else {
                if (isRequiredMetadataPresent
                        && isPreIngestActionsComplete
                        && generateDoc(product, productMetadata)) {
                    ingestResult = IngestStatus.Result.SUCCESS;
                    message = "Ingest was successful.";
                    this
                            .performPostIngestOnSuccessActions(product,
                                    productMetadata);
                } else {
                    ingestResult = IngestStatus.Result.FAILURE;
                    if (!isRequiredMetadataPresent) {
                        message = "Missing required metadata.";
                    } else if (!isPreIngestActionsComplete) {
                        message = "PreIngest actions failed to complete.";
                    } else {
                        message = "Failed to ingest product.";
                    }
                    this.performPostIngestOnFailActions(product, productMetadata);
                }
            }
        } else {
            ingestResult = IngestStatus.Result.PRECONDS_FAILED;
            message = "Failed to pass preconditions";
        }
        this.ingestStatus.add(new IngestStatus(product, ingestResult,
            message));
    }

    /**
     * Create the Product object.
     *
     * @param metadata A class representation of the metdata.
     *
     * @return A Product object.
     */
  protected ExtrinsicObject createProduct(Metadata metadata, File prodFile) {
    ExtrinsicObject product = new ExtrinsicObject();
    Set<Slot> slots = new HashSet<Slot>();
    Set metSet = metadata.getHashtable().entrySet();
    for (Iterator i = metSet.iterator(); i.hasNext();) {
      Map.Entry entry = (Map.Entry) i.next();
      String key = entry.getKey().toString();
      if (key.equals(Constants.REFERENCES)
          || key.equals(Constants.INCLUDE_PATHS) 
          || key.equals("file_ref")) {
        continue;
      }
      if (key.equals(Constants.LOGICAL_ID)) {
        product.setLid(metadata.getMetadata(Constants.LOGICAL_ID));
      } else if (key.equals(Constants.PRODUCT_VERSION)) {
        slots.add(new Slot(Constants.PRODUCT_VERSION,
            Arrays.asList(new String[]{
                metadata.getMetadata(Constants.PRODUCT_VERSION)}
            )));
      } else if (key.equals(Constants.OBJECT_TYPE)) {
        product.setObjectType(metadata.getMetadata(
             Constants.OBJECT_TYPE));
      } else if (key.equals(Constants.TITLE)) {
        product.setName(metadata.getMetadata(Constants.TITLE));
      } else if (key.equals(Constants.SLOT_METADATA)) {
        slots.addAll(metadata.getAllMetadata(Constants.SLOT_METADATA));
      } else {
        LOG.log(new ToolsLogRecord(ToolsLevel.WARNING,
            "Creating unexpected slot: " + key, prodFile));
        List<String> values = new ArrayList<String>();
        if (metadata.isMultiValued(key)) {
          values.addAll(metadata.getAllMetadata(key));
        } else {
          values.add(metadata.getMetadata(key));
        }
        slots.add(new Slot(key, values));
      }
      product.setSlots(slots);
    }

    if (LOG.getParent().getHandlers()[0].getLevel().intValue()
        <= ToolsLevel.DEBUG.intValue()) {
      try {
      LOG.log(new ToolsLogRecord(ToolsLevel.DEBUG,
        "Extrinsic object contents: \n" + Utility.toXML(product)));
      } catch (JAXBException je) {
        LOG.log(new ToolsLogRecord(ToolsLevel.SEVERE, je.getMessage()));
      }
    }
    return product;
  }     
  
  protected boolean generateDoc(File product, Metadata productMetadata) {
    try {
      ExtrinsicObject extrinsic = createProduct(productMetadata, product);
      searchDocGenerator.generate(extrinsic, productMetadata);
      String lidvid = extrinsic.getLid() + "::" + extrinsic.getSlot(
          Constants.PRODUCT_VERSION).getValues().get(0);
      LOG.log(new ToolsLogRecord(Level.INFO, 
          "Successfully generated document file for " + lidvid + ".", product));
    } catch (Exception e) {
       LOG.log(new ToolsLogRecord(Level.SEVERE, 
           "Exception generating document: " + e.getMessage(), product));
      return false;
    }
    return true;
  }

    protected abstract boolean passesPreconditions(File product);

    protected abstract Metadata getMetadataForProduct(File product);

    private boolean performPreIngestActions(File product,
            Metadata productMetadata) {
        if (this.actionRepo != null)
            return this.performProductCrawlerActions(this.actionRepo
                    .getPreIngestActions(), product, productMetadata);
        else
            return true;
    }

    private boolean performPostIngestOnSuccessActions(File product,
            Metadata productMetadata) {
        if (this.actionRepo != null)
            return this.performProductCrawlerActions(this.actionRepo
                    .getPostIngestOnSuccessActions(), product, productMetadata);
        else
            return true;
    }

    private boolean performPostIngestOnFailActions(File product,
            Metadata productMetadata) {
        if (this.actionRepo != null)
            return this.performProductCrawlerActions(this.actionRepo
                    .getPostIngestOnFailActions(), product, productMetadata);
        else
            return true;
    }

    private boolean performProductCrawlerActions(List<CrawlerAction> actions,
            File product, Metadata productMetadata) {
        boolean allSucceeded = true;
        for (CrawlerAction action : actions) {
            try {
                if (!action.performAction(product,
                        productMetadata))
                    throw new Exception("Action (id = "
                            + action.getId() + " : description = "
                            + action.getDescription()
                            + ") returned false");
            } catch (Exception e) {
                allSucceeded = false;
            }
        }
        return allSucceeded;
    }

    public void setActionRepo(CrawlerActionRepo repo) {
    	this.actionRepo = repo;
    }
    
    public SearchDocGenerator getSearchDocGenerator() {
      return this.searchDocGenerator;
    }
    
    public void setSearchDocGenerator(SearchDocGenerator generator) {
      this.searchDocGenerator = generator;
    }
}
