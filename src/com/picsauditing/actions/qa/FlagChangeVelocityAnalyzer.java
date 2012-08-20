package com.picsauditing.actions.qa;

import java.sql.SQLException;

import com.picsauditing.search.SelectSQL;

public class FlagChangeVelocityAnalyzer extends Analyzer {
	private TabularModel velocityData;

	public FlagChangeVelocityAnalyzer() {
	}

	public FlagChangeVelocityAnalyzer(String leftDatabase, String rightDatabase) {
		super(leftDatabase, rightDatabase);
	}

	public void run() throws SQLException {
		QueryRunner velocityQueryRunner = QueryRunnerFactory.instance(buildCronVelocityQuery());
		velocityData = velocityQueryRunner.run();
	}

	private String buildCronVelocityQuery() {
		String selectSQL = "select 100*done/total completePercent, done, total " + "FROM (SELECT "
				+ "(select count(*) from " + leftDatabase + ".accounts a " + "join " + leftDatabase
				+ ".contractor_info ci ON a.id = ci.id "
				+ "where ci.lastRecalculation > DATE_FORMAT(now(),'%Y-%m-%e') " + "and a.status = 'Active') as done, "
				+ "(select count(*) from " + leftDatabase + ".accounts a " + "join " + leftDatabase
				+ ".contractor_info ci ON a.id = ci.id " + "where a.status = 'Active') AS total " + ") t; ";
		return selectSQL;
	}

	public TabularModel getVelocityData() {
		return velocityData;
	}

}
