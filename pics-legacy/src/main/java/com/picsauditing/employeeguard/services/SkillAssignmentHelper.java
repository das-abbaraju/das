package com.picsauditing.employeeguard.services;

import com.picsauditing.employeeguard.daos.AccountSkillDAO;
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
	private AccountSkillDAO accountSkillDAO;

	public Set<AccountSkill> getRequiredSkillsFromProjectsAndSiteRoles(final Collection<ProjectCompany> projectCompanies,
																	   final Employee employee) {
		if (CollectionUtils.isEmpty(projectCompanies)) {
			return Collections.emptySet();
		}

		Set<AccountSkill> required = new HashSet<>();
		required.addAll(getProjectRequiredSkills(projectCompanies));

		Set<Integer> siteIds = getSiteIdsFromProjects(projectCompanies);

		required.addAll(getSiteSkills(siteIds));
		required.addAll(getSiteSkills(new HashSet<>(siteIds)));
		required.addAll(getSiteRoleSkills(employee, siteIds));

		return required;
	}

	private List<AccountSkill> getProjectRequiredSkills(final Collection<ProjectCompany> projectCompanies) {
		Set<Project> projects = getProjectsFromProjectCompanies(projectCompanies);
		if (CollectionUtils.isEmpty(projects)) {
			return Collections.emptyList();
		}

		return accountSkillDAO.findProjectRequiredSkills(projects);
	}

	private Set<Project> getProjectsFromProjectCompanies(final Collection<ProjectCompany> projectCompanies) {
		if (CollectionUtils.isEmpty(projectCompanies)) {
			return Collections.emptySet();
		}

		return ExtractorUtil.extractSet(projectCompanies, new Extractor<ProjectCompany, Project>() {

			@Override
			public Project extract(ProjectCompany projectCompany) {
				return projectCompany.getProject();
			}
		});
	}

	private Set<Integer> getSiteIdsFromProjects(Collection<ProjectCompany> projectCompanies) {
		return ExtractorUtil.extractSet(projectCompanies, new Extractor<ProjectCompany, Integer>() {
			@Override
			public Integer extract(ProjectCompany projectCompany) {
				return projectCompany.getProject().getAccountId();
			}
		});
	}

	private Set<AccountSkill> getSiteSkills(final Set<Integer> siteIds) {
		Set<Integer> siteAndCorporateIds = getSiteAndCorporateIds(siteIds);
		if (CollectionUtils.isEmpty(siteAndCorporateIds)) {
			return Collections.emptySet();
		}

		return new HashSet<>(accountSkillDAO.findSiteAndCorporateRequiredSkills(siteAndCorporateIds));
	}

	private Set<Integer> getSiteAndCorporateIds(Set<Integer> siteIds) {
		Set<Integer> siteAndCorporateIds = new HashSet<>();
		for (Integer siteId : siteIds) {
			siteAndCorporateIds.addAll(accountService.getTopmostCorporateAccountIds(siteId));
		}

		siteAndCorporateIds.addAll(siteIds);

		return siteAndCorporateIds;
	}

	private List<AccountSkill> getSiteRoleSkills(final Employee employee, final Collection<Integer> siteIds) {
		if (CollectionUtils.isEmpty(siteIds)) {
			return Collections.emptyList();
		}

		return accountSkillDAO.findBySiteAssignments(siteIds, employee);
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
