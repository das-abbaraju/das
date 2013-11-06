package com.picsauditing.employeeguard.daos;

import com.picsauditing.employeeguard.entities.AccountGroup;
import com.picsauditing.employeeguard.entities.AccountGroupEmployee;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Profile;

import javax.persistence.TypedQuery;
import java.util.Collections;
import java.util.List;

public class AccountGroupEmployeeDAO extends BaseEntityDAO<AccountGroupEmployee> {

    public AccountGroupEmployeeDAO() {
        this.type = AccountGroupEmployee.class;
    }

	public List<AccountGroupEmployee> findByProfile(final Profile profile) {
		if (profile == null) {
			return Collections.emptyList();
		}

		TypedQuery<AccountGroupEmployee> query = em.createQuery("FROM AccountGroupEmployee age WHERE age.employee.profile = :profile", AccountGroupEmployee.class);
		query.setParameter("profile", profile);
		return query.getResultList();
	}

	public List<AccountGroup> findByAccountAndEmployee(final int accountId, final int employeeId) {
		if (accountId == 0 || employeeId == 0) {
			return Collections.emptyList();
		}

		TypedQuery<AccountGroup> query = em.createQuery("SELECT age.group FROM AccountGroupEmployee age WHERE age.group.accountId = :accountId AND age.employee.id = :employeeId", AccountGroup.class);
		query.setParameter("accountId", accountId);
		query.setParameter("employeeId", employeeId);
		return query.getResultList();
	}

	public AccountGroupEmployee findByGroupAndEmployee(final Employee employee, final AccountGroup group) {
		if (employee == null || group == null) {
			return null;
		}

		TypedQuery<AccountGroupEmployee> query = em.createQuery("FROM AccountGroupEmployee age WHERE age.employee = :employee AND age.group = :group", AccountGroupEmployee.class);
		query.setParameter("employee", employee);
		query.setParameter("group", group);
		return query.getSingleResult();
	}
}
