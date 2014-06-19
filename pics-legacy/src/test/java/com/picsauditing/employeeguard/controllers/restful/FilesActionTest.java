package com.picsauditing.employeeguard.controllers.restful;

import com.picsauditing.PicsActionTest;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.employeeguard.models.MFileManager;
import com.picsauditing.employeeguard.services.EmployeeFileService;
import org.approvaltests.Approvals;
import org.approvaltests.reporters.DiffReporter;
import org.approvaltests.reporters.UseReporter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.picsauditing.employeeguard.EGTestDataUtil.PROFILE_DOCUMENT_1;
import static com.picsauditing.employeeguard.EGTestDataUtil.PROFILE_DOCUMENT_2;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;

@UseReporter(DiffReporter.class)
public class FilesActionTest extends PicsActionTest {

	@Mock
	private EmployeeFileService employeeFileService;

	// Class under test
	private FilesAction filesAction;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		filesAction = new FilesAction();

		super.setUp(filesAction);

		Whitebox.setInternalState(filesAction, "employeeFileService", employeeFileService);
	}

	@Test
	public void testIndex() throws Exception {
		setupTestIndex();

		String result = filesAction.index();

		verifyTestIndex(result);
	}

	private void setupTestIndex() {
		Set<MFileManager.MFile> fakeMFiles = new HashSet<>(Arrays.asList(
				new MFileManager.MFile(PROFILE_DOCUMENT_1).copyId().copyName().copyCreatedDate().copyExpirationDate(),
				new MFileManager.MFile(PROFILE_DOCUMENT_2).copyId().copyName().copyCreatedDate().copyExpirationDate()));
		when(employeeFileService.findEmployeeFiles(anyInt())).thenReturn(fakeMFiles);
	}

	private void verifyTestIndex(String result) throws Exception {
		assertEquals(PicsActionSupport.JSON_STRING, result);
		Approvals.verify(filesAction.getJsonString());
	}
}
