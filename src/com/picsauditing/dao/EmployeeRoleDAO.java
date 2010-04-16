package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.EmployeeRole;

@Transactional
@SuppressWarnings("unchecked")
public class EmployeeRoleDAO extends PicsDAO {
	public EmployeeRole find(int id) {
		return em.find(EmployeeRole.class, id);
	}

	public List<EmployeeRole> findAll() {
		return (List<EmployeeRole>) super.findAll(EmployeeRole.class);
	}

	public List<EmployeeRole> findByJobRole(int jobRoleID) {
		Query query = em.createQuery("SELECT e FROM EmployeeRole e " +
				"WHERE e.jobRole.id = ? ORDER BY e.employee.firstName");

		query.setParameter(1, jobRoleID);
		return query.getResultList();
	}
	
	public List<EmployeeRole> findByEmployee(int employeeID) {
		Query query = em.createQuery("SELECT e FROM EmployeeRole e "
				+ "WHERE e.employee.id = ? ORDER BY e.employee.firstName");

		query.setParameter(1, employeeID);
		return query.getResultList();
	}

	public List<EmployeeRole> findByContractor(int conID) {
		Query query = em.createQuery("SELECT e FROM EmployeeRole e "
				+ "WHERE e.employee.account.id = ? ORDER BY e.employee.firstName");

		query.setParameter(1, conID);
		return query.getResultList();
	}

	public EmployeeRole findByEmployeeAndJobRole(int employeeID, int jobRoleID) {
		Query q = em.createQuery("FROM EmployeeRole WHERE employee.id = :employeeID AND jobRole.id = :jobRoleID");
		q.setParameter("employeeID", employeeID);
		q.setParameter("jobRoleID", jobRoleID);

		return (EmployeeRole) q.getSingleResult();
	}
}