package com.picsauditing.employeeguard.controllers.contractor;

import com.picsauditing.PicsActionTest;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.services.EmployeeService;
import com.picsauditing.employeeguard.services.RoleService;
import com.picsauditing.jpa.entities.User;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import javax.persistence.NoResultException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

public class SiteAssignmentActionTest extends PicsActionTest {

    public static final int SITE_ID = 123;
    private SiteAssignmentAction siteAssignmentAction;

    @Mock
    private EmployeeService employeeService;
    @Mock
    private RoleService roleService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        siteAssignmentAction = new SiteAssignmentAction();
        this.setUp(siteAssignmentAction);

        Whitebox.setInternalState(siteAssignmentAction, "employeeService", employeeService);
        Whitebox.setInternalState(siteAssignmentAction, "roleService", roleService);

	    when(permissions.getAppUserID()).thenReturn(User.SYSTEM);
    }

    @Test
    public void testAssign_AssignmentFailed() throws Exception {
        when(employeeService.findEmployee(anyString(), anyInt())).thenThrow(new NoResultException());

        siteAssignmentAction.assign();

        verifyAssignmentFailure(siteAssignmentAction.getJson());
    }

    @Test
    public void testAssign_AssignmentSuccessful() throws Exception {
        when(employeeService.findEmployee(anyString(), anyInt())).thenReturn(new Employee());

        siteAssignmentAction.assign();

        verifyAssignmentSuccess(siteAssignmentAction.getJson());
    }

    @Test
    public void testUnassign_AssignmentFailed() throws Exception {
        when(employeeService.findEmployee(anyString(), anyInt())).thenThrow(new NoResultException());

        siteAssignmentAction.unassign();

        verifyAssignmentFailure(siteAssignmentAction.getJson());
    }

    @Test
    public void testUnassign_AssignmentSuccessful() throws Exception {
        when(employeeService.findEmployee(anyString(), anyInt())).thenReturn(new Employee());

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
        when(employeeService.findEmployee(anyString(), anyInt())).thenReturn(employee);
        siteAssignmentAction.setSiteId(SITE_ID);
    }

    @Test
    public void testUnassignAll_AssignmentFailed() throws Exception {
        when(employeeService.findEmployee(anyString(), anyInt())).thenThrow(new NoResultException());

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
