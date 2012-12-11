package com.picsauditing.integration.google;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

public class UrlSignerTest {
	private static String keyString = "vNIXE0xscrmjlyV-12Nj_BvUPaw=";
	private String clientId = "clientID";

	@BeforeClass
	public static void setup() throws Exception {
		System.setProperty("gk", keyString);
	}

	@AfterClass
	public static void teardown() throws Exception {
		System.clearProperty("gk");
	}


	@Test
	public void testSignRequest_Happy() throws Exception {
		String url = "http://maps.googleapis.com/maps/api/geocode/json?address=New+York&sensor=false";
		String signedUrl = "http://maps.googleapis.com/maps/api/geocode/json?address=New+York&sensor=false&client=clientID&signature=KrU1TzVQM7Ur0i8i7K3huiw3MsA=";

		String signedRequest = UrlSigner.signRequest(url, clientId);

		assertThat(signedRequest, is(equalTo(signedUrl)));
	}

	@Test
	public void testComputeSignature_Happy() throws Exception {
		String resource = "/maps/api/geocode/json?address=New+York&sensor=false&client=clientID";
		String signature = "KrU1TzVQM7Ur0i8i7K3huiw3MsA=";

		String computedSignature = Whitebox.invokeMethod(UrlSigner.class, "computeSignature", resource);

		assertThat(computedSignature, is(equalTo(signature)));
	}
}
