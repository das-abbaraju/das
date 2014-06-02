package com.picsauditing.employeeguard.services;

import com.picsauditing.employeeguard.daos.AccountSkillProfileDAO;
import com.picsauditing.employeeguard.daos.ProfileDocumentDAO;
import com.picsauditing.employeeguard.entities.ProfileDocument;
import com.picsauditing.employeeguard.entities.builders.ProfileDocumentBuilder;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ProfileDocumentServiceTest {

	public static final int DOCUMENT_ID = 1;

	private ProfileDocumentService profileDocumentService;

	@Mock
	private AccountSkillProfileDAO accountSkillProfileDAO;
	@Mock
	private ProfileDocumentDAO profileDocumentDAO;

	@Before
	public void setup() {
		profileDocumentService = new ProfileDocumentService();

		MockitoAnnotations.initMocks(this);

		Whitebox.setInternalState(profileDocumentService, "accountSkillProfileDAO", accountSkillProfileDAO);
		Whitebox.setInternalState(profileDocumentService, "profileDocumentDAO", profileDocumentDAO);
	}

	@Test
	public void testDelete_DocumentNotLinkedToAccountSkillEmployee() {
		ProfileDocument fakeProfileDocument = buildFakeProfileDocument();
		when(profileDocumentDAO.find(DOCUMENT_ID)).thenReturn(fakeProfileDocument);

		profileDocumentService.delete(DOCUMENT_ID);

		verify(profileDocumentDAO).delete(fakeProfileDocument);
	}

	private ProfileDocument buildFakeProfileDocument() {
		return new ProfileDocumentBuilder()
				.id(DOCUMENT_ID)
				.build();
	}
}
