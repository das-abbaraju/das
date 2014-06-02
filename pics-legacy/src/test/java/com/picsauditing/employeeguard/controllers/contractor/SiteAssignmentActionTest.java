package com.picsauditing.employeeguard.controllers.contractor;

import com.picsauditing.PicsActionTest;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.models.EntityAuditInfo;
import com.picsauditing.employeeguard.services.AccountService;
import com.picsauditing.employeeguard.services.AssignmentService;
import com.picsauditing.employeeguard.services.RoleService;
import com.picsauditing.employeeguard.services.entity.EmployeeEntityService;
import com.picsauditing.employeeguard.services.entity.SkillEntityService;
import com.picsauditing.employeeguard.services.status.StatusCalculatorService;
import com.picsauditing.jpa.entities.User;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

public class SiteAssignmentActionTest extends PicsActionTest {

	public static final int SITE_ID = 123;

	// Class under test
	private SiteAssignmentAction siteAssignmentAction;

	@Mock
	private AccountService accountService;
	@Mock
	private AssignmentService assignmentService;
	@Mock
	private EmployeeEntityService employeeEntityService;
	@Mock
	private RoleService roleService;
	@Mock
	private StatusCalculatorService statusCalculatorService;
	@Mock
	private SkillEntityService skillEntityService;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		siteAssignmentAction = new SiteAssignmentAction();
		this.setUp(siteAssignmentAction);

		Whitebox.setInternalState(siteAssignmentAction, "accountService", accountService);
		Whitebox.setInternalState(siteAssignmentAction, "assignmentService", assignmentService);
		Whitebox.setInternalState(siteAssignmentAction, "employeeEntityService", employeeEntityService);
		Whitebox.setInternalState(siteAssignmentAction, "roleService", roleService);
		Whitebox.setInternalState(siteAssignmentAction, "statusCalculatorService", statusCalculatorService);
		Whitebox.setInternalState(siteAssignmentAction, "skillEntityService", skillEntityService);

		when(permissions.getAppUserID()).thenReturn(User.SYSTEM);
	}

	@Test
	public void testAssign_AssignmentFailed() throws Exception {
		doThrow(new RuntimeException()).when(assignmentService)
				.assignEmployeeToSiteRole(anyInt(), anyInt(), anyInt(), any(EntityAuditInfo.class));

		siteAssignmentAction.assign();

		verifyAssignmentFailure(siteAssignmentAction.getJson());
	}

	@Test
	public void testAssign_AssignmentSuccessful() throws Exception {
		when(employeeEntityService.find(anyInt(), anyInt())).thenReturn(new Employee());

		siteAssignmentAction.assign();

		verifyAssignmentSuccess(siteAssignmentAction.getJson());
	}

	@Test
	public void testUnassign_AssignmentFailed() throws Exception {
		doThrow(new RuntimeException()).when(assignmentService)
				.unassignEmployeeFromSiteRole(anyInt(), anyInt(), anyInt());

		siteAssignmentAction.unassign();

		verifyAssignmentFailure(siteAssignmentAction.getJson());
	}

	@Test
	public void testUnassign_AssignmentSuccessful() throws Exception {
		when(employeeEntityService.find(anyInt(), anyInt())).thenReturn(new Employee());

		siteAssignmentAction.unassign();

		verifyAssignmentSuccess(siteAssignmentAction.getJson());
	}

	@Test
	public void testUnassignAll_AssignmentSuccessful() throws Exception {
		setupUnassignAllTest();

		String result = siteAssignmentAction.unassignAll();

		verifyAssignmentSuccess(siteAssignmentAction.getJson());
	}

	private void setupUnassignAllTest() {
		Employee employee = new Employee();

		when(employeeEntityService.find(anyInt(), anyInt())).thenReturn(employee);

		siteAssignmentAction.setSiteId(SITE_ID);
	}

	@Test
	public void testUnassignAll_AssignmentFailed() throws Exception {
		doThrow(new RuntimeException()).when(assignmentService)
				.unassignEmployeeFromSite(anyInt(), anyInt());

		String result = siteAssignmentAction.unassignAll();

		verifyAssignmentFailure(siteAssignmentAction.getJson());
	}

	private void verifyAssignmentFailure(final JSONObject json) {
		assertEquals("{\"status\":\"FAILURE\"}", json.toJSONString());
	}

	private void verifyAssignmentSuccess(final JSONObject json) {
		assertEquals("{\"status\":\"SUCCESS\"}", json.toJSONString());
	}
}
