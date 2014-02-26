package com.picsauditing.employeeguard.services.entity;

import com.picsauditing.employeeguard.daos.EmployeeDAO;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import static org.junit.Assert.fail;

public class EmployeeServiceTest {

	EmployeeService employeeService;

	@Mock
	private EmployeeDAO employeeDAO;

	@Before
	public void setUp() throws Exception {
		employeeService = new EmployeeService();

		MockitoAnnotations.initMocks(this);

		Whitebox.setInternalState(employeeService, "employeeDAO", employeeDAO);
	}

	@Test
	public void testFind() throws Exception {
		fail("Not implemented.");
	}

	@Test
	public void testSearch() throws Exception {
		fail("Not implemented.");
	}

	@Test
	public void testSave() throws Exception {
		fail("Not implemented.");
	}

	@Test
	public void testUpdate() throws Exception {
		fail("Not implemented.");
	}

	@Test
	public void testDelete() throws Exception {
		fail("Not implemented.");
	}

	@Test
	public void testDeleteById() throws Exception {
		fail("Not implemented.");
	}
}
