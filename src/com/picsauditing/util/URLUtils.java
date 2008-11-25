package com.picsauditing.util;

import java.net.URL;

import javax.servlet.http.HttpServletRequest;

public class URLUtils 
{
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
}
