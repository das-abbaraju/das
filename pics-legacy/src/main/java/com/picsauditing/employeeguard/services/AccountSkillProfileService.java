package com.picsauditing.employeeguard.services;

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.employeeguard.daos.AccountSkillProfileDAO;
import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.entities.builders.AccountSkillProfileBuilder;
import com.picsauditing.employeeguard.forms.employee.SkillDocumentForm;
import com.picsauditing.employeeguard.services.entity.ProfileEntityService;
import com.picsauditing.employeeguard.services.entity.SkillEntityService;
import com.picsauditing.employeeguard.services.status.ExpirationCalculator;
import com.picsauditing.jpa.entities.Account;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;


public class AccountSkillProfileService {
	private static Logger logger = LoggerFactory.getLogger(AccountSkillProfileService.class);

	@Autowired
	private AccountSkillProfileDAO accountSkillProfileDAO;
	@Autowired
	private ProfileDocumentService profileDocumentService;
	@Autowired
	private ProfileEntityService profileEntityService;
	@Autowired
	private SkillEntityService skillEntityService;

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

	public AccountSkillProfile getAccountSkillProfile(int appUserId, int skillId) {
		Profile profile = profileEntityService.findByAppUserId(appUserId);
		AccountSkill skill = skillEntityService.find(skillId);

		return this.getAccountSkillProfileForProfileAndSkill(profile, skill);
	}

	public ProfileDocument getAccountSkillProfileDocument(int appUserId, int skillId) {
		Profile profile = profileEntityService.findByAppUserId(appUserId);
		AccountSkill skill = skillEntityService.find(skillId);

		AccountSkillProfile accountSkillProfile= this.getAccountSkillProfileForProfileAndSkill(profile, skill);

		return this.getAccountSkillProfileDocument(accountSkillProfile);
	}

	public ProfileDocument getAccountSkillProfileDocument(AccountSkillProfile accountSkillProfile) {
		ProfileDocument profileDocument = null;
		if (accountSkillProfile != null) {
			profileDocument = accountSkillProfile.getProfileDocument();
		}

		return profileDocument;
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

	public void update(final AccountSkill accountSkill,
					   final Profile profile,
					   final SkillDocumentForm skillDocumentForm) {
		boolean satisfyingSkillFirstTime=false;
		SkillType skillType = accountSkill.getSkillType();
		AccountSkillProfile accountSkillProfile = accountSkillProfileDAO.findBySkillAndProfile(accountSkill, profile);
		if(accountSkillProfile==null){
			satisfyingSkillFirstTime=true;
			accountSkillProfile = newAccountSkillProfile(accountSkill, profile);
		}

		if (skillType.isTraining()) {
			handleTrainingSkillSatisfaction(accountSkillProfile, satisfyingSkillFirstTime, skillDocumentForm);
		}
		else if (skillType.isCertification()) {
			handleCertificationSkillSatisfaction(accountSkillProfile, satisfyingSkillFirstTime, skillDocumentForm);
		}


		return;
	}

	private void handleTrainingSkillSatisfaction(AccountSkillProfile accountSkillProfile, boolean satisfyingSkillFirstTime, final SkillDocumentForm skillDocumentForm){
		if (skillDocumentForm != null && skillDocumentForm.isVerified()) {
			accountSkillProfile.setStartDate(DateBean.today());
			accountSkillProfileDAO.save(accountSkillProfile);
		}
		else if(!satisfyingSkillFirstTime){
			accountSkillProfileDAO.delete(accountSkillProfile);
		}
	}

	private void handleCertificationSkillSatisfaction(AccountSkillProfile accountSkillProfile, boolean satisfyingSkillFirstTime, final SkillDocumentForm skillDocumentForm){
		if(skillDocumentForm.getDocumentId()<=0) {
			logger.warn("No document attached to Certification Skill - Returning without action");
			return;
		}

		ProfileDocument document = profileDocumentService.getDocument(skillDocumentForm.getDocumentId());
		accountSkillProfile.setProfileDocument(document);
		accountSkillProfileDAO.save(accountSkillProfile);
	}

	private AccountSkillProfile newAccountSkillProfile(final AccountSkill accountSkill, final Profile profile){
		return new AccountSkillProfileBuilder()
						.profile(profile)
						.accountSkill(accountSkill)
						.createdBy(1)
						.createdDate(DateBean.today())
						.startDate(DateBean.today())
						.build();
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
