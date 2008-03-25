package com.picsauditing.actions;

import java.util.List;

import com.opensymphony.xwork2.ActionSupport;
import com.picsauditing.dao.RulesRowDAO;
import com.picsauditing.jpa.entities.RulesRow;

public class RulesAction extends ActionSupport {
	protected List<RulesRow> rulesRows = null;
	protected RulesRowDAO dao = null;

	public RulesAction(RulesRowDAO dao) {
		this.dao = dao;
	}

	public String execute() throws Exception {
		rulesRows = dao.findAll();

		return SUCCESS;
	}

	public List<RulesRow> getRulesRows() {
		return rulesRows;
	}

	public void setRulesRows(List<RulesRow> rulesRows) {
		this.rulesRows = rulesRows;
	}


}
