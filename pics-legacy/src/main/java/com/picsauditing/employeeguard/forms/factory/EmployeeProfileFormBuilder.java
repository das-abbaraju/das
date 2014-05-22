package com.picsauditing.employeeguard.forms.factory;

import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Group;
import com.picsauditing.employeeguard.entities.Profile;
import com.picsauditing.employeeguard.forms.employee.CompanyGroupInfo;
import com.picsauditing.employeeguard.forms.employee.EmployeeProfileForm;
import com.picsauditing.employeeguard.models.AccountModel;
import com.picsauditing.employeeguard.process.ProfileSkillData;
import com.picsauditing.employeeguard.process.ProfileSkillStatusProcess;
import com.picsauditing.employeeguard.services.AccountService;
import com.picsauditing.employeeguard.services.GroupService;
import com.picsauditing.employeeguard.services.status.SkillStatus;
import com.picsauditing.employeeguard.viewmodel.model.Skill;
import com.picsauditing.model.i18n.KeyValue;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class EmployeeProfileFormBuilder {

	@Autowired
	private AccountService accountService;
	@Autowired
	private ProfileSkillStatusProcess profileSkillStatusProcess;
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
		ProfileSkillData profileSkillData = profileSkillStatusProcess.buildProfileSkillData(profile);
		return getSkillInfo(profileSkillData.getSkillStatusMap());
	}

	private List<Skill> getSkillInfo(final Map<AccountSkill, SkillStatus> skillStatusMap) {
		if (MapUtils.isEmpty(skillStatusMap)) {
			return Collections.emptyList();
		}

		List<Skill> skills = new ArrayList<>();
		for (AccountSkill accountSkill : skillStatusMap.keySet()) {
			skills.add(mapAccountSkillToSkillInfo(accountSkill, skillStatusMap.get(accountSkill)));
		}

		Collections.sort(skills);

		return skills;
	}

	private Skill mapAccountSkillToSkillInfo(final AccountSkill accountSkill, final SkillStatus skillStatus) {
		return new Skill.Builder()
				.id(accountSkill.getId())
				.name(accountSkill.getName())
				.skillStatus(skillStatus)
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
