package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.EmployeeSite;

@Transactional
@SuppressWarnings("unchecked")
public class EmployeeSiteDAO extends PicsDAO {

	public EmployeeSite find(int id) {
		return em.find(EmployeeSite.class, id);
	}

	public List<EmployeeSite> findAll() {
		return (List<EmployeeSite>) findAll(EmployeeSite.class);
	}

	public List<EmployeeSite> findWhere(String where) {
		return findWhere(where, -1);
	}

	public List<EmployeeSite> findWhere(String where, int limit) {
		if (where == null)
			where = "";
		if (where.length() > 0)
			where = "WHERE " + where;
		Query query = em.createQuery("SELECT e FROM EmployeeSite e " + where);
		if (limit > 0)
			query.setMaxResults(limit);
		return query.getResultList();
	}
}
