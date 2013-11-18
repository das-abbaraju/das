package com.picsauditing.util;

import com.picsauditing.PicsTest;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import org.junit.Test;
import com.picsauditing.EntityFactory;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.jpa.entities.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.*;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.when;

public class PermissionToViewContractorTest extends PicsTest {
    private static final int CON_ID = 100;
    private static final int OP_ID = 100;

    private PermissionToViewContractor permissionToViewContractor;

    @Mock
    ContractorAccount contractor;
    @Mock
    OperatorAccount operator;
    @Mock
    Permissions permissions;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        permissionToViewContractor = new PermissionToViewContractor(CON_ID, permissions);
    }

    @Test
    public void testCheck_Contractor() throws Exception {
        Boolean result;

        when(permissions.isContractor()).thenReturn(true);
        when(permissions.getAccountId()).thenReturn(CON_ID);
        result = permissionToViewContractor.check(false);
        assertTrue(result);
    }

    @Test
    public void testCheck_OperatorAssociatedWithContractor() throws Exception {
        Boolean result;

        ContractorOperator co = new ContractorOperator();
        co.setOperatorAccount(operator);
        co.setContractorAccount(contractor);
        List<ContractorOperator> conOps = new ArrayList<>();
        conOps.add(co);

        when(permissions.isOperatorCorporate()).thenReturn(true);
        when(permissions.getAccountId()).thenReturn(OP_ID);
        when(operator.getIdString()).thenReturn("" + OP_ID);
        when(contractor.getStatus()).thenReturn(AccountStatus.Active);
        when(permissions.getAccountIdString()).thenReturn("" + OP_ID);
        when(permissions.hasPermission(OpPerms.ContractorDetails)).thenReturn(true);

        permissionToViewContractor.setOperators(conOps);
        permissionToViewContractor.setContractor(contractor);
        result = permissionToViewContractor.check(false);


        assertTrue(result);
    }

    @Test
    public void testCheck_OperatorOnDeclinedDeactivatedDeletedContractor() throws Exception {
        Boolean result;

        when(permissions.isOperatorCorporate()).thenReturn(true);
        when(permissions.hasPermission(OpPerms.ContractorDetails)).thenReturn(true);

        permissionToViewContractor.setContractor(contractor);

        when(contractor.getStatus()).thenReturn(AccountStatus.Deactivated);
        result = permissionToViewContractor.check(false);
        assertFalse(result);

        when(contractor.getStatus()).thenReturn(AccountStatus.Declined);
        result = permissionToViewContractor.check(false);
        assertFalse(result);

        when(contractor.getStatus()).thenReturn(AccountStatus.Deleted);
        result = permissionToViewContractor.check(false);
        assertFalse(result);
    }
}
