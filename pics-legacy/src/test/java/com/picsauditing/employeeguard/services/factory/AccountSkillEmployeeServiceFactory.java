package com.picsauditing.employeeguard.services.factory;

import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.services.AccountSkillEmployeeService;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class AccountSkillEmployeeServiceFactory {

	private static AccountSkillEmployeeService accountSkillEmployeeService = Mockito.mock(AccountSkillEmployeeService.class);

	public static AccountSkillEmployeeService getAccountSkillEmployeeService() {
		Mockito.reset(accountSkillEmployeeService);

		AccountSkillProfile accountSkillEmployee = new AccountSkillProfile();
		AccountSkill accountSkill = new AccountSkill();
		accountSkill.setSkillType(SkillType.Certification);

		ProfileDocument profileDocument = new ProfileDocument();

		accountSkillEmployee.setProfileDocument(profileDocument);
		accountSkillEmployee.setSkill(accountSkill);

		List<AccountSkillProfile> accountSkillEmployees = Arrays.asList(accountSkillEmployee, new AccountSkillProfile());

		when(accountSkillEmployeeService.findByProfile(any(Profile.class))).thenReturn(accountSkillEmployees);
		when(accountSkillEmployeeService.getAccountSkillEmployeeForProfileAndSkill(any(Profile.class), any(AccountSkill.class))).thenReturn(accountSkillEmployee);
		when(accountSkillEmployeeService.getSkillsForAccountAndEmployee(any(Employee.class))).thenReturn(accountSkillEmployees);
		when(accountSkillEmployeeService.linkProfileDocumentToEmployeeSkill(any(AccountSkillProfile.class), any(ProfileDocument.class))).thenReturn(accountSkillEmployee);

		return accountSkillEmployeeService;
	}
}
