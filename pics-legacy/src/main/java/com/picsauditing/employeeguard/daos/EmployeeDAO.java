package com.picsauditing.employeeguard.daos;

import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Profile;
import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.entities.Role;
import com.picsauditing.employeeguard.util.ListUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.TypedQuery;
import java.util.*;

public class EmployeeDAO extends AbstractBaseEntityDAO<Employee> {

	private static final Logger LOG = LoggerFactory.getLogger(EmployeeDAO.class);

	public EmployeeDAO() {
		this.type = Employee.class;
	}

	public List<Employee> findByAccount(final int accountId) {
		TypedQuery<Employee> query = em.createQuery("FROM Employee e WHERE e.accountId = :accountId", Employee.class);

		query.setParameter("accountId", accountId);

		return query.getResultList();
	}

	public long findEmployeeCount(final int accountId) {
		TypedQuery<Long> query = em.createQuery("SELECT COUNT(e) FROM Employee e WHERE e.accountId = :accountId",
				Long.class);

		query.setParameter("accountId", accountId);

		return query.getSingleResult();
	}

	public List<Employee> findByAccounts(final Collection<Integer> accountIds) {
		TypedQuery<Employee> query = em.createQuery("FROM Employee e WHERE e.accountId IN (:accountIds)", Employee.class);

		query.setParameter("accountIds", accountIds);

		return query.getResultList();
	}

	public Employee findEmployeeByAccount(final int employeeId, final int accountId) {
		TypedQuery<Employee> query = em.createQuery("FROM Employee e " +
				"WHERE e.accountId = :accountId AND e.id = :employeeId", Employee.class);

		query.setParameter("accountId", accountId);
		query.setParameter("employeeId", employeeId);

		return query.getSingleResult();
	}

	public Employee findEmployeeByAccountIdAndEmail(final int accountId, final String email) {
		TypedQuery<Employee> query = em.createQuery("FROM Employee e " +
				"WHERE e.accountId = :accountId AND e.email = :email", Employee.class);

		query.setParameter("accountId", accountId);
		query.setParameter("email", email);

		try {
			return query.getSingleResult();
		} catch (Exception e) {
			return null;
		}

	}

	public Employee findEmployeeByAccountIdAndSlug(final int accountId, final String slug) {
		TypedQuery<Employee> query = em.createQuery("FROM Employee e " +
				"WHERE e.accountId = :accountId " +
				"AND e.slug = :slug", Employee.class);

		query.setParameter("accountId", accountId);
		query.setParameter("slug", slug);

		try {
			return query.getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}

	public List<Employee> findByIds(final List<Integer> employeeIds) {
		TypedQuery<Employee> query = em.createQuery("FROM Employee e " +
				"WHERE e.id IN (:employeeIds)", Employee.class);

		query.setParameter("employeeIds", employeeIds);

		return query.getResultList();
	}

	public List<Employee> findByProject(final Project project) {
		if (project == null) {
			return Collections.emptyList();
		}

		return findByProjectId(project.getId());
	}

	public List<Employee> findByProjectId(final int projectId) {
		TypedQuery<Employee> query = em.createQuery("SELECT DISTINCT pre.employee FROM ProjectRoleEmployee pre " +
				"WHERE pre.projectRole.project.id = :projectId", Employee.class);

		query.setParameter("projectId", projectId);

		return query.getResultList();
	}

	public List<Employee> search(final String searchTerm, final int accountId) {
		TypedQuery<Employee> query = em.createQuery(
				"SELECT DISTINCT e FROM Employee e " +
						"WHERE e.accountId = :accountId " +
						"AND (e.firstName LIKE :searchTerm " +
						"OR e.lastName LIKE :searchTerm " +
						"OR e.slug LIKE :searchTerm " +
						"OR e.email LIKE :searchTerm " +
						"OR CONCAT(e.firstName, '%', e.lastName) LIKE :normalizedSearchTerm " +
						"OR CONCAT(e.lastName, '%', e.firstName) LIKE :normalizedSearchTerm)", Employee.class);

		query.setParameter("accountId", accountId);
		query.setParameter("searchTerm", "%" + searchTerm + "%");
		query.setParameter("normalizedSearchTerm", "%" + searchTerm.trim().replaceAll("\\s+", "%") + "%");

		return query.getResultList();
	}

	public void delete(final int id, final int accountId) {
		Employee employee = findEmployeeByAccount(id, accountId);
		super.delete(employee);
	}

	public List<Employee> findByProjects(final Collection<Project> projects) {
		TypedQuery<Employee> query = em.createQuery("SELECT DISTINCT pre.employee FROM ProjectRoleEmployee pre " +
				"WHERE pre.projectRole.project IN (:projects)", Employee.class);

		query.setParameter("projects", projects);

		return query.getResultList();
	}

	public List<Employee> findEmployeesAssignedToSites(final Collection<Integer> siteIds) {
		TypedQuery<Employee> query = em.createQuery("SELECT DISTINCT sa.employee FROM SiteAssignment sa " +
				"WHERE sa.siteId IN (:siteIds)", Employee.class);

		query.setParameter("siteIds", siteIds);

		return query.getResultList();
	}

	public List<Employee> findEmployeesAssignedToSiteByProfile(final Collection<Integer> contractorIds,
															   final int siteId,
															   final Profile profile) {
		TypedQuery<Employee> query = em.createQuery("SELECT e FROM SiteAssignment sa " +
				"JOIN sa.employee e " +
				"JOIN e.profile p " +
				"WHERE sa.siteId = :siteId " +
				"AND e.accountId IN (:contractorIds) " +
				"AND p = :profile", Employee.class);

		query.setParameter("contractorIds", contractorIds);
		query.setParameter("siteId", siteId);
		query.setParameter("profile", profile);

		return query.getResultList();

//
//		TypedQuery<Employee> query = em.createQuery("SELECT DISTINCT e FROM Employee e " +
//				"JOIN e.projectRoles pre " +
//				"JOIN pre.projectRole pr " +
//				"JOIN pr.project p " +
//				"WHERE e.accountId IN (:accountIds) " +
//				"AND p.accountId = :siteId " +
//				"AND e.profile.id = :profileId", Employee.class);
//
//		query.setParameter("accountIds", accountIds);
//		query.setParameter("siteId", siteId);
//		query.setParameter("profileId", profileId);
//
//		List<Employee> employees = query.getResultList();
//
//		query = em.createQuery("SELECT DISTINCT e FROM Employee e " +
//				"JOIN e.roles re " +
//				"JOIN re.role r " +
//				"WHERE e.accountId IN (:accountIds) " +
//				"AND r.accountId = :siteId " +
//				"AND e.profile.id = :profileId", Employee.class);
//		query.setParameter("accountIds", accountIds);
//		query.setParameter("siteId", siteId);
//		query.setParameter("profileId", profileId);
//
//		employees.addAll(query.getResultList());
//
//		return ListUtil.removeDuplicatesAndSort(employees);
	}

	public List<Employee> findEmployeesAssignedToSiteRole(final Collection<Integer> accountIds,
														  final int siteId,
														  final Role role) {
		TypedQuery<Employee> query = em.createQuery("SELECT DISTINCT e FROM SiteAssignment sa " +
				"JOIN sa.employee e " +
				"JOIN sa.role r " +
				"WHERE e.accountId IN (:accountIds) " +
				"AND sa.siteId = :siteId " +
				"AND sa.role = :role", Employee.class);

		query.setParameter("accountIds", accountIds);
		query.setParameter("siteId", siteId);
		query.setParameter("role", role);

		return query.getResultList();
	}

	public int findRequestedEmployees(final int accountId) {
		TypedQuery<Long> query = em.createQuery("SELECT COUNT(e) " +
				"FROM Employee e " +
				"WHERE e.profile IS NULL " +
				"AND e.accountId = :accountId", Long.class);

		query.setParameter("accountId", accountId);

		try {
			long result = query.getSingleResult();
			return (int) result;
		} catch (Exception e) {
			return 0;
		}
	}


	public List<Integer> findContractorsForEmployeeBySite(final int siteId, final int employeeId) {
		TypedQuery<Integer> query = em.createQuery("SELECT DISTINCT e.accountId FROM SiteAssignment sa " +
				"JOIN sa.employee e " +
				"WHERE e.id = :employeeId " +
				"AND sa.siteId = :siteId", Integer.class);

		query.setParameter("siteId", siteId);
		query.setParameter("employeeId", employeeId);

		return query.getResultList();
	}

	public List<Employee> findByProfile(final Profile profile) {
		TypedQuery<Employee> query = em.createQuery("SELECT e FROM Employee e " +
				"JOIN e.profile p " +
				"WHERE p = :profile", Employee.class);

		query.setParameter("profile", profile);

		return query.getResultList();
	}
}
