package com.picsauditing.service.fileimport;

import com.picsauditing.PicsTranslationTest;
import com.picsauditing.access.Permissions;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.Employee;
import com.picsauditing.jpa.entities.EmployeeSite;
import com.picsauditing.jpa.entities.OperatorAccount;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class EmployeeFileParserTest extends PicsTranslationTest {
	private EmployeeFileParser employeeFileParser;

	@Mock
	private Account account;
	@Mock
	private Employee employee;
	@Mock
	private File file;
	@Mock
	private OperatorAccount operatorAccount;
	@Mock
	private OperatorAccountDAO operatorAccountDAO;
	@Mock
	private Permissions permissions;
	@Mock
	private Row row;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		employeeFileParser = new EmployeeFileParser(operatorAccountDAO, account, permissions);
	}

	@Test
	public void testBuildEmployeeSite() throws Exception {
		List<OperatorAccount> operators = new ArrayList<>();
		operators.add(operatorAccount);

		Whitebox.setInternalState(employeeFileParser, "operators", operators);

		EmployeeSite employeeSite = Whitebox.invokeMethod(employeeFileParser, "buildEmployeeSite", employee, 0);

		assertEquals(employee, employeeSite.getEmployee());
		assertEquals(operatorAccount, employeeSite.getOperator());
		assertNotNull(employeeSite.getEffectiveDate());
	}

	@Test
	public void testCopyEmployeeSitesToExistingEmployee() throws Exception {
		employee = new Employee();
		employee.setFirstName("Test");
		employee.setLastName("User");
		employee.setAccount(account);

		EmployeeSite employeeSite = mock(EmployeeSite.class);
		employeeSite.setEmployee(employee);
		employeeSite.setOperator(operatorAccount);

		List<EmployeeSite> sites = new ArrayList<>();
		sites.add(employeeSite);

		employee.setEmployeeSites(sites);

		Employee existing = new Employee();
		existing.setFirstName("Test");
		existing.setLastName("User");
		existing.setAccount(account);

		Set<Employee> employees = new HashSet<>();
		employees.add(existing);

		Whitebox.invokeMethod(employeeFileParser, "copyEmployeeSitesToExistingEmployee", employee, employees);

		assertFalse(existing.getEmployeeSites().isEmpty());
		verify(employeeSite, atLeastOnce()).setEmployee(existing);
	}

	@Test(expected = Exception.class)
	public void testParseRow_NullRow() throws Exception {
		Whitebox.invokeMethod(employeeFileParser, "parseRow", (Row) null);
	}

	@Test(expected = Exception.class)
	public void testParseRow_MissingRequiredField() throws Exception {
		Cell cell = mock(Cell.class);

		when(row.getCell(0)).thenReturn(cell);
		when(row.getCell(2)).thenReturn(cell);

		Whitebox.invokeMethod(employeeFileParser, "parseRow", row);
	}

	@Test
	public void testParseRow_Success() throws Exception {
		Cell firstName = mock(Cell.class);
		Cell lastName = mock(Cell.class);
		Cell title = mock(Cell.class);

		List<Cell> cells = new ArrayList<>();
		cells.add(firstName);
		cells.add(lastName);
		cells.add(title);

		when(row.cellIterator()).thenReturn(cells.iterator());
		when(row.getCell(0)).thenReturn(firstName);
		when(row.getCell(1)).thenReturn(lastName);
		when(row.getCell(2)).thenReturn(title);

		when(firstName.getColumnIndex()).thenReturn(0);
		when(firstName.toString()).thenReturn("First name");

		when(lastName.getColumnIndex()).thenReturn(1);
		when(lastName.toString()).thenReturn("Last name");

		when(title.getColumnIndex()).thenReturn(2);
		when(title.toString()).thenReturn("Title");

		Employee employee = Whitebox.invokeMethod(employeeFileParser, "parseRow", row);

		assertNotNull(employee);
		assertEquals("First name", employee.getFirstName());
		assertEquals("Last name", employee.getLastName());
		assertEquals("Title", employee.getTitle());
	}
}
