package com.picsauditing.actions.audits;

import java.util.List;

import com.opensymphony.xwork2.ActionSupport;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.util.AuditorCache;

public class AuditorsGet extends ActionSupport
{
	protected UserDAO dao = null;
	protected List<User> auditors = null;
	protected String controlName = "auditor.id";
	protected int[] presetValue;
	
	public AuditorsGet( UserDAO dao )
	{
		this.dao = dao;
	}
	
	public String execute()
	{
		this.auditors = new AuditorCache( dao ).getList();
		return SUCCESS;
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

	public int[] getPresetValue() {
		return presetValue;
	}

	public void setPresetValue(int[] presetValue) {
		this.presetValue = presetValue;
	}
	
	
}
