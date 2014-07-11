package com.picsauditing.auditbuilder.dao;

import com.picsauditing.auditbuilder.entities.BaseTable;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@SuppressWarnings("unchecked")
abstract public class PicsDAO {
	protected EntityManager em;

	@PersistenceContext
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}

	@Transactional(propagation = Propagation.NESTED)
	public BaseTable save(BaseTable o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	@Transactional(propagation = Propagation.NESTED)
	public void remove(BaseTable row) {
		if (row != null) {
			em.remove(row);
		}
	}

	public <T extends BaseTable> T find(Class<T> clazz, int id) {
		return em.find(clazz, id);
	}
}