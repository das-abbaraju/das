package com.picsauditing.actions.report;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.picsauditing.PicsActionTest;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.dao.ReportDAO;
import com.picsauditing.dao.ReportUserDAO;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.model.report.ReportModel;

public class ReportDataTest extends PicsActionTest {
	private ReportData reportAction;

	private Report report;
	@Mock
	private ReportDAO reportDao;
	@Mock
	private ReportModel reportModel;
	@Mock
	private ReportUserDAO reportUserDao;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		reportAction = new ReportData();
		super.setUp(reportAction);

		PicsTestUtil.autowireDAOsFromDeclaredMocks(reportAction, this);

		report = new Report();
		report.setId(123);

		reportAction.setReport(report);
	}

	@Test
	public void testReport_NullModelTypeFailsValidationSetsSuccessToFalse() throws Exception {
		report.setModelType(null);

		String strutsResult = reportAction.report();

		JSONObject json = reportAction.getJson();
		assertThat((Boolean) json.get("success"), is(equalTo(Boolean.FALSE)));
		assertEquals(ReportDynamic.JSON, strutsResult);
	}
}
