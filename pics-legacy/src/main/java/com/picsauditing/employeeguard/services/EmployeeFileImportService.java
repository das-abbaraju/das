package com.picsauditing.employeeguard.services;

import au.com.bytecode.opencsv.CSVReader;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.service.importfile.AbstractFileImportService;
import com.picsauditing.service.importfile.InvalidFileFormatException;

import java.io.File;
import java.io.FileReader;

public class EmployeeFileImportService extends AbstractFileImportService<Employee> {

	private static final int FIRST_NAME_INDEX = 0;
	private static final int LAST_NAME_INDEX = 1;
	private static final int POSITION_NAME_INDEX = 2;
	private static final int EMAIL_INDEX = 3;
	private static final int PHONE_INDEX = 4;
	private static final int EMPLOYEE_ID_INDEX = 5;

	@Override
	protected String buildErrorMessage(final String[] lineOfFile, final int rowCounter) {
		return "Row number " + rowCounter + " has errors";
	}

	@Override
	protected Employee buildEntity(final String[] lineOfFile, final int rowCounter) {
		Employee employee = new Employee();

		employee.setFirstName(lineOfFile[FIRST_NAME_INDEX]);
		employee.setLastName(lineOfFile[LAST_NAME_INDEX]);
		employee.setPositionName(lineOfFile[POSITION_NAME_INDEX]);
		employee.setEmail(lineOfFile[EMAIL_INDEX]);
		employee.setPhone(lineOfFile[PHONE_INDEX]);
		employee.setSlug(lineOfFile[EMPLOYEE_ID_INDEX]);

		return employee;
	}

	@Override
	protected boolean lineIsValid(final String[] lineOfFile) {
		// TODO Call validator here
		return !"First Name".equals(lineOfFile[0]);
	}

	@Override
	protected CSVReader getCsvReader(final File file) throws Exception {
		return new CSVReader(new FileReader(file));
	}

	@Override
	protected void validateFile(File file) throws InvalidFileFormatException {
	}
}
