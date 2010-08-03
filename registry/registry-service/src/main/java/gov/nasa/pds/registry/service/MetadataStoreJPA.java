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
import gov.nasa.pds.registry.model.PagedResponse;
import gov.nasa.pds.registry.model.Product;
import gov.nasa.pds.registry.model.Slot;
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

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void deleteProduct(String lid, String userVersion) {
		Product product = this.getProduct(lid, userVersion);
		entityManager.remove(product);
		entityManager.flush();
	}

	// TODO: Update to return null when product is not found or an exception.
	@Transactional(readOnly = true)
	public Product getProduct(String lid, String userVersion) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Product> cq = cb.createQuery(Product.class);
		Root<Product> productEntity = cq.from(Product.class);
		Path<String> lidAttr = productEntity.get("lid");
		Path<String> userVersionAttr = productEntity.get("userVersion");
		cq.where(cb.and(cb.equal(lidAttr, lid), cb.equal(userVersionAttr,
				userVersion)));
		TypedQuery<Product> query = entityManager.createQuery(cq);
		Product product = query.getSingleResult();
		return product;
	}

	// TODO: Update to return empty list when product is not found or an
	// exception.
	@Transactional(readOnly = true)
	public List<Product> getProductVersions(String lid) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Product> cq = cb.createQuery(Product.class);
		Root<Product> productEntity = cq.from(Product.class);
		Path<String> lidAttr = productEntity.get("lid");
		cq.where(cb.equal(lidAttr, lid));
		TypedQuery<Product> query = entityManager.createQuery(cq);
		return query.getResultList();
	}

	// TODO: Update to return empty list when outside boundaries or an
	// exception.
	@Transactional(readOnly = true)
	public List<Product> getProducts(Integer start, Integer rows) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Product> cq = cb.createQuery(Product.class);
		cq.from(Product.class);
		TypedQuery<Product> query = entityManager.createQuery(cq);
		// Database is 0 indexed not 1
		return query.setFirstResult(start - 1).setMaxResults(rows)
				.getResultList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gov.nasa.pds.registry.service.MetadataStore#getProducts(gov.nasa.pds.
	 * registry.query.RegistryQuery, java.lang.Integer, java.lang.Integer)
	 */
	@Transactional(readOnly = true)
	public PagedResponse getProducts(ProductQuery query, Integer start,
			Integer rows) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Product> cq = cb.createQuery(Product.class);
		Root<Product> productEntity = cq.from(Product.class);
		ObjectFilter filter = query.getFilter();
		List<Predicate> predicates = new ArrayList<Predicate>();
		if (filter != null) {
			if (filter.getGuid() != null) {
				predicates.add(cb.like(productEntity.get("guid").as(
						String.class), filter.getGuid().replace('*', '%')));
			}
			if (filter.getLid() != null) {
				predicates.add(cb.like(productEntity.get("lid")
						.as(String.class), filter.getLid().replace('*', '%')));
			}
			if (filter.getName() != null) {
				predicates.add(cb.like(productEntity.get("name").as(
						String.class), filter.getName().replace('*', '%')));
			}
			if (filter.getObjectType() != null) {
				predicates.add(cb
						.like(productEntity.get("objectType").as(String.class),
								filter.getObjectType().replace('*', '%')));
			}
			if (filter.getStatus() != null) {
				predicates.add(cb.equal(productEntity.get("status"), filter
						.getStatus()));
			}
			if (filter.getUserVersion() != null) {
				predicates.add(cb.like(productEntity.get("userVersion").as(
						String.class), filter.getUserVersion()
						.replace('*', '%')));
			}
			if (filter.getVersion() != null) {
				predicates.add(cb.like(productEntity.get("version").as(
						String.class), filter.getVersion().replace('*', '%')));
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
		PagedResponse response = new PagedResponse(start, (long) dbQuery
				.getResultList().size());
		response.setResults(dbQuery.setFirstResult(start - 1).setMaxResults(
				rows).getResultList());
		return response;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gov.nasa.pds.registry.service.MetadataStore#getAssociations(gov.nasa.
	 * pds.registry.query.AssociationQuery, java.lang.Integer,
	 * java.lang.Integer)
	 */
	public PagedResponse getAssociations(AssociationQuery query, Integer start,
			Integer rows) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Association> cq = cb.createQuery(Association.class);
		Root<Association> associationEntity = cq.from(Association.class);
		AssociationFilter filter = query.getFilter();
		List<Predicate> predicates = new ArrayList<Predicate>();
		if (filter != null) {
			if (filter.getTargetLid() != null) {
				predicates
						.add(cb.like(associationEntity.get("targetLid").as(
								String.class), filter.getTargetLid().replace(
								'*', '%')));
			}
			if (filter.getTargetVersion() != null) {
				predicates.add(cb.like(associationEntity.get("targetVersion")
						.as(String.class), filter.getTargetVersion().replace(
						'*', '%')));
			}
			if (filter.getTargetHome() != null) {
				predicates.add(cb
						.like(associationEntity.get("targetHome").as(
								String.class), filter.getTargetHome().replace(
								'*', '%')));
			}
			if (filter.getSourceLid() != null) {
				predicates
						.add(cb.like(associationEntity.get("sourceLid").as(
								String.class), filter.getSourceLid().replace(
								'*', '%')));
			}
			if (filter.getSourceVersion() != null) {
				predicates.add(cb.like(associationEntity.get("sourceVersion")
						.as(String.class), filter.getSourceVersion().replace(
						'*', '%')));
			}
			if (filter.getSourceHome() != null) {
				predicates.add(cb
						.like(associationEntity.get("sourceHome").as(
								String.class), filter.getSourceHome().replace(
								'*', '%')));
			}
			if (filter.getAssociationType() != null) {
				predicates.add(cb.like(associationEntity.get("associationType")
						.as(String.class), filter.getAssociationType().replace(
						'*', '%')));
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
		PagedResponse response = new PagedResponse(start, (long) dbQuery
				.getResultList().size());
		response.setResults(dbQuery.setFirstResult(start - 1).setMaxResults(
				rows).getResultList());
		return response;
	}

	@Transactional(readOnly = true)
	public long getNumProducts() {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Product> productEntity = cq.from(Product.class);
		cq.select(cb.count(productEntity));
		return entityManager.createQuery(cq).getSingleResult().longValue();
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void saveProduct(Product product) {
		entityManager.persist(product);
		for (Slot slot : product.getSlots()) {
			entityManager.persist(slot);
		}
		entityManager.flush();
	}

	// TODO: Update to return null when product is not found or an exception.
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public Product updateProduct(Product product) {
		entityManager.merge(product);
		entityManager.flush();
		return getProduct(product.getGuid());
	}

	@Transactional(readOnly = true)
	public boolean hasProduct(String lid, String userVersion) {
		return false;
	}

	// TODO: Update to return null when product is not found or an exception.
	@Transactional(readOnly = true)
	public Product getProduct(String guid) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Product> cq = cb.createQuery(Product.class);
		Root<Product> productEntity = cq.from(Product.class);
		Path<String> guidAttr = productEntity.get("guid");
		cq.where(cb.equal(guidAttr, guid));
		TypedQuery<Product> query = entityManager.createQuery(cq);
		return query.getSingleResult();
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void saveAuditableEvent(AuditableEvent event) {
		entityManager.persist(event);
		entityManager.flush();
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void saveAssociation(Association association) {
		entityManager.persist(association);
		entityManager.flush();
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public Association updateAssociation(Association association) {
		entityManager.merge(association);
		entityManager.flush();
		return getAssociation(association.getGuid());
	}

	// TODO: Update to return null when product is not found or an exception.
	@Transactional(readOnly = true)
	public Association getAssociation(String guid) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Association> cq = cb.createQuery(Association.class);
		Root<Association> associationEntity = cq.from(Association.class);
		Path<String> guidAttr = associationEntity.get("guid");
		cq.where(cb.equal(guidAttr, guid));
		TypedQuery<Association> query = entityManager.createQuery(cq);
		Association association = query.getSingleResult();
		return association;
	}

}
