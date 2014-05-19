package com.picsauditing.employeeguard.services;

import com.picsauditing.employeeguard.daos.AccountSkillEmployeeDAO;
import com.picsauditing.employeeguard.entities.AccountSkillEmployee;
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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;

public class AccountSkillEmployeeServiceTest {
	private AccountSkillEmployeeService accountSkillEmployeeService;

	private ProfileDocumentService profileDocumentService;
  private static final int ACCOUNT_ID = 1100;

	@Mock
	private AccountSkillEmployeeDAO accountSkillEmployeeDAO;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		accountSkillEmployeeService = new AccountSkillEmployeeService();
		profileDocumentService = ProfileDocumentServiceFactory.getProfileDocumentService();

		Whitebox.setInternalState(accountSkillEmployeeService, "accountSkillEmployeeDAO", accountSkillEmployeeDAO);
		Whitebox.setInternalState(accountSkillEmployeeService, "profileDocumentService", profileDocumentService);
	}

	@Test
	public void testUpdate_Certification() throws Exception {
		AccountSkillEmployee accountSkillEmployee = new AccountSkillEmployee();
		accountSkillEmployee.setSkill(new AccountSkillBuilder(ACCOUNT_ID).skillType(SkillType.Certification).build());

		SkillDocumentForm skillDocumentForm = new SkillDocumentForm();
		skillDocumentForm.setDocumentId(1);

		accountSkillEmployeeService.update(accountSkillEmployee, skillDocumentForm);

		verify(profileDocumentService).getDocument(anyInt());
		assertNull(accountSkillEmployee.getEndDate());
	}

	@Test
	public void testUpdate_Training_Unverified() throws Exception {
		AccountSkillEmployee accountSkillEmployee = new AccountSkillEmployee();
		accountSkillEmployee.setSkill(new AccountSkillBuilder(ACCOUNT_ID).skillType(SkillType.Training).intervalPeriod(3).intervalType(IntervalType.DAY).build());

		SkillDocumentForm skillDocumentForm = new SkillDocumentForm();

		accountSkillEmployeeService.update(accountSkillEmployee, skillDocumentForm);

		assertNull(accountSkillEmployee.getEndDate());
		verify(accountSkillEmployeeDAO).save(accountSkillEmployee);
	}

	@Test
	public void testUpdate_Training_Verified() throws Exception {
		AccountSkillEmployee accountSkillEmployee = new AccountSkillEmployee();
		accountSkillEmployee.setSkill(new AccountSkillBuilder(ACCOUNT_ID).skillType(SkillType.Training).intervalPeriod(3).intervalType(IntervalType.DAY).build());

		SkillDocumentForm skillDocumentForm = new SkillDocumentForm();
		skillDocumentForm.setVerified(true);

		accountSkillEmployeeService.update(accountSkillEmployee, skillDocumentForm);

		assertNull(accountSkillEmployee.getEndDate());
		verify(accountSkillEmployeeDAO).save(accountSkillEmployee);
	}
}
