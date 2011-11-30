package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import com.picsauditing.jpa.entities.ReportQuery;
import com.picsauditing.jpa.entities.ReportQueryColumn;

@SuppressWarnings("unchecked")
public class ReportQueryDAO extends PicsDAO {

	public ReportQuery find(int id) {
		ReportQuery a = em.find(ReportQuery.class, id);
		return a;
	}

	public List<ReportQuery> findByParent(int parentID) {
		Query query = em.createQuery("SELECT r FROM ReportQuery r WHERE r.parent.id = ?");
		query.setParameter(1, parentID);
		return query.getResultList();
	}

	public List<ReportQuery> findWhere(String where) {
		if (where == null)
			where = "";
		else
			where = " WHERE " + where;

		Query query = em.createQuery("SELECT r FROM ReportQuery r" + where);
		query.setMaxResults(100);
		return query.getResultList();
	}

	public List<ReportQueryColumn> findByQuery(int QueryID) {
		Query query = em.createQuery("SELECT r FROM ReportQueryColumn r WHERE r.Query.id = ?");
		query.setParameter(1, QueryID);
		return query.getResultList();
	}
}