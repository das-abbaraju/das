package com.picsauditing.employeeguard.services.entity.employee;

import au.com.bytecode.opencsv.CSVWriter;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.msgbundle.EGI18n;
import com.picsauditing.employeeguard.services.entity.util.file.CsvFileImportReader;
import com.picsauditing.employeeguard.services.entity.util.file.FileImportCommand;
import com.picsauditing.employeeguard.services.entity.util.file.FileImportService;
import com.picsauditing.employeeguard.services.entity.util.file.UploadResult;
import com.picsauditing.util.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

public class EmployeeImportExportProcess {

	@Autowired
	private FileImportService<Employee> fileImportService;

	public UploadResult<Employee> importEmployees(final int contractorId,
												  final File file,
												  final String uploadFileName) {
		FileImportCommand<Employee> fileImportCommand = new FileImportCommand.Builder<Employee>()
				.file(file)
				.filename(uploadFileName)
				.fileImportReader(new CsvFileImportReader())
				.fileRowMapper(new EmployeeFileRowMapper(contractorId))
				.build();

		return fileImportService.importFile(fileImportCommand);
	}

	public byte[] exportEmployees(final Collection<Employee> employees) throws IOException {
		ByteArrayOutputStream byteArrayOutputStream = null;
		CSVWriter csvWriter = null;

		try {
			byteArrayOutputStream = new ByteArrayOutputStream();
			csvWriter = new CSVWriter(new PrintWriter(byteArrayOutputStream));

			addHeader(csvWriter);

			writeEmployeesToFile(employees, csvWriter);

			csvWriter.flush();
			return byteArrayOutputStream.toByteArray();
		} finally {
			FileUtils.safeClose(byteArrayOutputStream);
			FileUtils.safeClose(csvWriter);
		}
	}

	private void addHeader(final CSVWriter csvWriter) {
		csvWriter.writeNext(new String[]{
				EGI18n.getTextFromResourceBundle("EMPLOYEE.IMPORT_EXPORT.TEMPLATE.HEADER.FIRST_NAME"),
				EGI18n.getTextFromResourceBundle("EMPLOYEE.IMPORT_EXPORT.TEMPLATE.HEADER.LAST_NAME"),
				EGI18n.getTextFromResourceBundle("EMPLOYEE.IMPORT_EXPORT.TEMPLATE.HEADER.TITLE"),
				EGI18n.getTextFromResourceBundle("EMPLOYEE.IMPORT_EXPORT.TEMPLATE.HEADER.EMAIL"),
				EGI18n.getTextFromResourceBundle("EMPLOYEE.IMPORT_EXPORT.TEMPLATE.HEADER.PHONE"),
				EGI18n.getTextFromResourceBundle("EMPLOYEE.IMPORT_EXPORT.TEMPLATE.HEADER.UNIQUE_ID")
		});
	}

	private void writeEmployeesToFile(final Collection<Employee> employees, final CSVWriter csvWriter) {
		for (Employee employee : employees) {
			csvWriter.writeNext(new String[]{
					employee.getFirstName(),
					employee.getLastName(),
					employee.getPositionName(),
					employee.getEmail(),
					employee.getPhone(),
					employee.getSlug()
			});
		}
	}
}
