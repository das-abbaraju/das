package com.picsauditing.employeeguard.daos;

import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Role;
import com.picsauditing.employeeguard.entities.RoleEmployee;

import javax.persistence.TypedQuery;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

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

	public List<RoleEmployee> findByContractorAndSiteId(final int contractorId, final int siteId) {
		TypedQuery<RoleEmployee> query = em.createQuery("SELECT re FROM RoleEmployee re " +
				"JOIN re.employee as e " +
				"JOIN re.role r " +
				"WHERE e.accountId = :contractorId " +
				"AND r.accountId = :siteId", RoleEmployee.class);
		query.setParameter("contractorId", contractorId);
		query.setParameter("siteId", siteId);
		return query.getResultList();
	}

	public List<RoleEmployee> findSiteRolesByContractorAndRoleId(final int contractorId, final Role siteRole) {
		TypedQuery<RoleEmployee> query = em.createQuery("SELECT re FROM RoleEmployee re " +
				"JOIN re.employee as e " +
				"WHERE e.accountId = :contractorId " +
				"AND re.role = :role", RoleEmployee.class);
		query.setParameter("contractorId", contractorId);
		query.setParameter("role", siteRole);
		return query.getResultList();
	}

	public List<RoleEmployee> findByEmployeeAndSiteId(final int employeeId, final int siteId) {
		TypedQuery<RoleEmployee> query = em.createQuery("FROM RoleEmployee re " +
				"WHERE re.employee.id = :employeeId " +
				"AND re.role.accountId = :siteId", RoleEmployee.class);
		query.setParameter("employeeId", employeeId);
		query.setParameter("siteId", siteId);
		return query.getResultList();
	}

	public List<RoleEmployee> findByEmployeesAndSiteId(final Collection<Employee> employees,
	                                                   final int siteId) {
		return findByEmployeesAndSiteIds(employees, Arrays.asList(siteId));
	}

	public List<RoleEmployee> findByEmployeesAndSiteIds(final Collection<Employee> employees,
	                                                    final Collection<Integer> siteIds) {
		TypedQuery<RoleEmployee> query = em.createQuery("SELECT re FROM RoleEmployee re " +
				"WHERE re.employee IN (:employees) " +
				"AND re.role.accountId IN (:siteIds)", RoleEmployee.class);
		query.setParameter("employees", employees);
		query.setParameter("siteIds", siteIds);
		return query.getResultList();
	}

	public List<RoleEmployee> findByEmployeeAndSiteIds(final int employeeId, final List<Integer> siteIds) {
		TypedQuery<RoleEmployee> query = em.createQuery("FROM RoleEmployee re " +
				"WHERE re.employee.id = :employeeId " +
				"AND re.role.accountId IN (:siteIds)", RoleEmployee.class);
		query.setParameter("employeeId", employeeId);
		query.setParameter("siteIds", siteIds);
		return query.getResultList();
	}
}
