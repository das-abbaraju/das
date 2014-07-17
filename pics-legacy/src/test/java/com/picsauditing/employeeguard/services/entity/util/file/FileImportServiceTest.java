package com.picsauditing.employeeguard.services.entity.util.file;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.util.ValueStack;
import com.picsauditing.PICS.Utilities;
import com.picsauditing.employeeguard.ResourceBundleMocking;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.util.Strings;
import com.spun.util.ClassUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class FileImportServiceTest {

	public static final String EMPLOYEE_IMPORT_FILE = "fakePersonImport.csv";

	@Mock
	private ThreadLocal<ActionContext> threadLocalActionContext;
	@Mock
	private ActionContext actionContext;
	@Mock
	private ActionInvocation actionInvocation;
	@Mock
	private ActionSupport actionSupport;
	@Mock
	private ValueStack valueStack;

	private ResourceBundleMocking resourceBundleMocking;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		resourceBundleMocking = new ResourceBundleMocking();
		resourceBundleMocking.setUp();
	}

	@After
	public void tearDown() {
		resourceBundleMocking.tearDown();
	}

	@Test
	public void testImportFile_InvalidFileType() {
		FileImportCommand<FakePerson> fileImportCommand = new FileImportCommand.Builder<FakePerson>()
				.fileImportReader(new CsvFileImportReader())
				.filename("test.exe")
				.file(getFile(EMPLOYEE_IMPORT_FILE))
				.build();

		UploadResult<Employee> result = new FileImportService<FakePerson>().importFile(fileImportCommand);

		assertTrue(result.isUploadError());
		assertEquals(ResourceBundleMocking.DEFAULT_RESOURCE_BUNDLE_STRING, result.getErrorMessage());
	}

	@Test
	public void testImportFile_Successful() {
		FileImportCommand<FakePerson> fileImportCommand = setupTestImportFile_Successful();

		UploadResult<FakePerson> result = new FileImportService<FakePerson>().importFile(fileImportCommand);

		verifyTestImportFile_Successful(result);
	}

	private FileImportCommand<FakePerson> setupTestImportFile_Successful() {
		File file = getFile(EMPLOYEE_IMPORT_FILE);

		return new FileImportCommand.Builder<FakePerson>()
				.fileImportReader(new CsvFileImportReader())
				.filename(file.getName())
				.file(file)
				.fileRowMapper(new FakeFileRowMapper())
				.build();
	}

	private void verifyTestImportFile_Successful(final UploadResult<FakePerson> result) {
		assertFalse(result.isUploadError());

		assertTrue(Utilities.collectionsAreEqual(new ArrayList<FakePerson>() {{

			add(new FakePerson("employee", "1", 10));
			add(new FakePerson("employee", "2", 11));
			add(new FakePerson("employee", "3", 12));
			add(new FakePerson("employee", "4", 13));

		}}
				, result.getImportedEntities()));
	}

	private File getFile(final String fileName) {
		return new File(ClassUtils.getSourceDirectory(FileImportServiceTest.class)
				.getAbsolutePath() + Strings.FILE_SEPARATOR + fileName);
	}
}
