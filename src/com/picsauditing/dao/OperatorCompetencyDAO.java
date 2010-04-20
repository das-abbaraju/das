package com.picsauditing.dao;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.Employee;
import com.picsauditing.jpa.entities.EmployeeCompetency;
import com.picsauditing.jpa.entities.JobCompetencyStats;
import com.picsauditing.jpa.entities.OperatorCompetency;
import com.picsauditing.util.DoubleMap;
import com.picsauditing.util.Strings;

@Transactional
@SuppressWarnings("unchecked")
public class OperatorCompetencyDAO extends PicsDAO {
	
	public OperatorCompetency find(int id) {
		OperatorCompetency o = em.find(OperatorCompetency.class, id);
		return o;
	}

	public List<OperatorCompetency> findAll() {
		Query q = em.createQuery("FROM OperatorCompetency o ORDER BY o.category, o.label");
		return q.getResultList();
	}
	
	public List<OperatorCompetency> findWhere(String where) {
		if (where == null)
			where = "";
		if (where.length() > 0)
			where = " WHERE " + where;
		
		Query q = em.createQuery("FROM OperatorCompetency o" + where + " ORDER BY o.category, o.label");
		return q.getResultList();
	}

	public List<OperatorCompetency> findByOperator(int opID) {
		Query query = em.createQuery("SELECT o FROM OperatorCompetency o WHERE opID = :opID " +
				"ORDER BY o.category, o.label");

		query.setParameter("opID", opID);
		return query.getResultList();
	}
	
	public List<OperatorCompetency> findByContractor(int conID) {
		Query query = em.createQuery("SELECT o FROM OperatorCompetency o " +
				"WHERE o IN (SELECT j.competency FROM JobCompetency j WHERE j.jobRole.account.id = ? " +
				"AND j.jobRole.active = 1) ORDER BY o.category, o.label");

		query.setParameter(1, conID);
		return query.getResultList();
	}
	
	public List<OperatorCompetency> findByJobRole(int jobRoleID) {
		Query query = em.createQuery("SELECT o FROM OperatorCompetency o"
				+ " WHERE o IN (SELECT j.competency FROM JobCompetency j WHERE j.jobRole.id = ?"
				+ " AND j.jobRole.active = 1) ORDER BY o.category, o.label");

		query.setParameter(1, jobRoleID);
		return query.getResultList();
	}
	
	public List<String> findDistinctCategories(){
		Query query = em.createQuery("SELECT DISTINCT o.category FROM OperatorCompetency o ORDER BY o.category");
		return query.getResultList();
	}
	
	public DoubleMap<Employee, OperatorCompetency, EmployeeCompetency> findEmployeeCompetencies(
			Collection<Employee> employees, Collection<OperatorCompetency> opCompetencies) {
		int[] eIDs = new int[employees.size()];
		int[] oIDs = new int[opCompetencies.size()];
		
		Iterator<Employee> ei = employees.iterator();
		Iterator<OperatorCompetency> oi = opCompetencies.iterator();
		
		for (int i = 0; i < eIDs.length; i++) {
			eIDs[i] = ei.next().getId();
		}
		
		for (int i = 0; i < oIDs.length; i++) {
			oIDs[i] = oi.next().getId();
		}
		
		Query query = em.createQuery("SELECT e FROM EmployeeCompetency e WHERE e.employee.id IN ("
				+ Strings.implode(eIDs) + ") AND e.competency.id IN (" + Strings.implode(oIDs) + ")" +
				"AND e.competency.id IN (SELECT jc.competency.id FROM JobCompetency jc WHERE jc.jobRole.active = 1)");
		
		List<EmployeeCompetency> results = query.getResultList();
		
		DoubleMap<Employee, OperatorCompetency, EmployeeCompetency> map = new DoubleMap<Employee, OperatorCompetency, EmployeeCompetency>();
		for (EmployeeCompetency employeeCompetency : results) {
			map.put(employeeCompetency.getEmployee(), employeeCompetency.getCompetency(), employeeCompetency);
		}

		return map;
	}

	public Map<OperatorCompetency, JobCompetencyStats> getJobCompetencyStats(String jobRole) {
		Query query = em.createQuery("FROM JobCompetencyStats " + "WHERE jobRole = ?");

		query.setParameter(1, jobRole);
		List<JobCompetencyStats> result = query.getResultList();
		Map<OperatorCompetency, JobCompetencyStats> map = new HashMap<OperatorCompetency, JobCompetencyStats>();
		for (JobCompetencyStats row : result) {
			map.put(row.getCompetency(), row);
		}
		return map;
	}
}
