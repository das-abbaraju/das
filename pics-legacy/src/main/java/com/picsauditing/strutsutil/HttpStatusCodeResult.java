package com.picsauditing.strutsutil;

import com.opensymphony.xwork2.ActionInvocation;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.StreamResult;

public class HttpStatusCodeResult extends StreamResult {

	@Override
	protected void doExecute(String finalLocation, ActionInvocation invocation) throws Exception {
		int httpStatusCode = (int) invocation.getStack().findValue("httpStatusCode");

		ServletActionContext.getResponse().setStatus(httpStatusCode);

		super.doExecute(finalLocation, invocation);
	}

}
