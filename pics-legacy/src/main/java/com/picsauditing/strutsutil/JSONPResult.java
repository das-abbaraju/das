package com.picsauditing.strutsutil;

import java.io.ByteArrayInputStream;
import java.io.StringBufferInputStream;

import org.apache.struts2.dispatcher.StreamResult;
import org.json.simple.JSONAware;

import com.opensymphony.xwork2.ActionInvocation;

/**
 * @see com.picsauditing.strutsutil.JSONResult
 * 
 * @author kpartridge
 *
 */
public class JSONPResult extends StreamResult {

	private static final long serialVersionUID = 7789432829226367722L;

	@Override
	protected void doExecute(String finalLocation, ActionInvocation invocation) throws Exception {

		JSONAware json = (JSONAware) invocation.getStack().findValue("json");
		String callback = invocation.getStack().findString("callback");

		try {
			StringBuffer sb = new StringBuffer(callback).append("(").append(json.toJSONString()).append(")");

			inputStream = new ByteArrayInputStream(sb.toString().getBytes());

			contentType = "text/javascript";
		} catch (NullPointerException e) {
			inputStream = new ByteArrayInputStream("missing no. (ex: callback=?)".getBytes());
		}
		super.doExecute(finalLocation, invocation);
	}
}
