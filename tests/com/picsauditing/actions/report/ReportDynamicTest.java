package com.picsauditing.actions.report;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.PICS.I18nCache;
import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.model.report.ReportModel;
import com.picsauditing.report.access.ReportUtil;
import com.picsauditing.util.SpringUtils;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ I18nCache.class, ServletActionContext.class, ActionContext.class, SpringUtils.class, LoggerFactory.class, ReportUtil.class })
@PowerMockIgnore({ "org.apache.commons.logging.*", "org.apache.xerces.*" })
public class ReportDynamicTest {

	private ReportDynamic reportDynamic;
	private Report report;
	private Map<String, Object> session;

	@Mock private Permissions permissions;
	@Mock private ReportModel reportModel;
	@Mock private HttpServletRequest request;
	@Mock private ActionContext actionContext;

	// PowerMocked in setUpBeforeClass
	private static Logger logger;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		logger = PowerMockito.mock(Logger.class);
		PowerMockito.mockStatic(LoggerFactory.class);
		PowerMockito.when(LoggerFactory.getLogger(ReportDynamic.class)).thenReturn(logger);
	}

	@Before
	public void setUp() throws Exception {
		PowerMockito.mockStatic(I18nCache.class);
		
		MockitoAnnotations.initMocks(this);

		PowerMockito.mockStatic(SpringUtils.class);
		PowerMockito.mockStatic(ActionContext.class);

		when(request.getParameter("report")).thenReturn("123");
		PowerMockito.mockStatic(ServletActionContext.class);
		when(ServletActionContext.getRequest()).thenReturn(request);

		session = new HashMap<String, Object>();
		when(actionContext.getSession()).thenReturn(session);
		when(ActionContext.getContext()).thenReturn(actionContext);

		report = new Report();

		reportDynamic = new ReportDynamic();
		reportDynamic.setReport(report);
		when(permissions.getUserId()).thenReturn(941);
		Whitebox.setInternalState(reportDynamic, "permissions", permissions);
		Whitebox.setInternalState(reportDynamic, "reportModel", reportModel);
	}

	@Test
	@Ignore
	public void testExecute_RedirectIfUserTypedJunkInUrl() throws Exception {
		// If the url is something like report=JUNK_HERE, the report will always be null
		reportDynamic.setReport(null);
		String invalidReportId = "NOT_A_VALID_REPORT_ID";
		when(request.getParameter("report")).thenReturn(invalidReportId);

		String strutsResult = reportDynamic.execute();

		assertEquals(ReportDynamic.REDIRECT, strutsResult);
		verify(logger).warn("java.lang.NumberFormatException: For input string: \"" + invalidReportId + "\"");
	}

	@Test
	@Ignore
	public void testExecute_NullReportServletActionContextThrowsException() throws Exception {
		PowerMockito.doThrow(new RuntimeException("test exception")).when(ServletActionContext.class);
		ServletActionContext.getRequest();
		reportDynamic.setReport(null);

		String strutsResult = reportDynamic.execute();

		assertEquals(ReportDynamic.REDIRECT, strutsResult);
		verify(logger).error("java.lang.RuntimeException: test exception");
	}

	@Test
	@Ignore
	public void testExecute_NullReport() throws Exception {
		reportDynamic.setReport(null);

		String strutsResult = reportDynamic.execute();

		assertEquals(ReportDynamic.REDIRECT, strutsResult);
	}

	@Test
	@Ignore
	public void testExecute_NullReportUserDoesNotHavePermissionToViewAndCopy() throws Exception {
		when(reportModel.canUserViewAndCopy(permissions, anyInt())).thenReturn(false);
		reportDynamic.setReport(null);

		String strutsResult = reportDynamic.execute();

		assertEquals(ReportDynamic.REDIRECT, strutsResult);
		assertThat((String)session.get("errorMessage"), is("You do not have permissions to view that report."));
	}

}
