package com.picsauditing.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;
import com.picsauditing.beans.RulesRowBean;

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

	public RulesRowBean save(RulesRowBean o) {
		if (o.getRowID() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}
	public void remove(int id) {
		RulesRowBean row = find(id);
        if (row != null) {
            em.remove(row);
        }
    }
	
	@SuppressWarnings("unchecked")
    public List<RulesRowBean> findAll() {
        Query query = getEntityManager().createQuery("select r FROM RulesRowBean r");
        return query.getResultList();
    }
	
	public RulesRowBean find(int id) {
        return em.find(RulesRowBean.class, id);
    }

}
