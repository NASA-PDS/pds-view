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
// $Id: PDSBundleMetExtractor.java 8360 2011-01-11 19:26:28Z mcayanan $
package gov.nasa.pds.harvest.crawler.metadata.extractor;

import gov.nasa.jpl.oodt.cas.metadata.Metadata;
import gov.nasa.jpl.oodt.cas.metadata.exceptions.MetExtractionException;
import gov.nasa.pds.harvest.constants.Constants;
import gov.nasa.pds.harvest.inventory.ReferenceEntry;
import gov.nasa.pds.harvest.logging.ToolsLevel;
import gov.nasa.pds.harvest.logging.ToolsLogRecord;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

import net.sf.saxon.tinytree.TinyElementImpl;

/**
 * Class that extracts metadata from a PDS Bundle file.
 *
 * @author mcayanan
 *
 */
public class BundleMetExtractor extends Pds4MetExtractor {
    /** Logger object. */
    private static Logger log = Logger.getLogger(
            BundleMetExtractor.class.getName());

    /**
     * Constructor.
     *
     * @param config A configuration to do metadata extraction.
     */
    public BundleMetExtractor(Pds4MetExtractorConfig config) {
        super(config);
    }

    /**
     * Extract the metadata.
     *
     * @param product A PDS4 collection file
     * @return a class representation of the extracted metadata
     *
     */
    public Metadata extractMetadata(File product)
    throws MetExtractionException {
        Metadata metadata = new Metadata();
        String objectType = "";
        String logicalID = "";
        String version = "";
        String title = "";
        List<TinyElementImpl> references = null;
        try {
            extractor.parse(product);
        } catch (Exception e) {
            throw new MetExtractionException("Parse failure: "
                    + e.getMessage());
        }
        try {
            objectType = extractor.getValueFromDoc(
                    Constants.coreXpathsMap.get(Constants.OBJECT_TYPE));
            logicalID = extractor.getValueFromDoc(
                    Constants.coreXpathsMap.get(Constants.LOGICAL_ID));
            version = extractor.getValueFromDoc(
                    Constants.coreXpathsMap.get(Constants.PRODUCT_VERSION));
            title = extractor.getValueFromDoc(
                    Constants.coreXpathsMap.get(Constants.TITLE));
            references = extractor.getNodesFromDoc(
                    Constants.coreXpathsMap.get(Constants.REFERENCES));
        } catch (Exception x) {
            //TODO: getMessage() doesn't always return a message
            throw new MetExtractionException(x.getMessage());
        }
        if (!"".equals(logicalID)) {
            metadata.addMetadata(Constants.LOGICAL_ID, logicalID);
        }
        if (!"".equals(version)) {
            metadata.addMetadata(Constants.PRODUCT_VERSION, version);
        }
        if (!"".equals(title)) {
            metadata.addMetadata(Constants.TITLE, title);
        }
        if (!"".equals(objectType)) {
            metadata.addMetadata(Constants.OBJECT_TYPE, objectType);
        }
        if (references.size() == 0) {
            log.log(new ToolsLogRecord(ToolsLevel.INFO,
                    "No associations found.", product));
        }
        if (!"".equals(objectType)) {
            String xpath = "";
            try {
                xpath = Constants.IDENTIFICATION_AREA_XPATH + "/*";
                List<TinyElementImpl> list = extractor.getNodesFromDoc(xpath);
                for (int i = 0; i < list.size(); i++) {
                    TinyElementImpl node = list.get(i);
                    if (!metadata.containsKey(node.getLocalPart())) {
                        metadata.addMetadata(node.getDisplayName(),
                            node.getStringValue());
                    }
                }
            } catch (Exception xe) {
                throw new MetExtractionException("Bad XPath Expression: "
                        + xpath);
            }
        }
        try {
            List<ReferenceEntry> refEntries = getReferences(references,
              product);
            metadata.addMetadata(Constants.REFERENCES, refEntries);
        } catch (Exception e) {
            throw new MetExtractionException(e.getMessage());
        }
        return metadata;
    }
}
