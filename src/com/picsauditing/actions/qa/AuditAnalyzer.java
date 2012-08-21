package com.picsauditing.actions.qa;

import java.sql.SQLException;

import com.picsauditing.search.SelectSQL;

public class AuditAnalyzer extends Analyzer {

	private TabularModel auditDiffData;
	private TabularModel caoDiffData;

	public AuditAnalyzer() {
	}

	public AuditAnalyzer(String leftDatabase, String rightDatabase) {
		super(leftDatabase, rightDatabase);
	}

	public void run() throws SQLException {
		QueryRunner analysis = QueryRunnerFactory.instance(buildQueryForAuditsCreatedInLeftNotCreatedInRight());
		auditDiffData = analysis.run();
		auditDiffData.setColumnEntityName(1, "Contractor");
		auditDiffData.setColumnEntityName(2, "Audit");

		QueryRunner analysis2 = QueryRunnerFactory.instance(buildQueryForCAOsCreatedInLeftNotCreatedInRight());
		caoDiffData = analysis2.run();
		caoDiffData.setColumnEntityName(1, "Contractor");
		caoDiffData.setColumnEntityName(2, "Operator");
		caoDiffData.setColumnEntityName(3, "Audit");
	}

	private SelectSQL buildQueryForAuditsCreatedInLeftNotCreatedInRight() {
		SelectSQL selectSQL = new SelectSQL(leftDatabase + ".contractor_audit ca1 ");
		selectSQL.addField("ca1.conID as `Contractor`");
		selectSQL.addField("ca1.id as `Audit`");
		selectSQL.addField("ca1.auditTypeID as `" + leftLabel + " AuditType`");
		selectSQL.addJoin("JOIN " + leftDatabase + ".accounts a on a.id = ca1.conID");
		selectSQL.addJoin("LEFT JOIN " + rightDatabase
				+ ".contractor_audit ca2 on ca2.auditTypeID = ca1.auditTypeID and ca2.conID = ca1.conID");
		selectSQL.addWhere("a.status = 'Active'");
		selectSQL.addWhere("ca1.creationDate > DATE_FORMAT(@now:=now(),'%Y-%m-%e')");
		selectSQL.addWhere("a.creationDate < DATE_FORMAT(DATE_SUB(now(), INTERVAL 1 DAY),'%Y-%m-%e-12-00')");
		selectSQL.addWhere("(ca2.auditTypeID is null OR ca2.conID is null)");
		selectSQL.addWhere("ca1.expiresDate > @now");
		selectSQL.addOrderBy("ca1.conID");

		return selectSQL;
	}

	private SelectSQL buildQueryForCAOsCreatedInLeftNotCreatedInRight() {
		SelectSQL innerSelect = new SelectSQL(rightDatabase + ".contractor_audit ca2");
		innerSelect.addField("ca2.auditTypeID");
		innerSelect.addField("ca2.conID");
		innerSelect.addField("cao2.opID");
		innerSelect.addJoin("JOIN " + rightDatabase + ".contractor_audit_operator cao2 on cao2.auditID = ca2.id");
		innerSelect.addWhere("cao2.creationDate > DATE_FORMAT(@now:=now(),'%Y-%m-%e')");

		SelectSQL selectSQL = new SelectSQL(leftDatabase + ".contractor_audit_operator cao1");
		selectSQL.addField("cao1.id as `CAO`");
		selectSQL.addField("ca1.conID as `Contractor`");
		selectSQL.addField("cao1.opID as `Operator`");
		selectSQL.addField("ca1.id as `Audit`");
		selectSQL.addField("ca1.auditTypeID as `Audit Type`");
		selectSQL.addJoin("JOIN " + leftDatabase + ".contractor_audit ca1 on ca1.id = cao1.auditID");
		selectSQL.addJoin("JOIN " + leftDatabase + ".accounts a on a.id = ca1.conID");
		selectSQL.addJoin("LEFT JOIN (" + innerSelect.toString()
				+ ") ca3 on ca3.auditTypeID = ca1.auditTypeID and ca3.conID = ca1.conID");
		selectSQL.addWhere("a.status = 'Active' ");
		selectSQL.addWhere("ca1.expiresDate > @now");
		selectSQL.addWhere("cao1.creationDate > DATE_FORMAT(@now,'%Y-%m-%e')");
		selectSQL.addWhere("ca3.opID is null");

		return selectSQL;
	}

	public TabularModel getAuditDiffData() {
		return auditDiffData;
	}

	public TabularModel getCaoDiffData() {
		return caoDiffData;
	}

}
