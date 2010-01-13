package com.picsauditing.actions.report;

import com.picsauditing.search.SelectSQL;

@SuppressWarnings("serial")
public class ReportNewRequestedContractor extends ReportActionSupport {

	protected SelectSQL sql = new SelectSQL();

	public SelectSQL getSql() {
		return sql;
	}

	public void setSql(SelectSQL sql) {
		this.sql = sql;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		
		sql.setFromTable("contractor_registration_request cr");
		sql.addJoin("JOIN accounts a ON a.id = cr.requestedByID");
		sql.addJoin("LEFT JOIN users u ON u.id = cr.requestedByUserID");
		sql.addJoin("LEFT JOIN users uc ON uc.id = cr.lastContactedBy");
		sql.addJoin("LEFT JOIN accounts con ON con.id = cr.conID");
		
		sql.addField("cr.id");
		sql.addField("cr.name");
		sql.addField("a.name AS RequestedBy");
		sql.addField("u.name AS RequestedUser");
		sql.addField("cr.deadline");
		sql.addField("uc.name AS ContactedBy");
		sql.addField("cr.lastContactDate");
		sql.addField("cr.contactCount");
		sql.addField("cr.matchCount");
		sql.addField("con.id AS conID");
		sql.addField("con.name AS contractorName");

		this.run(sql);

		return SUCCESS;
	}
}
