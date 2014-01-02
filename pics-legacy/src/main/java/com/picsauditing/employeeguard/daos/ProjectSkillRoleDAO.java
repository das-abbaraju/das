package com.picsauditing.employeeguard.daos;

import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.ProjectSkillRole;

import javax.persistence.TypedQuery;
import java.util.Collections;
import java.util.List;

public class ProjectSkillRoleDAO extends BaseEntityDAO<ProjectSkillRole> {
	public ProjectSkillRoleDAO() {
		this.type = ProjectSkillRole.class;
	}

	public List<ProjectSkillRole> findByEmployee(final Employee employee) {
		if (employee == null) {
			return Collections.emptyList();
		}

		TypedQuery<ProjectSkillRole> query = em.createQuery("SELECT psr FROM ProjectSkillRole psr " +
				"JOIN psr.projectRole pr " +
				"JOIN pr.role r " +
				"JOIN r.employees re " +
				"JOIN re.employee e " +
				"WHERE e = :employee", ProjectSkillRole.class);
		query.setParameter("employee", employee);

		return query.getResultList();
	}
}
