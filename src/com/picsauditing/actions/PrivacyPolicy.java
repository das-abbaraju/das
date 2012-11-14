package com.picsauditing.actions;

import com.picsauditing.access.Anonymous;
import com.picsauditing.strutsutil.AjaxUtils;

@SuppressWarnings("serial")
public class PrivacyPolicy extends PicsActionSupport {

	@Override
	@Anonymous
	public String execute() {
		if (AjaxUtils.isAjax(getRequest())) {
			return "privacy-policy";
		}

		return SUCCESS;
	}
}
