package com.picsauditing.actions.operators.gc;

import com.picsauditing.actions.report.ReportActionSupport;
import com.picsauditing.search.SelectSQL;

@SuppressWarnings("serial")
public class GeneralContractorsList extends ReportActionSupport {
	private SelectSQL sql = new SelectSQL("facilities f");

	@Override
	public String execute() throws Exception {
		buildQuery();

		run(sql);

		return SUCCESS;
	}

	protected void buildQuery() {
		sql.addJoin("JOIN accounts a ON a.id = f.opID AND a.status = 'Active' AND a.generalContractor = 1");

		sql.addWhere("f.corporateID = " + permissions.getAccountId());
		sql.addWhere("f.type = 'GeneralContractor'");

		sql.addField("a.id");
		sql.addField("a.name");
	}
}
