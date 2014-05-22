package com.picsauditing.employeeguard.services.factory;

import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.services.AccountSkillProfileService;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class AccountSkillProfileServiceFactory {

	private static AccountSkillProfileService accountSkillProfileService = Mockito.mock(AccountSkillProfileService.class);

	public static AccountSkillProfileService getAccountSkillProfileService() {
		Mockito.reset(accountSkillProfileService);

		AccountSkillProfile accountSkillProfile = new AccountSkillProfile();
		AccountSkill accountSkill = new AccountSkill();
		accountSkill.setSkillType(SkillType.Certification);

		ProfileDocument profileDocument = new ProfileDocument();

		accountSkillProfile.setProfileDocument(profileDocument);
		accountSkillProfile.setSkill(accountSkill);

		List<AccountSkillProfile> accountSkillProfiles = Arrays.asList(accountSkillProfile, new AccountSkillProfile());

		when(accountSkillProfileService.findByProfile(any(Profile.class))).thenReturn(accountSkillProfiles);
		when(accountSkillProfileService.getAccountSkillProfileForProfileAndSkill(any(Profile.class), any(AccountSkill.class))).thenReturn(accountSkillProfile);
		when(accountSkillProfileService.getSkillsForAccountAndEmployee(any(Employee.class))).thenReturn(accountSkillProfiles);

		return accountSkillProfileService;
	}
}
