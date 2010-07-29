package gov.nasa.pds.harvest.ingest;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.jersey.api.client.ClientResponse;

import gov.nasa.jpl.oodt.cas.filemgr.ingest.Ingester;
import gov.nasa.jpl.oodt.cas.filemgr.structs.exceptions.CatalogException;
import gov.nasa.jpl.oodt.cas.filemgr.structs.exceptions.IngestException;
import gov.nasa.jpl.oodt.cas.metadata.MetExtractor;
import gov.nasa.jpl.oodt.cas.metadata.Metadata;
import gov.nasa.pds.harvest.crawler.metadata.PDSCoreMetKeys;
import gov.nasa.pds.harvest.logging.ToolsLogRecord;
import gov.nasa.pds.registry.client.RegistryClient;
import gov.nasa.pds.registry.model.Product;
import gov.nasa.pds.registry.model.Slot;


public class RegistryIngester implements Ingester, PDSCoreMetKeys {
	private static Logger log = Logger.getLogger(RegistryIngester.class.getName());
	private String token;
	private String user;
	
	public RegistryIngester() {
		this(null, null);
	}
	
	public RegistryIngester(String user, String token) {
		this.token = token;
		this.user = user;
	}
	
	public boolean isRunning(URL registry) {
		RegistryClient client = new RegistryClient(registry.toString(), token);
		ClientResponse response = client.getStatus();
		if(response.getStatus() == ClientResponse.Status.OK.getStatusCode()) {
			return true;
		} else {
			return false;
		}
	}	
	
	public boolean hasProduct(URL registry, File prodFile) throws CatalogException {
		// No use for this method for now
		return false;
	}

	public boolean hasProduct(URL registry, String productID) throws CatalogException {
		RegistryClient client = new RegistryClient(registry.toString(), token);
		ClientResponse response = client.getLatestProduct(productID);
		if(response.getStatus() == ClientResponse.Status.OK.getStatusCode()) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean hasProduct(URL registry, String productID, String productVersion) throws CatalogException {
		RegistryClient client = new RegistryClient(registry.toString(), token);
		ClientResponse response = client.getProduct(productID, productVersion);
		if(response.getStatus() == ClientResponse.Status.OK.getStatusCode()) {
			return true;
		} else {
			return false;
		}
	}

	public String ingest(URL registry, File prodFile, Metadata met) throws IngestException {
		RegistryClient client = new RegistryClient(registry.toString(), token);
		Product product = createProduct(met);
		ClientResponse response = null;
		try {
			if(hasProduct(registry, product.getLid()))
				response = client.versionProduct(user, product, product.getLid());		
			else
				response = client.publishProduct(user, product);
		} catch(CatalogException c) {
			throw new IngestException(c.getMessage());
		}
		if(response.getStatus() == ClientResponse.Status.CREATED.getStatusCode()) {
			log.log(new ToolsLogRecord(Level.INFO, 
					"Succesfully registered product: " + response.getLocation(), 
					prodFile));
			return response.getLocation().toString();
		}
		else {
			log.log(new ToolsLogRecord(Level.SEVERE, 
					"POST request returned HTTP code: " + response.getStatus(),
					prodFile));
			throw new IngestException("POST request returned HTTP code: " + response.getStatus());
		}
	}
	
	private Product createProduct(Metadata metadata) {
		Product product = new Product();
		Set<Slot> slots = new HashSet<Slot>();			
		Set metSet = metadata.getHashtable().entrySet();
		for(Iterator i = metSet.iterator(); i.hasNext();) {
			Map.Entry entry = (Map.Entry) i.next();
			String key = entry.getKey().toString();
			if(key.equals(REFERENCES)) {
				continue;
			}
			if(key.equals(LOGICAL_ID)) {
				product.setLid(metadata.getMetadata(LOGICAL_ID));
			} else if(key.equals(PRODUCT_VERSION)) {
				product.setUserVersion(metadata.getMetadata(PRODUCT_VERSION));
			} else if(key.equals(OBJECT_TYPE)) {
				product.setObjectType(metadata.getMetadata(OBJECT_TYPE));
			} else if(key.equals(TITLE)) {
				product.setName(metadata.getMetadata(TITLE));
			} else {
				List<String> values = new ArrayList<String>();
				if(metadata.isMultiValued(key)) {
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
	
	public String ingest(URL fmUrl, File prodFile, MetExtractor extractor, File metConfFile)
			throws IngestException {
		//No need for this method at this time
		return null;
	}

	public void ingest(URL fmUrl, List<String> prodFiles, MetExtractor extractor, File metConfFile)
			throws IngestException {
		//No need for this method at this time
	}
}
