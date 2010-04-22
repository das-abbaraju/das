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

	public List<EmployeeCompetency> findByEmployee(int employeeID) {
		Query query = em.createQuery("SELECT e FROM EmployeeCompetency e"
				+ " WHERE e.employee.id = ?");

		query.setParameter(1, employeeID);
		return query.getResultList();
	}
	
	public EmployeeCompetency findByEmployeeOperatorCompetency(int employeeID, int operatorCompetencyID) {
		Query query = em.createQuery("SELECT e FROM EmployeeCompetency e "
				+ "WHERE e.employee.id = ? AND e.competency.id = ? ORDER BY e.employee.firstName");

		query.setParameter(1, employeeID);
		query.setParameter(2, operatorCompetencyID);
		
		return (EmployeeCompetency) query.getSingleResult();
	}

	public List<EmployeeCompetency> findByContractor(int conID) {
		Query query = em.createQuery("SELECT e FROM EmployeeCompetency e "
				+ "WHERE e.employee.account.id = ? ORDER BY e.employee.firstName");

		query.setParameter(1, conID);
		return query.getResultList();
	}
	
	public List<EmployeeCompetency> findByJobRole(int jobRoleID, int employeeID) {
		Query query = em.createQuery("SELECT e FROM EmployeeCompetency e"
				+ " WHERE e.competency IN (SELECT jc.competency FROM JobCompetency jc WHERE jc.jobRole.id = ?)"
				+ " AND e.employee.id = ? GROUP BY e.competency.id");

		query.setParameter(1, jobRoleID);
		query.setParameter(2, employeeID);
		return query.getResultList();
	}
}
