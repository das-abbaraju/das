package com.picsauditing.jpa.entities;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class EmployeeTest {
	private Employee employee;

	@Mock
	private EmployeeCompetency employeeCompetency;
	@Mock
	private OperatorCompetency operatorCompetency;
	@Mock
	private OperatorCompetencyEmployeeFile employeeFile;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		employee = new Employee();

		when(employeeCompetency.getCompetency()).thenReturn(operatorCompetency);
		when(employeeFile.getCompetency()).thenReturn(operatorCompetency);
	}

	@Test
	public void testCompareTo_SimpleLastName() throws Exception {
		Employee employee1 = new Employee();
		employee1.setLastName("Zoo");
		Employee employee2 = new Employee();
		employee2.setLastName("Aardvark");

		assertTrue(employee2.compareTo(employee1) < 0);
	}

	@Test
	public void testCompareTo_SimpleFirstName() throws Exception {
		Employee employee1 = new Employee();
		employee1.setFirstName("Zohar");
		employee1.setLastName("Smith");
		Employee employee2 = new Employee();
		employee2.setFirstName("Alex");
		employee2.setLastName("Smith");

		assertTrue(employee2.compareTo(employee1) < 0);
	}

	@Test
	public void testCompareTo_Title() throws Exception {
		Employee employee1 = new Employee();
		employee1.setFirstName("Alex");
		employee1.setLastName("Smith");
		employee1.setTitle("Zoologist");
		Employee employee2 = new Employee();
		employee2.setFirstName("Alex");
		employee2.setLastName("Smith");
		employee2.setTitle("Air Condition Specialist");

		assertTrue(employee2.compareTo(employee1) < 0);
	}

	@Test
	public void testCompareTo_Id() throws Exception {
		Employee employee1 = new Employee();
		employee1.setFirstName("Alex");
		employee1.setLastName("Smith");
		employee1.setTitle("Air Condition Specialist");
		employee1.setId(999);
		Employee employee2 = new Employee();
		employee2.setFirstName("Alex");
		employee2.setLastName("Smith");
		employee2.setTitle("Air Condition Specialist");
		employee2.setId(111);

		assertTrue(employee2.compareTo(employee1) < 0);

		employee2.setId(999);
		assertTrue(employee2.compareTo(employee1) == 0);
	}

	@Test
	public void testGetOverallFileStatus_NoRequiredCompetencyAndNoFiles() {
		assertEquals(OperatorCompetencyEmployeeFileStatus.NA, employee.getOverallFileStatus());
	}

	@Test
	public void testGetOverallFileStatus_RequiredCompetencyAndFileIsCurrent() {
		when(employeeFile.isExpired()).thenReturn(false);
		when(operatorCompetency.isRequiresDocumentation()).thenReturn(true);

		employee.getCompetencyFiles().add(employeeFile);
		employee.getEmployeeCompetencies().add(employeeCompetency);

		assertEquals(OperatorCompetencyEmployeeFileStatus.PROVIDED, employee.getOverallFileStatus());
	}

	@Test
	public void testGetOverallFileStatus_RequiredCompetencyAndFileIsExpired() {
		when(employeeFile.isExpired()).thenReturn(true);
		when(operatorCompetency.isRequiresDocumentation()).thenReturn(true);

		employee.getCompetencyFiles().add(employeeFile);
		employee.getEmployeeCompetencies().add(employeeCompetency);

		assertEquals(OperatorCompetencyEmployeeFileStatus.NEEDED, employee.getOverallFileStatus());
	}

	@Test
	public void testGetOverallFileStatus_RequiredCompetencyButNoFiles() {
		when(employeeFile.isExpired()).thenReturn(true);
		when(operatorCompetency.isRequiresDocumentation()).thenReturn(true);

		employee.getEmployeeCompetencies().add(employeeCompetency);

		assertEquals(OperatorCompetencyEmployeeFileStatus.NEEDED, employee.getOverallFileStatus());
	}
}