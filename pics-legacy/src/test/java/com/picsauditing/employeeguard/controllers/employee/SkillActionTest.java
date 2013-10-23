package com.picsauditing.employeeguard.controllers.employee;

import com.picsauditing.PicsActionTest;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.database.domain.Identifiable;
import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.AccountSkillEmployee;
import com.picsauditing.employeeguard.entities.Profile;
import com.picsauditing.employeeguard.forms.employee.SkillDocumentForm;
import com.picsauditing.employeeguard.forms.factory.CompanySkillsFormBuilder;
import com.picsauditing.employeeguard.forms.factory.FormBuilderFactory;
import com.picsauditing.employeeguard.services.*;
import com.picsauditing.jpa.entities.Account;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SkillActionTest extends PicsActionTest {
	public static final String ID = "ID";
	private SkillAction skillAction;

	private AccountSkillEmployeeService accountSkillEmployeeService;
	private CompanySkillsFormBuilder companySkillsFormBuilder;
	private FormBuilderFactory formBuilderFactory;
	private ProfileService profileService;
	private ProfileDocumentService profileDocumentService;
	private SkillService skillService;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		skillAction = new SkillAction();
		accountSkillEmployeeService = AccountSkillEmployeeServiceFactory.getAccountSkillEmployeeService();
		companySkillsFormBuilder = CompanySkillsFormBuilderFactory.getCompanySkillsFormBuilder();
		formBuilderFactory = new FormBuilderFactory();
		profileService = ProfileServiceFactory.getProfileService();
		profileDocumentService = ProfileDocumentServiceFactory.getProfileDocumentService();
		skillService = SkillServiceFactory.getSkillService();

		super.setUp(skillAction);

		Whitebox.setInternalState(skillAction, "accountSkillEmployeeService", accountSkillEmployeeService);
		Whitebox.setInternalState(skillAction, "formBuilderFactory", formBuilderFactory);
		Whitebox.setInternalState(skillAction, "profileService", profileService);
		Whitebox.setInternalState(skillAction, "profileDocumentService", profileDocumentService);
		Whitebox.setInternalState(skillAction, "skillService", skillService);
		Whitebox.setInternalState(formBuilderFactory, "companySkillsFormBuilder", companySkillsFormBuilder);

		when(permissions.getAccountId()).thenReturn(Account.PicsID);
		when(permissions.getAppUserID()).thenReturn(Identifiable.SYSTEM);
	}

	@Test
	public void testIndex() throws Exception {
		assertEquals(PicsRestActionSupport.LIST, skillAction.index());
		assertNotNull(skillAction.getCompanySkillInfoList());
	}

	@Test
	public void testShow() throws Exception {
		skillAction.setId(ID);
		assertEquals(PicsRestActionSupport.SHOW, skillAction.show());
		assertNotNull(skillAction.getSkillDocumentForm());
		verify(profileService).findByAppUserId(Identifiable.SYSTEM);
		verify(skillService).getSkill(ID);
		verify(accountSkillEmployeeService).getAccountSkillEmployeeForProfileAndSkill(any(Profile.class), any(AccountSkill.class));
	}

	@Test
	public void testEdit() throws Exception {
		skillAction.setId(ID);
		assertEquals("edit-form", skillAction.edit());
		assertNotNull(skillAction.getSkillDocumentForm());
		verify(profileService).findByAppUserId(Identifiable.SYSTEM);
		verify(skillService).getSkill(ID);
		verify(accountSkillEmployeeService).getAccountSkillEmployeeForProfileAndSkill(any(Profile.class), any(AccountSkill.class));
	}

	@Test
	public void testCertification() throws Exception {
		assertEquals("certification", skillAction.certification());
		assertNotNull(skillAction.getDocuments());
		verify(profileDocumentService).getDocumentsForProfile(Identifiable.SYSTEM);
	}

	@Test
	public void testManage() throws Exception {
		skillAction.setId(ID);
		assertEquals("manage", skillAction.manage());
		assertNotNull(skillAction.getDocuments());
		verify(profileService).findByAppUserId(Identifiable.SYSTEM);
		verify(profileDocumentService).getDocumentsForProfile(Identifiable.SYSTEM);
	}

	@Test
	public void testManage_NotCertification() throws Exception {
		skillAction.setId("ID2");
		assertEquals("manage", skillAction.manage());
	}

	@Test
	public void testUpdate() throws Exception {
		skillAction.setId(ID);
		SkillDocumentForm skillDocumentForm = new SkillDocumentForm();
		skillAction.setSkillDocumentForm(skillDocumentForm);
		assertEquals(PicsActionSupport.REDIRECT, skillAction.update());
		verify(profileService).findByAppUserId(Identifiable.SYSTEM);
		verify(skillService).getSkill(ID);
		verify(accountSkillEmployeeService).update(any(AccountSkillEmployee.class), eq(skillDocumentForm));
	}

	@Test
	public void testUpdate_NonCertificate() throws Exception {
		skillAction.setId("ID2");
		assertEquals(PicsActionSupport.REDIRECT, skillAction.update());
		verify(profileService).findByAppUserId(Identifiable.SYSTEM);
		verify(skillService).getSkill("ID2");
		verify(accountSkillEmployeeService).update(any(AccountSkillEmployee.class), any(SkillDocumentForm.class));
	}
}
