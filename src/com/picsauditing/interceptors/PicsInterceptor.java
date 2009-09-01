package com.picsauditing.interceptors;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.picsauditing.util.log.PicsLogger;

public class PicsInterceptor extends AbstractInterceptor {
	
	/**
	 * these are pages which should flip over to ssl
	 */
	public static Set<String> securePages = new HashSet<String>();
	
	@Override
	public String intercept(ActionInvocation arg0) throws Exception {

		String environment = System.getProperty("interceptor.enabled");

		HttpServletRequest request = ServletActionContext.getRequest();
		String url = request.getRequestURL().toString();
		
		String queryString = request.getQueryString();
		
		if( queryString != null && queryString.length() > 0 ) {
			url += "?" + queryString;
		}
		

		PicsLogger.start("picsinterceptor");
		PicsLogger.log(request.getRequestURI());
		PicsLogger.log("interceptor.enabled : " + System.getProperty("interceptor.enabled"));
		if( environment != null && environment.equals("enabled") && url.indexOf("Ajax") == -1) {

			if( url.toString().indexOf("https:") != -1 ) {
				
				for( String page : securePages ) {
					if( page.equals(request.getRequestURI())) {
						PicsLogger.stop();
						return run(arg0, request.getRequestURI() );
					}
				}
				
				url = switchToInsecure(url, environment);
			}
			else {
	
				boolean found = false;
				if( securePages.contains( request.getRequestURI())) {
					found = true;
				}
				
				if( ! found ) {
					PicsLogger.stop();
					return run(arg0, request.getRequestURI());
				}
				
				url = switchToSecure(url, environment);
				
			}
			
			ServletActionContext.getResponse().sendRedirect(url);
			PicsLogger.stop();
		}
		else {
			PicsLogger.stop();
			return run(arg0, request.getRequestURI());
		}
		
		
		return null;
	}

	private String switchToInsecure(String url, String environment) {
		PicsLogger.log( "switching to insecure (before): " + url);
		url = url.replaceAll("https:", "http:");
		PicsLogger.log( "switching to insecure (after): " + url);
		return url;
	}

	private String switchToSecure(String url, String environment) {
		PicsLogger.log( "switching to secure (before): " + url);
		url = url.replaceAll("http:", "https:");
		PicsLogger.log( "switching to secure (after): " + url);
		return url;
	}

	private String run(ActionInvocation arg0, String uri) throws Exception {
		PicsLogger.log( "executing next in chain" );
		ServletActionContext.getResponse().setHeader("Cache-Control", "private, max-age=0");
		
		return arg0.invoke();
	}
}
