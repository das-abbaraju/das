package com.picsauditing.actions.report;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.Matchers.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.models.ReportDynamicModel;
import com.picsauditing.report.access.DynamicReportUtil;
import com.picsauditing.report.access.ReportAccess;
import com.picsauditing.report.models.ModelType;
import com.picsauditing.util.SpringUtils;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ServletActionContext.class, ActionContext.class, SpringUtils.class, LoggerFactory.class, ReportAccess.class, DynamicReportUtil.class })
@PowerMockIgnore({ "org.apache.commons.logging.*", "org.apache.xerces.*" })
public class ReportDynamicTest {
	private ReportDynamic reportDynamic;
	private Report report;
	private Map<String, Object> session;

	@Mock
	private Permissions permissions;
	@Mock
	private ReportDynamicModel reportDynamicModel;
	@Mock
	private HttpServletRequest request;
	@Mock
	private ActionContext actionContext;
	// PowerMocked in setUp
	private Logger logger;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		PowerMockito.mockStatic(SpringUtils.class);
		PowerMockito.mockStatic(ActionContext.class);
		PowerMockito.mockStatic(ReportAccess.class);

		logger = PowerMockito.mock(Logger.class);
		PowerMockito.mockStatic(LoggerFactory.class);
		PowerMockito.when(LoggerFactory.getLogger(ReportDynamic.class)).thenReturn(logger);

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
		Whitebox.setInternalState(reportDynamic, "reportDynamicModel", reportDynamicModel);
	}

	@Ignore("Verifying logger is too hard for now")
	@Test
	public void testExecute_NullReportServletActionContextThrowsException() throws Exception {
		PowerMockito.doThrow(new RuntimeException("test exception")).when(ServletActionContext.class);
		ServletActionContext.getRequest();
		reportDynamic.setReport(null);

		String strutsResult = reportDynamic.execute();

		assertEquals(ReportDynamic.REDIRECT, strutsResult);
		verify(logger).error("test exception");
	}

	@Test
	public void testExecute_NullReport() throws Exception {
		reportDynamic.setReport(null);

		String strutsResult = reportDynamic.execute();

		assertEquals(ReportDynamic.REDIRECT, strutsResult);
	}

	@Test
	public void testExecute_NullReportUserDoesNotHavePermissionToViewAndCopy() throws Exception {
		when(ReportAccess.canUserViewAndCopy(anyInt(), anyInt())).thenReturn(false);
		reportDynamic.setReport(null);

		String strutsResult = reportDynamic.execute();

		assertEquals(ReportDynamic.REDIRECT, strutsResult);
		assertThat((String)session.get("errorMessage"), is("You do not have permissions to view that report."));
	}


	@Test
	public void testData_XXX() throws Exception {
		report.setModelType(ModelType.Contractors);

		String strutsResult = reportDynamic.data();

		assertEquals(ReportDynamic.JSON, strutsResult);
	}

	@Test
	public void testData_ReportFailsValidation() throws Exception {
		PowerMockito.mockStatic(DynamicReportUtil.class);
		PowerMockito.doThrow(new RuntimeException()).when(DynamicReportUtil.class);
		DynamicReportUtil.validate((Report) any());
		report.setModelType(ModelType.Contractors);

		String strutsResult = reportDynamic.data();

		// the only way to tell there was an exception is to inspect the json object
		JSONObject json = reportDynamic.getJson();
		assertFalse((Boolean)json.get("success"));
		assertEquals(ReportDynamic.JSON, strutsResult);
	}
}
