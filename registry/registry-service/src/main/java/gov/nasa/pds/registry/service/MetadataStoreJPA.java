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

import gov.nasa.pds.registry.model.Association;
import gov.nasa.pds.registry.model.AuditableEvent;
import gov.nasa.pds.registry.model.ClassificationNode;
import gov.nasa.pds.registry.model.RegistryResponse;
import gov.nasa.pds.registry.model.Product;
import gov.nasa.pds.registry.model.RegistryObject;
import gov.nasa.pds.registry.query.AssociationFilter;
import gov.nasa.pds.registry.query.AssociationQuery;
import gov.nasa.pds.registry.query.ObjectFilter;
import gov.nasa.pds.registry.query.ProductQuery;
import gov.nasa.pds.registry.query.QueryOperator;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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

  /*
   * (non-Javadoc)
   * 
   * @see
   * gov.nasa.pds.registry.service.MetadataStore#getProducts(gov.nasa.pds.registry
   * .query.ProductQuery, java.lang.Integer, java.lang.Integer)
   */
  @Override
  @Transactional(readOnly = true)
  public RegistryResponse getProducts(ProductQuery query, Integer start,
      Integer rows) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Product> cq = cb.createQuery(Product.class);
    Root<Product> productEntity = cq.from(Product.class);
    ObjectFilter filter = query.getFilter();
    List<Predicate> predicates = new ArrayList<Predicate>();
    if (filter != null) {
      if (filter.getGuid() != null) {
        predicates.add(cb.like(productEntity.get("guid").as(String.class),
            filter.getGuid().replace('*', '%')));
      }
      if (filter.getLid() != null) {
        predicates.add(cb.like(productEntity.get("lid").as(String.class),
            filter.getLid().replace('*', '%')));
      }
      if (filter.getName() != null) {
        predicates.add(cb.like(productEntity.get("name").as(String.class),
            filter.getName().replace('*', '%')));
      }
      if (filter.getObjectType() != null) {
        predicates.add(cb.like(
            productEntity.get("objectType").as(String.class), filter
                .getObjectType().replace('*', '%')));
      }
      if (filter.getStatus() != null) {
        predicates.add(cb
            .equal(productEntity.get("status"), filter.getStatus()));
      }
      if (filter.getVersionId() != null) {
        predicates.add(cb.like(productEntity.get("versionId").as(String.class),
            filter.getVersionId().replace('*', '%')));
      }
      if (filter.getVersionName() != null) {
        predicates.add(cb.like(productEntity.get("versionName")
            .as(String.class), filter.getVersionName().replace('*', '%')));
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
    TypedQuery<Product> dbQuery = entityManager.createQuery(cq);
    RegistryResponse response = new RegistryResponse(start, (long) dbQuery
        .getResultList().size(), dbQuery.setFirstResult(start - 1)
        .setMaxResults(rows).getResultList());
    return response;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * gov.nasa.pds.registry.service.MetadataStore#getAssociations(java.lang.String
   * , java.lang.String, java.lang.Integer, java.lang.Integer)
   */
  @Override
  @Transactional(readOnly = true)
  public RegistryResponse getAssociations(String lid, String versionId,
      Integer start, Integer rows) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Association> cq = cb.createQuery(Association.class);
    Root<Association> associationEntity = cq.from(Association.class);
    cq.where(cb.or(cb.and(cb.equal(associationEntity.get("targetLid"), lid), cb
        .equal(associationEntity.get("targetVersionId"), versionId)), cb.and(cb
        .equal(associationEntity.get("sourceLid"), lid), cb.equal(
        associationEntity.get("sourceVersionId"), versionId))));
    List<Order> orders = new ArrayList<Order>();
    orders.add(cb.asc(associationEntity.get("guid")));
    cq.orderBy(orders);
    TypedQuery<Association> dbQuery = entityManager.createQuery(cq);
    RegistryResponse response = new RegistryResponse(start, (long) dbQuery
        .getResultList().size(), dbQuery.setFirstResult(start - 1)
        .setMaxResults(rows).getResultList());
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
  public RegistryResponse getAssociations(AssociationQuery query,
      Integer start, Integer rows) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Association> cq = cb.createQuery(Association.class);
    Root<Association> associationEntity = cq.from(Association.class);
    AssociationFilter filter = query.getFilter();
    List<Predicate> predicates = new ArrayList<Predicate>();
    if (filter != null) {
      if (filter.getTargetLid() != null) {
        predicates.add(cb.like(associationEntity.get("targetLid").as(
            String.class), filter.getTargetLid().replace('*', '%')));
      }
      if (filter.getTargetVersionId() != null) {
        predicates.add(cb.like(associationEntity.get("targetVersionId").as(
            String.class), filter.getTargetVersionId().replace('*', '%')));
      }
      if (filter.getTargetHome() != null) {
        predicates.add(cb.like(associationEntity.get("targetHome").as(
            String.class), filter.getTargetHome().replace('*', '%')));
      }
      if (filter.getSourceLid() != null) {
        predicates.add(cb.like(associationEntity.get("sourceLid").as(
            String.class), filter.getSourceLid().replace('*', '%')));
      }
      if (filter.getSourceVersionId() != null) {
        predicates.add(cb.like(associationEntity.get("sourceVersionId").as(
            String.class), filter.getSourceVersionId().replace('*', '%')));
      }
      if (filter.getSourceHome() != null) {
        predicates.add(cb.like(associationEntity.get("sourceHome").as(
            String.class), filter.getSourceHome().replace('*', '%')));
      }
      if (filter.getAssociationType() != null) {
        predicates.add(cb.like(associationEntity.get("associationType").as(
            String.class), filter.getAssociationType().replace('*', '%')));
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
    RegistryResponse response = new RegistryResponse(start, (long) dbQuery
        .getResultList().size(), dbQuery.setFirstResult(start - 1)
        .setMaxResults(rows).getResultList());
    return response;
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
    RegistryObject registryObject = this.getRegistryObject(guid, objectClass);
    entityManager.remove(registryObject);
    entityManager.flush();
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
  public RegistryObject getRegistryObject(String lid, String versionId,
      Class<? extends RegistryObject> objectClass) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<?> cq = cb.createQuery(objectClass);
    Root<?> entity = cq.from(objectClass);
    Path<String> lidAttr = entity.get("lid");
    Path<String> versionIdAttr = entity.get("versionId");
    cq
        .where(cb.and(cb.equal(lidAttr, lid), cb
            .equal(versionIdAttr, versionId)));
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
   * gov.nasa.pds.registry.service.MetadataStore#hasRegistryObject(java.lang
   * .String, java.lang.String, java.lang.Class)
   */
  @Override
  @Transactional(readOnly = true)
  public boolean hasRegistryObject(String lid, String versionId,
      Class<? extends RegistryObject> objectClass) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<?> cq = cb.createQuery(objectClass);
    Root<?> entity = cq.from(objectClass);
    Path<String> lidAttr = entity.get("lid");
    Path<String> versionIdAttr = entity.get("versionId");
    cq
        .where(cb.and(cb.equal(lidAttr, lid), cb
            .equal(versionIdAttr, versionId)));
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

}
