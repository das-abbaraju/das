package com.picsauditing.integration.google;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.picsauditing.util.Strings;

public class GoogleApiOverHttp {
	private static final Logger logger = LoggerFactory.getLogger(GoogleApiOverHttp.class);

	private HttpClient httpClient;
	private HttpMethod httpMethod;

	String googleClientId;

	String createUrl(String urlFormat, String... data) {
		return String.format(urlFormat, encodeData(data));
	}

	// if the google client id is not set, we will not sign it and we'll be
	// using the free/open API which has strict limits - this is not a good
	// longterm idea but it will keep the application running
	String signUrlIfGoogleIdSet(String url) {
		if (Strings.isEmpty(googleClientId)) {
			return url;
		}
		try {
			return UrlSigner.signRequest(url, googleClientId);
		} catch (MalformedURLException e) {
			logger.error("Cannot sign url, returning unsigned (should use public API): {}", e.getLocalizedMessage());
			return url;
		}
	}

	private Object[] encodeData(String... data) {
		Object[] args = new Object[data.length];
		int i = 0;
		try {
			for (String item : data) {
				args[i++] = URLEncoder.encode(item, "UTF-8");
			}
		} catch (UnsupportedEncodingException e) {
			// won't happen unless java removes UTF-8 or we parameterize this
			logger.error("Cannot create geocode Url: {}", e.getMessage());
			return null;
		}
		return args;
	}

	InputStream executeUrl(String url) {
		try {
			HttpMethod method = httpMethod(url);
			HttpClient client = httpClient();
			int responseCode = client.executeMethod(method);
			if (responseCode != 200) {
				return null;
			}
			return method.getResponseBodyAsStream();
		} catch (HttpException e) {
			logger.error("HttpException trying to get lat-long from address, using url {}: {}", url, e.getMessage());
		} catch (IOException e) {
			logger.error("IOException trying to get lat-long from address, using url {}: {}", url, e.getMessage());
		}
		return null;
	}

	// seam for injecting a mock
	private HttpClient httpClient() {
		if (httpClient == null) {
			return new HttpClient();
		} else {
			return httpClient;
		}
	}

	// seam for injecting a mock
	private HttpMethod httpMethod(String url) {
		if (httpMethod == null) {
			return new GetMethod(url);
		} else {
			return httpMethod;
		}
	}
}
