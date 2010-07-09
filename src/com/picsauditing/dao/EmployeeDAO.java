package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.Employee;
import com.picsauditing.util.Strings;

@Transactional
@SuppressWarnings("unchecked")
public class EmployeeDAO extends PicsDAO {

	public Employee find(int id) {
		return em.find(Employee.class, id);
	}

	public List<Employee> findAll() {
		return (List<Employee>) findAll(Employee.class);
	}

	public List<Employee> findWhere(String where) {
		return findWhere(where, -1);
	}

	public List<Employee> findWhere(String where, int limit) {
		if (where == null)
			where = "";
		if (where.length() > 0)
			where = "WHERE " + where;
		Query query = em.createQuery("SELECT e FROM Employee e " + where + " ORDER BY e.lastName, e.firstName");
		if (limit > 0)
			query.setMaxResults(limit);
		return query.getResultList();
	}

	public List<Employee> findRandom(int limit) {
		Query query = em.createQuery("SELECT e FROM Employee e ORDER BY RAND()");
		if (limit > 0)
			query.setMaxResults(limit);
		return query.getResultList();
	}

	public List<Employee> findByEmail(String email) {
		Query query = em.createQuery("SELECT e FROM Employee e WHERE email = ?");
		query.setParameter(1, email);
		query.setMaxResults(10);
		return query.getResultList();
	}

	public List<Employee> findByAccount(Account account) {
		Query query = em.createQuery("SELECT e FROM Employee e WHERE e.account = :account ORDER BY e.firstName");
		query.setParameter("account", account);
		return query.getResultList();
	}

	public List<Employee> findByJobRole(int jobRoleID, int accountID) {
		Query query = em.createQuery("SELECT e FROM Employee e"
				+ " WHERE e.id IN (SELECT er.employee.id FROM EmployeeRole er WHERE er.jobRole.id = ?)"
				+ " AND e.account.id = ? ORDER BY e.lastName");
		query.setParameter(1, jobRoleID);
		query.setParameter(2, accountID);
		return query.getResultList();
	}

	public List<Employee> findByCompetencies(int[] competencyIDs, int accountID) {
		Query query = em.createQuery("SELECT e FROM Employee e WHERE e.id IN "
				+ "(SELECT ec.employee.id FROM EmployeeCompetency ec WHERE ec.competency.id IN ("
				+ Strings.implode(competencyIDs) + ")) AND e.account.id = ? ORDER BY e.lastName");

		query.setParameter(1, accountID);

		return query.getResultList();
	}

	public List<String> findCommonLocations(int accountID) {
		Query query = em.createQuery("SELECT DISTINCT e.location FROM Employee e "
				+ "WHERE e.account.id = :accountID AND e.location NOT LIKE '' "
				+ "GROUP BY location HAVING COUNT(*) > 1 ORDER BY COUNT(*) DESC");

		query.setParameter("accountID", accountID);

		return query.getResultList();
	}
	
	public List<String> findCommonTitles(){
		/*
		 *  select title, count(*) from employee
			where accountID in (select id from accounts where status in ('Active','Pending'))
			group by title
			having count(*) > 1
			order by title
		 */
		Query query = em.createQuery("SELECT e.title FROM Employee e " +
				"WHERE e.account.id IN (SELECT a.id FROM Account a WHERE a.status IN ('Active', 'Pending'))" +
				"GROUP BY e.title HAVING COUNT(*) > 1 ORDER BY e.title");
		return query.getResultList();
	}
}
