package com.picsauditing.integration.google;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.picsauditing.model.general.LatLong;

public class TimezoneLookupTest {
	private TimezoneLookup timezone;
	private LatLong latLong;

	@Mock
	private HttpClient httpClient;
	@Mock
	private HttpMethod httpMethod;

	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);

		timezone = new TimezoneLookup();

		latLong = new LatLong(39.71732000000001, -105.1713450);

		Whitebox.setInternalState(timezone, "httpClient", httpClient);
		Whitebox.setInternalState(timezone, "httpMethod", httpMethod);
	}

	@Test
	public void foo() throws Exception {
		DateTimeZone zone = DateTimeZone.forID("US/Pacific");
	}

	@Test
	public void testTimezoneFromLatLong() throws Exception {
		InputStream stream = new ByteArrayInputStream(responseOk.getBytes("UTF-8"));
		when(httpMethod.getResponseBodyAsStream()).thenReturn(stream);
		when(httpClient.executeMethod(httpMethod)).thenReturn(200);

		String tz = timezone.timezoneFromLatLong(latLong);
	}

	private static final String responseOk = "{\n" +
			"   \"dstOffset\" : 0.0,\n" +
			"   \"rawOffset\" : -25200.0,\n" +
			"   \"status\" : \"OK\",\n" +
			"   \"timeZoneId\" : \"America/Denver\",\n" +
			"   \"timeZoneName\" : \"Mountain Standard Time\"\n" +
			"}";

	private static final String responseNotOk = "{\n" +
			"   \"status\" : \"REQUEST_DENIED\"\n" +
			"}";
}
