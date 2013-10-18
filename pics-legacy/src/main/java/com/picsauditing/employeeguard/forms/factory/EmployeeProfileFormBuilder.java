package com.picsauditing.employeeguard.forms.factory;

import com.picsauditing.employeeguard.entities.AccountGroup;
import com.picsauditing.employeeguard.entities.AccountSkillEmployee;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Profile;
import com.picsauditing.employeeguard.forms.employee.CompanyGroupInfo;
import com.picsauditing.employeeguard.forms.employee.EmployeeProfileForm;
import com.picsauditing.employeeguard.forms.employee.SkillInfo;
import com.picsauditing.model.i18n.KeyValue;
import com.picsauditing.employeeguard.services.AccountGroupEmployeeService;
import com.picsauditing.employeeguard.services.AccountService;
import com.picsauditing.employeeguard.services.AccountSkillEmployeeService;
import com.picsauditing.employeeguard.services.GroupService;
import com.picsauditing.employeeguard.services.calculator.SkillStatusCalculator;
import com.picsauditing.employeeguard.services.models.AccountModel;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmployeeProfileFormBuilder {

	@Autowired
	private AccountService accountService;
	@Autowired
	private AccountSkillEmployeeService accountSkillEmployeeService;
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

	private List<SkillInfo> getSkillsForProfile(Profile profile) {
		List<AccountSkillEmployee> accountSkillEmployees = accountSkillEmployeeService.findByProfile(profile);
		return getSkillInfo(accountSkillEmployees);
	}

	private List<SkillInfo> getSkillInfo(List<AccountSkillEmployee> accountSkillEmployees) {
		List<SkillInfo> skills = new ArrayList<>(accountSkillEmployees.size());
		for (AccountSkillEmployee accountSkillEmployee : accountSkillEmployees) {
			skills.add(mapAccountSkillToSkillInfo(accountSkillEmployee));
		}

		return skills;
	}

	private SkillInfo mapAccountSkillToSkillInfo(AccountSkillEmployee accountSkillEmployee) {
		SkillInfo skillInfo = new SkillInfo();
		skillInfo.setId(accountSkillEmployee.getSkill().getId());
		skillInfo.setName(accountSkillEmployee.getSkill().getName());
		skillInfo.setSkillStatus(SkillStatusCalculator.calculateStatusFromSkill(accountSkillEmployee));
		return skillInfo;
	}

	private Map<Integer, List<KeyValue<Integer, String>>> getAccountGroupMap(Profile profile) {
		Map<Integer, List<KeyValue<Integer, String>>> accountGroupMap = new HashMap<>();

		List<Integer> accountIds = getAccountIds(profile.getEmployees());
		for (Integer accountId : accountIds) {
			List<AccountGroup> groups = groupService.getGroupsForAccount(accountId);
			accountGroupMap.put(accountId, getKeyValueList(groups));
		}

		return accountGroupMap;
	}

	private List<KeyValue<Integer, String>> getKeyValueList(List<AccountGroup> groups) {
		List<KeyValue<Integer, String>> values = new ArrayList<>();
		for (AccountGroup group : groups) {
			values.add(new KeyValue(group.getAccountId(), group.getName()));
		}

		return values;
	}

	private List<CompanyGroupInfo> getCompanyGroupInfoList(final Profile profile, List<AccountModel> accountModels) {
		List<CompanyGroupInfo> companyGroupInfoList = new ArrayList<>();

		Map<Integer, List<AccountGroup>> map = groupService.getMapOfAccountToGroupListByProfile(profile);

		for (AccountModel accountModel : accountModels) {
			CompanyGroupInfo companyGroupInfo = new CompanyGroupInfo();
			companyGroupInfo.setAccountModel(accountModel);
			companyGroupInfo.setGroupInfoList(map.get(accountModel.getId()));
			companyGroupInfoList.add(companyGroupInfo);
		}

		return companyGroupInfoList;
	}
}
