package com.picsauditing.employeeguard.daos;

import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.entities.RoleEmployee;
import com.picsauditing.employeeguard.util.ListUtil;
import com.picsauditing.util.Strings;
import org.apache.commons.collections.CollectionUtils;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class EmployeeDAO extends AbstractBaseEntityDAO<Employee> {

	public static final int EMPLOYEE_BATCH_SIZE = 100;

	private static final String INSERT_EMPLOYEE_QUERY = "INSERT INTO account_employee(`accountID`,`slug`,`firstName`,`lastName`," +
			"`positionName`,`email`,`phone`,`emailToken`," +
			"`createdBy`,`updatedBy`,`deletedBy`," +
			"`createdDate`,`updatedDate`,`deletedDate`) VALUES (" +
			"?,?,?,?," +
			"?,?,?,?,?," +
			"?,?,?,?,?)"
			+ " ON DUPLICATE KEY UPDATE `slug`=?, `firstName`=?, `lastName`=?, `positionName`=?, `phone`=?, `createdBy`=?, `createdDate`=?;";

	public EmployeeDAO() {
		this.type = Employee.class;
	}

	public List<Employee> findByAccount(int accountId) {
		TypedQuery<Employee> query = em.createQuery("FROM Employee e WHERE e.accountId = :accountId", Employee.class);
		query.setParameter("accountId", accountId);
		return query.getResultList();
	}

	public long findEmployeeCount(int accountId) {
		TypedQuery<Long> query = em.createQuery("SELECT COUNT(e) FROM Employee e WHERE e.accountId = :accountId",
				Long.class);
		query.setParameter("accountId", accountId);
		return query.getSingleResult();
	}

	public List<Employee> findByAccounts(final Collection<Integer> accountIds) {
		if (CollectionUtils.isEmpty(accountIds)) {
			return Collections.emptyList();
		}

		TypedQuery<Employee> query = em.createQuery("FROM Employee e WHERE e.accountId IN (:accountIds)", Employee.class);
		query.setParameter("accountIds", accountIds);
		return query.getResultList();
	}

	public Employee findEmployeeByAccount(int employeeId, int accountId) {
		TypedQuery<Employee> query = em.createQuery("FROM Employee e WHERE e.accountId = :accountId AND e.id = :employeeId", Employee.class);
		query.setParameter("accountId", accountId);
		query.setParameter("employeeId", employeeId);
		return query.getSingleResult();
	}

	public Employee findEmployeeByAccountIdAndEmail(int accountId, String email) {
		if (accountId > 0 && Strings.isNotEmpty(email)) {
			try {
				TypedQuery<Employee> query = em.createQuery("FROM Employee e WHERE e.accountId = :accountId AND e.email = :email", Employee.class);
				query.setParameter("accountId", accountId);
				query.setParameter("email", email);
				return query.getSingleResult();
			} catch (NoResultException noResultException) {
			}
		}

		return null;
	}

	public Employee findEmployeeByAccountIdAndSlug(int accountId, String slug) {
		if (accountId > 0 && Strings.isNotEmpty(slug)) {
			try {
				TypedQuery<Employee> query = em.createQuery("FROM Employee e WHERE e.accountId = :accountId AND e.slug = :slug", Employee.class);
				query.setParameter("accountId", accountId);
				query.setParameter("slug", slug);
				return query.getSingleResult();
			} catch (NoResultException noResultException) {
			}
		}

		return null;
	}

	public List<Employee> findByIds(List<Integer> employeeIds) {
		if (CollectionUtils.isNotEmpty(employeeIds)) {
			TypedQuery<Employee> query = em.createQuery("FROM Employee e WHERE e.id IN (:employeeIds)", Employee.class);
			query.setParameter("employeeIds", employeeIds);
			return query.getResultList();
		}

		return Collections.emptyList();
	}

	public List<Employee> findEmployeesByGroups(final Set<Integer> groupIds) {
		if (CollectionUtils.isEmpty(groupIds)) {
			return Collections.emptyList();
		}

		String sql2 = "SELECT DISTINCT e FROM GroupEmployee age INNER JOIN age.employee e WHERE age.group.id IN (:groupIds)";
		TypedQuery query = em.createQuery(sql2, Employee.class);
		query.setParameter("groupIds", groupIds);
		return query.getResultList();
	}

	public List<Employee> findByProject(final Project project) {
		if (project == null) {
			return Collections.emptyList();
		}

		TypedQuery<Employee> query = em.createQuery("SELECT DISTINCT pre.employee FROM ProjectRoleEmployee pre " +
				"WHERE pre.projectRole.project = :project", Employee.class);
		query.setParameter("project", project);
		return query.getResultList();
	}

	public List<Employee> search(String searchTerm, int accountId) {
		if (searchTerm == null) {
			searchTerm = Strings.EMPTY_STRING;
		}

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

	public void delete(int id, int accountId) {
		Employee employee = findEmployeeByAccount(id, accountId);
		super.delete(employee);
	}

	public List<Employee> findByProjects(final List<Project> projects) {
		if (CollectionUtils.isEmpty(projects)) {
			return Collections.emptyList();
		}

		TypedQuery<Employee> query = em.createQuery("SELECT DISTINCT pre.employee FROM ProjectRoleEmployee pre " +
				"WHERE pre.projectRole.project IN (:projects)", Employee.class);
		query.setParameter("projects", projects);
		return query.getResultList();
	}

	public List<Employee> findEmployeesAssignedToSite(final Collection<Integer> accountIds, final int siteId) {
		TypedQuery<Employee> query = em.createQuery("SELECT DISTINCT e FROM Employee e " +
				"JOIN e.projectRoles pre " +
				"JOIN pre.projectRole pr " +
				"JOIN pr.project p " +
				"WHERE e.accountId IN (:accountIds) " +
				"AND p.accountId = :siteId", Employee.class);
		query.setParameter("accountIds", accountIds);
		query.setParameter("siteId", siteId);

		List<Employee> employees = query.getResultList();

		query = em.createQuery("SELECT DISTINCT e FROM Employee e " +
				"JOIN e.roles re " +
				"JOIN re.role r " +
				"WHERE e.accountId IN (:accountIds) " +
				"AND r.accountId = :siteId", Employee.class);
		query.setParameter("accountIds", accountIds);
		query.setParameter("siteId", siteId);

		employees.addAll(query.getResultList());

		return ListUtil.removeDuplicatesAndSort(employees);
	}
}
