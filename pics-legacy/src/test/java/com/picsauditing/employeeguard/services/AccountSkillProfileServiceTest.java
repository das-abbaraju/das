package com.picsauditing.employeeguard.services;

import com.picsauditing.employeeguard.daos.AccountSkillProfileDAO;
import com.picsauditing.employeeguard.entities.AccountSkillProfile;
import com.picsauditing.employeeguard.entities.IntervalType;
import com.picsauditing.employeeguard.entities.SkillType;
import com.picsauditing.employeeguard.entities.builders.AccountSkillBuilder;
import com.picsauditing.employeeguard.forms.employee.SkillDocumentForm;
import com.picsauditing.employeeguard.services.factory.ProfileDocumentServiceFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.verify;

public class AccountSkillProfileServiceTest {

	private AccountSkillProfileService accountSkillProfileService;

	private ProfileDocumentService profileDocumentService;
	private static final int ACCOUNT_ID = 1100;

	@Mock
	private AccountSkillProfileDAO accountSkillProfileDAO;

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
		AccountSkillProfile accountSkillProfile = new AccountSkillProfile();
		accountSkillProfile.setSkill(new AccountSkillBuilder(ACCOUNT_ID).skillType(SkillType.Certification).build());

		SkillDocumentForm skillDocumentForm = new SkillDocumentForm();
		skillDocumentForm.setDocumentId(1);

		accountSkillProfileService.update(accountSkillProfile, skillDocumentForm);

		verify(profileDocumentService).getDocument(anyInt());
		assertNull(accountSkillProfile.getEndDate());
	}

	@Test
	public void testUpdate_Training_Unverified() throws Exception {
		AccountSkillProfile accountSkillProfile = new AccountSkillProfile();
		accountSkillProfile.setSkill(new AccountSkillBuilder(ACCOUNT_ID).skillType(SkillType.Training).intervalPeriod(3).intervalType(IntervalType.DAY).build());

		SkillDocumentForm skillDocumentForm = new SkillDocumentForm();

		accountSkillProfileService.update(accountSkillProfile, skillDocumentForm);

		assertNull(accountSkillProfile.getEndDate());
		verify(accountSkillProfileDAO).save(accountSkillProfile);
	}

	@Test
	public void testUpdate_Training_Verified() throws Exception {
		AccountSkillProfile accountSkillProfile = new AccountSkillProfile();
		accountSkillProfile.setSkill(new AccountSkillBuilder(ACCOUNT_ID).skillType(SkillType.Training).intervalPeriod(3).intervalType(IntervalType.DAY).build());

		SkillDocumentForm skillDocumentForm = new SkillDocumentForm();
		skillDocumentForm.setVerified(true);

		accountSkillProfileService.update(accountSkillProfile, skillDocumentForm);

		assertNull(accountSkillProfile.getEndDate());
		verify(accountSkillProfileDAO).save(accountSkillProfile);
	}
}
