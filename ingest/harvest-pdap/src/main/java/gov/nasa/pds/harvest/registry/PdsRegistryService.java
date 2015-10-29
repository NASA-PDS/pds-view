// Copyright 2006-2015, by the California Institute of Technology.
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
package gov.nasa.pds.harvest.registry;

import org.apache.log4j.Logger;

import gov.nasa.pds.registry.client.RegistryClient;
import gov.nasa.pds.registry.client.SecurityContext;
import gov.nasa.pds.registry.exception.RegistryClientException;
import gov.nasa.pds.registry.exception.RegistryServiceException;
import gov.nasa.pds.registry.model.ExtrinsicObject;
import gov.nasa.pds.registry.model.PagedResponse;
import gov.nasa.pds.registry.model.RegistryPackage;
import gov.nasa.pds.registry.model.Slot;
import gov.nasa.pds.registry.query.ExtrinsicFilter;
import gov.nasa.pds.registry.query.RegistryQuery;

public class PdsRegistryService {
  private static Logger log = Logger.getLogger(
      PdsRegistryService.class.getName());

  private RegistryClient client;

  private int batchMode;

  private BatchManager batchManager;

  public PdsRegistryService(String url)
  throws RegistryClientException {
    this (url, null, null, null);
  }

  public PdsRegistryService(String url, SecurityContext securityContext,
      String user, String password)
  throws RegistryClientException {
    client = new RegistryClient(url, securityContext, user, password);
    this.batchManager = new BatchManager(client);
    this.batchMode = 0;
  }

  public boolean hasProduct(String lid) {
    try {
      ExtrinsicObject extrinsic = client.getLatestObject(lid,
        ExtrinsicObject.class);
      return true;
    } catch (RegistryServiceException rse) {
      // Do nothing
    }
    return false;
  }

  public boolean hasProduct(String lid, String vid)
  throws RegistryServiceException {
    ExtrinsicFilter filter = new ExtrinsicFilter.Builder().lid(lid).build();
    RegistryQuery<ExtrinsicFilter> query = new RegistryQuery
    .Builder<ExtrinsicFilter>().filter(filter).build();
    PagedResponse<ExtrinsicObject> pr = client.getExtrinsics(query, null,
        null);
    if (pr.getNumFound() == 0) {
      return false;
    } else {
      for (ExtrinsicObject extrinsic : pr.getResults()) {
        for (Slot slot : extrinsic.getSlots()) {
          if (slot.getName().equals("version_id")
              && slot.getValues().contains(vid)) {
            return true;
          }
        }
      }
    }
    return false;
  }

  public void ingest(ExtrinsicObject extrinsic, String datasetId) {
    boolean versionObject = false;
    if (hasProduct(extrinsic.getLid())) {
      versionObject = true;
    }
    batchManager.cache(extrinsic, datasetId, versionObject);
    if (batchManager.getCacheSize() >= batchMode) {
      batchManager.ingest();
    }
  }

  public String createPackage(RegistryPackage registryPackage)
  throws RegistryServiceException {
    String guid = client.publishObject(registryPackage);
    client.setRegistrationPackageId(guid);
    return guid;
  }

  /**
   * Sets the number of concurrent registrations to make
   * during batch mode.
   *
   * @param value An integer value.
   */
  public void setBatchMode(int value) {
    this.batchMode = value;
  }

  /**
   * @return The Batch Manager object.
   */
  public BatchManager getBatchManager() {
    return this.batchManager;
  }
}
