package com.picsauditing.employeeguard.daos;

import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.entities.ProjectSkill;
import org.apache.commons.collections.CollectionUtils;

import javax.persistence.TypedQuery;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ProjectSkillDAO extends AbstractBaseEntityDAO<ProjectSkill> {
	public ProjectSkillDAO() {
		this.type = ProjectSkill.class;
	}

	public List<ProjectSkill> findByProjects(final Collection<Project> projects) {
		if (CollectionUtils.isEmpty(projects)) { // TODO: Refactor and move this into the services
			return Collections.emptyList();
		}

		TypedQuery<ProjectSkill> query = em.createQuery("FROM ProjectSkill ps WHERE ps.project IN (:projects)", ProjectSkill.class);
		query.setParameter("projects", projects);

		return query.getResultList();
	}
}