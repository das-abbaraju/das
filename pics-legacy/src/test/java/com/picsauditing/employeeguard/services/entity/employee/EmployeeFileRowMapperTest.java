package com.picsauditing.employeeguard.services.entity.employee;

import com.picsauditing.employeeguard.ResourceBundleMocking;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.msgbundle.EGI18n;
import com.picsauditing.employeeguard.services.entity.util.file.FileRowMapper;
import com.picsauditing.util.Strings;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class EmployeeFileRowMapperTest {

	public static final int CONTRACTOR_ID = 901;

	public static final String EMPLOYEE_FIRST_NAME = "Bob";
	public static final String EMPLOYEE_LAST_NAME = "The Builder";
	public static final String EMPLOYEE_EMAIL = "test@testing.com";

	private ResourceBundleMocking resourceBundleMocking;

	@Before
	public void setUp() {
		resourceBundleMocking = new ResourceBundleMocking();
		resourceBundleMocking.setUp();
	}

	@After
	public void tearDown() {
		resourceBundleMocking.tearDown();
	}

	@Test
	public void testIsHeader_RowIsNotHeader() {
		ResourceBundleMocking resourceBundleMocking = new ResourceBundleMocking();
		resourceBundleMocking.setUp();
		FileRowMapper<Employee> fileRowMapper = new EmployeeFileRowMapper(CONTRACTOR_ID);

		boolean result = fileRowMapper.isHeader(new String[]{"test", "1", "2", "3"});

		assertFalse(result);
	}

	@Test
	public void testIsHeader_RowIsHeader() {
		FileRowMapper<Employee> fileRowMapper = new EmployeeFileRowMapper(CONTRACTOR_ID);

		boolean result = fileRowMapper.isHeader(new String[] {
				EGI18n.getTextFromResourceBundle("EMPLOYEE.IMPORT_EXPORT.TEMPLATE.HEADER.FIRST_NAME"),
				EGI18n.getTextFromResourceBundle("EMPLOYEE.IMPORT_EXPORT.TEMPLATE.HEADER.LAST_NAME"),
				EGI18n.getTextFromResourceBundle("EMPLOYEE.IMPORT_EXPORT.TEMPLATE.HEADER.TITLE"),
				EGI18n.getTextFromResourceBundle("EMPLOYEE.IMPORT_EXPORT.TEMPLATE.HEADER.EMAIL"),
				EGI18n.getTextFromResourceBundle("EMPLOYEE.IMPORT_EXPORT.TEMPLATE.HEADER.PHONE"),
				EGI18n.getTextFromResourceBundle("EMPLOYEE.IMPORT_EXPORT.TEMPLATE.HEADER.UNIQUE_ID")
		});

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
		assertNotNull(result.getGuid());
	}

}
