package com.picsauditing.actions.qa;

public class TabularResultQueryRunnerBuilder implements QueryRunnerBuilder {

	@Override
	public QueryRunner build() {
		return new TabularResultQueryRunner();
	}

}
