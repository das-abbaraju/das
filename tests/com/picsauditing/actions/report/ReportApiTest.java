package com.picsauditing.actions.report;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
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

public class ReportApiTest extends PicsActionTest {

	private ReportApi reportAction;
	private Report report;

	@Mock
	private BufferedReader bufferedReader;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		reportAction = new ReportApi();
		super.setUp(reportAction);

		PicsTestUtil.autowireDAOsFromDeclaredMocks(reportAction, this);

		report = new Report();
		report.setId(123);

		reportAction.setReport(report);
	}

	@Test
	public void testReport_NullModelTypeFailsValidationSetsSuccessToFalse() throws Exception {
		report.setModelType(null);

		String strutsResult = reportAction.execute();

		JSONObject json = reportAction.getJson();
		assertThat((Boolean) json.get("success"), is(equalTo(Boolean.FALSE)));
		assertEquals(ReportDynamic.JSON, strutsResult);
	}

	@Test
	public void testGetJsonFromRequestPayload_NullReaderReturnsEmptyJSON() throws Exception {
		when(request.getReader()).thenReturn(null);

		JSONObject result = Whitebox.invokeMethod(reportAction, "getJsonFromRequestPayload");

		verify(bufferedReader, never()).close();
		assertTrue(result.isEmpty());
	}

	@Test
	public void testGetJsonFromRequestPayload_NullBufferedReaderReturnsEmptyJSON() throws Exception {
		when(bufferedReader.readLine()).thenReturn(null);
		when(request.getReader()).thenReturn(bufferedReader);

		JSONObject result = Whitebox.invokeMethod(reportAction, "getJsonFromRequestPayload");

		verify(bufferedReader, times(1)).close();
		assertTrue(result.isEmpty());
	}

	@Test
	public void testGetJsonFromRequestPayload_ParseJsonInRequest() throws Exception {
		String json = "{\"test\":\"yay it works\"}";
		BufferedReader spy = Mockito.spy(new BufferedReader(new StringReader(json)));

		when(request.getReader()).thenReturn(spy);

		JSONObject actual = Whitebox.invokeMethod(reportAction, "getJsonFromRequestPayload");

		verify(spy, times(1)).close();
		assertEquals(json, actual.toJSONString());
	}
}
