package com.picsauditing.actions.report;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import com.picsauditing.report.ReportJson;

public class ReportApiTest extends PicsActionTest {

	private ReportApi reportApi;

	@Mock
	private Report report;
	@Mock
	private ReportService reportService;
	@Mock
	private BufferedReader bufferedReader;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		reportApi = new ReportApi();
		super.setUp(reportApi);

		PicsTestUtil.autowireDAOsFromDeclaredMocks(reportApi, this);

		when(report.getId()).thenReturn(123);

		Whitebox.setInternalState(reportApi, "report", report);
		Whitebox.setInternalState(reportApi, "reportService", reportService);
	}

//	@Test
//	public void testExecute() {
//		reportAction.execute();
//		JSONObject json = reportAction.getJson();
//
//		assertNotNull(json);
//		boolean value = (Boolean)json.get(ReportJson.EXT_JS_SUCCESS);
//		assertEquals(true, value);
//	}

	@Test
	public void testReport_NullModelTypeFailsValidationSetsSuccessToFalse() throws Exception {
		when(report.getModelType()).thenReturn(null);

		String strutsResult = reportApi.execute();

		JSONObject json = reportApi.getJson();
		assertThat((Boolean) json.get(ReportJson.EXT_JS_SUCCESS), is(equalTo(Boolean.FALSE)));
		assertEquals(ReportDynamic.JSON, strutsResult);
	}

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
