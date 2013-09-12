package com.picsauditing.actions.users;

import com.picsauditing.actions.report.ReportActionSupport;
import com.picsauditing.search.SelectAccount;

@SuppressWarnings("serial")
public class ManageMultiUser extends ReportActionSupport {

	protected SelectAccount sql = new SelectAccount();

	public SelectAccount getSql() {
		return sql;
	}

	public void setSql(SelectAccount sql) {
		this.sql = sql;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		sql.addField("u.name as contact");
		sql.setFromTable("loginlog l");
		sql.addField("count(DISTINCT l.remoteAddress) as count");
		sql.addField("u.id as userId");
		sql.addJoin("join users u on u.id = l.userID");
		sql.addJoin("join accounts a on u.accountID = a.id");
		sql.addWhere("l.loginDate > SUBDATE(now(), INTERVAL 3 MONTH )");
		sql.addWhere("l.adminID is NULL");
		sql.addGroupBy("u.id");
		sql.addHaving("count(DISTINCT l.remoteAddress) > 3");

		this.run(sql);
		return SUCCESS;
	}
}
