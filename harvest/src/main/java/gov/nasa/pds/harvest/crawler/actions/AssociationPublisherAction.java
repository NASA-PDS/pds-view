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
import gov.nasa.pds.harvest.context.ReferenceEntry;
import gov.nasa.pds.harvest.crawler.metadata.PDSCoreMetKeys;
import gov.nasa.pds.harvest.logging.ToolsLevel;
import gov.nasa.pds.harvest.logging.ToolsLogRecord;
import gov.nasa.pds.registry.client.RegistryClient;
import gov.nasa.pds.registry.model.Association;
import gov.nasa.pds.registry.model.Product;

/**
 * Class to publish associations to the PDS Registry Service upon
 * successful ingestion of the product.
 *
 * @author mcayanan
 *
 */
public class AssociationPublisherAction extends CrawlerAction
implements PDSCoreMetKeys {
    private static Logger log = Logger.getLogger(
            AssociationPublisherAction.class.getName());
    private RegistryClient registryClient;
    private String user;
    private final String ID = "AssociationPublisherAction";
    private final String DESCRIPTION =
        "Registers the product's associations.";

    public AssociationPublisherAction(String registryUrl) {
        this(registryUrl, null, null);
    }

    public AssociationPublisherAction(String registryUrl, String user,
            String token) {
        super();
        if(user != null) {
            this.user = user;
            this.registryClient = new RegistryClient(registryUrl, token);
        } else {
            this.registryClient = new RegistryClient(registryUrl);
        }

        String []phases = {CrawlerActionPhases.POST_INGEST_SUCCESS};
        setPhases(Arrays.asList(phases));
        setId(ID);
        setDescription(DESCRIPTION);
    }

    @Override
    public boolean performAction(File product, Metadata productMetadata)
            throws CrawlerActionException {
        boolean passFlag = true;

        List<Association> associations = createAssociations(
                product, productMetadata);
        for(Association association : associations) {
            ClientResponse response = registryClient.publishAssociation(
                    user, association);
            if(response.getStatus() ==
                ClientResponse.Status.CREATED.getStatusCode()) {
                String lidvid = association.getTargetLid();
                if(association.getTargetVersionId() != null) {
                        lidvid += "::" + association.getTargetVersionId();
                }
                log.log(new ToolsLogRecord(ToolsLevel.INGEST_ASSOC_SUCCESS,
                        "Successfully registered association to " + lidvid,
                        product));
            } else {
                String lidvid = association.getTargetLid() + "::"
                   + association.getTargetVersionId();
                log.log(new ToolsLogRecord(ToolsLevel.INGEST_ASSOC_FAIL,
                        "Problem registering association to " + lidvid
                        + ". HTTP error code: " + response.getStatus(),
                        product));
                passFlag = false;
            }
        }
        return passFlag;
    }

    private List<Association> createAssociations(File product,
            Metadata metadata) {
        List<Association> associations = new ArrayList<Association>();
        if(metadata.containsKey(REFERENCES)) {
            for(ReferenceEntry re :
                (List<ReferenceEntry>) metadata.getAllMetadata(REFERENCES)) {
                Association association = new Association();
                association.setSourceLid(metadata.getMetadata(LOGICAL_ID));
                association.setSourceVersionId(
                        metadata.getMetadata(PRODUCT_VERSION));
                association.setAssociationType(re.getAssociationType());
                association.setObjectType(re.getObjectType());
                association.setTargetLid(re.getLogicalID());
                if(re.hasVersion()) {
                    association.setTargetVersionId(re.getVersion());
                } else {
                    ClientResponse response = registryClient.getLatestProduct(
                            re.getLogicalID());
                    if(response.getStatus() ==
                        ClientResponse.Status.OK.getStatusCode()) {
                        Product target = response.getEntity(Product.class);
                        association.setTargetVersionId(target.getVersionId());
                        log.log(new ToolsLogRecord(ToolsLevel.INFO,
                                "Found association in registry: "
                                + re.getLogicalID() + ". Target version will "
                                + "be set to latest registered: "
                                + target.getVersionId(),
                                product));
                    }
                    else {
                        log.log(new ToolsLogRecord(ToolsLevel.WARNING,
                                "No version found in label or registry for"
                                + " association to " + re.getLogicalID()
                                + ".", product));
                    }
                }
                associations.add(association);
            }
        }
        return associations;
    }

}
