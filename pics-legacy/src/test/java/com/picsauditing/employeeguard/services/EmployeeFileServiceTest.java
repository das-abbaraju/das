package com.picsauditing.employeeguard.services;

import com.picsauditing.PICS.Utilities;
import com.picsauditing.employeeguard.entities.ProfileDocument;
import com.picsauditing.employeeguard.models.MFileManager;
import com.picsauditing.employeeguard.services.entity.DocumentEntityService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.*;

import static com.picsauditing.employeeguard.EGTestDataUtil.PROFILE_DOCUMENT_1;
import static com.picsauditing.employeeguard.EGTestDataUtil.PROFILE_DOCUMENT_2;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;

public class EmployeeFileServiceTest {

	// Class under test
	private EmployeeFileService employeeFileService;

	@Mock
	private DocumentEntityService documentEntityService;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		employeeFileService = new EmployeeFileService();

		Whitebox.setInternalState(employeeFileService, "documentEntityService", documentEntityService);
	}

	@Test
	public void testFindEmployeeFiles() throws Exception {
		setupTestFindEmployeeFiles();

		Set<MFileManager.MFile> results = employeeFileService.findEmployeeFiles(123);

		verifyTestFindEmployeeFiles(results);
	}

	private void setupTestFindEmployeeFiles() {
		List<ProfileDocument> fakeProfileDocuments = Arrays.asList(PROFILE_DOCUMENT_1, PROFILE_DOCUMENT_2);
		when(documentEntityService.findDocumentsForAppUser(anyInt())).thenReturn(fakeProfileDocuments);
	}

	private void verifyTestFindEmployeeFiles(Set<MFileManager.MFile> results) {
		assertEquals(2, results.size());

		assertTrue(Utilities.collectionsAreEqual(results, new HashSet<>(Arrays.asList(
				new MFileManager.MFile(PROFILE_DOCUMENT_1)
						.copyId().copyName()
						.copyCreatedDate()
						.copyExpirationDate(),

				new MFileManager.MFile(PROFILE_DOCUMENT_2)
						.copyId()
						.copyName()
						.copyCreatedDate()
						.copyExpirationDate())),

				new Comparator<MFileManager.MFile>() {

					@Override
					public int compare(MFileManager.MFile o1, MFileManager.MFile o2) {
						return Integer.compare(o1.getId(), o2.getId());
					}
				}));
	}
}
