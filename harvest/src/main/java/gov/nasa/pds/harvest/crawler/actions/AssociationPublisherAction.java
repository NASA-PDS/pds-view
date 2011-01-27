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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import com.sun.jersey.api.client.ClientResponse;

import gov.nasa.jpl.oodt.cas.crawl.action.CrawlerAction;
import gov.nasa.jpl.oodt.cas.crawl.action.CrawlerActionPhases;
import gov.nasa.jpl.oodt.cas.crawl.structs.exceptions.CrawlerActionException;
import gov.nasa.jpl.oodt.cas.metadata.Metadata;
import gov.nasa.pds.harvest.constants.Constants;
import gov.nasa.pds.harvest.crawler.stats.AssociationStats;
import gov.nasa.pds.harvest.inventory.ReferenceEntry;
import gov.nasa.pds.harvest.logging.ToolsLevel;
import gov.nasa.pds.harvest.logging.ToolsLogRecord;
import gov.nasa.pds.registry.client.RegistryClient;
import gov.nasa.pds.registry.model.Association;
import gov.nasa.pds.registry.model.Product;
import gov.nasa.pds.registry.model.RegistryResponse;
import gov.nasa.pds.registry.query.AssociationFilter;
import gov.nasa.pds.registry.query.AssociationQuery;

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
    private final String DESCRIPTION =
        "Registers the product's associations.";

    private AssociationStats stats;

    /**
     * Constructor.
     *
     * @param registryUrl The URL to the registry service.
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
     */
    public AssociationPublisherAction(String registryUrl, String user,
            String token) {
        super();
        if (user != null) {
            this.user = user;
            this.registryClient = new RegistryClient(registryUrl, token);
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

        List<Association> associations = createAssociations(
                product, productMetadata);
        for (Association association : associations) {
            String lidvid = association.getTargetLid();
            if (association.getTargetVersionId() != null) {
                lidvid += "::" + association.getTargetVersionId();
            }
            if (!hasAssociation(association)) {
                ClientResponse response = registryClient.publishAssociation(
                    user, association);
                if (response.getStatus()
                    == ClientResponse.Status.CREATED.getStatusCode()) {
                    log.log(new ToolsLogRecord(
                        ToolsLevel.INGEST_ASSOC_SUCCESS,
                        "Successfully registered association to " + lidvid,
                        product));
                    ++numRegistered;
                } else {
                  log.log(new ToolsLogRecord(ToolsLevel.INGEST_ASSOC_FAIL,
                        "Problem registering association to " + lidvid
                        + ". HTTP error code: " + response.getStatus(),
                        product));
                  ++numNotRegistered;
                  passFlag = false;
                }
            } else {
                log.log(new ToolsLogRecord(ToolsLevel.INGEST_ASSOC_SKIP,
                    "Association to " + lidvid + ", with \'"
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
     */
    private boolean hasAssociation(Association association) {
        AssociationFilter.Builder fBuilder = new AssociationFilter.Builder();
        fBuilder.sourceLid(association.getSourceLid());
        fBuilder.sourceVersionId(association.getSourceVersionId());
        fBuilder.targetLid(association.getTargetLid());
        fBuilder.targetVersionId(association.getTargetVersionId());
        fBuilder.associationType(association.getAssociationType());

        AssociationQuery.Builder qBuilder = new AssociationQuery.Builder();
        qBuilder.filter(fBuilder.build());

        ClientResponse response = registryClient.getAssociations(
            qBuilder.build(), 1, 10);
        if (response.getStatus()
            == ClientResponse.Status.OK.getStatusCode()) {
            RegistryResponse registryResponse = response.getEntity(
                RegistryResponse.class);
            if (registryResponse.getNumFound() == 0) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    /**
     * Creates associations.
     *
     * @param product The file containing the associations.
     * @param metadata The metadata associated with the given product.
     *
     * @return A list containing the associations for the given product.
     */
    private List<Association> createAssociations(File product,
            Metadata metadata) {
        List<Association> associations = new ArrayList<Association>();
        if (metadata.containsKey(Constants.REFERENCES)) {
            for (ReferenceEntry re : (List<ReferenceEntry>)
                    metadata.getAllMetadata(Constants.REFERENCES)) {
                Association association = new Association();
                association.setSourceLid(metadata.getMetadata(
                        Constants.LOGICAL_ID));
                association.setSourceVersionId(
                        metadata.getMetadata(Constants.PRODUCT_VERSION));
                association.setAssociationType(re.getAssociationType());
                association.setObjectType(re.getObjectType());
                association.setTargetLid(re.getLogicalID());
                if (re.hasVersion()) {
                    association.setTargetVersionId(re.getVersion());
                } else {
                    ClientResponse response = registryClient.getLatestProduct(
                            re.getLogicalID());
                    if (response.getStatus()
                            == ClientResponse.Status.OK.getStatusCode()) {
                        Product target = response.getEntity(Product.class);
                        association.setTargetVersionId(target.getVersionId());
                        log.log(new ToolsLogRecord(ToolsLevel.INFO,
                                "Found association in registry: "
                                + re.getLogicalID() + ". Target version will "
                                + "be set to latest registered: "
                                + target.getVersionId(),
                                product));
                    } else {
                        log.log(new ToolsLogRecord(ToolsLevel.WARNING,
                                "No version found in label or registry for"
                                + " association to " + re.getLogicalID()
                                + ". Version will be set to 1.0.", product));
                        //TODO: Is this correct behavior?
                        association.setTargetVersionId("1.0");
                    }
                }
                associations.add(association);
            }
        }
        return associations;
    }
}
