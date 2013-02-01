package com.picsauditing.actions.report;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.picsauditing.PicsActionTest;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.report.ReportService;

public class ReportApiTest extends PicsActionTest {

	private PicsActionSupport reportApi;

	@Mock
	private ReportService reportService;
	@Mock
	private Report report;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		reportApi = new ReportApi();
		super.setUp(reportApi);

		PicsTestUtil.autowireDAOsFromDeclaredMocks(reportApi, this);

		Whitebox.setInternalState(reportApi, "reportService", reportService);
	}

	@Test
	public void testExecute() throws Exception {
		String strutsResult = reportApi.execute();

		assertEquals(PicsActionSupport.JSON, strutsResult);
	}

//	@Test
//	public void testReport_NullModelTypeFailsValidationSetsSuccessToFalse() throws Exception {
//		when(report.getModelType()).thenReturn(null);
//		when(reportService.createReport(any(ReportContext.class))).thenReturn(report);
//		when(reportService.buildJsonResponse(any(ReportContext.class))).thenReturn(reponseJson);
//
//		String strutsResult = reportApi.execute();
//
//		assertEquals(PicsActionSupport.JSON, strutsResult);
//		JSONObject json = reportApi.getJson();
//		assertThat((Boolean) json.get(ReportJson.EXT_JS_SUCCESS), is(equalTo(Boolean.FALSE)));
//	}

}
