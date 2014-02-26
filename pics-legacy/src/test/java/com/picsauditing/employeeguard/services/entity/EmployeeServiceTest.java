package com.picsauditing.employeeguard.services.entity;

import com.picsauditing.employeeguard.daos.EmployeeDAO;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class EmployeeServiceTest {

	EmployeeService employeeService;

	@Mock
	private EmployeeDAO employeeDAO;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		employeeService = new EmployeeService();
	}

	@Test
	public void testFind() throws Exception {

	}

	@Test
	public void testSearch() throws Exception {

	}

	@Test
	public void testSave() throws Exception {

	}

	@Test
	public void testUpdate() throws Exception {

	}

	@Test
	public void testDelete() throws Exception {

	}

	@Test
	public void testDeleteById() throws Exception {

	}
}
