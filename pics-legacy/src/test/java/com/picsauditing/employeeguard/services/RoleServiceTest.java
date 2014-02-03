package com.picsauditing.employeeguard.services;

import com.picsauditing.employeeguard.daos.AccountSkillDAO;
import com.picsauditing.employeeguard.daos.RoleDAO;
import com.picsauditing.employeeguard.daos.RoleEmployeeDAO;
import com.picsauditing.employeeguard.entities.AccountSkillEmployee;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.RoleEmployee;
import com.picsauditing.employeeguard.entities.builders.EmployeeBuilder;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.ArrayList;
import java.util.Arrays;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RoleServiceTest {

    public static final int SITE_ID = 345;
    public static final int CORPORATE_ROLE_ID = 23;
    public static final int USER_ID = 6;
    public static final int CORPORATE_ID = 712;
    private RoleService roleService;

    @Mock
    private AccountService accountService;
    @Mock
    private AccountSkillDAO accountSkillDAO;
    @Mock
    private AccountSkillEmployeeService accountSkillEmployeeService;
    @Mock
    private RoleDAO roleDAO;
    @Mock
    private RoleEmployeeDAO roleEmployeeDAO;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        roleService = new RoleService();

        Whitebox.setInternalState(roleService, "accountService", accountService);
        Whitebox.setInternalState(roleService, "accountSkillDAO", accountSkillDAO);
        Whitebox.setInternalState(roleService, "accountSkillEmployeeService", accountSkillEmployeeService);
        Whitebox.setInternalState(roleService, "roleDAO", roleDAO);
        Whitebox.setInternalState(roleService, "roleEmployeeDAO", roleEmployeeDAO);
    }

    @Test
    public void testAssignEmployeeToSite() {
        when(accountService.getTopmostCorporateAccountIds(SITE_ID)).thenReturn(Arrays.asList(CORPORATE_ID));

        roleService.assignEmployeeToSite(SITE_ID, CORPORATE_ROLE_ID, buildFakeEmployee(), USER_ID);

        verifyTest();
    }

    private void verifyTest() {
        verify(roleEmployeeDAO).save(any(RoleEmployee.class));
        verify(accountSkillEmployeeService).save(anyListOf(AccountSkillEmployee.class));
    }

    private Employee buildFakeEmployee() {
        return new EmployeeBuilder()
                .accountId(561)
                .email("test@test.com")
                .slug("ABE456A2")
                .build();
    }
}
