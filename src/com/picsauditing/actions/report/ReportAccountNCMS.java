package com.picsauditing.actions.report;

import com.picsauditing.access.OpPerms;
import com.picsauditing.search.SelectAccount;

@SuppressWarnings("serial")
public class ReportAccountNCMS extends ReportAccount {

	@Override
	public void checkPermissions() throws Exception {
		permissions.tryPermission(OpPerms.NCMS);
	}

	@Override
	public void buildQuery() {
		super.buildQuery();
		sql.addJoin("JOIN ncms_desktop d " + "ON (c.taxID = d.fedTaxID AND c.taxID != '') OR a.name=d.ContractorsName");
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
	}
}
