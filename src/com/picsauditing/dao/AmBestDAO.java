package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.AmBest;
import com.picsauditing.util.SpringUtils;

@Transactional
public class AmBestDAO extends PicsDAO {
	public AmBest save(AmBest o) {
		if (o.getAmBestId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	public void remove(AmBest row) {
		if (row != null) {
			em.remove(row);
		}
	}

	public void remove(int id) {
		AmBest row = find(id);
		if (row != null)
			em.remove(row);
	}

	public AmBest find(int id) {
		return em.find(AmBest.class, id);
	}

	public AmBest findByNaic(String naic) {
		Query q = em.createQuery("SELECT ab FROM AmBest ab WHERE ab.naic = ?");
		q.setParameter(1, naic);
		return (AmBest)q.getSingleResult();
	}
	
	@SuppressWarnings("unchecked")
	public List<AmBest> findByCompanyName(String name) {
		Query q = em.createQuery("SELECT ab FROM AmBest ab WHERE ab.companyName LIKE '%" + name +"%'");
		q.setMaxResults(20);
		return q.getResultList();
	}
	
	static public AmBest getAmBest(String naic) {
		AmBest amb;
		try {
			AmBestDAO amBestDAO = (AmBestDAO)SpringUtils.getBean("AmBestDAO");
			amb = amBestDAO.findByNaic(naic);
		} catch (Exception e) {
			amb = new AmBest();
			amb.setNaic(naic);
			amb.setCompanyName("Not found");
		}
		return amb;
	}
}
