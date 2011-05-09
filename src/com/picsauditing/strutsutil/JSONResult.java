package com.picsauditing.strutsutil;

import java.io.ByteArrayInputStream;

import org.apache.struts2.dispatcher.StreamResult;
import org.json.simple.JSONAware;

import com.opensymphony.xwork2.ActionInvocation;

public class JSONResult extends StreamResult {

	private static final long serialVersionUID = 7789432829226367722L;

	@Override
	protected void doExecute(String finalLocation, ActionInvocation invocation) throws Exception {

		JSONAware json = (JSONAware) invocation.getStack().findValue("json");
		inputStream = new ByteArrayInputStream(json.toJSONString().getBytes());
		contentType = "application/json";
		super.doExecute(finalLocation, invocation);
	}
}
