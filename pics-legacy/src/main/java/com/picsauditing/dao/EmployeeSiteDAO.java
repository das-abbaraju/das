package com.picsauditing.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import com.picsauditing.jpa.entities.Employee;
import com.picsauditing.jpa.entities.EmployeeSite;
import com.picsauditing.jpa.entities.EmployeeSiteTask;
import com.picsauditing.jpa.entities.OperatorAccount;

@SuppressWarnings("unchecked")
public class EmployeeSiteDAO extends PicsDAO {

	public EmployeeSite find(int id) {
		return em.find(EmployeeSite.class, id);
	}

	public List<EmployeeSite> findSitesByEmployee(Employee e) {
		Query query = em.createQuery("SELECT e FROM EmployeeSite e WHERE e.employee = ?");
		query.setParameter(1, e);
		return query.getResultList();
	}

	public List<EmployeeSite> findSitesByOperator(OperatorAccount o) {
		Query query = em.createQuery("SELECT e FROM EmployeeSite e WHERE e.operator = ?");
		query.setParameter(1, o);
		return query.getResultList();
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

	public EmployeeSite findByEmployeeAndOperator(int employeeID, int operatorID) {
		Query q = em.createQuery("FROM EmployeeSite WHERE employee.id = :employeeID AND operator.id = :operatorID");
		q.setParameter("employeeID", employeeID);
		q.setParameter("operatorID", operatorID);

		return (EmployeeSite) q.getSingleResult();
	}

	public List<EmployeeSiteTask> findTasksByEmployeeSite(int employeeID) {
		Query q = em.createQuery("FROM EmployeeSiteTask WHERE employeeSite.employee.id = ?");
		q.setParameter(1, employeeID);

		return q.getResultList();
	}

	public List<EmployeeSiteTask> findTasksByOperator(int opID) {
		Query q = em.createQuery("FROM EmployeeSiteTask WHERE employeeSite.operator.id = ?");
		q.setParameter(1, opID);

		return q.getResultList();
	}

	public List<Employee> findEmployeesBySite(int jobSiteID, int conID) {
		Query q = em.createQuery("SELECT DISTINCT es.employee FROM EmployeeSite es "
				+ "WHERE es.jobSite.id = :jobSiteID AND es.employee.account.id = :conID "
				+ "AND (es.expirationDate > :date OR es.expirationDate IS NULL) "
				+ "AND (es.jobSite.projectStop > :date OR es.jobSite.projectStop IS NULL) "
				+ "ORDER BY es.employee.lastName, es.employee.firstName");
		q.setParameter("jobSiteID", jobSiteID);
		q.setParameter("conID", conID);
		q.setParameter("date", new Date());

		return q.getResultList();
	}
}