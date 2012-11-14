package com.picsauditing.actions;

import com.picsauditing.access.Anonymous;
import com.picsauditing.strutsutil.AjaxUtils;

public class About extends PicsActionSupport {

	private static final long serialVersionUID = 1L;

	@Override
	@Anonymous
	public String execute() {
		if (AjaxUtils.isAjax(getRequest())) {
			return "partial";
		}
		
		return SUCCESS;
	}
	
}