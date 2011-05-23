package com.picsauditing.strutsutil;

import java.io.ByteArrayInputStream;

import org.apache.struts2.dispatcher.StreamResult;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

import com.google.common.base.Joiner;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionSupport;

/**
 * Custom result used to return JSON to the browser using the proper contentType.
 * 
 * @author kpartridge
 * 
 */
public class JSONResult extends StreamResult {

	private static final long serialVersionUID = 7789432829226367722L;

	@SuppressWarnings("unchecked")
	@Override
	protected void doExecute(String finalLocation, ActionInvocation invocation) throws Exception {

		JSONObject json = (JSONObject) invocation.getStack().findValue("json");

		/*
		 * Add in the action messages/errors if there are any.
		 */
		ActionSupport action = (ActionSupport) invocation.getAction();
		if (action.hasActionMessages() || action.hasActionErrors()) {
			if (action.hasActionMessages())
				json.put("actionMessage", Joiner.on("\n").join(action.getActionMessages()));
			if (action.hasActionErrors())
				json.put("actionError", Joiner.on("\n").join(action.getActionErrors()));
		}

		inputStream = new ByteArrayInputStream(json.toJSONString().getBytes());
		contentType = "application/json";
		super.doExecute(finalLocation, invocation);
	}
}
