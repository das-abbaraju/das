package com.picsauditing.employeeguard.services;

import com.picsauditing.employeeguard.daos.AccountSkillProfileDAO;
import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.entities.builders.AccountSkillBuilder;
import com.picsauditing.employeeguard.entities.builders.AccountSkillProfileBuilder;
import com.picsauditing.employeeguard.forms.employee.SkillDocumentForm;
import com.picsauditing.employeeguard.services.factory.ProfileDocumentServiceFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.Collections;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AccountSkillProfileServiceTest {

	private AccountSkillProfileService accountSkillProfileService;

	private ProfileDocumentService profileDocumentService;
	private static final int ACCOUNT_ID = 1100;

	@Mock
	private AccountSkillProfileDAO accountSkillProfileDAO;

	@Mock
	Profile profile;


	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		accountSkillProfileService = new AccountSkillProfileService();
		profileDocumentService = ProfileDocumentServiceFactory.getProfileDocumentService();

		Whitebox.setInternalState(accountSkillProfileService, "accountSkillProfileDAO", accountSkillProfileDAO);
		Whitebox.setInternalState(accountSkillProfileService, "profileDocumentService", profileDocumentService);

	}

	@Test
	public void testUpdate_Certification() throws Exception {
		AccountSkill accountSkill = new AccountSkillBuilder(ACCOUNT_ID).skillType(SkillType.Certification).build();
		AccountSkillProfile accountSkillProfile = new AccountSkillProfileBuilder().accountSkill(accountSkill).profile(profile).build();
		when(accountSkillProfileDAO.findBySkillAndProfile(any(AccountSkill.class), any(Profile.class))).thenReturn(accountSkillProfile);

		SkillDocumentForm skillDocumentForm = new SkillDocumentForm();
		skillDocumentForm.setDocumentId(1);


		accountSkillProfileService.update(accountSkill, profile, skillDocumentForm);

		verify(profileDocumentService).getDocument(anyInt());
		verify(accountSkillProfileDAO).save(any(AccountSkillProfile.class));

	}

	@Test
	public void testUpdate_Training_Unverified() throws Exception {
		AccountSkill accountSkill = new AccountSkillBuilder(ACCOUNT_ID).skillType(SkillType.Training).intervalPeriod(3).intervalType(IntervalType.DAY).build();
		AccountSkillProfile accountSkillProfile = new AccountSkillProfileBuilder().accountSkill(accountSkill).profile(profile).build();
		when(accountSkillProfileDAO.findBySkillAndProfile(any(AccountSkill.class), any(Profile.class))).thenReturn(accountSkillProfile);

		SkillDocumentForm skillDocumentForm = new SkillDocumentForm();

		accountSkillProfileService.update(accountSkill, profile, skillDocumentForm);

		assertNull(accountSkillProfile.getStartDate());
		verify(accountSkillProfileDAO).save(any(AccountSkillProfile.class));
	}

	@Test
	public void testUpdate_Training_Verified() throws Exception {
		AccountSkill accountSkill = new AccountSkillBuilder(ACCOUNT_ID).skillType(SkillType.Training).intervalPeriod(3).intervalType(IntervalType.DAY).build();
		AccountSkillProfile accountSkillProfile = new AccountSkillProfileBuilder().accountSkill(accountSkill).profile(profile).build();
		when(accountSkillProfileDAO.findBySkillAndProfile(any(AccountSkill.class), any(Profile.class))).thenReturn(accountSkillProfile);

		SkillDocumentForm skillDocumentForm = new SkillDocumentForm();
		skillDocumentForm.setVerified(true);

		accountSkillProfileService.update(accountSkill,  profile, skillDocumentForm);

		assertNotNull(accountSkillProfile.getStartDate());
		verify(accountSkillProfileDAO).save(accountSkillProfile);
	}
}
