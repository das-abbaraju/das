package com.picsauditing.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
				"WHERE e.jobRole.id = ? ORDER BY e.employee.lastName");

		query.setParameter(1, jobRoleID);
		return query.getResultList();
	}
	
	public Map<Integer, List<EmployeeRole>> findEmployeeRolesByContractor(int contractorID) {
		Query query = em.createQuery("SELECT e FROM EmployeeRole e"
				+ " WHERE e.employee.account.id = ? ORDER BY e.employee.id, e.jobRole.name");
		
		query.setParameter(1, contractorID);
		List<EmployeeRole> list = query.getResultList();
		Map<Integer, List<EmployeeRole>> map = new TreeMap<Integer, List<EmployeeRole>>();
		
		for (EmployeeRole er : list) {
			int employeeID = er.getEmployee().getId();
			
			if (map.get(employeeID) == null)
				map.put(employeeID, new ArrayList<EmployeeRole>());
			
			if (!map.get(employeeID).contains(er))
				map.get(employeeID).add(er);
		}
		
		return map;
	}

	public List<EmployeeRole> findByContractor(int conID) {
		Query query = em.createQuery("SELECT e FROM EmployeeRole e "
				+ "WHERE e.employee.account.id = ? ORDER BY e.employee.lastName");

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