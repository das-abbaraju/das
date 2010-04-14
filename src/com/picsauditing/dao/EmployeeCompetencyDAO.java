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

	public List<EmployeeCompetency> findAll() {
		Query q = em.createQuery("FROM EmployeeCompetency e");
		return q.getResultList();
	}

	public List<EmployeeCompetency> findByContractor(int conID) {
		Query query = em.createQuery("SELECT e FROM EmployeeCompetency e " +
				"WHERE e.employee.account.id = ? ORDER BY e.employee.firstName");

		query.setParameter(1, conID);
		return query.getResultList();
	}
}
