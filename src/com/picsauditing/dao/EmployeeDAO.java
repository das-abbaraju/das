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
		Query query = em.createQuery("SELECT e FROM Employee e " + where + " ORDER BY e.firstName");
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
		Query query = em.createQuery("SELECT e.employee FROM Employee e WHERE e.account = :account");
		query.setParameter("account", account);
		return query.getResultList();
	}
	
	public List<Employee> findByCompetencies(int[] competencyIDs) {
		Query query = em.createQuery("SELECT e FROM Employee e WHERE e.id IN "
				+ "(SELECT ec.employee.id FROM EmployeeCompetency ec WHERE ec.competency.id IN ("
				+ Strings.implode(competencyIDs) + ")) ORDER BY e.firstName");
		return query.getResultList();
	}
}
