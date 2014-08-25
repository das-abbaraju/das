package com.picsauditing.auditbuilder.dao;

import com.picsauditing.auditbuilder.entities.BaseTable;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Collection;
import java.util.List;

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


    public <T extends BaseTable> List<T> findByIDs(Class<T> clazz, Collection<Integer> ids) {
        Query q = em.createQuery("FROM " + clazz.getName() + " t WHERE t.id IN ( :ids )");
        q.setParameter("ids", ids);
        return q.getResultList();
    }

}