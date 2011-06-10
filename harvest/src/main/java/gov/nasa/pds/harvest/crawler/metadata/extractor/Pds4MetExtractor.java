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
// $Id: PDSMetExtractor.java 8360 2011-01-11 19:26:28Z mcayanan $
package gov.nasa.pds.harvest.crawler.metadata.extractor;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.xpath.XPathExpressionException;

import net.sf.saxon.tinytree.TinyElementImpl;

import gov.nasa.jpl.oodt.cas.metadata.MetExtractor;
import gov.nasa.jpl.oodt.cas.metadata.MetExtractorConfig;
import gov.nasa.jpl.oodt.cas.metadata.Metadata;
import gov.nasa.jpl.oodt.cas.metadata.exceptions.MetExtractionException;
import gov.nasa.pds.harvest.constants.Constants;
import gov.nasa.pds.harvest.inventory.ReferenceEntry;
import gov.nasa.pds.harvest.logging.ToolsLevel;
import gov.nasa.pds.harvest.logging.ToolsLogRecord;
import gov.nasa.pds.harvest.util.XMLExtractor;

/**
 * Class to extract metadata from a PDS4 XML file.
 *
 * @author mcayanan
 *
 */
public class Pds4MetExtractor implements MetExtractor {
  /** Logger object. */
  private static Logger log = Logger.getLogger(
      Pds4MetExtractor.class.getName());

  /** A metadata extraction configuration. */
  protected Pds4MetExtractorConfig config;

  /** An XMLExtractor to get the metadata. */
  protected XMLExtractor extractor;

  /**
   * Default constructor.
   *
   * @param config The configuration that contains what metadata
   * and what object types to extract.
   */
  public Pds4MetExtractor(Pds4MetExtractorConfig config) {
    this.config = config;
    extractor = new XMLExtractor();
  }

  /**
   * Extract the metadata
   *
   * @param product A PDS4 xml file
   * @return a class representation of the extracted metadata
   *
   * @throws MetExtractionException If an error occured while performing
   * metadata extraction.
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
      objectType = extractor.getValueFromDoc(Constants.coreXpathsMap.get(
          Constants.OBJECT_TYPE));
      logicalID = extractor.getValueFromDoc(Constants.coreXpathsMap.get(
          Constants.LOGICAL_ID));
      version = extractor.getValueFromDoc(Constants.coreXpathsMap.get(
          Constants.PRODUCT_VERSION));
      title = extractor.getValueFromDoc(Constants.coreXpathsMap.get(
          Constants.TITLE));
      references = extractor.getNodesFromDoc(Constants.coreXpathsMap.get(
          Constants.REFERENCES));
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
      log.log(new ToolsLogRecord(ToolsLevel.INFO, "No associations found.",
          product));
    }
    if ((!"".equals(objectType)) && (config.hasObjectType(objectType))) {
      metadata.addMetadata(extractMetadata(config.getMetXPaths(objectType))
          .getHashtable());
    }
    try {
      List<ReferenceEntry> lidVidEntries = new ArrayList<ReferenceEntry>();
      // Search for LID-based associations and register them as slots
      for (ReferenceEntry entry : getReferences(references, product)) {
        if (!entry.hasVersion()) {
          metadata.addMetadata(entry.getAssociationType(),
              entry.getLogicalID());
          log.log(new ToolsLogRecord(ToolsLevel.INFO, "Setting "
              + "LID-based association, \'" + entry.getLogicalID()
              + "\', under slot name \'" + entry.getAssociationType()
              + "\'.", product));
        } else {
          lidVidEntries.add(entry);
        }
      }
      if (!lidVidEntries.isEmpty()) {
        metadata.addMetadata(Constants.REFERENCES, lidVidEntries);
      }
    } catch (Exception e) {
      throw new MetExtractionException(e.getMessage());
    }
    return metadata;
  }

  /**
   * Extracts metadata
   *
   * @param xPaths A list of xpath expressions.
   *
   * @return A metadata object containing the extracted metadata.
   *
   * @throws MetExtractionException If a bad xPath expression was
   *  encountered.
   */
  protected Metadata extractMetadata(List<String> xPaths)
  throws MetExtractionException {
    Metadata metadata = new Metadata();
    for (String xpath : xPaths) {
      try {
        List<TinyElementImpl> list = extractor.getNodesFromDoc(xpath);
        for (int i = 0; i < list.size(); i++) {
          metadata.addMetadata(list.get(i).getDisplayName(),
              extractor.getValuesFromDoc(xpath));
        }
      } catch (Exception xe) {
        throw new MetExtractionException("Bad XPath Expression: " + xpath);
      }
    }
    return metadata;
  }

  /**
   * Extracts the metadata found in an association entry.
   *
   * @param references A list of association entries.
   * @param product The product.
   *
   * @return A list of ReferenceEntry objects, which holds the association
   * metadata.
   *
   * @throws XPathExpressionException If there was an invalid XPath
   * expression.
   * @throws MetExtractionException
   */
  protected List<ReferenceEntry> getReferences(
      List<TinyElementImpl> references, File product)
  throws XPathExpressionException, MetExtractionException {
    List<ReferenceEntry> refEntries = new ArrayList<ReferenceEntry>();
    String name = "";
    String value = "";
    for (TinyElementImpl reference : references) {
      List<TinyElementImpl> children = extractor.getNodesFromItem("*",
          reference);
      ReferenceEntry re = new ReferenceEntry();
      re.setFile(product);
      for (TinyElementImpl child : children) {
        re.setLineNumber(child.getLineNumber());
        name = child.getLocalPart();
        value = child.getStringValue();
        if (name.equals("lidvid_reference")) {
          try {
            re.setLogicalID(value.split("::")[0]);
            re.setVersion(value.split("::")[1]);
          } catch (ArrayIndexOutOfBoundsException ae) {
            log.log(new ToolsLogRecord(ToolsLevel.SEVERE, "Expected "
                + "a LID-VID reference, but found this: " + value,
                product.toString(),
            child.getLineNumber()));
          }
        } else if (name.equals("lid_reference")) {
          re.setLogicalID(value);
        } else if (name.equals("reference_association_type")) {
          re.setAssociationType(value);
        } else if (name.equals("referenced_object_type")) {
          re.setObjectType(value);
        }
      }
      refEntries.add(re);
    }
    return refEntries;
  }

  /**
   * Extract the metadata.
   *
   * @param product A PDS4 xml file.
   * @return a class representation of the extracted metadata.
   *
   */
  public Metadata extractMetadata(String product)
  throws MetExtractionException {
    return extractMetadata(new File(product));
  }

  /**
   * Extract the metadata.
   *
   * @param product A PDS4 xml file.
   * @return a class representation of the extracted metadata.
   *
   */
  public Metadata extractMetadata(URL product)
  throws MetExtractionException {
    return extractMetadata(product.toExternalForm());
  }

  /**
   * No need to be implemented.
   *
   */
  public Metadata extractMetadata(File product, File configFile)
  throws MetExtractionException {
    // No need to implement at this point
    return null;
  }

  /**
   * No need to be implemented.
   *
   */
  public Metadata extractMetadata(File product, String configFile)
  throws MetExtractionException {
    // No need to implement at this point
    return null;
  }

  /**
   * No need to be implemented.
   *
   */
  public Metadata extractMetadata(File product, MetExtractorConfig config)
  throws MetExtractionException {
    setConfigFile(config);
    return extractMetadata(product);
  }

  /**
   * No need to be implemented.
   *
   */
  public Metadata extractMetadata(URL product, MetExtractorConfig config)
  throws MetExtractionException {
    setConfigFile(config);
    return extractMetadata(product);
  }

  /**
   * No need to be implemented.
   *
   */
  public void setConfigFile(File configFile)
  throws MetExtractionException {
    // No need to implement at this point
  }

  /**
   * No need to be implemented.
   *
   */
  public void setConfigFile(String configFile)
  throws MetExtractionException {
    // No need to implement at this point
  }

  public void setConfigFile(MetExtractorConfig config) {
    this.config = (Pds4MetExtractorConfig) config;
  }
}
