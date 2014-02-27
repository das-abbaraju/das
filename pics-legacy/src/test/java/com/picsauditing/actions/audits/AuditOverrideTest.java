package com.picsauditing.actions.audits;

import com.picsauditing.PicsTest;
import com.picsauditing.access.PermissionAware;
import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.Account;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Created by kchase on 2/25/14.
 */
public class AuditOverrideTest extends PicsTest{
    private static final int OPERATOR_ACCOUNT_ID = 100;

    private AuditOverride auditOverride;

    @Mock
    private Permissions permissions;


    @Before
    public void setUp() throws Exception {
        super.setUp();
        MockitoAnnotations.initMocks(this);
        auditOverride = new AuditOverride();
        Whitebox.setInternalState(auditOverride, "permissions", permissions);
    }

    @Test
    public void testGetViewableByAccountId_NullAccountNonOperator() throws Exception {
        when(permissions.isOperatorCorporate()).thenReturn(false);

        Integer id = Whitebox.invokeMethod(auditOverride, "getViewableByAccountId", null);
        assertEquals(Account.EVERYONE, id.intValue());
    }

    @Test
    public void testGetViewableByAccountId_NullAccountOperator() throws Exception {
        when(permissions.isOperatorCorporate()).thenReturn(true);
        when(permissions.getAccountId()).thenReturn(OPERATOR_ACCOUNT_ID);

        Integer id = Whitebox.invokeMethod(auditOverride, "getViewableByAccountId", null);
        assertEquals(OPERATOR_ACCOUNT_ID, id.intValue());
    }

    @Test
    public void testGetViewableByAccountId_NonNullAccount() throws Exception {
        when(permissions.getAccountId()).thenReturn(OPERATOR_ACCOUNT_ID);

        Account account = new Account();
        account.setId(OPERATOR_ACCOUNT_ID);

        Integer id = Whitebox.invokeMethod(auditOverride, "getViewableByAccountId", account);
        assertEquals(OPERATOR_ACCOUNT_ID, id.intValue());
    }

}
