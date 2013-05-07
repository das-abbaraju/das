package com.picsauditing.jpa.entities;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class EmployeeCompetencyTest {
	private EmployeeCompetency employeeCompetency;

	@Mock
	private Employee employee;
	@Mock
	private OperatorCompetency operatorCompetency;
	@Mock
	private OperatorCompetencyEmployeeFile employeeFile;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		employeeCompetency = new EmployeeCompetency();
		employeeCompetency.setCompetency(operatorCompetency);
		employeeCompetency.setEmployee(employee);
	}

	@Test
	public void testIsMissingDocumentation_DocumentationNotRequired() throws Exception {
		when(operatorCompetency.isRequiresDocumentation()).thenReturn(false);

		assertFalse(employeeCompetency.isMissingDocumentation());
	}

	@Test
	public void testIsMissingDocumentation_DocumentationRequiredAndFileMissing() throws Exception {
		when(employee.getCompetencyFiles()).thenReturn(Collections.<OperatorCompetencyEmployeeFile>emptyList());
		when(operatorCompetency.isRequiresDocumentation()).thenReturn(true);

		assertTrue(employeeCompetency.isMissingDocumentation());
	}

	@Test
	public void testIsMissingDocumentation_DocumentationRequiredAndFileIsCurrent() throws Exception {
		List<OperatorCompetencyEmployeeFile> files = new ArrayList<>();
		files.add(employeeFile);

		when(employee.getCompetencyFiles()).thenReturn(files);
		when(employeeFile.getCompetency()).thenReturn(operatorCompetency);
		when(employeeFile.isExpired()).thenReturn(false);
		when(operatorCompetency.isRequiresDocumentation()).thenReturn(true);

		assertFalse(employeeCompetency.isMissingDocumentation());
	}

	@Test
	public void testIsMissingDocumentation_DocumentationRequiredAndFileIsExpired() throws Exception {
		List<OperatorCompetencyEmployeeFile> files = new ArrayList<>();
		files.add(employeeFile);

		when(employee.getCompetencyFiles()).thenReturn(files);
		when(employeeFile.getCompetency()).thenReturn(operatorCompetency);
		when(employeeFile.isExpired()).thenReturn(true);
		when(operatorCompetency.isRequiresDocumentation()).thenReturn(true);

		assertTrue(employeeCompetency.isMissingDocumentation());
	}
}
