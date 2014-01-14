package com.picsauditing.employeeguard.services;

import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.util.Extractor;
import com.picsauditing.employeeguard.util.ExtractorUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class SkillUsageLocator {

	@Autowired
	private GroupService groupService;
	@Autowired
	private SkillService skillService;
	@Autowired
	private ProjectService projectService;

	public List<SkillUsage> getSkillUsagesForEmployees(final Set<Employee> employees) {
		if (CollectionUtils.isEmpty(employees)) {
			return Collections.emptyList();
		}

		Map<Employee, List<Project>> employeeProjects = buildEmployeeProjectsMap(employees);
		return buildSkillUsages(employees,
				getEmployeesProjectRequiredSkills(employeeProjects),
				getEmployeesContractorGroupSkills(employees),
				getEmployeesProjectJobRoleSkills(employees),
				getEmployeesCorporateSkills(employeeProjects),
				getEmployeesSiteSkills(employeeProjects),
				getEmployeesSiteAssignmentSkills(employees));
	}

	private Map<Employee, List<Project>> buildEmployeeProjectsMap(final Set<Employee> employees) {
		Map<Employee, List<Project>> employeeProjectsMap = new TreeMap<>();
		for (Employee employee : employees) {
			List<Project> projects = ExtractorUtil.extractList(employee.getRoles(), new Extractor<ProjectRoleEmployee, Project>() {
				@Override
				public Project extract(ProjectRoleEmployee projectRoleEmployee) {
					return projectRoleEmployee.getProjectRole().getProject();
				}
			});

			employeeProjectsMap.put(employee, projects);
		}

		return employeeProjectsMap;
	}

	private List<SkillUsage> buildSkillUsages(final Set<Employee> employees,
	                                          final Map<Employee, Map<AccountSkill, Set<Project>>> employeesProjectRequiredSkills,
	                                          final Map<Employee, Map<AccountSkill, Set<Group>>> employeeContractorGroupSkills,
	                                          final Map<Employee, Map<AccountSkill, Set<Role>>> employeeProjectJobRoleSkills,
	                                          final Map<Employee, Map<AccountSkill, Set<Integer>>> employeeCorporateSkills,
	                                          final Map<Employee, Map<AccountSkill, Set<Integer>>> employeesSiteSkills,
	                                          final Map<Employee, Map<AccountSkill, Set<Integer>>> employeeSiteAssignmentSkills) {

		List<SkillUsage> skillUsageList = new ArrayList<>();
		for (Employee employee : employees) {
			skillUsageList.add(new SkillUsage.Builder()
					.employee(employee)
					.projectRequiredSkills(employeesProjectRequiredSkills.get(employee))
					.contractorGroupSkills(employeeContractorGroupSkills.get(employee))
					.projectJobRoleSkills(employeeProjectJobRoleSkills.get(employee))
					.corporateRequiredSkills(employeeCorporateSkills.get(employee))
					.siteRequiredSkills(employeesSiteSkills.get(employee))
					.siteAssignmentSkills(employeeSiteAssignmentSkills.get(employee))
					.build());
		}

		return Collections.unmodifiableList(skillUsageList);
	}

	private Map<Employee, Map<AccountSkill, Set<Project>>> getEmployeesProjectRequiredSkills(final Map<Employee, List<Project>> projects) {
		Map<Employee, Map<AccountSkill, Set<Project>>> employeesProjectRequiredSkills = new TreeMap<>();

		for (Map.Entry<Employee, List<Project>> entry : projects.entrySet()) {
			Map<AccountSkill, Set<Project>> projectSkills = skillService.getProjectRequiredSkillsMap(entry.getValue());
			employeesProjectRequiredSkills.put(entry.getKey(), projectSkills);
		}

		return employeesProjectRequiredSkills;
	}

	private Map<Employee, Map<AccountSkill, Set<Group>>> getEmployeesContractorGroupSkills(final Set<Employee> employees) {
		Map<Employee, Map<AccountSkill, Set<Group>>> employeesContractorGroupSkills = new TreeMap<>();

		for (Employee employee : employees) {
			List<Group> groups = ExtractorUtil.extractList(employee.getGroups(), new Extractor<GroupEmployee, Group>() {
				@Override
				public Group extract(GroupEmployee accountGroupEmployee) {
					return accountGroupEmployee.getGroup();
				}
			});

			Map<AccountSkill, Set<Group>> skillGroups = skillService.getSkillGroups(groups);

			employeesContractorGroupSkills.put(employee, skillGroups);
		}

		return employeesContractorGroupSkills;
	}

	private Map<Employee, Map<AccountSkill, Set<Role>>> getEmployeesProjectJobRoleSkills(final Set<Employee> employees) {
		Map<Employee, Map<AccountSkill, Set<Role>>> employeesJobRoleSkills = new TreeMap<>();

		for (Employee employee : employees) {
			employeesJobRoleSkills.put(employee, skillService.getProjectRoleSkillsMap(employee));
		}

		return employeesJobRoleSkills;
	}

	private Map<Employee, Map<AccountSkill, Set<Integer>>> getEmployeesCorporateSkills(final Map<Employee, List<Project>> projects) {
		Map<Employee, Map<AccountSkill, Set<Integer>>> employeeCorporateSkills = new TreeMap<>();

		for (Map.Entry<Employee, List<Project>> entry : projects.entrySet()) {
			Map<AccountSkill, Set<Integer>> corporateSkillsForProjects = skillService.getCorporateSkillsForProjects(entry.getValue());
			employeeCorporateSkills.put(entry.getKey(), corporateSkillsForProjects);
		}

		return employeeCorporateSkills;
	}

	private Map<Employee, Map<AccountSkill, Set<Integer>>> getEmployeesSiteSkills(final Map<Employee, List<Project>> projects) {
		Map<Employee, Map<AccountSkill, Set<Integer>>> employeeSiteSkills = new TreeMap<>();

		for (Map.Entry<Employee, List<Project>> entry : projects.entrySet()) {
			Map<AccountSkill, Set<Integer>> corporateSkillsForProjects = skillService.getSiteSkillsForProjects(entry.getValue());
			employeeSiteSkills.put(entry.getKey(), corporateSkillsForProjects);
		}

		return employeeSiteSkills;
	}

	private Map<Employee, Map<AccountSkill, Set<Integer>>> getEmployeesSiteAssignmentSkills(final Set<Employee> employees) {
		Map<Employee, Map<AccountSkill, Set<Integer>>> employeesSiteAssignmentSkills = new TreeMap<>();

		for (Employee employee : employees) {
			employeesSiteAssignmentSkills.put(employee, getSiteAssignmentSkills(employee));
		}

		return employeesSiteAssignmentSkills;
	}

	public SkillUsage getSkillUsagesForEmployee(final Employee employee) {
		List<Project> projects = projectService.getProjectsForEmployee(employee);
		return new SkillUsage.Builder()
				.employee(employee)
				.projectRequiredSkills(getEmployeeProjectRequiredSkills(projects))
				.contractorGroupSkills(getEmployeeContractorGroupSkills(employee))
				.projectJobRoleSkills(getEmployeeProjectJobRoleSkills(employee))
				.corporateRequiredSkills(getEmployeeCorporateSkills(projects))
				.siteRequiredSkills(getEmployeeSiteSkills(projects))
				.siteAssignmentSkills(getSiteAssignmentSkills(employee))
				.build();
	}

	private Map<AccountSkill, Set<Project>> getEmployeeProjectRequiredSkills(final List<Project> projects) {
		return skillService.getProjectRequiredSkillsMap(projects);
	}

	private Map<AccountSkill, Set<Group>> getEmployeeContractorGroupSkills(final Employee employee) {
		List<Group> groups = groupService.getEmployeeGroups(employee);
		return skillService.getSkillGroups(groups);
	}

	private Map<AccountSkill, Set<Role>> getEmployeeProjectJobRoleSkills(final Employee employee) {
		return skillService.getProjectRoleSkillsMap(employee);
	}

	private Map<AccountSkill, Set<Integer>> getEmployeeCorporateSkills(final List<Project> projects) {
		return skillService.getCorporateSkillsForProjects(projects);
	}

	private Map<AccountSkill, Set<Integer>> getEmployeeSiteSkills(final List<Project> projects) {
		return skillService.getSiteSkillsForProjects(projects);
	}

	private Map<AccountSkill, Set<Integer>> getSiteAssignmentSkills(final Employee employee) {
		// add skills from directly assigned job roles
		return skillService.getSiteAssignmentSkills(employee);
	}
}
