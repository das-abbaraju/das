package com.picsauditing.employeeguard.forms.factory;

import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.forms.contractor.ContractorDetailProjectForm;
import com.picsauditing.employeeguard.forms.contractor.JobRoleInfo;
import com.picsauditing.employeeguard.services.models.AccountModel;

import java.util.ArrayList;
import java.util.List;

public class ContractorDetailProjectFormBuilder {

	public ContractorDetailProjectForm build(ProjectCompany projectCompany, AccountModel accountModel) {
		Project project = projectCompany.getProject();

		ContractorDetailProjectForm projectDetailForm = new ContractorDetailProjectForm();
		projectDetailForm = addProjectInformation(accountModel, project, projectDetailForm);
		projectDetailForm.setJobRoles(buildJobRoles(project));
		projectDetailForm.setSkills(buildSkills(project));

		return projectDetailForm;
	}

	private ContractorDetailProjectForm addProjectInformation(AccountModel accountModel, Project project, ContractorDetailProjectForm projectDetailForm) {
		projectDetailForm.setSiteId(accountModel.getId());
		projectDetailForm.setSiteName(accountModel.getName());
		projectDetailForm.setProjectId(project.getId());
		projectDetailForm.setProjectName(project.getName());
		projectDetailForm.setLocation(project.getLocation());
		projectDetailForm.setStartDate(project.getStartDate());
		projectDetailForm.setEndDate(project.getEndDate());

		return projectDetailForm;
	}

	public List<JobRoleInfo> buildJobRoles(Project project) {
		List<JobRoleInfo> jobRoleInfoList = new ArrayList<>();
		for (ProjectRole projectRole : project.getRoles()) {
			jobRoleInfoList.add(buildJobRoleInfo(projectRole));
		}

		return jobRoleInfoList;
	}

	private JobRoleInfo buildJobRoleInfo(ProjectRole projectRole) {
		JobRoleInfo jobRoleInfo = new JobRoleInfo();
		jobRoleInfo = addRoleInfoFromGroup(projectRole, jobRoleInfo);
		jobRoleInfo.setSkills(buildSkills(projectRole));

		return jobRoleInfo;
	}

	private List<AccountSkill> buildSkills(ProjectRole projectRole) {
		List<AccountSkill> skills = new ArrayList<>();
		for (AccountSkillRole accountSkillRole : projectRole.getRole().getSkills()) {
			skills.add(accountSkillRole.getSkill());
		}

		return skills;
	}

	private JobRoleInfo addRoleInfoFromGroup(ProjectRole projectRole, JobRoleInfo jobRoleInfo) {
		Role role = projectRole.getRole();
		jobRoleInfo.setId(role.getId());
		jobRoleInfo.setName(role.getName());
		return jobRoleInfo;
	}

	private List<AccountSkill> buildSkills(Project project) {
		List<AccountSkill> skills = new ArrayList<>();
		for (ProjectSkill projectSkill : project.getSkills()) {
			skills.add(projectSkill.getSkill());
		}

		return skills;
	}
}
