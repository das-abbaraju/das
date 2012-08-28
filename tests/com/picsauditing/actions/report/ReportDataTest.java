package com.picsauditing.actions.report;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.json.simple.JSONObject;
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
import com.picsauditing.EntityFactory;
import com.picsauditing.PICS.I18nCache;
import com.picsauditing.access.Permissions;
import com.picsauditing.access.ReportValidationException;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.model.ReportModel;
import com.picsauditing.report.access.ReportUtil;
import com.picsauditing.report.models.ModelType;
import com.picsauditing.util.SpringUtils;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ I18nCache.class, ServletActionContext.class, ActionContext.class, SpringUtils.class,
		LoggerFactory.class, ReportUtil.class })
@PowerMockIgnore({ "org.apache.commons.logging.*", "org.apache.xerces.*" })
public class ReportDataTest {
	private ReportData reportAction;
	private Report report;
	private Map<String, Object> session;

	private Permissions permissions;
	@Mock
	private HttpServletRequest request;
	@Mock
	private ActionContext actionContext;

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

		PowerMockito.mockStatic(ServletActionContext.class);
		when(ServletActionContext.getRequest()).thenReturn(request);

		session = new HashMap<String, Object>();
		when(actionContext.getSession()).thenReturn(session);
		when(ActionContext.getContext()).thenReturn(actionContext);

		report = new Report();
		report.setId(123);

		reportAction = new ReportData();
		reportAction.setReport(report);
		permissions = EntityFactory.makePermission();
		Whitebox.setInternalState(reportAction, "permissions", permissions);
	}

	@Test
	@Ignore
	public void testData_JsonResult() throws Exception {
		report.setModelType(ModelType.Contractors);

		String strutsResult = reportAction.execute();

		assertEquals(ReportDynamic.JSON, strutsResult);
	}

	@Test
	@Ignore
	public void testData_ReportFailsValidation() throws Exception {
		Report report = new Report();
		report.setModelType(null);
		try {
			ReportModel.validate(report);
			// Should always throw before this line
			fail();
		} catch (ReportValidationException rve) {
		}

		String strutsResult = reportAction.execute();
		JSONObject json = reportAction.getJson();

		assertFalse((Boolean) json.get("success"));
		assertEquals(ReportDynamic.JSON, strutsResult);
	}
}
