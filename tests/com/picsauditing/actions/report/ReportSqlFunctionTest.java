package com.picsauditing.actions.report;

import static com.picsauditing.util.Assert.assertContains;
import static org.mockito.Mockito.*;

import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import com.picsauditing.EntityFactory;
import com.picsauditing.PicsActionTest;
import com.picsauditing.access.Permissions;
import com.picsauditing.report.models.ModelType;

public class ReportSqlFunctionTest extends PicsActionTest {

	ReportSqlFunction reportSqlFunction;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		reportSqlFunction = new ReportSqlFunction();
		super.setUp(reportSqlFunction);
	}

	@Test
	public void testReportSqlFunction_Accounts_AccountNameString() throws Exception {
		reportSqlFunction.setType(ModelType.Accounts);
		reportSqlFunction.setFieldId("AccountName");

		when(permissions.getAccountIdString()).thenReturn("123");
		when(permissions.getUserIdString()).thenReturn("123");

		reportSqlFunction.execute();
		JSONObject json = reportSqlFunction.getJson();

	
		assertContains("Report.Function.GroupConcat", json.toString());
	}
}
