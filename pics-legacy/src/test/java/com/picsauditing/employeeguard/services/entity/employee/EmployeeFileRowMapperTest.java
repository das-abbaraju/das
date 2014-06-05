package com.picsauditing.employeeguard.services.entity.employee;

import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.services.entity.util.file.FileRowMapper;
import com.picsauditing.util.Strings;
import org.junit.Test;

import static org.junit.Assert.*;

public class EmployeeFileRowMapperTest {

	public static final int CONTRACTOR_ID = 901;

	public static final String EMPLOYEE_FIRST_NAME = "Bob";
	public static final String EMPLOYEE_LAST_NAME = "The Builder";
	public static final String EMPLOYEE_EMAIL = "test@testing.com";

	@Test
	public void testIsHeader_RowIsNotHeader() {
		FileRowMapper<Employee> fileRowMapper = new EmployeeFileRowMapper(CONTRACTOR_ID);

		boolean result = fileRowMapper.isHeader(new String[]{"test", "1", "2", "3"});

		assertFalse(result);
	}

	@Test
	public void testIsHeader_RowIsHeader() {
		FileRowMapper<Employee> fileRowMapper = new EmployeeFileRowMapper(CONTRACTOR_ID);

		boolean result = fileRowMapper.isHeader(EmployeeImportTemplate.IMPORT_FILE_HEADER);

		assertTrue(result);
	}

	@Test
	public void testIsValid() {
		FileRowMapper<Employee> fileRowMapper = new EmployeeFileRowMapper(CONTRACTOR_ID);

		boolean result = fileRowMapper.isValid(new String[]{EMPLOYEE_FIRST_NAME, EMPLOYEE_LAST_NAME,
				Strings.EMPTY_STRING, EMPLOYEE_EMAIL});

		assertTrue(result);
	}

	@Test
	public void testIsEmptyRow_NullStringArray() {
		FileRowMapper<Employee> fileRowMapper = new EmployeeFileRowMapper(CONTRACTOR_ID);

		boolean result = fileRowMapper.isEmptyRow(null);

		assertTrue(result);
	}

	@Test
	public void testIsEmptyRow_EmptyStringArray() {
		FileRowMapper<Employee> fileRowMapper = new EmployeeFileRowMapper(CONTRACTOR_ID);

		boolean result = fileRowMapper.isEmptyRow(new String[]{});

		assertTrue(result);
	}

	@Test
	public void testIsEmptyRow_StringArrayWithEmptyStrings() {
		FileRowMapper<Employee> fileRowMapper = new EmployeeFileRowMapper(CONTRACTOR_ID);

		boolean result = fileRowMapper.isEmptyRow(new String[]{Strings.EMPTY_STRING, Strings.EMPTY_STRING,
				Strings.EMPTY_STRING, " "});

		assertTrue(result);
	}

	@Test
	public void testIsEmptyRow_RowIsNotEmpty() {
		FileRowMapper<Employee> fileRowMapper = new EmployeeFileRowMapper(CONTRACTOR_ID);

		boolean result = fileRowMapper.isEmptyRow(new String[]{EMPLOYEE_FIRST_NAME, EMPLOYEE_LAST_NAME,
				Strings.EMPTY_STRING, EMPLOYEE_EMAIL});

		assertFalse(result);
	}

	@Test
	public void testMapToEntity() {
		FileRowMapper<Employee> fileRowMapper = new EmployeeFileRowMapper(CONTRACTOR_ID);

		Employee result = fileRowMapper.mapToEntity(new String[]{EMPLOYEE_FIRST_NAME, EMPLOYEE_LAST_NAME,
				Strings.EMPTY_STRING, EMPLOYEE_EMAIL});

		verifyTestMapToEntity(result);
	}

	private void verifyTestMapToEntity(Employee result) {
		assertEquals(EMPLOYEE_FIRST_NAME, result.getFirstName());
		assertEquals(EMPLOYEE_LAST_NAME, result.getLastName());
		assertEquals(EMPLOYEE_EMAIL, result.getEmail());
	}

}
