package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.EmployeeRole;

@Transactional
@SuppressWarnings("unchecked")
public class EmployeeRoleDAO extends PicsDAO {
	
	public EmployeeRole find(int id) {
		EmployeeRole e = em.find(EmployeeRole.class, id);
		return e;
	}

	public List<EmployeeRole> findAll() {
		Query q = em.createQuery("FROM EmployeeRole e");
		return q.getResultList();
	}
	
	public List<EmployeeRole> findByEmployee(int employeeID) {
		Query query = em.createQuery("SELECT e FROM EmployeeRole e " +
				"WHERE e.employee.id = ? ORDER BY e.employee.firstName");

		query.setParameter(1, employeeID);
		return query.getResultList();
	}

	public List<EmployeeRole> findByContractor(int conID) {
		Query query = em.createQuery("SELECT e FROM EmployeeRole e " +
				"WHERE e.employee.account.id = ? ORDER BY e.employee.firstName");

		query.setParameter(1, conID);
		return query.getResultList();
	}
}
