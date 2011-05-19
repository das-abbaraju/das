package com.picsauditing.strutsutil;

import java.io.ByteArrayInputStream;

import org.apache.struts2.dispatcher.StreamResult;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionSupport;

/**
 * This is a result that uses the struts {@link org.apache.struts2.dispatcher.StreamResult} for returning plain text
 * results.
 * 
 * It uses the output paramater from the value stack. This will make it available for all sub-classes of
 * {@link com.picsauditing.actions.PicsActionSupport}
 * 
 * @author kpartridge
 * 
 */
public class MessageResult extends StreamResult {

	private static final long serialVersionUID = -9041792943829277043L;

	@Override
	protected void doExecute(String finalLocation, ActionInvocation invocation) throws Exception {

		ActionSupport action = (ActionSupport) invocation.getAction();
		StringBuilder sb = new StringBuilder();

		for (String message : action.getActionMessages()) {
			sb.append("INFO: ").append(message).append("\n");
		}

		for (String error : action.getActionErrors()) {
			sb.append("ERROR: ").append(error).append("\n");
		}

		inputStream = new ByteArrayInputStream(sb.toString().getBytes());
		
		super.doExecute(finalLocation, invocation);
	}
}
