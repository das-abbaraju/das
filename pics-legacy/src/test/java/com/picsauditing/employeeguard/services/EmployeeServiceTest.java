package com.picsauditing.employeeguard.services;

import com.picsauditing.employeeguard.EGTestDataUtil;
import com.picsauditing.employeeguard.daos.AccountGroupDAO;
import com.picsauditing.employeeguard.daos.EmployeeDAO;
import com.picsauditing.employeeguard.daos.softdeleted.SoftDeletedEmployeeDAO;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.services.email.EmailHashService;
import com.picsauditing.employeeguard.services.email.EmailService;
import com.picsauditing.employeeguard.services.entity.employee.EmployeeEntityService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import static org.junit.Assert.*;

public class EmployeeServiceTest {
	@Mock
	private AccountGroupDAO accountGroupDAO;
	@Mock
	private EmployeeDAO employeeDAO;
	@Mock
	private EmployeeEntityService employeeEntityService;
	@Deprecated
	@Mock
	private AccountSkillProfileService accountSkillProfileService;
	@Mock
	private SoftDeletedEmployeeDAO softDeletedEmployeeDAO;
	@Mock
	private EmailHashService emailHashService;
	@Mock
	private EmailService emailService;

	private EmployeeService employeeService;

	private EGTestDataUtil egTestDataUtil;
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		employeeService = new EmployeeService();
		Whitebox.setInternalState(employeeService, "accountGroupDAO", accountGroupDAO);
		Whitebox.setInternalState(employeeService, "employeeDAO", employeeDAO);
		Whitebox.setInternalState(employeeService, "employeeEntityService", employeeEntityService);
		Whitebox.setInternalState(employeeService, "accountSkillProfileService", accountSkillProfileService);
		Whitebox.setInternalState(employeeService, "softDeletedEmployeeDAO", softDeletedEmployeeDAO);
		Whitebox.setInternalState(employeeService, "emailHashService", emailHashService);
		Whitebox.setInternalState(employeeService, "emailService", emailService);

		egTestDataUtil = new EGTestDataUtil();
	}

	@After
	public void tearDown() throws Exception {

	}

	@Test
	public void testSave() throws Exception {

		Employee employee = egTestDataUtil.buildNewFakeEmployee();

		employeeService.save(employee,egTestDataUtil.CONTRACTOR_ID,egTestDataUtil.APP_USER_ID);

		assertNotNull(employee.getGuid());

	}
}
