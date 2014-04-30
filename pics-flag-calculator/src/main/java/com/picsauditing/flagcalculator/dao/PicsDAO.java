package com.picsauditing.flagcalculator.dao;

import com.picsauditing.flagcalculator.entities.BaseTable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

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

	@Transactional(propagation = Propagation.NESTED)
	public int deleteData(Class<? extends BaseTable> clazz, String where) {
		Query query = em.createQuery("DELETE " + clazz.getName() + " t WHERE " + where);
		return query.executeUpdate();
	}
}