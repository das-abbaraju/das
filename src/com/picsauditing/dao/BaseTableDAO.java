package com.picsauditing.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.BaseTable;


public class BaseTableDAO<T extends BaseTable> {

	protected EntityManager em;
	protected Class<T> clazz;
	
	public BaseTableDAO(Class<T> clazz) {
		this.clazz = clazz;
	}

	@PersistenceContext
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}

	public void clear() {
		em.clear();
	}

	public boolean isContained(T entity) {
		return em.contains(entity);
	}

	@Transactional(propagation = Propagation.NESTED)
	public BaseTable save(T entity) {
		if (entity.getId() == 0) {
			em.persist(entity);
		} else {
			entity = em.merge(entity);
		}
		
		return entity;
	}

	public void refresh(T entity) {
		if (em.contains(entity)) {
			em.refresh(entity);
		}
	}

	@Transactional(propagation = Propagation.NESTED)
	public void remove(T entity) {
		if (entity != null) {
			em.remove(entity);
		}
	}
	
	@Transactional(propagation = Propagation.NESTED)
	public int deleteData(String where) {
		Query query = em.createQuery("DELETE " + clazz.getName() + " t WHERE " + where);
		return query.executeUpdate();
	}

	public T findById(int id) {
		return em.find(clazz, id);
	}

	@SuppressWarnings("unchecked")
	public List<T> findAll() {
		Query q = em.createQuery("FROM " + clazz.getName() + " t ORDER BY t.id");
		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	public T findOne(String where) {
		Query q = em.createQuery("FROM " + clazz.getName() + " t WHERE " + where);
		return (T) q.getSingleResult();
	}

	public List<T> findWhere(String where) {
		return findWhere(where, 0);
	}

	public List<T> findWhere(String where, int limit) {
		return findWhere(where, limit, " t.id");
	}

	@SuppressWarnings("unchecked")
	public List<T> findWhere(String where, int limit, String orderBy) {
		Query q = em.createQuery("FROM " + clazz.getName() + " t WHERE " + where + " ORDER BY " + orderBy);
		if (limit > 0) {
			q.setMaxResults(limit);
		}
		
		return q.getResultList();
	}

	public int getCount(String where) {
		Query q = em.createQuery("SELECT COUNT(*) FROM " + clazz.getName() + " WHERE " + where);
		int result = 0;
		try {
			result = (Integer) q.getSingleResult();
		} catch (Exception e) {
			result = 0;
		}

		return result;
	}

}
