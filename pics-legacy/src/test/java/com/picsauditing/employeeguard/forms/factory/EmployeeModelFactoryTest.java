package com.picsauditing.employeeguard.forms.factory;

import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Profile;
import com.picsauditing.employeeguard.services.models.AccountModel;
import com.picsauditing.employeeguard.viewmodel.employee.EmployeeModel;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class EmployeeModelFactoryTest {
	public static final int CONTRACTOR_ID_1 = 123;
	public static final int CONTRACTOR_ID_2 = 124;
	public static final String CONTRACTOR_NAME_1 = "Contractor 1";
	public static final String CONTRACTOR_NAME_2 = "Contractor 2";
	public static final String PROFILE_NAME = "Profile name";

	private EmployeeModelFactory employeeModelFactory;

	@Mock
	private Employee employee1;
	@Mock
	private Employee employee2;
	@Mock
	private Profile profile;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		employeeModelFactory = new EmployeeModelFactory();
	}

	@Test
	public void testCreate_NullEmployee() throws Exception {
		EmployeeModel employeeModel = employeeModelFactory.create(null);

		assertNull(employeeModel);
	}

	@Test
	public void testCreate_ProfileWithMultipleEmployees() throws Exception {
		AccountModel contractor1 = new AccountModel.Builder().id(CONTRACTOR_ID_1).name(CONTRACTOR_NAME_1).build();
		AccountModel contractor2 = new AccountModel.Builder().id(CONTRACTOR_ID_2).name(CONTRACTOR_NAME_2).build();

		Map<Integer, AccountModel> contractorMap = new HashMap<>();
		contractorMap.put(CONTRACTOR_ID_1, contractor1);
		contractorMap.put(CONTRACTOR_ID_2, contractor2);

		when(employee1.getAccountId()).thenReturn(CONTRACTOR_ID_1);
		when(employee1.getProfile()).thenReturn(profile);
		when(employee2.getAccountId()).thenReturn(CONTRACTOR_ID_2);
		when(profile.getEmployees()).thenReturn(Arrays.asList(employee1, employee2));
		when(profile.getName()).thenReturn(PROFILE_NAME);

		EmployeeModel employeeModel = employeeModelFactory.create(employee1);

		assertNotNull(employeeModel);
		assertEquals(PROFILE_NAME, employeeModel.getName());
		assertEquals(2, employeeModel.getCompanyNames().size());
		assertTrue(employeeModel.getCompanyNames().contains(CONTRACTOR_NAME_1));
		assertTrue(employeeModel.getCompanyNames().contains(CONTRACTOR_NAME_2));
	}
}
