package com.picsauditing.report.tables;

public class NaicsTable extends AbstractTable {

	public NaicsTable(String alias, String foreignKey) {
		super("naics", alias, alias, alias + ".code = " + foreignKey);
		includedColumnImportance = FieldImportance.Average;
	}

	public void addFields() {
		addFields(com.picsauditing.jpa.entities.Naics.class);
	}

	public void addJoins() {
	}
}