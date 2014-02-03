package com.picsauditing.employeeguard.controllers.contractor;

import com.picsauditing.PicsActionTest;
import com.picsauditing.PicsTranslationTest;
import com.picsauditing.employeeguard.services.EmployeeService;
import com.picsauditing.employeeguard.services.RoleService;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import static org.junit.Assert.fail;

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
    public void testAssign() throws Exception {
        fail("Not implemented yet.");
    }
}
