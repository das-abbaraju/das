package com.picsauditing.access;

import com.picsauditing.jpa.entities.IdpUser;
import com.picsauditing.service.user.IdpUserService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.springframework.security.ldap.authentication.ad.ActiveDirectoryLdapAuthenticationProvider;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mock;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ActiveDirectoryLdapAuthenticationProvider.class)
public class LdapServiceTest {

    @Mock
    private ActiveDirectoryLdapAuthenticationProvider ldapActiveDirectoryAuthProvider;

    @Mock
    private IdpUserService idpUserService;

    @Mock
    private IdpUser idpUser;

    private LdapService ldapService;

    private String USERNAME = "joesixpack";

    private String PASSWORD = "8675309";

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        ldapService = new LdapService();
        ldapActiveDirectoryAuthProvider = mock(ActiveDirectoryLdapAuthenticationProvider.class);
        when(idpUserService.loadIdpUserBy(USERNAME, LdapService.PICSAD)).thenReturn(idpUser);

        Whitebox.setInternalState(ldapService, "ldapActiveDirectoryAuthProvider", ldapActiveDirectoryAuthProvider);
        Whitebox.setInternalState(ldapService, "idpUserService", idpUserService);

    }

    @Test
    public void testDoLDAPLoginAuthentication() throws Exception {
        boolean result = ldapService.doLdapLoginAuthentication(LdapService.PICSAD, USERNAME, PASSWORD);

        Assert.assertFalse(result);
    }

    @Test
    public void testDoPICSLdapAuthentication() throws Exception {
        boolean result = Whitebox.invokeMethod(ldapService, "doPicsLdapAuthentication", USERNAME, PASSWORD);

        assertFalse(result);
    }

    @Test
    public void testGetPicsLdapUser() throws Exception {
        String result = Whitebox.invokeMethod(ldapService, "appendPicsLdapDomain", USERNAME);

        assertEquals(result, USERNAME + LdapService.PICS_CORP);
    }

    @Test
    public void testIsValidIdp() throws Exception {
        boolean result = ldapService.isValidIdp(LdapService.PICSAD);

        assertTrue(result);
    }

    @Test
    public void testIsValidIdp_null() throws Exception {
        boolean result = ldapService.isValidIdp(null);

        assertFalse(result);
    }

    @Test
    public void testIsValidIdp_invalidText() throws Exception {
        boolean result = ldapService.isValidIdp("test");

        assertFalse(result);
    }
}
