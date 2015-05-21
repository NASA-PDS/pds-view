//	Copyright 2009-2010, by the California Institute of Technology.
//	ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
//	Any commercial use must be negotiated with the Office of Technology 
//	Transfer at the California Institute of Technology.
//	
//	This software is subject to U. S. export control laws and regulations 
//	(22 C.F.R. 120-130 and 15 C.F.R. 730-774). To the extent that the software 
//	is subject to U.S. export control laws and regulations, the recipient has 
//	the responsibility to obtain export licenses or other export authority as 
//	may be required before exporting such information to foreign countries or 
//	providing access to foreign nationals.
//	
//	$Id$
//

package gov.nasa.pds.registry.service;

import gov.nasa.pds.registry.client.RegistryClient;
import gov.nasa.pds.registry.exception.ExceptionType;
import gov.nasa.pds.registry.exception.RegistryClientException;
import gov.nasa.pds.registry.exception.RegistryServiceException;
import gov.nasa.pds.registry.model.AffectedInfo;
import gov.nasa.pds.registry.model.Association;
import gov.nasa.pds.registry.model.AuditableEvent;
import gov.nasa.pds.registry.model.ClassificationNode;
import gov.nasa.pds.registry.model.ClassificationScheme;
import gov.nasa.pds.registry.model.EventType;
import gov.nasa.pds.registry.model.ObjectAction;
import gov.nasa.pds.registry.model.ObjectClass;
import gov.nasa.pds.registry.model.ObjectStatus;
import gov.nasa.pds.registry.model.RegistryPackage;
import gov.nasa.pds.registry.model.PagedResponse;
import gov.nasa.pds.registry.model.ExtrinsicObject;
import gov.nasa.pds.registry.model.RegistryObject;
import gov.nasa.pds.registry.model.ReplicationReport;
import gov.nasa.pds.registry.model.ReplicationStatus;
import gov.nasa.pds.registry.model.Service;
import gov.nasa.pds.registry.model.ServiceBinding;
import gov.nasa.pds.registry.model.Slot;
import gov.nasa.pds.registry.model.SpecificationLink;
import gov.nasa.pds.registry.model.Report;
import gov.nasa.pds.registry.model.naming.IdentifierGenerator;
import gov.nasa.pds.registry.model.naming.Versioner;
import gov.nasa.pds.registry.query.AssociationFilter;
import gov.nasa.pds.registry.query.EventFilter;
import gov.nasa.pds.registry.query.ExtrinsicFilter;
import gov.nasa.pds.registry.query.ObjectFilter;
import gov.nasa.pds.registry.query.PackageFilter;
import gov.nasa.pds.registry.query.QueryOperator;
import gov.nasa.pds.registry.query.RegistryQuery;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.NoResultException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * This class contains all the logic for publishing, versioning, updating, and
 * deleting registry objects. The registry aims to support the registry portion
 * of the CCSDS regrep specification at the same time it leverages the much of
 * the ebXML information model.
 * 
 * @author pramirez
 * 
 */
@org.springframework.stereotype.Service("registryService")
public class RegistryServiceImpl implements RegistryService {

	@Autowired
	MetadataStore metadataStore;

	@Autowired
	@Qualifier("versioner")
	Versioner versioner;

	@Autowired
	@Qualifier("idGenerator")
	IdentifierGenerator idGenerator;

	@Autowired
	@Qualifier("report")
	Report report;

	private ReplicationReport replicationReport = null;

	private static List<String> CORE_OBJECT_TYPES = Arrays.asList("Association",
	    "AuditableEvent", "Classification", "ClassificationNode",
	    "ClassificationScheme", "ExtrinsicObject", "Service", "ServiceBinding",
	    "SpecificationLink", "RegistryPackage", "ExternalIdentifier");

	private static String OBJECT_TYPE_SCHEME = "urn:registry:classificationScheme:ObjectType";

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat(
	    "yyyy-MM-dd'T'HH:mm:ss");

	private HashMap<String, ClassificationNode> objectTypeCache = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gov.nasa.pds.registry.service.RegistryService#setMetadataStore(gov.nasa
	 * .pds.registry.service.MetadataStore)
	 */
	public void setMetadataStore(MetadataStore metadataStore) {
		this.metadataStore = metadataStore;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.nasa.pds.registry.service.RegistryService#getMetadataStore()
	 */
	public MetadataStore getMetadataStore() {
		return metadataStore;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gov.nasa.pds.registry.service.RegistryService#setVersioner(gov.nasa.pds
	 * .registry.model.naming.Versioner)
	 */
	public void setVersioner(Versioner versioner) {
		this.versioner = versioner;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.nasa.pds.registry.service.RegistryService#getVersioner()
	 */
	public Versioner getVersioner() {
		return this.versioner;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gov.nasa.pds.registry.service.RegistryService#setIdentifierGenerator(gov
	 * .nasa.pds.registry.model.naming.IdentifierGenerator)
	 */
	public void setIdentifierGenerator(IdentifierGenerator idGenerator) {
		this.idGenerator = idGenerator;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.nasa.pds.registry.service.RegistryService#getIdentifierGenerator()
	 */
	public IdentifierGenerator getIdentifierGenerator() {
		return this.idGenerator;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gov.nasa.pds.registry.service.RegistryService#getExtrinsics(java.lang.Integer
	 * , java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	public PagedResponse<ExtrinsicObject> getExtrinsics(Integer start,
	    Integer rows) {
		PagedResponse<ExtrinsicObject> page = new PagedResponse<ExtrinsicObject>(
		    start, metadataStore.getNumRegistryObjects(ExtrinsicObject.class),
		    (List<ExtrinsicObject>) metadataStore.getRegistryObjects(start, rows,
		        ExtrinsicObject.class));
		return page;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gov.nasa.pds.registry.service.RegistryService#getExtrinsics(gov.nasa.pds
	 * .registry.query.RegistryQuery)
	 */
	public PagedResponse<ExtrinsicObject> getExtrinsics(
	    RegistryQuery<ExtrinsicFilter> query) {
		return this.getExtrinsics(query, 1, 20);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gov.nasa.pds.registry.service.RegistryService#getExtrinsics(gov.nasa.pds
	 * .registry.query.RegistryQuery, java.lang.Integer, java.lang.Integer)
	 */
	public PagedResponse<ExtrinsicObject> getExtrinsics(
	    RegistryQuery<ExtrinsicFilter> query, Integer start, Integer rows) {
		if (start <= 0) {
			start = 1;
		}
		return metadataStore.getExtrinsics(query, start, rows);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.nasa.pds.registry.service.RegistryService#getReport()
	 */
	public Report getReport() {
		Report report = new Report(this.report);
		report.setAssociations(metadataStore
		    .getNumRegistryObjects(Association.class));
		report.setExtrinsics(metadataStore
		    .getNumRegistryObjects(ExtrinsicObject.class));
		report.setServices(metadataStore.getNumRegistryObjects(Service.class));
		report.setClassificationNodes(metadataStore
		    .getNumRegistryObjects(ClassificationNode.class));
		report.setClassificationSchemes(metadataStore
		    .getNumRegistryObjects(ClassificationScheme.class));
		report.setPackages(metadataStore
		    .getNumRegistryObjects(RegistryPackage.class));
		report.setEvents(metadataStore
			    .getNumRegistryObjects(AuditableEvent.class));
		//report.setHome(metadataStore.getHome());
		
		return report;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.nasa.pds.registry.service.RegistryService#versionObject(java
	 * .lang.String, gov.nasa.pds.registry.model.RegistryObject, boolean)
	 */
	public String versionObject(String user, RegistryObject registryObject,
	    boolean major) throws RegistryServiceException {
		return versionObject(user, registryObject, major, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gov.nasa.pds.registry.service.RegistryService#versionObject(java.lang.String
	 * , gov.nasa.pds.registry.model.RegistryObject, boolean, java.lang.String)
	 */
	@Override
	public String versionObject(String user, RegistryObject registryObject,
	    boolean major, String packageId) throws RegistryServiceException {
		RegistryObject referencedObject = this.getLatestObject(
		    registryObject.getLid(), registryObject.getClass());
		if (registryObject.getGuid() == null) {
			registryObject.setGuid(idGenerator.getGuid());
		}
		if (registryObject.getHome() == null) {
			registryObject.setHome(idGenerator.getHome());
		}
		registryObject.setVersionName(versioner.getNextVersion(
		    referencedObject.getVersionName(), major));
		registryObject.setStatus(referencedObject.getStatus());
		// Make sure slots have no id associated with them.
		Set<Slot> newSlots = new HashSet<Slot>();
		for (Slot slot : registryObject.getSlots()) {
			Slot newSlot = new Slot(slot.getName(), slot.getValues());
			newSlot.setSlotType(slot.getSlotType());
			newSlots.add(newSlot);
		}
		registryObject.setSlots(newSlots);
		this.validateObject(registryObject);
		// Constraints can throw a persistence exception
		try {
			metadataStore.saveRegistryObject(registryObject);
		} catch (javax.persistence.EntityExistsException ee) {
			//ee.printStackTrace();
			throw new RegistryServiceException("Registry object with logical id "
			    + registryObject.getLid() + " and version name "
			    + registryObject.getVersionName() + " already exists.\n",
			    ExceptionType.EXISTING_OBJECT);
		} catch (javax.persistence.PersistenceException pe) {
		    String errorMsg = "";
		    if (pe.getCause().getCause()!=null)
		    	errorMsg = pe.getCause().getCause().getMessage();
		    else { 
		    	if (pe.getCause()!=null)
		    		errorMsg = pe.getCause().getMessage();
		    	else 
		    		errorMsg = pe.getMessage();
		    }
			throw new RegistryServiceException("Error with the registry object with logical id "
				    + registryObject.getLid() + " and version name "
				    + registryObject.getVersionName() + ". " + errorMsg + "\n",
				    ExceptionType.INVALID_REQUEST);	
		}
		
		this.createAuditableEvent("versionObject " + referencedObject.getGuid()
		    + " " + registryObject.getGuid(), user, EventType.Versioned,
		    registryObject.getGuid(), registryObject.getClass());

		if (packageId == null) {
			this.createAuditableEvent("versionObject " + registryObject.getGuid(),
			    user, EventType.Created, registryObject.getGuid(),
			    registryObject.getClass());
		} else {
			Association hasMember = new Association();
			hasMember.setGuid(idGenerator.getGuid());
			hasMember.setHome(idGenerator.getHome());
			hasMember.setSourceObject(packageId);
			hasMember.setTargetObject(registryObject.getGuid());
			hasMember.setStatus(ObjectStatus.Submitted);
			hasMember.setAssociationType("urn:registry:AssociationType:HasMember");
			Set<Slot> slots = new HashSet<Slot>();
			Slot targetObjectType = new Slot("targetObjectType",
			    Arrays.asList(registryObject.getClass().getSimpleName()));
			slots.add(targetObjectType);
			hasMember.setSlots(slots);
			metadataStore.saveRegistryObject(hasMember);
			this.createAuditableEvent(
			    "versionObjectWithPackage " + packageId + " "
			        + registryObject.getGuid(),
			    user,
			    EventType.Created,
			    new AffectedInfo(Arrays.asList(registryObject.getGuid(),
			        hasMember.getGuid()), Arrays.asList(registryObject.getClass()
			        .getSimpleName(), hasMember.getClass().getSimpleName())));
		}
		return registryObject.getGuid();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gov.nasa.pds.registry.service.RegistryService#getLatestObject(java.lang
	 * .String, java.lang.Class)
	 */
	public RegistryObject getLatestObject(String lid,
	    Class<? extends RegistryObject> objectClass)
	    throws RegistryServiceException {
		try {
			List<? extends RegistryObject> objects = metadataStore
			    .getRegistryObjectVersions(lid, objectClass);
			Collections.sort(objects, versioner.getComparator());
			return objects.get(objects.size() - 1);
		} catch (NoResultException nre) {
			throw new RegistryServiceException("No object found for lid: " + lid,
			    ExceptionType.OBJECT_NOT_FOUND);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gov.nasa.pds.registry.service.RegistryService#getEarliestObject(java.lang
	 * .String, java.lang.Class)
	 */
	public RegistryObject getEarliestObject(String lid,
	    Class<? extends RegistryObject> objectClass)
	    throws RegistryServiceException {
		try {
			List<? extends RegistryObject> objects = metadataStore
			    .getRegistryObjectVersions(lid, objectClass);
			Collections.sort(objects, versioner.getComparator());
			return (ExtrinsicObject) objects.get(0);
		} catch (NoResultException nre) {
			throw new RegistryServiceException("No object found for lid: " + lid,
			    ExceptionType.OBJECT_NOT_FOUND);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gov.nasa.pds.registry.service.RegistryService#getNextObject(java.lang.String
	 * , java.lang.String, java.lang.Class)
	 */
	public RegistryObject getNextObject(String lid, String versionName,
	    Class<? extends RegistryObject> objectClass)
	    throws RegistryServiceException {
		// TODO: Make this method throw an exception if the lid or version is not
		// found
		List<? extends RegistryObject> objects = null;
		try {
			objects = metadataStore.getRegistryObjectVersions(lid, objectClass);
		} catch (NoResultException nre) {
			throw new RegistryServiceException("No object found for lid: " + lid,
			    ExceptionType.OBJECT_NOT_FOUND);
		}
		Collections.sort(objects, versioner.getComparator());
		for (int i = 0; i < objects.size(); i++) {
			RegistryObject object = objects.get(i);
			if (versionName.equals(object.getVersionName())) {
				if (i < objects.size() - 1) {
					return (ExtrinsicObject) objects.get(i + 1);
				} else {
					return null;
				}
			}
		}
		throw new RegistryServiceException("No object found for lid: " + lid
		    + " versionName: " + versionName, ExceptionType.OBJECT_NOT_FOUND);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gov.nasa.pds.registry.service.RegistryService#getPreviousObject(java.lang
	 * .String, java.lang.String, java.lang.Class)
	 */
	public RegistryObject getPreviousObject(String lid, String versionName,
	    Class<? extends RegistryObject> objectClass)
	    throws RegistryServiceException {
		List<? extends RegistryObject> objects = null;
		try {
			objects = metadataStore.getRegistryObjectVersions(lid, objectClass);
		} catch (NoResultException nre) {
			throw new RegistryServiceException("No object found for lid: " + lid,
			    ExceptionType.OBJECT_NOT_FOUND);
		}
		Collections.sort(objects, versioner.getComparator());
		for (int i = 0; i < objects.size(); i++) {
			RegistryObject object = objects.get(i);
			if (versionName.equals(object.getVersionName())) {
				if (i > 0) {
					return objects.get(i - 1);
				} else {
					return null;
				}
			}
		}
		throw new RegistryServiceException("No object found for lid: " + lid
		    + " versionName: " + versionName, ExceptionType.OBJECT_NOT_FOUND);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gov.nasa.pds.registry.service.RegistryService#getObjectVersions(java.lang
	 * .String, java.lang.Class)
	 */
	public List<RegistryObject> getObjectVersions(String lid,
	    Class<? extends RegistryObject> objectClass)
	    throws RegistryServiceException {
		try {
			return new ArrayList<RegistryObject>(
			    metadataStore.getRegistryObjectVersions(lid, objectClass));
		} catch (NoResultException nre) {
			throw new RegistryServiceException("No object found for lid: " + lid,
			    ExceptionType.OBJECT_NOT_FOUND);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gov.nasa.pds.registry.service.RegistryService#getObject(java.lang.String,
	 * java.lang.String, java.lang.Class)
	 */
	public RegistryObject getObject(String lid, String versionName,
	    Class<? extends RegistryObject> objectClass) {
		return metadataStore.getRegistryObject(lid, versionName, objectClass);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gov.nasa.pds.registry.service.RegistryService#getClassificationNodes(java
	 * .lang.String)
	 */
	public List<ClassificationNode> getClassificationNodes(String scheme) {
		ArrayList<ClassificationNode> nodes = new ArrayList<ClassificationNode>();
		nodes.addAll(metadataStore.getClassificationNodes(scheme));
		return nodes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.nasa.pds.registry.service.RegistryService#changeObjectStatus
	 * (java.lang.String, java.lang.String,
	 * gov.nasa.pds.registry.model.ObjectAction, java.lang.Class)
	 */
	public void changeObjectStatus(String user, String guid, ObjectAction action,
	    Class<? extends RegistryObject> objectClass) {
		this.createAuditableEvent("changeObjectStatus " + guid, user,
		    action.getEventType(),
		    this.changeObjectStatusById(user, guid, action, objectClass));
	}

	private AffectedInfo changeObjectStatusById(String user, String guid,
	    ObjectAction action, Class<? extends RegistryObject> objectClass) {
		// TODO: Consider whether a status update should apply to nested objects
		RegistryObject registryObject = metadataStore.getRegistryObject(guid,
		    objectClass);
		registryObject.setStatus(action.getObjectStatus());
		metadataStore.updateRegistryObject(registryObject);
		return new AffectedInfo(Arrays.asList(guid), Arrays.asList(objectClass
		    .getSimpleName()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.nasa.pds.registry.service.RegistryService#updateObject(java
	 * .lang.String, gov.nasa.pds.registry.model.RegistryObject)
	 */
	public void updateObject(String user, RegistryObject registryObject)
	    throws RegistryServiceException {
		// First grab the object that will be updated
		RegistryObject foundObject = this.getObject(registryObject.getGuid(),
		    registryObject.getClass());
		// Home was originally set by the registry so if not set by a client it
		// should be preserved
		if (registryObject.getHome() == null) {
			registryObject.setHome(foundObject.getHome());
		}
		// If the registry object status is not set it should use what is already in
		// the registry
		if (registryObject.getStatus() == null) {
			registryObject.setStatus(foundObject.getStatus());
		}
		// If the client has not specified a lid then it should preserve the one in
		// the registry
		if (registryObject.getLid() == null) {
			registryObject.setLid(foundObject.getLid());
		}
		// version name is controlled by the registry and should not be changed by
		// the client
		registryObject.setVersionName(foundObject.getVersionName());
		metadataStore.updateRegistryObject(registryObject);
		// Record that a registry object has been updated
		this.createAuditableEvent("updateObject " + registryObject.getGuid(), user,
		    EventType.Updated, registryObject.getGuid(), registryObject.getClass());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gov.nasa.pds.registry.service.RegistryService#getAssociations(gov.nasa.
	 * pds.registry.query.RegistryQuery, java.lang.Integer, java.lang.Integer)
	 */
	public PagedResponse<Association> getAssociations(
	    RegistryQuery<AssociationFilter> query, Integer start, Integer rows) {
		if (start <= 0) {
			start = 1;
		}
		return metadataStore.getAssociations(query, start, rows);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gov.nasa.pds.registry.service.RegistryService#getObjects(gov.nasa.pds.registry
	 * .query.RegistryQuery, java.lang.Integer, java.lang.Integer,
	 * java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	public PagedResponse<Association> getObjects(
	    RegistryQuery<ObjectFilter> query, Integer start, Integer rows,
	    Class<? extends RegistryObject> objectClass) {
		if (start <= 0) {
			start = 1;
		}
		return (PagedResponse<Association>) metadataStore.getRegistryObjects(query,
		    start, rows, objectClass);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gov.nasa.pds.registry.service.RegistryService#getPackages(gov.nasa.pds.registry
	 * .query.RegistryQuery, java.lang.Integer, java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	public PagedResponse<RegistryPackage> getPackages(
	    RegistryQuery<PackageFilter> query, Integer start, Integer rows) {
		if (start <= 0) {
			start = 1;
		}
		return metadataStore.getPackages(query, start, rows);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gov.nasa.pds.registry.service.RegistryService#getAuditableEvents(java.lang
	 * .String)
	 */
	public PagedResponse<AuditableEvent> getAuditableEvents(String affectedObject) {
		PagedResponse<AuditableEvent> response = new PagedResponse<AuditableEvent>();
		response.setResults(metadataStore.getAuditableEvents(affectedObject));
		return response;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gov.nasa.pds.registry.service.RegistryService#publishObject(java.lang.String
	 * , gov.nasa.pds.registry.model.RegistryObject, java.lang.String)
	 */
	@Override
	public String publishObject(String user, RegistryObject registryObject,
	    String packageId) throws RegistryServiceException {
		if (registryObject.getGuid() == null) {
			registryObject.setGuid(idGenerator.getGuid());
		}
		if (registryObject.getHome() == null) {
			registryObject.setHome(idGenerator.getHome());
		}
		registryObject.setVersionName(versioner.getInitialVersion());
		if (registryObject.getLid() == null) {
			registryObject.setLid(idGenerator.getGuid());
		}
		registryObject.setStatus(ObjectStatus.Submitted);
		this.validateObject(registryObject);

		// Constraints can throw a persistence exception
		try {
			metadataStore.saveRegistryObject(registryObject);
		} catch (javax.persistence.EntityExistsException ee) {
			//ee.printStackTrace();
			throw new RegistryServiceException("Registry object with logical id "
			    + registryObject.getLid() + " and version name "
			    + registryObject.getVersionName() + " already exists.\n",
			    ExceptionType.EXISTING_OBJECT);
		} catch (javax.persistence.PersistenceException pe) {
		    String errorMsg = "";
		    if (pe.getCause().getCause()!=null)
		    	errorMsg = pe.getCause().getCause().getMessage();
		    else { 
		    	if (pe.getCause()!=null)
		    		errorMsg = pe.getCause().getMessage();
		    	else 
		    		errorMsg = pe.getMessage();
		    }
			throw new RegistryServiceException("Error with the registry object with logical id "
				    + registryObject.getLid() + " and version name "
				    + registryObject.getVersionName() + ". " + errorMsg + "\n",
				    ExceptionType.INVALID_REQUEST);	
		}

		// If this is a classification node that belongs to the object type
		// classification scheme add it to the cache
		if (registryObject instanceof ClassificationNode) {
			ClassificationNode node = (ClassificationNode) registryObject;
			node.getPath().contains(OBJECT_TYPE_SCHEME);
			objectTypeCache.put(node.getCode(), node);
		}

		if (packageId == null) {
			this.createAuditableEvent("publishObject " + registryObject.getGuid(),
			    user, EventType.Created, registryObject.getGuid(),
			    registryObject.getClass());
		} else {
			Association hasMember = new Association();
			hasMember.setGuid(idGenerator.getGuid());
			hasMember.setHome(idGenerator.getHome());
			hasMember.setSourceObject(packageId);
			hasMember.setTargetObject(registryObject.getGuid());
			hasMember.setStatus(ObjectStatus.Submitted);
			hasMember.setAssociationType("urn:registry:AssociationType:HasMember");
			Set<Slot> slots = new HashSet<Slot>();
			Slot targetObjectType = new Slot("targetObjectType",
			    Arrays.asList(registryObject.getClass().getSimpleName()));
			slots.add(targetObjectType);
			hasMember.setSlots(slots);
			metadataStore.saveRegistryObject(hasMember);
			this.createAuditableEvent(
			    "publishObjectWithPackage " + packageId + " "
			        + registryObject.getGuid(),
			    user,
			    EventType.Created,
			    new AffectedInfo(Arrays.asList(registryObject.getGuid(),
			        hasMember.getGuid()), Arrays.asList(registryObject.getClass()
			        .getSimpleName(), hasMember.getClass().getSimpleName())));
		}
		return registryObject.getGuid();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gov.nasa.pds.registry.service.RegistryService#publishObjects(java.lang.String
	 * , gov.nasa.pds.registry.model.RegistryObjectList, java.lang.String)
	 */
	@Override
	public void publishObjects(String user, List<? extends RegistryObject> registryObjects, String packageId) 
			throws RegistryServiceException {		
		List<String> addedObjs = new ArrayList<String>();
		List<RegistryObject> needToStoreObjs = new ArrayList<RegistryObject>();
		for (RegistryObject registryObject: registryObjects) {
			if (registryObject.getGuid() == null) {
				registryObject.setGuid(idGenerator.getGuid());
			}
			if (registryObject.getHome() == null) {
				registryObject.setHome(idGenerator.getHome());
			}
			registryObject.setVersionName(versioner.getInitialVersion());
			if (registryObject.getLid() == null) {
				registryObject.setLid(idGenerator.getGuid());
			}
			registryObject.setStatus(ObjectStatus.Submitted);
			this.validateObject(registryObject);
			needToStoreObjs.add(registryObject);

			// If this is a classification node that belongs to the object type
			// classification scheme add it to the cache
			if (registryObject instanceof ClassificationNode) {
				ClassificationNode node = (ClassificationNode) registryObject;
				node.getPath().contains(OBJECT_TYPE_SCHEME);
				objectTypeCache.put(node.getCode(), node);
			}

			if (packageId == null) {
				needToStoreObjs.add(createAuditableEventObj("publishObject " + registryObject.getGuid(),
						user, EventType.Created, registryObject.getGuid(),
						registryObject.getClass()));
			} else {
				Association hasMember = new Association();
				hasMember.setGuid(idGenerator.getGuid());
				hasMember.setHome(idGenerator.getHome());
				hasMember.setSourceObject(packageId);
				hasMember.setTargetObject(registryObject.getGuid());
				hasMember.setStatus(ObjectStatus.Submitted);
				hasMember.setAssociationType("urn:registry:AssociationType:HasMember");
				Set<Slot> slots = new HashSet<Slot>();
				Slot targetObjectType = new Slot("targetObjectType",
						Arrays.asList(registryObject.getClass().getSimpleName()));
				slots.add(targetObjectType);
				hasMember.setSlots(slots);
				needToStoreObjs.add(hasMember);
				needToStoreObjs.add(createAuditableEventObj(
						"publishObjectWithPackage " + packageId + " "
								+ registryObject.getGuid(),
								user,
								EventType.Created,
								new AffectedInfo(Arrays.asList(registryObject.getGuid(),
										hasMember.getGuid()), Arrays.asList(registryObject.getClass()
												.getSimpleName(), hasMember.getClass().getSimpleName()))));
			}

			addedObjs.add(registryObject.getGuid());
		}
		metadataStore.saveRegistryObjects(needToStoreObjs);		
		System.out.println("needToStoreObjs.size() = " + needToStoreObjs.size() + "   needToStoreObjs + "
				+ "\naddedObjs.size() = " + addedObjs.size() + "    addedObjs = " + addedObjs);		//return addedObjs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.nasa.pds.registry.service.RegistryService#publishObject(java
	 * .lang.String, gov.nasa.pds.registry.model.RegistryObject)
	 */
	public String publishObject(String user, RegistryObject registryObject)
	    throws RegistryServiceException {
		return this.publishObject(user, registryObject, null);
	}
	
	public void publishObjects(String user, List<? extends RegistryObject> registryObjects)
		    throws RegistryServiceException {
			this.publishObjects(user, registryObjects, null);
		}

	private void createAuditableEvent(String requestId, String user,
	    EventType eventType, String guid,
	    Class<? extends RegistryObject> objectClass) {
		this.createAuditableEvent(requestId, user, eventType, new AffectedInfo(
		    Arrays.asList(guid), Arrays.asList(objectClass.getSimpleName())));
	}

	private void createAuditableEvent(String requestId, String user,
	    EventType eventType, AffectedInfo affectedInfo) {
		AuditableEvent event = new AuditableEvent(eventType,
		    affectedInfo.getAffectedIds(), user);
		event.setGuid(idGenerator.getGuid());
		event.setHome(idGenerator.getHome());
		event.setRequestId(requestId);
		event.addSlot(new Slot("affectedObjectTypes", affectedInfo
		    .getAffectedTypes()));
		metadataStore.saveRegistryObject(event);
	}
	
	private AuditableEvent createAuditableEventObj(String requestId, String user,
			EventType eventType, String guid,
			Class<? extends RegistryObject> objectClass) {
		return this.createAuditableEventObj(requestId, user, eventType, new AffectedInfo(
				Arrays.asList(guid), Arrays.asList(objectClass.getSimpleName())));
	}

	private AuditableEvent createAuditableEventObj(String requestId, String user,
			EventType eventType, AffectedInfo affectedInfo) {
		AuditableEvent event = new AuditableEvent(eventType,
				affectedInfo.getAffectedIds(), user);
		event.setGuid(idGenerator.getGuid());
		event.setHome(idGenerator.getHome());
		event.setRequestId(requestId);
		event.addSlot(new Slot("affectedObjectTypes", affectedInfo
				.getAffectedTypes()));
		//metadataStore.saveRegistryObject(event);
		return event;
	}

	private void validateObject(RegistryObject registryObject)
	    throws RegistryServiceException {
		// Check object type
		if (!validObjectType(registryObject.getObjectType())) {
			throw new RegistryServiceException("Not a valid object type \""
			    + registryObject.getObjectType() + "\"",
			    ExceptionType.INVALID_REQUEST);
		}
		// Run type specific validation
		if (registryObject instanceof ExtrinsicObject) {
			this.validateExtrinsic((ExtrinsicObject) registryObject);
		} else if (registryObject instanceof Association) {
			this.validateAssociation((Association) registryObject);
		} else if (registryObject instanceof ClassificationNode) {
			this.validateNode((ClassificationNode) registryObject);
		} else if (registryObject instanceof Service) {
			this.validateService((Service) registryObject);
		}
	}

	private synchronized void loadObjectTypes() {
		if (objectTypeCache == null) {
			objectTypeCache = new HashMap<String, ClassificationNode>();
			List<ClassificationNode> nodes = metadataStore
			    .getClassificationNodes(OBJECT_TYPE_SCHEME);
			for (ClassificationNode node : nodes) {
				objectTypeCache.put(node.getCode(), node);
			}
		}
	}

	private boolean validObjectType(String objectType) {
		loadObjectTypes();
		if (objectTypeCache.containsKey(objectType)
		    || CORE_OBJECT_TYPES.contains(objectType)) {
			return true;
		}
		return false;
	}

	private void validateExtrinsic(ExtrinsicObject object) {
		// Nothing to validate at this point
	}

	private void validateAssociation(Association association) {
		// TODO: Validate that the associationType exist within the classification
		// scheme for associations
	}

	private void validateNode(ClassificationNode node)
	    throws RegistryServiceException {
		StringBuffer path = new StringBuffer(node.getCode());
		path.insert(0, "/");
		boolean done = false;
		ClassificationNode currentNode = node;
		while (!done) {
			boolean nodeParentFound = false;
			try {
				currentNode = (ClassificationNode) this.getObject(
				    currentNode.getParent(), ClassificationNode.class);
				path.insert(0, currentNode.getCode());
				path.insert(0, "/");
				nodeParentFound = true;
			} catch (RegistryServiceException rse) {
				// Suppress as it could be the parent is a classification scheme
			}
			if (!nodeParentFound) {
				ClassificationScheme parent = (ClassificationScheme) this.getObject(
				    currentNode.getParent(), ClassificationScheme.class);
				path.insert(0, parent.getGuid());
				path.insert(0, "/");
				done = true;
			}
		}
		node.setPath(path.toString());
	}

	private void validateService(Service service) {
		for (ServiceBinding binding : service.getServiceBindings()) {
			if (binding.getGuid() == null) {
				binding.setGuid(idGenerator.getGuid());
			}
			if (binding.getGuid() == null) {
				binding.setHome(idGenerator.getHome());
			}
			if (binding.getService() == null) {
				binding.setService(service.getGuid());
			}
			for (SpecificationLink link : binding.getSpecificationLinks()) {
				if (link.getGuid() == null) {
					link.setGuid(idGenerator.getGuid());
				}
				if (link.getHome() == null) {
					link.setHome(idGenerator.getHome());
				}
				if (link.getServiceBinding() == null) {
					link.setServiceBinding(binding.getGuid());
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.nasa.pds.registry.service.RegistryService#deleteObject(java
	 * .lang.String, java.lang.String, java.lang.String, java.lang.Class)
	 */
	public void deleteObject(String user, String lid, String versionId,
	    Class<? extends RegistryObject> objectClass) {
		RegistryObject registryObject = metadataStore.getRegistryObject(lid,
		    versionId, objectClass);
		AffectedInfo affectedInfo = this.deleteObjectById(user,
		    registryObject.getGuid(), registryObject.getClass());
		this.createAuditableEvent("deleteObjectByLidVid " + lid + versionId, user,
		    EventType.Deleted, affectedInfo);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.nasa.pds.registry.service.RegistryService#deleteObject(java
	 * .lang.String, java.lang.String, java.lang.Class)
	 */
	public void deleteObject(String user, String guid,
	    Class<? extends RegistryObject> objectClass) {
		AffectedInfo affectedInfo = this.deleteObjectById(user, guid, objectClass);
		this.createAuditableEvent("deleteObjectById " + guid, user,
		    EventType.Deleted, affectedInfo);
	}

	private AffectedInfo deleteObjectById(String user, String guid,
	    Class<? extends RegistryObject> objectClass) {
		metadataStore.deleteRegistryObject(guid, objectClass);
		AssociationFilter filter = new AssociationFilter.Builder()
		    .targetObject(guid).sourceObject(guid).build();
		RegistryQuery.Builder<AssociationFilter> queryBuilder = new RegistryQuery.Builder<AssociationFilter>()
		    .filter(filter).operator(QueryOperator.OR);
		PagedResponse<Association> response = metadataStore.getAssociations(
		    queryBuilder.build(), 1, -1);
		List<String> affectedIds = new ArrayList<String>();
		List<String> affectedTypes = new ArrayList<String>();
		affectedIds.add(guid);
		affectedTypes.add(objectClass.getSimpleName());
		for (RegistryObject object : response.getResults()) {
			affectedIds.add(object.getGuid());
			affectedTypes.add(object.getClass().getSimpleName());
			metadataStore.deleteRegistryObject(object.getGuid(), object.getClass());
		}
		return new AffectedInfo(affectedIds, affectedTypes);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gov.nasa.pds.registry.service.RegistryService#getAssocation(java.lang.String
	 * )
	 */
	public Association getAssocation(String guid) throws RegistryServiceException {
		return (Association) this.getObject(guid, Association.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gov.nasa.pds.registry.service.RegistryService#getExtrinsic(java.lang.String
	 * )
	 */
	public ExtrinsicObject getExtrinsic(String guid)
	    throws RegistryServiceException {
		return (ExtrinsicObject) this.getObject(guid, ExtrinsicObject.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gov.nasa.pds.registry.service.RegistryService#getObject(java.lang.String,
	 * java.lang.Class)
	 */
	public RegistryObject getObject(String guid,
	    Class<? extends RegistryObject> objectClass)
	    throws RegistryServiceException {
		try {
			return metadataStore.getRegistryObject(guid, objectClass);
		} catch (NoResultException nre) {
			throw new RegistryServiceException("Object not found " + guid,
			    ExceptionType.OBJECT_NOT_FOUND);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gov.nasa.pds.registry.service.RegistryService#configure(java.lang.String,
	 * gov.nasa.pds.registry.model.RegistryPackage, java.util.List)
	 */
	public String configure(String user, RegistryPackage registryPackage,
	    List<? extends RegistryObject> registryObjects)
	    throws RegistryServiceException {
		RegistryServiceException exception = null;
		String packageId = null;
		Map<String, Class<? extends RegistryObject>> registered = new HashMap<String, Class<? extends RegistryObject>>();
		try {
			packageId = this.publishObject(user, registryPackage);
		} catch (RegistryServiceException e) {
			throw e;
		}
		// For now we will simply call publish on every item in the list that is a
		// classification scheme of classification node
		// TODO: Make this a transaction to rollback if all don't work
		for (RegistryObject object : registryObjects) {
			try {
				if (object instanceof ClassificationScheme
				    || object instanceof ClassificationNode) {
					String id = this.publishObject(user, object, packageId);
					registered.put(id, object.getClass());
				}
			} catch (RegistryServiceException e) {
				exception = e;
				break;
			}
		}

		// TODO: Make this method transactional and just rollback anything added
		// here. Instead of deleting one by one
		if (exception != null) {
			for (String id : registered.keySet()) {
				this.deleteObject(user, id, registered.get(id));
			}
			// TODO: Remove package and associations?
			throw exception;
		}

		return packageId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gov.nasa.pds.registry.service.RegistryService#getNextObject(java.lang.String
	 * , java.lang.Class)
	 */
	@Override
	public RegistryObject getNextObject(String guid,
	    Class<? extends RegistryObject> objectClass)
	    throws RegistryServiceException {
		RegistryObject object = getObject(guid, objectClass);
		return getNextObject(object.getLid(), object.getVersionName(), objectClass);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gov.nasa.pds.registry.service.RegistryService#getPreviousObject(java.lang
	 * .String, java.lang.Class)
	 */
	@Override
	public RegistryObject getPreviousObject(String guid,
	    Class<? extends RegistryObject> objectClass)
	    throws RegistryServiceException {
		RegistryObject object = getObject(guid, objectClass);
		return getPreviousObject(object.getLid(), object.getVersionName(),
		    objectClass);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gov.nasa.pds.registry.service.RegistryService#changeStatusOfPackageMembers
	 * (java.lang.String, java.lang.String,
	 * gov.nasa.pds.registry.model.ObjectAction)
	 */
	@Override
	public void changeStatusOfPackageMembers(String user, String packageId,
	    ObjectAction action) {
		// Formulate a filter to look up members of the specified package
		AssociationFilter filter = new AssociationFilter.Builder()
		    .sourceObject(packageId)
		    .associationType("urn:registry:AssociationType:HasMember").build();
		RegistryQuery.Builder<AssociationFilter> queryBuilder = new RegistryQuery.Builder<AssociationFilter>()
		    .filter(filter);

		// Set up some counting variables to use for paging
		int count = 0;
		// How many we will grab on each query to the database (process in chunks)
		int rows = 100;
		// Keep track of items whose status have been changed
		List<String> changedIds = new ArrayList<String>();
		List<String> changedTypes = new ArrayList<String>();
		// Create the query that will be used
		RegistryQuery<AssociationFilter> query = queryBuilder.build();
		// Grab first page of results
		PagedResponse<Association> pagedAssociations = this.getAssociations(query,
		    1, rows);
		boolean done = (pagedAssociations.getNumFound() > 0) ? false : true;
		while (!done) {
			// Process this set of target objects
			for (Association association : pagedAssociations.getResults()) {
				count++;
				Slot slot = association.getSlot("targetObjectType");
				Class<? extends RegistryObject> objectClass = null;
				if (slot != null) {
					objectClass = ObjectClass.fromName(slot.getValues().get(0))
					    .getObjectClass();
				}
				AffectedInfo affectedInfo = this.changeObjectStatusById(user,
				    association.getTargetObject(), action, objectClass);
				changedIds.addAll(affectedInfo.getAffectedIds());
				changedTypes.addAll(affectedInfo.getAffectedTypes());
			}
			// Check to see if we are done processing all
			if (count >= pagedAssociations.getNumFound()) {
				done = true;
			} else {
				// Grab next set
				pagedAssociations = this.getAssociations(query, count + 1, rows);
			}
		}
		this.createAuditableEvent("changeStatusOfPackageMembers " + packageId,
		    user, action.getEventType(), new AffectedInfo(changedIds, changedTypes));
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gov.nasa.pds.registry.service.RegistryService#changeStatusOfPackageMembersWithQuery
	 * (java.lang.String, java.lang.String,
	 * gov.nasa.pds.registry.model.ObjectAction)
	 */
	@Override
	public void changeStatusOfPackageMembersWithQuery(String user, String packageId,
	    ObjectAction action) {
		AffectedInfo affectedInfo = metadataStore.updateRegistryObjects(packageId, new Integer(action.getObjectStatus().ordinal()),
				"urn:registry:AssociationType:HasMember");
		this.createAuditableEvent("changeStatusOfPackageMembers " + packageId,
					    user, action.getEventType(), affectedInfo);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gov.nasa.pds.registry.service.RegistryService#deletePackageMembers(java
	 * .lang.String, java.lang.String)
	 */
	@Override
	public void deletePackageMembers(String user, String packageId)
	    throws RegistryServiceException {
		// Formulate a filter to look up members of the specified package
		AssociationFilter filter = new AssociationFilter.Builder()
		    .sourceObject(packageId)
		    .associationType("urn:registry:AssociationType:HasMember").build();
		RegistryQuery.Builder<AssociationFilter> queryBuilder = new RegistryQuery.Builder<AssociationFilter>()
		    .filter(filter);
		// How many we will grab on each query to the database (process in chunks)
		int rows = 100;
		// Keep track of items that have been deleted
		List<String> deletedIds = new ArrayList<String>();
		List<String> deletedTypes = new ArrayList<String>();
		// Create the query that will be used
		RegistryQuery<AssociationFilter> query = queryBuilder.build();
		// Grab first page of results
		PagedResponse<Association> pagedAssociations = this.getAssociations(query,
		    1, rows);
		while (pagedAssociations.getNumFound() > 0) {
			// Process this set of target objects
			for (Association association : pagedAssociations.getResults()) {
				Slot slot = association.getSlot("targetObjectType");
				Class<? extends RegistryObject> objectClass = null;
				if (slot != null) {
					objectClass = ObjectClass.fromName(slot.getValues().get(0))
					    .getObjectClass();
				}
				AffectedInfo affectedInfo = this.deleteObjectById(user,
				    association.getTargetObject(), objectClass);
				deletedIds.addAll(affectedInfo.getAffectedIds());
				deletedTypes.addAll(affectedInfo.getAffectedTypes());
			}
			// Grab next set
			pagedAssociations = this.getAssociations(query, 1, rows);
		}
		this.createAuditableEvent("deletePackageMembers " + packageId, user,
		    EventType.Deleted, new AffectedInfo(deletedIds, deletedTypes));
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gov.nasa.pds.registry.service.RegistryService#deletePackageMembersWithQuery(java
	 * .lang.String, java.lang.String)
	 */
	@Override
	public void deletePackageMembersWithQuery(String user, String packageId)
	    throws RegistryServiceException {
		
		AffectedInfo affectedInfo = metadataStore.deleteRegistryObjects(packageId, "urn:registry:AssociationType:HasMember");		
		this.createAuditableEvent("deletePackageMembers " + packageId, user,
			    EventType.Deleted, affectedInfo);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.nasa.pds.registry.service.RegistryService#getReplicationReport()
	 */
	@Override
	public ReplicationReport getReplicationReport() {
		return replicationReport;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gov.nasa.pds.registry.service.RegistryService#hasReplicationInProgress()
	 */
	@Override
	public Boolean hasReplicationInProgress() {
		return (replicationReport == null) ? false
		    : (replicationReport.getStatus() == ReplicationStatus.RUNNING) ? true
		        : false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gov.nasa.pds.registry.service.RegistryService#performReplication(java.lang
	 * .String, java.lang.String, java.util.Date, java.lang.String)
	 */
	@Override
	public void performReplication(String user, String registryUrl,
	    Date lastModified, String objectType, RegistryPackage replicationPackage) 
	    		throws RegistryServiceException {
		// Setup the replication report
		initializeReplicationReport();

		// Register the package that items in this replication event will be associated
		String packageGuid = null;
		try {
			packageGuid = this.publishObject(user, replicationPackage);
		} catch (RegistryServiceException e) {
			throw e;
		}
		// Set the packageGuid in the report
		replicationReport.setPackageGuid(packageGuid);
		
		// Create a client to talk to the remote registry
		try {
			RegistryClient remoteRegistry = new RegistryClient(registryUrl);
			remoteRegistry.setMediaType(MediaType.APPLICATION_XML);
			// Query the remote registry for all the events based on last modified
			EventFilter filter = null;
			if (lastModified == null) {
				filter = new EventFilter.Builder().eventEnd(
				    replicationReport.getStarted()).build();
				replicationReport.setLastModified(null);
			} else {
				filter = new EventFilter.Builder().eventStart(lastModified)
				    .eventEnd(replicationReport.getStarted()).build();
				replicationReport.setLastModified(lastModified);
			}
			
			// Set up some counting variables to use for paging
			int count = 0;
			// How many we will grab on each query to the database (process in chunks)
			int rows = 100;
			RegistryQuery<EventFilter> query = new RegistryQuery.Builder<EventFilter>()
			    .filter(filter).build();
			PagedResponse<AuditableEvent> events = remoteRegistry.getAuditableEvents(
			    query, 1, rows);
			// Set the total number of events to be processed
			replicationReport.setTotalEvents(events.getNumFound());
			boolean done = (events.getNumFound() > 0) ? false : true;
			// Create a map of items that have already been addressed
			Map<String, EventType> processed = new HashMap<String, EventType>();
			List<String> affectedReplicatedIds = new ArrayList<String>();
			List<String> affectedReplicatedTypes = new ArrayList<String>();
			while (!done) {
				// Process this set of target objects
				for (AuditableEvent event : events.getResults()) {
					count++;
					if (event.getRequestId().startsWith("performReplication")) {
						replicationReport.addSkippedEvent(event.getGuid());
					} else {
						Slot affectedTypes = event.getSlot("affectedObjectTypes");
						for (int i = 0; i < event.getAffectedObjects().size(); i++) {
							String affectedId = event.getAffectedObjects().get(i);
							String affectedType = affectedTypes.getValues().get(i);
							Class<? extends RegistryObject> objectClass = ObjectClass
							    .fromName(affectedType).getObjectClass();
							if (!processed.containsKey(affectedId)) {
								processed.put(affectedId, event.getEventType());
								if (event.getEventType() == EventType.Deleted) {
									try {
										// Get object from our local registry
										RegistryObject object = this.getObject(affectedId,
										    objectClass);
										// Check to make the object is not owned by us (i.e. the
										// home
										// is not equal to local home)
										if (!object.getHome().equals(idGenerator.getHome())) {
											affectedReplicatedIds.add(affectedId);
											affectedReplicatedTypes.add(objectClass.getSimpleName());
											metadataStore.deleteRegistryObject(affectedId,
											    objectClass);
										}
									} catch (RegistryServiceException ex) {
										replicationReport.addSkippedObject(affectedId);
									} catch (Exception ex) {
										// Catch all so we can continue to proceed
										replicationReport.addSkippedObject(affectedId);
									}
								} else {
									try {
										// Time to grab the object from the remote registry
										RegistryObject remoteObject = remoteRegistry.getObject(
										    affectedId, objectClass);
										// Check to see if we are filtering only for certain
										// objectTypes
										if (objectType == null
										    || remoteObject.getObjectType().equals(objectType)) {
											// Check to make the object is not owned by us (i.e. the
											// home
											// is not equal to local home)
											if (!remoteObject.getHome().equals(idGenerator.getHome())) {
												affectedReplicatedIds.add(affectedId);
												affectedReplicatedTypes
												    .add(objectClass.getSimpleName());
												// Remove the object in case it already exists
												metadataStore.deleteRegistryObject(affectedId,
												    objectClass);
												// Need to allow current registry to set its own
												// internal slot ids
												Set<Slot> newSlots = new HashSet<Slot>();
												for (Slot slot : remoteObject.getSlots()) {
													Slot newSlot = new Slot(slot.getName(),
													    slot.getValues());
													slot.setSlotType(slot.getSlotType());
													newSlots.add(newSlot);
												}
												remoteObject.setSlots(newSlots);
												// Replicate it
												publishObject(user, remoteObject, packageGuid);
											}
										}
									} catch (RegistryServiceException ex) {
										replicationReport.addSkippedObject(affectedId);
									} catch (Exception ex) {
										// Catch all so we can continue to proceed
										replicationReport.addSkippedObject(affectedId);
									}
								}
							}
						}
						// Increment the number of events that have been taken care of
						replicationReport.setEventsProcessed(replicationReport
						    .getEventsProcessed() + 1);
					}
				}
				// Check to see if we are done processing all
				if (count >= events.getNumFound()) {
					done = true;
				} else {
					// Grab next set
					events = remoteRegistry.getAuditableEvents(query, count + 1, rows);
				}

				// Create an auditable event to track what happened for this replication
				this.createAuditableEvent(
				    "performReplication "
				        + registryUrl
				        + ((lastModified == null) ? "" : " "
				            + dateFormat.format(lastModified)), user,
				    EventType.Replicated, new AffectedInfo(affectedReplicatedIds,
				        affectedReplicatedTypes));
			}
		} catch (RegistryClientException e) {
			throw new RegistryServiceException("Could not contact remote registry: "
			    + e.getMessage(), Response.Status.BAD_REQUEST);
		}

		// Now we're done so let's set the status to complete and record the time we
		// finished
		replicationReport.setStatus(ReplicationStatus.COMPLETE);
		replicationReport.setLastModified(new Date());
	}

	private synchronized void initializeReplicationReport()
	    throws RegistryServiceException {
		if (replicationReport != null
		    && replicationReport.getStatus() == ReplicationStatus.RUNNING) {
			throw new RegistryServiceException(
			    "Replication is already running. Only one request can be processed at a time",
			    Response.Status.CONFLICT);
		}
		if (replicationReport == null
		    || replicationReport.getStatus() == ReplicationStatus.COMPLETE) {
			replicationReport = new ReplicationReport();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gov.nasa.pds.registry.service.RegistryService#getAuditableEvents(gov.nasa
	 * .pds.registry.query.RegistryQuery, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public PagedResponse<AuditableEvent> getAuditableEvents(
	    RegistryQuery<EventFilter> query, Integer start, Integer rows) {
		if (start <= 0) {
			start = 1;
		}
		return metadataStore.getAuditableEvents(query, start, rows);
	}

}
