package com.picsauditing.actions.qa;

import java.sql.SQLException;

import com.picsauditing.search.SelectSQL;

public class AuditAnalyzer extends Analyzer {

	private TabularModel auditDiffData;
	private TabularModel caoDiffData;
	private TabularModel auditForDiffData;

	public AuditAnalyzer() {
	}

	public AuditAnalyzer(String leftDatabase, String rightDatabase) {
		super(leftDatabase, rightDatabase);
	}

	public void run() throws SQLException {
		QueryRunner analysis = QueryRunnerFactory.instance(buildQueryForAuditsCreatedInLeftNotCreatedInRight());
		auditDiffData = analysis.run();

		QueryRunner analysis2 = QueryRunnerFactory.instance(buildQueryForCAOsCreatedInLeftNotCreatedInRight());
		caoDiffData = analysis2.run();

		QueryRunner analysis3 = QueryRunnerFactory.instance(buildQueryForAuditsWhereAuditForYearsNotEqual());
		auditForDiffData = analysis3.run();
	}

	private SelectSQL buildQueryForAuditsCreatedInLeftNotCreatedInRight() {
		SelectSQL selectSQL = new SelectSQL(leftDatabase + ".contractor_audit ca1 ");
		selectSQL.addField("ca1.conID as `Contractor`");
		selectSQL.addField("ca1.auditTypeID `" + leftLabel + " AuditType`");
		selectSQL.addJoin("LEFT JOIN " + rightDatabase
				+ ".contractor_audit ca2 on ca2.auditTypeID = ca1.auditTypeID and ca2.conID = ca1.conID");
		selectSQL.addWhere("(ca2.auditTypeID is null OR ca2.conID is null)");
		selectSQL.addWhere("ca1.creationDate > date_sub(now(), INTERVAL 1 DAY)");
		selectSQL.addOrderBy("ca1.conID");
		return selectSQL;
	}

	private SelectSQL buildQueryForCAOsCreatedInLeftNotCreatedInRight() {
		SelectSQL innerSelect = new SelectSQL(rightDatabase + ".contractor_audit ca2");
		innerSelect.addField("ca2.auditTypeID");
		innerSelect.addField("ca2.conID");
		innerSelect.addField("cao2.opID");
		innerSelect.addJoin("JOIN " + rightDatabase + ".contractor_audit_operator cao2 on cao2.auditID = ca2.id");
		innerSelect.addWhere("cao2.creationDate > date_sub(now(), INTERVAL 1 DAY)");

		SelectSQL selectSQL = new SelectSQL(leftDatabase + ".contractor_audit_operator cao1");
		selectSQL.addField("cao1.id as `CAO`");
		selectSQL.addField("ca1.conID as `Contractor`");
		selectSQL.addField("cao1.opID as `Operator`");
		selectSQL.addField("ca1.auditTypeID as `Audit Type`");
		selectSQL.addJoin("JOIN " + leftDatabase + ".contractor_audit ca1 on ca1.id = cao1.auditID");
		selectSQL.addJoin("LEFT JOIN (" + innerSelect.toString()
				+ ") ca3 on ca3.auditTypeID = ca1.auditTypeID and ca3.conID = ca1.conID");
		selectSQL.addWhere("cao1.creationDate > date_sub(now(), INTERVAL 1 DAY)");
		selectSQL.addWhere("ca3.opID is null");
		return selectSQL;
	}

	private SelectSQL buildQueryForAuditsWhereAuditForYearsNotEqual() {
		SelectSQL selectSQL = new SelectSQL(leftDatabase + ".contractor_audit ca1 ");
		selectSQL.addField("ca1.conID as `Contractor`");
		selectSQL.addField("ca1.id as `Contractor Audit`");
		selectSQL.addField("ca1.auditTypeID `" + leftLabel + " AuditType`");
		selectSQL.addField("ca1.auditFor `" + leftLabel + " Audit For`");
		selectSQL.addField("ca2.auditTypeID `" + rightLabel + " AuditType`");
		selectSQL.addField("ca2.auditFor `" + rightLabel + " Audit For`");
		selectSQL.addJoin("LEFT JOIN " + rightDatabase
				+ ".contractor_audit ca2 on ca2.auditTypeID = ca1.auditTypeID and ca2.conID = ca1.conID");
		selectSQL.addWhere("ca1.auditFor <> ca2.auditFor");
		selectSQL.addWhere("ca1.creationDate > date_sub(now(), INTERVAL 1 DAY)");
		selectSQL.addOrderBy("ca1.conID");
		return selectSQL;
	}

	public TabularModel getAuditDiffData() {
		return auditDiffData;
	}

	public TabularModel getCaoDiffData() {
		return caoDiffData;
	}

	public TabularModel getAuditForDiffData() {
		return auditForDiffData;
	}

}
