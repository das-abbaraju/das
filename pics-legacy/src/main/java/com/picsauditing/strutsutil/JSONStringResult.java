package com.picsauditing.strutsutil;

import com.opensymphony.xwork2.ActionInvocation;
import org.apache.struts2.dispatcher.StreamResult;

import java.io.ByteArrayInputStream;

public class JSONStringResult extends StreamResult {

	@Override
	protected void doExecute(String finalLocation, ActionInvocation invocation) throws Exception {
		String json = (String) invocation.getStack().findValue("jsonString");

		inputStream = new ByteArrayInputStream(json.getBytes("UTF-8"));
		contentType = "application/json";
		super.doExecute(finalLocation, invocation);
	}
}
