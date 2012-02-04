package com.picsauditing.strutsutil;

import java.io.ByteArrayInputStream;

import org.apache.struts2.dispatcher.StreamResult;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.google.common.base.Joiner;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionSupport;

/**
 * Custom result used to return JSON Arrays to the browser using the proper contentType.
 * 
 * @author lkam 
 * 
 */
public class JSONArrayResult extends StreamResult {

	private static final long serialVersionUID = 7789432829226367722L;

	@SuppressWarnings("unchecked")
	@Override
	protected void doExecute(String finalLocation, ActionInvocation invocation) throws Exception {

		JSONArray jsonArray = (JSONArray) invocation.getStack().findValue("jsonArray");

		/*
		 * Add in the action messages/errors if there are any.
		 */
		ActionSupport action = (ActionSupport) invocation.getAction();
		if (action.hasActionMessages() || action.hasActionErrors()) {
			if (action.hasActionMessages())
				jsonArray.add(new JSONObject().put("actionMessage", Joiner.on("\n").join(action.getActionMessages())));
			if (action.hasActionErrors())
				jsonArray.add(new JSONObject().put("actionError", Joiner.on("\n").join(action.getActionErrors())));
		}

		inputStream = new ByteArrayInputStream(jsonArray.toJSONString().getBytes());
		contentType = "application/json";
		super.doExecute(finalLocation, invocation);
	}
}
