package com.picsauditing.actions;

import org.apache.struts2.ServletActionContext;

@SuppressWarnings("serial")
public class Trevor extends PicsActionSupport {
	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		
		ServletActionContext.getResponse().sendRedirect("images/ReportAccountAudits.csv");
		return BLANK;
	}
}
