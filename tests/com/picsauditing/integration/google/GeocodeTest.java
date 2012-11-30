package com.picsauditing.integration.google;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.picsauditing.model.general.LatLong;

public class GeocodeTest {
	private Geocode geocode;

	@Mock
	private HttpClient httpClient;
	@Mock
	private HttpMethod httpMethod;
	
	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);

		geocode = new Geocode();

		Whitebox.setInternalState(geocode, "httpClient", httpClient);
		Whitebox.setInternalState(geocode, "httpMethod", httpMethod);
	}
	
	@Test
	public void testExtractLatLong_Happy() throws Exception {
		JSONObject response = (JSONObject) JSONValue.parse(responseExample);

		LatLong latLong = Whitebox.invokeMethod(geocode, "extractLatLong", response);

		assertThat(39.71732000000001, is(equalTo(latLong.getLatitude())));
		assertThat(-105.1713450, is(equalTo(latLong.getLongitude())));
	}

	@Test
	public void testLatLongFromAddress_Happy() throws Exception {
		InputStream stream = new ByteArrayInputStream(responseExample.getBytes("UTF-8"));
		when(httpMethod.getResponseBodyAsStream()).thenReturn(stream);
		when(httpClient.executeMethod(httpMethod)).thenReturn(200);
		
		LatLong latLong = geocode.latLongFromAddress("15318 W. Ellsworth Dr., Golden CO 80401");

		assertThat(39.71732000000001, is(equalTo(latLong.getLatitude())));
		assertThat(-105.1713450, is(equalTo(latLong.getLongitude())));
	}
	
	@Test
	public void testLatLongFromAddress_HttpError() throws Exception {
		InputStream stream = new ByteArrayInputStream(responseExample.getBytes("UTF-8"));
		when(httpMethod.getResponseBodyAsStream()).thenReturn(stream);
		when(httpClient.executeMethod(httpMethod)).thenReturn(500);

		LatLong latLong = geocode.latLongFromAddress("15318 W. Ellsworth Dr., Golden CO 80401");

		assertThat(latLong, is(equalTo(null)));
	}

	private static final String responseDeniedExample = "{\n" +
			"   \"results\" : [],\n" +
			"   \"status\" : \"REQUEST_DENIED\"\n" +
			"}";

	private static final String responseExample = "{\n" +
			"   \"results\" : [\n" +
			"      {\n" +
			"         \"address_components\" : [\n" +
			"            {\n" +
			"               \"long_name\" : \"15318\",\n" +
			"               \"short_name\" : \"15318\",\n" +
			"               \"types\" : [ \"street_number\" ]\n" +
			"            },\n" +
			"            {\n" +
			"               \"long_name\" : \"W Ellsworth Dr\",\n" +
			"               \"short_name\" : \"W Ellsworth Dr\",\n" +
			"               \"types\" : [ \"route\" ]\n" +
			"            },\n" +
			"            {\n" +
			"               \"long_name\" : \"Mesa View Estates\",\n" +
			"               \"short_name\" : \"Mesa View Estates\",\n" +
			"               \"types\" : [ \"neighborhood\", \"political\" ]\n" +
			"            },\n" +
			"            {\n" +
			"               \"long_name\" : \"Golden\",\n" +
			"               \"short_name\" : \"Golden\",\n" +
			"               \"types\" : [ \"locality\", \"political\" ]\n" +
			"            },\n" +
			"            {\n" +
			"               \"long_name\" : \"Jefferson\",\n" +
			"               \"short_name\" : \"Jefferson\",\n" +
			"               \"types\" : [ \"administrative_area_level_2\", \"political\" ]\n" +
			"            },\n" +
			"            {\n" +
			"               \"long_name\" : \"Colorado\",\n" +
			"               \"short_name\" : \"CO\",\n" +
			"               \"types\" : [ \"administrative_area_level_1\", \"political\" ]\n" +
			"            },\n" +
			"            {\n" +
			"               \"long_name\" : \"United States\",\n" +
			"               \"short_name\" : \"US\",\n" +
			"               \"types\" : [ \"country\", \"political\" ]\n" +
			"            },\n" +
			"            {\n" +
			"               \"long_name\" : \"80401\",\n" +
			"               \"short_name\" : \"80401\",\n" +
			"               \"types\" : [ \"postal_code\" ]\n" +
			"            }\n" +
			"         ],\n" +
			"         \"formatted_address\" : \"15318 W Ellsworth Dr, Golden, CO 80401, USA\",\n" +
			"         \"geometry\" : {\n" +
			"            \"location\" : {\n" +
			"               \"lat\" : 39.71732000000001,\n" +
			"               \"lng\" : -105.1713450\n" +
			"            },\n" +
			"            \"location_type\" : \"ROOFTOP\",\n" +
			"            \"viewport\" : {\n" +
			"               \"northeast\" : {\n" +
			"                  \"lat\" : 39.71866898029151,\n" +
			"                  \"lng\" : -105.1699960197085\n" +
			"               },\n" +
			"               \"southwest\" : {\n" +
			"                  \"lat\" : 39.71597101970851,\n" +
			"                  \"lng\" : -105.1726939802915\n" +
			"               }\n" +
			"            }\n" +
			"         },\n" +
			"         \"types\" : [ \"street_address\" ]\n" +
			"      }\n" +
			"   ],\n" +
			"   \"status\" : \"OK\"\n" +
			"}";
}
