// Copyright 2009, by the California Institute of Technology.
// ALL RIGHTS RESERVED. United States Government sponsorship acknowledged.
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
package gov.nasa.pds.citool.ingestor;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.oodt.cas.filemgr.datatransfer.RemoteDataTransferFactory;
import org.apache.oodt.cas.filemgr.structs.Product;
import org.apache.oodt.cas.filemgr.structs.exceptions.ConnectionException;
import org.apache.oodt.cas.filemgr.structs.exceptions.RepositoryManagerException;
import org.apache.oodt.cas.filemgr.system.XmlRpcFileManagerClient;
import org.apache.oodt.cas.filemgr.util.GenericFileManagerObjectFactory;
import org.apache.oodt.cas.filemgr.versioning.VersioningUtils;
import org.apache.oodt.cas.metadata.Metadata;

import gov.nasa.pds.citool.ingestor.CatalogObject;
import gov.nasa.pds.citool.file.FileObject;
import gov.nasa.pds.citool.ingestor.Constants;

/**
 * Class that will ingest registered products to the PDS Storage
 * Service.
 *
 * @author hyunlee
 *
 */
public class StorageIngester {
	
	/** The Storage Service Client object. */
	private XmlRpcFileManagerClient fmClient;

	/** The product type name for the Storage Service. */
	private String productTypeName = "CatalogObject";
	
	/** The product name for the Storage Service. Uses VOLUME_ID for now */
	private String productName;

	/**	
	 * Constructor.
	 *
	 * @param storageServerUrl URL to the PDS storage server.
	 *
	 * @throws ConnectionException If there was an error connecting to the
	 * Storage Service.
	 */
	public StorageIngester(URL storageServerUrl) throws ConnectionException {
		fmClient = new XmlRpcFileManagerClient(storageServerUrl);
		fmClient.setDataTransfer(GenericFileManagerObjectFactory
				.getDataTransferServiceFromFactory(RemoteDataTransferFactory.class.getName()));
		productName = "default_product";
	}
	
	/**
	 * Sets the product name for the storage service
	 */
	public void setProductName(String productName) {
		this.productName = productName;
	}
	
	/**
	 * Returns the File Manager client.
	 */
	public XmlRpcFileManagerClient getFMClient() {
		return this.fmClient;
	}
	
	  /**
	   * Ingests a file object and gets a product id from the storage server.
	   *
	   * @param product The file associated with the given file object.
	   * @param fileObject The file object to ingest.
	   * @param metadata The metadata associated with the given file.
	   *
	   * @return The storage service product identifier if an ingestion
	   * was successful. If an error occurred, a null will be returned.
	   */
	public String ingestToStorage(CatalogObject catObj) {
		FileObject fileObject = catObj.getFileObject();
		Product prod = new Product();
		
		String productId = null;
		prod.setProductName(this.productName + ":" + fileObject.getName());
		prod.setProductStructure(Product.STRUCTURE_FLAT);
		
		try {
			prod.setProductType(fmClient.getProductTypeByName(productTypeName));
		} catch (RepositoryManagerException rme) {
			rme.printStackTrace();
			return null;
		}
		// add references to the product
		List<String> references = new Vector<String>();
	    references.add(new File(fileObject.getLocation(), fileObject.getName()).toURI().toString());
	    VersioningUtils.addRefsFromUris(prod, references);
	    
		Metadata prodMet = new Metadata();
		prodMet.addMetadata("ProductClass", Constants.FILE_PROD);
		prodMet.addMetadata("ProductType", this.productTypeName);
		prodMet.addMetadata("ProductName", this.productName + ":" + fileObject.getName().toLowerCase());
		prodMet.addMetadata("OriginalFilename", fileObject.getName().toLowerCase());
		prodMet.addMetadata("VolumeId", this.productName);
		prodMet.addMetadata("Version", String.valueOf(catObj.getVersion()));
		
		try {
			//productId = fmClient.ingestProduct(prod, prodMet, false);
			productId = fmClient.ingestProduct(prod, prodMet, true);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return productId;
	}
	
	/**
	   * Gets a product by given product id
	   *
	   * @param productId   The ID of the product 
	   * @return The storage service product if the product is exists on the 
	   * storage server. Otherwise, a null will be returned.
	   */
	public Product getProductById(String productId) {
		try {
			return fmClient.getProductById(productId);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	 /**
	   * Gets a product by given name
	   *
	   * @param productName    The name of the product 
	   * @return The storage service product if the product is exists on the 
	   * storage server. Otherwise, a null will be returned.
	   */
	public Product getProductByName(String productName) {
		try {
			return fmClient.getProductByName(productName);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}