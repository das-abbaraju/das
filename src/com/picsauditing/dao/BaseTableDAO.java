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
		super();
		this.clazz = clazz;
	}

	@PersistenceContext
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}
	
	public void setClass(Class<T> clazz) {
		this.clazz = clazz;
	}

	public void clear() {
		em.clear();
	}

	public boolean isContained(T o) {
		return em.contains(o);
	}

	@Transactional(propagation = Propagation.NESTED)
	public T save(T o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		
		return o;
	}

	public void refresh(T o) {
		if (em.contains(o)) {
			em.refresh(o);
		}
	}

	@Transactional(propagation = Propagation.NESTED)
	public void remove(T o) {
		if (o != null) {
			em.remove(o);
		}
	}

	public T find(int id) {
		return em.find(clazz, id);
	}

	@SuppressWarnings("unchecked")
	protected List<T> findAll() {		
		Query q = em.createQuery("FROM " + clazz.getName() + " t ORDER BY t.id");
		return q.getResultList();
	}

}
