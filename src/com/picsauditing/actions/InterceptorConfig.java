package com.picsauditing.actions;



import java.util.Iterator;

import com.picsauditing.interceptors.PicsInterceptor;

public class InterceptorConfig extends PicsActionSupport {

	private String uri = null;
	
	@Override
	public String execute() throws Exception {
		
		output = "";
		if( button == null || "list".equalsIgnoreCase(button) ) {
			
			System.out.println("===");
			System.out.println("Interceptor status: " + System.getProperty( "interceptor.enabled" ));
			System.out.println("===");

			System.out.println("Pages which require SSL");
			for( String s : PicsInterceptor.securePages ) {
				System.out.println(s);
			}
			System.out.println("===");

		}
		else if( "addSecure".equals(button)) {
			if( uri != null && uri.length() != 0 ) {
				PicsInterceptor.securePages.add(uri);
			}
		}
		else if( "deleteSecure".equals(button)) {
			if( uri != null && uri.length() != 0 ) {
				PicsInterceptor.securePages.remove( uri );
			}
		}
		else if( "clearSecure".equals(button)) {
			PicsInterceptor.securePages.clear();
		}
		else if( button.equals( "enable" ) ) {
			System.setProperty("interceptor.enabled", "enabled" );
		}
		else if( button.equals( "disable" ) ) {
			System.setProperty("interceptor.enabled", "disabled" );
		}
		
		
		return SUCCESS;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}
}
