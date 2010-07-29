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
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.jersey.api.client.ClientResponse;

import gov.nasa.jpl.oodt.cas.crawl.action.CrawlerAction;
import gov.nasa.jpl.oodt.cas.crawl.action.CrawlerActionPhases;
import gov.nasa.jpl.oodt.cas.crawl.structs.exceptions.CrawlerActionException;
import gov.nasa.jpl.oodt.cas.metadata.Metadata;
import gov.nasa.pds.harvest.context.ReferenceEntry;
import gov.nasa.pds.harvest.crawler.metadata.PDSCoreMetKeys;
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
public class AssociationPublisherAction extends CrawlerAction {
	private static Logger log = Logger.getLogger(AssociationPublisherAction.class.getName());	
	private RegistryClient registryClient;
	private String user;
	private final String ID = "AssociationPublisherAction";
	private final String DESCRIPTION = "Registers the product's associations.";
	
	public AssociationPublisherAction(String registryUrl) {
		this(registryUrl, null, null);
	}
	
	public AssociationPublisherAction(String registryUrl, String user, String token) {
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
		
		List<Association> associations = createAssociations(product, productMetadata);
		for(Association association : associations) {
			ClientResponse response = registryClient.publishAssociation(user, association);
			if(response.getStatus() == ClientResponse.Status.CREATED.getStatusCode()) {
	            log.log(new ToolsLogRecord(Level.INFO, 
	            		"Successfully registered association: " + response.getLocation(), product));
			} else {
				String lidvid = association.getTargetLid() + "::" + association.getTargetVersion();
				log.log(new ToolsLogRecord(Level.WARNING, 
						"Problem registering association " + lidvid + ". HTTP error code: " + response.getStatus(),
						product));
				passFlag = false;
			}
		}
		return passFlag;
	}
	
	private List<Association> createAssociations(File product, Metadata metadata) {
		List<Association> associations = new ArrayList<Association>();
		if(metadata.containsKey(PDSCoreMetKeys.REFERENCES)) {
			for(ReferenceEntry re : (List<ReferenceEntry>) metadata.getAllMetadata(PDSCoreMetKeys.REFERENCES)) {
				Association association = new Association();
				association.setSourceLid(metadata.getMetadata(PDSCoreMetKeys.LOGICAL_ID));
				association.setSourceVersion(metadata.getMetadata(PDSCoreMetKeys.PRODUCT_VERSION));
				association.setAssociationType(re.getAssociationType());
				association.setObjectType(re.getObjectType());
				association.setTargetLid(re.getLogicalID());
				if(re.hasVersion()) {
					association.setTargetVersion(re.getVersion());
				} else {
					Product target = registryClient.getLatestProduct(re.getLogicalID()).getEntity(Product.class);
					if(target != null) {
						association.setTargetVersion(target.getUserVersion());
					} else {
						log.log(new ToolsLogRecord(Level.WARNING,
								"Association missing a target version. Target product not found: " + re.getLogicalID(),
								product));
					}
				}
				associations.add(association);
			}
		}
		return associations;
	}	

}
