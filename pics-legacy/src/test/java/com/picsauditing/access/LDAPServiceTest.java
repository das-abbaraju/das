package com.picsauditing.access;

import com.picsauditing.access.LDAPService;
import com.picsauditing.jpa.entities.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.springframework.security.core.Authentication;
import org.springframework.security.ldap.authentication.ad.ActiveDirectoryLdapAuthenticationProvider;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ActiveDirectoryLdapAuthenticationProvider.class)
public class LDAPServiceTest {

    @Mock
    private ActiveDirectoryLdapAuthenticationProvider ldapActiveDirectoryAuthProvider;

    private LDAPService ldapService;

    private String USERNAME = "joesixpack";

    private String PASSWORD = "8675309";


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        ldapService = new LDAPService();
        ldapActiveDirectoryAuthProvider = PowerMockito.mock(ActiveDirectoryLdapAuthenticationProvider.class);
        Whitebox.setInternalState(ldapService, "ldapActiveDirectoryAuthProvider", ldapActiveDirectoryAuthProvider);
    }

    @Test
    public void testDoLDAPLoginAuthentication() throws Exception {
        boolean result = ldapService.doLDAPLoginAuthentication(LDAPService.PICSAD,USERNAME,PASSWORD);

        Assert.assertFalse(result);
    }


    @Test
    public void testDoPICSLdapAuthentication() throws Exception {
        boolean result = Whitebox.invokeMethod(ldapService, "doPICSLdapAuthentication", USERNAME, PASSWORD);

        assertFalse(result);
    }

    @Test
    public void testGetPICSLdapUser() throws Exception {
        String result = Whitebox.invokeMethod(ldapService, "getPICSLdapUser", USERNAME);

        assertEquals(result, USERNAME + LDAPService.PICS_CORP);
    }
}
