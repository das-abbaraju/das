package com.picsauditing.employeeguard.services;

import com.picsauditing.employeeguard.daos.ProjectRoleDAO;
import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.util.ListUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class ProjectRoleService {

    @Autowired
    private ProjectRoleDAO projectRoleDAO;
	@Autowired
	private ProjectService projectService;

	/**
	 * Includes skills attached directly on projects as well as site and corporate required skills
	 *
	 * @param projectRole
	 * @return
	 */
	public List<AccountSkill> getRequiredSkills(final ProjectRole projectRole) {
		List<AccountSkill> accountSkills = new ArrayList<>();

		for (AccountSkillGroup accountSkillGroup : projectRole.getRole().getSkills()) {
			accountSkills.add(accountSkillGroup.getSkill());
		}

		accountSkills.addAll(projectService.getRequiredSkills(projectRole.getProject()));

		return ListUtil.removeDuplicatesAndSort(accountSkills);
	}

    public List<ProjectRole> getRolesForEmployee(Employee employee) {
        return projectRoleDAO.findByEmployee(employee);
    }

    public List<ProjectRole> getRolesForProfile(Profile profile) {
        return projectRoleDAO.findByProfile(profile);
    }

	public List<ProjectRole> getProjectRolesByProjectsAndRole(List<Integer> projectIds, AccountGroup role) {
		return projectRoleDAO.findByProjectsAndRole(projectIds, role);
	}

    public List<ProjectRoleEmployee> getProjectRolesForContractor(Project project, int accountId) {
        return projectRoleDAO.findByProjectAndContractor(project, accountId);
    }
}
