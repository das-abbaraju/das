package com.picsauditing.interceptors;

import com.picsauditing.access.AjaxNotLoggedInException;
import com.picsauditing.access.SecurityAware;
import com.picsauditing.actions.rest.api.ApiCheck;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SecurityInterceptorTest {
    private SecurityInterceptor securityInterceptor;

    @Before
    public void setUp() throws Exception {
        securityInterceptor = new SecurityInterceptor();
    }
    @Test
    public void checkSecurityAnnotationsLoggingInAsNecessaryTest_APICall_with_NoAPIKey() throws Exception {
        SecurityAware action = new ApiCheck();
        Method method = action.getClass().getMethod("execute");
        Exception e = securityInterceptor.checkSecurityAnnotationsLoggingInAsNecessary(action,method);
        assertTrue(e instanceof AjaxNotLoggedInException);
    }
}
