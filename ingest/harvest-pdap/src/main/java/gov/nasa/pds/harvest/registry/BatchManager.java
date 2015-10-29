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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import gov.nasa.pds.harvest.pdap.constants.Constants;
import gov.nasa.pds.harvest.pdap.logging.ToolsLevel;
import gov.nasa.pds.harvest.pdap.logging.ToolsLogRecord;
import gov.nasa.pds.harvest.pdap.stats.HarvestPdapStats;
import gov.nasa.pds.registry.client.RegistryClient;
import gov.nasa.pds.registry.exception.RegistryServiceException;
import gov.nasa.pds.registry.model.ExtrinsicObject;
import gov.nasa.pds.registry.model.RegistryObject;
import gov.nasa.pds.registry.model.RegistryObjectList;

/**
 * Class that manages the batch ingestion of products into the Registry.
 *
 * @author mcayanan
 */
public class BatchManager {
  /** Logger object. */
  private static Logger log = Logger.getLogger(BatchManager.class.getName());

  /** Registry client. */
  private RegistryClient client;

  /** RegistryObjects to be versioned. */
  private LinkedHashMap<String, List<RegistryObject>> versionObjects;

  /** RegistryObjects to be published. */
  private LinkedHashMap<String, List<RegistryObject>> publishObjects;

  /**
   * Constructor.
   *
   * @param client A RegistryClient object.
   */
  public BatchManager(RegistryClient client) {
    this.client = client;
    versionObjects = new LinkedHashMap<String, List<RegistryObject>>();
    publishObjects = new LinkedHashMap<String, List<RegistryObject>>();
  }

  /**
   * Caches the given RegistryObject for future registration.
   *
   * @param object The RegistryObject to ingest.
   * @param datasetId The datasetId associated with the given RegistryObject.
   * @param version Flag to indicate whether to version the object.
   *
   */
  public void cache(RegistryObject object, String datasetId, boolean version) {
    if (version) {
      List<RegistryObject> objects = versionObjects.get(datasetId);
      if (objects == null) {
        objects = new ArrayList<RegistryObject>();
        objects.add(object);
        versionObjects.put(datasetId, objects);
      } else {
        objects.add(object);
        versionObjects.put(datasetId, objects);
      }
    } else {
      List<RegistryObject> objects = publishObjects.get(datasetId);
      if (objects == null) {
        objects = new ArrayList<RegistryObject>();
        objects.add(object);
        publishObjects.put(datasetId, objects);
      } else {
        objects.add(object);
        publishObjects.put(datasetId, objects);
      }
    }
  }

  /**
   * @return The size of the cache.
   */
  public int getCacheSize() {
    return versionObjects.size() + publishObjects.size();
  }

  public RegistryClient getRegistryClient() {
    return this.client;
  }

  /**
   * Ingests all products that have been cached.
   *
   */
  public void ingest() {
    ingest(publishObjects, false);
    ingest(versionObjects, true);
    publishObjects.clear();
    versionObjects.clear();
  }

  /**
   * Ingests the given registry objects.
   *
   * @param objects The registry objects to register.
   * @param version Set to true to version the objects. False otherwise.
   */
  private void ingest(LinkedHashMap<String, List<RegistryObject>> objects,
      boolean version) {
    List<RegistryObject> registryObjects = new ArrayList<RegistryObject>();
    for (Map.Entry<String, List<RegistryObject>> entry
        : objects.entrySet()) {
      registryObjects.addAll(entry.getValue());
    }
    if (registryObjects.size() == 1) {
      RegistryObject registryObject = registryObjects.get(0);
      String datasetId = objects.keySet().iterator().next();
      try {
        if (version) {
          client.versionObject(registryObjects.get(0));
        } else {
          client.publishObject(registryObjects.get(0));
        }
        logResults(objects, false);
      } catch (RegistryServiceException e) {
        log.log(new ToolsLogRecord(ToolsLevel.SEVERE, e.getMessage()));
        String lidvid = registryObject.getLid() + "::"
              + registryObject.getSlot(Constants.PRODUCT_VERSION)
              .getValues().get(0);
        log.log(new ToolsLogRecord(ToolsLevel.SEVERE,
              lidvid + " was not registered.", datasetId));
          if (Constants.DATA_SET_PRODUCT_CLASS.equals(
              registryObject.getObjectType())) {
            ++HarvestPdapStats.numDatasetsNotRegistered;
          } else {
            ++HarvestPdapStats.numResourcesNotRegistered;
          }

      }
    } else if (!registryObjects.isEmpty()) {
      RegistryObjectList list = new RegistryObjectList();
      list.setObjects(registryObjects);
      try {
        if (version) {
          client.versionObjects(list, true);
        } else {
          client.publishObjects(list);
        }
        logResults(objects, false);
      } catch (RegistryServiceException e) {
        log.log(new ToolsLogRecord(ToolsLevel.SEVERE, e.getMessage()));
        logResults(objects, true);
      }
    }
  }

  /**
   * Logs the given objects.
   *
   * @param registryObjects The registry objects.
   * @param verify Set to 'true' to verify that the given object does in
   * fact exist in the registry.
   */
  private void logResults(
      LinkedHashMap<String, List<RegistryObject>> registryObjects,
      boolean verify) {
    for (Map.Entry<String, List<RegistryObject>> entry
        : registryObjects.entrySet()) {
      for (RegistryObject object : entry.getValue()) {
        try {
          if (verify) {
            client.getObject(object.getGuid(), ExtrinsicObject.class);
          }
          String lidvid = object.getLid() + "::"
              + object.getSlot(Constants.PRODUCT_VERSION)
              .getValues().get(0);
          log.log(new ToolsLogRecord(ToolsLevel.SUCCESS,
              "Successfully registered product: " + lidvid,
               entry.getKey()));
          log.log(new ToolsLogRecord(ToolsLevel.INFO,
              "Product has the following GUID: " + object.getGuid(),
              entry.getKey()));
          if (Constants.DATA_SET_PRODUCT_CLASS.equals(
              object.getObjectType())) {
            ++HarvestPdapStats.numDatasetsRegistered;
          } else {
            ++HarvestPdapStats.numResourcesRegistered;
          }
        } catch (RegistryServiceException r) {
          String lidvid = object.getLid() + "::"
              + object.getSlot(Constants.PRODUCT_VERSION)
              .getValues().get(0);
          log.log(new ToolsLogRecord(ToolsLevel.SEVERE,
              lidvid + " was not registered.", entry.getKey()));
          if (Constants.DATA_SET_PRODUCT_CLASS.equals(
              object.getObjectType())) {
            ++HarvestPdapStats.numDatasetsNotRegistered;
          } else {
            ++HarvestPdapStats.numResourcesNotRegistered;
          }
          log.log(new ToolsLogRecord(ToolsLevel.SEVERE,
              r.getMessage(), entry.getKey()));
        }
      }
    }
  }
}
