package com.picsauditing.actions.qa;

import com.picsauditing.search.SelectSQL;

public class QueryRunnerFactory {

	// this allows us to replace the builder with a mock builder in testing w/o
	// powermock runner
	private static QueryRunnerBuilder queryRunnerBuilder = new TabularResultQueryRunnerBuilder();

	public static QueryRunner instance() {
		return new TabularResultQueryRunner();
	}

	public static QueryRunner instance(SelectSQL query) {
		QueryRunner queryRunner = queryRunnerBuilder.build();
		queryRunner.setSelectSQL(query);
		return queryRunner;
	}

	public static QueryRunner instance(String query) {
		QueryRunner queryRunner = queryRunnerBuilder.build();
		queryRunner.setSelectSQL(query);
		return queryRunner;
	}

	public static QueryRunner instance(TabularModel data) {
		QueryRunner queryRunner = queryRunnerBuilder.build();
		queryRunner.setTabularModelForData(data);
		return queryRunner;
	}

	public static QueryRunner instance(SelectSQL query, TabularModel data) {
		QueryRunner queryRunner = queryRunnerBuilder.build();
		queryRunner.setSelectSQL(query);
		queryRunner.setTabularModelForData(data);
		return queryRunner;
	}

}
