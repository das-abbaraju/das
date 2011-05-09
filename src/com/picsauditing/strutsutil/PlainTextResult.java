package com.picsauditing.strutsutil;

import java.io.ByteArrayInputStream;

import org.apache.catalina.ssi.ByteArrayServletOutputStream;
import org.apache.struts2.dispatcher.StreamResult;

import com.opensymphony.xwork2.ActionInvocation;
import com.picsauditing.actions.PicsActionSupport;

public class PlainTextResult extends StreamResult {

	private static final long serialVersionUID = 6352738726784889399L;

	@Override
	protected void doExecute(String finalLocation, ActionInvocation invocation)
			throws Exception {

		String output = invocation.getStack().findString("output");
		inputStream = new ByteArrayInputStream(output.getBytes());
		super.doExecute(finalLocation, invocation);
	}
}
