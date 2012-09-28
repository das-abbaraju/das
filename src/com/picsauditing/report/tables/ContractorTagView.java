package com.picsauditing.report.tables;

import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FilterType;

public class ContractorTagView extends AbstractTable {

	public ContractorTagView() {
		super("(SELECT c.conID, o.opID, c.tagID, o.tag FROM contractor_tag c "
				+ "JOIN operator_tag o ON c.tagID = o.id AND o.active = 1)");
		addField(new Field("Tag", "GROUP_CONCAT(" + ReportOnClause.ToAlias + ".tag SEPARATOR ', ')", FilterType.String))
				.setFilterable(false);
		addField(new Field("TagID", "tagID", FilterType.TagID)).setVisible(false);
	}

	protected void addJoins() {
	}
}