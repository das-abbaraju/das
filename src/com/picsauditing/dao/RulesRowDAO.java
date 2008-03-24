package com.picsauditing.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.entities.RulesRow;

import java.util.List;

@Transactional
public class RulesRowDAO {
	EntityManager em = null;

	@PersistenceContext
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}
	private EntityManager getEntityManager() {
        return em;
    }

	public RulesRow save(RulesRow o) {
		if (o.getRowID() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}
	public void remove(int id) {
		RulesRow row = find(id);
        if (row != null) {
            em.remove(row);
        }
    }
	
	@SuppressWarnings("unchecked")
    public List<RulesRow> findAll() {
        Query query = getEntityManager().createQuery("select r FROM RulesRowBean r");
        return query.getResultList();
    }
	
	public RulesRow find(int id) {
        return em.find(RulesRow.class, id);
    }

}
