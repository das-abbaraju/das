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

		QueryRunner analysis3 = QueryRunnerFactory.instance(buildFlagDiffCaoStatusQuery());
		flagDiffCaoStatus = analysis3.run();
	}

	private SelectSQL buildInitialQuery() {
		SelectSQL selectSQL = new SelectSQL(leftDatabase + ".generalcontractors gc1");
		selectSQL.addField("gc1.subID as `Contractor`");
		selectSQL.addField("gc1.genID as `Operator`");
		selectSQL.addField("gc1.flag as `" + leftLabel + " Flag`");
		selectSQL.addField("gc1.baselineFlag as `" + leftLabel + " Old Flag`");
		selectSQL.addField("gc1.flagLastUpdated as `" + leftLabel + " FlagLastUpdated`");
		selectSQL.addField("gc2.flag as `" + rightLabel + " Flag`");
		selectSQL.addField("gc2.baselineFlag as `" + rightLabel + " Old Flag`");
		selectSQL.addField("gc2.flagLastUpdated as `" + rightLabel + " FlagLastUpdated`");
		selectSQL.addJoin("JOIN " + rightDatabase + ".generalcontractors gc2 on gc2.id = gc1.id");
		selectSQL.addWhere("gc1.flagLastUpdated > date_sub(now(), INTERVAL 1 DAY) ");
		selectSQL.addWhere("gc2.flagLastUpdated > date_sub(now(), INTERVAL 1 DAY)");
		selectSQL.addWhere("gc1.flag <> gc2.flag");
		selectSQL.addOrderBy("gc1.subID");
		selectSQL.addOrderBy("gc1.genID");
		return selectSQL;
	}

	private SelectSQL buildFlagDiffCaoStatusQuery() {
		SelectSQL selectSQL = new SelectSQL(leftDatabase + ".generalcontractors gc1");
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
		selectSQL.addJoin("JOIN " + rightDatabase + ".generalcontractors gc2 on gc2.id = gc1.id");
		selectSQL.addJoin("JOIN " + leftDatabase + ".contractor_audit ca1 on ca1.conID = gc1.subID");
		selectSQL.addJoin("JOIN " + leftDatabase
				+ ".contractor_audit_operator cao1 on gc1.genID = cao1.opID and ca1.id = cao1.auditID");
		selectSQL.addJoin("JOIN " + rightDatabase + ".contractor_audit_operator cao2 on cao2.id = cao1.id");
		selectSQL.addWhere("gc1.flagLastUpdated > date_sub(now(), INTERVAL 1 DAY)");
		selectSQL.addWhere("gc2.flagLastUpdated > date_sub(now(), INTERVAL 1 DAY)");
		selectSQL.addWhere("gc1.flag <> gc2.flag");
		selectSQL
				.addWhere("(cao1.statusChangedDate > date_sub(now(), INTERVAL 1 DAY) OR cao2.statusChangedDate > date_sub(now(), INTERVAL 1 DAY))");
		return selectSQL;
	}

	public TabularModel getFlagDiffData() {
		return flagDiffData;
	}

	public TabularModel getFlagDiffCaoStatus() {
		return flagDiffCaoStatus;
	}

}
