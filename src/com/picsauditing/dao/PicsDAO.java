package com.picsauditing.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.BaseTable;

@Transactional
abstract public class PicsDAO {
	protected EntityManager em;
	protected QueryMetaData queryMetaData = null;

	@PersistenceContext
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}

	public QueryMetaData getQueryMetaData() {
		return queryMetaData;
	}

	public void setQueryMetaData(QueryMetaData queryMetaData) {
		this.queryMetaData = queryMetaData;
	}

	protected void applyQueryMetaData(Query query) {
		QueryMetaData qmd = getQueryMetaData();
		if (qmd != null) {
			if (qmd.getMaxRows() != -1) {
				query.setMaxResults(qmd.getMaxRows());
			}

			if (qmd.getStartRow() != -1) {
				query.setFirstResult(qmd.getStartRow());
			}
		}
	}

	protected static void setOptionalParameter(Query query, String name, Object value) {
		try {
			query.setParameter(name, value);
		} catch (IllegalArgumentException e) {
		}
	}

	public void clear() {
		em.clear();
	}

	public boolean isContained(Object o) {
		return em.contains(o);
	}

	public BaseTable save(BaseTable o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	public void remove(BaseTable row) {
		if (row != null) {
			em.remove(row);
		}
	}

}
