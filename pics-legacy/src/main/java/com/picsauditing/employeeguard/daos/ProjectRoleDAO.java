package com.picsauditing.employeeguard.daos;

import com.picsauditing.employeeguard.entities.*;
import com.vividsolutions.jts.util.CollectionUtil;
import org.apache.commons.collections.CollectionUtils;

import javax.persistence.TypedQuery;
import java.util.Collections;
import java.util.List;

public class ProjectRoleDAO extends BaseEntityDAO<ProjectRole> {
	public ProjectRoleDAO() {
		this.type = ProjectRole.class;
	}

	public ProjectRole findByProjectAndRoleId(final int projectId, final int roleId) {
		TypedQuery<ProjectRole> query = em.createQuery("FROM ProjectRole pr WHERE pr.project.id = :projectId AND pr.role.id = :roleId", ProjectRole.class);
		query.setParameter("projectId", projectId);
		query.setParameter("roleId", roleId);

		try {
			return query.getSingleResult();
		} catch (Exception exception) {
			return null;
		}
	}

    public List<ProjectRole> findByEmployee(Employee employee) {
        TypedQuery<ProjectRole> query = em.createQuery("SELECT pre.projectRole FROM ProjectRoleEmployee pre WHERE pre.employee = :employee", ProjectRole.class);
        query.setParameter("employee", employee);

        try {
            return query.getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    public List<ProjectRole> findByProfile(Profile profile) {
        TypedQuery<ProjectRole> query = em.createQuery("SELECT pre.projectRole FROM ProjectRoleEmployee pre WHERE pre.employee.profile = :profile", ProjectRole.class);
        query.setParameter("profile", profile);

        try {
            return query.getResultList();
        } catch (Exception e) {
            return null;
        }
    }

	public List<ProjectRole> findByProjectsAndRole(final List<Integer> projectIds, final AccountGroup role) {
		if (CollectionUtils.isEmpty(projectIds) || role == null) {
			return Collections.emptyList();
		}

		TypedQuery<ProjectRole> query = em.createQuery("FROM ProjectRole pr " +
				"WHERE pr.project.id IN (:projectIds) AND pr.role = :role", ProjectRole.class);
		query.setParameter("projectIds", projectIds);
		query.setParameter("role", role);
		return query.getResultList();
	}

    public List<ProjectRoleEmployee> findByProjectAndContractor(Project project, int accountId) {
        TypedQuery<ProjectRoleEmployee> query = em.createQuery("FROM ProjectRoleEmployee pre " +
                "WHERE pre.projectRole.project = :project AND pre.employee.accountId = :accountId", ProjectRoleEmployee.class);
        query.setParameter("project", project);
        query.setParameter("accountId", accountId);
        return query.getResultList();
    }

    public List<ProjectRole> findByProject(final Project project) {
        TypedQuery<ProjectRole> query = em.createQuery("FROM ProjectRole pr " +
                "WHERE pr.project = :project", ProjectRole.class);
        query.setParameter("project", project);
        return query.getResultList();
    }
}
