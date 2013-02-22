package com.picsauditing.interceptors;

import com.picsauditing.PicsActionTest;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.access.*;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.security.EncodedKey;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.lang.reflect.Method;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class SecurityInterceptorTest extends PicsActionTest {
    private SecurityInterceptor securityInterceptor;
    private String apiKey = EncodedKey.randomApiKey();
    private ActionClassToTestSecurityInterceptor action;

    @Mock
    private UserDAO userDAO;
    @Mock
    private User user;
    @Mock
    private PermissionBuilder permissionBuilder;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        securityInterceptor = new SecurityInterceptor();
        action = new ActionClassToTestSecurityInterceptor();
        setUp(action);

        action.setApiKey(apiKey);

        PicsTestUtil.autowireDAOsFromDeclaredMocks(action, this);

        when(userDAO.findByApiKey(apiKey)).thenReturn(user);

        Whitebox.setInternalState(action, "permissionBuilder", permissionBuilder);
        Whitebox.setInternalState(action, "permissions", permissions);
    }

    @Test
    public void checkSecurityAnnotationsLoggingInAsNecessary_ApiKeyIsBogus() throws Exception {
        when(userDAO.findByApiKey(apiKey)).thenThrow(new SecurityException());
        Method method = action.getClass().getMethod("executeApi");

        Exception e = securityInterceptor.checkSecurityAnnotationsLoggingInAsNecessary(action,method);

        assertTrue(e instanceof AjaxNotLoggedInException);
    }

    @Test
    public void checkSecurityAnnotationsLoggingInAsNecessary_MethodIsAnonymous() throws Exception {
        action.setApiKey(null);
        when(permissions.loginRequired(response, request)).thenReturn(true);
        Method method = action.getClass().getMethod("executeAnonymous");

        Exception e = securityInterceptor.checkSecurityAnnotationsLoggingInAsNecessary(action,method);

        assertThat(e, is(nullValue()));
    }


    @Test
    public void checkSecurityAnnotationsLoggingInAsNecessary_MethodIsAnonymousAndRequiresPermissions() throws Exception {
        Method method = action.getClass().getMethod("executeAnonymousAndRequiredPermission");

        Exception e = securityInterceptor.checkSecurityAnnotationsLoggingInAsNecessary(action,method);

        assertTrue(e instanceof SecurityException);
    }

    @Test
    public void checkSecurityAnnotationsLoggingInAsNecessary_MethodIsAnonymousAndApi() throws Exception {
        Method method = action.getClass().getMethod("executeAnonymousAndApi");

        Exception e = securityInterceptor.checkSecurityAnnotationsLoggingInAsNecessary(action,method);

        assertTrue(e instanceof SecurityException);
    }

    @Test
    public void checkSecurityAnnotationsLoggingInAsNecessary_MethodIsApiAndRequiresPermissions_UserIsNotApiUserDoesNotHaveRequiredPermission() throws Exception {
        action.setApiKey(null);
        when(permissions.loginRequired(response, request)).thenReturn(true);
        doThrow(new NoRightsException("foo")).when(permissions).tryPermission((OpPerms) Matchers.any(), (OpType) Matchers.any());
        Method method = action.getClass().getMethod("executeApiOrRequiredPermission");

        Exception e = securityInterceptor.checkSecurityAnnotationsLoggingInAsNecessary(action,method);

        assertTrue(e instanceof NoRightsException);
    }

    @Test
    public void checkSecurityAnnotationsLoggingInAsNecessary_MethodIsApiAndRequiresPermissions_UserIsNotApiUserHasRequiredPermission() throws Exception {
        action.setApiKey(null);
        when(permissions.loginRequired(response, request)).thenReturn(true);
        doNothing().when(permissions).tryPermission((OpPerms) Matchers.any(), (OpType)Matchers.any());
        Method method = action.getClass().getMethod("executeApiOrRequiredPermission");

        Exception e = securityInterceptor.checkSecurityAnnotationsLoggingInAsNecessary(action,method);

        assertThat(e, is(nullValue()));
    }

    @Test
    public void checkSecurityAnnotationsLoggingInAsNecessary_MethodIsApiAndRequiresPermissions_ApiUser() throws Exception {
        Method method = action.getClass().getMethod("executeApiOrRequiredPermission");

        Exception e = securityInterceptor.checkSecurityAnnotationsLoggingInAsNecessary(action,method);

        assertThat(e, is(nullValue()));
    }

    @Test
    public void checkSecurityAnnotationsLoggingInAsNecessary_apiCallWithApiKey_noPermsAnnot_userNoPerms() throws Exception {
        Method method = action.getClass().getMethod("executeApi");

        Exception e = securityInterceptor.checkSecurityAnnotationsLoggingInAsNecessary(action,method);

        assertThat(e, is(nullValue()));
    }

    @Test
    public void checkSecurityAnnotationsLoggingInAsNecessary_apiCallWithNoAPIKey() throws Exception {
        action.setApiKey(null);
        when(request.getHeader("X-Requested-With")).thenReturn("XMLHttpRequest");
        Method method = action.getClass().getMethod("executeApi");
        Exception e = securityInterceptor.checkSecurityAnnotationsLoggingInAsNecessary(action,method);
        assertTrue(e instanceof AjaxNotLoggedInException);
    }
}
