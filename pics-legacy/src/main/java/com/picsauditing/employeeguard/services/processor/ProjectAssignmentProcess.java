package com.picsauditing.employeeguard.services.processor;

import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.entities.Role;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class ProjectAssignmentProcess {

	public Map<Project, Map<Employee, Set<AccountSkill>>> getProjectSkillsForEmployees(final ProjectAssignmentDataSet dataSet) {
		Map<Project, Map<Employee, Set<AccountSkill>>> projectSkillsForEmployee = new HashMap<>();

		for (Project project : dataSet.getProjects()) {
			if (noRolesForProject(dataSet, project)) {
				continue;
			}

			for (Role role : dataSet.getProjectRoles().get(project)) {
				if (noEmployeesForRole(dataSet, role)) {
					continue;
				}

				for (final Employee employee : dataSet.getRoleEmployees().get(role)) {
					if (employeeNotAssignedToProject(dataSet, project, employee)) {
						continue;
					}

					final Set<AccountSkill> projectSkills = aggregateProjectSpecificEmployeeSkills(dataSet,
							projectSkillsForEmployee, project, role, employee);

					projectSkillsForEmployee.get(project).get(employee).addAll(projectSkills);
				}
			}
		}

		return projectSkillsForEmployee;
	}

	private Set<AccountSkill> aggregateProjectSpecificEmployeeSkills(final ProjectAssignmentDataSet projectAssignmentCommand,
																	 final Map<Project, Map<Employee, Set<AccountSkill>>> projectSkillsForEmployee,
																	 final Project project,
																	 final Role role,
																	 final Employee employee) {
		final Set<AccountSkill> projectSkills = new HashSet<>(projectAssignmentCommand.getSiteAndCorporateRequiredSkills());

		if (projectHasRequiredSkills(projectAssignmentCommand, project)) {
			projectSkills.addAll(projectAssignmentCommand.getProjectRequiredSkills().get(project));
		}

		if (roleHasSkills(projectAssignmentCommand, role)) {
			projectSkills.addAll(projectAssignmentCommand.getProjectRoleSkills().get(role));
		}

		if (!projectSkillsForEmployee.containsKey(project)) {
			projectSkillsForEmployee.put(project, new HashMap<Employee, Set<AccountSkill>>());
		}

		if (!projectSkillsForEmployee.get(project).containsKey(employee)) {
			projectSkillsForEmployee.get(project).put(employee, new HashSet<AccountSkill>());
		}

		return projectSkills;
	}

	private boolean roleHasSkills(ProjectAssignmentDataSet projectAssignmentCommand, Role role) {
		return projectAssignmentCommand.getProjectRoleSkills().containsKey(role);
	}

	private boolean projectHasRequiredSkills(ProjectAssignmentDataSet projectAssignmentCommand, Project project) {
		return projectAssignmentCommand.getProjectRequiredSkills().containsKey(project);
	}

	private boolean employeeNotAssignedToProject(final ProjectAssignmentDataSet projectAssignmentCommand,
												 final Project project,
												 final Employee employee) {
		return !employeeBelongsToProject(projectAssignmentCommand.getProjectEmployees(), project, employee);
	}

	private boolean noEmployeesForRole(final ProjectAssignmentDataSet projectAssignmentCommand,
									   final Role role) {
		return !projectAssignmentCommand.getRoleEmployees().containsKey(role);
	}

	private boolean noRolesForProject(final ProjectAssignmentDataSet projectAssignmentCommand,
									  final Project project) {
		return !projectAssignmentCommand.getProjectRoles().containsKey(project);
	}

	private boolean employeeBelongsToProject(final Map<Project, Set<Employee>> projectEmployees,
											 final Project project,
											 final Employee employee) {
		return (projectEmployees.containsKey(project) && projectEmployees.get(project).contains(employee));
	}
}
