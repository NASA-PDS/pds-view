//	Copyright 2009-2014, by the California Institute of Technology.
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

import gov.nasa.pds.registry.model.AffectedInfo;
import gov.nasa.pds.registry.model.Association;
import gov.nasa.pds.registry.model.AuditableEvent;
import gov.nasa.pds.registry.model.ClassificationNode;
import gov.nasa.pds.registry.model.ClassificationScheme;
import gov.nasa.pds.registry.model.PagedResponse;
import gov.nasa.pds.registry.model.ExtrinsicObject;
import gov.nasa.pds.registry.model.RegistryObject;
import gov.nasa.pds.registry.model.RegistryPackage;
import gov.nasa.pds.registry.model.Slot;
import gov.nasa.pds.registry.query.AssociationFilter;
import gov.nasa.pds.registry.query.EventFilter;
import gov.nasa.pds.registry.query.ExtrinsicFilter;
import gov.nasa.pds.registry.query.ObjectFilter;
import gov.nasa.pds.registry.query.PackageFilter;
import gov.nasa.pds.registry.query.QueryOperator;
import gov.nasa.pds.registry.query.RegistryQuery;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Arrays;
import java.util.Set;

import javax.persistence.EntityManager;
//import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * This is a database implementation using Java's persistence library. Any
 * database that supports JPA can be used in conjunction with this
 * implementation of a {@link MetadataStore}. To use a different type of
 * database one would need to update the persistence unit provided via
 * configuration and injected with Spring.
 *
 * @author pramirez
 *
 */
@Repository(value = "metadataStore")
public class MetadataStoreJPA implements MetadataStore {

  private EntityManager entityManager;

  @PersistenceContext
  public void setEntityManager(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  public EntityManager getEntityManager() {
    return entityManager;
  }

  private Predicate generatePredicate(CriteriaBuilder builder,
      Expression<String> attribute, String filter) {
    // If a wildcard is used, indicated by star, then use like,
    // otherwise use equal.
    if (filter.contains("*")) {
      return builder.like(attribute, escape(filter), '\\');
    } else {
      return builder.equal(attribute, filter);
    }
  }

  private String escape(String filter) {
    // Underscores count as a single wildard so escape them with a backslash.
    // The replace all function ends up requiring 4 backslashes to replace one.
    // Convert the star character to a percent for handling multi character
    // wildcards.
    return filter.replaceAll("_", "\\\\_").replaceAll("\\*", "%");
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * gov.nasa.pds.registry.service.MetadataStore#getExtrinsics(gov.nasa.pds.
   * registry .query.ProductQuery, java.lang.Long, java.lang.Integer)
   */
  @Override
  @Transactional(readOnly = true)
  public PagedResponse<ExtrinsicObject> getExtrinsics(
      RegistryQuery<ExtrinsicFilter> query, Integer start, Integer rows) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<ExtrinsicObject> cq = cb.createQuery(ExtrinsicObject.class);
    Root<ExtrinsicObject> productEntity = cq.from(ExtrinsicObject.class);
    ExtrinsicFilter filter = query.getFilter();
    List<Predicate> predicates = new ArrayList<Predicate>();
    if (filter != null) {
      if (filter.getGuid() != null) {
        predicates.add(generatePredicate(cb, productEntity.get("guid").as(
            String.class), filter.getGuid()));
      }
      if (filter.getLid() != null) {
        predicates.add(generatePredicate(cb, productEntity.get("lid").as(
            String.class), filter.getLid()));
      }
      if (filter.getName() != null) {
        predicates.add(generatePredicate(cb, productEntity.get("name").as(
            String.class), filter.getName()));
      }
      if (filter.getObjectType() != null) {
        predicates.add(generatePredicate(cb, productEntity.get("objectType")
            .as(String.class), filter.getObjectType()));
      }
      if (filter.getStatus() != null) {
        predicates.add(cb
            .equal(productEntity.get("status"), filter.getStatus()));
      }
      if (filter.getVersionName() != null) {
        predicates.add(generatePredicate(cb, productEntity.get("versionName")
            .as(String.class), filter.getVersionName()));
      }
    }

    if (predicates.size() != 0) {
      Predicate[] p = new Predicate[predicates.size()];
      if (query.getOperator() == QueryOperator.AND) {
        cq.where(cb.and(predicates.toArray(p)));
      } else {
        cq.where(cb.or(predicates.toArray(p)));
      }
    }

    List<Order> orders = new ArrayList<Order>();
    if (query.getSort().size() > 0) {
      for (String sort : query.getSort()) {
        String[] s = sort.split(" ");
        if (s.length == 1 || s[1].equalsIgnoreCase("ASC")) {
          orders.add(cb.asc(productEntity.get(s[0])));
        } else {
          orders.add(cb.desc(productEntity.get(s[0])));
        }
      }
    } else {
      orders.add(cb.asc(productEntity.get("guid")));
    }
    cq.orderBy(orders);
    TypedQuery<ExtrinsicObject> dbQuery = entityManager.createQuery(cq);
    PagedResponse<ExtrinsicObject> response = new PagedResponse<ExtrinsicObject>(
        start, (long) dbQuery.getResultList().size(), dbQuery.setFirstResult(
            start - 1).setMaxResults(rows).getResultList());
    return response;
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * gov.nasa.pds.registry.service.MetadataStore#getPackages(gov.nasa.pds.
   * registry .query.ProductQuery, java.lang.Long, java.lang.Integer)
   */
  @Override
  @Transactional(readOnly = true)
  public PagedResponse<RegistryPackage> getPackages(
      RegistryQuery<PackageFilter> query, Integer start, Integer rows) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<RegistryPackage> cq = cb.createQuery(RegistryPackage.class);
    Root<RegistryPackage> productEntity = cq.from(RegistryPackage.class);
    PackageFilter filter = query.getFilter();
    List<Predicate> predicates = new ArrayList<Predicate>();
    if (filter != null) {
      if (filter.getGuid() != null) {
        predicates.add(generatePredicate(cb, productEntity.get("guid").as(
            String.class), filter.getGuid()));
      }
      if (filter.getLid() != null) {
        predicates.add(generatePredicate(cb, productEntity.get("lid").as(
            String.class), filter.getLid()));
      }
      if (filter.getName() != null) {
        predicates.add(generatePredicate(cb, productEntity.get("name").as(
            String.class), filter.getName()));
      }
      if (filter.getStatus() != null) {
          predicates.add(cb
              .equal(productEntity.get("status"), filter.getStatus()));
      }
    }

    if (predicates.size() != 0) {
      Predicate[] p = new Predicate[predicates.size()];
      if (query.getOperator() == QueryOperator.AND) {
        cq.where(cb.and(predicates.toArray(p)));
      } else {
        cq.where(cb.or(predicates.toArray(p)));
      }
    }

    List<Order> orders = new ArrayList<Order>();
    if (query.getSort().size() > 0) {
      for (String sort : query.getSort()) {
        String[] s = sort.split(" ");
        if (s.length == 1 || s[1].equalsIgnoreCase("ASC")) {
          orders.add(cb.asc(productEntity.get(s[0])));
        } else {
          orders.add(cb.desc(productEntity.get(s[0])));
        }
      }
    } else {
      orders.add(cb.asc(productEntity.get("guid")));
    }
    cq.orderBy(orders);
    TypedQuery<RegistryPackage> dbQuery = entityManager.createQuery(cq);
    PagedResponse<RegistryPackage> response = new PagedResponse<RegistryPackage>(
        start, (long) dbQuery.getResultList().size(), dbQuery.setFirstResult(
            start - 1).setMaxResults(rows).getResultList());
    return response;
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * gov.nasa.pds.registry.service.MetadataStore#getAuditableEvents(java.lang
   * .String)
   */
  @SuppressWarnings("unchecked")
  @Override
  @Transactional(readOnly = true)
  public List<AuditableEvent> getAuditableEvents(String affectedObject) {
    // Because hibernate did not allow MEMBER OF queries against
    // ElementCollections went with a string based query. If the bug in the
    // hibernate api gets fixed this should be converted to use the Criteria API
    // (see http://opensource.atlassian.com/projects/hibernate/browse/HHH-869)
    TypedQuery<AuditableEvent> dbQuery = (TypedQuery<AuditableEvent>) entityManager
        .createQuery(
            "from AuditableEvent e join e.affectedObjects a where a =:affected")
        .setParameter("affected", affectedObject);
    return dbQuery.getResultList();
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * gov.nasa.pds.registry.service.MetadataStore#getClassificationNodes(java
   * .lang.String)
   */
  @Override
  @Transactional(readOnly = true)
  public List<ClassificationNode> getClassificationNodes(String scheme) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<ClassificationNode> cq = cb
        .createQuery(ClassificationNode.class);
    Root<ClassificationNode> nodeEntity = cq.from(ClassificationNode.class);
    cq.where(cb.like(nodeEntity.get("path").as(String.class), "/" + scheme
        + "/%"));
    List<Order> orders = new ArrayList<Order>();
    orders.add(cb.asc(nodeEntity.get("guid")));
    cq.orderBy(orders);
    TypedQuery<ClassificationNode> dbQuery = entityManager.createQuery(cq);
    return dbQuery.getResultList();
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * gov.nasa.pds.registry.service.MetadataStore#getAssociations(gov.nasa.pds
   * .registry.query.AssociationQuery, java.lang.Integer, java.lang.Integer)
   */
  @Override
  @Transactional(readOnly = true)
  public PagedResponse<Association> getAssociations(
      RegistryQuery<AssociationFilter> query, Integer start, Integer rows) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Association> cq = cb.createQuery(Association.class);
    Root<Association> associationEntity = cq.from(Association.class);
    AssociationFilter filter = query.getFilter();
    List<Predicate> predicates = new ArrayList<Predicate>();
    if (filter != null) {
      if (filter.getTargetObject() != null) {
        predicates.add(generatePredicate(cb, associationEntity.get(
            "targetObject").as(String.class), filter.getTargetObject()));
      }
      if (filter.getSourceObject() != null) {
        predicates.add(generatePredicate(cb, associationEntity.get(
            "sourceObject").as(String.class), filter.getSourceObject()));
      }
      if (filter.getAssociationType() != null) {
        predicates.add(generatePredicate(cb, associationEntity.get(
            "associationType").as(String.class), filter.getAssociationType()));
      }
    }
    if (predicates.size() != 0) {
      Predicate[] p = new Predicate[predicates.size()];
      if (query.getOperator() == QueryOperator.AND) {
        cq.where(cb.and(predicates.toArray(p)));
      } else {
        cq.where(cb.or(predicates.toArray(p)));
      }
    }

    List<Order> orders = new ArrayList<Order>();
    if (query.getSort().size() > 0) {
      for (String sort : query.getSort()) {
        String[] s = sort.split(" ");
        if (s.length == 1 || s[1].equalsIgnoreCase("ASC")) {
          orders.add(cb.asc(associationEntity.get(s[0])));
        } else {
          orders.add(cb.desc(associationEntity.get(s[0])));
        }
      }
    } else {
      orders.add(cb.asc(associationEntity.get("guid")));
    }
    cq.orderBy(orders);
    TypedQuery<Association> dbQuery = entityManager.createQuery(cq);

    if (rows == -1) {
      return new PagedResponse<Association>(start, (long) dbQuery
          .getResultList().size(), dbQuery.setFirstResult(start - 1)
          .getResultList());
    }

    return new PagedResponse<Association>(start, (long) dbQuery.getResultList()
        .size(), dbQuery.setFirstResult(start - 1).setMaxResults(rows)
        .getResultList());
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * gov.nasa.pds.registry.service.MetadataStore#getRegistyObject(java.lang.
   * String, java.lang.Class)
   */
  @Override
  @Transactional(readOnly = true)
  public RegistryObject getRegistryObject(String guid,
      Class<? extends RegistryObject> objectClass) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<?> cq = cb.createQuery(objectClass);
    Root<?> entity = cq.from(objectClass);
    Path<String> guidAttr = entity.get("guid");
    cq.where(cb.equal(guidAttr, guid));
    TypedQuery<?> query = entityManager.createQuery(cq);
    return (RegistryObject) query.getSingleResult();
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * gov.nasa.pds.registry.service.MetadataStore#saveRegistryObject(gov.nasa
   * .pds.registry.model.RegistryObject)
   */
  @Override
  @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
  public void saveRegistryObject(RegistryObject registryObject) {
    entityManager.persist(registryObject);
    entityManager.flush();
    // Needed to detach the object from the EntityManager to free up
    // resources.
    entityManager.clear();
  }
  
  /*
   * (non-Javadoc)
   *
   * @see
   * gov.nasa.pds.registry.service.MetadataStore#saveRegistryObject(gov.nasa
   * .pds.registry.model.RegistryObject)
   */
  @Override
  @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = false)
  //@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
  public void saveRegistryObjects(List<RegistryObject> registryObjects) {
	  for (RegistryObject registryObject: registryObjects) {
		  entityManager.persist(registryObject);
		  // may store guid into the hashmap and return?
	  }  
	  entityManager.flush();
	  // Needed to detach the object from the EntityManager to free up
	  // resources.
	  entityManager.clear();
	  // how to rollback?????
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * gov.nasa.pds.registry.service.MetadataStore#deleteRegistryObject(java.lang
   * .String, java.lang.Class)
   */
  @Override
  @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
  public void deleteRegistryObject(String guid,
      Class<? extends RegistryObject> objectClass) {
    try {
      RegistryObject registryObject = this.getRegistryObject(guid, objectClass);
      entityManager.remove(registryObject);
      entityManager.flush();
    } catch (NoResultException nre) {
      // Suppress as the object was not found
    }
  }
  
  /*
   * (non-Javadoc)
   *
   * @see
   * gov.nasa.pds.registry.service.MetadataStore#deleteRegistryObjects(java.lang
   * .String, java.lang.String)
   */
  @Override
  @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
  public AffectedInfo deleteRegistryObjects(String packageId, String associationType) {
	  // TODO TODO TODO: need to be able to delete scheme and classificationNode entries
	  
	  int deletedCount = 0;
	  try {   
		  String selectQuery = "select targetObject from Association where associationType='" + associationType +
				  "' and sourceObject='" + packageId + "'";
		  System.out.println("selectQuery = " + selectQuery);		  
		  Query query = entityManager.createQuery(selectQuery);
		  List<String> targetObjects = query.getResultList();
		  int extObjCount = 0, assocCount = 0, cnObjCount = 0, csObjCount = 0;
		  int i=0;
		  List<String> deletedIds = new ArrayList<String>();
		  List<String> deletedTypes = new ArrayList<String>();
		
		  // need to delete ExtrinsicObject and Association tables. 
		  for (String targetObject: targetObjects) {
			  //System.out.println("========targetObject guid = " + targetObject);
			  int tmpCount = 0;
			  List<Association> objectToRemove = entityManager.createQuery("select a from Association a where a.guid = :guid").setParameter("guid", targetObject).getResultList();
			  if (objectToRemove!=null) {				  
				  for (Association assoc: objectToRemove) {  
					  Set<Slot> slotsToRemove = assoc.getSlots();
					  for (Slot o: slotsToRemove) {
						  entityManager.remove(o);
						  tmpCount++;
					  }
					  entityManager.remove(assoc);
					  assocCount++;
				  }
			  }			  
			  //System.out.println("deletedCount for Slot tables of Association = " + tmpCount);		
			  //System.out.println("deletedCount for Association tables = " + assocCount);

			  if (tmpCount>0) {
				  AffectedInfo affectedInfo = new AffectedInfo(Arrays.asList(targetObject), Arrays.asList("Association"));
				  deletedIds.addAll(affectedInfo.getAffectedIds());
				  deletedTypes.addAll(affectedInfo.getAffectedTypes());	
			  }

			  List<ExtrinsicObject> eoToRemove = entityManager.createQuery("select eo from ExtrinsicObject eo where eo.guid = :guid").setParameter("guid", targetObject).getResultList();
			  tmpCount = 0;
			  if (eoToRemove!=null) {				  
				  for (ExtrinsicObject extObj: eoToRemove) {  
					  Set<Slot> slotsToRemove = extObj.getSlots();
					  for (Slot o: slotsToRemove) {
						  entityManager.remove(o);
						  tmpCount++;
					  }
					  entityManager.remove(extObj);
					  extObjCount++;
				  }
			  }		
			  //System.out.println("deletedCount for Slot tables of ExtrinsicObject = " + tmpCount);	
			  //System.out.println("deletedCount for ExtrinsicObject = " + extObjCount);	
			  
			  if (tmpCount>0) {
				  AffectedInfo affectedInfo = new AffectedInfo(Arrays.asList(targetObject), Arrays.asList("ExtrinsicObject"));
				  deletedIds.addAll(affectedInfo.getAffectedIds());
				  deletedTypes.addAll(affectedInfo.getAffectedTypes());	
			  }
			  //System.out.println((i++) + "     extObjCount = " + extObjCount + "     assocCount = " + assocCount);
			  
			  // classification node object 
			  List<ClassificationNode> cnToRemove = entityManager.createQuery("select cn from ClassificationNode cn where cn.guid = :guid").setParameter("guid", targetObject).getResultList();
			  tmpCount = 0;
			  if (cnToRemove!=null) {				  
				  for (ClassificationNode cnObj: cnToRemove) {  
					  Set<Slot> slotsToRemove = cnObj.getSlots();
					  for (Slot o: slotsToRemove) {
						  entityManager.remove(o);
						  tmpCount++;
					  }
					  entityManager.remove(cnObj);
					  cnObjCount++;
				  }
			  }		
			  //System.out.println("deletedCount for Slot tables of ClassificationNode = " + tmpCount);	
			  //System.out.println("deletedCount for ClassificationNode = " + cnObjCount);	
			  
			  if (tmpCount>0) {
				  AffectedInfo affectedInfo = new AffectedInfo(Arrays.asList(targetObject), Arrays.asList("ClassificationNode"));
				  deletedIds.addAll(affectedInfo.getAffectedIds());
				  deletedTypes.addAll(affectedInfo.getAffectedTypes());	
			  }
			  
			// ClassificationScheme object 
			  List<ClassificationScheme> csToRemove = entityManager.createQuery("select cn from ClassificationScheme cn where cn.guid = :guid").setParameter("guid", targetObject).getResultList();
			  tmpCount = 0;
			  if (cnToRemove!=null) {				  
				  for (ClassificationScheme csObj: csToRemove) {  
					  Set<Slot> slotsToRemove = csObj.getSlots();
					  for (Slot o: slotsToRemove) {
						  entityManager.remove(o);
						  tmpCount++;
					  }
					  entityManager.remove(csObj);
					  csObjCount++;
				  }
			  }		
			  //System.out.println("deletedCount for Slot tables of ClassificationScheme = " + tmpCount);	
			  //System.out.println("deletedCount for ClassificationScheme = " + csObjCount);	
			  
			  if (tmpCount>0) {
				  AffectedInfo affectedInfo = new AffectedInfo(Arrays.asList(targetObject), Arrays.asList("ClassificationScheme"));
				  deletedIds.addAll(affectedInfo.getAffectedIds());
				  deletedTypes.addAll(affectedInfo.getAffectedTypes());	
			  }
			  
		  }

		  deletedCount = extObjCount + assocCount + cnObjCount +  csObjCount;
		  System.out.println("Deleted package memebers...extObjCount = " + extObjCount + "     assocCount = " + assocCount + 
				  "   classificationNode count = " + cnObjCount + "   scheme count = " + csObjCount + 
				  "   total deletedCount = " + deletedCount + "  deletedIds.size() = " + deletedIds.size() + 
				  "   deletedTypes.size() = " + deletedTypes.size());
		  
		  return new AffectedInfo(deletedIds, deletedTypes);
	  } catch (NoResultException nre) {
		  // Suppress as the object was not found
		  return null;
	  } catch (Exception ex) {
		  ex.printStackTrace();
		  return null;
	  }
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * gov.nasa.pds.registry.service.MetadataStore#getNumRegistryObjects(java.
   * lang.Class)
   */
  @Override
  @Transactional(readOnly = true)
  public long getNumRegistryObjects(Class<? extends RegistryObject> objectClass) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> cq = cb.createQuery(Long.class);
    Root<?> entity = cq.from(objectClass);
    cq.select(cb.count(entity));
    return entityManager.createQuery(cq).getSingleResult().longValue();
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * gov.nasa.pds.registry.service.MetadataStore#getRegistryObject(java.lang
   * .String, java.lang.String, java.lang.Class)
   */
  @Override
  public RegistryObject getRegistryObject(String lid, String versionName,
      Class<? extends RegistryObject> objectClass) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<?> cq = cb.createQuery(objectClass);
    Root<?> entity = cq.from(objectClass);
    Path<String> lidAttr = entity.get("lid");
    Path<String> versionNameAttr = entity.get("versionName");
    cq.where(cb.and(cb.equal(lidAttr, lid), cb.equal(versionNameAttr,
        versionName)));
    TypedQuery<?> query = entityManager.createQuery(cq);
    return (RegistryObject) query.getSingleResult();
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * gov.nasa.pds.registry.service.MetadataStore#getRegistryObjectVersions(java
   * .lang.String, java.lang.Class)
   */
  @SuppressWarnings("unchecked")
  @Override
  @Transactional(readOnly = true)
  public List<RegistryObject> getRegistryObjectVersions(String lid,
      Class<? extends RegistryObject> objectClass) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<?> cq = cb.createQuery(objectClass);
    Root<?> entity = cq.from(objectClass);
    Path<String> lidAttr = entity.get("lid");
    cq.where(cb.equal(lidAttr, lid));
    TypedQuery<?> query = entityManager.createQuery(cq);
    if (query.getResultList().size() == 0) {
      throw new NoResultException();
    }
    return (List<RegistryObject>) query.getResultList();
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * gov.nasa.pds.registry.service.MetadataStore#getRegistryObjects(java.lang
   * .Integer, java.lang.Integer)
   */
  @SuppressWarnings("unchecked")
  @Override
  @Transactional(readOnly = true)
  public List<RegistryObject> getRegistryObjects(Integer start, Integer rows,
      Class<? extends RegistryObject> objectClass) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<?> cq = cb.createQuery(objectClass);
    cq.from(objectClass);
    TypedQuery<?> query = entityManager.createQuery(cq);
    // Database is 0 indexed not 1
    return (List<RegistryObject>) query.setFirstResult(start - 1)
        .setMaxResults(rows).getResultList();
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * gov.nasa.pds.registry.service.MetadataStore#getRegistryObjects(gov.nasa
   * .pds.registry.query.ObjectQuery, java.lang.Integer, java.lang.Integer,
   * java.lang.Class)
   */
  @SuppressWarnings("unchecked")
  @Override
  public PagedResponse getRegistryObjects(RegistryQuery<ObjectFilter> query,
      Integer start, Integer rows, Class<? extends RegistryObject> objectClass) {

    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<?> cq = cb.createQuery(objectClass);
    Root<?> objectEntity = cq.from(objectClass);
    ObjectFilter filter = query.getFilter();
    List<Predicate> predicates = new ArrayList<Predicate>();
    if (filter != null) {
      if (filter.getGuid() != null) {
        predicates.add(generatePredicate(cb, objectEntity.get("guid").as(
            String.class), filter.getGuid()));
      }
      if (filter.getLid() != null) {
        predicates.add(generatePredicate(cb, objectEntity.get("lid").as(
            String.class), filter.getLid()));
      }
      if (filter.getName() != null) {
        predicates.add(generatePredicate(cb, objectEntity.get("name").as(
            String.class), filter.getName()));
      }
      if (filter.getObjectType() != null) {
        predicates.add(generatePredicate(cb, objectEntity.get("objectType").as(
            String.class), filter.getObjectType()));
      }
      if (filter.getStatus() != null) {
        predicates
            .add(cb.equal(objectEntity.get("status"), filter.getStatus()));
      }
      if (filter.getVersionName() != null) {
        predicates.add(generatePredicate(cb, objectEntity.get("versionName")
            .as(String.class), filter.getVersionName()));
      }
    }

    if (predicates.size() != 0) {
      Predicate[] p = new Predicate[predicates.size()];
      if (query.getOperator() == QueryOperator.AND) {
        cq.where(cb.and(predicates.toArray(p)));
      } else {
        cq.where(cb.or(predicates.toArray(p)));
      }
    }

    List<Order> orders = new ArrayList<Order>();
    if (query.getSort().size() > 0) {
      for (String sort : query.getSort()) {
        String[] s = sort.split(" ");
        if (s.length == 1 || s[1].equalsIgnoreCase("ASC")) {
          orders.add(cb.asc(objectEntity.get(s[0])));
        } else {
          orders.add(cb.desc(objectEntity.get(s[0])));
        }
      }
    } else {
      orders.add(cb.asc(objectEntity.get("guid")));
    }
    cq.orderBy(orders);
    TypedQuery<?> dbQuery = entityManager.createQuery(cq);
    PagedResponse response = new PagedResponse(start, (long) dbQuery
        .getResultList().size(), (List<RegistryObject>) dbQuery.setFirstResult(
        start - 1).setMaxResults(rows).getResultList());
    return response;
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * gov.nasa.pds.registry.service.MetadataStore#hasRegistryObject(java.lang
   * .String, java.lang.String, java.lang.Class)
   */
  @Override
  @Transactional(readOnly = true)
  public boolean hasRegistryObject(String lid, String versionName,
      Class<? extends RegistryObject> objectClass) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<?> cq = cb.createQuery(objectClass);
    Root<?> entity = cq.from(objectClass);
    Path<String> lidAttr = entity.get("lid");
    Path<String> versionNameAttr = entity.get("versionName");
    cq.where(cb.and(cb.equal(lidAttr, lid), cb.equal(versionNameAttr,
        versionName)));
    TypedQuery<?> query = entityManager.createQuery(cq);
    return !query.getResultList().isEmpty();
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * gov.nasa.pds.registry.service.MetadataStore#hasRegistryObjectVersions(java
   * .lang.String, java.lang.Class)
   */
  @Override
  @Transactional(readOnly = true)
  public boolean hasRegistryObjectVersions(String lid,
      Class<? extends RegistryObject> objectClass) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<?> cq = cb.createQuery(objectClass);
    Root<?> entity = cq.from(objectClass);
    Path<String> lidAttr = entity.get("lid");
    cq.where(cb.equal(lidAttr, lid));
    TypedQuery<?> query = entityManager.createQuery(cq);
    return !query.getResultList().isEmpty();
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * gov.nasa.pds.registry.service.MetadataStore#hasRegistryObject(java.lang
   * .String, java.lang.Class)
   */
  @Override
  @Transactional(readOnly = true)
  public boolean hasRegistryObject(String guid,
      Class<? extends RegistryObject> objectClass) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<?> cq = cb.createQuery(objectClass);
    Root<?> entity = cq.from(objectClass);
    Path<String> guidAttr = entity.get("guid");
    cq.where(cb.equal(guidAttr, guid));
    TypedQuery<?> query = entityManager.createQuery(cq);
    return !query.getResultList().isEmpty();
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * gov.nasa.pds.registry.service.MetadataStore#updateRegistryObject(gov.nasa
   * .pds.registry.model.RegistryObject)
   */
  @Override
  @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
  public void updateRegistryObject(RegistryObject registryObject) {
    entityManager.merge(registryObject);
    entityManager.flush();
  }
  
  /*
   * (non-Javadoc)
   *
   * @see
   * gov.nasa.pds.registry.service.MetadataStore#updateRegistryObjects(java.lang.String, java.lang.Integer, java.lang.String)
   */
  @Override
  @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
  public AffectedInfo updateRegistryObjects(String packageId, Integer status, String associationType) {
	  int updatedCount = 0;
	  try {
		  String selectQuery = "select targetObject from Association where associationType='" + associationType +
				  "' and sourceObject='" + packageId + "'";
		  System.out.println("selectQuery = " + selectQuery);		  
		  Query query = entityManager.createQuery(selectQuery);
		  List<String> targetObjects = query.getResultList();
		  int extObjCount = 0, assocCount = 0;
		  int i=0;
		  List<String> changedIds = new ArrayList<String>();
		  List<String> changedTypes = new ArrayList<String>();
		
		  // need to update ExtrinsicObject and Association tables. 
		  for (String targetObject: targetObjects) {
			  String updateQuery = "UPDATE ExtrinsicObject set status = " + status.intValue() + " WHERE guid ='" + targetObject + "'";
			  Query updateQry = entityManager.createQuery(updateQuery);
			  int tmpCount = updateQry.executeUpdate();			  
			  if (tmpCount>0) {
				  AffectedInfo affectedInfo = new AffectedInfo(Arrays.asList(targetObject), Arrays.asList("ExtrinsicObject"));
				  changedIds.addAll(affectedInfo.getAffectedIds());
				  changedTypes.addAll(affectedInfo.getAffectedTypes());	
			  }
			  extObjCount += tmpCount;
			  
			  String updateAssociationQuery = "UPDATE Association set status = " + status.intValue() + " WHERE guid ='" + targetObject + "'";
			  Query updateAssocQry = entityManager.createQuery(updateAssociationQuery);
			  
			  tmpCount = updateAssocQry.executeUpdate();			  
			  if (tmpCount>0) {
				  AffectedInfo affectedInfo = new AffectedInfo(Arrays.asList(targetObject), Arrays.asList("Association"));
				  changedIds.addAll(affectedInfo.getAffectedIds());
				  changedTypes.addAll(affectedInfo.getAffectedTypes());	
			  }
			  assocCount += tmpCount;
			  //System.out.println((i++) + "     extObjCount = " + extObjCount + "     assocCount = " + assocCount);
		  }

		  updatedCount = extObjCount + assocCount;
		  System.out.println("updateRegistryObjects....extObjCount = " + extObjCount + "     assocCount = " + assocCount + 
				  "   total updatedCount = " + updatedCount + "  changedIds.size() = " + changedIds.size() + 
				  "   changedTypes.size() = " + changedTypes.size());
		  
		  return new AffectedInfo(changedIds, changedTypes);
	  } catch (NoResultException nre) {
		  // Suppress as the object was not found
		  return null;
	  }
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * gov.nasa.pds.registry.service.MetadataStore#hasClassificationNode(java.
   * lang.String, java.lang.String)
   */
  @Override
  @Transactional(readOnly = true)
  public boolean hasClassificationNode(String scheme, String code) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<ClassificationNode> cq = cb
        .createQuery(ClassificationNode.class);
    Root<ClassificationNode> nodeEntity = cq.from(ClassificationNode.class);
    cq.where(cb.and(cb.like(nodeEntity.get("path").as(String.class), "/"
        + scheme + "/%"), cb.equal(nodeEntity.get("code"), code)));
    TypedQuery<ClassificationNode> dbQuery = entityManager.createQuery(cq);
    return !dbQuery.getResultList().isEmpty();
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * gov.nasa.pds.registry.service.MetadataStore#getAuditableEvents(gov.nasa
   * .pds.registry.query.RegistryQuery, java.lang.Integer, java.lang.Integer)
   */
  @Override
  public PagedResponse<AuditableEvent> getAuditableEvents(
      RegistryQuery<EventFilter> query, Integer start, Integer rows) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<AuditableEvent> cq = cb.createQuery(AuditableEvent.class);
    Root<AuditableEvent> eventEntity = cq.from(AuditableEvent.class);
    EventFilter filter = query.getFilter();
    List<Predicate> predicates = new ArrayList<Predicate>();
    if (filter != null) {
      if (filter.getEventType() != null) {
        predicates.add(cb.equal(eventEntity.get("eventType"), filter
            .getEventType()));
      }
      if (filter.getEventStart() != null) {
        predicates.add(cb.greaterThanOrEqualTo(eventEntity.get("timestamp").as(
            Date.class), filter.getEventStart()));
      }
      if (filter.getEventEnd() != null) {
        predicates.add(cb.lessThanOrEqualTo(eventEntity.get("timestamp").as(
            Date.class), filter.getEventEnd()));
      }
      if (filter.getRequestId() != null) {
        predicates.add(generatePredicate(cb, eventEntity.get("requestId").as(
            String.class), filter.getRequestId()));
      }
      if (filter.getUser() != null) {
        predicates.add(generatePredicate(cb, eventEntity.get("user").as(
            String.class), filter.getUser()));
      }
    }
    if (predicates.size() != 0) {
      Predicate[] p = new Predicate[predicates.size()];
      if (query.getOperator() == QueryOperator.AND) {
        cq.where(cb.and(predicates.toArray(p)));
      } else {
        cq.where(cb.or(predicates.toArray(p)));
      }
    }

    List<Order> orders = new ArrayList<Order>();
    if (query.getSort().size() > 0) {
      for (String sort : query.getSort()) {
        String[] s = sort.split(" ");
        if (s.length == 1 || s[1].equalsIgnoreCase("ASC")) {
          orders.add(cb.asc(eventEntity.get(s[0])));
        } else {
          orders.add(cb.desc(eventEntity.get(s[0])));
        }
      }
    } else {
      orders.add(cb.asc(eventEntity.get("guid")));
    }
    cq.orderBy(orders);
    TypedQuery<AuditableEvent> dbQuery = entityManager.createQuery(cq);

    if (rows == -1) {
      return new PagedResponse<AuditableEvent>(start, (long) dbQuery
          .getResultList().size(), dbQuery.setFirstResult(start - 1)
          .getResultList());
    }

    return new PagedResponse<AuditableEvent>(start, (long) dbQuery
        .getResultList().size(), dbQuery.setFirstResult(start - 1)
        .setMaxResults(rows).getResultList());
  }

}
