package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.EmployeeCompetency;

@Transactional
@SuppressWarnings("unchecked")
public class EmployeeCompetencyDAO extends PicsDAO {
	public EmployeeCompetency find(int id) {
		EmployeeCompetency ec = em.find(EmployeeCompetency.class, id);
		return ec;
	}

	public EmployeeCompetency find(int employeeID, int competencyID) {
		Query query = em.createQuery("FROM EmployeeCompetency e WHERE e.employee.id = ? AND e.competency.id = ?");
		query.setParameter(1, employeeID);
		query.setParameter(2, competencyID);
		try {
			return (EmployeeCompetency) query.getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}

	public List<EmployeeCompetency> findAll() {
		Query q = em.createQuery("FROM EmployeeCompetency e");
		return q.getResultList();
	}
}
