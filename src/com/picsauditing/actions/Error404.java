package com.picsauditing.actions;

import com.opensymphony.xwork2.ActionContext;

@SuppressWarnings("serial")
public class Error404 extends PicsActionSupport {
	@Override
	public String execute() {
		System.out.println("I'm in the execute of Error404.java!");
		addActionError("Moo");
		return SUCCESS;
	}
}
