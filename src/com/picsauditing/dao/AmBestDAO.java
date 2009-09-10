package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.PICS.Utilities;
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

	@SuppressWarnings("unchecked")
	public AmBest findByNaic(String naic) {
		Query q = em.createQuery("SELECT ab FROM AmBest ab WHERE ab.naic = ?");
		q.setParameter(1, naic);

		List<AmBest> list = (List<AmBest>) q.getResultList();
		if (list != null && list.size() > 0) {
			if(list.size() == 1)
				return list.get(0);
			else {
				for(AmBest amBest : list) {
					if(amBest.getRatingCode() < 70) {
						return amBest;
					}
				}
			}
		}	
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<AmBest> findByCompanyName(String name) {
		name = Utilities.escapeQuotes(name);
		Query q = em.createQuery("SELECT ab FROM AmBest ab " +
				"WHERE ab.companyName LIKE '%" + parseCompany(name) + "%' " +
					"OR ab.naic LIKE '" + name + "%' " +
				"ORDER BY CASE ab.naic WHEN '0' THEN 1 ELSE 0 END, ab.companyName");
		q.setMaxResults(20);
		return q.getResultList();
	}

	static public AmBest getAmBest(String naic) {
		AmBest amb;
		try {
			AmBestDAO amBestDAO = (AmBestDAO) SpringUtils.getBean("AmBestDAO");
			amb = amBestDAO.findByNaic(naic);
			return amb;
		} catch (Exception e) {
			System.out.println("Failed to query AmBest for " + naic + " error=" + e.getMessage());
			return null;
		}
	}

	/**
	 * Remove the (naic) data from the end of a company name when doing searches
	 * @param fullCompany
	 * @return
	 */
	static public String parseCompany(String fullCompany) {
		int start = fullCompany.lastIndexOf("(");
		int end = fullCompany.lastIndexOf(")");
		if (start < 1 || end < 1 || start > end)
			return fullCompany;

		return fullCompany.substring(0, start).trim();
	}
}
