package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import com.picsauditing.jpa.entities.ReportModel;
import com.picsauditing.jpa.entities.ReportModelColumn;

@SuppressWarnings("unchecked")
public class ReportModelDAO extends PicsDAO {

	public ReportModel find(int id) {
		ReportModel a = em.find(ReportModel.class, id);
		return a;
	}

	public List<ReportModel> findByParent(int parentID) {
		Query query = em.createQuery("SELECT r FROM ReportModel r WHERE r.parent.id = ?");
		query.setParameter(1, parentID);
		return query.getResultList();
	}

	public List<ReportModel> findWhere(String where) {
		if (where == null)
			where = "";
		else
			where = " WHERE " + where;

		Query query = em.createQuery("SELECT r FROM ReportModel r" + where);
		query.setMaxResults(100);
		return query.getResultList();
	}

	public List<ReportModelColumn> findByModel(int modelID) {
		Query query = em.createQuery("SELECT r FROM ReportModelColumn r WHERE r.model.id = ?");
		query.setParameter(1, modelID);
		return query.getResultList();
	}
}