package com.picsauditing.employeeguard.services;

import com.picsauditing.employeeguard.daos.AccountSkillEmployeeDAO;
import com.picsauditing.employeeguard.daos.ProfileDocumentDAO;
import com.picsauditing.employeeguard.entities.AccountSkillEmployee;
import com.picsauditing.employeeguard.entities.ProfileDocument;
import com.picsauditing.employeeguard.entities.builders.AccountSkillEmployeeBuilder;
import com.picsauditing.employeeguard.entities.builders.ProfileDocumentBuilder;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ProfileDocumentServiceTest {

	public static final int DOCUMENT_ID = 1;
	public static final int PROFILE_ID = 2;
	public static final int APP_USER_ID = 3;

	private ProfileDocumentService profileDocumentService;

	@Mock
	private AccountSkillEmployeeDAO accountSkillEmployeeDAO;
	@Mock
	private ProfileDocumentDAO profileDocumentDAO;

	@Before
	public void setup() {
		profileDocumentService = new ProfileDocumentService();

		MockitoAnnotations.initMocks(this);

		Whitebox.setInternalState(profileDocumentService, "accountSkillEmployeeDAO", accountSkillEmployeeDAO);
		Whitebox.setInternalState(profileDocumentService, "profileDocumentDAO", profileDocumentDAO);
	}

	@Test
	public void testDelete_DocumentNotLinkedToAccountSkillEmployee() {
		ProfileDocument profileDocument = new ProfileDocument();
		when(profileDocumentDAO.findByDocumentIdAndProfileId(1, 2)).thenReturn(profileDocument);

		profileDocumentService.delete(DOCUMENT_ID, PROFILE_ID);

		verify(profileDocumentDAO).delete(profileDocument);
	}

	@Test
	public void testDelete() {
		ProfileDocument profileDocument = buildProfileDocumentWithAccountSkillEmployees();
		when(profileDocumentDAO.findByDocumentIdAndProfileId(DOCUMENT_ID, PROFILE_ID)).thenReturn(profileDocument);

		profileDocumentService.delete(DOCUMENT_ID, PROFILE_ID);

		verifyDeletedProfileDocumentAndAccountSkillEmployees(profileDocument);
	}

	private ProfileDocument buildProfileDocumentWithAccountSkillEmployees() {
		return new ProfileDocumentBuilder().employeeSkills(buildAccountSkillEmployees()).build();
	}

	private List<AccountSkillEmployee> buildAccountSkillEmployees() {
		List<AccountSkillEmployee> accountSkillEmployees = new ArrayList<>();
		for (int index = 0; index < 3; index++) {
			accountSkillEmployees.add(new AccountSkillEmployeeBuilder().profileDocument(new ProfileDocument())
					.startDate(new Date()).endDate(new Date()).build());
		}

		return accountSkillEmployees;
	}

	private void verifyDeletedProfileDocumentAndAccountSkillEmployees(ProfileDocument profileDocument) {
		verify(profileDocumentDAO).delete(profileDocument);

		for (AccountSkillEmployee accountSkillEmployee : profileDocument.getEmployeeSkills()) {
			assertNull(accountSkillEmployee.getProfileDocument());
			assertNotNull(accountSkillEmployee.getStartDate());
			assertNull(accountSkillEmployee.getEndDate());
		}

		verify(accountSkillEmployeeDAO).save(profileDocument.getEmployeeSkills());
	}
}
