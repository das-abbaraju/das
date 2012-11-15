package com.picsauditing.actions;

import com.picsauditing.access.Anonymous;
import com.picsauditing.access.UserAgentParser;

public class About extends PicsActionSupport {

	private String browserName;
	private String operatingSystem;
		
	private static final long serialVersionUID = 1L;

	@Override
	@Anonymous
	public String execute() {
		UserAgentParser userAgentParser = new UserAgentParser(getRequest().getHeader("User-Agent"));
		browserName = userAgentParser.getBrowserName();
		operatingSystem = userAgentParser.getBrowserOperatingSystem();
		
		return SUCCESS;
	}
	
	public String getBrowserName() {
		return browserName;
	}
	
	public String getOperatingSystem() {
		return operatingSystem;
	}	
	
}