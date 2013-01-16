package com.picsauditing.actions.report;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

import java.io.BufferedReader;
import java.io.StringReader;

import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.picsauditing.PicsActionTest;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.dao.ReportDAO;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.model.report.ReportModel;
import com.picsauditing.util.Strings;

public class ReportApiTest extends PicsActionTest {
	private ReportApi reportAction;

	private Report report;
	@Mock
	private ReportDAO reportDao;
	@Mock
	private ReportModel reportModel;
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

		verify(bufferedReader, never()).close();
		assertTrue(result.isEmpty());
	}

	@Ignore
	@Test
	public void testGetJsonFromRequestPayload_ParseJsonInRequest() throws Exception {
		String json = "{\"test\", \"yay it works\"}";

		when(bufferedReader.readLine()).thenReturn(json + Strings.NEW_LINE);
		when(request.getReader()).thenReturn(bufferedReader);

		JSONObject actual = Whitebox.invokeMethod(reportAction, "getJsonFromRequestPayload");

		verify(bufferedReader, times(1)).close();
		assertEquals(json, actual.toJSONString());
	}
}
