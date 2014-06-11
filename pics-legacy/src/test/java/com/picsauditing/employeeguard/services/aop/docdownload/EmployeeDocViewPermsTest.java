package com.picsauditing.employeeguard.services.aop.docdownload;

import com.picsauditing.access.Permissions;
import com.picsauditing.employeeguard.daos.ProfileDocumentDAO;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Profile;
import com.picsauditing.employeeguard.entities.ProfileDocument;
import com.picsauditing.employeeguard.services.entity.employee.EmployeeEntityService;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.web.SessionInfoProvider;
import com.picsauditing.web.SessionInfoProviderFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.springframework.context.ApplicationContext;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EmployeeDocViewPermsTest {

	@Mock
	private ApplicationContext applicationContext;

	@Mock
	private Permissions permissions;

	@Mock
	private OperatorDocViewPerms nextInChain;

	@Mock
	private SessionInfoProvider sessionInfoProvider;

	@Mock
	private ProfileDocumentDAO profileDocumentDAO;

	@Mock
	private ProfileDocument profileDocument;

	@Mock
	private Employee employee;

	@Mock
	private EmployeeEntityService employeeEntityService;

	@Mock
	Profile profile;

	private static final int SKILL_ID = 11;
	private static final int EMPLOYEE_ID = 9;
	private static final int APP_USER_ID = 131073;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		SpringUtils springUtils = new SpringUtils();
		springUtils.setApplicationContext(applicationContext);
		when(applicationContext.getBean("EmployeeEntityService")).thenReturn(employeeEntityService);
		when(employeeEntityService.find(any(Integer.class))).thenReturn(employee);
		when(employee.getProfile()).thenReturn(profile);
		when(profile.getUserId()).thenReturn(APP_USER_ID);


	}

	@Test
	public void testChkPermissions_Allowed() throws Exception {
		Whitebox.setInternalState(SessionInfoProviderFactory.class, "mockSessionInfoProvider", sessionInfoProvider);
		when(sessionInfoProvider.getAppUserId()).thenReturn(APP_USER_ID);

		EmployeeDocViewPerms employeeDocViewPerms = new EmployeeDocViewPerms();

		DocViewableStatus docViewableStatus = employeeDocViewPerms.chkPermissions(EMPLOYEE_ID, SKILL_ID);

		assertEquals("Expected Result to be " + DocViewableStatus.ALLOWED.toString(), DocViewableStatus.ALLOWED.toString(), docViewableStatus.toString());

	}

	@Test
	public void testChkPermissions_VerifyNextInChainIsCalled() throws Exception {
		Whitebox.setInternalState(SessionInfoProviderFactory.class, "mockSessionInfoProvider", sessionInfoProvider);
		when(sessionInfoProvider.getAppUserId()).thenReturn(-999);

		EmployeeDocViewPerms employeeDocViewPerms = new EmployeeDocViewPerms();
		employeeDocViewPerms.attach(nextInChain);

		int skillId = -999;
		employeeDocViewPerms.chkPermissions(EMPLOYEE_ID, skillId);

		verify(nextInChain).chkPermissions(EMPLOYEE_ID, skillId);

	}

	@Test
	public void testChkPermissions_VerifyUNKNOWNStatusIsEvaluated() throws Exception {
		Whitebox.setInternalState(SessionInfoProviderFactory.class, "mockSessionInfoProvider", sessionInfoProvider);
		when(sessionInfoProvider.getAppUserId()).thenReturn(-999);

		EmployeeDocViewPerms employeeDocViewPerms = new EmployeeDocViewPerms();

		int skillId = -999;
		DocViewableStatus docViewableStatus = employeeDocViewPerms.chkPermissions(EMPLOYEE_ID, skillId);

		assertEquals("Expected Result to be " + DocViewableStatus.UNKNOWN.toString(), DocViewableStatus.UNKNOWN.toString(), docViewableStatus.toString());

	}


}
