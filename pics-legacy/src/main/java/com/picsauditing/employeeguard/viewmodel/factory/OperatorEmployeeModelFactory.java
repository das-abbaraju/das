package com.picsauditing.employeeguard.viewmodel.factory;

import com.picsauditing.PICS.Utilities;
import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import com.picsauditing.employeeguard.services.models.AccountModel;
import com.picsauditing.employeeguard.viewmodel.IdNameTitleModel;
import com.picsauditing.employeeguard.viewmodel.RoleModel;
import com.picsauditing.employeeguard.viewmodel.employee.OperatorEmployeeModel;
import com.picsauditing.employeeguard.viewmodel.employee.ProjectDetailModel;
import com.picsauditing.employeeguard.viewmodel.operator.OperatorEmployeeSkillModel;

import java.util.*;

public class OperatorEmployeeModelFactory {
	public OperatorEmployeeModel create(final Employee employee,
	                                    final Map<Integer, AccountModel> accounts,
	                                    final Map<Project, SkillStatus> projectStatusMap,
	                                    final Map<Role, SkillStatus> roleStatusMap,
	                                    final Map<AccountSkill, SkillStatus> skillStatusMap,
	                                    final SkillStatus overallStatus) {
		List<IdNameTitleModel> companies = getCompanyNamesAndTitles(employee, accounts);
		List<ProjectDetailModel> projects = getProjects(projectStatusMap);
		List<RoleModel> roles = getRoles(roleStatusMap);
		List<OperatorEmployeeSkillModel> skills = getSkills(skillStatusMap);

		return new OperatorEmployeeModel.Builder()
				.id(employee.getId())
				.name(employee.getName())
				.companies(companies)
				.projects(projects)
				.roles(roles)
				.skills(skills)
				.overallStatus(overallStatus)
				.build();
	}

	private List<IdNameTitleModel> getCompanyNamesAndTitles(Employee employee, Map<Integer, AccountModel> accounts) {
		List<IdNameTitleModel> companies = new ArrayList<>();
		if (employee.getProfile() != null) {
			for (Employee otherCompany : employee.getProfile().getEmployees()) {
				IdNameTitleModel companyNameAndTitle = new IdNameTitleModel.Builder()
						.id(Integer.toString(otherCompany.getAccountId()))
						.name(accounts.get(otherCompany.getAccountId()).getName())
						.title(otherCompany.getPositionName())
						.build();
				companies.add(companyNameAndTitle);
			}
		} else {
			companies.add(new IdNameTitleModel.Builder()
					.id(Integer.toString(employee.getAccountId()))
					.name(accounts.get(employee.getAccountId()).getName())
					.title(employee.getPositionName())
					.build());
		}
		return companies;
	}

	private List<ProjectDetailModel> getProjects(Map<Project, SkillStatus> projectStatusMap) {
		List<ProjectDetailModel> projects = new ArrayList<>();
		for (Project project : projectStatusMap.keySet()) {
			Set<Integer> skillIds = Utilities.getIdsFromCollection(project.getSkills(), new Utilities.Identitifable<ProjectSkill, Integer>() {
				@Override
				public Integer getId(ProjectSkill element) {
					return element.getSkill().getId();
				}
			});

			Set<Integer> roleIds = Utilities.getIdsFromCollection(project.getRoles(), new Utilities.Identitifable<ProjectRole, Integer>() {
				@Override
				public Integer getId(ProjectRole element) {
					return element.getRole().getId();
				}
			});

			ProjectDetailModel projectDetailModel = new ProjectDetailModel.Builder()
					.id(project.getId())
					.name(project.getName())
					.skills(skillIds)
					.roles(roleIds)
					.status(projectStatusMap.get(project))
					.build();
			projects.add(projectDetailModel);
		}

		return projects;
	}

	private List<RoleModel> getRoles(Map<Role, SkillStatus> roleStatusMap) {
		List<RoleModel> roles = new ArrayList<>();
		for (Role role : roleStatusMap.keySet()) {
			Set<Integer> skillIds = Utilities.getIdsFromCollection(role.getSkills(), new Utilities.Identitifable<AccountSkillRole, Integer>() {
				@Override
				public Integer getId(AccountSkillRole element) {
					return element.getSkill().getId();
				}
			});

			RoleModel roleModel = new RoleModel.Builder()
					.id(role.getId())
					.name(role.getName())
					.skills(skillIds)
					.status(roleStatusMap.get(role))
					.build();

			roles.add(roleModel);
		}

		return roles;
	}

	private List<OperatorEmployeeSkillModel> getSkills(Map<AccountSkill, SkillStatus> skillStatusMap) {
		List<OperatorEmployeeSkillModel> skills = new ArrayList<>();
		for (AccountSkill skill : skillStatusMap.keySet()) {
			Set<Integer> projectIds = new HashSet<>(Utilities.getIdsFromCollection(skill.getProjects(), new Utilities.Identitifable<ProjectSkill, Integer>() {
				@Override
				public Integer getId(ProjectSkill element) {
					return element.getProject().getId();
				}
			}));

			Set<Integer> roleIds = Utilities.getIdsFromCollection(skill.getRoles(), new Utilities.Identitifable<AccountSkillRole, Integer>() {
				@Override
				public Integer getId(AccountSkillRole element) {
					return element.getRole().getId();
				}
			});

			for (AccountSkillRole accountSkillRole : skill.getRoles()) {
				for (ProjectRole projectRole : accountSkillRole.getRole().getProjects()) {
					projectIds.add(projectRole.getProject().getId());
				}
			}

			OperatorEmployeeSkillModel model = new OperatorEmployeeSkillModel.Builder()
					.id(skill.getId())
					.name(skill.getName())
					.projects(projectIds)
					.roles(roleIds)
					.status(skillStatusMap.get(skill))
					.build();

			skills.add(model);
		}

		return skills;
	}
}
