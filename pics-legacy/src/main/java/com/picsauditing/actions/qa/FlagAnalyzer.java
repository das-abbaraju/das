package com.picsauditing.actions.qa;

import java.sql.SQLException;

import com.picsauditing.search.SelectSQL;

public class FlagAnalyzer extends Analyzer {

	private TabularModel flagDiffData;
	private TabularModel flagDiffCaoStatus;

	public FlagAnalyzer() {
	}

	public FlagAnalyzer(String leftDatabase, String rightDatabase) {
		super(leftDatabase, rightDatabase);
	}

	public void run() throws SQLException {
		QueryRunner analysis = QueryRunnerFactory.instance(buildInitialQuery());
		flagDiffData = analysis.run();
		flagDiffData.setColumnEntityName(1, "Contractor");
		flagDiffData.setColumnEntityName(2, "Operator");

		QueryRunner analysis3 = QueryRunnerFactory.instance(buildFlagDiffCaoStatusQuery());
		flagDiffCaoStatus = analysis3.run();
		flagDiffCaoStatus.setColumnEntityName(1, "Contractor");
	}

	private SelectSQL buildInitialQuery() {
		SelectSQL selectSQL = new SelectSQL(leftDatabase + ".contractor_operator co1");
		selectSQL.addField("co1.conID as `Contractor`");
		selectSQL.addField("co1.opID as `Operator`");
		selectSQL.addField("co1.flag as `" + leftLabel + " Flag`");
		selectSQL.addField("co1.baselineFlag as `" + leftLabel + " Old Flag`");
		selectSQL.addField("co1.flagLastUpdated as `" + leftLabel + " FlagLastUpdated`");
		selectSQL.addField("co2.flag as `" + rightLabel + " Flag`");
		selectSQL.addField("co2.baselineFlag as `" + rightLabel + " Old Flag`");
		selectSQL.addField("co2.flagLastUpdated as `" + rightLabel + " FlagLastUpdated`");
		selectSQL.addJoin("JOIN " + leftDatabase + ".accounts a ON a.id = co1.opID ");
		selectSQL.addJoin("JOIN " + rightDatabase + ".contractor_operator co2 on co2.id = co1.id");
		selectSQL.addWhere("co1.flagLastUpdated > DATE_FORMAT(@now:=now(),'%Y-%m-%e')  ");
		selectSQL.addWhere("co2.flagLastUpdated > DATE_FORMAT(@now,'%Y-%m-%e') ");
		selectSQL.addWhere("co1.flag <> co2.flag");
		selectSQL.addWhere("co1.flag <> co1.baselineFlag");
		selectSQL.addWhere("a.status = 'Active'");
		selectSQL.addOrderBy("co1.conID");
		selectSQL.addOrderBy("co1.opID");
		return selectSQL;
	}

	private SelectSQL buildFlagDiffCaoStatusQuery() {
		SelectSQL selectSQL = new SelectSQL(leftDatabase + ".contractor_operator co1");
		selectSQL.addField("ca1.conID as `Contractor`");
		selectSQL.addField("cao1.status as `" + leftLabel + " CAO Status`");
		selectSQL.addField("cao1.flag as `" + leftLabel + " CAO Flag`");
		selectSQL.addField("cao1.percentVerified as `" + leftLabel + " CAO Percent Verified`");
		selectSQL.addField("cao1.percentComplete as `" + leftLabel + " CAO Percent Complete`");
		selectSQL.addField("cao1.statusChangedDate as `" + leftLabel + " CAO Status Changed`");
		selectSQL.addField("cao2.status as `" + rightLabel + " CAO Status`");
		selectSQL.addField("cao2.flag as `" + rightLabel + " CAO Flag`");
		selectSQL.addField("cao2.percentVerified as `" + rightLabel + " CAO Percent Verified`");
		selectSQL.addField("cao2.percentComplete as `" + rightLabel + " CAO Percent Complete`");
		selectSQL.addField("cao2.statusChangedDate as `" + rightLabel + " CAO Status Changed`");
		selectSQL.addJoin("JOIN " + leftDatabase + ".contractor_audit ca1 on ca1.conID = co1.conID");
		selectSQL.addJoin("JOIN " + leftDatabase + ".accounts a on a.id = ca1.conID");
		selectSQL.addJoin("JOIN " + leftDatabase
				+ ".contractor_audit_operator cao1 on co1.opID = cao1.opID and ca1.id = cao1.auditID");
		selectSQL.addJoin("JOIN " + rightDatabase + ".contractor_audit_operator cao2 on cao2.id = cao1.id");
		selectSQL.addJoin("JOIN " + rightDatabase + ".contractor_operator co2 on co2.id = co1.id");
		selectSQL.addWhere("co1.flagLastUpdated > DATE_FORMAT(@now:=now(),'%Y-%m-%e')");
		selectSQL.addWhere("co1.flag <> co2.flag");
		selectSQL.addWhere("co1.flag <> co1.baselineFlag");
		selectSQL.addWhere("a.status = 'Active'");
		selectSQL
				.addWhere("(cao1.statusChangedDate > date_sub(now(), INTERVAL 1 DAY) OR cao2.statusChangedDate > date_sub(now(), INTERVAL 1 DAY))");
		selectSQL.addWhere("cao1.creationDate < DATE_FORMAT(DATE_SUB(now(), INTERVAL 1 DAY),'%Y-%m-%e-12-00')");
		selectSQL.addWhere("co1.creationDate < DATE_FORMAT(DATE_SUB(now(), INTERVAL 1 DAY),'%Y-%m-%e-12-00')");
		selectSQL.addWhere("ca1.creationDate < DATE_FORMAT(DATE_SUB(now(), INTERVAL 1 DAY),'%Y-%m-%e-12-00')");
		selectSQL.addWhere("ca1.expiresDate > @now");
		selectSQL.addWhere("co2.flagLastUpdated > DATE_FORMAT(@now:=now(),'%Y-%m-%e')");
		return selectSQL;
	}

	public TabularModel getFlagDiffData() {
		return flagDiffData;
	}

	public TabularModel getFlagDiffCaoStatus() {
		return flagDiffCaoStatus;
	}

}
