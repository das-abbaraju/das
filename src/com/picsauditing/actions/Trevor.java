package com.picsauditing.actions;

import com.picsauditing.gwt.shared.GetLoginLogRequest;



@SuppressWarnings("serial")
public class Trevor extends PicsActionSupport {
	
	@Override
	public String execute() throws Exception {

		final GetLoginLogRequest getLoginLogRequest = new GetLoginLogRequest();
		
		getLoginLogRequest.startIndex = 3;
		
		System.out.println(getLoginLogRequest.startIndex);
		
		return BLANK;
	}
}
