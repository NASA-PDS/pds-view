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
package gov.nasa.pds.registry.server.connection;

import gov.nasa.pds.registry.client.RegistryClient;
import gov.nasa.pds.registry.exception.RegistryClientException;
import gov.nasa.pds.registry.exception.RegistryServiceException;
import gov.nasa.pds.registry.model.Association;
import gov.nasa.pds.registry.model.AuditableEvent;
import gov.nasa.pds.registry.model.ClassificationNode;
import gov.nasa.pds.registry.model.ClassificationScheme;
import gov.nasa.pds.registry.model.ExtrinsicObject;
import gov.nasa.pds.registry.model.Link;
import gov.nasa.pds.registry.model.ObjectAction;
import gov.nasa.pds.registry.model.ObjectStatus;
import gov.nasa.pds.registry.model.PagedResponse;
import gov.nasa.pds.registry.model.RegistryObject;
import gov.nasa.pds.registry.model.RegistryPackage;
import gov.nasa.pds.registry.model.Report;
import gov.nasa.pds.registry.model.Slot;
import gov.nasa.pds.registry.model.Service;
import gov.nasa.pds.registry.model.ServiceBinding;
import gov.nasa.pds.registry.model.SpecificationLink;
import gov.nasa.pds.registry.query.AssociationFilter;
import gov.nasa.pds.registry.query.ExtrinsicFilter;
import gov.nasa.pds.registry.query.EventFilter;
import gov.nasa.pds.registry.query.PackageFilter;
import gov.nasa.pds.registry.query.RegistryQuery;

import gov.nasa.pds.registry.ui.shared.StatusInformation;
import gov.nasa.pds.registry.ui.shared.ViewAssociation;
import gov.nasa.pds.registry.ui.shared.ViewAssociations;
import gov.nasa.pds.registry.ui.shared.ViewAuditableEvent;
import gov.nasa.pds.registry.ui.shared.ViewAuditableEvents;
import gov.nasa.pds.registry.ui.shared.ViewClassificationNode;
import gov.nasa.pds.registry.ui.shared.ViewClassificationNodes;
import gov.nasa.pds.registry.ui.shared.ViewProduct;
import gov.nasa.pds.registry.ui.shared.ViewProducts;
import gov.nasa.pds.registry.ui.shared.ViewRegistryPackage;
import gov.nasa.pds.registry.ui.shared.ViewRegistryPackages;
import gov.nasa.pds.registry.ui.shared.ViewScheme;
import gov.nasa.pds.registry.ui.shared.ViewSchemes;
import gov.nasa.pds.registry.ui.shared.ViewSlot;
import gov.nasa.pds.registry.ui.shared.ViewService;
import gov.nasa.pds.registry.ui.shared.ViewServices;
import gov.nasa.pds.registry.ui.shared.ViewServiceBinding;
import gov.nasa.pds.registry.ui.shared.ViewSpecificationLink;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.HashSet;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.core.MediaType;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manager to send and receive info from remote registry service.
 * 
 * Note that connection to localhost is currently hard wired in.
 * 
 * @author jagander, hyunlee
 */
public class ConnectionManager {

	/**
	 * Default location of service endpoint
	 */
	public static final String DEFAULT_SERVICE_ENDPOINT = "http://localhost:8080/registry/"; //$NON-NLS-1$

	/**
	 * Name of properties file that contains override for service endpoint
	 */
	public static final String PROPS_NAME = "Application.properties"; //$NON-NLS-1$

	/**
	 * Instance of applicaiton properties
	 */
	public static Properties props;

	public static Logger logger = Logger.getLogger("registry-ui");

	// Synchronizes the incoming registry artifacts with those already present
	// in the registry.
	public static void synch() {
		// stub
	}

	// Retrieve the status of the registry service. This can be used to monitor
	// the health of the registry.
	public static StatusInformation getStatusInfo(String serverUrl) {
		RegistryClient client = getRegistry(serverUrl);
		StatusInformation si = new StatusInformation();
		try {			
			Report rpt = client.getReport();
			
			si.setStatus(rpt.getStatus().toString());
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS");
			si.setServerStarted(sdf.format(rpt.getServerStarted()));
			si.setAssociations(rpt.getAssociations());
			si.setExtrinsics(rpt.getExtrinsics());
			si.setServices(rpt.getServices());
			si.setClassificationSchemes(rpt.getClassificationSchemes());
			si.setClassificationNodes(rpt.getClassificationNodes());
			si.setPackages(rpt.getPackages());
			si.setRegistryVersion(rpt.getRegistryVersion());
			si.setEvents(rpt.getEvents());
			
		} catch (RegistryServiceException e) {
			System.out.println(e.getMessage());
		}
		
		return si; 
	}

	/**
	 * Get products starting from the first record, with no filtering, the
	 * default sort and the default number of records.
	 * 
	 * @products default product retrieval
	 * 
	 * @see #getProducts(RegistryQuery, Integer, Integer)
	 */
	public static ViewProducts getProducts(String serverUrl) {
		return getProducts(serverUrl, null, null, null);
	}

	/**
	 * Get products starting from the given record number, with no filtering,
	 * the default sort and the default number of records.
	 * 
	 * @return products from the given start
	 * 
	 * @see #getProducts(RegistryQuery, Integer, Integer)
	 */
	public static ViewProducts getProducts(String serverUrl, Integer start) {
		return getProducts(serverUrl, null, start, null);
	}

	/**
	 * Get products starting from the given record number and number of records.
	 * 
	 * @return given number of products from the given start
	 * 
	 * @see #getProducts(RegistryQuery, Integer, Integer)
	 */
	public static ViewProducts getProducts(String serverUrl, Integer start, Integer numResults) {
		return getProducts(serverUrl, null, start, numResults);
	}

	/**
	 * Retrieve products from remote registry service. Note that this wraps the
	 * returned results in a view-only version of the products so that they may
	 * be used on the GWT client side.
	 * 
	 * @param query
	 *            a query object containing filter and search params for
	 *            retrieving products
	 * @param start
	 *            start index for retrieved results. Defaults to 1.
	 * @param numResults
	 *            number of results to return. Defaults to 20.
	 * 
	 * @return given number of products from the given start with the given
	 *         filter conditions
	 * 
	 */
	public static ViewProducts getProducts(String serverUrl,
			RegistryQuery<ExtrinsicFilter> query, Integer start,
			Integer numResults) {
		RegistryClient client = getRegistry(serverUrl);
		PagedResponse<ExtrinsicObject> pagedResp = null;
		try {			
			if (query != null) {
				pagedResp = client.getExtrinsics(query, start, numResults);
			} else {				
				pagedResp = client.getObjects(start, numResults,
						ExtrinsicObject.class);
			}
		} catch (RegistryServiceException e) {
			System.out.println(e.getMessage());
		}

		// convert response to view products
		ViewProducts viewProducts = respToViewProducts(pagedResp);

		return viewProducts;
	}

	/**
	 * Retrieve services from remote registry service. Note that this wraps the
	 * returned results in a view-only version of the services so that they may
	 * be used on the GWT client side.
	 * 
	 * @param start
	 *            start index for retrieved results. Defaults to 1.
	 * @param numResults
	 *            number of results to return. Defaults to 20.
	 * 
	 * @return given number of services from the given start
	 * 
	 */
	public static ViewServices getServices(String serverUrl, Integer start, Integer numResults) {
		RegistryClient client = getRegistry(serverUrl);

		PagedResponse<Service> pagedResp = null;
		try {
			pagedResp = client.getObjects(start, numResults, Service.class);
		} catch (RegistryServiceException e) {
			e.printStackTrace();

			System.out.println(e.getMessage());
		}
		ViewServices viewServices = respToViewService(pagedResp);
		
		return viewServices;
	}

	/**
	 * Retrieve schemes from remote registry service. Note that this wraps the
	 * returned results in a view-only version of the schemes so that they may
	 * be used on the GWT client side.
	 * 
	 * @param start
	 *            start index for retrieved results. Defaults to 1.
	 * @param numResults
	 *            number of results to return. Defaults to 20.
	 * 
	 * @return given number of schemes from the given start
	 * 
	 */
	public static ViewSchemes getSchemes(String serverUrl, Integer start, Integer numResults) {
		RegistryClient client = getRegistry(serverUrl);

		PagedResponse<ClassificationScheme> pagedResp = null;
		try {
			pagedResp = client.getObjects(start, numResults,
					ClassificationScheme.class);
		} catch (RegistryServiceException e) {
			e.printStackTrace();

			System.out.println(e.getMessage());
		}

		ViewSchemes viewSchemes = respToViewSchemes(pagedResp);

		return viewSchemes;
	}
	
	/**
	 * Retrieve packages from remote registry service. Note that this wraps the
	 * returned results in a view-only version of the packages so that they may
	 * be used on the GWT client side.
	 * 
	 * @param start
	 *            start index for retrieved results. Defaults to 1.
	 * @param numResults
	 *            number of results to return. Defaults to 20.
	 * 
	 * @return given number of packages from the given start
	 * 
	 */
	public static ViewRegistryPackages getPackages(String serverUrl, RegistryQuery<PackageFilter> query,
			Integer start, Integer numResults) {
		RegistryClient client = getRegistry(serverUrl);

		PagedResponse<RegistryPackage> pagedResp = null;
		try {
			if (query!=null) {
				pagedResp = client.getPackages(query, start, numResults);
			}
			else {
				pagedResp = client.getObjects(start, numResults,
						RegistryPackage.class);
			}
		} catch (RegistryServiceException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}

		ViewRegistryPackages viewPackages = respToViewPackage(pagedResp);
	
		return viewPackages;
	}
	
	/**
	 * Updates the given RegistryPackage
	 * 
	 * @param viewRegistryPackage a RegistryPackage to update
	 */
	public static boolean updatePackage(String serverUrl, ViewRegistryPackage registryPackage) {
		RegistryClient client = getRegistry(serverUrl);
		boolean returnStatus = false;
		try {
			RegistryPackage rp = client.getObject(registryPackage.getGuid(), RegistryPackage.class);
			ObjectAction oa = null;			
			if (registryPackage.getStatus().equals("Approved"))
				oa = ObjectAction.approve;
			else if (registryPackage.getStatus().equals("Deprecated"))
				oa = ObjectAction.deprecate;
			else if (registryPackage.getStatus().equals("Submitted"))
				oa = ObjectAction.undeprecate;
				
			if (rp.getStatus()==null)  {
				if (!rp.getStatus().equals("Unknown")) {
					client.changeStatusOfPackageMembers(registryPackage.getGuid(), oa);
					//rp.setStatus(ObjectStatus.valueOf(registryPackage.getStatus()));
					//client.updateObject(rp);
					client.changeStatusOfPackage(registryPackage.getGuid(), oa);
					returnStatus = true;
				}
			}
			else {
				if (!rp.getStatus().toString().equals(registryPackage.getStatus())) {
					client.changeStatusOfPackageMembers(registryPackage.getGuid(), oa);
					//rp.setStatus(ObjectStatus.valueOf(registryPackage.getStatus()));				
					//client.updateObject(rp);
					client.changeStatusOfPackage(registryPackage.getGuid(), oa);
					returnStatus = true;
				}
			}
		} catch (RegistryServiceException e) {
			e.printStackTrace();
		}
		return returnStatus;
	}

	/**
	 * Updates the given ExtrinsicObject
	 * 
	 * @param product an ExtrinsicObject to update
	 */
	public static boolean updateProduct(String serverUrl, ViewProduct product) {
		RegistryClient client = getRegistry(serverUrl);
		boolean returnStatus = false;
		try {
			ExtrinsicObject extObj = client.getObject(product.getGuid(), ExtrinsicObject.class);						
			if (extObj.getStatus()==null) {
				if (!product.getStatus().equals("Unknown")) {
					extObj.setStatus(ObjectStatus.valueOf(product.getStatus()));
					client.updateObject(extObj);
					returnStatus = true;
				}
			}
			else {
				if (!extObj.getStatus().toString().equals(product.getStatus())) {
					extObj.setStatus(ObjectStatus.valueOf(product.getStatus()));
					client.updateObject(extObj);
					returnStatus = true;
				}
			}
		} catch (RegistryServiceException e) {
			e.printStackTrace();
		}
		return returnStatus;
	}

	/**
	 * Removes an ExtrinsicObject from the registry
	 * 
	 * @param product an extrinsic object to remove
	 */
	public static boolean deleteProduct(String serverUrl, ViewProduct product) {
		RegistryClient client = getRegistry(serverUrl);
		boolean status = false;
		try {
			if (product!=null) {
				client.deleteObject(product.getGuid(), ExtrinsicObject.class);
				status = true;
			}
		} catch (RegistryServiceException e) {
			e.printStackTrace();
		}
		return status;
	}
	
	/**
	 * Removes a RegistryPackage from the registry 
	 * 
	 * @param registryPackage a RegistryPackage object to remove
	 */
	public static boolean deletePackage(String serverUrl, ViewRegistryPackage registryPackage) {
		RegistryClient client = getRegistry(serverUrl);
		boolean status = false;
		try {
			if (registryPackage!=null) {
				client.deletePackageMembers(registryPackage.getGuid());
				client.deleteObject(registryPackage.getGuid(), RegistryPackage.class);
				status = true;
			}
		} catch (RegistryServiceException e) {
			e.printStackTrace();
		}
		return status;
	}

	/**
	 * Retrieve events from remote registry service. Note that this wraps the
	 * returned results in a view-only version of the events so that they may
	 * be used on the GWT client side.
	 * 
	 * @param query
	 *            a query object containing filter and search params for
	 *            retrieving events
	 * @param start
	 *            start index for retrieved results. Defaults to 1.
	 * @param numResults
	 *            number of results to return. Defaults to 20.
	 * 
	 * @return given number of events from the given start with the given
	 *         filter conditions
	 *         
	 */
	public static ViewAuditableEvents getEvents(String serverUrl,
			RegistryQuery<EventFilter> query, Integer start, Integer numResults) {

		RegistryClient client = getRegistry(serverUrl);

		PagedResponse<AuditableEvent> pagedResp = null;
		try {
			if (query != null) {
				pagedResp = client.getAuditableEvents(query, start, numResults);
			} else {
				pagedResp = client.getObjects(start, numResults,
						AuditableEvent.class);
			}
		} catch (RegistryServiceException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		// convert response to view events
		ViewAuditableEvents viewEvents = respToViewEvents(pagedResp);
		
		return viewEvents;
	}

	/**
	 * Retrieve a product from remote registry service. Note that this wraps the
	 * returned results in a view-only version of the products so that they may
	 * be used on the GWT client side.
	 * 
	 * @param guid
	 * 			global unique identifier
	 * 
	 * @return a product with the given guid
	 * 
	 */
	public static ViewProduct getProduct(String serverUrl, String guid) {
		RegistryClient client = getRegistry(serverUrl);

		ExtrinsicObject extrinsicObj = null;
		try {
			extrinsicObj = client.getObject(guid, ExtrinsicObject.class);
		} catch (RegistryServiceException e) {
			System.out.println(e.getMessage());
		}
		ViewProduct viewProduct = respToViewProduct(extrinsicObj);

		return viewProduct;
	}
	
	/**
	 * Retrieve classification nodes from remote registry service. Note that this wraps the
	 * returned results in a view-only version of the classification nodes so that they may
	 * be used on the GWT client side.
	 * 
	 * @param guid
	 * 			global unique identifier
	 * 
	 * @return classification nodes with the given guid
	 * 
	 */
	// TODO: no classification node filter, how todo?
	public static ViewClassificationNodes getClassificationNodes(String serverUrl,
			RegistryQuery<ExtrinsicFilter> query, Integer start,
			Integer numResults) {
		RegistryClient client = getRegistry(serverUrl);

		PagedResponse<ClassificationNode> pagedResp = null;
		try {		
			pagedResp = client.getObjects(start, numResults, ClassificationNode.class);
		} catch (RegistryServiceException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		ViewClassificationNodes nodes = respToViewClassificationNodes(pagedResp);

		return nodes;
	}
	
	public static List<ViewAssociation> getAssociations(String serverUrl,
			final RegistryQuery<AssociationFilter> query) {
		return getAssociations(serverUrl, query, null, null);

	}

	/**
	 * Retrieve associations from remote registry service. Note that this wraps the
	 * returned results in a view-only version of the associations so that they may
	 * be used on the GWT client side.
	 * 
	 * @param query
	 *            a query object containing filter and search params for
	 *            retrieving associations
	 * @param start
	 *            start index for retrieved results. Defaults to 1.
	 * @param numResults
	 *            number of results to return. Defaults to 20.
	 * 
	 * @return given number of associations from the given start with the given
	 *         filter conditions
	 * 
	 */
	public static ViewAssociations getAssociations(String serverUrl,
			RegistryQuery<AssociationFilter> query, Integer start,
			Integer numResults) {
		RegistryClient client = getRegistry(serverUrl);

		PagedResponse<Association> pagedResp = null;
		try {
			pagedResp = client.getAssociations(query, start, numResults);
		} catch (RegistryServiceException e) {
			System.out.println(e.getMessage());
		}

		ViewAssociations viewAssociations = respToViewAssociation(pagedResp);

		return viewAssociations;
	}

	// TODO: determine the response format, returns errors for duplicate id for
	// instance
	public static void postArtifacts() {
		// stub
	}

	// Creates a new revision of an artifact in the registry. Follows the same
	// procedures as publishing with the caveat that the logical identifier this
	// artifact carries should already exist in the registry (412 Precondition
	// Failed).
	public static void postRevision() {
		// stub
	}

	/**
	 * Get instance of RegistryClient to work with REST interface of registry.
	 * Instantiates if necessary.
	 */
	/*
	public static RegistryClient getRegistry() {
		String serviceEndpoint = null;
		// TODO: determine why the below does not work
		if (false) {
			try {
				// Get a handle to the JNDI environment naming context
				Context env = (Context) new InitialContext().lookup("java:comp/env"); //$NON-NLS-1$

				// get value from context
				serviceEndpoint = (String) env.lookup("serviceEndpoint"); //$NON-NLS-1$
			} catch (NamingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (true) {
			// TODO: remove this when the above is working
			Properties props = getProperties();
			if (props != null) {
				serviceEndpoint = props.getProperty(
						"service.endpoint", DEFAULT_SERVICE_ENDPOINT); //$NON-NLS-1$
				
				String[] endpoints = serviceEndpoint.split(",");
				serviceEndpoint = endpoints[0];
			} else {
				serviceEndpoint = DEFAULT_SERVICE_ENDPOINT;
			}
		}

		// if unable to get endpoint from context, use the default
		if (serviceEndpoint == null) {
			serviceEndpoint = DEFAULT_SERVICE_ENDPOINT;
		}

		// create an instance of the registry client
		try {
			//RegistryClient client = new RegistryClientString baseUrl, SecurityContext securityContext,
		    // String username, String password)
			
			RegistryClient client = new RegistryClient(serviceEndpoint);
			client.setMediaType(MediaType.APPLICATION_XML);
			return client;
		} catch (RegistryClientException e) {
			System.out.println(e.getMessage());
			return null;
		}
	}
*/
	/**
	 * Get instance of RegistryClient to work with REST interface of registry.
	 * Instantiates if necessary.
	 */
	public static RegistryClient getRegistry(String serverUrl) {
		String serviceEndpoint = null;
		// if unable to get endpoint from user, use the default
		if (serverUrl == null) 
			serviceEndpoint = DEFAULT_SERVICE_ENDPOINT;
		else 
			serviceEndpoint = serverUrl;
		
		// create an instance of the registry client
		try {
			RegistryClient client = new RegistryClient(serviceEndpoint);
			client.setMediaType(MediaType.APPLICATION_XML);
			return client;
		} catch (RegistryClientException e) {
			System.out.println(e.getMessage());
			return null;
		}
	}
	
	/**
	 * Get an instance of the application properties.
	 */
	@SuppressWarnings("nls")
	public static Properties getProperties() {
		if (props == null) {
			final InputStream is = ConnectionManager.class
					.getResourceAsStream("/gov/nasa/pds/registry/ui/client/" + PROPS_NAME);

			try {
				props = new Properties();
				props.load(is);
				is.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return props;
	}

	private static ViewProducts respToViewProducts(
			final PagedResponse<ExtrinsicObject> pagedResp) {
		ViewProducts viewProducts = new ViewProducts();
		viewProducts.setStart(pagedResp.getStart());
		viewProducts.setSize(pagedResp.getNumFound());
		List<ExtrinsicObject> products = pagedResp.getResults();
		for (RegistryObject product : products) {
			ViewProduct vProduct = new ViewProduct();
			viewProducts.add(vProduct);
			vProduct.setGuid(product.getGuid());
			vProduct.setHome(product.getHome());
			vProduct.setLid(product.getLid());
			vProduct.setObjectType(product.getObjectType());
			if (product.getStatus() == null) {
				vProduct.setStatus("Unknown");
			} else {
				vProduct.setStatus(product.getStatus().toString());
			}
			vProduct.setName(product.getName());
			vProduct.setVersionName(product.getVersionName());

			List<ViewSlot> vslots = new ArrayList<ViewSlot>();
			vProduct.setSlots(vslots);
			Set<Slot> slots = product.getSlots();
			if (slots != null) {
				for (Slot slot : slots) {
					ViewSlot vslot = new ViewSlot();
					vslots.add(vslot);
					vslot.setId(String.valueOf(slot.getId()));
					vslot.setName(slot.getName());
					vslot.setValues(slot.getValues());
					//if (slot.getSlotType()!=null)
					vslot.setSlotType(slot.getSlotType());
				}
			}
		}
		return viewProducts;
	}

	private static ViewProduct respToViewProduct(final ExtrinsicObject obj) {
		if (obj == null)
			return null;

		ViewProduct vProduct = new ViewProduct();
		vProduct.setGuid(obj.getGuid());
		vProduct.setHome(obj.getHome());
		vProduct.setLid(obj.getLid());
		vProduct.setObjectType(obj.getObjectType());
		if (obj.getStatus() == null) {
			vProduct.setStatus("Unknown");
		} else {
			vProduct.setStatus(obj.getStatus().toString());
		}
		vProduct.setName(obj.getName());
		vProduct.setVersionName(obj.getVersionName());

		List<ViewSlot> vslots = new ArrayList<ViewSlot>();
		vProduct.setSlots(vslots);
		Set<Slot> slots = obj.getSlots();
		if (slots != null) {
			for (Slot slot : slots) {
				ViewSlot vslot = new ViewSlot();
				vslots.add(vslot);
				vslot.setId(String.valueOf(slot.getId()));
				vslot.setName(slot.getName());
				vslot.setValues(slot.getValues());
				vslot.setSlotType(slot.getSlotType());
			}
		}
		return vProduct;
	}

	private static ViewAssociations respToViewAssociation(
			final PagedResponse<Association> pagedResp) {

		ViewAssociations viewAssociations = new ViewAssociations();
		viewAssociations.setStart(pagedResp.getStart());
		viewAssociations.setSize(pagedResp.getNumFound());

		List<Association> associations = pagedResp.getResults();
		for (RegistryObject ro : associations) {
			Association association = (Association) ro;
			ViewAssociation vAssociation = new ViewAssociation();
			viewAssociations.add(vAssociation);
			vAssociation.setAssociationType(association.getAssociationType());
			vAssociation.setSourceGuid(association.getSourceObject());
			vAssociation.setTargetGuid(association.getTargetObject());

			vAssociation.setLid(association.getLid());
			vAssociation.setObjectType(association.getObjectType());
			if (association.getStatus() == null) {
				vAssociation.setStatus("Unknown");
			} else {
				vAssociation.setStatus(association.getStatus().toString());
			}
			vAssociation.setName(association.getName());
			vAssociation.setVersionName(association.getVersionName());

			List<ViewSlot> vslots = new ArrayList<ViewSlot>();
			vAssociation.setSlots(vslots);
			Set<Slot> slots = ro.getSlots();
			if (slots != null) {
				for (Slot slot : slots) {
					ViewSlot vslot = new ViewSlot();
					vslots.add(vslot);
					vslot.setId(String.valueOf(slot.getId()));
					vslot.setName(slot.getName());
					vslot.setValues(slot.getValues());
					vslot.setSlotType(slot.getSlotType());
				}
			}

		}
		return viewAssociations;
	}

	private static ViewServices respToViewService(
			final PagedResponse<Service> pagedResp) {
		ViewServices viewServices = new ViewServices();
		viewServices.setStart(pagedResp.getStart());
		viewServices.setSize(pagedResp.getNumFound());

		for (Service service : pagedResp.getResults()) {
			ViewService vService = new ViewService();
			viewServices.add(vService);

			vService.setGuid(service.getGuid());
			vService.setHome(service.getHome());
			vService.setLid(service.getLid());
			vService.setObjectType(service.getObjectType());
			if (service.getStatus() == null) {
				vService.setStatus("Unknown");
			} else {
				vService.setStatus(service.getStatus().toString());
			}
			vService.setName(service.getName());
			vService.setVersionName(service.getVersionName());

			List<ViewSlot> vslots = new ArrayList<ViewSlot>();
			vService.setSlots(vslots);
			Set<Slot> slots = service.getSlots();
			if (slots != null) {
				for (Slot slot : slots) {
					ViewSlot vslot = new ViewSlot();
					vslots.add(vslot);
					vslot.setId(String.valueOf(slot.getId()));
					vslot.setName(slot.getName());
					vslot.setValues(slot.getValues());
					vslot.setSlotType(slot.getSlotType());
				}
			}

			List<ViewServiceBinding> vbindings = new ArrayList<ViewServiceBinding>();
			vService.setServiceBindings(vbindings);

			Set<ServiceBinding> bindings = service.getServiceBindings();
			if (bindings != null) {
				for (ServiceBinding srvBinding : bindings) {
					ViewServiceBinding vServBinding = new ViewServiceBinding();
					vbindings.add(vServBinding);

					vServBinding.setAccessURI(srvBinding.getAccessURI());
					vServBinding.setService(srvBinding.getService());
					vServBinding.setTargetBinding(srvBinding.getTargetBinding());

					List<ViewSpecificationLink> vspeclinks = new ArrayList<ViewSpecificationLink>();
					vServBinding.setSpecificationLinks(vspeclinks);

					Set<SpecificationLink> links = srvBinding
							.getSpecificationLinks();
					if (links != null) {
						for (SpecificationLink link : links) {
							ViewSpecificationLink vlink = new ViewSpecificationLink();
							vspeclinks.add(vlink);

							vlink.setServiceBinding(link.getServiceBinding());
							vlink.setSpecificationObject(link
									.getSpecificationObject());
							vlink.setUsageDescription(link
									.getUsageDescription());
							vlink.setUsageParameters(link.getUsageParameters());
						}
					}
				}
			}
		}
		return viewServices;
	}

	private static ViewRegistryPackages respToViewPackage(
			final PagedResponse<RegistryPackage> pagedResp) {
		ViewRegistryPackages viewPackages = new ViewRegistryPackages();
		viewPackages.setStart(pagedResp.getStart());
		viewPackages.setSize(pagedResp.getNumFound());
		List<RegistryPackage> products = pagedResp.getResults();
		for (RegistryObject product : products) {
			ViewRegistryPackage vProduct = new ViewRegistryPackage();
			viewPackages.add(vProduct);
			vProduct.setGuid(product.getGuid());
			vProduct.setHome(product.getHome());
			vProduct.setLid(product.getLid());
			vProduct.setObjectType(product.getObjectType());
			if (product.getStatus() == null) {
				vProduct.setStatus("Unknown");
			} else {
				vProduct.setStatus(product.getStatus().toString());
			}
			vProduct.setName(product.getName());
			vProduct.setVersionName(product.getVersionName());
			vProduct.setDescription(product.getDescription());

			List<ViewSlot> vslots = new ArrayList<ViewSlot>();
			vProduct.setSlots(vslots);
			Set<Slot> slots = product.getSlots();
			if (slots != null) {
				for (Slot slot : slots) {
					ViewSlot vslot = new ViewSlot();
					vslots.add(vslot);
					vslot.setId(String.valueOf(slot.getId()));
					vslot.setName(slot.getName());
					vslot.setValues(slot.getValues());
					vslot.setSlotType(slot.getSlotType());
				}
			}
		}
		return viewPackages;
	}
	
	private static ViewAuditableEvents respToViewEvents(
			final PagedResponse<AuditableEvent> pagedResp) {
		ViewAuditableEvents viewEvents = new ViewAuditableEvents();
		viewEvents.setStart(pagedResp.getStart());
		viewEvents.setSize(pagedResp.getNumFound());
		List<AuditableEvent> events = pagedResp.getResults();
		for (AuditableEvent product : events) {
			ViewAuditableEvent vEvent = new ViewAuditableEvent();
			viewEvents.add(vEvent);
			vEvent.setGuid(product.getGuid());
			vEvent.setHome(product.getHome());
			vEvent.setLid(product.getLid());
			vEvent.setObjectType(product.getObjectType());
			if (product.getStatus() == null) {
				vEvent.setStatus("Unknown");
			} else {
				vEvent.setStatus(product.getStatus().toString());
			}
			vEvent.setName(product.getName());
			vEvent.setVersionName(product.getVersionName());

			List<ViewSlot> vslots = new ArrayList<ViewSlot>();
			vEvent.setSlots(vslots);
			Set<Slot> slots = product.getSlots();
			if (slots != null) {
				for (Slot slot : slots) {
					ViewSlot vslot = new ViewSlot();
					vslots.add(vslot);
					vslot.setId(String.valueOf(slot.getId()));
					vslot.setName(slot.getName());
					vslot.setValues(slot.getValues());
					vslot.setSlotType(slot.getSlotType());
				}
			}

			vEvent.setEventType(product.getEventType().toString());
			vEvent.setRequestId(product.getRequestId());
			vEvent.setUser(product.getUser());
			vEvent.setAffectedObjects(product.getAffectedObjects());
			vEvent.setTimestamp(product.getTimestamp());
		}
		return viewEvents;
	}

	private static ViewSchemes respToViewSchemes(
			final PagedResponse<ClassificationScheme> pagedResp) {
		ViewSchemes viewSchemes = new ViewSchemes();
		viewSchemes.setStart(pagedResp.getStart());
		viewSchemes.setSize(pagedResp.getNumFound());
		for (ClassificationScheme scheme : pagedResp.getResults()) {
			ViewScheme vScheme = new ViewScheme();
			viewSchemes.add(vScheme);

			vScheme.setGuid(scheme.getGuid());
			vScheme.setHome(scheme.getHome());
			vScheme.setLid(scheme.getLid());
			vScheme.setObjectType(scheme.getObjectType());
			if (scheme.getStatus() == null) {
				vScheme.setStatus("Unknown");
			} else {
				vScheme.setStatus(scheme.getStatus().toString());
			}
			vScheme.setName(scheme.getName());
			vScheme.setVersionName(scheme.getVersionName());
			vScheme.setNodeType(scheme.getNodeType().toString());
			vScheme.setIsInternal(scheme.getIsInternal());

			List<ViewSlot> vslots = new ArrayList<ViewSlot>();
			vScheme.setSlots(vslots);
			Set<Slot> slots = scheme.getSlots();
			if (slots != null) {
				for (Slot slot : slots) {
					ViewSlot vslot = new ViewSlot();
					vslots.add(vslot);
					vslot.setId(String.valueOf(slot.getId()));
					vslot.setName(slot.getName());
					vslot.setValues(slot.getValues());
					vslot.setSlotType(slot.getSlotType());
				}
			}
		}
		return viewSchemes;
	}
	
	private static ViewClassificationNodes respToViewClassificationNodes(
			final PagedResponse<ClassificationNode> pagedResp) {
		ViewClassificationNodes viewClassificationNodes = new ViewClassificationNodes();
		viewClassificationNodes.setStart(pagedResp.getStart());
		viewClassificationNodes.setSize(pagedResp.getNumFound());
		
		for (ClassificationNode classNode : pagedResp.getResults()) {
			ViewClassificationNode vClassificationNode = new ViewClassificationNode();
			viewClassificationNodes.add(vClassificationNode);

			vClassificationNode.setGuid(classNode.getGuid());
			vClassificationNode.setHome(classNode.getHome());
			vClassificationNode.setLid(classNode.getLid());
			vClassificationNode.setObjectType(classNode.getObjectType());
			if (classNode.getStatus() == null) {
				vClassificationNode.setStatus("Unknown");
			} else {
				vClassificationNode.setStatus(classNode.getStatus().toString());
			}
			vClassificationNode.setName(classNode.getName());
			vClassificationNode.setVersionName(classNode.getVersionName());
			
			vClassificationNode.setParent(classNode.getParent());
			vClassificationNode.setCode(classNode.getCode());
			vClassificationNode.setPath(classNode.getPath());			
			
			List<ViewSlot> vslots = new ArrayList<ViewSlot>();
			vClassificationNode.setSlots(vslots);
			Set<Slot> slots = classNode.getSlots();
			if (slots != null) {
				for (Slot slot : slots) {
					ViewSlot vslot = new ViewSlot();
					vslots.add(vslot);
					vslot.setId(String.valueOf(slot.getId()));
					vslot.setName(slot.getName());
					vslot.setValues(slot.getValues());
					vslot.setSlotType(slot.getSlotType());
				}
			}
		}
		return viewClassificationNodes;
	}
}
