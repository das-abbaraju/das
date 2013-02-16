package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.FlagCriteriaOperator;

public class FlagCriteriaOperatorTable extends AbstractTable {

	public FlagCriteriaOperatorTable() {
		super("flag_criteria_operator");
		addFields(FlagCriteriaOperator.class);
	}

	@Override
	protected void addJoins() {
	}

}
