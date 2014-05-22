package com.picsauditing.employeeguard.services;

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.employeeguard.daos.AccountSkillProfileDAO;
import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.entities.builders.AccountSkillProfileBuilder;
import com.picsauditing.employeeguard.forms.employee.SkillDocumentForm;
import com.picsauditing.employeeguard.services.status.ExpirationCalculator;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;


public class AccountSkillProfileService {

	@Autowired
	private AccountSkillProfileDAO accountSkillProfileDAO;
	@Autowired
	private ProfileDocumentService profileDocumentService;

	public List<AccountSkillProfile> findByProfile(final Profile profile) {
		return accountSkillProfileDAO.findByProfile(profile);
	}

	public List<AccountSkillProfile> findByEmployeesAndSkills(final List<Employee> employees,
															  final List<AccountSkill> accountSkills) {
		if (CollectionUtils.isEmpty(employees) || CollectionUtils.isEmpty(accountSkills)) {
			return Collections.emptyList();
		}

		return accountSkillProfileDAO.findByEmployeesAndSkills(employees, accountSkills);
	}

	public AccountSkillProfile linkProfileDocumentToEmployeeSkill(final AccountSkillProfile accountSkillProfile,
																  final ProfileDocument profileDocument) {
		accountSkillProfile.setProfileDocument(profileDocument);
		accountSkillProfile.setEndDate(null);
		return accountSkillProfileDAO.save(accountSkillProfile);
	}

	public AccountSkillProfile getAccountSkillProfileForProfileAndSkill(Profile profile, AccountSkill skill) {
		return accountSkillProfileDAO.findByProfileAndSkill(profile, skill);
	}

	public List<AccountSkillProfile> getSkillsForAccountAndEmployee(Employee employee) {
		return accountSkillProfileDAO.findByContractorCreatedSkillsForEmployee(employee);
	}

	public void save(AccountSkillProfile accountSkillProfile) {
		accountSkillProfileDAO.save(accountSkillProfile);
	}

	public void save(List<AccountSkillProfile> accountSkillProfiles) {
		if (CollectionUtils.isNotEmpty(accountSkillProfiles)) {
			accountSkillProfileDAO.save(accountSkillProfiles);
		}
	}

	public void update(AccountSkillProfile accountSkillProfile, final SkillDocumentForm skillDocumentForm) {
		AccountSkill skill = accountSkillProfile.getSkill();
		SkillType skillType = skill.getSkillType();

		if (skillType.isCertification()) {
			ProfileDocument document = profileDocumentService.getDocument(skillDocumentForm.getDocumentId());
			linkProfileDocumentToEmployeeSkill(accountSkillProfile, document);
		} else if (skillType.isTraining()) {
			accountSkillProfile.setEndDate(null);
			accountSkillProfileDAO.save(accountSkillProfile);
		}
	}

	public void update(AccountSkillProfile accountSkillProfile, final ProfileDocument document) {
		AccountSkill skill = accountSkillProfile.getSkill();
		SkillType skillType = skill.getSkillType();

		if (skillType.isCertification()) {
			linkProfileDocumentToEmployeeSkill(accountSkillProfile, document);
			accountSkillProfileDAO.save(accountSkillProfile);
		}
	}

	public void update(final AccountSkill accountSkill,
					   final Profile profile,
					   final SkillDocumentForm skillDocumentForm) {
		SkillType skillType = accountSkill.getSkillType();

		List<AccountSkillProfile> accountSkillProfiles = accountSkillProfileDAO
				.findBySkillAndProfile(accountSkill, profile);

		accountSkillProfiles = addNewAccountSkillProfiles(accountSkillProfiles, profile, accountSkill);

		if (skillType.isCertification()) {
			ProfileDocument document = profileDocumentService.getDocument(skillDocumentForm.getDocumentId());
			linkProfileDocumentToEmployeeSkills(accountSkillProfiles, document);
		} else if (skillType.isTraining()) {
			if (skillDocumentForm != null && skillDocumentForm.isVerified()) {
				for (AccountSkillProfile accountSkillProfile : accountSkillProfiles) {
					accountSkillProfile.setEndDate(ExpirationCalculator.calculateExpirationDate(accountSkillProfile));
				}
			} else {
				for (AccountSkillProfile accountSkillProfile : accountSkillProfiles) {
					accountSkillProfile.setEndDate(null);
				}
			}

			accountSkillProfileDAO.save(accountSkillProfiles);
		}
	}

	public void linkProfileDocumentToEmployeeSkills(final List<AccountSkillProfile> accountSkillProfiles,
													final ProfileDocument profileDocument) {
		for (AccountSkillProfile accountSkillProfile : accountSkillProfiles) {
			accountSkillProfile.setProfileDocument(profileDocument);
			accountSkillProfile.setEndDate(ExpirationCalculator.calculateExpirationDate(accountSkillProfile));
		}

		accountSkillProfileDAO.save(accountSkillProfiles);
	}

	public List<AccountSkillProfile> addNewAccountSkillProfiles(final List<AccountSkillProfile> accountSkillProfiles,
																final Profile profile,
																final AccountSkill accountSkill) {
		List<AccountSkillProfile> allAccountSkillProfiles = new ArrayList<>(accountSkillProfiles);
		for (Employee employee : profile.getEmployees()) {
			if (!foundAccountSkillProfile(employee, allAccountSkillProfiles)) {
				allAccountSkillProfiles.add(new AccountSkillProfileBuilder()
						.profile(profile)
						.accountSkill(accountSkill)
						.createdBy(1)
						.createdDate(DateBean.today())
						.startDate(DateBean.today())
						.build());
			}
		}

		return allAccountSkillProfiles;
	}

	private boolean foundAccountSkillProfile(final Employee employee, List<AccountSkillProfile> acountSkillProfiles) {
		for (AccountSkillProfile accountSkillProfile : acountSkillProfiles) {
			if (accountSkillProfile.getProfile().getEmployees().contains(employee)) {
				return true;
			}
		}

		return false;
	}

	public List<AccountSkillProfile> getAccountSkillProfileForProjectAndContractor(final Project project,
																																								 final int accountId) {
		return accountSkillProfileDAO.findByProjectAndContractor(project, accountId);
	}

	public List<AccountSkillProfile> getSkillsForAccount(final int accountId) {
		return accountSkillProfileDAO.findByEmployeeAccount(accountId);
	}

	public Map<Employee, Set<AccountSkillProfile>> getSkillMapForAccountAndRole(final int accountId, final int roleId) {
		List<AccountSkillProfile> roleSkills = accountSkillProfileDAO.findDistinctByContractorAndRole(accountId, roleId);

		return buildEmployeeAccountSkillProfileMap(new HashSet<>(roleSkills), new HashSet<>(Arrays.asList(accountId)));
	}

	public Map<Employee, Set<AccountSkillProfile>> getEmployeeSkillMapForContractorsAndSite(
			final Set<Integer> contractorIds,
			final int siteId,
			final List<Integer> corporateIds,
			final Map<Role, Role> siteToCorporateRoles) {
		Collection<Role> corporateRoles = siteToCorporateRoles.values();
		Set<Integer> siteAndCorporateIds = new HashSet<>(corporateIds);
		siteAndCorporateIds.add(siteId);

		Set<AccountSkillProfile> allSiteSkills = getAllSiteSkills(contractorIds, siteId, corporateRoles, siteAndCorporateIds);

		return buildEmployeeAccountSkillProfileMap(allSiteSkills, contractorIds);
	}

	private Map<Employee, Set<AccountSkillProfile>> buildEmployeeAccountSkillProfileMap(final Set<AccountSkillProfile> allSiteSkills,
																						final Set<Integer> contractorIds) {
		Map<Employee, Set<AccountSkillProfile>> employeeSkills = new HashMap<>();
		for (AccountSkillProfile accountSkillProfile : allSiteSkills) {
			for (Employee employee : accountSkillProfile.getProfile().getEmployees()) {
				if (contractorIds.contains(employee.getAccountId())) {
					if (!employeeSkills.containsKey(employee)) {
						employeeSkills.put(employee, new HashSet<AccountSkillProfile>());
					}

					employeeSkills.get(employee).add(accountSkillProfile);
				}
			}
		}

		return employeeSkills;
	}

	private Set<AccountSkillProfile> getAllSiteSkills(final Set<Integer> contractorIds,
													  final int siteId,
													  final Collection<Role> corporateRoles,
													  final Set<Integer> siteAndCorporateIds) {
		Set<AccountSkillProfile> allSiteSkills = new HashSet<>();

		allSiteSkills.addAll(accountSkillProfileDAO.getProjectRoleSkillsForContractorsAndSite(contractorIds, siteId));
		allSiteSkills.addAll(accountSkillProfileDAO.getProjectReqdSkillsForContractorsAndSite(contractorIds, siteId));
		allSiteSkills.addAll(accountSkillProfileDAO.getRoleSkillsForContractorsAndRoles(contractorIds, corporateRoles));
		allSiteSkills.addAll(accountSkillProfileDAO.getSiteReqdSkillsForContractorsAndSites(contractorIds, siteAndCorporateIds));

		return allSiteSkills;
	}

	public Table<Employee, AccountSkill, AccountSkillProfile> buildTable(final List<Employee> employees,
																		 final List<AccountSkill> skills) {
		List<AccountSkillProfile> accountSkillProfiles = findByEmployeesAndSkills(employees, skills);

		Table<Employee, AccountSkill, AccountSkillProfile> table = TreeBasedTable.create();
		for (Employee employee : employees) {
			for (AccountSkill skill : skills) {
				table.put(employee, skill, findAccountSkillProfileByEmployeeAndSkill(accountSkillProfiles, employee,
								skill));
			}
		}

		return table;
	}

	private AccountSkillProfile findAccountSkillProfileByEmployeeAndSkill(final List<AccountSkillProfile> accountSkillProfiles,
																																				final Employee employee,
																																				final AccountSkill skill) {
		for (AccountSkillProfile accountSkillProfile : accountSkillProfiles) {
			if (skill.equals(accountSkillProfile.getSkill())
					&& accountSkillProfile.getProfile().getEmployees().contains(employee)) {

				return accountSkillProfile;
			}
		}

		return new AccountSkillProfileBuilder().build();
	}
}
