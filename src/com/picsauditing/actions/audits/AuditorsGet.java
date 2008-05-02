package com.picsauditing.actions.audits;

import java.util.List;

import com.opensymphony.xwork2.ActionSupport;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.User;

public class AuditorsGet extends ActionSupport
{
	protected UserDAO dao = null;
	protected List<User> auditors = null;
	protected boolean shouldIncludeHeader = false;
	protected String controlName = "auditor.id";
	
	public AuditorsGet( UserDAO dao )
	{
		this.dao = dao;
	}
	
	public String execute()
	{
		this.auditors = dao.findAuditors();
		return SUCCESS;
	}

	public boolean isShouldIncludeHeader() {
		return shouldIncludeHeader;
	}

	public void setShouldIncludeHeader(boolean shouldIncludeHeader) {
		this.shouldIncludeHeader = shouldIncludeHeader;
	}

	public String getControlName() {
		return controlName;
	}

	public void setControlName(String controlName) {
		this.controlName = controlName;
	}

	public List<User> getAuditors() {
		return auditors;
	}

	public void setAuditors(List<User> auditors) {
		this.auditors = auditors;
	}
	
	
}
