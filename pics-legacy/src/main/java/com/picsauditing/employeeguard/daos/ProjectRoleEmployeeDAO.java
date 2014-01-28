package com.picsauditing.employeeguard.daos;

import com.picsauditing.employeeguard.entities.*;
import org.apache.commons.collections.CollectionUtils;

import javax.persistence.TypedQuery;
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

	public List<ProjectRoleEmployee> findByEmployees(final List<Employee> employees) {
		if (CollectionUtils.isEmpty(employees)) {
			return Collections.emptyList();
		}

		TypedQuery<ProjectRoleEmployee> query = em.createQuery("FROM ProjectRoleEmployee pre " +
				"WHERE pre.employee IN (:employees)", ProjectRoleEmployee.class);
		query.setParameter("employees", employees);
		return query.getResultList();
	}

	public List<Employee> getEmployeesByRole(Group role) {
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
}
