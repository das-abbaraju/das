package com.picsauditing.actions.qa;

import com.picsauditing.search.SelectSQL;

public class QueryRunnerFactory {
	
	public static QueryRunner instance() {
		return new TabularResultQueryRunner();
	}
	
	public static QueryRunner instance(SelectSQL query) {
		TabularResultQueryRunner queryRunner = new TabularResultQueryRunner();
		queryRunner.setSelectSQL(query);
		return queryRunner;
	}

	public static QueryRunner instance(TabularModel data) {
		TabularResultQueryRunner queryRunner = new TabularResultQueryRunner();
		queryRunner.setTabularModelForData(data);
		return queryRunner;
	}

	public static QueryRunner instance(SelectSQL query, TabularModel data) {
		TabularResultQueryRunner queryRunner = new TabularResultQueryRunner();
		queryRunner.setSelectSQL(query);
		queryRunner.setTabularModelForData(data);
		return queryRunner;
	}

}
