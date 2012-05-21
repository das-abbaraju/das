package com.picsauditing.actions.qa;

import java.sql.SQLException;

import com.picsauditing.search.SelectSQL;

public class FlagAnalyzer extends Analyzer {
	
	private TabularModel flagDiffData;
	private TabularModel flagDataDiffDetails;
	private TabularModel flagDiffCaoStatus;

	public FlagAnalyzer() {}
	
	public FlagAnalyzer(String leftDatabase, String rightDatabase) {
		super(leftDatabase, rightDatabase);
	}
	
	public void run() throws SQLException {
		super.run();
		
		QueryRunner analysis = QueryRunnerFactory.instance(buildInitialQuery());
		flagDiffData = analysis.run();

		QueryRunner analysis2 = QueryRunnerFactory.instance(buildFlagDiffDataDetailsQuery());
		flagDataDiffDetails = analysis2.run();

		QueryRunner analysis3 = QueryRunnerFactory.instance(buildFlagDiffCaoStatusQuery());
		flagDiffCaoStatus = analysis3.run();
	}
	
	private SelectSQL buildInitialQuery() {
		SelectSQL selectSQL = new SelectSQL(leftDatabase+".generalcontractors gc1");
		selectSQL.addField("gc1.subID as `Contractor`");
		selectSQL.addField("gc1.genID as `Operator`");
		selectSQL.addField("gc1.flag as `"+leftLabel+" Flag`");
		selectSQL.addField("gc1.baselineFlag as `"+leftLabel+" Old Flag`");
		selectSQL.addField("gc1.flagLastUpdated as `"+leftLabel+" FlagLastUpdated`");
		selectSQL.addField("gc2.flag as `"+rightLabel+" Flag`");
		selectSQL.addField("gc2.baselineFlag as `"+rightLabel+" Old Flag`");
		selectSQL.addField("gc2.flagLastUpdated as `"+rightLabel+" FlagLastUpdated`");
		selectSQL.addJoin("JOIN "+rightDatabase+".generalcontractors gc2 on gc2.id = gc1.id");
		selectSQL.addWhere("gc1.flagLastUpdated > date_sub(now(), INTERVAL 1 DAY) ");
		selectSQL.addWhere("gc2.flagLastUpdated > date_sub(now(), INTERVAL 1 DAY)");
		selectSQL.addWhere("gc1.flag <> gc2.flag");
		selectSQL.addOrderBy("gc1.subID");
		selectSQL.addOrderBy("gc1.genID");
		return selectSQL;
	}
	
	private SelectSQL buildFlagDiffDataDetailsQuery() {
		SelectSQL selectSQL = new SelectSQL(leftDatabase+".flag_data fd1");
		selectSQL.setDistinct(true);
		selectSQL.addField("fd1.conID as `Contractor`");
		selectSQL.addField("fd1.opID as `Operator`");
		selectSQL.addField("fd1.criteriaID as `Flag Criteria`");
		selectSQL.addField("fd1.updateDate as `"+leftLabel+" Updated`");
		selectSQL.addField("fd1.flag as `"+leftLabel+" Flag`");
		selectSQL.addField("fd1.baselineFlag as `"+leftLabel+" Old Flag`");
		selectSQL.addField("fd2.updateDate as `"+rightLabel+" Updated`");
		selectSQL.addField("fd2.flag as `"+rightLabel+" Flag`");
		selectSQL.addField("fd2.baselineFlag as `"+rightLabel+" Old Flag`");
		selectSQL.addJoin("JOIN "+rightDatabase+".flag_data fd2 on fd2.id = fd1.id");
		selectSQL.addJoin("JOIN "+leftDatabase+".generalcontractors gc1 on gc1.subID = fd1.conID");
		selectSQL.addJoin("JOIN "+rightDatabase+".generalcontractors gc2 on gc2.id = gc1.id");
		selectSQL.addWhere("fd1.updateDate > date_sub(now(), INTERVAL 1 DAY)");
		selectSQL.addWhere("gc1.flagLastUpdated > date_sub(now(), INTERVAL 1 DAY)");
		selectSQL.addWhere("gc2.flagLastUpdated > date_sub(now(), INTERVAL 1 DAY)");
		selectSQL.addWhere("gc1.flag <> gc2.flag");
		selectSQL.addOrderBy("fd1.conID");
		selectSQL.addOrderBy("fd1.opID");
		return selectSQL;
	}
	
	private SelectSQL buildFlagDiffCaoStatusQuery() {
		SelectSQL selectSQL = new SelectSQL(leftDatabase+".generalcontractors gc1");
		selectSQL.addField("ca1.conID as `Contractor`");
		selectSQL.addField("cao1.status as `"+leftLabel+" CAO Status`");
		selectSQL.addField("cao1.flag as `"+leftLabel+" CAO Flag`");
		selectSQL.addField("cao1.percentVerified as `"+leftLabel+" CAO Percent Verified`");
		selectSQL.addField("cao1.percentComplete as `"+leftLabel+" CAO Percent Complete`");
		selectSQL.addField("cao1.statusChangedDate as `"+leftLabel+" CAO Status Changed`");
		selectSQL.addField("cao2.status as `"+rightLabel+" CAO Status`");
		selectSQL.addField("cao2.flag as `"+rightLabel+" CAO Flag`");
		selectSQL.addField("cao2.percentVerified as `"+rightLabel+" CAO Percent Verified`");
		selectSQL.addField("cao2.percentComplete as `"+rightLabel+" CAO Percent Complete`");
		selectSQL.addField("cao2.statusChangedDate as `"+rightLabel+" CAO Status Changed`");
		selectSQL.addJoin("JOIN "+rightDatabase+".generalcontractors gc2 on gc2.id = gc1.id");
		selectSQL.addJoin("JOIN "+leftDatabase+".contractor_audit ca1 on ca1.conID = gc1.subID");
		selectSQL.addJoin("JOIN "+leftDatabase+".contractor_audit_operator cao1 on gc1.genID = cao1.opID and ca1.id = cao1.auditID");
		selectSQL.addJoin("JOIN "+rightDatabase+".contractor_audit_operator cao2 on cao2.id = cao1.id");
		selectSQL.addWhere("gc1.flagLastUpdated > date_sub(now(), INTERVAL 1 DAY)");
		selectSQL.addWhere("gc2.flagLastUpdated > date_sub(now(), INTERVAL 1 DAY)");
		selectSQL.addWhere("gc1.flag <> gc2.flag");
		selectSQL.addWhere("(cao1.statusChangedDate > date_sub(now(), INTERVAL 1 DAY) OR cao2.statusChangedDate > date_sub(now(), INTERVAL 1 DAY))");
		return selectSQL;
	}
	
	public TabularModel getFlagDiffData() {
		return flagDiffData;
	}

	public TabularModel getFlagDataDiffDetails() {
		return flagDataDiffDetails;
	}
	
	public TabularModel getFlagDiffCaoStatus() {
		return flagDiffCaoStatus;
	}
	
}
