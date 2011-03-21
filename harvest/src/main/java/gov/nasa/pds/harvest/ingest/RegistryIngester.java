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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.sun.jersey.api.client.ClientResponse;

import gov.nasa.jpl.oodt.cas.filemgr.ingest.Ingester;
import gov.nasa.jpl.oodt.cas.filemgr.structs.exceptions.CatalogException;
import gov.nasa.jpl.oodt.cas.filemgr.structs.exceptions.IngestException;
import gov.nasa.jpl.oodt.cas.metadata.MetExtractor;
import gov.nasa.jpl.oodt.cas.metadata.Metadata;
import gov.nasa.pds.harvest.constants.Constants;
import gov.nasa.pds.harvest.logging.ToolsLevel;
import gov.nasa.pds.harvest.logging.ToolsLogRecord;
import gov.nasa.pds.registry.client.RegistryClient;
import gov.nasa.pds.registry.model.ExtrinsicObject;
import gov.nasa.pds.registry.model.Slot;

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

    /** A security token. */
    private String token;

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
    public RegistryIngester(String user, String token) {
        this.token = token;
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
     *
     * @throws CatalogException If an error occurred while talking to the
     * ingester.
     */
    public boolean hasProduct(URL registry, String productID)
    throws CatalogException {
        RegistryClient client = new RegistryClient(registry.toString(),
                token);
        ClientResponse response = client.getLatestExtrinsic(productID);
        if (response.getStatus()
                == ClientResponse.Status.OK.getStatusCode()) {
            return true;
        } else {
            return false;
        }
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
        RegistryClient client = new RegistryClient(registry.toString(),
                token);
        ClientResponse response = client.getExtrinsic(productID,
                productVersion);
        if (response.getStatus()
                == ClientResponse.Status.OK.getStatusCode()) {
            return true;
        } else {
            return false;
        }
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
        RegistryClient client = new RegistryClient(registry.toString(),
                token);
        ExtrinsicObject product = createProduct(met);
        ClientResponse response = null;
        try {
            if (hasProduct(registry, product.getLid())) {
                response = client.versionExtrinsic(user, product,
                        product.getLid());
            }
            else {
                response = client.publishExtrinsic(user, product);
            }
        } catch (CatalogException c) {
            throw new IngestException(c.getMessage());
        }
        if (response.getStatus()
                == ClientResponse.Status.CREATED.getStatusCode()) {
            String lidvid = met.getMetadata(Constants.LOGICAL_ID) + "::"
            + met.getMetadata(Constants.PRODUCT_VERSION);
            String guid = response.getEntity(String.class);
            log.log(new ToolsLogRecord(ToolsLevel.INGEST_SUCCESS,
                    "Successfully registered product: " + lidvid, prodFile));
            log.log(new ToolsLogRecord(ToolsLevel.INFO,
                    "Product has the following GUID: " + guid, prodFile));
            met.addMetadata(Constants.PRODUCT_GUID, guid);
            return response.getLocation().toString();
        }
        else {
            log.log(new ToolsLogRecord(ToolsLevel.INGEST_FAIL,
                    "POST request returned HTTP code: "
                    + response.getStatus(),
                    prodFile));
            throw new IngestException("POST request returned HTTP code: "
                    + response.getStatus());
        }
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
            } else if (key.equals(Constants.PRODUCT_VERSION)) {
                product.setVersionId(metadata.getMetadata(
                        Constants.PRODUCT_VERSION));
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
