package com.picsauditing.employeeguard.controllers.contractor;

import com.picsauditing.PicsActionTest;
import com.picsauditing.PicsTranslationTest;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.services.EmployeeService;
import com.picsauditing.employeeguard.services.RoleService;
import org.json.simple.JSONObject;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import javax.persistence.NoResultException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

public class SiteAssignmentActionTest extends PicsActionTest {

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

    private void verifyAssignmentFailure(final JSONObject json) {
        assertEquals("{\"status\":\"FAILURE\"}", json.toJSONString());
    }

    private void verifyAssignmentSuccess(final JSONObject json) {
        assertEquals("{\"status\":\"SUCCESS\"}", json.toJSONString());
    }
}
