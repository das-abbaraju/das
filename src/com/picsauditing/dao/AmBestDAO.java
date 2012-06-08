package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.AmBest;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.util.Strings;

@SuppressWarnings("unchecked")
public class AmBestDAO extends PicsDAO {
	private final static Logger logger = LoggerFactory.getLogger(AmBestDAO.class);
	
	@Transactional(propagation = Propagation.NESTED)
	public AmBest save(AmBest o) {
		if (o.getAmBestId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	@Transactional(propagation = Propagation.NESTED)
	public void remove(AmBest row) {
		if (row != null) {
			em.remove(row);
		}
	}

	@Transactional(propagation = Propagation.NESTED)
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

	public List<AmBest> findByCompanyName(String name) {
		name = Strings.escapeQuotes(name);
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
			logger.error("Failed to query AmBest for {} error = {}", naic, e.getMessage());
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
