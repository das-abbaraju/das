package com.picsauditing.employeeguard.daos;

import com.picsauditing.employeeguard.entities.ProjectRole;

import javax.persistence.TypedQuery;

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
}
