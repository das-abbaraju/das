package com.picsauditing.actions.report;

import com.picsauditing.search.SelectContractorAudit;

@SuppressWarnings("serial")
public class ReportWcbAccounts extends ReportAccount {
	
	public void prepare() throws Exception {
		super.prepare();
		
		getFilter().setShowAccountName(false);
		getFilter().setShowCcOnFile(false);
		getFilter().setShowService(false);
		getFilter().setShowState(false);
		getFilter().setShowRegistrationDate(false);
		getFilter().setShowConWithPendingAudits(false);
		getFilter().setShowPrimaryInformation(false);
		getFilter().setShowTradeInformation(false);
		getFilter().setShowSoleProprietership(false);
	}

	public void buildQuery() {
		sql = new SelectContractorAudit();
		sql.addField("p.answer as wcbAccountNumber");
		sql.addField("CASE ca.auditTypeID "+
				"WHEN 143 THEN 'MB' "+
				"WHEN 144 THEN 'NL' "+
				"WHEN 145 THEN 'AB' "+
				"WHEN 146 THEN 'BC' "+
				"WHEN 147 THEN 'SK' "+
				"WHEN 148 THEN 'PE' "+
				"WHEN 166 THEN 'ON' "+
				"WHEN 167 THEN 'QC' "+
				"WHEN 168 THEN 'NS' "+
				"WHEN 169 THEN 'YT' "+
				"WHEN 170 THEN 'NT' "+
				"WHEN 261 THEN 'NB' "+
				"  END AS province");
		sql.addField("cao.creationDate");
		sql.addJoin("JOIN contractor_audit_operator cao ON cao.auditID = ca.id");
		sql.addJoin("JOIN pqfData p ON cao.auditID = p.auditID");
		sql.addWhere("ca.auditTypeID in(143,144,145,146,147,148,166,167,168,169,170,261)");
		sql.addWhere("cao.status IN('Complete','Pending','Submitted')");
		sql.addWhere("cao.visible = 1");
		sql.addWhere("p.questionID IN(8129, 8063, 8070, 8077, 8089, 8142, 8147, 1818, 1819, 1820, 1816, 1868)");

		orderByDefault = "WCBAccountNumber, a.name";
		filteredDefault = true;
		
		addFilterToSQL();
	}
}