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
		if (profile == null) {
			return Collections.emptyList();
		}

		TypedQuery<GroupEmployee> query = em.createQuery("FROM GroupEmployee age WHERE age.employee.profile = :profile", GroupEmployee.class);
		query.setParameter("profile", profile);
		return query.getResultList();
	}

	public List<Group> findByAccountAndEmployee(final int accountId, final int employeeId) {
		if (accountId == 0 || employeeId == 0) {
			return Collections.emptyList();
		}

		TypedQuery<Group> query = em.createQuery("SELECT age.group FROM GroupEmployee age WHERE age.group.accountId = :accountId AND age.employee.id = :employeeId", Group.class);
		query.setParameter("accountId", accountId);
		query.setParameter("employeeId", employeeId);
		return query.getResultList();
	}

	public GroupEmployee findByGroupAndEmployee(final Employee employee, final Group group) {
		if (employee == null || group == null) {
			return null;
		}

		TypedQuery<GroupEmployee> query = em.createQuery("FROM GroupEmployee age WHERE age.employee = :employee AND age.group = :group", GroupEmployee.class);
		query.setParameter("employee", employee);
		query.setParameter("group", group);
		return query.getSingleResult();
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
