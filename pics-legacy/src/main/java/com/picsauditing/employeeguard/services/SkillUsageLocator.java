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

	public List<SkillUsages> getSkillUsagesForEmployees(final Set<Employee> employees) {
		if (CollectionUtils.isEmpty(employees)) {
			return Collections.emptyList();
		}

		Map<Employee, List<Project>> employeeProjects = buildEmployeeProjectsMap(employees);
		return buildSkillUsages(employees,
				getEmployeesProjectRequiredSkills(employeeProjects),
				getEmployeesContractorGroupSkills(employees),
				getEmployeesJobRoleSkills(employees),
				getEmployeesCorporateSkills(employeeProjects),
				getEmployeesSiteSkills(employeeProjects));
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

	private List<SkillUsages> buildSkillUsages(final Set<Employee> employees,
	                                           final Map<Employee, Map<AccountSkill, Set<Project>>> employeesProjectRequiredSkills,
	                                           final Map<Employee, Map<AccountSkill, Set<Group>>> employeeContractorGroupSkills,
	                                           final Map<Employee, Map<AccountSkill, Set<Group>>> employeeJobRoleSkills,
	                                           final Map<Employee, Map<AccountSkill, Set<Integer>>> employeeCorporateSkills,
	                                           final Map<Employee, Map<AccountSkill, Set<Integer>>> employeesSiteSkills) {

		List<SkillUsages> skillUsageList = new ArrayList<>();
		for (Employee employee : employees) {
			skillUsageList.add(new SkillUsages.Builder()
					.employee(employee)
					.projectRequiredSkills(employeesProjectRequiredSkills.get(employee))
					.contractorGroupSkills(employeeContractorGroupSkills.get(employee))
					.projectJobRoleSkills(employeeJobRoleSkills.get(employee))
					.corporateRequiredSkills(employeeCorporateSkills.get(employee))
					.siteRequiredSkills(employeesSiteSkills.get(employee))
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
			List<Group> groups = ExtractorUtil.extractList(employee.getGroups(), new Extractor<AccountGroupEmployee, Group>() {
				@Override
				public Group extract(AccountGroupEmployee accountGroupEmployee) {
					return accountGroupEmployee.getGroup();
				}
			});

			Map<AccountSkill, Set<Group>> skillGroups = skillService.getSkillGroups(groups);

			employeesContractorGroupSkills.put(employee, skillGroups);
		}

		return employeesContractorGroupSkills;
	}

	private Map<Employee, Map<AccountSkill, Set<Group>>> getEmployeesJobRoleSkills(final Set<Employee> employees) {
		Map<Employee, Map<AccountSkill, Set<Group>>> employeesJobRoleSkills = new TreeMap<>();

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

	public SkillUsages getSkillUsagesForEmployee(final Employee employee) {
		List<Project> projects = projectService.getProjectsForEmployee(employee);
		return new SkillUsages.Builder()
				.employee(employee)
				.projectRequiredSkills(getEmployeeProjectRequiredSkills(projects))
				.contractorGroupSkills(getEmployeeContractorGroupSkills(employee))
				.projectJobRoleSkills(getEmployeeJobRoleSkills(employee))
				.corporateRequiredSkills(getEmployeeCorporateSkills(projects))
				.siteRequiredSkills(getEmployeeSiteSkills(projects))
				.build();
	}

	private Map<AccountSkill, Set<Project>> getEmployeeProjectRequiredSkills(final List<Project> projects) {
		return skillService.getProjectRequiredSkillsMap(projects);
	}

	private Map<AccountSkill, Set<Group>> getEmployeeContractorGroupSkills(final Employee employee) {
		List<Group> groups = groupService.getEmployeeGroups(employee);
		return skillService.getSkillGroups(groups);
	}

	private Map<AccountSkill, Set<Group>> getEmployeeJobRoleSkills(final Employee employee) {
		return skillService.getProjectRoleSkillsMap(employee);
	}

	private Map<AccountSkill, Set<Integer>> getEmployeeCorporateSkills(final List<Project> projects) {
		return skillService.getCorporateSkillsForProjects(projects);
	}

	private Map<AccountSkill, Set<Integer>> getEmployeeSiteSkills(final List<Project> projects) {
		return skillService.getSiteSkillsForProjects(projects);
	}

	private Map<AccountSkill, Set<SiteAssignment>> getSiteAssignmentSkills(final int siteId) {

	}
}
