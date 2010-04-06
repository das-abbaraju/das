package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.Employee;

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
		Query query = em.createQuery("SELECT e FROM Employee e " + where + " ORDER BY e.account.name, e.name");
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

}
