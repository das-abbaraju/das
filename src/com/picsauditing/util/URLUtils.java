package com.picsauditing.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;

public class URLUtils implements StreamContentProvider {

	public String getResponseFrom(String uri) throws IOException {
			return contentOf(uri);
	}

	private String contentOf(String uri) throws IOException {

		InputStream inputStream = openResponseFrom(uri);
		StringBuffer buffer = new StringBuffer();
		int nextByte;
		while ((nextByte = inputStream.read()) > -1) {
			buffer.append((char) nextByte);
		}
		inputStream.close();
		return buffer.toString();
	}

	public static String getProtocol(HttpServletRequest request)
	{
		try
		{
			URL url = new URL(request.getRequestURL().toString());
			return url.getProtocol();
		}
		catch( Exception e ) {}

		return null;
	}

	public InputStream openResponseFrom(String uri) throws IOException {
		return new URL(uri).openStream();
	}
}
