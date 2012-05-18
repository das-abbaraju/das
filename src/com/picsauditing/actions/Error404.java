package com.picsauditing.actions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opensymphony.xwork2.ActionContext;

@SuppressWarnings("serial")
public class Error404 extends PicsActionSupport {
	private final Logger logger = LoggerFactory.getLogger(Error404.class);
	@Override
	public String execute() {
		logger.error("I'm in the execute of Error404.java!");
		addActionError("Moo");
		return SUCCESS;
	}
}
