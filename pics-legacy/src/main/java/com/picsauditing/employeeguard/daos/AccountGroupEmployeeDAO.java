package com.picsauditing.employeeguard.daos;

import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Group;
import com.picsauditing.employeeguard.entities.GroupEmployee;
import com.picsauditing.employeeguard.entities.Profile;

import javax.persistence.TypedQuery;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class AccountGroupEmployeeDAO extends AbstractBaseEntityDAO<GroupEmployee> {

	public AccountGroupEmployeeDAO() {
		this.type = GroupEmployee.class;
	}

	public List<GroupEmployee> findByProfile(final Profile profile) {
		TypedQuery<GroupEmployee> query = em.createQuery("FROM GroupEmployee age " +
				"WHERE age.employee.profile = :profile", GroupEmployee.class);

		query.setParameter("profile", profile);

		return query.getResultList();
	}

	public List<GroupEmployee> findByEmployees(final Collection<Employee> employees) {
		TypedQuery<GroupEmployee> query = em.createQuery("SELECT age FROM GroupEmployee age " +
				"JOIN age.employee e " +
				"JOIN age.group g " +
				"JOIN g.skills s " +
				"WHERE e IN (:employees)", GroupEmployee.class);

		query.setParameter("employees", employees);

		return query.getResultList();
	}
}
