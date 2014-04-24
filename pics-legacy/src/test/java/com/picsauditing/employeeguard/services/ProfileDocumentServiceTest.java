package com.picsauditing.employeeguard.services;

import com.picsauditing.employeeguard.daos.AccountSkillEmployeeDAO;
import com.picsauditing.employeeguard.daos.ProfileDocumentDAO;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import static org.mockito.Mockito.verify;

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
		profileDocumentService.delete(DOCUMENT_ID, PROFILE_ID);

		verify(profileDocumentDAO).delete(DOCUMENT_ID, PROFILE_ID);
	}
}
