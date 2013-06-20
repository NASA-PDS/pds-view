// Copyright 2006-2012, by the California Institute of Technology.
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
package gov.nasa.pds.harvest.pdap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBException;

import org.apache.oodt.cas.metadata.Metadata;

import uk.ac.starlink.table.RowSequence;
import uk.ac.starlink.table.StarTable;

import gov.nasa.pds.harvest.pdap.catalog.StatementFinder;
import gov.nasa.pds.harvest.pdap.constants.Constants;
import gov.nasa.pds.harvest.pdap.logging.ToolsLevel;
import gov.nasa.pds.harvest.pdap.logging.ToolsLogRecord;
import gov.nasa.pds.harvest.pdap.policy.DynamicMetadata;
import gov.nasa.pds.harvest.pdap.policy.Element;
import gov.nasa.pds.harvest.pdap.policy.PdapService;
import gov.nasa.pds.harvest.pdap.policy.PdapServices;
import gov.nasa.pds.harvest.pdap.policy.ProductMetadata;
import gov.nasa.pds.harvest.pdap.policy.ResourceMetadata;
import gov.nasa.pds.harvest.pdap.policy.StaticMetadata;
import gov.nasa.pds.harvest.pdap.registry.client.PdapRegistryClient;
import gov.nasa.pds.harvest.pdap.registry.client.PdapRegistryClientException;
import gov.nasa.pds.harvest.pdap.registry.client.PsaRegistryClient;
import gov.nasa.pds.harvest.pdap.stats.HarvestPdapStats;
import gov.nasa.pds.harvest.pdap.util.Utility;
import gov.nasa.pds.harvest.registry.PdsRegistryService;
import gov.nasa.pds.registry.exception.RegistryServiceException;
import gov.nasa.pds.registry.model.ExtrinsicObject;
import gov.nasa.pds.registry.model.Slot;
import gov.nasa.pds.tools.dict.parser.DictIDFactory;
import gov.nasa.pds.tools.label.AttributeStatement;
import gov.nasa.pds.tools.label.Label;
import gov.nasa.pds.tools.label.ObjectStatement;
import gov.nasa.pds.tools.label.PointerStatement;
import gov.nasa.pds.tools.label.Scalar;
import gov.nasa.pds.tools.label.Sequence;
import gov.nasa.pds.tools.label.Value;

/**
 * Front-end class to the Harvest-PDAP Tool.
 *
 * @author mcayanan
 *
 */
public class HarvesterPdap {
  /** logger object. */
  private static Logger log = Logger.getLogger(
      HarvesterPdap.class.getName());

  /** PDS Registry Service. */
  private PdsRegistryService registryService;

  /** Metadata to register with every product registration. */
  private StaticMetadata staticMetadata;

  /** Metadata to extract for candidate products. */
  private DynamicMetadata dynamicMetadata;

  /** Resource metadata for every product registration. */
  private ResourceMetadata resourceMetadata;

  /** Contains the metadata to extract from every candidate product. */
  private List<String> elementsToGet;

  /** PDAP Client interface. */
  private PdapRegistryClient pdapClient;

  /**
   * Constructor.
   *
   * @param registryService Registry Service.
   * @param metadata Product metadata.
   * @param resourceMet Resource metadata.
   */
  public HarvesterPdap(PdsRegistryService registryService,
      ProductMetadata metadata, ResourceMetadata resourceMet) {
    this.pdapClient = null;
    this.registryService = registryService;
    this.staticMetadata = metadata.getStaticMetadata();
    this.dynamicMetadata = metadata.getDynamicMetadata();
    this.resourceMetadata = resourceMet;
    this.elementsToGet = new ArrayList<String>();
    for (Element element : dynamicMetadata.getElement()) {
      this.elementsToGet.add(element.getName());
    }
  }

  /**
   * Harvest products from a list of PDAP Services.
   *
   * @param pdapServices List of PDAP services.
   */
  public void harvest(PdapServices pdapServices) {
    for (PdapService pdapService : pdapServices.getPdapService()) {
      harvest(pdapService);
    }
  }

  /**
   * Harvest products from a PDAP service.
   *
   * @param pdapService A PDAP Service.
   */
  public void harvest(PdapService pdapService) {
    log.log(new ToolsLogRecord(ToolsLevel.INFO, "Connecting to PDAP Service: "
        + pdapService.getUrl()));
    if ("esa".equalsIgnoreCase(pdapService.getAgency().toString())) {
      pdapClient = new PsaRegistryClient(pdapService.getUrl());
    }
    try {
      List<StarTable> tables = pdapClient.getAllDataSets();
      for (StarTable table : tables) {
        RowSequence rseq = table.getRowSequence();
        while (rseq.next()) {
          Metadata datasetMet = new Metadata();
          for (int i = 0; i < table.getColumnCount(); i++) {
            String columnName = table.getColumnInfo(i).getUCD();
            if (rseq.getCell(i) != null) {
              String cell = rseq.getCell(i).toString();
              log.log(new ToolsLogRecord(ToolsLevel.DEBUG, "Extracted Key: "
                  + columnName));
              log.log(new ToolsLogRecord(ToolsLevel.DEBUG, "Extracted Value: "
                  + cell));
              if (columnName.equals("TARGET_NAME")
                  || columnName.equals("INSTRUMENT_NAME")
                  || columnName.equals("INSTRUMENT_ID")) {
                String[] tokens = (cell + ",").split(",");
                for (String token : Arrays.asList(tokens)) {
                  datasetMet.addMetadata(columnName, token.trim());
                }
              } else {
                datasetMet.addMetadata(columnName, cell);
              }
            }
          }
          String datasetId = datasetMet.getMetadata("DATA_SET_ID");
          if (datasetId == null) {
            throw new Exception("DATA_SET_ID not found.");
          }
          log.log(new ToolsLogRecord(ToolsLevel.INFO, "Processing dataset.",
              datasetId));
          ++HarvestPdapStats.numDatasetsProcessed;
          String lid = createLid(Constants.LID_PREFIX, datasetId);
          String lidvid = lid + "::1.0";
          if (registryService.hasProduct(lid, "1.0")) {
            log.log(new ToolsLogRecord(ToolsLevel.SEVERE,
                "Product already exists: " + lidvid, datasetId));
            ++HarvestPdapStats.numDatasetsNotRegistered;
          } else {
            try {
              String guid = registerDataset(lid, datasetMet);
              log.log(new ToolsLogRecord(ToolsLevel.SUCCESS,
                  "Successfully ingested product: " + lidvid, datasetId));
              log.log(new ToolsLogRecord(ToolsLevel.INFO, "Product guid is "
                  + guid, datasetId));
              ++HarvestPdapStats.numDatasetsRegistered;
            } catch (RegistryServiceException rse) {
              log.log(new ToolsLogRecord(ToolsLevel.SEVERE,
                  "Exception occurred while attempting to register product '"
                  + lidvid + "'", datasetId));
              ++HarvestPdapStats.numDatasetsNotRegistered;
              continue;
            }
            try {
              String resourceLid = createLid(Constants.RESOURCE_PREFIX, datasetId);
              String resourceLidvid = resourceLid + "::1.0";
              if (registryService.hasProduct(resourceLid, "1.0")) {
                log.log(new ToolsLogRecord(ToolsLevel.SEVERE,
                    "Product already exists: " + resourceLidvid, datasetId));
                ++HarvestPdapStats.numResourcesNotRegistered;
              } else {
                String guid = registerResource(resourceLid, datasetMet);
                log.log(new ToolsLogRecord(ToolsLevel.SUCCESS,
                    "Successfully ingested product: " + resourceLidvid,
                    datasetId));
                log.log(new ToolsLogRecord(ToolsLevel.INFO, "Product guid is "
                    + guid, datasetId));
                ++HarvestPdapStats.numResourcesRegistered;
              }
            } catch (PdapRegistryClientException pe) {
              log.log(new ToolsLogRecord(ToolsLevel.SEVERE,
                  "Exception occurred while getting resource link: "
                  + pe.getMessage(), datasetId));
              ++HarvestPdapStats.numResourcesNotRegistered;
              continue;
            } catch (RegistryServiceException rse) {
              log.log(new ToolsLogRecord(ToolsLevel.SEVERE,
                  "Exception occurred while attempting to register resource: "
                  + rse.getMessage(), datasetId));
              ++HarvestPdapStats.numResourcesNotRegistered;
              continue;
            }
          }
        }
      }
    } catch (PdapRegistryClientException pe) {
      log.log(new ToolsLogRecord(ToolsLevel.SEVERE,
          "Exception while crawling PDAP Target '" + pdapService.getUrl()
          + "': " + pe.getMessage()));
    } catch (IOException io) {
      log.log(new ToolsLogRecord(ToolsLevel.SEVERE,
          "IOException while crawling PDAP Target '" + pdapService.getUrl()
          + "': " + io.getMessage()));
    } catch (RegistryServiceException rse) {
      log.log(new ToolsLogRecord(ToolsLevel.SEVERE,
          "Registry Service Exception while crawling PDAP Target '"
          + pdapService.getUrl() + "': " + rse.getMessage()));
      ++HarvestPdapStats.numDatasetsNotRegistered;
    } catch (Exception e) {
      log.log(new ToolsLogRecord(ToolsLevel.SEVERE, e.getMessage()));
    }
  }

  /**
   * Creates the logical identifier. Any URI reserved characters found in
   * the dataset id will be substituted with dash characters when forming
   * the lid.
   *
   * @param prefix A prefix for the lid.
   *
   * @param datasetId The dataset id.
   *
   * @return The logical identifier.
   */
  private String createLid(String prefix, String datasetId) {
    String lid = prefix + "." + datasetId;
    lid = lid.toLowerCase();
    String conformingLid = lid.replaceAll(Constants.URN_ILLEGAL_CHARACTERS, "-");
    if (!conformingLid.equals(lid)) {
      log.log(new ToolsLogRecord(ToolsLevel.WARNING, "Dataset ID contains "
          + "URN reserved and/or excluded characters. Replacing with '-' characters "
          + "to create the logical identifier: " + conformingLid,
          datasetId));
    }
    return conformingLid;
  }

  /**
   * Register the dataset.
   *
   * @param lid Logical identifier of the dataset to be registered.
   * @param datasetMet The metadata to register.
   * @return The GUID of the registered product.
   * @throws RegistryServiceException If an error occurred while ingesting
   * the product to the PDS Registry.
   */
  private String registerDataset(String lid, Metadata datasetMet)
  throws RegistryServiceException {
    ExtrinsicObject extrinsic = createExtrinsic(lid, datasetMet);
    if (log.getParent().getHandlers()[0].getLevel().intValue()
        <= ToolsLevel.DEBUG.intValue()) {
      try {
      log.log(new ToolsLogRecord(ToolsLevel.DEBUG,
        "Extrinsic object contents: \n" + Utility.toXML(extrinsic)));
      } catch (JAXBException je) {
        log.log(new ToolsLogRecord(ToolsLevel.SEVERE, je.getMessage()));
      }
    }
    return registryService.ingest(extrinsic);
  }

  /**
   * Create the extrinsic object to be used to ingest into the PDS Registry.
   *
   * @param lid The logical identifier of the product to be registered.
   *
   * @param datasetMet Metadata associated with the product.
   *
   * @return An Extrinsic object.
   */
  private ExtrinsicObject createExtrinsic(String lid, Metadata datasetMet) {
    String datasetId = datasetMet.getMetadata("DATA_SET_ID");
    Metadata extrinsicMet = new Metadata();
    List<String> elementsToGetCopy = new ArrayList<String>();
    elementsToGetCopy.addAll(elementsToGet);
    for (String element : elementsToGet) {
      if (datasetMet.containsKey(element)) {
        if (datasetMet.isMultiValued(element)) {
          extrinsicMet.addMetadata(element, datasetMet.getAllMetadata(element));
        } else {
          extrinsicMet.addMetadata(element, datasetMet.getMetadata(element));
        }
        elementsToGetCopy.remove(element);
      }
    }
    if (!elementsToGetCopy.isEmpty()) {
      log.log(new ToolsLogRecord(ToolsLevel.INFO, "Additional metadata "
          + "needed. Getting dataset catalog file.", datasetId));
      Label catalog = null;
      String catalogFilename = "";
      try {
        log.log(new ToolsLogRecord(ToolsLevel.INFO, "Retrieving VOLDESC.CAT to "
            + "look up the data set catalog file name.", datasetId));
        Label voldesc = pdapClient.getVoldescFile(datasetId);
        catalogFilename = getCatalogFile(voldesc, datasetId);
        if (catalogFilename != null) {
          log.log(new ToolsLogRecord(ToolsLevel.INFO, "Retrieving catalog "
              + "file: " + catalogFilename, datasetId));
          catalog = pdapClient.getCatalogFile(datasetId, catalogFilename);
          for (String leftoverElement : elementsToGetCopy) {
            List<AttributeStatement> attributes =
              StatementFinder.getStatementsRecursively(catalog, leftoverElement);
            for (AttributeStatement a : attributes) {
              Value value = a.getValue();
              if (value instanceof Set) {
                gov.nasa.pds.tools.label.Set set =
                  (gov.nasa.pds.tools.label.Set) value;
                for (Iterator<Scalar> i = set.iterator(); i.hasNext();) {
                  extrinsicMet.addMetadata(leftoverElement, i.next().toString());
                }
              } else if (value instanceof Sequence) {
                Sequence sequence = (Sequence) value;
                for (Iterator<Value> i = sequence.iterator(); i.hasNext();) {
                  extrinsicMet.addMetadata(leftoverElement, i.next().toString());
                }
              } else {
                extrinsicMet.addMetadata(leftoverElement, value.toString());
              }
            }
          }
        }
      } catch (PdapRegistryClientException e) {
        log.log(new ToolsLogRecord(ToolsLevel.SEVERE, e.getMessage(),
            datasetId));
      }
    }
    ExtrinsicObject extrinsic = new ExtrinsicObject();
    extrinsic.setObjectType(Constants.DATA_SET_PRODUCT_CLASS);
    extrinsic.setLid(lid);
    if (extrinsicMet.containsKey("DATA_SET_NAME")) {
      extrinsic.setName(extrinsicMet.getMetadata("DATA_SET_NAME"));
    }
    Set<Slot> slots = new HashSet<Slot>();
    slots.add(new Slot(Constants.PRODUCT_VERSION, Arrays.asList(
        new String[]{"1.0"})));
    slots.add(new Slot("resource_ref", Arrays.asList(
        new String[]{createLid(Constants.RESOURCE_PREFIX, datasetId)})));
    slots.add(new Slot("modification_date", Arrays.asList(
        new String[]{Utility.getDate()})));
    // Register static metadata as slots
    for (gov.nasa.pds.harvest.pdap.policy.Slot slot :
      staticMetadata.getSlot()) {
      slots.add(new Slot(slot.getName(), slot.getValue()));
    }
    for (String key : extrinsicMet.getAllKeys()) {
      if (key.equals("DATA_SET_NAME")) {
        continue;
      }
      List<String> values = new ArrayList<String>();
      if (extrinsicMet.isMultiValued(key)) {
        values.addAll(extrinsicMet.getAllMetadata(key));
      } else {
        values.add(extrinsicMet.getMetadata(key));
      }
      for (Element element : dynamicMetadata.getElement()) {
        if (element.getName().equals(key)) {
          for (String slotName : element.getSlotName()) {
            slots.add(new Slot(slotName, values));
          }
        }
      }
    }
    extrinsic.setSlots(slots);
    return extrinsic;
  }

  /**
   * Gets the catalog file name.
   *
   * @param voldesc The VOLDESC.CAT label.
   * @param datasetId The dataset ID associated with the VOLDESC.CAT.
   *
   * @return The catalog file name associated with the given dataset ID.
   */
  private String getCatalogFile(Label voldesc, String datasetId) {
    List<ObjectStatement> volumes = voldesc.getObjects("VOLUME");
    if (volumes.isEmpty()) {
      log.log(new ToolsLogRecord(Level.SEVERE, "No VOLUME object found in "
          + "VOLDESC.CAT", datasetId));
      return null;
    } else {
      ObjectStatement volume = volumes.get(0);
      List<ObjectStatement> catalogs = volume.getObjects("CATALOG");
      if (catalogs.isEmpty()) {
        log.log(new ToolsLogRecord(Level.SEVERE, "No CATALOG object found "
            + "in VOLDESC.CAT", datasetId));
        return null;
      } else {
        ObjectStatement catalog = catalogs.get(0);
        PointerStatement datasetCatPointer = catalog.getPointer(
            DictIDFactory.createPointerDefId("DATA_SET_CATALOG"));
        if (datasetCatPointer != null) {
          return datasetCatPointer.getValue().toString();
        } else {
          log.log(new ToolsLogRecord(Level.SEVERE, "No DATA_SET_CATALOG "
              + "pointer statement found in the CATALOG object within the "
              + "VOLDESC.CAT.", datasetId));
          return null;
        }
      }
    }
  }

  /**
   * Register a resource product.
   *
   * @param lid The logical identifier of the product to be registered.
   * @param datasetMet The metadata to register.
   *
   * @return The GUID of the registered product.
   *
   * @throws PdapRegistryClientException If an error occurred while talking to
   * the PDAP Registry Service.
   *
   * @throws RegistryServiceException If an error occurred while ingesting to
   * the PDS Registry.
   *
   */
  private String registerResource(String lid, Metadata datasetMet)
  throws PdapRegistryClientException, RegistryServiceException {
    ExtrinsicObject extrinsic = createResourceExtrinsic(lid, datasetMet);
    if (log.getParent().getHandlers()[0].getLevel().intValue()
        <= ToolsLevel.DEBUG.intValue()) {
      try {
      log.log(new ToolsLogRecord(ToolsLevel.DEBUG,
        "Extrinsic object contents: \n" + Utility.toXML(extrinsic)));
      } catch (JAXBException je) {
        log.log(new ToolsLogRecord(ToolsLevel.SEVERE, je.getMessage()));
      }
    }
    return registryService.ingest(extrinsic);
  }

  /**
   * Creates an extrinsic object for the Resource product to be registered.
   *
   * @param lid The logical identifier of the product to be registered.
   * @param datasetMet The metadata to register.
   * @return An extrinsic object of the Resource product.
   *
   * @throws PdapRegistryClientException If an error occurred while talking
   * to the PDAP Registry Service.
   */
  private ExtrinsicObject createResourceExtrinsic(String lid, Metadata datasetMet)
  throws PdapRegistryClientException {
    String datasetId = datasetMet.getMetadata("DATA_SET_ID");
    ExtrinsicObject extrinsic = new ExtrinsicObject();
    extrinsic.setObjectType(Constants.RESOURCE_PRODUCT_CLASS);
    extrinsic.setLid(lid);
    extrinsic.setName(resourceMetadata.getTitle());
    Set<Slot> slots = new HashSet<Slot>();
    slots.add(new Slot(Constants.PRODUCT_VERSION, Arrays.asList(
        new String[]{"1.0"})));
    slots.add(new Slot("resource_type", Arrays.asList(
        new String[]{resourceMetadata.getType()})));
    slots.add(new Slot("resource_url", Arrays.asList(
        new String[]{pdapClient.getResourceLink(datasetId).toString()})));
    slots.add(new Slot("modification_date", Arrays.asList(
        new String[]{Utility.getDate()})));
    for (gov.nasa.pds.harvest.pdap.policy.Slot slot :
      resourceMetadata.getSlot()) {
      slots.add(new Slot(slot.getName(), slot.getValue()));
    }
    extrinsic.setSlots(slots);
    return extrinsic;
  }
}
