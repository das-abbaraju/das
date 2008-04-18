package com.picsauditing.actions.report;

import com.picsauditing.search.Report;
import com.picsauditing.search.SelectAccount;

public class ReportAccountNCMS extends ReportAccount {

	public String execute() throws Exception {
		// this.getPermissions();
		// permissions.tryPermission(OpPerms.NCMS);

		sql
				.addJoin("JOIN ncms_desktop d ON (c.taxID = d.fedTaxID AND c.taxID != '') OR a.name=d.ContractorsName");
		sql
				.addWhere("a.id IN ( "
						+ "SELECT a.id FROM accounts a JOIN ncms_desktop d ON a.name = d.ContractorsName WHERE d.remove = 'No' "
						+ "UNION "
						+ "SELECT c.id FROM contractor_info c JOIN ncms_desktop d ON c.taxID = d.fedTaxID WHERE d.remove = 'No' "
						+ ") ");
		sql.addField("c.taxID");
		sql.addField("d.fedTaxID");
		sql.addField("d.ContractorsName");
		sql.addField("d.lastReview");

		return super.execute();
	}
}
