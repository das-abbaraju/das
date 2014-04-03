package com.picsauditing.employeeguard.daos;

import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Role;
import com.picsauditing.employeeguard.entities.RoleEmployee;

import javax.persistence.TypedQuery;

public class RoleEmployeeDAO extends AbstractBaseEntityDAO<RoleEmployee> {

	public RoleEmployeeDAO() {
		this.type = RoleEmployee.class;
	}

	public RoleEmployee findByEmployeeAndRole(final Employee employee, final Role role) {
		if (employee == null || role == null) {
			return null;
		}

		try {
			TypedQuery<RoleEmployee> query = em.createQuery("FROM RoleEmployee re " +
					"WHERE re.employee = :employee " +
					"AND re.role = :role", RoleEmployee.class);
			query.setParameter("employee", employee);
			query.setParameter("role", role);
			return query.getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}
}
