package com.picsauditing.actions.report;

import com.picsauditing.access.OpPerms;
import com.picsauditing.search.SelectSQL;

@SuppressWarnings("serial")
public class ReportExceptions extends ReportActionSupport {
	private SelectSQL selectSQL = new SelectSQL();

	/**
	 * Return the sql clause in this format:
	 * 
	 * SELECT {fields<String>} FROM {fromTable} [{joinClause<String>}] [WHERE
	 * {whereClause}] [GROUP BY {groupByFields<String>] [HAVING {havingClause}]
	 * [ORDER BY {orderBys<String>} [LIMIT {limit}|LIMIT {startRow}, {limit}]
	 */
	protected void buildQuery() {
		selectSQL.setFromTable("app_error_log AS el");
		selectSQL.addField("el.id");
		selectSQL.addField("el.category");
		selectSQL.addField("el.priority");
		selectSQL.addField("el.status");
		selectSQL.addField("el.createdBy");
		selectSQL.addField("el.updatedBy");
		selectSQL.addField("el.creationDate");
		selectSQL.addField("el.updateDate");
		selectSQL.addField("el.message");
		selectSQL.addOrderBy("creationDate DESC");
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		getPermissions();
		tryPermissions(OpPerms.DevelopmentEnvironment);

		buildQuery();
		run(selectSQL);

		return SUCCESS;
	}
}
