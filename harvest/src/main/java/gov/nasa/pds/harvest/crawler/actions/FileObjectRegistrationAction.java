// Copyright 2006-2011, by the California Institute of Technology.
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

package gov.nasa.pds.harvest.crawler.actions;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.commons.io.FilenameUtils;

import gov.nasa.jpl.oodt.cas.crawl.action.CrawlerAction;
import gov.nasa.jpl.oodt.cas.crawl.action.CrawlerActionPhases;
import gov.nasa.jpl.oodt.cas.crawl.structs.exceptions.CrawlerActionException;
import gov.nasa.jpl.oodt.cas.filemgr.structs.exceptions.CatalogException;
import gov.nasa.jpl.oodt.cas.metadata.Metadata;
import gov.nasa.pds.harvest.constants.Constants;
import gov.nasa.pds.harvest.crawler.status.Status;
import gov.nasa.pds.harvest.file.FileObject;
import gov.nasa.pds.harvest.ingest.RegistryIngester;
import gov.nasa.pds.harvest.inventory.ReferenceEntry;
import gov.nasa.pds.harvest.logging.ToolsLevel;
import gov.nasa.pds.harvest.logging.ToolsLogRecord;
import gov.nasa.pds.registry.exception.RegistryClientException;
import gov.nasa.pds.registry.exception.RegistryServiceException;
import gov.nasa.pds.registry.model.ExtrinsicObject;
import gov.nasa.pds.registry.model.Slot;


/**
 * Class to register file objects.
 *
 * @author mcayanan
 *
 */
public class FileObjectRegistrationAction extends CrawlerAction {
  /** Logger object. */
  private static Logger log = Logger.getLogger(
          AssociationCheckerAction.class.getName());

  /** Crawler action ID. */
  private final String ID = "FileObjectRegistrationAction";

  /** Crawler action description. */
  private final String DESCRIPTION = "Registers file objects associated with "
    + "a product.";

  /** Registry URL. */
  private String registryUrl;

  /** The registry client. */
  private RegistryIngester registryIngester;

  /**
   * Constructor.
   *
   * @param registryUrl url of the registry.
   * @param ingester The RegistryIngester.
   * @throws RegistryClientException
   */
  public FileObjectRegistrationAction(String registryUrl,
      RegistryIngester ingester)
  throws RegistryClientException {
      super();
      String []phases = {CrawlerActionPhases.POST_INGEST_SUCCESS};
      setPhases(Arrays.asList(phases));
      setId(ID);
      setDescription(DESCRIPTION);
      this.registryUrl = registryUrl;
      this.registryIngester = ingester;
  }

  /**
   * Perform the action to register the file products.
   *
   * @param product The file.
   * @param metadata The metadata associated with the file.
   *
   * @return Always returns true.
   *
   * @throws CrawlerActionException If an error occurred while processing
   * the file objects.
   */
  @Override
  public boolean performAction(File product, Metadata metadata)
      throws CrawlerActionException {
    for (FileObject fileObject
        : (List<FileObject>) metadata.getAllMetadata(Constants.FILE_OBJECTS)) {
      ExtrinsicObject fileProduct = createFileProduct(fileObject, metadata);
      try {
        String guid = "";
        String lid = fileProduct.getLid();
        String vid = metadata.getMetadata(Constants.PRODUCT_VERSION);
        String lidvid = lid + "::" + vid;
        if (!registryIngester.hasProduct(new URL(registryUrl), lid, vid)) {
          guid = registryIngester.ingest(new URL(registryUrl), fileProduct);
          log.log(new ToolsLogRecord(ToolsLevel.INGEST_SUCCESS,
            "Successfully registered file product: " + lidvid, product));
          log.log(new ToolsLogRecord(ToolsLevel.INFO,
            "File product has the following GUID: " + guid, product));
          // Create a reference entry of the file association and add that
          // back to the list of reference entries to be processed later.
          ReferenceEntry refEntry = new ReferenceEntry();
          refEntry.setLogicalID(fileProduct.getLid());
          refEntry.setVersion(metadata.getMetadata(Constants.PRODUCT_VERSION));
          refEntry.setGuid(guid);
          refEntry.setAssociationType("has_File");
          List<ReferenceEntry> refEntries = metadata.getAllMetadata(
            Constants.REFERENCES);
          refEntries.add(refEntry);
          metadata.replaceMetadata(Constants.REFERENCES, refEntries);
        } else {
          log.log(new ToolsLogRecord(ToolsLevel.WARNING,
              "File product already exists: " + lidvid, product));
          log.log(new ToolsLogRecord(ToolsLevel.NOTIFICATION,
              Status.PRODUCT_EXISTS, product));
        }
      } catch (RegistryServiceException ex) {
        log.log(new ToolsLogRecord(ToolsLevel.INGEST_FAIL,
            "Problems registering file product \'" + fileObject.getName()
            + "\': " + ex.getMessage(), product));
      } catch (MalformedURLException mue) {
        log.log(new ToolsLogRecord(ToolsLevel.SEVERE,
            "Malformed registry url: " + mue.getMessage(), product));
        throw new CrawlerActionException(mue.getMessage());
      } catch (RegistryClientException rce) {
        log.log(new ToolsLogRecord(ToolsLevel.SEVERE,
            "Error while initializing RegistryClient: " + rce.getMessage(),
            product));
        throw new CrawlerActionException(rce.getMessage());
      } catch (CatalogException ce) {
        // Ignore
      }
    }
    return true;
  }

  /**
   * Creates the object needed for the registry.
   *
   * @param fileObject The file object to be registered.
   * @param metadata The metadata associated with the file.
   *
   * @return An ExtrinsicObject.
   */
  private ExtrinsicObject createFileProduct(FileObject fileObject,
      Metadata metadata) {
    ExtrinsicObject product = new ExtrinsicObject();
    product.setLid(metadata.getMetadata(
        Constants.LOGICAL_ID) + ":" + fileObject.getName());
    product.setName(FilenameUtils.getBaseName(fileObject.getName()));
    product.setObjectType("Product_File_Repository");

    Set<Slot> slots = new HashSet<Slot>();

    slots.add(new Slot(Constants.FILE_NAME, Arrays.asList(
        new String[]{fileObject.getName()})));
    slots.add(new Slot(Constants.FILE_LOCATION, Arrays.asList(
        new String[]{fileObject.getLocation()})));
    slots.add(new Slot(Constants.FILE_SIZE, Arrays.asList(
        new String[]{Long.toString(fileObject.getSize())})));
    slots.add(new Slot(Constants.MD5_CHECKSUM, Arrays.asList(
        new String[]{fileObject.getChecksum()})));
    slots.add(new Slot(Constants.CREATION_DATE_TIME, Arrays.asList(
        new String[]{fileObject.getCreationDateTime()})));
    Set metSet = metadata.getHashtable().entrySet();
    for (Iterator i = metSet.iterator(); i.hasNext();) {
      Map.Entry entry = (Map.Entry) i.next();
      String key = entry.getKey().toString();
      if (key.equals("dd_version_id")
          || key.equals("std_ref_version_id")
          || key.equals(Constants.PRODUCT_VERSION)) {
        slots.add(new Slot(key, Arrays.asList(
            new String[]{metadata.getMetadata(key)})));
      }
    }
    product.setSlots(slots);
    return product;
  }
}
