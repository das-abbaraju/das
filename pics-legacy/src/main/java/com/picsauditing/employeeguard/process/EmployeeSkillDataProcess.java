package com.picsauditing.employeeguard.process;

import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.entities.Role;
import com.picsauditing.employeeguard.services.StatusCalculatorService;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import com.picsauditing.employeeguard.services.entity.ProjectEntityService;
import com.picsauditing.employeeguard.services.entity.RoleEntityService;
import com.picsauditing.employeeguard.services.entity.SkillEntityService;
import com.picsauditing.employeeguard.util.PicsCollectionUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EmployeeSkillDataProcess {

	@Autowired
	private SkillEntityService skillEntityService;
	@Autowired
	private ProjectEntityService projectEntityService;
	@Autowired
	private RoleEntityService roleEntityService;
	@Autowired
	private StatusCalculatorService statusCalculatorService;

	public EmployeeSkillData buildEmployeeSkillData(final Employee employee,
													final Collection<Integer> allSiteAndCorporateIds) {

		Set<AccountSkill> allEmployeeSkills = getAllSkillsForEmployee(employee, allSiteAndCorporateIds);
		Map<AccountSkill, SkillStatus> skillStatuses = statusCalculatorService.getSkillStatuses(employee, allEmployeeSkills);

		EmployeeSkillData employeeSkillData = new EmployeeSkillData();

		employeeSkillData.setAccountSkills(allEmployeeSkills);
		employeeSkillData.setSkillStatuses(skillStatuses);

		return employeeSkillData;
	}

	private Set<AccountSkill> getAllSkillsForEmployee(final Employee employee,
													  final Collection<Integer> allSiteAndCorporateIds) {
		Set<AccountSkill> allSkills = new HashSet<>();

		allSkills = addGroupSkills(allSkills, employee);
		allSkills = addRoleSkills(allSkills, employee);
		allSkills = addProjectRequiredSkills(allSkills, employee);
		allSkills = addSiteAndCorporateRequiredSkills(allSkills, allSiteAndCorporateIds);

		return allSkills;
	}

	private Set<AccountSkill> addGroupSkills(final Set<AccountSkill> accountSkills, final Employee employee) {
		accountSkills.addAll(skillEntityService.getGroupSkillsForEmployee(employee));

		return accountSkills;
	}

	private Set<AccountSkill> addRoleSkills(final Set<AccountSkill> accountSkills, final Employee employee) {
		Set<Role> roles = roleEntityService.getAllSiteRolesForEmployee(employee);
		Map<Role, Set<AccountSkill>> roleSkills = skillEntityService.getSkillsForRoles(roles);

		accountSkills.addAll(PicsCollectionUtil.mergeCollectionOfCollections(roleSkills.values()));

		return accountSkills;
	}

	private Set<AccountSkill> addProjectRequiredSkills(final Set<AccountSkill> accountSkills, final Employee employee) {
		Set<Project> projects = projectEntityService.getProjectsForEmployee(employee);
		Map<Project, Set<AccountSkill>> projectRequiredSkills = skillEntityService.getRequiredSkillsForProjects(projects);

		accountSkills.addAll(PicsCollectionUtil.mergeCollectionOfCollections(projectRequiredSkills.values()));

		return accountSkills;
	}

	private Set<AccountSkill> addSiteAndCorporateRequiredSkills(final Set<AccountSkill> accountSkills,
																final Collection<Integer> allSiteAndCorporateIds) {
		Map<Integer, Set<AccountSkill>> siteAndCorporateRequiredSkills = skillEntityService
				.getSiteRequiredSkills(allSiteAndCorporateIds);

		accountSkills.addAll(PicsCollectionUtil
				.mergeCollectionOfCollections(siteAndCorporateRequiredSkills.values()));

		return accountSkills;
	}

}
