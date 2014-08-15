package com.picsauditing.featuretoggle;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import org.apache.struts2.StrutsStatics;
import org.junit.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.togglz.servlet.util.HttpServletRequestHolder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ServletFeatureUserTest {
    private static final int NOT_ZERO = 123;
    private static final String ENV = "Test Environment";
    private ServletFeatureUser servletFeatureUser;
    private ActionContext actionContext;
    protected Map<String, Object> session;

    @Mock
    private HttpSession httpSession;
    @Mock
    private HttpServletRequest request;
    @Mock
    private Permissions permissions;

    @Before
    public void setUp() throws Exception {
        System.setProperty("pics.env", ENV);

        MockitoAnnotations.initMocks(this);

        servletFeatureUser = new ServletFeatureUser();

        Map<String, Object> context = new HashMap<>();
        session = new HashMap<>();
        session.put("permissions", permissions);
        context.put(StrutsStatics.HTTP_REQUEST, request);
        context.put(ActionContext.SESSION, session);
        actionContext = new ActionContext(context);
        ActionContext.setContext(actionContext);

        when(permissions.getAccountId()).thenReturn(NOT_ZERO);
        when(permissions.getUserId()).thenReturn(NOT_ZERO);
        Whitebox.setInternalState(servletFeatureUser, "permissions", permissions);

        when(request.getSession()).thenReturn(httpSession);
        HttpServletRequestHolder.bind(request);
    }

    @After
    public void tearDown() throws Exception {
        System.clearProperty("pics.env");
        HttpServletRequestHolder.release();
    }

    @Test
    public void testGetName_NullPermsReturnsAnonymous() throws Exception {
        Whitebox.setInternalState(servletFeatureUser, "permissions", (Permissions)null);
        assertTrue("Anonymous".equals(servletFeatureUser.getName()));
    }

    @Test
    public void testGetName_PullsFromPermissions() throws Exception {
        servletFeatureUser.getName();

        verify(permissions).getName();
    }

    @Test
    public void testIsFeatureAdmin_NullPermsIsFalse() throws Exception {
        Whitebox.setInternalState(servletFeatureUser, "permissions", (Permissions)null);

        assertFalse(servletFeatureUser.isFeatureAdmin());
    }

    @Test
    public void testIsFeatureAdmin_PullsFromPermissions() throws Exception {
        assertFalse(servletFeatureUser.isFeatureAdmin());
        verify(permissions).has(OpPerms.DevelopmentEnvironment);
    }

    @Test
    public void testGetAttribute_UserIDProxiesToPermissions() throws Exception {
        servletFeatureUser.getAttribute("userID");
        verify(permissions, atLeastOnce()).getUserId();
    }

    @Test
    public void testGetAttribute_accountIDProxiesToPermissions() throws Exception {
        servletFeatureUser.getAttribute("accountID");
        verify(permissions, atLeastOnce()).getAccountId();
    }

    @Test
    public void testGetAttribute_GroupsProxiesToPermissions() throws Exception {
        servletFeatureUser.getAttribute("groups");
        verify(permissions).getAllInheritedGroupIds();
    }

    @Test
    public void testGetAttribute_countryCodeProxiesToPermissions() throws Exception {
        servletFeatureUser.getAttribute("countryCode");
        verify(permissions).getCountry();
    }

    @Test
    public void testGetAttribute_countrySubdivisionCodeProxiesToPermissions() throws Exception {
        servletFeatureUser.getAttribute("countrySubdivisionCode");
        verify(permissions).getCountrySubdivision();
    }

    @Test
    public void testGetAttribute_EnvReturnsSysProp() throws Exception {
        String env = (String)servletFeatureUser.getAttribute("env");
        assertEquals(ENV, env);
    }

    @Test
    public void testGetAttribute_EnvironmentReturnsSysProp() throws Exception {
        String env = (String)servletFeatureUser.getAttribute("environment");
        assertEquals(ENV, env);
    }

    @Test
    public void testGetAttribute_UnknownAttributeReturnsNull() throws Exception {
        Object result = servletFeatureUser.getAttribute("NOT A GOOD ATTRIBUTE");
        assertNull(result);
    }

    @Test
    public void testPermissions_NullPermsPullsFromActionContext() throws Exception {
        Whitebox.setInternalState(servletFeatureUser, "permissions", (Permissions)null);

        Permissions permsReturned = Whitebox.invokeMethod(servletFeatureUser, "permissions");

        assertEquals(permsReturned, permissions);
    }

    @Test
    public void testPermissions_NullActionContextPullsFromServletRequest() throws Exception {
        Whitebox.setInternalState(servletFeatureUser, "permissions", (Permissions)null);
        ActionContext.setContext(null);

        Whitebox.invokeMethod(servletFeatureUser, "permissions");

        verify(httpSession).getAttribute("permissions");
    }

}
