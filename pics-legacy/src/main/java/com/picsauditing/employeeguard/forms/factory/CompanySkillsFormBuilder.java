package com.picsauditing.employeeguard.forms.factory;

import com.picsauditing.employeeguard.forms.employee.CompanySkillInfo;
import com.picsauditing.employeeguard.forms.employee.CompanySkillsForm;
import com.picsauditing.employeeguard.forms.employee.SkillInfo;
import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.AccountSkillEmployee;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Profile;
import com.picsauditing.employeeguard.services.AccountService;
import com.picsauditing.employeeguard.services.AccountSkillEmployeeService;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import com.picsauditing.employeeguard.services.calculator.SkillStatusCalculator;
import com.picsauditing.employeeguard.services.models.AccountModel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class CompanySkillsFormBuilder {

	@Autowired
	private AccountService accountService;
	@Autowired
	private AccountSkillEmployeeService accountSkillEmployeeService;

	public CompanySkillsForm build(Profile profile) {
		List<CompanySkillInfo> companySkillInfoList = new ArrayList<>();

		List<AccountModel> accounts = getAccountsForProfile(profile);
		for (AccountModel accountModel : accounts) {
			Employee employee = getEmployeeForAccount(profile.getEmployees(), accountModel.getId());

			CompanySkillInfo companySkillInfo = new CompanySkillInfo();
			companySkillInfo.setAccountModel(accountModel);
			companySkillInfo.setCompletedSkills(getSkillInfoForEmployee(employee, SkillStatus.Complete));
			companySkillInfo.setAboutToExpireSkills(getSkillInfoForEmployee(employee, SkillStatus.Expiring));
			companySkillInfo.setExpiredSkills(getSkillInfoForEmployee(employee, SkillStatus.Expired));
			companySkillInfoList.add(companySkillInfo);
		}

		CompanySkillsForm companySkillsForm = new CompanySkillsForm();
		companySkillsForm.setCompanySkillInfoList(companySkillInfoList);
		return companySkillsForm;
	}

	private List<AccountModel> getAccountsForProfile(Profile profile) {
		List<Employee> employees = profile.getEmployees();
		List<Integer> accountIds = getAccountIds(employees);
		return accountService.getAccountsByIds(accountIds);
	}

	private List<Integer> getAccountIds(List<Employee> employees) {
		List<Integer> accountIds = new ArrayList<>();
		for (Employee employee : employees) {
			accountIds.add(employee.getAccountId());
		}

		return accountIds;
	}

	private Employee getEmployeeForAccount(List<Employee> employees, int accountId) {
		for (Employee employee : employees) {
			if (employee.getAccountId() == accountId) {
				return employee;
			}
		}

		return null;
	}

	private List<SkillInfo> getSkillInfoForEmployee(Employee employee, final SkillStatus status) {
		List<AccountSkillEmployee> accountSkills = getSkillsForAccountAndEmployee(employee);

		CollectionUtils.filter(accountSkills, new Predicate() {
			@Override
			public boolean evaluate(Object object) {
				return status == SkillStatusCalculator.calculateStatusFromSkill((AccountSkillEmployee) object);
			}
		});

		return mapAccountSkillToSkillInfo(accountSkills);
	}

	private List<AccountSkillEmployee> getSkillsForAccountAndEmployee(Employee employee) {
		return accountSkillEmployeeService.getSkillsForAccountAndEmployee(employee);
	}

	private List<SkillInfo> mapAccountSkillToSkillInfo(List<AccountSkillEmployee> accountSkills) {
		List<SkillInfo> skillInfoList = new ArrayList<>();
		for (AccountSkillEmployee accountSkill : accountSkills) {
			skillInfoList.add(mapAccountSkillToSkillInfo(accountSkill.getSkill()));
		}

		return skillInfoList;
	}

	private SkillInfo mapAccountSkillToSkillInfo(AccountSkill accountSkill) {
		SkillInfo skillInfo = new SkillInfo();
		skillInfo.setId(accountSkill.getId());
		skillInfo.setName(accountSkill.getName());
		skillInfo.setSkillStatus(SkillStatus.Complete);
		return skillInfo;
	}
}
