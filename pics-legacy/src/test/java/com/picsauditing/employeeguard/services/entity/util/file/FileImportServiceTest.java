package com.picsauditing.employeeguard.services.entity.util.file;

import com.picsauditing.PICS.Utilities;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.util.Strings;
import com.spun.util.ClassUtils;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class FileImportServiceTest {

	public static final String EMPLOYEE_IMPORT_FILE = "fakePersonImport.csv";

	@Test
	public void testImportFile_InvalidFileType() {
		FileImportCommand<FakePerson> fileImportCommand = new FileImportCommand.Builder<FakePerson>()
				.fileImportReader(new CsvFileImportReader())
				.filename("test.exe")
				.file(getFile(EMPLOYEE_IMPORT_FILE))
				.build();

		UploadResult<Employee> result = new FileImportService<FakePerson>().importFile(fileImportCommand);

		assertTrue(result.isUploadError());
		assertEquals("Invalid file", result.getErrorMessage());
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
