package com.picsauditing.employeeguard.services;

import com.picsauditing.employeeguard.daos.*;
import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.entities.helper.BaseEntityCallback;
import com.picsauditing.employeeguard.entities.helper.EntityHelper;
import com.picsauditing.util.Strings;
import com.picsauditing.util.generic.IntersectionAndComplementProcess;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class ContractorProjectService {

	@Autowired
	private ProjectCompanyDAO projectCompanyDAO;
	@Autowired
	private AccountGroupEmployeeDAO accountGroupEmployeeDAO;
	@Autowired
	private AccountSkillEmployeeDAO accountSkillEmployeeDAO;
	@Autowired
	private AccountSkillGroupDAO accountSkillGroupDAO;
	@Autowired
	private AccountSkillDAO accountSkillDAO;

	public ProjectCompany getProject(String id, int accountId) {
		return projectCompanyDAO.findProject(NumberUtils.toInt(id), accountId);
	}

	public List<ProjectCompany> getProjectsForContractor(int accountId) {
		return projectCompanyDAO.findByContractorAccount(accountId);
	}

	public List<ProjectCompany> search(String searchTerm, int accountId) {
		if (Strings.isEmpty(searchTerm)) {
			return Collections.emptyList();
		}

		return projectCompanyDAO.search(searchTerm, accountId);
	}

	public void assignEmployeeToProjectRole(Employee employee, ProjectRole projectRole, int appUserId) {
		Date now = new Date();
		// the employee needs to have all the operator's skills for that project
		List<AccountSkill> accountSkills = getSkillsForProjectGroup(projectRole);
		List<AccountSkillEmployee> accountSkillEmployees = buildAccountSkillEmployees(employee, accountSkills);

		accountSkillEmployees = IntersectionAndComplementProcess.intersection(accountSkillEmployees, employee.getSkills(),
				AccountSkillEmployee.COMPARATOR, new BaseEntityCallback(appUserId, now));
		accountSkillEmployeeDAO.save(accountSkillEmployees);

		AccountGroupEmployee accountGroupEmployee = new AccountGroupEmployee(employee, projectRole.getRole());
		EntityHelper.setCreateAuditFields(accountGroupEmployee, appUserId, now);
		accountGroupEmployeeDAO.save(accountGroupEmployee);
	}

	private List<AccountSkill> getSkillsForProjectGroup(ProjectRole projectRole) {
		return accountSkillDAO.findByGroups(Arrays.asList(projectRole.getRole()));
	}

	private List<AccountSkillEmployee> buildAccountSkillEmployees(Employee employee, List<AccountSkill> skills) {
		if (skills.isEmpty()) {
			return Collections.emptyList();
		}

		List<AccountSkillEmployee> accountSkillEmployees = new ArrayList<>();
		for (AccountSkill accountSkill : skills) {
			AccountSkillEmployee accountSkillEmployee = new AccountSkillEmployee(accountSkill, employee);
			accountSkillEmployee.setStartDate(new Date());
			accountSkillEmployees.add(accountSkillEmployee);
		}

		return accountSkillEmployees;
	}

	public void unassignEmployeeFromProjectRole(Employee employee, ProjectRole projectRole, int appUserId) {
		Date now = new Date();

		List<AccountSkillEmployee> accountSkillEmployees = getListOfSkillsForThisProject(employee, projectRole);
		EntityHelper.softDelete(accountSkillEmployees, appUserId, now);
		accountSkillEmployeeDAO.save(accountSkillEmployees);

		AccountGroupEmployee accountGroupEmployee = accountGroupEmployeeDAO.findByGroupAndEmployee(employee, projectRole.getRole());
		EntityHelper.softDelete(accountGroupEmployee, appUserId, now);
		accountGroupEmployeeDAO.save(accountGroupEmployee);
	}

	private List<AccountSkillEmployee> getListOfSkillsForThisProject(Employee employee, ProjectRole projectRole) {
		List<AccountGroup> groups = accountGroupEmployeeDAO.findByAccountAndEmployee(projectRole.getProject().getAccountId(), employee.getId());
		List<AccountSkillGroup> accountSkillGroups = accountSkillGroupDAO.findByGroups(groups);
		Map<AccountSkill, List<AccountGroup>> skillToGroups = getSkillToGroupsMap(accountSkillGroups);
		List<AccountSkill> deletableSkills = filterDeletableAccountSkills(skillToGroups, projectRole.getRole().getId());

		return accountSkillEmployeeDAO.findByEmployeeAndSkills(employee, deletableSkills);
	}

	private Map<AccountSkill, List<AccountGroup>> getSkillToGroupsMap(List<AccountSkillGroup> accountSkillGroups) {
		Map<AccountSkill, List<AccountGroup>> skillToGroups = new HashMap<>();

		for (AccountSkillGroup accountSkillGroup : accountSkillGroups) {
			AccountSkill skill = accountSkillGroup.getSkill();
			AccountGroup group = accountSkillGroup.getGroup();

			if (!skillToGroups.containsKey(skill)) {
				skillToGroups.put(skill, new ArrayList<AccountGroup>());
			}

			skillToGroups.get(skill).add(group);
		}

		return skillToGroups;
	}

	private List<AccountSkill> filterDeletableAccountSkills(Map<AccountSkill, List<AccountGroup>> skillToGroups, int roleId) {
		List<AccountSkill> deletable = new ArrayList<>();

		for (Map.Entry<AccountSkill, List<AccountGroup>> entry : skillToGroups.entrySet()) {
			if (entry.getValue().size() == 1 && entry.getValue().get(0).getId() == roleId) {
				deletable.add(entry.getKey());
			}
		}

		return deletable;
	}

	public Map<Integer, List<Integer>> sumEmployeeRolesForProject(final int accountId, final Project project) {
		if (accountId == 0 || project == null) {
			return Collections.emptyMap();
		}

		Map<Integer, List<Integer>> sumEmployeeRoles = new HashMap<>();



		return sumEmployeeRoles;
	}
}