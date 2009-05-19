package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.AmBest;

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

	@SuppressWarnings("unchecked")
	public List<AmBest> findByCompanyName(String name) {
		Query q = em.createQuery("SELECT ab FROM AmBest ab WHERE ab.companyName LIKE '%" + name +"%'");

		return q.getResultList();
	}
}
