package com.picsauditing.employeeguard.services;

import com.picsauditing.employeeguard.daos.RoleEmployeeDAO;
import com.picsauditing.employeeguard.daos.SiteSkillDAO;
import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.util.Extractor;
import com.picsauditing.employeeguard.util.ExtractorUtil;
import com.picsauditing.util.generic.GenericPredicate;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class SkillAssignmentHelper {

	@Autowired
	private AccountService accountService;
	@Autowired
	private RoleEmployeeDAO roleEmployeeDAO;
	@Autowired
	private SiteSkillDAO siteSkillDAO;

	public Set<AccountSkill> getRequiredSkillsFromProjectsAndSiteRoles(List<ProjectCompany> projectCompanies, Employee employee, Map<Role, Role> siteToCorporateRoles) {
		if (CollectionUtils.isEmpty(projectCompanies)) {
			return Collections.emptySet();
		}

		Set<AccountSkill> required = new HashSet<>();

		for (ProjectCompany projectCompany : projectCompanies) {
			Project project = projectCompany.getProject();

			required.addAll(getProjectSkills(project));
			required.addAll(getProjectRoleSkills(project));
		}

		List<Integer> siteIds = getSiteIdsFromProjects(projectCompanies);

		required.addAll(getSiteSkills(new HashSet<>(siteIds)));
		required.addAll(getSiteRoleSkills(employee, siteIds, siteToCorporateRoles));

		return required;
	}

	private Set<AccountSkill> getProjectSkills(Project project) {
		Set<AccountSkill> requiredSkills = new HashSet<>();

		for (ProjectSkill projectSkill : project.getSkills()) {
			requiredSkills.add(projectSkill.getSkill());
		}

		return requiredSkills;
	}

	private Set<AccountSkill> getProjectRoleSkills(Project project) {
		Set<AccountSkill> requiredSkills = new HashSet<>();

		for (ProjectRole projectRole : project.getRoles()) {
			for (AccountSkillRole accountSkillRole : projectRole.getRole().getSkills()) {
				requiredSkills.add(accountSkillRole.getSkill());
			}
		}

		return requiredSkills;
	}

	private List<Integer> getSiteIdsFromProjects(List<ProjectCompany> projectCompanies) {
		return ExtractorUtil.extractList(projectCompanies, new Extractor<ProjectCompany, Integer>() {
			@Override
			public Integer extract(ProjectCompany projectCompany) {
				return projectCompany.getProject().getAccountId();
			}
		});
	}

	private Set<AccountSkill> getSiteSkills(Set<Integer> siteIds) {
		Set<AccountSkill> requiredSkills = new HashSet<>();
		Set<Integer> siteAndCorporateIds = new HashSet<>();

		for (Integer siteId : siteIds) {
			siteAndCorporateIds.addAll(accountService.getTopmostCorporateAccountIds(siteId));
		}

		siteAndCorporateIds.addAll(siteIds);
		List<SiteSkill> siteSkills = siteSkillDAO.findByAccountIds(siteAndCorporateIds);
		for (SiteSkill siteSkill : siteSkills) {
			requiredSkills.add(siteSkill.getSkill());
		}

		return requiredSkills;
	}

	private Set<AccountSkill> getSiteRoleSkills(Employee employee, List<Integer> siteIds,
	                                            Map<Role, Role> siteToCorporateRoles) {
		Set<AccountSkill> requiredSkills = new HashSet<>();
		List<RoleEmployee> siteRoles = roleEmployeeDAO.findByEmployeeAndSiteIds(employee.getId(), siteIds);

		for (RoleEmployee roleEmployee : siteRoles) {
			Role corporateRole = siteToCorporateRoles.get(roleEmployee.getRole());

			for (AccountSkillRole accountSkillRole : corporateRole.getSkills()) {
				requiredSkills.add(accountSkillRole.getSkill());
			}
		}

		return requiredSkills;
	}

	public Set<AccountSkillEmployee> filterNoLongerNeededEmployeeSkills(Employee employee,
	                                                                    final int contractorId,
	                                                                    final Set<AccountSkill> requiredSkills) {
		HashSet<AccountSkillEmployee> accountSkillEmployees = new HashSet<>(employee.getSkills());

		CollectionUtils.filter(accountSkillEmployees, new GenericPredicate<AccountSkillEmployee>() {
			@Override
			public boolean evaluateEntity(AccountSkillEmployee accountSkillEmployee) {
				boolean notContractorSkill = accountSkillEmployee.getSkill().getAccountId() != contractorId;
				boolean notRequiredSkill = !requiredSkills.contains(accountSkillEmployee.getSkill());
				return notContractorSkill && notRequiredSkill;
			}
		});

		return accountSkillEmployees;
	}
}
