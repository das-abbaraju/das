package com.picsauditing.employeeguard.forms.factory;

import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.forms.employee.CompanyGroupInfo;
import com.picsauditing.employeeguard.forms.employee.EmployeeProfileForm;
import com.picsauditing.employeeguard.models.AccountModel;
import com.picsauditing.employeeguard.services.AccountService;
import com.picsauditing.employeeguard.services.AccountSkillProfileService;
import com.picsauditing.employeeguard.services.GroupService;
import com.picsauditing.employeeguard.services.status.SkillStatusCalculator;
import com.picsauditing.employeeguard.viewmodel.model.Skill;
import com.picsauditing.model.i18n.KeyValue;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class EmployeeProfileFormBuilder {

	@Autowired
	private AccountService accountService;
	@Autowired
	private AccountSkillProfileService accountSkillProfileService;
	@Autowired
	private GroupService groupService;

	@Autowired
	private EmployeeProfileEditFormBuilder employeeProfileEditFormBuilder;

	public EmployeeProfileForm build(Profile profile) {
		EmployeeProfileForm employeeProfileForm = new EmployeeProfileForm();
		employeeProfileForm.setEmploymentInfo(getAccountModels(profile));
		employeeProfileForm.setPersonalInformation(employeeProfileEditFormBuilder.build(profile));
		employeeProfileForm.setSkillInfoList(getSkillsForProfile(profile));
		employeeProfileForm.setCompanyGroupInfoList(getCompanyGroupInfoList(profile, getAccountModels(profile)));
		return employeeProfileForm;
	}


	private List<AccountModel> getAccountModels(Profile profile) {
		List<Integer> accountIds = getAccountIds(profile.getEmployees());
		return accountService.getAccountsByIds(accountIds);
	}

	private List<Integer> getAccountIds(List<Employee> employees) {
		List<Integer> accountIds = new ArrayList<>(employees.size());
		for (Employee employee : employees) {
			accountIds.add(employee.getAccountId());
		}

		return accountIds;
	}

	private List<Skill> getSkillsForProfile(Profile profile) {
		List<AccountSkillProfile> accountSkillProfiles = accountSkillProfileService.findByProfile(profile);
		return getSkillInfo(accountSkillProfiles);
	}

	private List<Skill> getSkillInfo(List<AccountSkillProfile> accountSkillProfiles) {
		List<Skill> skills = new ArrayList<>(accountSkillProfiles.size());
		for (AccountSkillProfile accountSkillProfile : accountSkillProfiles) {
			skills.add(mapAccountSkillToSkillInfo(accountSkillProfile));
		}

		Collections.sort(skills);

		return skills;
	}

	private Skill mapAccountSkillToSkillInfo(AccountSkillProfile accountSkillProfile) {
		AccountSkill accountSkill = accountSkillProfile.getSkill();
		return new Skill.Builder()
				.id(accountSkill.getId())
				.name(accountSkill.getName())
				.skillStatus(SkillStatusCalculator.calculateStatusFromSkill(accountSkillProfile))
				.build();
	}

	private Map<Integer, List<KeyValue<Integer, String>>> getAccountGroupMap(Profile profile) {
		Map<Integer, List<KeyValue<Integer, String>>> accountGroupMap = new HashMap<>();

		List<Integer> accountIds = getAccountIds(profile.getEmployees());
		for (Integer accountId : accountIds) {
			List<Group> groups = groupService.getGroupsForAccount(accountId);
			accountGroupMap.put(accountId, getKeyValueList(groups));
		}

		return accountGroupMap;
	}

	private List<KeyValue<Integer, String>> getKeyValueList(List<Group> groups) {
		List<KeyValue<Integer, String>> values = new ArrayList<>();
		for (Group group : groups) {
			values.add(new KeyValue(group.getAccountId(), group.getName()));
		}

		return values;
	}

	private List<CompanyGroupInfo> getCompanyGroupInfoList(final Profile profile, List<AccountModel> accountModels) {
		List<CompanyGroupInfo> companyGroupInfoList = new ArrayList<>();

		Map<Integer, List<Group>> map = groupService.getMapOfAccountToGroupListByProfile(profile);

		for (AccountModel accountModel : accountModels) {
			CompanyGroupInfo companyGroupInfo = new CompanyGroupInfo();
			companyGroupInfo.setAccountModel(accountModel);
			companyGroupInfo.setGroupInfoList(map.get(accountModel.getId()));
			companyGroupInfoList.add(companyGroupInfo);
		}

		return companyGroupInfoList;
	}
}
