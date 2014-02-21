package com.picsauditing.employeeguard.viewmodel.factory;

import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import com.picsauditing.employeeguard.services.calculator.SkillStatusCalculator;
import com.picsauditing.employeeguard.viewmodel.employee.EmployeeSkillModel;
import com.picsauditing.employeeguard.viewmodel.employee.EmployeeSkillsModel;
import org.apache.commons.collections.map.ListOrderedMap;

import java.util.*;

public class EmployeeSkillsModelFactory {

	public EmployeeSkillsModel create(final Employee employee,
	                                  final List<ProjectRole> siteProjectRoles,
	                                  final List<Role> siteRoles,
	                                  final Map<Role, Role> siteToCorporateRoles,
	                                  final List<AccountSkill> siteSkills) {
		return new EmployeeSkillsModel.Builder()
				.employeeSkills(buildEmployeeSkillSections(employee, siteProjectRoles, siteRoles, siteToCorporateRoles, siteSkills))
				.build();
	}

	private Map<String, List<EmployeeSkillModel>> buildEmployeeSkillSections(Employee employee,
	                                                                         List<ProjectRole> siteProjectRoles,
	                                                                         List<Role> siteRoles,
	                                                                         Map<Role, Role> siteToCorporateRoles,
	                                                                         List<AccountSkill> siteSkills) {
		Map<AccountSkill, SkillStatus> skillStatuses = buildSkillStatusMap(employee);

		Map<String, List<EmployeeSkillModel>> employeeSkillSections = new ListOrderedMap();
		employeeSkillSections.put("Project Required", buildSkillSectionForProjectSkills(siteProjectRoles, skillStatuses));

		for (Role siteRole : siteRoles) {
			Role corporateRole = siteToCorporateRoles.get(siteRole);
			employeeSkillSections.putAll(buildSkillSectionMap(null, corporateRole, siteSkills, skillStatuses));
		}

		for (ProjectRole siteProjectRole : siteProjectRoles) {
			Role role = siteProjectRole.getRole();
			Project project = siteProjectRole.getProject();

			employeeSkillSections.putAll(buildSkillSectionMap(project, role, siteSkills, skillStatuses));
		}

		return employeeSkillSections;
	}

	private Map<AccountSkill, SkillStatus> buildSkillStatusMap(Employee employee) {
		Map<AccountSkill, SkillStatus> skillStatuses = new HashMap<>();
		for (AccountSkillEmployee accountSkillEmployee : employee.getSkills()) {
			skillStatuses.put(accountSkillEmployee.getSkill(), SkillStatusCalculator.calculateStatusFromSkill(accountSkillEmployee));
		}

		return skillStatuses;
	}

	private List<EmployeeSkillModel> buildSkillSectionForProjectSkills(List<ProjectRole> siteProjectRoles,
	                                                                   Map<AccountSkill, SkillStatus> skillStatuses) {
		Set<Project> projects = new HashSet<>();
		for (ProjectRole projectRole : siteProjectRoles) {
			projects.add(projectRole.getProject());
		}

		List<EmployeeSkillModel> skillSectionForProjectSkills = new ArrayList<>();
		for (Project project : projects) {
			for (ProjectSkill projectSkill : project.getSkills()) {
				AccountSkill skill = projectSkill.getSkill();
				skillSectionForProjectSkills.add(new EmployeeSkillModel.Builder()
						.belongsToProject(project.getId())
						.belongsToRole(0)
						.skillName(skill.getName())
						.skillStatus(skillStatuses.get(skill))
						.build());
			}
		}

		Collections.sort(skillSectionForProjectSkills);
		return skillSectionForProjectSkills;
	}

	private Map<String, List<EmployeeSkillModel>> buildSkillSectionMap(Project project,
	                                                                   Role role,
	                                                                   List<AccountSkill> siteSkills,
	                                                                   Map<AccountSkill, SkillStatus> skillStatuses) {
		Map<String, List<EmployeeSkillModel>> employeeSkillSections = new HashMap<>();
		List<EmployeeSkillModel> employeeSkills = buildEmployeeSkillModels(project, role, siteSkills, skillStatuses);
		employeeSkillSections.put(role.getName(), employeeSkills);

		return employeeSkillSections;
	}

	private List<EmployeeSkillModel> buildEmployeeSkillModels(Project project,
	                                                          Role role,
	                                                          List<AccountSkill> siteSkills,
	                                                          Map<AccountSkill, SkillStatus> skillStatuses) {
		List<EmployeeSkillModel> employeeSkills = new ArrayList<>();
		for (AccountSkillRole roleSkill : role.getSkills()) {
			AccountSkill skill = roleSkill.getSkill();
			employeeSkills.add(new EmployeeSkillModel.Builder()
					.skillName(skill.getName())
					.skillStatus(skillStatuses.get(skill))
					.belongsToProject(project == null ? 0 : project.getId())
					.belongsToRole(role.getId())
					.build());
		}

		for (AccountSkill skill : siteSkills) {
			employeeSkills.add(new EmployeeSkillModel.Builder()
					.skillName(skill.getName())
					.skillStatus(skillStatuses.get(skill))
					.belongsToProject(project == null ? 0 : project.getId())
					.belongsToRole(role.getId())
					.build());
		}

		return employeeSkills;
	}
}
