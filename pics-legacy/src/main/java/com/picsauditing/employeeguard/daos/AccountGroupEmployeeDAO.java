package com.picsauditing.employeeguard.daos;

import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Group;
import com.picsauditing.employeeguard.entities.GroupEmployee;
import com.picsauditing.employeeguard.entities.Profile;

import javax.persistence.TypedQuery;
import java.util.Collections;
import java.util.List;

public class AccountGroupEmployeeDAO extends BaseEntityDAO<GroupEmployee> {

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
}
