package com.picsauditing.report.tables;

import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FilterType;

public class ContractorNumberTable extends AbstractTable {

	public ContractorNumberTable() {
		super("contractor_operator_number");
		addField(new Field("Tag", "GROUP_CONCAT(" + ReportOnClause.ToAlias + ".tag SEPARATOR ', ')", FilterType.String))
				.setFilterable(false);
		addField(new Field("TagID", "tagID", FilterType.TagID)).setVisible(false);
	}

	protected void addJoins() {
	}
}