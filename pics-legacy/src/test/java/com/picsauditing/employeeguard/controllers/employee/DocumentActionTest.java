package com.picsauditing.employeeguard.controllers.employee;

import com.picsauditing.PicsActionTest;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.database.domain.Identifiable;
import com.picsauditing.employeeguard.entities.Profile;
import com.picsauditing.employeeguard.entities.ProfileDocument;
import com.picsauditing.employeeguard.forms.SearchForm;
import com.picsauditing.employeeguard.forms.contractor.DocumentForm;
import com.picsauditing.employeeguard.forms.factory.FormBuilderFactory;
import com.picsauditing.employeeguard.forms.factory.ProfileDocumentInfoBuilder;
import com.picsauditing.employeeguard.services.ProfileDocumentService;
import com.picsauditing.employeeguard.services.ProfileService;
import com.picsauditing.employeeguard.services.factory.ProfileDocumentServiceFactory;
import com.picsauditing.employeeguard.services.factory.ProfileServiceFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DocumentActionTest extends PicsActionTest {
	private static final String TEST = "Test";
	public static final String ID = "ID";

	private DocumentAction documentAction;

	private ProfileService profileService;
	private ProfileDocumentService profileDocumentService;

    @Mock
    private FormBuilderFactory formBuilderFactory;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		documentAction = new DocumentAction();
		profileService = ProfileServiceFactory.getProfileService();
		profileDocumentService = ProfileDocumentServiceFactory.getProfileDocumentService();

		super.setUp(documentAction);

		when(permissions.getAppUserID()).thenReturn(Identifiable.SYSTEM);

        Whitebox.setInternalState(documentAction, "formBuilderFactory", formBuilderFactory);
		Whitebox.setInternalState(documentAction, "profileService", profileService);
		Whitebox.setInternalState(documentAction, "profileDocumentService", profileDocumentService);

        when(formBuilderFactory.getProfileDocumentInfoBuilder()).thenReturn(new ProfileDocumentInfoBuilder());
	}

	@Test
	public void testIndex() throws Exception {
		String result = documentAction.index();
		assertEquals(PicsRestActionSupport.LIST, result);
		assertFalse(documentAction.getDocuments().isEmpty());
		verify(profileDocumentService).getDocumentsForProfile(Identifiable.SYSTEM);
	}

	@Test
	public void testIndex_Search() throws Exception {
		SearchForm searchForm = new SearchForm();
		searchForm.setSearchTerm(TEST);
		documentAction.setSearchForm(searchForm);

		String result = documentAction.index();
		assertEquals(PicsRestActionSupport.LIST, result);
		assertFalse(documentAction.getDocuments().isEmpty());
		verify(profileDocumentService).search(TEST, Identifiable.SYSTEM);
	}

	@Test
	public void testShow() throws Exception {
		documentAction.setId(ID);
		assertEquals(PicsRestActionSupport.SHOW, documentAction.show());
		assertNotNull(documentAction.getDocument());
		verify(profileDocumentService).getDocument(ID);
	}

	@Test
	public void testCreate() throws Exception {
		assertEquals(PicsRestActionSupport.CREATE, documentAction.create());
	}

	@Test
	public void testEditFileSection() throws Exception {
		documentAction.setDocumentForm(new DocumentForm());

		assertEquals("edit-form", documentAction.editFileSection());
		assertNotNull(documentAction.getDocument());
		assertNotNull(documentAction.getDocumentForm());
	}

	@Test
	public void testEditFileSection_DocumentFormIsNull() throws Exception {
		documentAction.setId(ID);
		assertEquals("edit-form", documentAction.editFileSection());
		assertNotNull(documentAction.getDocument());
		assertNotNull(documentAction.getDocumentForm());
		verify(profileDocumentService).getDocument(ID);
	}

	@Test
	public void testDownload() throws Exception {
		documentAction.setId(ID);
		assertEquals(PicsActionSupport.FILE_DOWNLOAD, documentAction.download());
		verify(profileDocumentService).getDocument(ID);
		verify(profileDocumentService).getDocumentFile(any(ProfileDocument.class), anyString());
	}

	@Test
	public void testInsert() throws Exception {
		assertEquals(PicsActionSupport.REDIRECT, documentAction.insert());
		assertTrue(documentAction.getUrl().startsWith("/employee-guard/employee/file/"));
		verify(profileService).findByAppUserId(Identifiable.SYSTEM);
		verify(profileDocumentService).create(any(Profile.class), any(DocumentForm.class), anyString(), eq(Identifiable.SYSTEM));
	}

//	@Test
//	public void testUpdate() throws Exception {
//		documentAction.setDocumentForm(new DocumentForm());
//		assertEquals(PicsActionSupport.REDIRECT, documentAction.update());
//		assertTrue(documentAction.getUrl().startsWith("/employee-guard/employee/file/"));
//		verify(profileService).findByAppUserId(Identifiable.SYSTEM);
//		verify(profileDocumentService).update(anyString(), eq(Identifiable.SYSTEM), any(ProfileDocument.class), eq(Identifiable.SYSTEM));
//	}

	@Test
	public void testDelete() throws Exception {
		documentAction.setId(ID);
		assertEquals(PicsActionSupport.REDIRECT, documentAction.delete());
		assertEquals("/employee-guard/employee/file", documentAction.getUrl());
		verify(profileService).findByAppUserId(Identifiable.SYSTEM);
		verify(profileDocumentService).delete(ID, Identifiable.SYSTEM, Identifiable.SYSTEM);
	}
}
