package com.picsauditing.integration.google;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import org.junit.Before;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

public class UrlSignerTest {
	private UrlSigner urlSigner;
	private String keyString = "vNIXE0xscrmjlyV-12Nj_BvUPaw=";

	@Before
	public void setup() throws Exception {
		System.setProperty("gk", keyString);
		urlSigner = new UrlSigner();
	}

	@Test
	public void testSignRequest_Happy() throws Exception {
		String url = "http://maps.googleapis.com/maps/api/geocode/json?address=New+York&sensor=false&client=clientID";
		String signedUrl = "http://maps.googleapis.com/maps/api/geocode/json?address=New+York&sensor=false&client=clientID&signature=KrU1TzVQM7Ur0i8i7K3huiw3MsA=";

		String signedRequest = urlSigner.signRequest(url);

		assertThat(signedUrl, is(equalTo(signedRequest)));
	}

	@Test
	public void testComputeSignature_Happy() throws Exception {
		String resource = "/maps/api/geocode/json?address=New+York&sensor=false&client=clientID";
		String signature = "KrU1TzVQM7Ur0i8i7K3huiw3MsA=";

		String computedSignature = Whitebox.invokeMethod(urlSigner, "computeSignature", resource);

		assertThat(computedSignature, is(equalTo(signature)));
	}
}
