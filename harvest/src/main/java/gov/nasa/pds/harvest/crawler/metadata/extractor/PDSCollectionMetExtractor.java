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
package gov.nasa.pds.harvest.crawler.metadata.extractor;

import gov.nasa.jpl.oodt.cas.metadata.Metadata;
import gov.nasa.jpl.oodt.cas.metadata.exceptions.MetExtractionException;
import gov.nasa.pds.harvest.context.InventoryEntry;
import gov.nasa.pds.harvest.context.InventoryReaderException;
import gov.nasa.pds.harvest.context.InventoryTableReader;
import gov.nasa.pds.harvest.context.ReferenceEntry;
import gov.nasa.pds.harvest.crawler.metadata.CoreXPaths;
import gov.nasa.pds.harvest.logging.ToolsLevel;
import gov.nasa.pds.harvest.logging.ToolsLogRecord;
import gov.nasa.pds.harvest.util.XMLExtractor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.NodeList;

/**
 * Class to extract metadata from a PDS Collection file.
 *
 * @author mcayanan
 *
 */
public class PDSCollectionMetExtractor extends PDSMetExtractor {
    private static Logger log = Logger.getLogger(
            PDSCollectionMetExtractor.class.getName());

    public static String ASSOCIATION_TYPE_XPATH = "//*[starts-with("
        + "name(),'Inventory')]/reference_association_type";

    public PDSCollectionMetExtractor(PDSMetExtractorConfig config) {
        super(config);
    }

    /**
     * Extract the metadata
     *
     * @param product A PDS4 collection file
     * @return a class representation of the extracted metadata
     *
     */
    public Metadata extractMetadata(File product)
    throws MetExtractionException {
        XMLExtractor xmlExtractor = null;
        Metadata metadata = new Metadata();
        String objectType = "";
        String logicalID = "";
        String version = "";
        String title = "";
        String associationType = "";

        try {
            xmlExtractor = new XMLExtractor(product);
            xmlExtractor.setDefaultNamespace(
                    config.getNamespaceContext().getDefaultNamepsace());
            xmlExtractor.setNamespaceContext(config.getNamespaceContext());
        } catch (Exception e) {
            throw new MetExtractionException("Parse failure: "
                    + e.getMessage());
        }
        try {
            objectType = xmlExtractor.getValueFromDoc(
                    CoreXPaths.map.get(OBJECT_TYPE));
            logicalID = xmlExtractor.getValueFromDoc(
                    CoreXPaths.map.get(LOGICAL_ID));
            version = xmlExtractor.getValueFromDoc(
                    CoreXPaths.map.get(PRODUCT_VERSION));
            title = xmlExtractor.getValueFromDoc(CoreXPaths.map.get(TITLE));
            associationType =
                xmlExtractor.getValueFromDoc(ASSOCIATION_TYPE_XPATH);
        } catch (XPathExpressionException x) {
            //TODO: getMessage() doesn't always return a message
            throw new MetExtractionException(x.getMessage());
        }
        if(!"".equals(logicalID))
            metadata.addMetadata(LOGICAL_ID, logicalID);
        if(!"".equals(version))
            metadata.addMetadata(PRODUCT_VERSION, version);
        if(!"".equals(title))
            metadata.addMetadata(TITLE, title);
        if(!"".equals(objectType))
            metadata.addMetadata(OBJECT_TYPE, objectType);
        if((!"".equals(objectType)) && (config.hasObjectType(objectType))) {
            List<String> metXPaths = new ArrayList<String>();
            metXPaths.addAll(config.getMetXPaths(objectType));
            for(String xpath : metXPaths) {
                try {
                    NodeList list = xmlExtractor.getNodesFromDoc(xpath);
                    for(int i=0; i < list.getLength(); i++) {
                        metadata.addMetadata(list.item(i).getNodeName(),
                                xmlExtractor.getValuesFromDoc(xpath));
                    }
                } catch (XPathExpressionException xe) {
                    throw new MetExtractionException("Bad XPath Expression: "
                            + xpath);
                }
            }
        }
        List<ReferenceEntry> refEntries = new ArrayList<ReferenceEntry>();
        try {
            InventoryTableReader reader = new InventoryTableReader(product,
                    config.getNamespaceContext());
            for(InventoryEntry entry = reader.getNext(); entry != null;) {
                ReferenceEntry re = new ReferenceEntry();
                String identifier = entry.getLidvid();
                //Check for a LID or LIDVID
                if(identifier.indexOf("::") != -1) {
                    re.setLogicalID(identifier.split("::")[0]);
                    re.setVersion(identifier.split("::")[1]);
                } else {
                    re.setLogicalID(identifier);
                }
                re.setAssociationType(associationType);
                refEntries.add(re);
                entry = reader.getNext();
             }
        } catch(InventoryReaderException ire) {
            throw new MetExtractionException(ire.getMessage());
        }
        if(refEntries.size()== 0) {
            log.log(new ToolsLogRecord(ToolsLevel.INFO,
                    "No associations found.", product));
        } else {
            metadata.addMetadata(REFERENCES, refEntries);
        }

        return metadata;
    }
}
