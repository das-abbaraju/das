package com.picsauditing.actions.report;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.BufferedReader;
import java.io.StringReader;

import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.picsauditing.PicsActionTest;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.model.report.ReportService;

public class ReportApiTest extends PicsActionTest {

	private ReportApi reportApi;

	@Mock
	private ReportService reportService;
	@Mock
	private Report report;
	@Mock
	private BufferedReader bufferedReader;

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

		assertEquals(ReportDynamic.JSON, strutsResult);
	}

//	@Test
//	public void testReport_NullModelTypeFailsValidationSetsSuccessToFalse() throws Exception {
//		when(report.getModelType()).thenReturn(null);
//		when(reportService.createReport(any(ReportContext.class))).thenReturn(report);
//		when(reportService.buildJsonResponse(any(ReportContext.class))).thenReturn(reponseJson);
//
//		String strutsResult = reportApi.execute();
//
//		assertEquals(ReportDynamic.JSON, strutsResult);
//		JSONObject json = reportApi.getJson();
//		assertThat((Boolean) json.get(ReportJson.EXT_JS_SUCCESS), is(equalTo(Boolean.FALSE)));
//	}

	@Test
	public void testGetJsonFromRequestPayload_NullReaderReturnsEmptyJSON() throws Exception {
		when(request.getReader()).thenReturn(null);

		JSONObject result = Whitebox.invokeMethod(reportApi, "getJsonFromRequestPayload");

		verify(bufferedReader, never()).close();
		assertTrue(result.isEmpty());
	}

	@Test
	public void testGetJsonFromRequestPayload_NullBufferedReaderReturnsEmptyJSON() throws Exception {
		when(bufferedReader.readLine()).thenReturn(null);
		when(request.getReader()).thenReturn(bufferedReader);

		JSONObject result = Whitebox.invokeMethod(reportApi, "getJsonFromRequestPayload");

		verify(bufferedReader, times(1)).close();
		assertTrue(result.isEmpty());
	}

	@Test
	public void testGetJsonFromRequestPayload_ParseJsonInRequest() throws Exception {
		String json = "{\"test\":\"yay it works\"}";
		BufferedReader spy = Mockito.spy(new BufferedReader(new StringReader(json)));

		when(request.getReader()).thenReturn(spy);

		JSONObject actual = Whitebox.invokeMethod(reportApi, "getJsonFromRequestPayload");

		verify(spy, times(1)).close();
		assertEquals(json, actual.toJSONString());
	}
}
