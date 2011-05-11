package com.picsauditing.strutsutil;

import java.io.ByteArrayInputStream;

import org.apache.struts2.dispatcher.StreamResult;

import com.opensymphony.xwork2.ActionInvocation;

/**
 * This is a result that uses the struts {@link org.apache.struts2.dispatcher.StreamResult}
 * for returning plain text results.
 * 
 * It uses the output paramater from the value stack. This will make it available for all
 * sub-classes of {@link com.picsauditing.actions.PicsActionSupport}
 * 
 * @author kpartridge
 *
 */
public class PlainTextResult extends StreamResult {

	private static final long serialVersionUID = 6352738726784889399L;

	@Override
	protected void doExecute(String finalLocation, ActionInvocation invocation) throws Exception {

		String output = invocation.getStack().findString("output");
		inputStream = new ByteArrayInputStream(output.getBytes());
		super.doExecute(finalLocation, invocation);
	}
}
