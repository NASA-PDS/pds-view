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
package gov.nasa.pds.harvest.ingest;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.ws.rs.core.Response.Status;

import gov.nasa.jpl.oodt.cas.filemgr.ingest.Ingester;
import gov.nasa.jpl.oodt.cas.filemgr.structs.exceptions.CatalogException;
import gov.nasa.jpl.oodt.cas.filemgr.structs.exceptions.IngestException;
import gov.nasa.jpl.oodt.cas.metadata.MetExtractor;
import gov.nasa.jpl.oodt.cas.metadata.Metadata;
import gov.nasa.pds.harvest.constants.Constants;
import gov.nasa.pds.harvest.logging.ToolsLevel;
import gov.nasa.pds.harvest.logging.ToolsLogRecord;
import gov.nasa.pds.registry.client.RegistryClient;
import gov.nasa.pds.registry.exception.RegistryServiceException;
import gov.nasa.pds.registry.model.ExtrinsicObject;
import gov.nasa.pds.registry.model.PagedResponse;
import gov.nasa.pds.registry.model.Slot;
import gov.nasa.pds.registry.query.ExtrinsicFilter;
import gov.nasa.pds.registry.query.RegistryQuery;

/**
 * Class that supports ingestion of PDS4 products into the PDS registry.
 *
 * @author mcayanan
 *
 */
public class RegistryIngester implements Ingester {
  /** Logger object. */
  private static Logger log = Logger.getLogger(
      RegistryIngester.class.getName());

  /** Password of the authorized user. */
  private String password;

  /** Username of the authorized user. */
  private String user;


  /**
   * Default constructor.
   *
   */
  public RegistryIngester() {
    this(null, null);
  }

  /**
    * Constructor.
    *
    * @param user An authorized user.
    * @param token The security token that allows the authorized user to
    * ingest products into the registry.
    */
  public RegistryIngester(String user, String password) {
    this.password = password;
    this.user = user;
  }

  /**
   * Method not used at this time.
   *
   */
  public boolean hasProduct(URL registry, File prodFile)
  throws CatalogException {
      // No use for this method for now
    return false;
  }

  /**
   * Determines whether a product is already in the registry.
   *
   * @param registry The URL to the registry service.
   * @param productID The PDS4 logical identifier.
   *
   * @return 'true' if the logical identifier was found in the registry.
   * 'false' otherwise.
   *
   * @throws CatalogException exception ignored.
   */
  public boolean hasProduct(URL registry, String productID)
  throws CatalogException {
    try {
      RegistryClient client = new RegistryClient(registry.toString(), user,
          password);
      ExtrinsicObject extrinsic = client.getLatestObject(productID,
          ExtrinsicObject.class);
      return true;
    } catch (RegistryServiceException re) {
      // Do nothing
    }
    return false;
  }

  /**
   * Determines whether a version of a product is already in the registry.
   *
   * @param registry The URL to the registry service.
   * @param productID The PDS4 logical identifier.
   * @param productVersion The version of the product.
   *
   * @return 'true' if the logical identifier and version was found in the
   * registry.
   *
   * @throws CatalogException If an error occurred while talking to the
   * ingester.
   */
  public boolean hasProduct(URL registry, String productID,
          String productVersion) throws CatalogException {
    RegistryClient client = new RegistryClient(registry.toString(), user,
        password);
    ExtrinsicFilter filter = new ExtrinsicFilter.Builder().lid(productID)
    .build();
    RegistryQuery<ExtrinsicFilter> query = new RegistryQuery
    .Builder<ExtrinsicFilter>().filter(filter).build();
    try {
      PagedResponse<ExtrinsicObject> pr = client.getExtrinsics(query, null,
          null);
      if (pr.getNumFound() == 0) {
        return false;
      } else {
        for (ExtrinsicObject extrinsic : pr.getResults()) {
          for (Slot slot : extrinsic.getSlots()) {
            if (slot.getName().equals(Constants.PRODUCT_VERSION)
                && slot.getValues().contains(productVersion)) {
              return true;
            }
          }
        }
      }
    } catch (RegistryServiceException r) {
      throw new CatalogException(r.getMessage());
    }
    return false;
  }

  /**
   * Ingests the product into the registry.
   *
   * @param registry The URL to the registry service.
   * @param prodFile The PDS4 product file.
   * @param met The metadata to register.
   *
   * @return The URL of the registered product.
   * @throws IngestException If an error occurred while ingesting the
   * product.
   */
  public String ingest(URL registry, File prodFile, Metadata met)
  throws IngestException {
      RegistryClient client = new RegistryClient(registry.toString(), user,
              password);
      ExtrinsicObject product = createProduct(met);
      String guid = "";
      try {
        if (hasProduct(registry, product.getLid())) {
          guid = client.versionObject(product);
        } else {
          guid = client.publishObject(product);
        }
      } catch (RegistryServiceException r) {
        log.log(new ToolsLogRecord(ToolsLevel.INGEST_FAIL,
            r.getMessage(), prodFile));
        throw new IngestException(r.getMessage());
      } catch (CatalogException c) {
        //hasProduct throws this exception, but we can ignore it
      }
      String lid = met.getMetadata(Constants.LOGICAL_ID);
      String vid = met.getMetadata(Constants.PRODUCT_VERSION);
      String lidvid = lid + "::" + vid;
      log.log(new ToolsLogRecord(ToolsLevel.INGEST_SUCCESS,
          "Successfully registered product: " + lidvid, prodFile));
      log.log(new ToolsLogRecord(ToolsLevel.INFO,
          "Product has the following GUID: " + guid, prodFile));
      met.addMetadata(Constants.PRODUCT_GUID, guid);
      return guid;
  }

    /**
     * Create the Product object.
     *
     * @param metadata A class representation of the metdata.
     *
     * @return A Product object.
     */
  private ExtrinsicObject createProduct(Metadata metadata) {
    ExtrinsicObject product = new ExtrinsicObject();
    Set<Slot> slots = new HashSet<Slot>();
    Set metSet = metadata.getHashtable().entrySet();
    for (Iterator i = metSet.iterator(); i.hasNext();) {
      Map.Entry entry = (Map.Entry) i.next();
      String key = entry.getKey().toString();
      if (key.equals(Constants.REFERENCES)) {
        continue;
      }
      if (key.equals(Constants.LOGICAL_ID)) {
        product.setLid(metadata.getMetadata(Constants.LOGICAL_ID));
      } else if (key.equals(Constants.OBJECT_TYPE)) {
         product.setObjectType(metadata.getMetadata(
             Constants.OBJECT_TYPE));
      } else if (key.equals(Constants.TITLE)) {
         product.setName(metadata.getMetadata(Constants.TITLE));
      } else {
         List<String> values = new ArrayList<String>();
         if (metadata.isMultiValued(key)) {
           values.addAll(metadata.getAllMetadata(key));
         } else {
           values.add(metadata.getMetadata(key));
         }
           slots.add(new Slot(key, values));
      }
    }
    product.setSlots(slots);

    return product;
  }

  /**
   * Method not implemented at this time.
   *
   */
  public String ingest(URL fmUrl, File prodFile, MetExtractor extractor,
          File metConfFile) throws IngestException {
    //No need for this method at this time
    return null;
  }

  /**
   * Method not implemented at this time.
   *
   */
  public void ingest(URL fmUrl, List<String> prodFiles,
          MetExtractor extractor, File metConfFile)
          throws IngestException {
      //No need for this method at this time
  }
}
