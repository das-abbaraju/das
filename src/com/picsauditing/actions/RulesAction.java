package com.picsauditing.actions;

import java.util.List;

import com.opensymphony.xwork2.ActionSupport;
import com.picsauditing.dao.RulesRowDAO;
import com.picsauditing.beans.RulesRowBean;

public class RulesAction extends ActionSupport {
	protected List<RulesRowBean> rulesRows = null;
	protected RulesRowDAO dao = null;

	public RulesAction(RulesRowDAO dao) {
		this.dao = dao;
	}

	public String execute() throws Exception {
		rulesRows = dao.findAll();

		return SUCCESS;
	}

	public List<RulesRowBean> getRulesRows() {
		return rulesRows;
	}

	public void setRulesRows(List<RulesRowBean> rulesRows) {
		this.rulesRows = rulesRows;
	}


}
