package com.picsauditing.employeeguard.services.entity.util.file;

import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.util.Strings;
import com.spun.util.ClassUtils;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class FileImportServiceTest {


	public static final String EMPLOYEE_IMPORT_FILE = "employeeImport.csv";

	@Test
	public void testImportFile_InvalidFileType() {
		FileImportCommand<Employee> fileImportCommand = new FileImportCommand.Builder<Employee>()
				.fileImportReader(new CsvFileImportReader())
				.filename("test.exe")
				.file(getFile(EMPLOYEE_IMPORT_FILE))
				.build();

		UploadResult<Employee> result = new FileImportService<Employee>().importFile(fileImportCommand);

		assertTrue(result.isUploadError());
		assertEquals("Invalid file", result.getErrorMessage());
	}

	@Test
	public void testImportFile_Successful() {
		File file = getFile(EMPLOYEE_IMPORT_FILE);
		FileImportCommand<Employee> fileImportCommand = new FileImportCommand.Builder<Employee>()
				.fileImportReader(new CsvFileImportReader())
				.filename(file.getName())
				.file(file)
				.build();

		UploadResult<Employee> result = new FileImportService<Employee>().importFile(fileImportCommand);

		assertFalse(result.isUploadError());
	}

	private File getFile(final String fileName) {
		return new File(ClassUtils.getSourceDirectory(FileImportServiceTest.class)
				.getAbsolutePath() + Strings.FILE_SEPARATOR + fileName);
	}

}
