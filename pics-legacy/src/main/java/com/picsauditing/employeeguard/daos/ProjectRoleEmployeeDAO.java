package com.picsauditing.employeeguard.daos;

import com.picsauditing.employeeguard.entities.*;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ProjectRoleEmployeeDAO extends AbstractBaseEntityDAO<ProjectRoleEmployee> {
	public ProjectRoleEmployeeDAO() {
		this.type = ProjectRoleEmployee.class;
	}

	public ProjectRoleEmployee findByEmployeeAndProjectRole(final Employee employee, final ProjectRole projectRole) {
		if (employee == null || projectRole == null) {
			return null;
		}

		TypedQuery<ProjectRoleEmployee> query = em.createQuery("FROM ProjectRoleEmployee pre " +
				"WHERE pre.employee = :employee " +
				"AND pre.projectRole = :projectRole", ProjectRoleEmployee.class);
		query.setParameter("employee", employee);
		query.setParameter("projectRole", projectRole);
		return query.getSingleResult();
	}

	public List<ProjectRoleEmployee> findByEmployeesAndProjectRole(List<Employee> employees, ProjectRole projectRole) {
		if (CollectionUtils.isEmpty(employees) || projectRole == null) {
			return null;
		}

		TypedQuery<ProjectRoleEmployee> query = em.createQuery("FROM ProjectRoleEmployee pre " +
				"WHERE pre.employee IN (:employees) " +
				"AND pre.projectRole = :projectRole", ProjectRoleEmployee.class);
		query.setParameter("employees", employees);
		query.setParameter("projectRole", projectRole);
		return query.getResultList();
	}

	public List<ProjectRoleEmployee> findByEmployees(final Collection<Employee> employees) {
		if (CollectionUtils.isEmpty(employees)) {
			return Collections.emptyList();
		}

		TypedQuery<ProjectRoleEmployee> query = em.createQuery("FROM ProjectRoleEmployee pre " +
				"WHERE pre.employee IN (:employees)", ProjectRoleEmployee.class);
		query.setParameter("employees", employees);
		return query.getResultList();
	}

	public List<Employee> getEmployeesByRole(Role role) {
		if (role == null) {
			return Collections.emptyList();
		}

		TypedQuery<Employee> query = em.createQuery("SELECT pre.employee FROM ProjectRoleEmployee pre " +
				"WHERE pre.projectRole.role = :role", Employee.class);
		query.setParameter("role", role);
		return query.getResultList();
	}

	public List<Employee> findByProjectAndRoleId(final Project project, final int roleId) {
		if (roleId == 0) {
			return Collections.emptyList();
		}

		TypedQuery<Employee> query = em.createQuery("SELECT DISTINCT pre.employee FROM ProjectRoleEmployee pre " +
				"WHERE pre.projectRole.role.id = :roleId AND pre.projectRole.project = :project", Employee.class);
		query.setParameter("roleId", roleId);
		query.setParameter("project", project);
		return query.getResultList();
	}

	public List<ProjectRoleEmployee> findByProject(final Project project) {
		TypedQuery<ProjectRoleEmployee> query = em.createQuery("SELECT pre FROM ProjectRoleEmployee pre " +
				"JOIN pre.projectRole pr " +
				"JOIN pr.project p " +
				"WHERE p = :project", ProjectRoleEmployee.class);

		query.setParameter("project", project);

		return query.getResultList();
	}

	public List<ProjectRoleEmployee> findByProjectAndRole(final Project project, final AccountGroup role) {
		TypedQuery<ProjectRoleEmployee> query = em.createQuery("SELECT pre FROM ProjectRoleEmployee pre " +
				"JOIN pre.projectRole pr " +
				"JOIN pr.project p " +
				"WHERE p = :project AND pr.role = :role", ProjectRoleEmployee.class);

		query.setParameter("project", project);
		query.setParameter("role", role);

		return query.getResultList();
	}

	public List<ProjectRoleEmployee> findByAccountId(final int accountId) {
		TypedQuery<ProjectRoleEmployee> query = em.createQuery("FROM ProjectRoleEmployee pre " +
				"WHERE pre.employee.accountId = :accountId", ProjectRoleEmployee.class);
		query.setParameter("accountId", accountId);
		return query.getResultList();
	}

	public List<ProjectRoleEmployee> findByCorporateAndContractor(final List<Integer> corporateIds, final int contractorId) {
		TypedQuery<ProjectRoleEmployee> query = em.createQuery("FROM ProjectRoleEmployee pre " +
				"WHERE pre.employee.accountId = :contractorId " +
				"AND pre.projectRole.role.accountId IN (:corporateIds)", ProjectRoleEmployee.class);
		query.setParameter("contractorId", contractorId);
		query.setParameter("corporateIds", corporateIds);
		return query.getResultList();
	}

	public List<ProjectRoleEmployee> findByEmployeeAndSiteId(final int employeeId, final int siteId) {
		TypedQuery<ProjectRoleEmployee> query = em.createQuery("FROM ProjectRoleEmployee pre " +
				"WHERE pre.employee.id = :employeeId " +
				"AND pre.projectRole.project.accountId = :siteId", ProjectRoleEmployee.class);
		query.setParameter("employeeId", employeeId);
		query.setParameter("siteId", siteId);
		return query.getResultList();
	}

	public List<ProjectRoleEmployee> findByEmployeeAndRole(final Employee employee, final Role role) {
		TypedQuery<ProjectRoleEmployee> query = em.createQuery("SELECT pre FROM ProjectRoleEmployee pre " +
				"WHERE pre.projectRole.role = :role " +
				"AND pre.employee = :employee", ProjectRoleEmployee.class);
		query.setParameter("role", role);
		query.setParameter("employee", employee);
		return query.getResultList();
	}

	public List<ProjectRoleEmployee> findByEmployeesAndProjects(final Collection<Employee> employees,
	                                                            final Collection<Project> projects) {
		TypedQuery<ProjectRoleEmployee> query = em.createQuery("SELECT pre FROM ProjectRoleEmployee pre " +
				"JOIN pre.projectRole pr " +
				"JOIN pr.project p " +
				"WHERE p IN (:projects) " +
				"AND pre.employee IN (:employees)", ProjectRoleEmployee.class);

		query.setParameter("employees", employees);
		query.setParameter("projects", projects);

		return query.getResultList();
	}

	public List<ProjectRoleEmployee> findByEmployeesAndSiteId(final Collection<Employee> employees,
	                                                          final int siteId) {
		return findByEmployeesAndSiteIds(employees, Arrays.asList(siteId));
	}

	public List<ProjectRoleEmployee> findByEmployeesAndSiteIds(final Collection<Employee> employees,
	                                                           final Collection<Integer> siteIds) {
		TypedQuery<ProjectRoleEmployee> query = em.createQuery("SELECT pre FROM ProjectRoleEmployee pre " +
				"JOIN pre.projectRole pr " +
				"JOIN pr.project p " +
				"WHERE p.accountId IN (:siteIds) " +
				"AND pre.employee in (:employees)", ProjectRoleEmployee.class);

		query.setParameter("employees", employees);
		query.setParameter("siteIds", siteIds);

		return query.getResultList();
	}

	public List<ProjectRoleEmployee> findByProjects(final Collection<Project> projects) {
		TypedQuery<ProjectRoleEmployee> query = em.createQuery("SELECT pre FROM ProjectRoleEmployee pre " +
				"JOIN pre.projectRole pr " +
				"JOIN pr.project p " +
				"WHERE p IN (:projects)", ProjectRoleEmployee.class);

		query.setParameter("projects", projects);

		return query.getResultList();
	}

	public ProjectRoleEmployee findByProjectRoleAndEmployeeId(final ProjectRole projectRole, final int employeeId) {
		TypedQuery<ProjectRoleEmployee> query = em.createQuery("SELECT pre FROM ProjectRoleEmployee pre " +
				"JOIN pre.projectRole pr " +
				"WHERE pr = :projectRole " +
				"AND pre.employee.id = :employeeId", ProjectRoleEmployee.class);

		query.setParameter("projectRole", projectRole);
		query.setParameter("employeeId", employeeId);

		try {
			return query.getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}

	@Transactional(propagation = Propagation.NESTED)
	public void delete(final Project project, final int roleId, final int employeeId) {
		Query query = em.createQuery("DELETE FROM ProjectRoleEmployee pre " +
				"WHERE pre.projectRole.role.id = :roleId " +
				"AND pre.projectRole.project = :project " +
				"AND pre.employee.id = :employeeId");

		query.setParameter("project", project);
		query.setParameter("roleId", roleId);
		query.setParameter("employeeId", employeeId);

		query.executeUpdate();
	}
}
