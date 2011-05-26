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
package gov.nasa.pds.harvest.crawler.actions;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;

import gov.nasa.jpl.oodt.cas.crawl.action.CrawlerAction;
import gov.nasa.jpl.oodt.cas.crawl.action.CrawlerActionPhases;
import gov.nasa.jpl.oodt.cas.crawl.structs.exceptions.CrawlerActionException;
import gov.nasa.jpl.oodt.cas.filemgr.structs.exceptions.CatalogException;
import gov.nasa.jpl.oodt.cas.metadata.Metadata;
import gov.nasa.pds.harvest.constants.Constants;
import gov.nasa.pds.harvest.crawler.stats.AssociationStats;
import gov.nasa.pds.harvest.inventory.ReferenceEntry;
import gov.nasa.pds.harvest.logging.ToolsLevel;
import gov.nasa.pds.harvest.logging.ToolsLogRecord;
import gov.nasa.pds.registry.client.RegistryClient;
import gov.nasa.pds.registry.exception.RegistryServiceException;
import gov.nasa.pds.registry.model.Association;
import gov.nasa.pds.registry.model.ExtrinsicObject;
import gov.nasa.pds.registry.model.PagedResponse;
import gov.nasa.pds.registry.model.Slot;
import gov.nasa.pds.registry.query.AssociationFilter;
import gov.nasa.pds.registry.query.ExtrinsicFilter;
import gov.nasa.pds.registry.query.RegistryQuery;

/**
 * Class to publish associations to the PDS Registry Service upon
 * successful ingestion of the product.
 *
 * @author mcayanan
 *
 */
public class AssociationPublisherAction extends CrawlerAction {
  /** Logger object. */
  private static Logger log = Logger.getLogger(
      AssociationPublisherAction.class.getName());

  /** The registry client. */
  private RegistryClient registryClient;

  /** The usernname of the authorized user. */
  private String user;

  /** The ID of the crawler action. */
  private final String ID = "AssociationPublisherAction";

  /** A description of the crawler action. */
  private final String DESCRIPTION = "Registers the product's associations.";

  private AssociationStats stats;

  /**
   * Constructor.
   *
   * @param registryUrl The URL to the registry service.
   * @throws RegistryClientException
   */
  public AssociationPublisherAction(String registryUrl) {
    this(registryUrl, null, null);
  }

  /**
   * Constructor.
   *
   * @param registryUrl The URL to the registry service.
   * @param user Name of the user authorized to publish the associations.
   * @param token A security token associated with the user.
   * @throws RegistryClientException
   */
  public AssociationPublisherAction(String registryUrl, String user,
      String password) {
    super();
    if ((user != null) && (password != null)) {
      this.user = user;
      this.registryClient = new RegistryClient(registryUrl, user,
          password);
    } else {
      this.registryClient = new RegistryClient(registryUrl);
    }

    String []phases = {CrawlerActionPhases.POST_INGEST_SUCCESS};
    setPhases(Arrays.asList(phases));
    setId(ID);
    setDescription(DESCRIPTION);
    stats = new AssociationStats();
  }

  /**
   *
   * @return the association statistics.
   */
  public AssociationStats getAssociationStats() {
    return stats;
  }

  /**
   * Publish the association.
   *
   * @param product The file containing the associations.
   * @param productMetadata The metadata associated with the given product.
   *
   * @return 'true' if the associations were registered successfully.
   *
   * @throws CrawlerActionException If an error occured while performing
   * this action.
   */
  @Override
  public boolean performAction(File product, Metadata productMetadata)
  throws CrawlerActionException {
    boolean passFlag = true;
    int numRegistered = 0;
    int numNotRegistered = 0;
    int numSkipped = 0;

    if (!productMetadata.containsKey(Constants.REFERENCES)) {
      return passFlag;
    }

    for (ReferenceEntry refEntry: (List<ReferenceEntry>)
        productMetadata.getAllMetadata(Constants.REFERENCES)) {
      Association association = createAssociation(product, productMetadata,
          refEntry);
      String targetReference = refEntry.getLogicalID();
      if (refEntry.hasVersion()) {
        targetReference += "::" + refEntry.getVersion();
      }
      if (!hasAssociation(association)) {
        try {
          String guid = registryClient.publishObject(association);
          log.log(new ToolsLogRecord(ToolsLevel.INGEST_ASSOC_SUCCESS,
              "Successfully registered association to " + targetReference,
                product));
          log.log(new ToolsLogRecord(ToolsLevel.INFO,
              "Association has the following GUID: " + guid, product));
          ++numRegistered;
        } catch (RegistryServiceException r) {
          log.log(new ToolsLogRecord(ToolsLevel.INGEST_ASSOC_FAIL,
              "Problem registering association to " + targetReference + ": "
              + r.getMessage(), product));
          ++numNotRegistered;
          passFlag = false;
        }
      } else {
        log.log(new ToolsLogRecord(ToolsLevel.INGEST_ASSOC_SKIP,
            "Association to " + targetReference + ", with \'"
            + association.getAssociationType()
            + "\' association type, already exists in the "
            + "registry.", product));
        ++numSkipped;
      }
    }
    stats.addNumRegistered(numRegistered);
    stats.addNumNotRegistered(numNotRegistered);
    stats.addNumSkipped(numSkipped);
    return passFlag;
  }

  /**
   * Determines if an association already exists in the registry.
   *
   * @param association The association.
   *
   * @return true if the association exists.
   * @throws RegistryServiceException
   */
  private boolean hasAssociation(Association association) {
    boolean result = false;
    AssociationFilter filter = new AssociationFilter.Builder()
    .sourceObject(association.getSourceObject())
    .targetObject(association.getTargetObject())
    .associationType(association.getAssociationType()).build();
    RegistryQuery<AssociationFilter> query = new RegistryQuery
    .Builder<AssociationFilter>().filter(filter).build();
    try {
      PagedResponse<Association> response = registryClient.getAssociations(
        query, 1, 10);
      if (response.getNumFound() != 0) {
        result = true;
      }
    } catch (RegistryServiceException r) {
      //Do nothing
    }
    return false;
  }

    /**
     * Creates an association.
     *
     * @param product The file containing the associations.
     * @param metadata The metadata associated with the given product.
     * @param refEntry A reference entry in the product label.
     *
     * @return An association for the given product.
     */
  private Association createAssociation(File product, Metadata metadata,
      ReferenceEntry refEntry) {
    Association association = new Association();
    Boolean verifiedFlag = false;

    association.setSourceObject(metadata.getMetadata(Constants.PRODUCT_GUID));
    association.setAssociationType(refEntry.getAssociationType());
    association.setObjectType(refEntry.getObjectType());
    //In both cases, check to see if the target product is in the registry.
    //If it isn't, then the target GUID will be the LID or LIDVID reference
    //of the target.
    if (refEntry.hasVersion()) {
      ExtrinsicObject target = getExtrinsic(refEntry.getLogicalID(),
          refEntry.getVersion());
      String lidvid = refEntry.getLogicalID() + "::" + refEntry.getVersion();
      if (target != null) {
        association.setTargetObject(target.getGuid());
        log.log(new ToolsLogRecord(ToolsLevel.INFO, "Found registered "
            + "product for LIDVID-based reference: " + lidvid, product));
        verifiedFlag = true;
      } else {
        association.setTargetObject(lidvid);
        log.log(new ToolsLogRecord(ToolsLevel.WARNING,
            "Product not found in registry for LIDVID-based reference: "
            + lidvid + ". LIDVID will be used as the target reference "
            + "for the association.", product));
        }
    } else {
      try {
        ExtrinsicObject target = registryClient.getLatestObject(
            refEntry.getLogicalID(), ExtrinsicObject.class);
        association.setTargetObject(target.getGuid());
        log.log(new ToolsLogRecord(ToolsLevel.INFO, "Found registered "
            + "product for LID-based reference: " + refEntry.getLogicalID(),
            product));
        verifiedFlag = true;
      } catch (RegistryServiceException r) {
        log.log(new ToolsLogRecord(ToolsLevel.WARNING,
            "Product not found in registry for the LID-based reference: "
            + refEntry.getLogicalID() + ". LID will be used as the target "
            + "reference for the association.", product));
        association.setTargetObject(refEntry.getLogicalID());
      }
    }
    Set<Slot> slots = new HashSet<Slot>();
    slots.add(new Slot("verified", Arrays.asList(
      new String[]{verifiedFlag.toString()}))
    );
    association.setSlots(slots);
    return association;
  }

  private ExtrinsicObject getExtrinsic(String lid, String version) {
    ExtrinsicObject result = null;
    ExtrinsicFilter filter = new ExtrinsicFilter.Builder().lid(lid)
    .build();
    RegistryQuery<ExtrinsicFilter> query = new RegistryQuery
    .Builder<ExtrinsicFilter>().filter(filter).build();
    try {
      PagedResponse<ExtrinsicObject> pr = registryClient.getExtrinsics(query,
        null, null);
      if (pr.getNumFound() != 0) {
        for (ExtrinsicObject extrinsic : pr.getResults()) {
          for (Slot slot : extrinsic.getSlots()) {
            if (slot.getName().equals(Constants.PRODUCT_VERSION)
                && slot.getValues().contains(version)) {
              result = extrinsic;
            }
          }
        }
      }
    } catch (RegistryServiceException r) {
      //Ignore. Nothing found.
    }
    return result;
  }
}
