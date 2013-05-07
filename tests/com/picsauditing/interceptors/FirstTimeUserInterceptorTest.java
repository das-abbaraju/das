package com.picsauditing.interceptors;

import com.opensymphony.xwork2.*;
import com.opensymphony.xwork2.interceptor.PreResultListener;
import com.opensymphony.xwork2.util.ValueStack;
import com.picsauditing.PICS.I18nCache;
import com.picsauditing.access.Permissions;
import com.picsauditing.actions.report.ManageReports;
import com.picsauditing.search.Database;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.Date;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FirstTimeUserInterceptorTest {

    FirstTimeUserInterceptor firstTimeUserInterceptor;
	private ManageReports manageReports;

    @Mock
    private Permissions permissions;
    @Mock
    private ActionContext actionContext;
    @Mock
    private Map<String, Object> session;
    @Mock
    private ActionInvocation invocation;
    @Mock
    private ValueStack valueStack;
	@Mock
	private I18nCache i18nCache;

    @AfterClass
    public static void tearDownClass() throws Exception {
        ActionContext.setContext((ActionContext) null);
	    Whitebox.setInternalState(I18nCache.class, "databaseForTesting", (Database) null);
    }

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        firstTimeUserInterceptor = new FirstTimeUserInterceptor();
        when(actionContext.getSession()).thenReturn(session);
        when(invocation.invoke()).thenReturn("next");
        when(invocation.getStack()).thenReturn(valueStack);

	    manageReports = new ManageReports();
	    Whitebox.setInternalState(manageReports, "i18nCache", i18nCache);

        ActionContext.setContext(actionContext);
    }

	@BeforeClass
	public static void setupClass() throws Exception {
		Whitebox.setInternalState(I18nCache.class, "databaseForTesting", mock(Database.class));
	}

    @Test
    public void testIntercept_NullPermissions() throws Exception {
        when(session.get("permissions")).thenReturn(null);
        String result = firstTimeUserInterceptor.intercept(invocation);
        assertEquals("next", result);
    }

    @Test
    public void testIntercept_RedirectForFirstTimeUser() throws Exception {
        when(permissions.isLoggedIn()).thenReturn(true);
        when(permissions.isUsingVersion7Menus()).thenReturn(true);
        when(permissions.getUsingVersion7MenusDate()).thenReturn(null);

        when(session.get("permissions")).thenReturn(permissions);

        String result = firstTimeUserInterceptor.intercept(invocation);

        assertEquals("redirect", result);
    }

    @Test
    public void testShouldRedirectToV7NavigationTutorial_NullPermissions() throws Exception {
        Boolean result = Whitebox.invokeMethod(firstTimeUserInterceptor, "shouldRedirectToV7NavigationTutorial",
                (Permissions) null);
        assertFalse(result);
    }

    @Test
    public void testShouldRedirectToV7NavigationTutorial_AlreadyUsedDynamicReports() throws Exception {
        when(permissions.isLoggedIn()).thenReturn(true);
        when(permissions.isUsingVersion7Menus()).thenReturn(true);
        when(permissions.getUsingVersion7MenusDate()).thenReturn(new Date());

        Boolean result = Whitebox.invokeMethod(firstTimeUserInterceptor, "shouldRedirectToV7NavigationTutorial",
                permissions);

        assertFalse(result);
    }

    @Test
    public void testShouldRedirectToV7NavigationTutorial_FirstTimeUser() throws Exception {
        when(permissions.isLoggedIn()).thenReturn(true);
        when(permissions.isUsingVersion7Menus()).thenReturn(true);
        when(permissions.getUsingVersion7MenusDate()).thenReturn(null);

        Boolean result = Whitebox.invokeMethod(firstTimeUserInterceptor, "shouldRedirectToV7NavigationTutorial",
                permissions);

        assertTrue(result);
    }

    @Test
    public void testIntercept_reportsManagerTutorial_RedirectForFirstTimeUser() throws Exception {
        when(permissions.isLoggedIn()).thenReturn(true);
        when(permissions.isUsingVersion7Menus()).thenReturn(false);
        when(permissions.isDynamicReportsUser()).thenReturn(true);
        when(permissions.getReportsManagerTutorialDate()).thenReturn(null);
	    when(invocation.getAction()).thenReturn(manageReports);

        when(session.get("permissions")).thenReturn(permissions);

        String result = firstTimeUserInterceptor.intercept(invocation);

        assertEquals("redirect", result);
    }

    @Test
    public void testShouldRedirectToReportsManagerTutorial_NullPermissions() throws Exception {
        Boolean result = Whitebox.invokeMethod(firstTimeUserInterceptor, "shouldRedirectToReportsManagerTutorial",
                (Permissions) null, (ActionInvocation) null);
        assertFalse(result);
    }

    @Test
    public void testShouldRedirectToReportsManagerTutorial_notFirstVisit() throws Exception {
        when(permissions.isLoggedIn()).thenReturn(true);
        when(permissions.isUsingVersion7Menus()).thenReturn(false);
        when(permissions.isDynamicReportsUser()).thenReturn(true);
        when(permissions.getReportsManagerTutorialDate()).thenReturn(new Date());
	    when(invocation.getAction()).thenReturn(manageReports);

        Boolean result = Whitebox.invokeMethod(firstTimeUserInterceptor, "shouldRedirectToReportsManagerTutorial",
                permissions, invocation);

        assertFalse(result);
    }

    @Test
    public void testShouldRedirectToReportsManagerTutorial_FirstTimeUser() throws Exception {
	    when(permissions.isLoggedIn()).thenReturn(true);
	    when(permissions.isUsingVersion7Menus()).thenReturn(false);
	    when(permissions.isDynamicReportsUser()).thenReturn(true);
	    when(permissions.getReportsManagerTutorialDate()).thenReturn(null);
	    when(invocation.getAction()).thenReturn(manageReports);

        Boolean result = Whitebox.invokeMethod(firstTimeUserInterceptor, "shouldRedirectToReportsManagerTutorial",
                permissions, invocation);

        assertTrue(result);
    }

}
