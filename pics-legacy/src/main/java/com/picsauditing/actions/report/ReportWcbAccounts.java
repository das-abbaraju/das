package com.picsauditing.actions.report;

import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.search.SelectContractorAudit;
import com.picsauditing.util.excel.ExcelColumn;

@SuppressWarnings("serial")
public class ReportWcbAccounts extends ReportContractorAuditOperator {
	
	public void prepare() throws Exception {
		super.prepare();
		
		getFilter().setShowAccountName(false);
		getFilter().setShowCcOnFile(false);
		getFilter().setShowService(false);
		getFilter().setShowLocation(false);
		getFilter().setShowRegistrationDate(false);
		getFilter().setShowConWithPendingAudits(false);
		getFilter().setShowPrimaryInformation(false);
		getFilter().setShowTradeInformation(false);
		getFilter().setShowSoleProprietership(false);
		getFilter().setShowAuditStatus(true);
		getFilter().setAuditStatus(new AuditStatus[]{AuditStatus.Submitted});
		
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
		sql.addWhere("cao.visible = 1");
		sql.addWhere("p.questionID IN(8077,8129,8063,8070,8175,8168,8154,8161,8147,8183,8142,8089)");

		orderByDefault = "WCBAccountNumber, a.name";
		filteredDefault = true;
		
		addFilterToSQL();
	}

	@Override
	public void addExcelColumns() {
		excelSheet.setData(data);
		excelSheet.addColumn(new ExcelColumn("wcbAccountNumber", "WCB Account Number"));
		excelSheet.addColumn(new ExcelColumn("name", "Contractor Name"));
		excelSheet.addColumn(new ExcelColumn("id", "PICS Account Number"));
		excelSheet.addColumn(new ExcelColumn("province", "WCB Province"));
		excelSheet.addColumn(new ExcelColumn("creationDate", "Date"));
	}
}