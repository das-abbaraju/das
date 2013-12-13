package com.picsauditing.service.i18n;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.PicsActionTest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class ActionUsageContextTest extends PicsActionTest {
    private static final String TEST_PAGENAME_CONTEXT_PARAM = "TestPageNameContextParameter";
    private static final String TEST_PAGENAME_ACTIONCONTEXT = "TestPageNameActionContext";
    private static final String TEST_PAGENAME_REQUEST_PARAM = "TestPageNameActionContext";
    private static final String TEST_PAGEORDER_REQUEST_PARAM = "1.2.3";

    private ActionUsageContext actionUsageContext;
    private Map<String, Object> parameters;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        actionUsageContext = new ActionUsageContext();
        setupMocks();
        parameters = new HashMap<>();
        actionContext.put(ActionContext.PARAMETERS, parameters);
        parameters.put(ActionUsageContext.PAGE_NAME_PARAMETER_KEY, TEST_PAGENAME_CONTEXT_PARAM);
        actionContext.setName(TEST_PAGENAME_ACTIONCONTEXT);
        when(request.getParameter(ActionUsageContext.PAGE_NAME_PARAMETER_KEY)).thenReturn(TEST_PAGENAME_REQUEST_PARAM);
    }

    @Test
    public void testPageName_PullFromContextParameter() throws Exception {
        String result = actionUsageContext.pageName();
        assertTrue(result.equals(TEST_PAGENAME_CONTEXT_PARAM));
    }

    @Test
    public void testPageName_PullFromRequestParameter() throws Exception {
        parameters.clear();

        String result = actionUsageContext.pageName();
        assertTrue(result.equals(TEST_PAGENAME_REQUEST_PARAM));
    }

    @Test
    public void testPageName_PullFromActionContext() throws Exception {
        parameters.clear();
        when(request.getParameter(ActionUsageContext.PAGE_NAME_PARAMETER_KEY)).thenReturn(null);

        String result = actionUsageContext.pageName();
        assertTrue(result.equals(TEST_PAGENAME_ACTIONCONTEXT));
    }

    @Test
    public void testPageName_DefaultPageNameWhenNothingElseAvailable() throws Exception {
        parameters.clear();
        when(request.getParameter(ActionUsageContext.PAGE_NAME_PARAMETER_KEY)).thenReturn(null);
        actionContext.setName(null);

        String result = actionUsageContext.pageName();
        assertTrue(result.equals(ActionUsageContext.DEFAULT_PAGENAME));
    }

    @Test
    public void testPageOrder_PullFromRequestParams() throws Exception {
        when(request.getParameter(ActionUsageContext.PAGE_ORDER_PARAMETER_KEY)).thenReturn(TEST_PAGEORDER_REQUEST_PARAM);
        String result = actionUsageContext.pageOrder();
        assertTrue(result.equals(TEST_PAGEORDER_REQUEST_PARAM));
    }

    @Test
    public void testPageOrder_NullWhenNotSetInParams() throws Exception {
        when(request.getParameter(ActionUsageContext.PAGE_ORDER_PARAMETER_KEY)).thenReturn(null);
        String result = actionUsageContext.pageOrder();
        assertNull(result);
    }

}
